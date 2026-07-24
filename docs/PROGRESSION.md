# Knowledge, Epiphanies, And Discoveries

Status: P0-P6 implemented as a first progression network. Definitions are
reloadable and KubeJS-extensible, three epiphanies and three discoveries are
playable, and the craftable Field Ledger exposes synchronized progress.

This document is the implementation contract for MathMod progression. It
separates knowledge from inventory resources: items pay for a proof, while
knowledge determines which constructions a player understands well enough to
create or edit.

## 1. Player Contract

- An epiphany is earned through deterministic practice with correlated
  materials across tiers.
- A discovery is learned from a validated field record found in the world.
- Progression gates construction and editing, not execution. A valid talisman
  already inscribed in an older world remains castable.
- Core onboarding must not depend on random loot.
- The server owns progression. Client screens only present synchronized state.
- New schema-3 players see six advanced routes as legible conjectures.
- Players migrated from schema 1 keep the P2/P3 catalog entries, while players
  migrated from schema 2 keep the four constructions newly gated by P6.

## 2. Stable IDs

Theorems and Laboratory Forms now use namespaced ids:

- Theorem: `mathmod:hop`
- Laboratory Form: `mathmod:vector_add_up`

Button ids and enum ordinals remain UI protocol details, never persistent
identity. Existing Laboratory saves containing enum names such as
`VECTOR_ADD_UP` are still decoded; new saves write the stable id. The theorem
index accepts old unqualified lookups while returning the canonical id.

All progression ids use `NamespacedId`, a loader-independent validated value.
This keeps codecs and migrations testable while converting to Minecraft
resource locations only at integration boundaries.

## 3. Player Knowledge

`PlayerKnowledge` is an immutable, schema-versioned attachment with six bounded
sets and one bounded progress map:

| Kind | Meaning |
| --- | --- |
| `material` | Materials the player has meaningfully studied. |
| `correlation` | Observed relations such as motion, force, or continuity. |
| `epiphany` | Completed deterministic insights. |
| `discovery` | Read and accepted world records. |
| `rune` | Rune definitions granted for construction. |
| `theorem` | Presets granted for construction. |
| `study_progress` | Server-owned counters for incomplete deterministic study. |

Each kind is limited to 2,048 ids. Values are deduplicated and sorted before
serialization, making saves and synchronization deterministic.
Study progress is limited to 4,096 entries and each counter is capped at 64.

## 4. Persistence And Synchronization

- Attachment id: `mathmod:player_knowledge`.
- Codec schema: version `3`.
- The attachment is copied on death.
- NeoForge attachment synchronization sends the state only to its owning
  player.
- Joining, data-pack synchronization, respawn, and administrative mutations
  refresh the client copy.
- Mutation always creates a new snapshot, installs it server-side, and then
  synchronizes it.

No GUI or execution code may make an authoritative decision from client state.

## 5. Aliases And Migration

`KnowledgeAliasRegistry` resolves aliases per knowledge kind. It supports
chains and rejects self-aliases, conflicting definitions, and cycles.

Current built-in theorem aliases recognize the old default-namespace
interpretation (`minecraft:hop`) and resolve it to `mathmod:hop`. Stored state
is canonicalized on join/reload and through the administrative migration
command.

Future data-driven aliases must publish one immutable validated snapshot.
Removing an alias is a save migration and must be treated as such.

## 6. Administrative Commands

Commands require permission level 2:

```text
/mathmod knowledge get [player]
/mathmod knowledge grant <player> <kind> <id>
/mathmod knowledge revoke <player> <kind> <id>
/mathmod knowledge clear <player>
/mathmod knowledge migrate <player>
```

Unqualified command ids use the `mathmod` namespace. The `get` command reports
schema version, total entries, and the complete list for each knowledge kind.

Schema-1 players receive the P2/P3 compatibility grants and then the P6
compatibility grants. Schema-2 players receive only the P6 grants, preserving
their deliberate P2/P3 progress. New schema-3 players start with an empty
ledger.

## 7. P2: Harmonic Correspondence

P2 adds pure Java definitions, not arbitrary callbacks:

- `EpiphanyDefinition`
- `KnowledgeRequirement`
- `KnowledgeGrant`
- `MaterialStudyRequirement`

The first epiphany is `mathmod:harmonic_motion`. It is completed after two
successful casts whose immutable cost plans contain `minecraft:feather` and
two successful casts whose plans contain `minecraft:quartz`. The material
definitions span tier I and tier II. Merely holding, moving, or preparing an
item without a successful cast does not advance study.

Completion grants:

- Correlation `mathmod:harmonic_correspondence`.
- Runes `mathmod:number_sin` and `mathmod:number_cos`.
- Theorem `mathmod:harmonic_step`.

Progress and completion feedback are emitted by the server after successful
resource payment. A cast can advance both counters when both materials belong
to its final cost plan.

## 8. P3: The Rotated Horizon

