# A0 Authoring Metadata and Extension Boundary Contract

**Status:** Sol W0 frozen for staged implementation

**Contract version:** 1

**Release placement:** contract in `0.2.x`; implementation begins in `0.3.0`

**Owner:** Sol

**Parent assignment:** `FOUNDATION_BETA_A0_ASSIGNMENT.md`

**Execution authority:** unchanged — `ProgramGraph`

**Guided persistence:** unchanged — `GuidedWorkspaceState` schema 1

---

## 1. Decision Summary

This contract freezes the following decisions:

1. A rune presentation uses the rune's existing namespaced id. It does not
   introduce another rune identity.
2. A Rune Form has its own stable namespaced id. Every current
   `CustomSpellAction` id is already a Rune Form id and remains unchanged.
3. Executable rune semantics, rune presentation, Rune Form identity, Rune Form
   expansion, notation, and canvas state are separate boundaries.
4. `RunePresentationDescriptor` is optional presentation data. Its absence or
   failure cannot invalidate or disable an executable graph.
5. `RuneFormDefinition` is replay-sensitive authoring data. Missing or changed
   semantics disable Guided editing for the affected workspace; they never
   trigger partial replay.
6. The first A0 implementation uses trusted built-in legacy adapters for form
   expansion. Pack-defined declarative expansion is deferred until a separate
   bounded-template semantic review.
7. Built-in, future KubeJS, and future datapack sources assemble immutable
   candidate snapshots with deterministic precedence. Conflicting Rune Form
   semantics never use last-write-wins.
8. A successful candidate publishes atomically. A fatal candidate retains the
   last known-good snapshot.
9. A0 adds no field to `ProgramGraph`, no Data Component, and no persistence
   schema during its first implementation.
10. A1, D0, S0, L0, and L1 may consume A0 metadata but may not redefine its
    identities or authority.

These decisions are sufficient to start Cycle 2 registry and adapter work.

---

## 2. Product Outcome

A player sees the same Guided palette and obtains the same proof as before, but
the code no longer requires one enum to own:

- persistent form identity;
- categories;
- labels;
- icons;
- compact formulas;
- parameter editor descriptions;
- future notation input;
- discovery by authoring surfaces.

The initial player-visible result is intentionally compatibility-preserving:

- current categories remain in the same order;
- current forms remain in the same order within their categories;
- current EN/PT-BR keys remain valid;
- current compact notation remains unchanged;
- current parameter defaults and bounds remain unchanged;
- applying the same invocation sequence produces an equal `ProgramGraph`;
- missing optional presentation metadata uses a technical fallback;
- missing replay semantics makes the workspace read-only with a diagnostic.

A0 creates leverage for later authoring work; it does not itself add a new
authoring mode.

---

## 3. Problem and Non-Goals

### 3.1 Problem

`CustomSpellAction` currently mixes four different kinds of information:

1. **persistent identity**, through `persistentId()`;
2. **presentation**, through translation keys, categories, icons, and compact
   notation;
3. **parameter editing**, through numeric parameter descriptors;
4. **expansion semantics**, through enum-specific logic in
   `CustomSpellWorkspace`.

The screen also derives palette content and ordering from
`CustomSpellAction.values()` and `Category.values()`. This makes future
presentation, notation, Discipline ordering, and pack extension depend on enum
ownership and ordinal stability.

### 3.2 Non-goals

This contract does not authorize:

- changing `ProgramGraph`;
- changing execution, validation, purity, costs, resources, or executor keys;
- changing `GuidedWorkspaceState` schema 1;
- removing legacy `program_custom_actions` dual-write data;
- introducing `ADVANCED` persistence or mutable graph packets;
- defining L0 source persistence or the L1 textual grammar;
- implementing Disciplines or notation profiles;
- arbitrary JavaScript callbacks;
- player-defined native executors;
- public datapack or KubeJS A0 codecs in the first registry slice;
- pack-defined Rune Form expansion in the first registry slice;
- formula text becoming semantic lookup;
- automatic recovery or approximation of an unreplayable Guided workspace.

---

## 4. Sources of Truth and Authority

| Concern | Authoritative source | A0 role |
|---|---|---|
| Executable proof | `ProgramGraph` | none; presentation only |
| Rune signature and execution metadata | `RuneDefinition` / `RuneRegistry` | referenced read-only |
| Server graph validity | `ProgramValidator` and execution policy | always recomputed |
| Guided editable recipe | `GuidedWorkspaceState` | resolves form ids for exact replay |
| Current form expansion | `CustomSpellWorkspace` legacy behavior | characterized behind trusted adapter |
| Rune display metadata | `RunePresentationRegistry` snapshot | optional projection |
| Rune Form definition | `RuneFormRegistry` snapshot | identity, parameters, presentation, expansion reference |
| Notation profile | future S0 contract | consumes A0 template; cannot alter identity |
| Canvas positions and viewport | client session / future A1 workspace | never part of A0 identity |
| Localization text | client resource pack by translation key | presentation only |

The client may propose an invocation by stable form id and bounded arguments.
The server must:

1. resolve the form in its active snapshot;
2. canonicalize and validate all arguments;
3. expand through an approved bounded strategy;
4. validate the resulting graph;
5. recheck held-item and mode authority;
6. perform the existing atomic item write.

A client descriptor, label, icon, category, formula, ordering value, or cached
snapshot never authorizes a write or cast.

---

## 5. Identity Model

### 5.1 Rune presentation identity

`RunePresentationDescriptor` is keyed by the exact stable id from
`RuneDefinition.id()`.

Example:

```text
RuneDefinition.id          = mathmod:number_add
RunePresentation.runeId    = mathmod:number_add
```

There is no separate persistent presentation id. A descriptor whose `runeId`
does not resolve in the active `RuneRegistry` is an orphan and is rejected from
the candidate snapshot with a record-local diagnostic.

The following are not identity:

- translation key;
- localized label;
- glyph;
- compact formula;
- icon texture;
- category;
- layout;
- sort order;
- purity color;
- enum name or ordinal.

### 5.2 Rune Form identity

Every Rune Form has one stable `NamespacedId formId`.

The canonical id for every current enum value is:

```text
mathmod:<lowercase_enum_name>
```

This is not a new naming rule applied at load time. It characterizes the strings
already returned by `CustomSpellAction.persistentId()` and freezes them as
public ids.

Examples:

```text
SELF               -> mathmod:self
NUMBER_ONE         -> mathmod:number_one
FINITE_DIFFERENCE  -> mathmod:finite_difference
EXECUTE_PLAN       -> mathmod:execute_plan
```

Adding, removing, or reordering enum constants must not create ids after the A0
adapter exists. New forms must declare an explicit namespaced id.

### 5.3 Category identity

Categories use namespaced ids. Current categories map as follows:

| Legacy category | Canonical category id |
|---|---|
| `SOURCES` | `mathmod:sources` |
| `ALGEBRA` | `mathmod:algebra` |
| `GEOMETRY` | `mathmod:geometry` |
| `TRIGONOMETRY` | `mathmod:trigonometry` |
| `CALCULUS` | `mathmod:calculus` |
| `LINEAR_ALGEBRA` | `mathmod:linear_algebra` |
| `SYMMETRY` | `mathmod:symmetry` |
| `ALCHEMY` | `mathmod:alchemy` |
| `METAMAGIC` | `mathmod:metamagic` |
| `QUERIES` | `mathmod:queries` |
| `EFFECTS` | `mathmod:effects` |

Category id is authoring organization, not execution semantics. Moving a form
between categories does not change a graph, but built-in category mappings and
ordering remain visually frozen for the compatibility migration.

### 5.4 Alias identity

Aliases are directed mappings from a legacy form token to one canonical form
id. They do not create a second definition.

Resolution order is:

1. exact canonical namespaced form id;
2. registered namespaced alias;
3. current legacy unqualified path, interpreted in the `mathmod` namespace;
4. current case-insensitive enum-name alias;
5. ordinal migration only when reading an explicitly ordinal-bearing legacy
   field.

A numeric invocation string is never interpreted as an ordinal. Ordinal
migration is confined to the legacy format that declared the value as an
ordinal.

Alias rules:

- maximum chain depth: 8;
- cycles are fatal to the candidate alias snapshot;
- one alias cannot target two canonical ids;
- aliases cannot shadow a live canonical id;
- a missing final target produces `UNKNOWN_FORM`;
- successful alias resolution does not mutate an item during read;
- the next explicit successful save writes the canonical id.

---

## 6. Immutable Data Model

The following are conceptual Java contracts. Package and constructor mechanics
may vary, but fields, authority, and validation rules may not.

### 6.1 `RunePresentationDescriptor`

```text
RunePresentationDescriptor
  runeId: NamespacedId
  nameTranslationKey: String
  categoryId: NamespacedId
  icon: PresentationIcon
  formula: FormulaLayoutTemplate
  sortOrder: int
  descriptionTranslationKey: Optional<String>
```

Rules:

- `runeId` must resolve to an active rune definition.
- `nameTranslationKey` and optional description key are resource keys, not text
  sent back as semantic data.
- `icon` is a bounded declarative reference; it cannot contain renderer code.
- `formula` is presentation-only.
- `sortOrder` is used only within a stable category/profile projection.
- no field may restate or override rune inputs, output, purity, budget,
  resources, attributes, tier, executor key, params, or enabled state.
- missing descriptors use the fallback in section 12.

### 6.2 `RuneFormDefinition`

```text
RuneFormDefinition
  formId: NamespacedId
  nameTranslationKey: String
  categoryId: NamespacedId
  icon: PresentationIcon
  formula: FormulaLayoutTemplate
  parameters: List<AuthoringParameterDescriptor>
  inputHints: List<FormInputHint>
  outputHint: Optional<String>
  sortOrder: int
  expansion: RuneFormExpansionSpec
```

Fields divide into two groups.

**Replay-sensitive fields:**

- `formId`;
- parameter keys, order, type ids, defaults, and canonicalization rules;
- input-hint identities when the expansion strategy consumes them;
- `expansion`.

**Presentation-only fields:**

- translation keys;
- category;
- icon;
- formula;
- sort order;
- input and output display hints that are not consumed by expansion.

An implementation must expose this distinction directly, preferably through a
semantic fingerprint operation. It must not infer replay compatibility from
record equality.

### 6.3 `AuthoringParameterDescriptor`

```text
AuthoringParameterDescriptor
  key: String
  typeId: NamespacedId
  translationKey: String
  defaultValue: CanonicalParameterValue
  constraints: ParameterConstraints
  editorHint: Optional<NamespacedId>
```

Cycle 2 supports one type:

```text
mathmod:number
```

Its value is a finite IEEE-754 double, canonicalized exactly as current
`CustomNumericParameter` behavior:

- non-finite input becomes the declared default;
- finite input clamps to inclusive minimum and maximum;
- persistence continues to use the current hexadecimal double encoding;
- descriptor order determines canonical argument encoding order;
- unknown supplied keys are ignored for schema 1 compatibility;
- missing keys use the declared default.

For every migrated built-in form, key, order, default, minimum, and maximum must
match the current enum declaration exactly.

Future boolean, integer, enum, resource-id, selector, vector, or structured
parameters require:

- a stable namespaced type id;
- bounded canonical codec;
- editor and narration behavior;
- migration impact review.

If such a value must be persisted in Guided invocations and cannot be encoded
by the current double argument grammar, it requires `GuidedWorkspaceState`
schema 2. It may not be smuggled into a numeric or display string.

### 6.4 `PresentationIcon`

Cycle 2 supports:

```text
RuneIcon(runeId)
```

This preserves current `iconRuneId()` behavior and existing rune texture
resolution. Unknown icon rune ids fall back to the technical-id icon and emit a
record-local diagnostic; they do not remove the executable rune or graph.

Additional item, texture, or glyph icon kinds require bounded resource
validation but do not require a persistence migration because icons are not
saved semantic identity.

### 6.5 `FormulaLayoutTemplate`

The template is a controlled immutable tree. It never contains executable text,
callbacks, reflection, TeX macros, commands, or lookup code.

The shared node vocabulary is:

```text
Symbol(token)
InputRef(inputName)
ParameterRef(parameterKey)
Sequence(children)
Group(openToken, body, closeToken)
FunctionApplication(function, arguments)
Fraction(numerator, denominator)
Superscript(base, exponent)
Subscript(base, subscript)
Radical(body)
Integral(lower, upper, body, variable)
Summation(lower, upper, body)
Matrix(rows)
Cases(rows)
```

Cycle 2 only needs to construct and render the subset required to reproduce
current compact notation. Unsupported valid nodes must degrade to a bounded
linear technical representation; they must not affect lookup or execution.

Template validation:

