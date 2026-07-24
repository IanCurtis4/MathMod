# P12-P15 Evolution Plan And Document Review

Status: architecture assessment prepared on 2026-07-22 after the P10 house and
P11 block-physics slices.

## Purpose

This document reconciles the current planning documents with the implemented
code and defines the next four priorities. It is an ordering and boundary
document. Existing technical contracts remain authoritative for their own
systems until a later contract explicitly extends them.

The requested direction is split into four steps:

1. P12 closes survival-readiness and documentation debt.
2. P13 creates a deterministic environmental correspondence field.
3. P14 generalizes safe block mutation for excavation and destruction.
4. P15 turns scalar/vector fields into convergent, divergent, rotational, and
   eventually terrain-affecting effects.

This order is deliberate. P13 gives field effects honest mathematical inputs;
P14 gives destructive effects an honest world-mutation boundary; P15 may then
compose both without hiding a fixed explosion behind a mathematical name.

## Verified Baseline

The repository already contains useful foundations:

- P5 has immutable field-provider snapshots, bounded sample planning, a
  scalar living-density provider, centered gradient evaluation, and Gradient
  Lantern. `CalculusOperator` reserves derivative, gradient, divergence, curl,
  and integration, but only scalar-field/gradient gameplay is currently
  registered and executable.
- P8 has pure regions, deterministic voxel candidates, item escrow, bounded
  EMPTY_ONLY fill transactions, rollback for admitted simple states, transient
  construct motion, and a protection-service boundary.
- P11 has immutable physical-profile snapshots, sampled block volume, mass,
  center, inertia, compression policy, and launch-time profile capture. It
  explicitly grants no terrain-damage authority.
- P9 has effect plans, target firewalls, and pre-mutation item escrow for
  bounded entity effects.
- P10 has data-driven manuscripts, loot, a Mathemagician profession, trades,
  reconciliation, and an optional standalone field house that is disabled by
  default.

The following are not implemented player surfaces:

- `VectorField` as a graph/rune type;
- vector-field providers and executable divergence or curl runes;
- gravity, magnetism, attraction, implosion, explosion, or field pulse plans;
- block breaking, harvested drops, terrain destruction, or impact craters;
- a dimension/biome/height/seed-derived environmental field.

## Conflict And Gap Register

| ID | Finding | Resolution |
| --- | --- | --- |
| C1 | The original priority summary still described P1-P7 as future work after those slices were implemented. | Preserve their history, but make P12-P15 the active queue in `PRIORITY_ASSESSMENT.md`. |
| C2 | P5's header said its Terra runtime added no runes, while the same contract and code contain `living_density_field` and `field_gradient`. | Correct the header and distinguish planned operator vocabulary from executable runes. |
| C3 | The P5 operator enum includes divergence and curl, but `RuneType`, `BuiltInRunes`, `FieldCalculus`, and `ProgramExecutor` do not expose a vector-field runtime. | P13 adds the missing type/provider substrate; P15 adds effects only after the mathematical operators are executable. |
| C4 | P8 rollback applies to simple EMPTY_ONLY placement. It is not a general break/replace/drop transaction. | P14 owns a new block-mutation contract and may reuse P8 services without pretending the old fill plan already covers destruction. |
| C5 | P11 computes mass and resistance-like metadata but explicitly forbids deriving uncapped damage, explosion power, or terrain permission from them. | P15 may consume P11 values only through capped schedules after P14 authorizes candidate mutations. |
| C6 | P10 documentation alternated between an unimplemented village-pool house and an implemented house. | Record the actual state: rare standalone structure, disabled by default, no road attachment or resident guarantee. |
| C7 | `PROGRESSION.md` and `MANUSCRIPTS.md` still called professions, trades, and the structure wholly future work. | Update them to reflect P10 and leave only live economy/worldgen acceptance open. |
| C8 | The mathematical roadmap still listed P1 scalar primitives as future. | Mark the P1 set implemented and retain matrices, distributions, solvers, and full dimensions as future work. |
| C9 | P6 has no standalone `P6_*.md`. | This is not automatically a missing contract: its authority is intentionally split across `PROGRESSION.md`, `MANUSCRIPTS.md`, `LORE.md`, and `SAFETY.md`. Create a dedicated contract only if P6 gains new persistence or protocol scope. |
| C10 | Lore uses **The Convergence** as a proper name, while field theory uses mathematical convergence. | Use `The Convergence` only for the lore event/tradition concept and lower-case `convergent field/effect` for an operational vector field. |
| C11 | Resource attributes are item correspondences; treating an ambient attribute value as payment would create disguised mana. | P13 ambient channels are observations. They cannot satisfy fixed items, catalysts, escrow, or item counts. Any bounded modifier is a later explicit cost policy. |
| C12 | Many contracts claim implementation but retain live dedicated-server, claim, reconnect, narrator, or first-use gates. | P12 turns these into one evidence matrix instead of silently treating unit tests as survival readiness. |