The first discovery is the static built-in record
`mathmod:rotated_horizon`. Its Field Manuscript appears in village
cartographer chests through a NeoForge global loot modifier. P6 preserves the
one-in-three manuscript outcome while dividing its manuscript weight among
three records. Duplicate manuscripts remain readable and are never consumed.

Reading the record for the first time grants:

- Discovery `mathmod:rotated_horizon`.
- Runes `mathmod:cyclic_element` and `mathmod:cyclic_rotate_y`.
- Theorem `mathmod:quarter_turn`.

The Programmer keeps locked theorems and Laboratory Forms visible as
conjectures, explains the required route on hover, and prevents inscription or
editing until the server-owned knowledge is present. Existing inscribed graphs
remain executable regardless of knowledge.

The first slice proves:

- Old inscribed talismans still execute.
- Duplicate reading is idempotent.
- Legacy migration preserves the formerly open catalog.
- Progress counters are bounded and server-owned.
- Missing manuscript ids produce an explicit fallback instead of another
  record.

The manuscript/tradition registry and aliases are implemented. The remaining
P3/P6 verification is a dedicated-server reload smoke test plus the optional
full display catalog synchronization described in `MANUSCRIPTS.md`.

## 9. P4: Declarative Definition Registry

Epiphanies and discovery grants now resolve through one immutable active
snapshot. Sources replace definitions by stable id in this order:

1. Built-in Java fallback.
2. KubeJS startup registration.
3. Data-pack JSON.

Data-pack locations:

```text
data/<namespace>/mathmod/epiphanies/<path>.json
data/<namespace>/mathmod/discoveries/<path>.json
data/<namespace>/mathmod/knowledge_aliases/<path>.json
```

The path becomes the namespaced definition id. Reload parsing validates schema,
field bounds, material tiers, counters, grant kinds, rune ids, theorem ids,
duplicate manuscript ownership, and alias cycles. Each definition or alias
snapshot is published atomically; a rejected replacement leaves its previous
snapshot active.

Limits are 256 data-pack epiphanies, 1,024 data-pack discoveries, 4,096 aliases,
eight studies per epiphany, and sixteen grants per definition.

Every granted rune or theorem automatically acquires the granting epiphany or
discovery as its construction requirement. Pack authors do not maintain a
second GUI lock table.

The callable KubeJS surface is documented in `KUBEJS.md`. It accepts only
declarative fields and never a callback, command, executor, loot mutation, or
direct knowledge grant.

## 10. P5: Field Ledger

`mathmod:field_ledger` is crafted from a writable book and Rune Chalk. Using it
opens a server-authored, bounded view containing:

- Completed and incomplete epiphanies.
- Per-material successful-cast counters.
- Known and unknown discoveries.
- The localized route for incomplete records.
- Rune and theorem grants attached to each record.

The menu payload is bounded independently from the persistent attachment. The
client renders the transmitted view and does not infer authoritative knowledge
from its local registry. The ledger is informational: it cannot grant,
revoke, inscribe, execute, or mutate a proof.

P5 is deliberately separate from manuscript profession milestones M4/M5.
P10 now provides the config-gated Mathemagician, deterministic manuscript
trades, and an optional standalone field house disabled by default. Live
dedicated-server economy/worldgen acceptance remains in `MANUSCRIPTS.md`.

## 11. P6: Convergent Study Network

P6 expands the vertical slice into two complete
`world record -> material practice -> epiphany` chains.

The Bound Measure manuscript grants:

- Discovery `mathmod:bound_measure`.
- Rune `mathmod:soul_bind_entities_plan`.
- Theorem `mathmod:soul_constraint`.

Two successful proofs paid by Vital Salt and two paid by Binding Resin complete
`mathmod:vital_correspondence`, granting the Vital Infusion planning rune and
Theorem.

The Ledger Of Remainders manuscript grants:

- Discovery `mathmod:ledger_of_remainders`.
- Rune `mathmod:parsimony_plan`.
- Theorem `mathmod:axiom_of_parsimony`.

Two successful proofs paid by Nether Quartz and two paid by Axiomatic Ink
complete `mathmod:conserved_remainder`, granting the Conservation planning rune
and Theorem.

One proof may advance every active study whose material appears in its final
immutable cost plan. Completing one epiphany clears only its own progress keys;
useful observations belonging to another epiphany remain.

All three manuscript variants use the same item and Data Component. Village
loot assigns the stable manuscript id explicitly, creative inventory exposes
one stack per active discovery, and item use opens a bounded reader resolved
from the active manuscript record. The reader can navigate to its declared
Patchouli entry and inspect a valid referenced theorem without editing it.

P6 implements manuscript/tradition display records and a dedicated reader.
P10 implements trades, profession, reconciliation, and the optional standalone
field house. Full login/reload display catalog synchronization and live
dedicated-server reload/economy verification remain independent manuscript
milestones.
