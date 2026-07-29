# L0 Scoped Source Wire-Format Contract

**Task:** `L0-SOL-02`  
**Date:** 2026-07-27  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Scope:** persistent envelope, schema 1, bounded decode, opaque preservation,
network exclusion, migration, conflict, and rollback vectors

## 1. Gate input and purpose

`L0-TH-01` is accepted with recommendation `APPROVE`.

The semantic review in `docs/L0_PURE_COMPILE_SEMANTIC_REVIEW.md` covers the
complete pure-compiler contract and all OBS-SHARE, TAIL, and BOUND vectors. It
does not claim persistence, executable admission, networking, or inscription.

This contract now freezes the persistent representation reserved by
`docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`.

It does not authorize Java implementation. In particular, it does not authorize
editing `ModDataComponents`, `ProgramStorage`, networking, items, menus,
screens, `ProgramGraph`, Guided persistence, or public APIs.

## 2. Frozen authority and precedence

The authority order remains:

1. persisted `ProgramGraph` is the only castable/executable program;
2. the scoped source component is optional authoring metadata;
3. a decoded schema-1 source is only a candidate for authoritative server
   compilation;
4. a future, malformed, conflicting, stale, or source-only envelope cannot
   replace or disable a valid graph;
5. reads never compile, repair, migrate, canonicalize, delete, or rewrite;
6. client claims remain non-authoritative.

Name and resource selections remain in their existing shared components. They
must not be duplicated inside the source payload.

`ScopedProgramSource.CURRENT_VERSION` remains an internal model constant. The
persistent schema below is independently assigned version 1 by this contract;
the equal numeric value is not identity or automatic coupling.

## 3. Data Component identity

The reserved persistent Data Component id is:

```text
mathmod:program_scoped_source
```

The future component value is a bounded opaque envelope, not
`ScopedProgramSource` directly.

Required registration behavior:

```text
persistent codec: yes
cache encoding: yes, only with content-based immutable value semantics
network synchronized: no
```

The envelope implementation must defensively copy payload bytes and implement
equality/hash by byte content. A Java record containing a mutable `byte[]`
without defensive copying and content equality is forbidden.

No other component id is an alias. In particular,
`mathmod:scoped_program_source`, `mathmod:functional_source`, and the internal
model version are not accepted persistent identities.

## 4. Persistent envelope

The component's persistent value has exactly two fields:

```json
{
  "schema_version": 1,
  "payload": "<opaque byte array>"
}
```

Field rules:

| Field | Type | Rule |
|---|---|---|
| `schema_version` | signed 32-bit integer | `1` is current; every other integer is unsupported and preserved |
| `payload` | byte array | length `0..262144`; interpreted only when schema is `1` |

The maximum payload size is:

```text
MAX_SCOPED_SOURCE_PAYLOAD_BYTES = 262_144
```

The byte limit is checked before UTF-8 decoding, JSON parsing, AST allocation,
type construction, registry lookup, or compilation.

An outer value with a missing field, duplicate field, wrong field type, or
payload above the limit is `INVALID_ENVELOPE`. Platform decode may reject it;
opaque-preservation guarantees apply only after a bounded envelope value has
been constructed.

Any integer schema other than `1`, including zero and negative values, is
`UNSUPPORTED_VERSION`. Its bounded payload is retained byte-for-byte and is
never parsed as schema 1.

## 5. Why the envelope is opaque

A typed `Codec<ScopedProgramSource>` would fail before a future or malformed
value exists in memory to preserve. It would therefore be unable to satisfy the
frozen recovery/no-rewrite rules.

The envelope separates:

- safe outer persistence and byte bounds;
- schema-version classification;
- strict schema-1 parsing;
- optional authoring recovery/export;
- internal `ScopedProgramSource` construction.

The payload bytes are the preservation authority. A parsed AST is a derived
view and must never overwrite those bytes during read.

## 6. Schema 1 payload encoding

Schema 1 payload bytes are strict UTF-8 JSON.

New successful inscriptions encode deterministic compact JSON:

- UTF-8 without BOM;
- no insignificant whitespace;
- object fields emitted in the field order listed in this contract;
- array order preserved;
- minimal decimal JSON integers;
- JSON control characters escaped;
- non-ASCII characters encoded directly as UTF-8;
- no trailing bytes after the root JSON value.