- maximum nodes: 128;
- maximum depth: 16;
- maximum sequence children: 32;
- maximum matrix cells: 64;
- maximum cases: 16;
- maximum token length: 64;
- every `InputRef` must refer to a declared input name;
- every `ParameterRef` must refer to a declared parameter key;
- tokens are data, never translation keys unless the node explicitly declares
  a translation-key reference in a later contract.

S0 may add renderers and notation-specific overlays. It may not reinterpret a
template node as execution semantics.

### 6.6 `RuneFormExpansionSpec`

The sealed expansion boundary is:

```text
RuneFormExpansionSpec
  LegacyBuiltInAdapter(adapterId)
  FutureDeclarativeTemplate(templateId)  // reserved, not enabled in Cycle 2
```

Cycle 2 permits only `LegacyBuiltInAdapter`.

An adapter id:

- is namespaced and explicitly registered by trusted Java;
- maps one current form id to the existing deterministic expansion behavior;
- accepts only canonical bounded parameter values and the current bounded
  workspace context;
- cannot access a player, level, item, network, clock, random source, file,
  command, JavaScript callback, or executor;
- returns a proposed graph/workspace result for normal server validation;
- is covered by graph-equality characterization tests.

The adapter id is internal implementation identity. Persisted workspaces keep
the form id, not the adapter id.

`FutureDeclarativeTemplate` remains disabled until Terra High reviews:

- template composition and input selection;
- maximum nodes and edges added per invocation;
- deterministic node-id allocation;
- constant encoding;
- previous-output and typed-input reuse;
- failure atomicity;
- exact graph equivalence;
- pack validation and diagnostics.

### 6.7 `AuthoringCategoryDescriptor`

```text
AuthoringCategoryDescriptor
  categoryId: NamespacedId
  translationKey: String
  sortOrder: int
```

Missing category presentation does not invalidate a form. The UI falls back to
the category's technical id and deterministic id ordering.

### 6.8 Registry snapshots

```text
AuthoringMetadataSnapshot
  generation: long
  runePresentations: Map<NamespacedId, RunePresentationDescriptor>
  runeForms: Map<NamespacedId, RuneFormDefinition>
  categories: Map<NamespacedId, AuthoringCategoryDescriptor>
  aliases: Map<LegacyFormKey, NamespacedId>
  diagnostics: List<AuthoringMetadataDiagnostic>
```

Snapshots are immutable and assembled off to the side. Readers see either the
old complete snapshot or the new complete snapshot, never a partially rebuilt
registry.

Registry query surface:

```text
RunePresentationRegistry.snapshot()
RunePresentationRegistry.find(runeId)
RuneFormRegistry.snapshot()
RuneFormRegistry.find(formId)
RuneFormRegistry.resolveFormId(rawId, legacyContext)
RuneFormRegistry.orderedForms(categoryId)
```

Returned collections are immutable. Ordering is:

1. category `sortOrder`;
2. category id;
3. form `sortOrder`;
4. form id.

Localized text never participates in registry ordering because locale changes
must not change identity or replay.

---

## 7. Semantic Fingerprint and Compatible Overrides

A Rune Form semantic fingerprint is computed from a canonical representation
of:

- form id;
- ordered parameter keys;
- parameter type ids;
- canonical defaults and constraints;
- expansion kind and expansion id;
- any future declarative expansion template;
- any input identity consumed by expansion.

It excludes:

- translation keys;
- category id;
- icon;
- formula;
- sort order;
- purely descriptive input/output hints.

Two definitions with the same form id are replay-compatible only when their
semantic fingerprints are equal.

Consequences:

- presentation-only replacement is allowed under source precedence;
- changing a parameter default, range, key, order, type, adapter, or expansion
  template under an existing form id is rejected;
- a semantic change requires a new form id plus an explicit migration alias
  only when exact equivalence is proven;
- an alias cannot claim equivalence between definitions that produce different
  graphs for any accepted invocation/workspace context.

The fingerprint is a compatibility comparison, not a persisted security hash.
If an implementation serializes it for diagnostics, the algorithm and version
must be explicit; saved workspaces do not depend on that serialization.

---

## 8. Source Precedence, Namespace, and Collision Policy

### 8.1 Sources

The logical source order is:

```text
built-in < KubeJS startup declarations < datapack candidate
```

This matches the established declarative extension direction in the project,
but A0 applies the compatibility rules below rather than generic
last-write-wins.

The first Cycle 2 slice implements built-ins only. KubeJS and datapack ingestion
remain disabled until their codecs/builders and network behavior receive a
separate implementation approval.

### 8.2 Rune presentations

For `RunePresentationDescriptor`:

- higher-precedence sources may replace the full presentation descriptor;
- replacement may change only presentation because the model contains no
  executable fields;
- the target rune must exist in the active server rune registry;
- duplicate ids within one source layer are fatal to that layer's candidate;
- byte-for-byte or record-equal duplicates within one layer are still rejected
  to prevent load-order dependence;
- datapack stack selection for the same resource path follows the resource
  manager, but two different resources resolving to the same logical rune id
  are a duplicate;
- malformed or orphan descriptors are record-local rejections;
- built-in fallback remains available when an overlay is rejected.

### 8.3 Rune Forms

For `RuneFormDefinition`:

- a new id may be introduced only in the declaring source's namespace, unless
  an explicit compatibility allowlist grants another namespace;
- `mathmod:*` built-in form semantics are reserved to MathMod;
- higher-precedence sources may replace presentation fields of an existing form
  only when the semantic fingerprint is equal;
- conflicting semantic fingerprints are fatal to the entire candidate
  snapshot;
- duplicate ids within one source layer are fatal even if equal;
- source order never chooses between conflicting expansion semantics;
- aliases are validated after all definitions are assembled.

This protects saved workspaces from a pack changing the meaning of a persistent
form id.

### 8.4 Categories

For category descriptors:

- higher-precedence sources may replace translation key and sort order;
- category replacement does not affect replay;
- a missing category descriptor uses a technical fallback;
- duplicate ids within one layer are fatal to that layer's candidate.

### 8.5 KubeJS

If an A0 KubeJS API is later exposed:

- declarations are startup-only;
- the declaration store freezes before the first server authoring snapshot;
- duplicate KubeJS ids are rejected rather than resolved by script call order;
- builders accept declarative values only;
- no callback, function object, executor key, player, level, item mutation,
  command, reflection handle, or arbitrary Java object is accepted;
