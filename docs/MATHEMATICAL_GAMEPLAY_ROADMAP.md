# Mathematical Gameplay Roadmap

This document assumes the foundation, typed rune graph, GUI, programmed talismans, world anchors, KubeJS integration, movement/raycast MVP, and initial safety work are already substantially complete.

## 1. Design Goal

The spell system should behave as a small typed functional language whose programs can affect the Minecraft world.

The central separation is:

1. **Pure computation** defines values, functions, geometry, conditions, and transformations.
2. **World queries** read blocks, entities, positions, inventories, time, and other server state.
3. **Effect planning** converts computed values into a proposed world change.
4. **Resource validation** calculates and verifies the required items.
5. **Effect execution** applies the result atomically on the server.

Mathematical nodes should not be disguised fixed spells. A useful primitive should support several unrelated programs.

## 2. Normal Gameplay Applications

| Gameplay area | Mathematical tools | Example programs |
|---|---|---|
| Movement | vectors, normalization, raycasts, interpolation, collision predicates | blink to the last safe point, controlled dash, soft landing, orbit around an anchor |
| Exploration | coordinate frames, distance functions, intersections, weighted averages | relative waypoint compass, triangulation from several anchors, estimate the center of detected signals |
| Mining | regions, block predicates, sampling, path cost, gradients | mine a tunnel with a computed shape, count matching blocks in a volume, choose the lowest-cost path through terrain |
| Building | matrices, transformations, regions, functions over positions | mirror or rotate a placement pattern, repeat a module along a path, fill only points satisfying a geometric predicate |
| Combat | derivatives, prediction, fields, falloff functions, filters | lead a moving target, radial knockback with configurable falloff, select the nearest hostile entity satisfying conditions |
| Farming | entity/block filters, map, reduce, distributions, timers | select mature crops, distribute an action across farmland, prioritize the driest or least-grown area |
| Logistics | counting, rates, derivatives, integrals, comparisons | measure item throughput, detect when production rate falls, accumulate transported items over time |
| Redstone and automation | Boolean functions, state, timers, feedback, thresholds | proportional controller, delayed trigger, hysteresis switch, anchor that reacts to changing measurements |
| Support | distance weighting, interpolation, distributions, target filters | divide an effect among allies, stronger effect near the center, exclude full-health or protected targets |
| Area control | scalar fields, vector fields, divergence, curl | pull toward a point, rotate entities around an axis, push away from dangerous blocks |

## 3. Core Types

The language should gradually support the following types:

```text
Bool
Integer
Real
Vector3
Position
Direction
Duration
Entity
LivingEntity
Player
BlockState
ItemStack
List[T]
Option[T]
Result[T, Error]
Region
Path
Field[T]
Function[A, B]
EffectPlan
ResourceRequirement
```

Important composite types:

- `Region`: a spatial predicate, conceptually `Position -> Bool`.
- `Path`: a parameterized sequence of positions.
- `Field[T]`: a value associated with each position, conceptually `Position -> T`.
- `EffectPlan`: a description of a world mutation that has not yet been executed.
- `ResourceRequirement`: the computed material cost of an effect plan.

## 4. Mathematical Capabilities

### 4.1 Arithmetic and scalar functions

Support arithmetic, comparisons, powers, roots, logarithms, trigonometric functions, clamping, interpolation, minimum, maximum, and configurable approximation.

Gameplay uses include distance falloff, trajectory formulas, thresholds, randomized distributions, and scaling costs by effect magnitude.

Implemented foundation slice:

- Scalar runes: `number_add`, `number_subtract`, `number_multiply`, `number_divide`, and `number_clamp`.
- Invalid scalar operations such as division by zero report a safe runtime math error.

Implemented advanced scalar slice:

- Added radian `number_sin` and `number_cos` primitives.
- Resonance is an abstract resource attribute and quartz is only its configurable default catalyst.
- P1 now implements power, square root, logarithm, exponential, `atan2`,
  comparisons, min/max/absolute value, and interpolation with explicit domains
  and bounded outputs. General angle units and broader inverse trigonometry
  remain future work.

### 4.2 Vectors and geometry

Support vector construction, length, normalization, dot product, cross product, projection, reflection, distance, angles, lines, planes, and intersections.

Gameplay uses include movement, targeting, orientation, safe teleportation, projectile direction, and spatial selection.

Implemented foundation slice:

- Vector runes: `vector_add`, `vector_subtract`, `vector_normalize`, `vector_length`, `vector_dot`, and `vector_distance`.
- The Custom programmer exposes simple actions backed by these pure math runes.

Implemented advanced vector slice:

- Added oriented cross product, projection onto a non-zero axis, and reflection across a supplied normal.
- The Laboratory exposes honest guided forms for cross-with-up, project-onto-look, and reflect-across-up.
- Matrix values, general rotations, plane/line intersections, and affine maps remain separate future slices.

### 4.3 Matrices and coordinate systems

Support matrix-vector multiplication, translation, rotation, scaling, reflection, determinant, inverse when valid, and local coordinate frames.

Gameplay uses include transforming building patterns, defining effects relative to an anchor or entity, rotating fields, and converting local spell geometry into world coordinates.

Implemented foundation slice:

- Added the typed `Frame` value, `player_frame`, and `transform_local_vector`.
- Player frames expose right/up/forward axes aligned to horizontal yaw, allowing pitch-independent local movement.
- Right Angle, Planar Dash, and Oblique Leap demonstrate matrix-style local-to-world transformation through ordinary graph composition.

### 4.4 Collections and higher-order functions

Support typed lists and bounded versions of `map`, `filter`, `fold`, `sort`, `take`, `nearest`, `farthest`, `minimumBy`, and `maximumBy`.

Gameplay uses include selecting targets, processing nearby blocks, distributing effects, finding extrema, and aggregating sensor results.

All collection operations must have strict server-configurable limits.

### 4.5 Regions and spatial predicates

A region should be constructible from predicates and combinators rather than only fixed shapes.

Core operations:

```text
sphere(center, radius)
box(min, max)
cylinder(origin, axis, radius, height)
implicitRegion(position -> Real)
union(regionA, regionB)
intersection(regionA, regionB)
difference(regionA, regionB)
contains(region, position)
sample(region, resolution)
```

This supports mining volumes, building masks, trigger zones, target areas, and collision-safe movement.

### 4.6 Numerical calculus

Calculus should operate on bounded, explicitly sampled functions rather than pretending arbitrary expressions can always be solved symbolically.

Initial operations:

```text
derivative(Real -> Real, point, step)
gradient(Position -> Real, point, step)
integrate(Real -> Real, interval, samples)
integrateField(Field[Real], region, samples)
```

Gameplay uses:

- estimate entity velocity from sampled positions;
- predict interception;
- find the direction of greatest increase in a sensed value;
- calculate total density, heat, charge, growth, or another pack-defined field in a region;
- distribute an effect according to a normalized function.

Later operations may include divergence, curl, Jacobians, Hessians, and numerical root finding.

Implemented bounded foundation slice:

- `finite_difference(start, end, step)` evaluates one explicit secant slope and rejects zero step.
- `simpson_integral(start, midpoint, end, width)` evaluates one Simpson panel and rejects zero width.
- Quadrature Leap demonstrates three explicit samples of sine; the implementation does not claim arbitrary symbolic calculus.
- The observable foundation reads entity `deltaMovement`, maps vectors to
  magnitudes, and reduces bounded number lists with sum, mean, or maximum.
- Kinetic Transducer demonstrates a world-derived scalar by converting mean
  nearby speed into a ten-second `0..15` Rune Anchor signal snapshot.
- Closed pure scalar/vector subgraphs are normalized before execution;
  observations and effects remain dynamic.
- Function values, true beta reduction, multi-panel sampling, world-derived
  fields, gradients, and sample-count resource scaling remain future work.

### 4.7 Numerical solvers and optimization

Provide bounded algorithms rather than unrestricted recursion:

- bisection;
- Newton iteration with an iteration limit;
- fixed-point iteration;
- finite-difference approximation;
- local minimum/maximum search;
- path-cost minimization over a bounded search space.

Every solver exposes tolerance, iteration limit, and estimated execution cost.

## 5. Material and Cost Model

The engine should avoid hard-coding one item per mathematical concept. KubeJS or data packs should map items and tags to resource properties.

Example resource properties:

```text
energy
mass
heat
spatial
stability
precision
memory
information
biological
mechanical
```

A spell may require a combination of these properties. Pack authors decide which items provide them.

Suggested cost components:

```text
base rune cost
+ number of world queries
+ number of sampled positions
+ collection size
+ region volume or surface estimate
+ requested precision
+ effect magnitude (implemented for sampled-calculus result precision; broader effect scaling remains future work)
+ effect duration
+ persistent state cost
```

Pure arithmetic should be inexpensive. Reading or changing the world should dominate the cost.

Catalysts may provide capacity, precision, memory, or permitted operation classes without being consumed. Consumables pay for each execution.

Implemented foundation slice:

- Rune definitions can require abstract resource attributes in addition to fixed item selectors.
- Registered materials provide attributes, optional budget bonus, tier, and consumed/catalyst behavior.
- The talisman has a resource menu that shows plural consumed items, catalysts, required/provided/missing attributes, and effective budget.
- KubeJS can register materials, add material attributes, mark materials as consumed or catalytic, and add/clear rune attribute requirements.
- Default material presets cover vanilla items plus common modpack tags such as tin, bronze, steel, osmium, arcane essence/source, and allthemodium.