JSON object-field order is not semantic when decoding. Duplicate, missing, or
unknown fields are rejected. Canonical output is generated only for a new
successful explicit inscription or explicit successful migration, never on
read.

### 6.1 Root

Exact schema-1 root:

```json
{
  "expression": {},
  "result_type": {},
  "budget_limit": 0
}
```

All three fields are required. No defaults or extra fields exist.

`result_type` must decode to a value type. A function type at the program root
is invalid even though function types are valid inside lambdas.

### 6.2 Type expressions

Value type:

```json
{
  "kind": "value",
  "rune_type": "number"
}
```

Function type:

```json
{
  "kind": "function",
  "parameter_type": {},
  "result_type": {}
}
```

Exact type tags:

```text
value
function
```

`rune_type` uses the existing stable `RuneType` id, not enum ordinal, localized
text, Java class name, or executor key.

### 6.3 Expressions

Literal:

```json
{
  "kind": "literal",
  "rune_type": "number",
  "value": "1.0"
}
```

Parameter reference:

```json
{
  "kind": "parameter",
  "index": 0
}
```

Rune call:

```json
{
  "kind": "rune_call",
  "rune_id": "mathmod:number_add",
  "arguments": [
    {
      "input_name": "left",
      "expression": {}
    }
  ]
}
```

Lambda:

```json
{
  "kind": "lambda",
  "name_hint": "x",
  "parameter_type": {},
  "body": {}
}
```

Application:

```json
{
  "kind": "application",
  "function": {},
  "argument": {}
}
```

Let:

```json
{
  "kind": "let",
  "name_hint": "x",
  "value": {},
  "body": {}
}
```

Exact expression tags:

```text
literal
parameter
rune_call
lambda
application
let
```

Every field shown for a tag is required. No tag has an optional field or
default. Argument order is preserved, but socket identity comes from
`input_name`.

Binder hints are presentation only. De Bruijn `index` remains the semantic
binding identity.

## 7. Decode and allocation limits

The outer byte limit is followed by a strict, bounded UTF-8/JSON preflight.
No unbounded generic JSON tree may be built first and validated later.

Frozen limits:

| Concern | Limit |
|---|---:|
| payload bytes | 262,144 |
| total JSON values/containers | 4,096 |
| JSON/container nesting | 272 |
| AST nodes | 256 |
| expression nesting | 256 |
| total argument records | 255 |
| arguments per rune call | 16 |
| binding depth | 16 |
| application count | 64 |
| type nesting depth | 4 |
| total type-expression nodes | 1,024 |
| literal Java length | 160 |
| literal UTF-8 bytes | 640 |
| binder hint Java length | 32 |
| binder hint UTF-8 bytes | 128 |
| rune id UTF-8 bytes | 256 |
| input name UTF-8 bytes | 128 |
| RuneType id UTF-8 bytes | 64 |
| budget limit | `0..128` |
| parameter index | non-negative signed 32-bit integer |

The stricter of Java-length and UTF-8-byte limits applies.

The parser must reject before allocating the next overflowing list, string,
node, type, or record. Integer overflow, out-of-range JSON numbers, fractional
indices/budgets, and negative parameter indices fail closed.

After bounded parsing, existing structural validation remains mandatory.
Therefore an index that is non-negative but free at its structural location is
rejected as `FREE_PARAMETER`.

Rune ids and input names must be non-blank, contain no surrounding whitespace,
and satisfy their byte limits. They are not resolved during persistence read.
Unknown rune ids remain parseable source and receive authoritative compile
diagnostics only on an explicit compile attempt.

Binder hints must be non-blank, contain no surrounding whitespace, and satisfy
both hint limits. Schema 1 never silently substitutes or trims a hint.

Literal values preserve exact decoded characters. The wire parser does not
trim, parse, or canonicalize a NUMBER; the trusted literal resolver owns that
later compile-stage decision.

## 8. Decode classifications and stable identities

The persistence/read boundary returns one of:

```text
ABSENT
CURRENT_VALID
CURRENT_UNREADABLE
UNSUPPORTED_VERSION
INVALID_ENVELOPE
CONFLICT
```

Stable diagnostic identities:

```text
SOURCE_ENVELOPE_LIMIT
SOURCE_ENVELOPE_INVALID
SOURCE_SCHEMA_UNSUPPORTED
SOURCE_UTF8_INVALID
SOURCE_JSON_INVALID
SOURCE_FIELD_INVALID
SOURCE_TAG_UNKNOWN
SOURCE_LIMIT_EXCEEDED
SOURCE_CONFLICT
```