- KubeJS may add presentation metadata;
- KubeJS may not provide expansion in the first public A0 API.

### 8.6 Datapacks

If datapack A0 loading is later exposed:

- the server loads and validates the complete candidate;
- clients do not independently load semantic Rune Form definitions;
- datapacks may add or override presentation under the rules above;
- external expansion remains disabled until the declarative-template contract
  is approved;
- a reload never rewrites items or executes forms.

### 8.7 Resource packs

Resource packs may provide:

- localized strings for declared translation keys;
- textures or glyph assets referenced by allowed icon kinds.

Resource packs may not provide:

- form ids;
- aliases;
- parameter definitions;
- expansion semantics;
- rune signatures;
- category membership used by the server snapshot.

Missing localized text falls back to a readable technical label. Missing assets
fall back to a standard technical icon. Neither affects replay.

### 8.8 Server snapshot and clients

The server snapshot is authoritative for form availability, canonical parameter
rules, category ids, ordering, formula structure used by authoring, and
expansion selection.

Until a bounded network snapshot exists, the first Cycle 2 implementation may
use identical built-in common-side registrations on client and server. It must
still validate the form again on the server.

A future network payload requires its own protocol version and limits. A client
with a missing or incompatible snapshot may inspect graphs with fallbacks but
may not submit Guided mutations based on guessed metadata.

---

## 9. Candidate Assembly, Diagnostics, and Reload

### 9.1 Candidate lifecycle

```text
collect sources
  -> decode records
  -> validate local bounds
  -> index each source
  -> apply precedence-compatible overlays
  -> validate semantic fingerprints
  -> validate rune references
  -> validate aliases and categories
  -> build immutable snapshot
  -> publish atomically
```

### 9.2 Record-local failures

These reject one external record and allow candidate construction to continue:

- malformed optional rune presentation;
- unknown presentation rune id;
- missing icon asset reference;
- malformed category presentation;
- unsupported formula node in an optional presentation overlay, when a bounded
  technical fallback can replace it without changing form semantics.

The rejected overlay falls back to the next lower-precedence valid definition.

### 9.3 Fatal candidate failures

These reject publication of the whole candidate:

- conflicting Rune Form semantic fingerprints;
- duplicate form ids within one source layer;
- alias cycles or aliases shadowing canonical ids;
- unknown legacy adapter id;
- a built-in form missing its required adapter;
- snapshot count or aggregate bounds exceeded;
- nondeterministic ordering inputs;
- form parameter definitions incompatible with Guided schema 1;
- any validation failure that could change replay depending on load order.

### 9.4 Last-known-good behavior

On a reload candidate failure:

- the active snapshot and generation remain unchanged;
- no item, player data, graph, or workspace is rewritten;
- one summary diagnostic states that the previous snapshot remains active;
- detailed diagnostics identify code, record kind, id, source, and reason;
- the client continues using the last server-published generation;
- no partial source layer is published after a fatal failure.

At initial startup, invalid built-in metadata is a startup failure. There is no
older safe snapshot to retain.

### 9.5 Diagnostic model

Each diagnostic contains:

```text
severity: INFO | WARNING | ERROR | FATAL
code: stable technical code
recordKind: RUNE_PRESENTATION | RUNE_FORM | CATEGORY | ALIAS | SNAPSHOT
id: optional NamespacedId
sourceKind: BUILT_IN | KUBEJS | DATA_PACK | NETWORK
sourceName: bounded String
message: bounded technical String
```

Minimum stable codes:

```text
DECODE_FAILED
UNSUPPORTED_SCHEMA
DUPLICATE_ID
UNKNOWN_RUNE
UNKNOWN_FORM
UNKNOWN_CATEGORY
UNKNOWN_ICON
UNKNOWN_ADAPTER
SEMANTIC_COLLISION
ALIAS_CYCLE
ALIAS_SHADOWS_CANONICAL
INVALID_PARAMETER
INVALID_TEMPLATE
LIMIT_EXCEEDED
SNAPSHOT_NOT_PUBLISHED
WORKSPACE_UNREPLAYABLE
GRAPH_REPLAY_MISMATCH
```

Raw exception text is logged for developers but is not a stable player
protocol. Player-facing explanations resolve stable codes through localization.

---

## 10. Boundedness

The A0 snapshot and its records have explicit maxima:

| Boundary | Maximum |
|---|---:|
| Rune presentation descriptors | 2,048 |
| Rune Forms | 1,024 |
| Categories | 128 |
| Aliases | 2,048 |
| Parameters per form | 16 |
| Input hints per form | 16 |
| Formula nodes per template | 128 |
| Formula depth | 16 |
| Translation/source key length | 160 characters |
| Display token length | 64 characters |
| Diagnostics retained per candidate | 1,024 |
| Alias resolution depth | 8 |
| Guided invocations | existing 128 |
| Encoded Guided invocation | existing 512 characters |
| Expanded graph nodes | existing `ProgramValidator.MAX_NODES`, 64 |
| Expanded graph edges | existing `ProgramValidator.MAX_EDGES`, 128 |
| Graph budget limit | existing `ProgramValidator.MAX_BUDGET_LIMIT`, 128 |

Counts are checked before publication or graph activation. Truncating a
semantic collection is forbidden. Diagnostic retention may cap additional
record-local messages after emitting `LIMIT_EXCEEDED`.

Registry lookup must be bounded map lookup. Ordering may be
`O(n log n)` during candidate construction and cached in the immutable
snapshot. No sorting by localized text occurs during replay.

---

## 11. Legacy Adapter and Migration Policy

### 11.1 Compatibility window

The first A0 implementation keeps:

- `CustomSpellAction`;
- `CustomSpellInvocation`;
- `CustomSpellWorkspace`;
- current legacy field readers;
- current dual-write behavior;
- `GuidedWorkspaceState.CURRENT_VERSION == 1`.

The enum becomes an implementation detail behind the registry adapter. It is not
removed in the same slice that introduces the registry.

### 11.2 Adapter construction

Built-in registration iterates an explicit compatibility table, not enum
ordinal as identity. For each of the 67 current enum constants, it registers:

- canonical form id equal to current `persistentId()`;
- current translation key;
- current category mapping;
- current icon rune id;
- current compact notation represented by a bounded template;
- current ordered numeric parameter descriptors;
- current sort order equal to the current category/enum presentation order;
- trusted legacy adapter id.