## 6. Units and Dimensional Validation

Optional dimensional types can prevent invalid programs and improve balance:

```text
Length
Time
Mass
Velocity
Acceleration
Force
Energy
Temperature
```

Examples:

```text
Length / Time -> Velocity
Mass * Velocity -> Momentum
Force * Length -> Energy
```

This does not need to model real physics exactly. Its purpose is to make spell formulas readable, constrain accidental misuse, and connect effect strength to material cost.

## 7. Precision and Approximation

Operations involving sampling, integration, derivatives, fields, or solvers should expose:

```text
sampleCount
stepSize
tolerance
maximumIterations
```

Higher accuracy increases computational and material cost. The GUI should display the estimated cost before saving or casting.

Programs should be deterministic for the same server state, parameters, and random seed.

## 8. Errors and Failure Modes

Compile-time validation should cover:

- type mismatch;
- missing inputs;
- cycles where not permitted;
- invalid resource duplication;
- unsupported operation combinations;
- known budget overflow.

Runtime validation should cover:

- division by zero;
- invalid square roots or logarithms;
- singular matrices;
- failed collision checks;
- unloaded or protected chunks;
- empty target sets;
- solver non-convergence;
- sample and iteration limits;
- insufficient materials.

Programs should use `Option` or `Result` values where failure is expected. A failed cast should normally avoid consuming materials unless the effect was partially committed.

## 9. Recommended Post-MVP Epics

### Epic 11: Collections and Target Queries

- Add bounded typed lists of entities, blocks, and positions.
- Add filtering, mapping, reduction, nearest/farthest selection, and deterministic ordering.
- Acceptance: a spell can select nearby entities by predicate and apply a computed effect plan to a capped subset.

Implemented slice:

- Added `entity_list` and `effect_plan` rune types.
- Added `block_pos_list` and `vec3_list` for block and position collections.
- Added capped nearby entity and nearby block queries, entity filtering, entity/block position mapping, entity/block counting, average position reduction, nearest-target ordering, and farthest-target ordering.
- `Lift` selects nearby living entities, filters non-player targets, keeps the nearest capped subset, builds a push effect plan, and executes it server-side.

### Epic 12: Regions and Spatial Predicates

- Add reusable region values, primitive shapes, Boolean region operations, containment tests, and bounded sampling.
- Acceptance: the same region can be used for entity selection, block queries, visualization, and effect planning.

Implemented foundation slice:

- Added `region` as a typed runtime value.
- Added primitive region runes: `sphere_region`, `box_region`, `region_contains`, and `sample_region`.
- Added collection filters: `filter_entities_in_region` and `filter_blocks_in_region`.
- The Custom programmer can now assemble region-filtered target spells and region sampling chains.

P8 architecture slice:

- `docs/P8_CONSTRUCTIVE_REGIONS_CONTRACT.md` separates pure geometry,
  deterministic candidate enumeration, item-counted fill plans, transient
  construct bodies, and explicit effects.
- The planned Cavalieri Projectile composes a bounded radial profile, solid of
  revolution, selected block material, mass-conserving compression, angular
  motion, and launch. It must remain an ordinary graph, not a unique opaque
  executor.

### Epic 13: Coordinate Frames and Matrix Transforms

- Add local frames and affine transformations.
- Acceptance: a player can define a pattern once and rotate, reflect, scale, or anchor it relative to the player or a world anchor.

Implemented foundation slice:

- Added player-relative horizontal frames and local-to-world vector transformation.
- Added three executable movement theorems and matching Laboratory actions.
- Generic affine matrices, anchor-relative frames, rotations, reflections, and pattern transforms remain in the next slice.

### Epic 14: Fields and Numerical Derivatives

- Add scalar/vector fields, finite differences, derivative, gradient, and sampled entity motion.
- Acceptance: a spell can estimate target velocity and follow the gradient of a bounded world-derived field.

The P5 contract narrows this first implementation to bounded, loaded-chunk-safe
field samples and an anchor-local Gradient Lantern. See
`docs/P5_FIELDS_AND_CALCULUS_CONTRACT.md`.

### Epic 15: Numerical Integration and Distributions

- Add bounded one-dimensional integration, region sampling, normalized weight functions, and effect distribution.
- Acceptance: a spell can divide a fixed total effect among targets according to distance or another computed weight.

### Epic 16: Bounded Solvers and Prediction

