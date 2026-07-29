package com.mathmod.program;
import com.mathmod.language.ScopedProgramSource;
import java.util.Optional;
final class ScopedSourceRead {
 enum Status { ABSENT,CURRENT_VALID,CURRENT_UNREADABLE,UNSUPPORTED_VERSION,INVALID_ENVELOPE,CONFLICT }
 enum Diagnostic { SOURCE_ENVELOPE_LIMIT,SOURCE_ENVELOPE_INVALID,SOURCE_SCHEMA_UNSUPPORTED,SOURCE_UTF8_INVALID,SOURCE_JSON_INVALID,SOURCE_FIELD_INVALID,SOURCE_TAG_UNKNOWN,SOURCE_LIMIT_EXCEEDED,SOURCE_CONFLICT }
 private final Status status; private final Optional<ScopedSourceEnvelope> envelope; private final Optional<ScopedProgramSource> source; private final Optional<Diagnostic> diagnostic;
 private ScopedSourceRead(Status s,ScopedSourceEnvelope e,ScopedProgramSource p,Diagnostic d){status=s;envelope=Optional.ofNullable(e);source=Optional.ofNullable(p);diagnostic=Optional.ofNullable(d);}
 static ScopedSourceRead valid(ScopedSourceEnvelope e,ScopedProgramSource p){return new ScopedSourceRead(Status.CURRENT_VALID,e,p,null);} static ScopedSourceRead unreadable(ScopedSourceEnvelope e,Diagnostic d){return new ScopedSourceRead(Status.CURRENT_UNREADABLE,e,null,d);}
 static ScopedSourceRead absent(){return new ScopedSourceRead(Status.ABSENT,null,null,null);} static ScopedSourceRead unsupported(ScopedSourceEnvelope e){return new ScopedSourceRead(Status.UNSUPPORTED_VERSION,e,null,Diagnostic.SOURCE_SCHEMA_UNSUPPORTED);} static ScopedSourceRead conflict(ScopedSourceEnvelope e){return new ScopedSourceRead(Status.CONFLICT,e,null,Diagnostic.SOURCE_CONFLICT);}
 Status status(){return status;} Optional<ScopedSourceEnvelope> envelope(){return envelope;} Optional<ScopedProgramSource> source(){return source;} Optional<Diagnostic> diagnostic(){return diagnostic;}
}