No broken `docs/*.md` references were found during this review.

## P12. Consolidation And Survival Readiness

### Scope

P12 is a closure epic, not a new gameplay family. It collects the acceptance
gates currently scattered across P0, P1, P3, P5, P6, P7, P8, P9, P10, and P11.

Required evidence:

- real dedicated-server reload/reconnect for parameters, lore, KubeJS
  declarations, acquisition snapshots, and physical profiles;
- stale-menu, spoofed-payload, and server-recomputed cost rejection;
- P8 fill rollback, unloaded-chunk, collision, and protection behavior;
- P9 item escrow and target-firewall behavior;
- P10 live profession progression, offer reconciliation, and disabled-feature
  behavior;
- P11 reload/flight/collision snapshot stability;
- one independent first-use observation and one real ATM10 GUI-scale pass;
- explicit classification of tests as pure unit, GameTest, dedicated smoke,
  manual multiplayer, or optional integration.

P12 does not require every optional claim mod. It does require a tested default
policy and a fail-closed adapter contract. Unsupported integrations must not be
advertised as compatible.

### Delivery And Models

- **Terra High (completed):** reconciled the cross-system acceptance matrix,
  evidence labels, required dedicated/manual scenarios, and ambiguous-failure
  policy in `docs/P12_SURVIVAL_READINESS_CONTRACT.md`.
- **Terra Medium (completed automated slice):** added runtime GameTests for
  P8 rollback/admission/construct collision, P9 failed preflight, P10 feature
  configuration, and P11 future snapshot publication. `gradlew test` and nine
  required GameTests pass. The construct collision test also found and fixed a
  null collision-context crash. Dedicated-server smoke rows remain open.
- **Luna:** update bilingual copy, preview fixtures, and evidence indexes after
  runtime behavior is accepted.

### Exit Condition

Every implemented P0-P11 slice is labelled `implemented`, `survival-ready`, or
`experimental`, with evidence for the label. P13 may begin architecture work in
parallel, but P14 cannot enable destructive survival gameplay before P12 closes
the P8 transaction/protection gates.

## P13. Environmental Correspondence Field

### Mathematical Model

The world does not contain one physical vector at every point. It contains a
finite-dimensional **attribute field** over world positions:

```text
A_d(p) in R^n

A_d(p) = clamp(B_dimension
             + B_biome
             + H(normalizedHeight)
             + N(derivedSeed, dimensionSalt, p / spatialScale))
```

`d` is the dimension, `p` is a loaded world position, and each basis component
is a stable namespaced correspondence channel such as `spatial`, `stability`,
`vitality`, `decay`, or a pack-defined channel. This is a vector in attribute
space, not automatically a physical `Vec3` direction.

Each component `A_i(p)` can be exposed as a scalar field. A spatial vector
field is introduced separately, either directly by a bounded provider or as a
derived gradient of a scalar potential. This distinction prevents a list of
biome affinities from being mislabeled as velocity, force, or direction.

### First Static Slice

P13-A uses only stable inputs:

- dimension id and a dimension declaration;
- biome id/tags at the sampled loaded position;
- normalized build height;
- server-only salted noise derived from the world seed;
- bounded declarative coefficients and curves.

