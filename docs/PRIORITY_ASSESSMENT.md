# MathMod Priority Assessment

Status: reassessed on 2026-07-22 after the P10 house and P11 physics slices.

This document consolidates the nearest actionable steps from the current MathMod
planning documents. It is an ordering document, not a replacement for the
technical contracts in the source documents. When a priority here conflicts
with an implementation contract, the contract remains authoritative and this
assessment should be updated.

## Decision Summary

P0-P11 now form a substantial implemented foundation, but several slices still
carry live acceptance gates. The active queue is:

1. **P12, consolidation:** close dedicated-server, reload, protection, economy,
   narrator, real-modpack, and first-use evidence before calling dangerous
   systems survival-ready.
2. **P13, environmental fields:** add deterministic dimension/biome/height/
   seed-derived correspondence fields, a true vector-field type, and bounded
   observation without introducing mana.
3. **P14, block mutation:** generalize P8 placement into a protected,
   transactional break/remove/replace framework with explicit drop policies.
4. **P15, field dynamics:** compose convergent, divergent, and rotational
   fields into gravity, magnetism, pulses, vortices, and P14-gated destruction.

P13 architecture may proceed beside P12 because it is initially observational.
P14 must not enable survival destruction until P12 closes P8's transaction and
protection evidence. P15-A may ship entity-only effects after P13; P15-B terrain
effects depend on P14.

The detailed cross-document review and contract seeds are in
`docs/P12_P15_EVOLUTION_PLAN.md`.

## Evaluation Method

Each step receives four relative ratings:

| Rating | Meaning |
| --- | --- |
| Benefit | Player value, reuse by later systems, and reduction of future rework. |
| Cost | Implementation, testing, migration, UI, and documentation effort. |
| Risk | Probability of corrupting saves, weakening authority, or creating an unstable API. |
| Dependency leverage | How many later steps become easier or possible after this step. |

Ratings use **Low**, **Medium**, and **High**. Priority is the result of high
benefit and leverage, low or controlled risk, and respect for dependencies. A
large player-facing feature is not automatically earlier than a small contract
that unlocks several systems.

## Model Profiles

Model names below describe the recommended role, not a requirement that every
task use a single model for its entire lifecycle.

| Model | Recommended role in this repository |
| --- | --- |
| **Sol (GPT-5.6)** | Long-horizon architecture, cross-document dependency analysis, persistence/migration design, and difficult multi-module implementation. Use when a wrong decision would force broad rework. |
| **Terra (GPT-5.6)** | Main implementation agent for Java, NeoForge networking, tests, build verification, and focused GUI changes after the contract is decided. |
| **Terra (GPT-5.6), with high reasoning** | Mathematical semantics, typed-language design, balance reasoning, lore/system design, and independent review of proposed contracts. |
| **Luna (GPT-5.6)** | Bounded mechanical work: localization, Patchouli pages, test expansion, asset manifests, small refactors, and deterministic preview wiring. |

The model choice follows the project's current GPT-5.6 operating taxonomy:
Sol handles the most consequential long-horizon work, Terra handles general
implementation and technical design, and Luna handles bounded mechanical work.
Effort should be raised only when the task's dependency or correctness risk
demands it.

## Global Priority Order

### P0. Establish the acceptance baseline

**Steps:** dedicated-server authority tests, packet/reload boundary tests,
first-use UX observation, and correction of stale planning references.

**Progress:** packet text is now bounded at the server handler, custom edits
revalidate the active programmer menu and held talisman before mutation, and
oversized persisted invocation text is rejected before parsing. The calculus
documentation now records the implemented parameterized forms and precision
surcharge. A live dedicated-server/reload run and independent first-use
observation remain required acceptance gates.

**Benefit:** High. These tests protect every later feature and reveal whether
the current onboarding is actually discoverable rather than merely functional.

**Cost:** Low to Medium. The project already has a preview harness and a
dedicated-server isolation test, so this is mostly extending existing seams.

**Risk:** Low. It is diagnostic and protective.

**Model:** Terra with **Medium** effort for implementation. Defer preview-matrix
and capture wiring to Luna after the implementation is accepted; use Terra with
high effort only to interpret ambiguous human-observation results.