The compatibility table must be tested against the enum during the migration
window. A missing or extra enum value fails built-in snapshot construction.

### 11.3 Read behavior

| Persisted state | Execution | Guided editing |
|---|---|---|
| Valid graph + known exact workspace | graph remains authoritative | enabled after equal replay |
| Valid graph + aliased legacy forms | graph remains authoritative | enabled only after canonical replay equals graph |
| Valid graph + unknown form | graph remains authoritative | disabled |
| Valid graph + malformed arguments | graph remains authoritative | disabled if canonical replay cannot equal graph |
| Valid graph + future workspace version | graph remains authoritative | disabled |
| Workspace without valid graph | not executable | recovery/export only; no activation |

Reads do not rewrite. An explicit successful Guided save may write canonical
form ids while preserving the same graph and invocation order.

### 11.4 Exact replay

For migration to be accepted:

```text
legacyWorkspace.toGraph() == registryAdapterWorkspace.toGraph()
```

Equality is exact `ProgramGraph` equality, including:

- node ids and order;
- rune ids;
- constants;
- edges and input names;
- output node id;
- budget limit.

Semantic similarity, isomorphism, normalized equality, same effect, or same
cost is insufficient.

### 11.5 When schema 2 is required

Cycle 2 does not require schema 2 because:

- form ids remain the same strings;
- invocation order remains the same;
- numeric argument grammar remains the same;
- adapter ids and presentation metadata are not persisted.

`GuidedWorkspaceState` schema 2 is required before any of these:

- nonnumeric parameter values not representable by the current grammar;
- structured invocation records replace encoded strings;
- a workspace stores a form semantic version or fingerprint;
- alias provenance must be preserved;
- multiple named outputs or nested form structure is persisted;
- exact replay needs metadata not present in schema 1.

A desire to clean up encoding is not by itself permission for a schema bump.

### 11.6 Removed definitions

Removing a Rune Form does not remove its invocation text from a workspace.
Unknown text remains preserved within existing bounds. Guided editing is
disabled, while an intact graph remains inspectable and executable subject to
normal rune availability and execution validation.

No migration drops an invocation, substitutes another form, or guesses a graph.

---

## 12. Failure and Fallback Behavior

### 12.1 Missing rune presentation

Fallback presentation is derived without changing semantics:

- name: readable path from rune id;
- category: `mathmod:uncategorized`;
- icon: standard technical rune icon;
- formula: technical rune id plus named inputs;
- order: rune id.

The inspector remains available.

### 12.2 Missing category presentation

Use the readable category id and deterministic id order. Forms remain available
if their expansion definition is valid.

### 12.3 Missing form presentation fields

A built-in form definition must be structurally complete. An invalid external
presentation overlay is rejected and falls back to the lower-precedence
complete form presentation.

### 12.4 Missing or conflicting expansion

The form is not usable for a new Guided mutation. A workspace containing it is
unreplayable. The UI must show `WORKSPACE_UNREPLAYABLE` or the more specific
diagnostic and offer read-only graph inspection.

It must not:

- omit the form;
- replay the remaining list;
- substitute a rune with the same icon;
- use a form with the same translated name;
- use a higher-precedence conflicting expansion;
- write a repaired workspace automatically.

### 12.5 Replay mismatch

If every invocation resolves but replayed graph differs from the authoritative
graph, Guided editing remains disabled with `GRAPH_REPLAY_MISMATCH`.

The mismatch is not repaired through graph normalization or node renaming.

### 12.6 Client/server mismatch

If client generation differs from the server:

- read-only inspection remains possible with technical fallbacks;
- Guided mutation controls are disabled or refreshed;
- the server rejects stale form mutation requests;
- no client-provided descriptor or parameter bound is trusted.

---

## 13. Boundaries with Other Epics

### 13.1 A1 — Advanced editor

A1 may consume:

- rune names, icons, categories, formulas;
- parameter editor descriptors;
- stable rune ids and named inputs.

A1 owns:

- graph working copies;
- node/edge/output mutation;
- validation history;
- undo/redo;
- canvas metadata;
- future Advanced persistence;
- Guided-to-Advanced conversion.

A0 must not store canvas coordinates, selected nodes, zoom, history, or mutable
graph state.

### 13.2 D0 — Built-in Disciplines

D0 may provide a projection that:

- recommends forms;
- filters by learned availability;
- changes palette ordering;
- selects a default notation profile.

D0 may not:

- change a form or rune id;
- replace form expansion;
- change graph validation or executor behavior;
- place Discipline state in A0 registries;
- make an old talisman depend on the active Discipline.

Discipline ordering is a derived view over the stable A0 snapshot.

### 13.3 S0 — Notation profiles

S0 may map A0 formula trees to alternate renderings. It may add
profile-specific presentation overlays keyed by stable rune/form id.

S0 may not:

- change lookup;
- change parameter values;
- alter graph equality, cost, purity, or execution;
- treat a glyph or formula as identity;
- execute TeX or macros.

### 13.4 L0 — Scoped functional source

L0 uses rune semantic signatures from `RuneRegistry`, not presentation
descriptors, for checking and lowering.

L0 may use A0 presentation for inspection and editor affordances only. Its
source codec, optional Data Component, compiler limits, and atomic source/graph
write remain independent.

A0 and L0 must not share a persistence component or schema version.

### 13.5 L1 — Textual proof DSL

L1 parses stable rune ids/names defined by its grammar and targets
`ScopedProgramSource`.

A0 may supply completion labels, documentation, and rendered formulas. A
translation, compact symbol, or category never becomes an implicit parser
alias. Textual aliases require the L1 grammar contract.

---

## 14. Dependency Graph

```text
P2 authority and exact Guided replay
              |
              v
      A0 W0 contract (this document)
          /          \
         v            v
 A0 pure models   legacy characterization tests
         \            /
          v          v
       built-in candidate snapshot
                   |
                   v
       legacy adapter equivalence gate
                   |
         +---------+----------+
         |                    |
         v                    v
 A1 presentation use   Luna terminology migration
         |
         v
 future external A0 schema review
         |
   +-----+------+
   v            v
 KubeJS      datapacks
```

L0 audit and read-only A1 hardening may proceed independently because neither
requires A0 persistence or external loading.

---

## 15. Cycle 2 File Ownership Proposal

One Terra Medium implementation conversation owns the A0 core boundary.

### 15.1 Expected new files

Recommended package:

```text
src/main/java/com/mathmod/authoring/
```