Time, weather, moon phase, nearby entities, redstone, and mutable block
composition are dynamic layers deferred to P13-B. Every dynamic layer must
declare its cache key and cast-time snapshot semantics.

The raw world seed is never sent to the client or exposed through a rune. The
server derives salted field noise and returns only bounded sampled values. A
field sample never force-loads a chunk and never scans a dimension.

### Data And Runtime Boundaries

P13 needs:

- versioned dimension, biome, channel, height-curve, noise, and clamp records;
- `built-in < KubeJS startup < datapack` precedence;
- atomic immutable publication and stable diagnostics;
- a closed first-slice channel cap and coefficient bounds;
- `VECTOR_FIELD` as a distinct typed graph value;
- scalar/vector provider declarations with quantity metadata;
- per-cast sample caching and existing P5 `SamplePlan` ownership;
- an inspector view showing channel, contributing layers, quantity, sample
  count, and whether the displayed value is exact or estimated.

KubeJS remains declarative. It may add bounded coefficients, selectors, and
channels, but no sampler callback, arbitrary noise function, world access, or
effect executor.

Ambient values do not pay for a cast. The first slice is observational. A later
policy may apply a small capped multiplier to an attribute requirement, but it
may never replace fixed witnesses, exact block items, catalysts, or escrow.

### First Theorem

`Dimensional Survey` samples three named channels at an anchor and compares
their local gradient magnitudes. It produces a bounded redstone signal and a
read-only field report. It changes no entities or terrain, making it suitable
for validating data, sampling, cost, and UI semantics before force effects.

### Delivery And Models

- **Sol (completed):** snapshot, data, migration, privacy, world-secret,
  KubeJS, and P5 compatibility rules are frozen in
  `docs/P13_ENVIRONMENTAL_FIELD_CONTRACT.md`.
- **Terra High (completed):** attribute-space semantics, gradient/vector
  distinction, layer composition, continuity boundaries, noise golden vectors,
  quantities, formulas, and counterexamples are frozen in
  `docs/P13_ENVIRONMENTAL_FIELD_SEMANTIC_REVIEW.md`.
- **Terra Medium (completed):** persistent server-only
  secret storage, immutable built-in channels, P5-compatible scalar providers,
  frozen noise vectors, Dimensional Survey, real anchor GameTests, declarative
  datapack publication, aliases, captured execution generations, typed
  projection, and player-safe anchor reports are implemented.
- **Luna (completed):** starter `mathmod:resonance` dimension/biome/height
  data, bilingual Patchouli, a resonance glyph, and preview-matrix targets.

## P14. Transactional Block Mutation And Destruction

### Boundary

P14 generalizes P8 placement into an explicit server-owned mutation plan. It
does not modify `FillPlan` in place and does not call a vanilla explosion as a
shortcut.

The core records should separate:

```text
BlockMutationCandidate(position, expectedState, proposedState, action)
BlockMutationPlan(candidates, permissionSnapshot, lootPolicy, costPlan)
BlockMutationReceipt(changedStates, escrow, generatedDrops, status)
```

Actions begin with `PLACE`, `REMOVE`, and `REPLACE`. Named spell runes produce
plans; only a terminal effect rune may commit them.

### Safety And Economy

Every candidate is bounded, deterministically ordered, loaded, inside world
limits, revalidated immediately before mutation, and approved by the common
protection service. Client-supplied states, hardness, drops, mass, and counts
are ignored.

P14-A begins with simple blocks only and a `DISINTEGRATE` policy that produces
no drops. This proves mutation and rollback without duplicating loot. P14-B may
add `HARVEST` only after it freezes tool context, loot-table evaluation, XP,
output escrow, inventory overflow, and modded-drop behavior. Unbounded world
drops are not the fallback.

Block entities, fluids, multipart blocks, portals, moving blocks, custom break
callbacks, and multi-tick jobs remain rejected until a later annex. Larger or
delayed operations require a persistent write-ahead journal; P8's in-memory
rollback does not claim crash atomicity.