**Exit condition:** a dedicated server rejects invalid client-side mutations,
reload/login synchronization remains bounded, and at least one external tester
can complete the first spell flow without verbal rescue.

### P1. Complete the scalar and literal foundation

**Steps:** consolidate typed numeric descriptors, add `abs`, `min`, `max`,
`power`, `sqrt`, `log`, `exp`, `atan2`, interpolation, explicit domain errors,
and output bounds. Add one non-movement theorem driven by a scalar threshold.

**Benefit:** High. These operations are cheap to implement, immediately useful
in spells, and prerequisites for meaningful calculus, fields, thresholds, and
cost scaling.

**Cost:** Medium. The runtime operations are small; the real work is typed
validation, GUI exposure, persistence compatibility, resource defaults,
Patchouli teaching, and theorem acceptance.

**Risk:** Medium. Domain and non-finite behavior must be fixed before exposing
forms, especially for logarithms, roots, powers, and interpolation.

**Model:** Terra with high effort for the mathematical contract and test cases,
followed by Terra with Medium effort for implementation and dedicated-server
verification.

**Progress:** Terra High completed `docs/P1_SCALAR_CONTRACT.md`, freezing
primitive semantics, finite/output bounds, domain failures, descriptor
ownership, magnitude-cost policy, Threshold Beacon, and the named pure through
dedicated-server acceptance cases. Terra Medium implemented the registry,
runes, forms, execution, closed-domain rejection, costs, localization, and
Threshold Beacon. The remaining P1 gate is the real dedicated-server execution;
P2's inspector remains out of scope.

**Exit condition:** each primitive has a domain contract, pure law tests, graph
validation, server executor coverage, EN/PT-BR presentation, and one gameplay
theorem with a visible effect other than fixed movement.

### P2. Build the F1 inspector and read-only graph surface

**Steps:** finish the typed-literal editor, show purity/formula/normalized value
and dynamic dependencies, attribute costs to nodes, and implement the first
read-only node canvas from existing graphs.

**Benefit:** Very High. It makes the current computational model inspectable,
reduces the opacity of complex spells, and provides the safest stepping stone
to direct graph editing.

**Cost:** Medium to High. It touches GUI layout, narration, keyboard focus,
normalization, graph presentation, and cost explanation.

**Risk:** Medium. The execution graph must remain authoritative while canvas
metadata stays presentation-only.

**Model:** Sol for persistence and mode boundaries; Terra for GUI and tests;
Luna for bilingual Patchouli and preview additions.

**Progress:** Sol completed the mode and persistence boundary. The common-side
policy now distinguishes Theorem, Guided, Inscribed, and Inspector
capabilities; guided recipes have versioned bounded persistence with exact
legacy migration; and read-only inspection cannot carry mutable workspace or
canvas state. See `P2_MODE_PERSISTENCE_CONTRACT.md`. Terra still owns the GUI,
derived inspector data, packet enforcement, and viewport/accessibility tests.

**Exit condition:** guided and read-only inspection round-trip without changing
the graph, technical values are distinguishable from player-facing labels, and
the inspector works at compact viewports with keyboard focus.

### P3. Implement manuscript M0b and M1 infrastructure

**Progress (2026-07-22):** Sol snapshot/migration, Terra codec/reload, and Luna
bilingual/preview/rejection fixtures are complete. A clean dedicated-server
reload smoke test remains open. See `P3_MANUSCRIPT_SNAPSHOT_CONTRACT.md`.

**Steps:** tradition/manuscript codecs, validation tests, reload listener,
immutable snapshots, built-in records, aliases, source-aware diagnostics, and
dedicated-server reload validation.

**Benefit:** High. This unlocks the intended discovery layer and gives KubeJS,
Patchouli reading, loot, and later profession work a stable data contract.

**Cost:** Medium. The schema and limits are already specified, but reload
precedence, diagnostics, migration, and tests are broad.

**Risk:** Medium to High. Bad precedence or synchronization can create pack
compatibility and save-migration problems.

**Model:** Sol for snapshot/migration architecture; Terra for codec/reload
implementation; Luna for fixtures, localization, and validation matrices.

**Exit condition:** invalid records are omitted atomically, valid records reload
deterministically, aliases are bounded and tested, and no record can execute,
inscribe, grant, or mutate state by itself.