- Add root finding, fixed-point iteration, interpolation, and interception helpers built from generic solver primitives.
- Acceptance: a spell can calculate a bounded projectile interception solution and report non-convergence safely.

### Epic 17: Stateful Programs and Time

- Add explicit anchor state, timers, previous-value storage, rolling averages, and rate-of-change measurements.
- Acceptance: an anchor can measure item throughput or another signal over time without unrestricted loops.

### Epic 18: Resource Properties and Effect Planning

- Separate effect planning from execution and map item tags to configurable resource properties.
- Acceptance: the same program can report its exact resource requirement before an atomic server-side cast.

### Epic 19: Profiling, Simplification, and Visualization

- Display query count, sample count, target cap, estimated cost, and expensive subgraphs.
- Add constant folding, common-subexpression reuse, and safe memoization for pure nodes.
- Acceptance: players can compare two equivalent programs and understand why one is cheaper.

### Epic 20: Gameplay Presets and Documentation

- Ship examples for movement, mining, building, farming, combat control, logistics measurement, and world anchors.
- Document each preset as a composition of reusable primitives rather than a unique built-in spell.
- Acceptance: each major mathematical subsystem has at least one survival-relevant example and one editable tutorial program.

Implemented beta slice:

- Replaced the fixed five-button preset row with a data-driven catalog of 27 named theorems.
- Movement examples now include Hop, Dash, Vector Leap, Recoil, Blink, three player-relative frame transformations, and four advanced demonstrations spanning trigonometry, vector algebra, cyclic symmetry, and numerical quadrature.
- Sensing examples include ray marking plus ore and living-entity centroids built from bounded collections and mean reduction.
- Control examples include nearest, hostile-only, look-directed, and farthest-priority entity push plans.
- Alchemy examples include healing, Speed, Invisibility, Night Vision, Wither, Soul Constraint, temporary attribute infusion, and compound effect plans.
- The programmer displays each theorem's formula, category, icon, typed output, validation state, resource plan, and full hover description.

Implemented alchemical effect-planning slice:

- Seven temporary transformations now produce typed `effect_plan` values and require explicit execution.
- Eight editable theorems demonstrate classic status effects, bounded hostile control, temporary registered attribute modifiers, and plan composition.
- Seven craftable reagents provide configurable correspondence attributes, with explicit consumed and catalyst roles.
- Runtime clamps and non-player hostile targeting keep the default execution surface bounded.

## 10. Implementation Priority

The original post-MVP order below is retained as design history:

1. Collections and target queries.
2. Regions and spatial predicates.
3. Coordinate frames and matrices.
4. Fields and finite differences.
5. Integration and distributions.
6. Bounded solvers.
7. Stateful anchors and time-series operations.
8. Resource properties, profiling, and optimization.

This order creates useful survival gameplay at every stage while keeping advanced calculus dependent on stable spatial, collection, and cost systems.

Most of its foundations are now implemented through P11. The active order is
defined in `docs/PRIORITY_ASSESSMENT.md` and
`docs/P12_P15_EVOLUTION_PLAN.md`:

1. P12 consolidation and survival-readiness evidence.
2. P13 environmental correspondence fields.
3. P14 transactional block mutation and destruction.
4. P15 convergent, divergent, rotational, and destructive field dynamics.

Matrices, distributions, bounded solvers, and stateful time-series anchors
remain valid independent future branches after these dependencies are measured.

## 11. Narrative Continuity

The Convergence lore frame treats each mathematical subsystem as a recurring discovery rather than a school-specific fixed spell. When a new subsystem becomes playable, add a concise Patchouli field note and, where useful, promote an existing manuscript conjecture into an editable theorem or tutorial.

The canonical terminology and traditions live in `docs/LORE.md`; the manuscript data, item, acquisition, and villager implementation contract lives in `docs/MANUSCRIPTS.md`. Pack-defined materials remain local evidence conventions and must not create hard crossover canon with another mod.

A conjecture is promoted only after four questions have concrete answers:

1. Which generic typed runes express the idea without embedding one opaque spell?
2. Which theorem or Laboratory form teaches the smallest useful construction?
3. Which witnesses, catalysts, limits, and server-side failure modes make execution honest?
4. Which manuscript and Patchouli text describe the discovery without claiming that unrelated traditions share one author or chronology?

Promotion updates the theorem/rune implementation and tests first, then Patchouli and the manuscript reference in the same slice. A lore-only manuscript must remain explicitly conjectural when the required mechanics are absent or disabled by a pack.

For alternate demonstrations, promotion also requires an inspectable difference. The two routes must vary in typed decomposition, coordinate frame, query strategy, or resource preparation, and the documented premises must explain when their results are comparable. A second title pointing to the same opaque graph is not an alternate mathematical tradition.