Costs are monotonic in visited candidates, changed blocks, hardness/resistance
bands, requested drop policy, and precision. P11 profiles may inform capped
bands, but never directly become damage or permission.

### First Theorem

`Euclidean Bore` removes a short capped cylinder selected by an ordinary region
graph. Its first survival version disintegrates admitted simple blocks, refuses
protected/unloaded/unsupported positions, and reports the exact candidate and
changed-block costs before commitment.

### Delivery And Models

- **Sol:** transaction, protection, rollback, loot, crash, and migration
  contract.
- **Terra High:** block-policy semantics, cost bands, adversarial examples, and
  mod-compatibility review.
- **Terra Medium:** pure planner, NeoForge commit adapter, escrow/rollback,
  GameTests, and Euclidean Bore.
- **Luna:** warning states, bilingual teaching, block-policy icons, and bounded
  preview fixtures.

## P15. Field Dynamics And Directed Effects

### Reusable Effect Model

P15 maps computed fields to bounded effect plans. It must not introduce opaque
`gravity_spell` or `explosion_spell` runes. The reusable layers are:

```text
Scalar potential or VectorField
-> bounded sample/evaluation plan
-> target or block candidate set
-> falloff and clamp
-> EntityImpulsePlan or BlockMutationPlan
-> terminal execution
```

Recommended primitives:

- `radial_potential(center, strength, falloff) -> ScalarField`;
- `negative_gradient(field) -> VectorField` for convergence;
- `field_scale`, `field_add`, and bounded `field_clamp`;
- `field_divergence` and `field_curl` with P5 centered sampling;
- `sample_vector_field(field, position) -> Vec3`;
- `impulse_entities_from_field(targets, field, cap) -> EffectPlan`;
- a later `mutate_blocks_from_field(region, field, policy)` that delegates to
  P14 rather than mutating terrain itself.

`gravity` is a convergent acceleration/impulse interpretation. `magnetism` is a
filtered convergent interaction and must declare what is magnetic; the first
slice should use item/entity tags rather than inspect arbitrary inventories.
`pulse` is a transient divergent impulse. A destructive blast is a pulse plus
a P14 block-mutation policy, not a separate unbounded explosion executor.

### Delivery Order

P15-A is non-destructive:

- attraction toward an anchor;
- repulsion pulse;
- orbit/vortex from a curl-bearing field;
- tagged-item magnetism;
- no terrain damage and no player targets from autonomous anchors.

P15-B is destructive and depends on P14:

- implosive or explosive candidate selection;
- capped terrain mutation with explicit disintegrate/harvest policy;
- P11 resistance and mass only through bounded schedules;
- no vanilla chain reactions, fire, chunk loading, or uncapped propagation.

Cost is known before execution and grows monotonically with sample count,
target cap, impulse bound, affected block cap, resistance band, and selected
drop policy. Ambient P13 values may shape direction or a capped coefficient,
but do not replace item payment.

### First Theorems

- `Gravitational Well`: a player-cast, bounded non-player pull around a target
  point.
- `Ferric Recall`: attracts tagged item entities without reading inventories.
- `Divergent Pulse`: a harmless radial repulsion demonstrating positive
  divergence.
- `Controlled Nova`: after P14, applies the same field to a bounded
  disintegration plan and makes its terrain cost explicit.

### Delivery And Models

- **Sol:** effect-plan architecture and P5/P9/P13/P14/P11 integration.
- **Terra High:** vector-calculus definitions, physical interpretations,
  falloff laws, caps, and counterexamples.
- **Terra Medium:** vector-field runtime, operators, entity plans, then the
  separately gated destructive adapter and GameTests.
- **Luna:** theorem data, bilingual Patchouli, field glyphs, warnings, and
  deterministic previews.

## Cross-Document Nearest Actions

