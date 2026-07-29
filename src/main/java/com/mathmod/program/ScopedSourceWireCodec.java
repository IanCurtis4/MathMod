package com.mathmod.program;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.mathmod.language.RuneTypeExpression;
import com.mathmod.language.ScopedExpression;
import com.mathmod.language.ScopedProgramSource;
import com.mathmod.language.ScopedStructureValidator;
import com.mathmod.runes.RuneType;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Strict streaming schema-1 source decoder; it never resolves a rune or compiles. */
final class ScopedSourceWireCodec {
    private static final ThreadLocal<TokenBudget> TOKENS = new ThreadLocal<>();
    private static final ThreadLocal<Limits> LIMITS = new ThreadLocal<>();
    private ScopedSourceWireCodec() { }

    static ScopedSourceRead decode(ScopedSourceEnvelope envelope) {
        final String json;
        try {
            json = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(envelope.payload())).toString();
        } catch (CharacterCodingException exception) { return ScopedSourceRead.unreadable(envelope, ScopedSourceRead.Diagnostic.SOURCE_UTF8_INVALID); }
        if (json.startsWith("\uFEFF")) return ScopedSourceRead.unreadable(envelope, ScopedSourceRead.Diagnostic.SOURCE_JSON_INVALID);
        if (json.isBlank()) return ScopedSourceRead.unreadable(envelope, ScopedSourceRead.Diagnostic.SOURCE_JSON_INVALID);
        TOKENS.set(new TokenBudget());
        LIMITS.set(new Limits());
        try (JsonReader reader = new JsonReader(new StringReader(json))) {
            reader.setLenient(false);
            beginObject(reader);
            ScopedExpression expression = null; RuneTypeExpression.ValueType result = null; Integer budget = null;
            boolean e=false,t=false,b=false;
            while (reader.hasNext()) {
                String name=reader.nextName();
                switch(name) {
                    case "expression" -> { if(e) throw field(); e=true; expression=expression(reader, 1, 0); }
                    case "result_type" -> { if(t) throw field(); t=true; RuneTypeExpression type=type(reader, 0); if (!(type instanceof RuneTypeExpression.ValueType value)) throw field(); result=value; }
                    case "budget_limit" -> { if(b) throw field(); b=true; budget=integer(reader,0,128); }
                    default -> throw field();
                }
            }
            endObject(reader); if (!e||!t||!b) throw field(); if(reader.peek()!=JsonToken.END_DOCUMENT) throw json();
            ScopedProgramSource source = new ScopedProgramSource(ScopedProgramSource.CURRENT_VERSION, expression, result, budget);
            if (!ScopedStructureValidator.validateStructure(source).valid()) throw field();
            return ScopedSourceRead.valid(envelope, source);
        } catch (WireFailure failure) { return ScopedSourceRead.unreadable(envelope, failure.diagnostic); }
          catch (IOException | IllegalArgumentException failure) { return ScopedSourceRead.unreadable(envelope, ScopedSourceRead.Diagnostic.SOURCE_JSON_INVALID); }
          finally { TOKENS.remove(); LIMITS.remove(); }
    }

    static ScopedSourceEnvelope encode(ScopedProgramSource source) {
        if (source.version() != ScopedProgramSource.CURRENT_VERSION) throw new IllegalArgumentException("unsupported source model version");
        StringBuilder out = new StringBuilder();
        out.append("{\"expression\":"); expression(out, source.expression());
        out.append(",\"result_type\":"); type(out, source.resultType());
        out.append(",\"budget_limit\":").append(source.budgetLimit()).append('}');
        return new ScopedSourceEnvelope(1, out.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void type(StringBuilder out, RuneTypeExpression type) {
        if (type instanceof RuneTypeExpression.ValueType value) out.append("{\"kind\":\"value\",\"rune_type\":").append(quoted(value.type().id())).append('}');
        else { RuneTypeExpression.FunctionType function=(RuneTypeExpression.FunctionType)type; out.append("{\"kind\":\"function\",\"parameter_type\":"); type(out,function.parameterType()); out.append(",\"result_type\":");type(out,function.resultType());out.append('}'); }
    }
    private static void expression(StringBuilder out, ScopedExpression expression) {
        if(expression instanceof ScopedExpression.Literal literal) out.append("{\"kind\":\"literal\",\"rune_type\":").append(quoted(literal.type().type().id())).append(",\"value\":").append(quoted(literal.encodedValue())).append('}');
        else if(expression instanceof ScopedExpression.ParameterReference parameter) out.append("{\"kind\":\"parameter\",\"index\":").append(parameter.deBruijnIndex()).append('}');
        else if(expression instanceof ScopedExpression.RuneCall call) { out.append("{\"kind\":\"rune_call\",\"rune_id\":").append(quoted(call.runeId())).append(",\"arguments\":["); for(int i=0;i<call.arguments().size();i++){if(i>0)out.append(',');ScopedExpression.Argument a=call.arguments().get(i);out.append("{\"input_name\":").append(quoted(a.inputName())).append(",\"expression\":");expression(out,a.expression());out.append('}');}out.append("]}"); }
        else if(expression instanceof ScopedExpression.Lambda lambda) { out.append("{\"kind\":\"lambda\",\"name_hint\":").append(quoted(lambda.nameHint())).append(",\"parameter_type\":");type(out,lambda.parameterType());out.append(",\"body\":");expression(out,lambda.body());out.append('}'); }
        else if(expression instanceof ScopedExpression.Application application) { out.append("{\"kind\":\"application\",\"function\":");expression(out,application.function());out.append(",\"argument\":");expression(out,application.argument());out.append('}'); }
        else { ScopedExpression.Let let=(ScopedExpression.Let)expression; out.append("{\"kind\":\"let\",\"name_hint\":").append(quoted(let.nameHint())).append(",\"value\":");expression(out,let.value());out.append(",\"body\":");expression(out,let.body());out.append('}'); }
    }
    private static String quoted(String input) { StringBuilder out=new StringBuilder("\""); for(int i=0;i<input.length();i++){char c=input.charAt(i);switch(c){case '\\'->out.append("\\\\");case '"'->out.append("\\\"");case '\b'->out.append("\\b");case '\f'->out.append("\\f");case '\n'->out.append("\\n");case '\r'->out.append("\\r");case '\t'->out.append("\\t");default->{if(c<0x20)out.append(String.format("\\u%04x",(int)c));else out.append(c);}}}return out.append('"').toString(); }

    private static RuneTypeExpression type(JsonReader r, int depth) throws IOException, WireFailure {
        LIMITS.get().type(depth); beginObject(r); String kind=null, rune=null; RuneTypeExpression p=null,q=null;
        while(r.hasNext()) { String n=r.nextName(); switch(n) {
            case "kind" -> { if(kind!=null) throw field(); kind=string(r,64); }
            case "rune_type" -> { if(rune!=null) throw field(); rune=string(r,64); }
            case "parameter_type" -> { if(p!=null) throw field(); p=type(r, depth + 1); }
            case "result_type" -> { if(q!=null) throw field(); q=type(r, depth + 1); }
            default -> throw field(); }} endObject(r);
        if("value".equals(kind) && rune!=null && p==null && q==null) return RuneType.byId(rune).result().map(RuneTypeExpression::value).orElseThrow(ScopedSourceWireCodec::field);
        if("function".equals(kind) && rune==null && p!=null && q!=null) return RuneTypeExpression.function(p,q);
        if(!"value".equals(kind)&&!"function".equals(kind)) throw new WireFailure(ScopedSourceRead.Diagnostic.SOURCE_TAG_UNKNOWN);
        throw field();
    }

    private static ScopedExpression expression(JsonReader r, int depth, int bindingDepth) throws IOException, WireFailure {
        LIMITS.get().expression(depth); LIMITS.get().binding(bindingDepth); beginObject(r); String kind=null,runeType=null,value=null,runeId=null,hint=null,input=null; Integer index=null;
        RuneTypeExpression parameterType=null; ScopedExpression body=null,function=null,argument=null,letValue=null; java.util.List<ScopedExpression.Argument> arguments=null;
        while(r.hasNext()) { String n=r.nextName(); switch(n) {
            case "kind" -> { if(kind!=null) throw field(); kind=string(r,32); }
            case "rune_type" -> { if(runeType!=null) throw field(); runeType=string(r,64); }
            case "value" -> { if (r.peek() == JsonToken.BEGIN_OBJECT) { if(letValue!=null) throw field(); letValue=expression(r, depth + 1, bindingDepth); } else { if(value!=null) throw field(); value=literal(r); } }
            case "index" -> { if(index!=null) throw field(); index=integer(r,0,Integer.MAX_VALUE); }
            case "rune_id" -> { if(runeId!=null) throw field(); runeId=string(r,256); }
            case "name_hint" -> { if(hint!=null) throw field(); hint=string(r,128); if(hint.length()>32) throw field(); }
            case "parameter_type" -> { if(parameterType!=null) throw field(); parameterType=type(r, 0); }
            // Field order is intentionally irrelevant. A valid `body` field belongs
            // only to lambda/let, both of which introduce exactly one binder.
            case "body" -> { if(body!=null) throw field(); body=expression(r, depth + 1, bindingDepth + 1); }
            case "function" -> { if(function!=null) throw field(); function=expression(r, depth + 1, bindingDepth); }
            case "argument" -> { if(argument!=null) throw field(); argument=expression(r, depth + 1, bindingDepth); }
            case "arguments" -> { if(arguments!=null) throw field(); arguments=arguments(r, depth, bindingDepth); }
            case "input_name" -> { if(input!=null) throw field(); input=string(r,128); }
            default -> throw field(); }} endObject(r);
        boolean other=runeId!=null||hint!=null||input!=null||parameterType!=null||body!=null||function!=null||argument!=null||letValue!=null||arguments!=null;
        if("literal".equals(kind)&&runeType!=null&&value!=null&&index==null&&!other) return new ScopedExpression.Literal(RuneTypeExpression.value(RuneType.byId(runeType).result().orElseThrow(ScopedSourceWireCodec::field)),value);
        if("parameter".equals(kind)&&index!=null&&runeType==null&&value==null&&!other) return new ScopedExpression.ParameterReference(index);
        if("rune_call".equals(kind)&&runeId!=null&&arguments!=null&&runeType==null&&value==null&&index==null&&hint==null&&input==null&&parameterType==null&&body==null&&function==null&&argument==null&&letValue==null) return new ScopedExpression.RuneCall(runeId,arguments);
        if("lambda".equals(kind)&&hint!=null&&parameterType!=null&&body!=null&&runeType==null&&value==null&&index==null&&runeId==null&&input==null&&function==null&&argument==null&&letValue==null&&arguments==null) return new ScopedExpression.Lambda(hint,parameterType,body);
        if("application".equals(kind)&&function!=null&&argument!=null&&runeType==null&&value==null&&index==null&&runeId==null&&hint==null&&input==null&&parameterType==null&&body==null&&letValue==null&&arguments==null) { LIMITS.get().application(); return new ScopedExpression.Application(function,argument); }
        if("let".equals(kind)&&hint!=null&&letValue!=null&&body!=null&&runeType==null&&value==null&&index==null&&runeId==null&&input==null&&parameterType==null&&function==null&&argument==null&&arguments==null) return new ScopedExpression.Let(hint,letValue,body);
        if(kind==null||!java.util.Set.of("literal","parameter","rune_call","lambda","application","let").contains(kind)) throw new WireFailure(ScopedSourceRead.Diagnostic.SOURCE_TAG_UNKNOWN);
        throw field();
    }
    private static java.util.List<ScopedExpression.Argument> arguments(JsonReader r, int depth, int bindingDepth) throws IOException, WireFailure {
        beginArray(r); java.util.ArrayList<ScopedExpression.Argument> result=new java.util.ArrayList<>();
        while(r.hasNext()) { if(result.size()>=16)throw limit(); LIMITS.get().argument(); beginObject(r);String name=null;ScopedExpression expression=null;
            while(r.hasNext()){String field=r.nextName();if("input_name".equals(field)){if(name!=null)throw field();name=string(r,128);}else if("expression".equals(field)){if(expression!=null)throw field();expression=expression(r, depth + 1, bindingDepth);}else throw field();}endObject(r);if(name==null||expression==null)throw field();result.add(new ScopedExpression.Argument(name,expression)); }
        endArray(r); return List.copyOf(result);
    }
    private static String string(JsonReader r,int bytes) throws IOException,WireFailure { require(r,JsonToken.STRING); String s=r.nextString(); if(s.isBlank()||!s.equals(s.trim())||unpairedSurrogate(s)||s.getBytes(StandardCharsets.UTF_8).length>bytes)throw field();return s; }
    private static String literal(JsonReader r) throws IOException,WireFailure { require(r,JsonToken.STRING); String s=r.nextString(); if(s.isEmpty()||unpairedSurrogate(s)||s.length()>160||s.getBytes(StandardCharsets.UTF_8).length>640)throw new WireFailure(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED);return s; }
    private static boolean unpairedSurrogate(String text) { for(int i=0;i<text.length();i++){char c=text.charAt(i);if(Character.isHighSurrogate(c)){if(i+1>=text.length()||!Character.isLowSurrogate(text.charAt(++i)))return true;}else if(Character.isLowSurrogate(c))return true;}return false; }
    private static int integer(JsonReader r,int min,int max)throws IOException,WireFailure { require(r,JsonToken.NUMBER); String s=r.nextString(); if(!s.matches("0|[1-9][0-9]*"))throw field();try{int n=Integer.parseInt(s);if(n<min||n>max)throw field();return n;}catch(NumberFormatException e){throw field();} }
    private static void require(JsonReader r,JsonToken t)throws IOException,WireFailure{if(r.peek()!=t)throw field();if(t!=JsonToken.END_DOCUMENT)TOKENS.get().charge();}
    private static void beginObject(JsonReader r) throws IOException, WireFailure { require(r, JsonToken.BEGIN_OBJECT); LIMITS.get().container(); r.beginObject(); }
    private static void endObject(JsonReader r) throws IOException { r.endObject(); LIMITS.get().leaveContainer(); }
    private static void beginArray(JsonReader r) throws IOException, WireFailure { require(r, JsonToken.BEGIN_ARRAY); LIMITS.get().container(); r.beginArray(); }
    private static void endArray(JsonReader r) throws IOException { r.endArray(); LIMITS.get().leaveContainer(); }
    private static WireFailure field(){return new WireFailure(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID);}
    private static WireFailure json(){return new WireFailure(ScopedSourceRead.Diagnostic.SOURCE_JSON_INVALID);}
    private static WireFailure limit(){return new WireFailure(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED);}
    private static final class WireFailure extends Exception { final ScopedSourceRead.Diagnostic diagnostic; WireFailure(ScopedSourceRead.Diagnostic d){diagnostic=d;} }
    private static final class TokenBudget { int values; void charge() throws WireFailure { if (++values > 4096) throw new WireFailure(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED); } }
    private static final class Limits {
        int containers, nodes, types, arguments, applications;
        void container() throws WireFailure { if (++containers > 272) throw limit(); }
        void leaveContainer() { containers--; }
        void expression(int depth) throws WireFailure { if (depth > 256 || ++nodes > 256) throw limit(); }
        void type(int depth) throws WireFailure { if (depth > 4 || ++types > 1024) throw limit(); }
        void argument() throws WireFailure { if (++arguments > 255) throw limit(); }
        void application() throws WireFailure { if (++applications > 64) throw limit(); }
        void binding(int depth) throws WireFailure { if (depth > 16) throw limit(); }
    }
}