### P4. Add bounded scoped functions and beta reduction

**Progress (2026-07-22):** Sol language architecture/migration and Terra High
semantic review/counterexamples are complete. The next work is staged Terra
implementation. See `P4_FUNCTION_LANGUAGE_CONTRACT.md` and
`P4_SEMANTIC_REVIEW.md`.

**Steps:** `Function[A,B]`, parameter references, abstraction/application,
lexical serialization, capture-safe beta reduction, named `let`, and bounded
cost for collection operations.

**Benefit:** Very High. This is the central architectural unlock for reusable
math, genuine function-valued fields, derivatives, gradients, distributions,
and substantially richer player-authored spells.

**Cost:** Very High. It affects codecs, validation, normalization, editor
presentation, execution limits, security, and cost planning.

**Risk:** High. Recursion, effects inside functions, variable capture, cyclic
graphs, and unbounded collection work must all be rejected explicitly.

**Model:** Sol for the language architecture and migration plan, Terra with high
effort for semantic review and counterexamples, and Terra for staged
implementation.

**Exit condition:** no recursion or effectful lambda bodies, bounded evaluation,
capture-safe substitution tests, versioned persistence, and one theorem that
uses a reusable function rather than a fixed macro.

### P5. Implement F3 fields and bounded calculus

**Progress (2026-07-22):** Terra High completed the mathematical and physical
contract, and Sol implemented immutable provider snapshots, bounded
`SamplePlan` construction, server adapter boundaries, stable failures, and a
one-cast cache. Terra implemented the concrete living-density provider,
executor/resource integration, and Gradient Lantern. A live dedicated-server
smoke run remains the final acceptance gate; GUI teaching/inspection remains
the Luna follow-up.

**Steps:** scalar/vector fields, dimensional metadata or types, sampled
derivative, gradient, divergence, curl, bounded integration, sample-count cost,
and a world-derived theorem.

**Benefit:** Very High and directly aligned with the project vision. This is the
first step that turns the mod from a collection of mathematical-looking forms
into a flexible algorithmic magic system.

**Cost:** Very High. It requires F2, explicit sampling semantics, world-query
limits, dimensional policy, GUI parameter design, and resource scaling.

**Risk:** High. World sampling can become expensive or misleading if fields,
units, chunk loading, or temporal meaning are vague.

**Model:** Terra with high effort for mathematical definitions and physical
interpretation; Sol for cost/execution architecture; Terra for staged runtime
and theorem work.

**Exit condition:** every operator declares sample bounds and units, server cost
is known before execution, no chunk force-loading occurs, and divergence/curl/
gradient are demonstrated through bounded, inspectable fields.

### P6. Complete manuscript reading and navigation

**Progress (2026-07-22):** Terra Medium implemented the first M2 reader:
opening a field manuscript now sends a bounded, server-resolved display view to
a read-only paginated menu. It covers canonical aliases and the missing-record
fallback without granting, editing, inscribing, or executing anything. The
remaining M2 work is an optional full login/reload display catalog and its
reconnect coverage. Luna completed bilingual navigation and preview-matrix
coverage. Terra High completed the lore-consistency review, replaced the
mutable Programmer theorem route with a local read-only Inspector route, and
added manuscript/discovery/conjecture contract tests; the global catalog and
dedicated-server smoke run remain separate acceptance work.

**Steps:** M2 synchronization/reading screen and M3 Field Manual/theorem links,
with missing-record behavior, compact layouts, narration, and reconnect tests.

**Benefit:** High for the intended adventure layer, but lower architectural
leverage than the codec/reload foundation.

**Cost:** Medium to High. It is a complete client/server UI feature with many
accessibility and fallback states.

**Risk:** Medium. Navigation must never silently inscribe, edit, grant, or
choose a replacement theorem.

**Model:** Terra for server/client flow; Luna for Patchouli, localization, and
preview matrix work; Terra with high effort for lore consistency review.

### P7. KubeJS manuscript display API

**Progress (2026-07-22):** Sol froze the exact builder surface, startup-only
lifecycle, immutable declaration generation, duplicate policy, validation
phases, and `built-in < KubeJS < data pack` assembly contract. Terra implemented
the public builders, alias entry point, frozen staging integration, built-in
pack classification, registry icon validation, and precedence/lifecycle tests.
Luna completed the bilingual Patchouli teaching entry, canonical startup
example, and documentation fixtures. Live dedicated-server reload remains.
See `P7_KUBEJS_MANUSCRIPT_API_CONTRACT.md`.

