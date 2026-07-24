# Functional Rune Language

## Purpose

MathMod programs are a bounded typed functional language whose final value may
produce a Minecraft world effect. Mathematical vocabulary must remain reusable:
an academic name cannot disguise a fixed spell.

The language separates four concepts:

1. **Literals** are values chosen by the program, such as a number or vector.
2. **Observations** read server state without mutating it.
3. **Witness attributes** are item-provided inscription or cast requirements.
4. **Effects** cross the world-mutation boundary.

Witness attributes are not runtime variables. An entity velocity accessor reads
motion; an attribute named `motion` remains material evidence.

## Execution Semantics

Every rune definition has one purity class:

- `PURE`: deterministic computation over its inputs;
- `OBSERVATION`: contextual or world-backed read;
- `EFFECT`: mutation or construction/execution of a mutation plan.

Before runtime evaluation, `ProgramNormalizer` evaluates supported closed pure
subgraphs and seeds the executor cache with their results. Invalid closed
expressions are left for the normal runtime diagnostic. Observations and effects
are never normalized.

This is constant evaluation, not beta reduction. True beta reduction requires a
serialized scoped function representation and remains a later slice.

## Implemented Observable Slice

The first slice adds:

```text
sense_nearby_entities(Vec3) -> EntityList
entity_velocities(EntityList) -> Vec3List
vector_lengths(Vec3List) -> NumberList
sum_numbers(NumberList) -> Number
mean_number(NumberList) -> Number
max_number(NumberList) -> Number
number_round(Number) -> Number
emit_anchor_redstone(Number, Number) -> Unit
```

`sense_nearby_entities` intentionally permits an empty list. Numeric reductions
return zero for an empty list, allowing sensors to represent absence instead of
failing a cast. Existing targeting queries retain their fail-on-empty behavior.

Entity velocity is Minecraft `deltaMovement`, measured in blocks per tick.
Dimensional display and conversion to blocks per second remain future work.

## Kinetic Transducer

The first observable theorem is an anchor-only snapshot:

```text
E = sense_nearby_entities(anchor, radius=8, limit=8)
V = entity_velocities(E)
S = vector_lengths(V)
P = clamp(round(mean(S) * 40), 0, 15)
emit_anchor_redstone(P, 10 seconds)
```

The Rune Anchor emits direct and comparator-readable power from its own block.
The observed area does not create invisible powered blocks. The signal state is
persisted with an absolute game-time expiry, schedules its own removal, and is
cleared when the inscription is erased or replaced.

This theorem samples once per empty-hand execution. It is not a continuously
reevaluated controller.

## Safety Bounds

- observation radius and entity-list size use the existing execution policy;
- redstone power is clamped to `0..15`;
- signal duration is capped at 30 seconds;
- only a loaded Rune Anchor may execute `emit_anchor_redstone`;
- no recursion, unbounded iteration, or hidden world access is introduced;
- saved graph version and textual constant representation are unchanged.

## Next Slices

### F1: Typed Literals And Inspector

- Implemented F1a: bounded numeric descriptors drive focused Laboratory forms
  for literals, finite differences, and one-panel Simpson quadrature.
- Implemented F1a persistence: parameterized invocations retain typed numeric
  arguments while legacy action ids receive deterministic defaults.
- Implemented F1a cost probe: normalized sampled-calculus results add a
  server-computed, logarithmically bounded precision requirement.
- consolidate remaining rune constants under the typed descriptor system;
- preserve version-1 graphs through an explicit migration adapter;
- show purity, formula, normalized value, and dynamic dependencies in the
  advanced read-only inspector;
- distinguish witness attributes from runtime observations in GUI vocabulary.

### F2: Scoped Functions And Beta Reduction

- Implemented architecture: bounded `Function[A, B]`, De Bruijn parameter
  references, abstraction/application, named `let` presentation, structural
  limits, purity boundary, collection cost estimator, and non-mutating
  migration policy.
- Keep the existing first-order `ProgramGraph` as executable authority; future
  functional source compiles away before inscription and is stored separately.
- Implemented semantic review: the typing and purity judgments, De Bruijn
  counterexamples, alpha equivalence, administrative beta-to-let step, and
  effect-tail rule are frozen in `docs/P4_SEMANTIC_REVIEW.md`.
- Next implementation slice: codecs, type checker, reducer, lowering compiler,
  optional source component, atomic dual-write, and one reusable-function
  theorem.
- Full contract: `docs/P4_FUNCTION_LANGUAGE_CONTRACT.md`.

### F3: Fields And Dimensional Calculus

- add scalar and vector fields over positions;
- add explicit length, time, velocity, acceleration, angle, and signal
  dimensions or validated dimensional metadata;
- implement bounded derivative, gradient, divergence, curl, and integration;
- expose sample count and computed cost before inscription.

### F4: Constructive Regions

- add region union, intersection, difference, translation, and rotation;
- construct implicit regions and solids of revolution from bounded profiles;
- keep mathematical region construction separate from block mutation;
- make fill plans count candidate blocks and required items before execution.

### F5: Sustained Anchors

- add bounded reevaluation intervals and explicit typed memory;
- represent hysteresis, delayed values, and temporal derivatives honestly;
- fund sustained proofs with finite item-backed leases rather than mana;
- stop evaluation when the anchor unloads and never force-load chunks.

## Acceptance

Each slice requires pure law tests, graph validation, executor-policy coverage,
bilingual names, Patchouli teaching, distinct rune icons, dedicated-server
validation, and at least one player-visible theorem that cannot be reduced to a
fixed movement constant.