Classification:

| Condition | Classification | Diagnostic |
|---|---|---|
| no component | `ABSENT` | none |
| schema 1, strict bounded parse succeeds | `CURRENT_VALID` | none |
| schema 1, invalid UTF-8 | `CURRENT_UNREADABLE` | `SOURCE_UTF8_INVALID` |
| schema 1, malformed/truncated/trailing JSON | `CURRENT_UNREADABLE` | `SOURCE_JSON_INVALID` |
| schema 1, missing/duplicate/extra/wrong-type field | `CURRENT_UNREADABLE` | `SOURCE_FIELD_INVALID` |
| schema 1, unknown `kind` | `CURRENT_UNREADABLE` | `SOURCE_TAG_UNKNOWN` |
| schema 1, inner bound exceeded | `CURRENT_UNREADABLE` | `SOURCE_LIMIT_EXCEEDED` |
| schema other than 1 | `UNSUPPORTED_VERSION` | `SOURCE_SCHEMA_UNSUPPORTED` |
| outer payload exceeds 262,144 | `INVALID_ENVELOPE` | `SOURCE_ENVELOPE_LIMIT` |
| malformed outer envelope | `INVALID_ENVELOPE` | `SOURCE_ENVELOPE_INVALID` |
| both Guided and scoped-source metadata present | `CONFLICT` | `SOURCE_CONFLICT` |

The first deterministic failure in the order above is the read diagnostic.
Raw parser/Java exception text is log detail only.

Current unreadable and unsupported-version envelopes retain the exact
`schema_version` and payload bytes. They are available only for bounded
recovery/export and cannot become compile input.

## 9. Read behavior and coexistence

Presence means physical Data Component presence, not successful schema parsing.

| Components | Graph behavior | Source/Guided behavior | Mutation on read |
|---|---|---|---|
| graph only | executes normally | functional source absent | none |
| graph + current valid source | graph remains authority | source may reopen as candidate | none |
| graph + unreadable/future source | graph remains authority | bounded recovery/export only | none |
| source only | not executable | recovery or explicit compile candidate if valid | none |
| neither | existing empty behavior | explicit new source may be created | none |
| Guided + scoped source | graph remains authority if present | both authoring projections conflicted/read-only | none |

Guided/source conflict applies even when:

- the source is unreadable or future;
- Guided is unreplayable;
- no graph exists;
- the two projections happen to describe an equal graph.

No validity-based precedence exists. Only an explicit successful re-inscription
through one authoring mode resolves the conflict.

A current valid source that recompiles differently from the persisted graph is
stale/conflicting. The graph remains authoritative and no write occurs.

## 10. Network decision

Schema 1 has:

```text
Data Component network synchronization: none
StreamCodec: none
CustomPacketPayload: none
client-to-server source transport: none
```

Using `ByteBufCodecs.fromCodecWithRegistries` for the raw source envelope is
forbidden.

The read-only projection slice may later define a separate bounded diagnostic
DTO. A mutable editor or compile request requires a separate payload contract
with:

- pre-allocation byte limits;
- menu, player, item, slot, and request identity;
- stale-request handling;
- no trusted client graph/type/purity/cost/generation claims.

Neither DTO nor request is approved here.

## 11. Migration

There is no persisted schema 0.

Existing items without `mathmod:program_scoped_source` are graph-only or
Guided items and remain unchanged. `ScopedProgramSource.CURRENT_VERSION == 1`
does not create a migration source.

Read-time migration is forbidden:

- no schema upgrade/downgrade;
- no Guided-to-functional conversion;
- no graph-to-source invention;
- no malformed-source repair;
- no future-source re-encode;
- no automatic compile;
- no component rewrite.

A future explicit migration must:

1. retain the exact original envelope;
2. construct a schema-1 candidate off-item;
3. pass bounded decode, authoritative compile, graph validation, executable
   policy, resources, knowledge, item/slot, and registry-generation checks;
4. enter the same atomic commit plan as a new inscription;
5. restore the exact original envelope and all other captured components on
   any failure.

Graph-to-source import remains explicit and flat under the original L0
contract; it is not schema migration and may not invent lambdas, lets, or
authorship.

## 12. Atomic commit and rollback contract

