# P3 Manuscript Snapshot And Migration Contract

Status: Sol architecture and Terra codec/reload slices implemented on
2026-07-22. Localization fixtures and a clean dedicated-server reload smoke
test remain for the following Luna and verification slices.

## Scope

This slice establishes the Java boundary for lore-only traditions and
manuscripts. It does not replace the existing discovery snapshot. Discovery
definitions remain responsible for validated knowledge grants; manuscript
definitions contain only localized display data, provenance, rarity, and
optional navigation references.

The implemented records are:

- `TraditionDefinition`
- `ManuscriptDefinition`
- `ManuscriptAliasDefinition`
- `ManuscriptDefinitionSource`
- `ManuscriptDiagnostic`
- `ManuscriptSnapshot`
- `ManuscriptReferenceMigration`

No record has an executor, command, program, callback, grant list, inscription
operation, or mutable player/world reference.

## Publication Boundary

`ManuscriptSnapshotBuilder` validates one candidate generation before it can be
published. Local malformed data is expected to be rejected by the future codec;
cross-record failures omit only the affected candidate and produce a
source-aware diagnostic. Global count overflow makes the entire build result
unpublishable.

`ManuscriptSnapshotStore` swaps one immutable snapshot through an atomic
reference. An unpublishable build result leaves the previous generation active.
The future reload listener must finish decoding and registry validation before
calling this boundary; it must never mutate an active snapshot in place.

Current caps:

| Payload | Maximum |
| --- | ---: |
| Traditions | 256 |
| Manuscripts | 1,024 |
| Aliases | 2,048 |
| Pages per manuscript | 8 |
| Alias hops | 16 |
| Translation key | 160 characters |
| Namespaced id | 128 characters |

## Precedence

The frozen layer order is:

`built-in < KubeJS < data pack`

Within a layer, the future loader supplies a non-negative priority. Higher
priority wins. `sourceName` is the final deterministic tie-break across distinct
sources and is retained in diagnostics. Two different records with the same id
and identical source coordinates are ambiguous and omitted rather than selected
by load order.

For data packs, the reload listener must derive priority from Minecraft's
resolved pack order. It must not use filesystem enumeration order. P7 now
defines KubeJS as one startup-only source coordinate,
`KUBEJS/0/kubejs:startup_scripts`, frozen before the first manuscript reload.
The Terra integration consumes that generation during candidate assembly. The
remaining verification is a live dedicated-server reload rather than another
public API change.

## Alias And Persistence Migration

Aliases are validated while building and flattened to canonical current ids in
the published snapshot. Cycles, missing targets, aliases that shadow current
records, and chains beyond 16 hops are omitted with diagnostics. Runtime lookup
therefore performs one bounded map lookup rather than walking an input-defined
chain.

`ManuscriptReferenceMigration` reports one of:

- `CURRENT`: the stored id is canonical.
- `ALIASED`: a canonical record exists and an explicit server interaction may
  persist the replacement id.
- `MISSING`: neither a current record nor a valid alias exists.

Rendering and client synchronization must never rewrite an item. Migration is
pure until a later server-owned inventory interaction or data-fix step chooses
to persist an `ALIASED` result.

Schema version 1 is both current and the oldest supported version. Future codecs
must migrate older supported payloads before constructing the records and reject
unknown future versions. Raising the current version requires a documented,
pure migration and fixtures for every supported prior version.

## Diagnostics

Every diagnostic carries severity, stable code, record kind, record id, source,
and a bounded technical message. The future reload adapter may log these values,
but player-facing text must use localization rather than exposing raw messages.

Expected non-fatal diagnostics include shadowed sources, unknown traditions,
unknown theorems, and invalid aliases. Record-cap overflow is fatal for the
candidate generation because truncation would make publication depend on input
enumeration order.

## Next Implementation Slice

The implemented Terra adapter uses Mojang codecs, the stable theorem registry,
runtime item validation, and the server resource reload lifecycle. It provides
four built-in lore records while preserving this publication boundary and adding
no gameplay authority to lore records.

The Luna slice adds bilingual fixtures, rejected-record matrices, and
Patchouli/preview coverage without changing schema or precedence. These
fixtures now cover the four built-in records and the Weighted Gathering
conjecture spread. A separate
dedicated-server smoke test still needs to prove initial load and `/reload` in
the local NeoForge runtime.