Expected model and registry files:

```text
AuthoringCategoryDescriptor.java
AuthoringMetadataDiagnostic.java
AuthoringMetadataSnapshot.java
AuthoringParameterDescriptor.java
BuiltInAuthoringMetadata.java
FormulaLayoutTemplate.java
LegacyRuneFormAdapter.java
PresentationIcon.java
RuneFormDefinition.java
RuneFormExpansionSpec.java
RuneFormRegistry.java
RunePresentationDescriptor.java
RunePresentationRegistry.java
```

Expected tests:

```text
src/test/java/com/mathmod/authoring/**
```

Names may be consolidated when that reduces mechanical classes, but the
contract boundaries must remain visible.

### 15.2 Files initially owned by the A0 implementer

Phase A0-1:

- new `com.mathmod.authoring` production files;
- new `com.mathmod.authoring` tests.

Phase A0-2, after pure models pass:

- `CustomSpellAction.java`;
- `CustomSpellInvocation.java`;
- `CustomSpellWorkspace.java`;
- their focused tests.

Phase A0-3, only after the read-only A1 owner hands off:

- exact Guided palette methods in `RuneProgrammerScreen.java`;
- exact associated screen tests;
- preview harness cases explicitly assigned to the A0 integrator.

### 15.3 Read-only

- `ProgramGraph.java`;
- `ProgramNode.java`;
- `ProgramEdge.java`;
- `RuneDefinition.java`;
- `RuneRegistry.java`;
- `ProgramValidator.java`;
- `ProgramSurface.java`;
- `ProgramSurfaceMode.java`;
- `GuidedWorkspaceState.java`;
- `ModDataComponents.java`;
- existing network payload schemas;
- P2, P4, A0, and roadmap documents.

### 15.4 Forbidden

- graph schema or equality changes;
- Data Component additions or edits;
- `ProgramSurfaceMode` additions;
- mutable graph packets;
- execution allowlist changes;
- inscription atomicity changes;
- knowledge or Discipline attachments;
- L0 codecs;
- public KubeJS or datapack A0 loaders in the first slice.

### 15.5 File conflict rule

If A1 read-only work still owns `RuneProgrammerScreen` or the preview harness,
A0 stops before Phase A0-3. One integrator receives the file after the first
handoff; concurrent complete-file edits are forbidden.

---

## 16. Staged Implementation Plan

### A0-1 — Pure metadata model

Owner: Terra Medium
Review: Terra High for bounds and fingerprints

Deliver:

- immutable descriptor and template types;
- validation and stable diagnostics;
- registry snapshot interfaces;
- deterministic ordering;
- semantic fingerprint comparison;
- pure unit tests.

No existing program or screen class changes.

### A0-2 — Built-in registry and characterization

Owner: Terra Medium
Review: Sol on identity table

Deliver:

- explicit 67-form built-in compatibility table;
- 11 built-in category descriptors;
- rune presentation fallback;
- built-in candidate snapshot;
- counts and duplicate validation;
- characterization tests for ids, categories, icons, formulas, parameters, and
  ordering.

No external loaders or public API.

### A0-3 — Legacy expansion adapter

Owner: Terra Medium
Review: Terra High for deterministic replay

Deliver:

- trusted adapter registry;
- form-id resolution through A0;
- current invocation decoding routed through the adapter boundary;
- exhaustive per-form and sequence graph-equality tests;
- unknown-form and replay-mismatch diagnostics;
- current Guided persistence unchanged.

The old path remains available for comparison during this slice.

### A0-4 — Guided palette consumption

Owner: Terra Medium integrator
Parallel: Luna after terminology handoff

Deliver:

- registry-backed category and form enumeration;
- current ordering and appearance preserved;
- registry-backed typed numeric parameter dialogs;
- fallback presentation;
- keyboard, narrator, search, and viewport regressions;
- no graph mutation beyond existing Guided behavior.

This slice is serialized after conflicting A1 screen work.

### A0-5 — Compatibility hardening

Owners:

- Terra Medium: runtime and tests;
- Luna: EN/PT-BR, Patchouli, preview evidence;
- Terra High: ambiguous collision or replay failures;
- Sol: release gate.

Deliver:

- old-save migration evidence;
- dedicated-server evidence;
- reload/reconnect behavior;
- malformed/missing metadata evidence;
- complete handoff;
- decision on whether external A0 schemas are ready for a new contract.

### Deferred A0-6 — External sources

Requires a new approved slice for:

- datapack codecs;
- KubeJS declarative builders;
- network snapshot payload;
- declarative form expansion template, if pursued.

---

## 17. Acceptance Matrix

### 17.1 Identity and registry

- [ ] All 67 current enum values map to their existing canonical ids.
- [ ] All canonical ids are unique and namespaced.
- [ ] All 11 current categories map to the frozen category ids.
- [ ] Enum reordering does not change registry lookup or saved identity.
- [ ] Registry iteration is deterministic without localized sorting.
- [ ] Returned snapshots and collections are immutable.
- [ ] Duplicate and limit diagnostics use stable codes.

### 17.2 Presentation isolation

- [ ] Removing all optional rune presentation descriptors leaves graph
  validation and execution unchanged.
- [ ] A missing descriptor produces the technical fallback.
- [ ] Changing icon, formula, category, translation key, or order cannot change
  graph equality, validation, cost, resources, purity, or execution.
- [ ] Orphan rune presentation is rejected locally.
- [ ] Formula templates obey node, depth, token, and reference limits.

### 17.3 Rune Form compatibility

- [ ] Every built-in form preserves translation key, category, icon, compact
  notation, parameter order, defaults, minimum, maximum, and palette order.
- [ ] Every current invocation string decodes to the same form and arguments.
- [ ] For every built-in form and accepted context, legacy and adapter expansion
  produce equal graphs.
- [ ] Representative multi-form sequences produce equal graphs.
- [ ] Unknown forms preserve invocation text and disable complete Guided replay.
- [ ] A conflicting semantic fingerprint rejects the candidate snapshot.
- [ ] A presentation-only compatible overlay does not alter replay.

### 17.4 Persistence and migration

- [ ] `GuidedWorkspaceState.CURRENT_VERSION` remains 1.
- [ ] `mathmod:program_guided_workspace` codec is unchanged.
- [ ] No new A0 Data Component exists.
- [ ] Reads never rewrite an item.
- [ ] Explicit resave canonicalizes aliases only after equal replay.
- [ ] Valid graphs remain inspectable and executable when workspace metadata is
  unknown, malformed, or future-versioned.