The future persistence slice must snapshot exact presence and value for:

```text
mathmod:program
mathmod:program_scoped_source
mathmod:program_name
mathmod:program_resources
mathmod:program_guided_workspace
mathmod:program_custom_actions
```

The snapshot includes byte-exact source payload content.

### Functional success

One atomic functional commit:

- writes the admitted graph;
- writes canonical schema-1 source envelope;
- writes/removes the shared program name according to the accepted name;
- writes the admitted resource selections;
- removes `program_guided_workspace`;
- removes legacy `program_custom_actions`;
- does not remove the shared name merely because Guided metadata is removed.

### Guided or graph-only success

Any later successful Guided or graph-only inscription must remove
`program_scoped_source` in the same atomic commit.

### Failure

Decode, compile, executable, resource, knowledge, registry-generation,
item/slot, disconnect, stale-request, or commit-application failure:

- activates no candidate graph;
- mutates no component before the commit stage;
- restores exact component presence and values after a commit-stage exception;
- preserves future/malformed payload bytes exactly;
- never transfers the attempt to another item;
- never retries invisibly.

`ProgramStorage.saveValidated`, `saveValidatedCustom`,
`GuidedWorkspacePersistence.write`, and `clear` are sequential existing
operations. They are not the L0 atomic commit boundary and may not be composed
piecemeal to implement this contract.

## 13. Adversarial acceptance matrix

The future codec/persistence implementation must prove:

### Envelope and JSON

- payload 262,144 bytes accepted by the outer envelope;
- payload 262,145 rejected before UTF-8/JSON/AST;
- valid and invalid UTF-8;
- empty, truncated, trailing, and multi-root JSON;
- duplicate, missing, extra, and wrong-type fields;
- every known tag and an unknown tag;
- deterministic canonical encode for current valid source;
- byte-exact retention for current unreadable and unsupported versions.

### Language bounds

- AST 256/257;
- expression depth 256/257;
- total arguments 255/256;
- call arguments 16/17;
- binding depth 16/17;
- applications 64/65;
- type depth 4/5;
- total type nodes 1,024/1,025;
- literal 160/161 Java characters and 640/641 UTF-8 bytes;
- hint 32/33 Java characters and 128/129 UTF-8 bytes;
- rune id 256/257 UTF-8 bytes;
- input name 128/129 UTF-8 bytes;
- budget 0, 128, -1, 129, fractional, and integer overflow;
- parameter index 0, negative, free, fractional, and integer overflow.

### State and authority

- graph-only read;
- valid source + graph;
- malformed current + graph;
- unsupported future + graph;
- valid/malformed/future source-only;
- neither component;
- source/graph mismatch;
- Guided + source for every valid/unreadable/future/unreplayable combination;
- read performs no compile, graph replacement, component removal, or rewrite.

### Commit and rollback

- functional success writes graph/source/name/resources and clears both Guided
  representations;
- Guided success clears source;
- graph-only success clears source;
- failure before commit leaves exact state;
- injected failure at each component application restores complete component
  equality and source bytes;
- registry generation, item, slot, menu, and request changes cancel without
  mutation;
- copy/save/reload/reconnect retain future and malformed bounded envelopes.

## 14. Implementation sequencing

This contract completes `L0-SOL-02`. It does not dispatch persistence.

Next gate:

```text
L0-SOL-03 — Server Compile Service Readiness
```

`L0-SOL-03` is documentation-only and must:

- assign exact non-overlapping files for `L0-TM-02`;
- freeze immutable rune snapshot/generation ownership;
- delimit executable/resource/knowledge admission;
- define cancellation and stale-generation evidence;
- keep item mutation and persistence out of `L0-TM-02`.

`L0-TM-02` remains `BLOCKED` until `L0-SOL-03` is accepted.

`L0-TM-03` remains blocked on accepted server service plus a separate atomic
persistence readiness assignment. No implementation of the component, envelope,
codec, parser, commit path, StreamCodec, or payload is authorized by this
document.

## 15. Gate result

```text
L0-TH-01 DONE (APPROVE)
    -> L0-SOL-02 DONE (ACCEPT)
    -> L0-SOL-03 READY

L0-TM-02 BLOCKED
L0-TM-03 BLOCKED
L0-TM-04 BLOCKED
L0-LU-01 BLOCKED
L0-TM-05 BLOCKED
```