**Steps:** expose declarative tradition/manuscript registration only after the
Java registry and reload precedence are stable; add exact signatures and pack
fixtures.

**Benefit:** High for modpack integration, medium for immediate vanilla
gameplay.

**Cost:** Medium. The public API, precedence, reload lifecycle, validation, and
documentation must all agree.

**Risk:** High if released before the registry contract is frozen; low if it is
kept declarative and versioned.

**Model:** Sol for API/precedence review, Terra for code, Luna for examples and
docs.

### P8. Constructive regions and fill planning

**Progress (2026-07-22):** Sol froze the pure-region, candidate-plan,
item-counted fill-plan, transient construct-body, and effect boundaries. The
contract defines server-owned caps, exact dynamic payment, stale-plan
revalidation, escrow, rollback, protection adapters, chunk policy, and the
compositional Cavalieri Projectile throughline. Terra High then froze closed
boundary semantics, finite implicit predicates, radial-band solids of
revolution, voxel-center sampling, center of mass, scalar inertia, and
counterexamples. Terra A now implements Boolean/constant-revolution descriptors
and a pure deterministic candidate planner with limit tests. Transactional fill
execution now adds exact block ids, EMPTY_ONLY preflight, escrow, revalidation,
and rollback. Dedicated-server/claim coverage is next before construct motion.
See
`P8_CONSTRUCTIVE_REGIONS_CONTRACT.md` and
`P8_GEOMETRY_SEMANTIC_REVIEW.md`.

**Steps:** Boolean regions, implicit regions, solids of revolution, bounded
sampling, candidate-block counts, item requirements, and separate fill plans.

**Benefit:** Very High gameplay potential, especially for world building and
anchor-area magic.

**Cost:** Very High. It combines geometry, sampling, block mutation, inventory
planning, preview UX, server limits, and rollback/failure behavior.

**Risk:** Very High. Large-volume operations can damage worlds or create severe
performance problems.

**Model:** Sol for safety and transaction design; Terra with high effort for
geometry semantics; Terra for bounded implementation and GameTests; Luna for
bilingual teaching, assets, and deterministic preview fixtures.

### P11. Derived block physics and construct material profiles

**Progress (2026-07-22):** the Sol slice is complete. The authority boundary,
schema-1 profile and policy formats, `built-in < KubeJS < datapack`
precedence, ambiguity rules, atomic snapshot lifecycle, numerical caps, stable
diagnostics, startup-only KubeJS builders, and no-graph-migration compatibility
matrix are frozen in `docs/P11_DERIVED_BLOCK_PHYSICS_CONTRACT.md`. Terra High
then completed the canonical-shape sampled union, mass-weighted aggregate,
compression, legacy-impact, and counterexample review in
`docs/P11_PHYSICS_SEMANTIC_REVIEW.md`. Next is Terra's pure core and immutable
data snapshot implementation. The pure core is now complete in
`com.mathmod.physics`, including sampled volume, precedence, fallback, LRU
snapshot cache, weighted aggregate, tensor, compression, and unit tests. The
NeoForge canonical-state adapter and atomic profile/policy reload publication
are now complete; the remaining Terra work is dedicated-server verification and
capturing the immutable resolved profile for future construct planning. Launch
capture is now complete: each flight retains its resolved material aggregate
and profile snapshot version, while P8 payment and impact behavior remain
unchanged. The only Terra remainder before Luna is broader dedicated-server
reload/flight/collision/unloaded-chunk acceptance coverage. The reusable
fixture pipeline and first P11 `GameTest` are now in place: `runGameTestServer`
passed its canonical-adapter and fallback-profile check on 2026-07-22.
The Luna surface is now also complete: starter profile data, bilingual
Patchouli teaching, preview-matrix coverage, and estimate-labelled resource
panel copy. P11 can proceed to optional hardening or hand its bounded outputs
to a separately contracted P9 effect.