- [ ] No invocation is dropped, substituted, or partially replayed.

### 17.5 Authority and safety

- [ ] Server resolves and validates form ids and arguments independently.
- [ ] Stale client snapshot requests fail closed.
- [ ] Adapter code cannot access world, player, item, random, clock, file,
  command, or JavaScript callback.
- [ ] Expanded graphs pass existing validation before activation.
- [ ] No presentation field can register an executor or change a rune
  signature.
- [ ] Dedicated-server execution has no client presentation dependency.

### 17.6 Candidate and reload

- [ ] Candidate publication is atomic.
- [ ] Fatal reload retains the last known-good snapshot and generation.
- [ ] Record-local presentation failure falls back to a lower source.
- [ ] Duplicate ids within a source are not resolved by load order.
- [ ] Conflicting form semantics never use precedence.
- [ ] Alias cycles and canonical shadowing are rejected.
- [ ] Snapshot count limits fail before publication.

### 17.7 UI and product

- [ ] Guided palette player-visible behavior is unchanged in the compatibility
  slice.
- [ ] Search uses stable registry entries and presentation text without making
  text identity.
- [ ] Pointer and keyboard activate the same form id.
- [ ] Narration exposes label, category, parameter bounds, and technical id
  where required.
- [ ] EN/PT-BR and missing-translation fallbacks are evidenced.
- [ ] ATM10 viewport and first-use journey are evidenced under the Foundation
  Beta acceptance work.

---

## 18. Rollback

A0 implementation is divided into independently reversible slices.

Rollback rules:

- A0-1 and A0-2 add isolated models and registries and can be removed without
  save migration.
- A0-3 keeps the legacy expansion path long enough to compare and revert.
- A0-4 must not delete enum-owned presentation until compatibility evidence is
  accepted.
- No slice writes a new persistent format, so rollback to the pre-A0 build reads
  the same graph and Guided workspace.
- A failed reload retains the last known-good in-memory snapshot.
- Removing an external A0 experiment must leave saved invocation strings
  untouched and affected workspaces read-only.

Rollback must never:

- downgrade or rewrite a workspace on read;
- change a `ProgramGraph`;
- select a different expansion under the same form id;
- drop unknown invocation data.

---

## 19. Risks and Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Enum-to-registry ordering drift | visible palette regression | explicit compatibility table and ordering tests |
| Parameter default/order drift | saved invocation changes meaning | semantic fingerprint and exhaustive characterization |
| Screen refactor overlaps A1 | merge conflicts and lost behavior | serialize A0-4 after A1 handoff |
| Presentation becomes semantic | execution changes through resource data | descriptor model excludes executable fields |
| Form override changes replay | old saves produce different graph | conflicting fingerprints are fatal |
| Partial reload removes forms | Guided workspaces become inconsistent | atomic candidate and last-known-good snapshot |
| Client snapshot is trusted | authority bypass | server re-resolves and validates every mutation |
| Formula layout grows unbounded | client/server resource exhaustion | explicit tree and aggregate limits |
| External expansion invites callbacks | unsafe runtime extension | disabled until declarative-template review |
| Legacy adapter never retires | permanent duplicated architecture | reassess only after schema and migration evidence |

---

## 20. Explicit Deferrals

The following remain deferred after this contract:

- public A0 datapack schema;
- public A0 KubeJS API;
- declarative external Rune Form expansion;
- nonnumeric persisted Guided parameters;
- `GuidedWorkspaceState` schema 2;
- removal of `CustomSpellAction`;
- removal of legacy Guided fields;
- Advanced mutable editing and persistence;
- Discipline-specific registry semantics;
- notation profile selection and renderer completion;
- textual DSL aliases;
- user-defined operators;
- arbitrary custom renderers or callbacks.

Each requires its own approved task or contract.

---

## 21. Cycle 2 Start and Stop Gate

### Start authorized

Cycle 2 may start with A0-1 and A0-2 when:

- this contract is the referenced source of truth;
- one A0 core writer is named;
- exact files are declared before edits;
- A1 screen ownership is recorded;
- Terra High may review bounds/fingerprints without writing the same files;
- no persistence or public API work is bundled into the slice.

### Stop and escalate

Implementation must stop when:

- a current canonical id differs from the 67-form compatibility table;
- equal replay cannot be achieved without changing `ProgramGraph`;
- current parameter behavior cannot be represented by `mathmod:number`;
- A0 requires a Data Component or workspace schema bump;
- a proposed external form requires a callback;
- two sources need conflicting expansion semantics under one id;
- the client would need to authorize a form or graph change;
- A0 and A1 need the same screen file without an integrator handoff;
- A0 and L0 need the same codec, Data Component, or network payload;
- a fallback would silently repair or partially replay a workspace.

Sol resolves the stop condition or narrows the slice. Terra Medium does not
invent fallback semantics.

---

## 22. Reassessment Trigger

Reassess this contract after:

- A0-3 proves or fails exhaustive legacy replay equivalence;
- the first real old-save migration is run;
- A0-4 completes the Guided palette transition;
- an ATM10 or dedicated-server test exposes snapshot mismatch;
- a pack author demonstrates a concrete need for external presentation or forms;
- S0 requires a layout node not covered by the controlled tree;
- nonnumeric Guided parameters become a committed product requirement.

The reassessment may reduce scope. It must not weaken exact replay, server
authority, or stable-id rules inside `1.x`.

---

## 23. Handoff

### Completed

- Frozen the A0 presentation and authoring identity model.
- Defined immutable descriptor, template, parameter, expansion, registry, and
  diagnostic boundaries.
- Defined source precedence and collision behavior.
- Defined the legacy enum adapter and schema-1 migration policy.
- Defined exact boundaries with A1, D0, S0, L0, and L1.
- Defined bounds, failure behavior, rollback, implementation slices, file
  ownership, acceptance, and Cycle 2 gates.

### Decisions implemented

- A0 remains non-persistent.
- Current Rune Form ids remain unchanged.
- Presentation overlays may use precedence.
- Conflicting Rune Form semantics may not use precedence.
- Cycle 2 expansion is trusted built-in adapter only.
- Candidate snapshots publish atomically with last-known-good reload behavior.

### Files changed

- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`

### Contracts referenced

- `MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`
- `FOUNDATION_BETA_A0_ASSIGNMENT.md`
- `P2_MODE_PERSISTENCE_CONTRACT.md`
- `ADVANCED_EDITOR.md`
- `P4_FUNCTION_LANGUAGE_CONTRACT.md`
- `P4_SEMANTIC_REVIEW.md`
- `KUBEJS.md`

### Tests and evidence

- Source baseline inspected.
- No production or persistence code changed in W0.
- Implementation acceptance tests are specified in section 17.

### Known limitations

- External A0 codecs, builders, payloads, and declarative expansion remain
  intentionally unspecified at implementation level.
- Formula tree rendering beyond the current compatibility subset is deferred to
  S0.

### Unresolved questions

- None block A0-1 or A0-2.
- Retirement timing for the legacy enum is deferred until migration evidence.
- External form expansion remains a separate semantic review.

### Next owner

- Terra Medium, with Terra High review.

### Exact next task

- Implement A0-1 pure metadata models and registry snapshots, followed by A0-2
  built-in compatibility registration and characterization tests.

### Files the next owner may edit

- new files under `src/main/java/com/mathmod/authoring/`;
- new files under `src/test/java/com/mathmod/authoring/`.

### Files the next owner must not edit

- `ProgramGraph`, `ProgramSurfaceMode`, `GuidedWorkspaceState`,
  `ModDataComponents`, network payloads, execution policy, or public extension
  APIs during A0-1/A0-2.

---

## Appendix A — Frozen Built-In Rune Form IDs

The following 67 mappings are public compatibility input:

| Legacy enum | Canonical form id |
|---|---|
| `SELF` | `mathmod:self` |
| `NUMBER_ONE` | `mathmod:number_one` |
| `ADD_ONE` | `mathmod:add_one` |
| `SUBTRACT_ONE` | `mathmod:subtract_one` |
| `DOUBLE_NUMBER` | `mathmod:double_number` |
| `HALVE_NUMBER` | `mathmod:halve_number` |
| `CLAMP_NUMBER` | `mathmod:clamp_number` |
| `UP_VECTOR` | `mathmod:up_vector` |
| `LOOK_VECTOR` | `mathmod:look_vector` |
| `SCALE_VECTOR` | `mathmod:scale_vector` |
| `VECTOR_ADD_UP` | `mathmod:vector_add_up` |
| `VECTOR_SUBTRACT_UP` | `mathmod:vector_subtract_up` |
| `NORMALIZE_VECTOR` | `mathmod:normalize_vector` |
| `VECTOR_LENGTH` | `mathmod:vector_length` |
| `DOT_WITH_LOOK` | `mathmod:dot_with_look` |
| `DISTANCE_TO_SELF` | `mathmod:distance_to_self` |
| `SPHERE_REGION` | `mathmod:sphere_region` |
| `BOX_REGION` | `mathmod:box_region` |
| `REGION_CONTAINS_SELF` | `mathmod:region_contains_self` |
| `SAMPLE_REGION` | `mathmod:sample_region` |
| `RAYCAST` | `mathmod:raycast` |
| `RAY_HIT_POSITION` | `mathmod:ray_hit_position` |
| `NEARBY_LIVING` | `mathmod:nearby_living` |
| `FILTER_NON_PLAYERS` | `mathmod:filter_non_players` |
| `FILTER_TARGETS_REGION` | `mathmod:filter_targets_region` |
| `NEAREST_TARGETS` | `mathmod:nearest_targets` |
| `NEARBY_BLOCKS` | `mathmod:nearby_blocks` |
| `FILTER_BLOCKS_REGION` | `mathmod:filter_blocks_region` |
| `BLOCK_POSITIONS` | `mathmod:block_positions` |
| `AVERAGE_POSITION` | `mathmod:average_position` |
| `PUSH_SELF` | `mathmod:push_self` |
| `DEBUG_MARKER` | `mathmod:debug_marker` |
| `BLINK` | `mathmod:blink` |
| `PUSH_TARGETS_PLAN` | `mathmod:push_targets_plan` |
| `EXECUTE_PLAN` | `mathmod:execute_plan` |
| `RIGHT_BASIS_VECTOR` | `mathmod:right_basis_vector` |
| `FORWARD_BASIS_VECTOR` | `mathmod:forward_basis_vector` |
| `OBLIQUE_BASIS_VECTOR` | `mathmod:oblique_basis_vector` |
| `SINE_NUMBER` | `mathmod:sine_number` |
| `COSINE_NUMBER` | `mathmod:cosine_number` |
| `CROSS_WITH_UP` | `mathmod:cross_with_up` |
| `PROJECT_ONTO_LOOK` | `mathmod:project_onto_look` |
| `REFLECT_ACROSS_UP` | `mathmod:reflect_across_up` |
| `QUARTER_TURN_VECTOR` | `mathmod:quarter_turn_vector` |
| `HEAL_SELF` | `mathmod:heal_self` |
| `SPEED_SELF` | `mathmod:speed_self` |
| `INVISIBILITY_SELF` | `mathmod:invisibility_self` |
| `NIGHT_VISION_SELF` | `mathmod:night_vision_self` |
| `WITHER_HOSTILES` | `mathmod:wither_hostiles` |
| `SOUL_BIND_HOSTILES` | `mathmod:soul_bind_hostiles` |
| `VITAL_INFUSION_SELF` | `mathmod:vital_infusion_self` |
| `ALCHEMICAL_MANTLE` | `mathmod:alchemical_mantle` |
| `PARSIMONY_SELF` | `mathmod:parsimony_self` |
| `CONSERVATION_SELF` | `mathmod:conservation_self` |
| `FINITE_DIFFERENCE` | `mathmod:finite_difference` |
| `SIMPSON_INTEGRAL` | `mathmod:simpson_integral` |
| `ABS_NUMBER` | `mathmod:abs_number` |
| `MIN_NUMBER` | `mathmod:min_number` |
| `MAX_NUMBER` | `mathmod:max_number` |
| `POWER_NUMBER` | `mathmod:power_number` |
| `SQRT_NUMBER` | `mathmod:sqrt_number` |
| `LOG_NUMBER` | `mathmod:log_number` |
| `EXP_NUMBER` | `mathmod:exp_number` |
| `ATAN2_NUMBER` | `mathmod:atan2_number` |
| `LERP_NUMBER` | `mathmod:lerp_number` |
| `AT_LEAST_NUMBER` | `mathmod:at_least_number` |
| `SELECT_NUMBER` | `mathmod:select_number` |