| Document | Nearest action after this review |
| --- | --- |
| `ADVANCED_EDITOR.md` | Keep advanced mutation editing behind the current read-only inspector; add field-layer inspection during P13. |
| `ADVANCED_MATHEMATICS.md` | Mark P1/P5 slices current; use P13/P15 for vector-field operators and retain matrices/distributions as independent work. |
| `ALCHEMICAL_EFFECTS.md` | Keep permanent infusion deferred; share P15 target/effect plans without granting terrain authority. |
| `EPICS.md` | Track P12-P15 and their exit conditions. |
| `FUNCTIONAL_LANGUAGE.md` | Preserve graph authority and bounded lowering; later allow pure field combinators, never arbitrary closures over world mutation. |
| `KUBEJS.md` | Add only declarative P13 field coefficients and P14 policy selectors after Java contracts freeze. |
| `LORE.md` | Distinguish The Convergence from convergent fields and introduce environmental surveys as observations, not a power source. |
| `MANUSCRIPTS.md` | Close live P10 acquisition tests; seed P13/P15 conjectures only when gameplay status is explicit. |
| `MATHEMATICAL_GAMEPLAY_ROADMAP.md` | Replace its old implementation order with P12-P15 while preserving later matrices, solvers, state, and distributions. |
| `METAMAGIC.md` | Do not let environmental fields bypass witnesses; wait for cast-cost telemetry before ambient discounts. |
| `P0_ACCEPTANCE.md` | Becomes an input to the broader P12 evidence matrix; its human first-use gate remains open. |
| `P1_SCALAR_CONTRACT.md` | Close live dedicated execution; otherwise stable. |
| `P2_MODE_PERSISTENCE_CONTRACT.md` | Reuse inspect/edit boundaries for P13 layers and P14 destructive confirmations. |
| `P3_MANUSCRIPT_SNAPSHOT_CONTRACT.md` | Close live reload; reuse immutable publication patterns in P13. |
| `P4_FUNCTION_LANGUAGE_CONTRACT.md` | Keep field functions pure and bounded; no effectful function bodies. |
| `P4_SEMANTIC_REVIEW.md` | Reuse substitution/purity rules for future field combinators. |
| `P5_FIELDS_AND_CALCULUS_CONTRACT.md` | P13 extends providers/types; P15 implements vector operators/effects in a new contract instead of silently widening P5. |
| `P7_KUBEJS_MANUSCRIPT_API_CONTRACT.md` | Close live reload; do not conflate manuscript API with field/effect APIs. |
| `P8_CONSTRUCTIVE_REGIONS_CONTRACT.md` | Close transaction/claim GameTests; P14 generalizes mutation separately. |
| `P8_GEOMETRY_SEMANTIC_REVIEW.md` | Reuse candidate lattice and region laws for excavation/blast regions. |
| `P9_ALCHEMICAL_PLAYER_POLICY.md` | Reuse target firewall and escrow; permanent effects stay separately gated. |
| `P10_MANUSCRIPT_ACQUISITION_CONTRACT.md` | Close live economy/worldgen tests and document standalone-house behavior accurately. |
| `P11_DERIVED_BLOCK_PHYSICS_CONTRACT.md` | Close optional hardening; expose only capped inputs to P14/P15. |
| `P11_PHYSICS_SEMANTIC_REVIEW.md` | Supply bounded mass/resistance counterexamples for P15, not direct damage formulas. |
| `PRIORITY_ASSESSMENT.md` | Make P12-P15 the active queue and retain P0-P11 as historical progress. |
| `PROGRESSION.md` | Reflect P10 acquisition and later attach discoveries to P13/P15 without blocking old talismans. |
| `SAFETY.md` | Add P13 sampling/privacy, P14 mutation, and P15 impulse/destruction limits as each contract freezes. |
| `UI_PREVIEWS.md` | Add P13 layer breakdown, P14 destructive confirmation, and P15 vector/falloff cases in Luna slices. |
| `UX_AUDIT.md` | Run the outstanding real ATM10/first-use audit; define non-color destructive warnings before P14 ships. |

## Decisions Deferred To The Contracts

The planning pass deliberately does not freeze exact radii, block caps,
attribute channel counts, noise functions, hardness coefficients, drop modes,
or impulse constants. Those values need Terra High semantic/balance review and
measured server tests. This document freezes dependencies and ownership so the
implementation can choose numbers without crossing safety boundaries.