**Dependency:** P8 dedicated-server, claim/protection, and transaction
acceptance must be closed first. P11 then precedes permanent P9 infusions,
physics-scaled effects, terrain impact, and broad material transmutation.

**Steps:** freeze the separation between exact item payment,
`massEquivalent`, and resolved physical mass; define reloadable profile data;
resolve bounded density and `VoxelShape` volume; aggregate mixed-construct
mass, center, and inertia; integrate only server-resolved bounded motion; then
document previews and declarative pack examples.

**Benefit:** Very High. It makes material selection meaningful for constructs,
upgrades P8's equal-mass approximation without changing its economy, gives P5
an honest route to mass/density quantities, and supplies a safe common basis
for later P9 material effects.

**Cost:** High. The work crosses Minecraft shape semantics, datapack reload,
cache invalidation, server authority, existing construct behavior, and
dedicated-server tests.

**Risk:** High but controllable. The main hazards are silently changing saved
spell balance, treating hardness as literal density, expensive shape scans, and
turning material metadata into uncapped damage. The P11 contract preserves P8
economy, starts with `gamma = 0`, caps all derived gameplay schedules, and
forbids terrain damage in this epic.

**Model sequence:** Sol for the profile/reload/migration contract; Terra with
High effort for volume, inertia, and counterexamples; Terra with Medium effort
for the pure core, NeoForge adapter, and dedicated-server verification; Luna
for data examples, bilingual Patchouli, and preview fixtures.

**Contract:** `docs/P11_DERIVED_BLOCK_PHYSICS_CONTRACT.md`.

### P9. Alchemical expansion and metamagic extensions

**Progress (2026-07-22):** Terra High completed the P9 balance and player
policy in `docs/P9_ALCHEMICAL_PLAYER_POLICY.md`. Terra Medium now implements
the shared pre-mutation item escrow, restoration on failure, target firewall,
and bounded cleansing/resistance/absorption presets. It leaves P11 quantities
out of player scaling and defers permanent infusion behind a separate
persistence contract. Luna then added the bilingual Patchouli explanation,
resource-panel preview cases, and defensive rune asset coverage.

**Steps:** beneficial target selection, cleansing/resistance/absorption,
controlled transmutation, permanent infusion policy, additional metamagic
discounts, and effect persistence.

**Benefit:** High player-facing benefit because effects are immediately legible.

**Cost:** Medium to High per effect, High for permanent infusion and persistence.

**Risk:** Medium for bounded temporary effects; High for permanent attributes,
ownership, migration, and automated discounts.

**Model:** Terra with high effort for balance and player policy, Terra for
effect plans/GameTests, Luna for recipes, textures, and Patchouli.

The safe order inside this group is temporary effects and target policies first,
then persistence, then permanent infusion, and only afterward more aggressive
metamagic discounts.

### P10. Manuscript acquisition, profession, trades, and structure

**Progress (2026-07-22):** Sol completed the architecture contract in
`docs/P10_MANUSCRIPT_ACQUISITION_CONTRACT.md`: validated acquisition snapshot,
bounded loot and trade schemas, independent feature flags, surplus economy,
reload reconciliation, profession-without-house invariant, and optional
world-generation boundary. Terra Medium then implemented the pure codecs,
candidate snapshot builder, alias validation, feature configuration, and
weighted selection. Terra High now atomically publishes lore, acquisition, and
server-config snapshots at reload, including safe bootstrap defaults and a
dedicated-server generation GameTest. Terra then added the bounded cartographer
loot modifier, chance policy, canonical item component, and config-generation
refresh. Luna added four built-in acquisition records, bounded trade metadata,
bilingual Patchouli content, and preview coverage; profession integration and
economy tests remain next. Terra now registers the craftable Demonstration
Table, POI, and config-gated Mathemagician, with a deterministic manuscript
catalog and progression-safe novice paper offer; reload reconciliation remains
the next runtime boundary. Terra Medium now reconciles only marked manuscript
offers for loaded, non-trading Mathemagicians: valid offers retain their state,
rejected records are removed, and current deterministic slots are filled.
Live dedicated-server economy coverage remains.

**Steps:** configurable duplicate/loot policy, profession and trades, then an
optional Demonstration Table/village house with independent world-generation
controls.

**Benefit:** High thematic benefit and strong adventure identity.

