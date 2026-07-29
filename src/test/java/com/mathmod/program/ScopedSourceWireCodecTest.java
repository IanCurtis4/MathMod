package com.mathmod.program;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.InvocationTargetException;
import static org.junit.jupiter.api.Assertions.*;

class ScopedSourceWireCodecTest {
 @Test void encodesCanonicalSchemaOneAndRoundTrips() {
  String json="{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}";
  ScopedSourceRead decoded=ScopedSourceWireCodec.decode(new ScopedSourceEnvelope(1,json.getBytes(StandardCharsets.UTF_8)));
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decoded.status());
  ScopedSourceEnvelope canonical=ScopedSourceWireCodec.encode(decoded.source().orElseThrow());
  assertEquals(json,new String(canonical.payload(),StandardCharsets.UTF_8));
 }
 @Test void rejectsKnownButInapplicableFieldsAndMalformedUtf8() {
  String json="{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\",\"rune_id\":\"x:y\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}";
  assertEquals(ScopedSourceRead.Status.CURRENT_UNREADABLE,ScopedSourceWireCodec.decode(new ScopedSourceEnvelope(1,json.getBytes(StandardCharsets.UTF_8))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_UTF8_INVALID,ScopedSourceWireCodec.decode(new ScopedSourceEnvelope(1,new byte[]{(byte)0xC3,(byte)0x28})).diagnostic().orElseThrow());
 }
 @Test void decodesAllExpressionTagsWithoutResolvingRuneIds() {
  for(String expression: new String[]{
   "{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}",
   "{\"kind\":\"parameter\",\"index\":0}",
   "{\"kind\":\"rune_call\",\"rune_id\":\"future:x\",\"arguments\":[]}",
   "{\"kind\":\"lambda\",\"name_hint\":\"x\",\"parameter_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"body\":{\"kind\":\"parameter\",\"index\":0}}",
   "{\"kind\":\"application\",\"function\":{\"kind\":\"lambda\",\"name_hint\":\"x\",\"parameter_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"body\":{\"kind\":\"parameter\",\"index\":0}},\"argument\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}}",
   "{\"kind\":\"let\",\"name_hint\":\"x\",\"value\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"},\"body\":{\"kind\":\"parameter\",\"index\":0}}"}) {
   String json="{\"expression\":"+expression+",\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}";
   ScopedSourceRead.Status expected=expression.startsWith("{\"kind\":\"parameter")?ScopedSourceRead.Status.CURRENT_UNREADABLE:ScopedSourceRead.Status.CURRENT_VALID;
   assertEquals(expected,ScopedSourceWireCodec.decode(new ScopedSourceEnvelope(1,json.getBytes(StandardCharsets.UTF_8))).status(),expression);
  }
 }
 @Test void rejectsStrictJsonFieldAndIntegerFailures() {
  String root="{\"expression\":%s,\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}";
  for(String expression:new String[]{
   "{\"kind\":\"unknown\"}",
   "{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\",\"extra\":true}",
   "{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\",\"value\":\"2\"}",
   "{\"kind\":\"parameter\",\"index\":-1}",
   "{\"kind\":\"parameter\",\"index\":1.0}",
   "{\"kind\":\"parameter\",\"index\":2147483648}"}) {
   assertEquals(ScopedSourceRead.Status.CURRENT_UNREADABLE,decode(String.format(root,expression)).status(),expression);
  }
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_JSON_INVALID,decode(String.format(root,"{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}")+" {}" ).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_JSON_INVALID,decode("").diagnostic().orElseThrow());
 }
 @Test void enforcesWireStringAndArgumentBoundariesBeforeModelConstruction() {
  String literal160="x".repeat(160), literal161="x".repeat(161);
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLiteral(literal160)).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootLiteral(literal161)).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCall(arguments(16))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootCall(arguments(17))).diagnostic().orElseThrow());
 }
 @Test void enforcesCombinedAstAndTotalArgumentBoundary() {
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCall(wideArguments(15))).status(),"256 AST nodes / 255 arguments");
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootCall(wideArguments(16))).diagnostic().orElseThrow(),"the next node/argument must fail before construction");
 }
 @Test void enforcesBudgetTypeBindingAndApplicationLimits() {
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootWithBudget("0")).status());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootWithBudget("128")).status());
  for(String invalid:new String[]{"-1","129","1.0","01","2147483648"}) assertEquals(ScopedSourceRead.Status.CURRENT_UNREADABLE,decode(rootWithBudget(invalid)).status(),invalid);
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLambda(type(4),16)).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootLambda(type(5),1)).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootLambda(type(0),17)).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootApplication(64)).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootApplication(65)).diagnostic().orElseThrow());
 }
 @Test void enforcesIdentifierAndHintUtf8Bounds() {
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCallWithId("x".repeat(256))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootCallWithId("x".repeat(257))).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCall(inputNamed("x".repeat(128)))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootCall(inputNamed("x".repeat(129)))).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLambdaHint("x".repeat(32))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootLambdaHint("x".repeat(33))).diagnostic().orElseThrow());
 }
 private void legacyStrictJsonAndIndependentUtf8BoundariesAreRejectedAtExactEdges() {
  for(String bad:new String[]{"\uFEFF"+rootLiteral("1"), rootLiteral("1").substring(0,20), rootLiteral("1")+" {}",
   "{\"expression\":{},\"budget_limit\":1}", "{\"expression\":\"bad\",\"result_type\":{},\"budget_limit\":1}",
   "{\"expression\":{\"kind\":\"literal\",\"rune_type\":true,\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"})
   assertEquals(ScopedSourceRead.Status.CURRENT_UNREADABLE,decode(bad).status(),bad);
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLiteral("é".repeat(160))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootLiteral("é".repeat(161))).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCallWithId("é".repeat(128))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootCallWithId("é".repeat(129))).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCall(inputNamed("é".repeat(64)))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootCall(inputNamed("é".repeat(65)))).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLambdaHint("é".repeat(32))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootLambdaHint("é".repeat(33))).diagnostic().orElseThrow());
 }
 @Test void exactAstArgumentAndExpressionDepthEdges() throws Exception {
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCall(wideArguments(15))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootCall(wideArguments(15)+","+inputNamed("last"))).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLambda(type(0),16)).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootLambda(type(0),17)).diagnostic().orElseThrow());
  Object expressionLimits=counter("Limits"); java.lang.reflect.Method expressionMethod=expressionLimits.getClass().getDeclaredMethod("expression",int.class); expressionMethod.setAccessible(true);
  expressionMethod.invoke(expressionLimits,256);
  assertThrows(InvocationTargetException.class,()->expressionMethod.invoke(expressionLimits,257),"expression depth 257 must fail; nested JSON reaches the container ceiling earlier");
 }
 @Test void strictJsonAndUtf8BoundaryMatrix() {
  for(String bad:new String[]{"\uFEFF"+rootLiteral("1"),rootLiteral("1").substring(0,20),rootLiteral("1")+" {}",
   "{\"expression\":{},\"budget_limit\":1}","{\"expression\":\"bad\",\"result_type\":{},\"budget_limit\":1}",
   "{\"expression\":{\"kind\":\"literal\",\"rune_type\":true,\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"}) assertEquals(ScopedSourceRead.Status.CURRENT_UNREADABLE,decode(bad).status());
  String multi=Character.toString(0x00e9);
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLiteral(multi.repeat(160))).status());
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootLiteral(multi.repeat(161))).diagnostic().orElseThrow());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCallWithId(multi.repeat(128))).status());
  assertEquals(ScopedSourceRead.Status.CURRENT_UNREADABLE,decode(rootCallWithId(multi.repeat(129))).status());
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootCall(inputNamed(multi.repeat(64)))).status());
  assertEquals(ScopedSourceRead.Status.CURRENT_UNREADABLE,decode(rootCall(inputNamed(multi.repeat(65)))).status());
 }
 @Test void noTrimOrDefaultPrecedenceIsExplicit() {
  for(String id:new String[]{" "," future:x","future:x "}) assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootCallWithId(id)).diagnostic().orElseThrow());
  for(String hint:new String[]{" "," x","x "}) assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootLambdaHint(hint)).diagnostic().orElseThrow());
  for(String input:new String[]{" "," x","x "}) assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootCall(inputNamed(input))).diagnostic().orElseThrow());
  String typeWhitespace="{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\" number \"},\"budget_limit\":1}";
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(typeWhitespace).diagnostic().orElseThrow());
  String three=Character.toString(0x20ac); assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLiteral(three.repeat(160))).status(),"Java length accepts 160 chars before the unreachable 640-byte edge");
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_LIMIT_EXCEEDED,decode(rootLiteral(three.repeat(161))).diagnostic().orElseThrow(),"Java-length limit has precedence over 640-byte limit");
  assertEquals(ScopedSourceRead.Status.CURRENT_VALID,decode(rootLambdaHint(three.repeat(32))).status(),"Java length accepts 32 three-byte binder-hint characters before the nominal UTF-8 ceiling");
  assertEquals(ScopedSourceRead.Diagnostic.SOURCE_FIELD_INVALID,decode(rootLambdaHint(three.repeat(33))).diagnostic().orElseThrow(),"Java-length limit controls before the unreachable nominal 128/129-byte binder-hint edge");
 }
 @Test void internalTokenAndTypeCountersRejectTheirFirstForbiddenValue() throws Exception {
  Object tokens=counter("TokenBudget"); java.lang.reflect.Method charge=tokens.getClass().getDeclaredMethod("charge"); charge.setAccessible(true);
  for(int index=0;index<4096;index++) charge.invoke(tokens);
  assertThrows(InvocationTargetException.class,()->charge.invoke(tokens),"4,097th JSON value/container must fail");
  Object limits=counter("Limits"); java.lang.reflect.Method type=limits.getClass().getDeclaredMethod("type",int.class); type.setAccessible(true);
  for(int index=0;index<1024;index++) type.invoke(limits,0);
  assertThrows(InvocationTargetException.class,()->type.invoke(limits,0),"1,025th type node must fail");
 }
 private static Object counter(String simpleName) throws Exception { Class<?> type=Class.forName("com.mathmod.program.ScopedSourceWireCodec$"+simpleName); var constructor=type.getDeclaredConstructor();constructor.setAccessible(true);return constructor.newInstance(); }
 private static ScopedSourceRead decode(String json) { return ScopedSourceWireCodec.decode(new ScopedSourceEnvelope(1,json.getBytes(StandardCharsets.UTF_8))); }
 private static String rootLiteral(String value) { return "{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\""+value+"\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"; }
 private static String rootCall(String arguments) { return "{\"expression\":{\"kind\":\"rune_call\",\"rune_id\":\"future:x\",\"arguments\":["+arguments+"]},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"; }
 private static String arguments(int count) { StringBuilder values=new StringBuilder(); for(int index=0;index<count;index++){if(index>0)values.append(',');values.append("{\"input_name\":\"x").append(index).append("\",\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}}");} return values.toString(); }
 private static String wideArguments(int calls) { StringBuilder values=new StringBuilder(); for(int call=0;call<calls;call++){if(call>0)values.append(',');values.append("{\"input_name\":\"call").append(call).append("\",\"expression\":{\"kind\":\"rune_call\",\"rune_id\":\"future:").append(call).append("\",\"arguments\":[").append(arguments(16)).append("]}}");} return values.toString(); }
 private static String rootWithBudget(String budget) { return "{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":"+budget+"}"; }
 private static String type(int nesting) { String value="{\"kind\":\"value\",\"rune_type\":\"number\"}"; for(int index=0;index<nesting;index++)value="{\"kind\":\"function\",\"parameter_type\":"+value+",\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"}}"; return value; }
 private static String rootLambda(String parameterType,int depth) { String body="{\"kind\":\"parameter\",\"index\":0}"; for(int index=0;index<depth;index++)body="{\"kind\":\"lambda\",\"name_hint\":\"x\",\"parameter_type\":"+parameterType+",\"body\":"+body+"}"; return "{\"expression\":"+body+",\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"; }
 private static String rootApplication(int count) { String expression="{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}"; for(int index=0;index<count;index++) expression="{\"kind\":\"application\",\"function\":"+expression+",\"argument\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}}"; return "{\"expression\":"+expression+",\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"; }
 private static String rootCallWithId(String id) { return "{\"expression\":{\"kind\":\"rune_call\",\"rune_id\":\""+id+"\",\"arguments\":[]},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"; }
 private static String inputNamed(String name) { return "{\"input_name\":\""+name+"\",\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}}"; }
 private static String rootLambdaHint(String hint) { return "{\"expression\":{\"kind\":\"lambda\",\"name_hint\":\""+hint+"\",\"parameter_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"body\":{\"kind\":\"parameter\",\"index\":0}},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"; }
 private static String rootWithExpression(String expression) { return "{\"expression\":"+expression+",\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}"; }
 private static String deepCall(int calls) { String expression="{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}"; for(int index=0;index<calls;index++) expression="{\"kind\":\"rune_call\",\"rune_id\":\"future:x\",\"arguments\":[{\"input_name\":\"x\",\"expression\":"+expression+"}]}"; return expression; }
}