**Cost:** High to Very High. Each step crosses world generation, economy,
multiplayer, reload, and dedicated-server behavior.

**Risk:** High. Random access must not gate core onboarding, and structures must
not make the profession unusable when generation is disabled.

**Model:** Sol for data/economy/worldgen boundaries, Terra for implementation and
server tests, Luna for loot tables, trade copy, and assets.

### P12. Consolidation and survival readiness

**Progress (2026-07-22):** Terra High completed the cross-system evidence
matrix, release labels, GameTest/dedicated/manual rows, and ambiguous-failure
policy in `docs/P12_SURVIVAL_READINESS_CONTRACT.md`. Terra Medium completed an
automated slice: `gradlew test` and all nine registered GameTests pass, covering
P8 rollback/admission/collision, P9 failed preflight, P10 independent feature
flags, and P11 future snapshot publication. The remaining GT variants and all
dedicated/manual smoke rows remain open.

**Steps:** create one evidence matrix for every open P0-P11 acceptance gate,
then run the missing dedicated-server, reload, reconnect, protection,
profession/economy, worldgen, narrator, ATM10 viewport, and first-use checks.

**Benefit:** Very High. It converts a broad implemented prototype into an
honestly classified baseline and protects every destructive or world-derived
feature that follows.

**Cost:** Medium. Most seams and fixtures exist; the cost is integration,
runtime observation, and repairing defects the evidence reveals.

**Risk:** Low for the work itself and Very High if skipped.

**Model:** Terra High for the matrix and ambiguous failures, Terra Medium for
GameTests/runtime fixes, Luna for final bilingual evidence and previews.

**Exit condition:** every P0-P11 slice is labelled implemented,
survival-ready, or experimental with named evidence. P8 protection/transaction
tests and P0 first-use evidence are no longer implicit TODOs.

### P13. Environmental correspondence field

**Progress (2026-07-22):** Sol completed
`docs/P13_ENVIRONMENTAL_FIELD_CONTRACT.md`. The contract freezes the distinct
attribute-space, scalar-field, and spatial-vector types; schema-one bounds;
atomic publication; private world-secret persistence; reload migration;
declarative KubeJS limits; P5 planning compatibility; and the observational
Dimensional Survey boundary. Terra High completed the mathematical and
adversarial review in `docs/P13_ENVIRONMENTAL_FIELD_SEMANTIC_REVIEW.md`,
including layer algebra, continuity boundaries, exact noise vectors, and the
survey signal formula. Terra Medium completed its runtime work: persistent
world-secret storage, bounded P5 providers, Dimensional Survey, the dedicated
anchor GameTest, declarative datapack reload, aliases, captured execution
generations, typed projection, and player-safe anchor reports. Luna completed
the starter `mathmod:resonance` data, bilingual Patchouli teaching, resonance
glyph, and preview-matrix target; the contract remains experimental.

**Steps:** define a finite attribute space, versioned declarative dimension and
biome contributions, normalized-height curves, salted seed noise, immutable
snapshots, scalar/vector provider types, bounded P5 sampling, and Dimensional
Survey.

**Benefit:** Very High. It gives dimensions and biomes a mathematical identity,
unlocks honest vector calculus, and provides a shared substrate for exploration,
automation, progression, and later effects.

**Cost:** High. It crosses data reload, deterministic noise, types, quantity
metadata, privacy, GUI inspection, and server sampling.

**Risk:** High unless attribute-space vectors are kept separate from physical
directions and ambient values are prevented from becoming disguised mana.

**Model:** Sol architecture/migration and Terra High review complete; Terra
Medium for implementation/GameTests, Luna for data and teaching.

**Exit condition:** loaded-position samples are deterministic, bounded,
server-owned, do not expose the seed, do not force-load chunks, do not pay item
costs, and can drive one non-mutating theorem.

### P14. Transactional block mutation and destruction

**Steps:** define mutation candidates/plans/receipts, permission snapshots,
revalidation, escrow, rollback, disintegration, and later explicit harvest
semantics. Reuse P8 regions and P11 capped bands without widening either
contract silently.

**Benefit:** Very High. It enables mining, excavation, controlled terrain
destruction, and later physical impacts through one auditable world boundary.

**Cost:** Very High. Loot, XP, block entities, fluids, claims, callbacks,
rollback, crash honesty, and mod compatibility make this a server-critical
system.

**Risk:** Very High. Duplication, protected-block bypass, partial commits, and
world damage are direct failure modes.

**Model:** Sol for transaction architecture, Terra High for policy and
counterexamples, Terra Medium for implementation/GameTests, Luna for warnings
and teaching.

**Exit condition:** Euclidean Bore can remove only admitted simple blocks with
known cost and no drops; every failure before commit consumes nothing, handled
mid-commit failures roll back, and protected/unloaded/unsupported candidates
fail closed.

### P15. Field dynamics and directed effects

**Steps:** add scalar potentials, vector-field combinators, executable
divergence/curl/sampling, bounded entity impulse plans, gravity, tagged-item
magnetism, repulsion, and vortices. Add terrain-affecting pulses only through
P14 mutation plans.

**Benefit:** Very High. This is the strongest synthesis of the mod's mathematics,
functional composition, physical interpretation, item economy, and magical
gameplay.

**Cost:** High for entity-only effects and Very High for destructive effects.

**Risk:** High. Mathematical labels can become misleading, while force and
terrain schedules can become uncapped if P5, P11, and P14 boundaries are
bypassed.

**Model:** Sol for cross-system effect architecture, Terra High for vector
calculus/physics/balance, Terra Medium for runtime/GameTests, Luna for theorems,
Patchouli, and previews.

**Exit condition:** Gravitational Well, Ferric Recall, and Divergent Pulse are
ordinary inspectable graphs with known costs; Controlled Nova remains disabled
until the P14 terrain transaction acceptance passes.

## Per-Document Assessment

| Document | Nearest actionable step | Benefit | Cost | Risk | Priority | Recommended model |
| --- | --- | --- | --- | --- | --- | --- |
| `ADVANCED_EDITOR.md` | Add read-only P13 field-layer inspection; reserve destructive editing confirmation for P14 | High | M | M | P13/P14 | Sol + Terra |
| `ADVANCED_MATHEMATICS.md` | Contract vector fields, divergence, curl, and their quantities | Very High | H | H | P13/P15 | Terra High |
| `ALCHEMICAL_EFFECTS.md` | Reuse P15 effect plans; keep permanent infusion separately gated | High | M | H | P15/P9 future | Terra High + Terra |
| `EPICS.md` | Track P12 evidence and P13-P15 exits | High | S | L | P12 | Terra Medium |
| `FUNCTIONAL_LANGUAGE.md` | Specify pure bounded field combinators without effectful closures | High | H | H | P13/P15 | Sol |
| `KUBEJS.md` | Add declarative field coefficients only after the P13 Java contract | High | M | H if premature | P13 | Sol + Terra |
| `LORE.md` | Teach environmental surveys without treating them as energy; preserve Convergence naming | M-H | S | M | P13 | Terra High + Luna |
| `MANUSCRIPTS.md` | Close live P10 economy/worldgen and P3/P7 reload evidence | High | M | M | P12 | Terra Medium |
| `MATHEMATICAL_GAMEPLAY_ROADMAP.md` | Use P12-P15 as the active sequence | High | S | L | P12 | Terra High |
| `METAMAGIC.md` | Preserve item witnesses; defer ambient modifiers until field/cost telemetry exists | M-H | M-H | H | After P13/P15-A | Terra High |
| `P8_CONSTRUCTIVE_REGIONS_CONTRACT.md` | Close fill/protection GameTests before P14 mutation | Very High | M | Very High if skipped | P12 | Terra Medium |
| `P11_DERIVED_BLOCK_PHYSICS_CONTRACT.md` | Close reload/flight hardening; expose only capped P14/P15 inputs | High | M | H | P12 then P14/P15 | Terra Medium |
| `PROGRESSION.md` | Close live authority tests; add P13/P15 discoveries only with playable mechanics | M-H | M | M | P12/P13 | Terra + Luna |
| `SAFETY.md` | Freeze sampling privacy, mutation, drop, and field-force limits | Very High | M | L | P12-P15 | Sol + Terra High |
| `UI_PREVIEWS.md` | Add evidence, field-layer, destruction-warning, and force/falloff matrices by slice | M | S-M | L | P12-P15 | Luna |
| `UX_AUDIT.md` | Run human first-use, narrator, and actual ATM10 GUI-scale tests | Very High | S-M | L | P12 | Terra Medium + Luna |
| `P12_P15_EVOLUTION_PLAN.md` | Freeze P13 while P12 closes runtime evidence | Very High | M then H | H | P12/P13 | Sol + Terra High + Terra + Luna |

## Parallel Work Plan

### Sprint A: P12 evidence

- Run the dedicated-server/reload matrix for P1, P3, P5, P7, P9, P10, and P11.
- Close P8 transaction, unload, collision, and protection tests.
- Run the independent first-use, narrator, and actual ATM10 GUI-scale checks.

### Sprint B: P13 contract, parallel with Sprint A

- Freeze attribute-space versus spatial-vector semantics.
- Freeze data precedence, salted seed privacy, static/dynamic layers, and P5
  sample ownership.
- Define the vector-field graph type and non-mutating first theorem.

### Sprint C: P13 runtime, then P14 contract

- Implement static environmental providers and Dimensional Survey.
- Measure sample and GUI costs before adding dynamic layers.
- Freeze block mutation, disintegration, protection, rollback, and drop policy.

### Sprint D: P15 in two gates

- Implement entity-only convergence, divergence, vortex, and magnetism.
- Enable terrain effects only after P14 acceptance.

Do not combine the P13 snapshot/type migration and the P14 mutation transaction
in one implementation change. Each needs independent rollback and acceptance.

## Explicit Deferrals

The following should not be the next implementation target despite their visual
appeal:

- free-form node-and-edge editing before the read-only canvas and inspector;
- new divergence/curl effects before vector fields and bounded providers;
- villager profession, trades, or a custom house before manuscript reload and
  duplicate policy;
- permanent infusion before ownership, migration, expiry, and allowlists;
- broad KubeJS manuscript callbacks or arbitrary executor registration;
- large fill/solid-of-revolution effects before candidate counts, item plans,
  rollback behavior, and dedicated-server performance tests.
- physical block damage, penetration, or terrain impact before P12 closes P8
  acceptance and P14 freezes a separate protection/rollback design;
- using ambient P13 correspondence values as mana, free items, fixed witnesses,
  catalysts, escrow, or direct permission to mutate the world;
- terrain-affecting P15 effects before P14 acceptance.

## Document Consistency Issues To Resolve

Resolved in the 2026-07-22 reassessment:

- P5 now states that scalar-field/gradient gameplay exists while vector fields,
  divergence, and curl remain deferred.
- P10, `MANUSCRIPTS.md`, `PROGRESSION.md`, and `EPICS.md` now agree that the
  standalone field house is implemented, disabled by default, and not
  road-attached.
- The mathematical roadmap now marks the P1 scalar primitives implemented.

Active boundaries:

- `CalculusOperator` reserves divergence and curl, but that enum is planning
  vocabulary, not evidence of an executable rune. P13/P15 own the missing type,
  formulas, providers, executors, and teaching.
- P8's EMPTY_ONLY fill receipt is not a break/drop transaction. P14 must define
  mutation and loot ownership separately.
- P11 physical quantities remain separate from P8 payment and cannot directly
  become damage, explosion power, or mutation permission.
- Use **The Convergence** for the lore proper noun and lower-case
  `convergent field/effect` for vector-calculus behavior.
- P6's distributed ownership across progression, manuscripts, lore, and safety
  is intentional until it gains a new persistence or protocol boundary.
- `FUNCTIONAL_LANGUAGE.md` still needs a later editorial pass separating
  implemented descriptors/inspector behavior from remaining general literal
  and free-form authoring work; this is documentation debt, not a P13 blocker.

## Definition Of Done For This Assessment

This assessment is complete when every new implementation PR links back to one
priority here, identifies its dependency and model role, updates the owning
technical document, and adds the acceptance tests required by that document.
Priority should be revisited after P12 evidence, the P13 contract, the first P14
mutation GameTests, and P15-A player testing rather than treated as a permanent
schedule.

## Model Reference

Model recommendations use the current project taxonomy supplied by the owner:
GPT-5.6 Sol, Terra, and Luna. Recheck availability and permitted effort settings
before automating a pipeline.
