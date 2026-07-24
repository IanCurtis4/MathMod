# Advanced Mathematical Runes

This document is the implementation contract for expanding MathMod beyond
elementary arithmetic and fixed spatial helpers. The governing rule is:

> A mathematical rune must compute a reusable typed value. It must not hide a
> unique world effect behind mathematical vocabulary.

World queries and mutations remain separate runes. All numerical operations are
deterministic for the same inputs and fail before resource consumption when
their mathematical preconditions are not satisfied.

## Implemented Slice

### Trigonometry

| Rune | Signature | Meaning | Cost evidence |
| --- | --- | --- | --- |
| `number_sin` | `Number -> Number` | Sine of a radian angle. | Resonance 1 |
| `number_cos` | `Number -> Number` | Cosine of a radian angle. | Resonance 1 |

Degrees are deliberately not implicit. A future angle type may distinguish
units, but serialized numeric angles currently mean radians.

### Numerical Calculus

| Rune | Signature | Meaning | Failure |
| --- | --- | --- | --- |
| `finite_difference` | `(start, end, step) -> Number` | `(end-start)/step` | Zero step |
| `simpson_integral` | `(start, midpoint, end, width) -> Number` | One-panel Simpson quadrature | Zero width |

These runes operate on explicit samples. They are not symbolic differentiation
or integration and do not accept an arbitrary function. Their current purpose
is to make approximation inspectable before the language gains bounded
function and field values.

### Linear Algebra

| Rune | Signature | Meaning | Failure |
| --- | --- | --- | --- |
| `vector_cross` | `(Vector, Vector) -> Vector` | Oriented cross product. | None for finite inputs |
| `vector_project` | `(vector, onto) -> Vector` | Orthogonal projection onto an axis. | Zero-length axis |
| `vector_reflect` | `(vector, normal) -> Vector` | Reflection across the plane with the supplied normal. | Zero-length normal |

These values remain pure until connected to `push_self`,
`push_entities_plan`, another effect planner, or a world query.

### Finite Groups

`CyclicElement` is a distinct serialized rune type. It cannot be connected to
a numeric socket accidentally.

| Rune | Signature | Meaning |
| --- | --- | --- |
| `cyclic_element` | `(order, value) -> CyclicElement` | Normalized residue in `C_order`. |
| `cyclic_compose` | `(C_n, C_n) -> C_n` | Group composition by modular addition. |
| `cyclic_inverse` | `C_n -> C_n` | Additive inverse. |
| `cyclic_rotate_y` | `(C_n, Vector) -> Vector` | Action of the element as a Y-axis rotation. |

Orders are integral and bounded to `2..64`. Composition rejects elements from
different groups. The group action rotates by `2*pi*value/order`.

## Shipped Theorems

| Theorem | Main ideas | Budget | World conclusion |
| --- | --- | ---: | --- |
| Harmonic Step | `sin`, `cos`, local frame | 19 / 24 | Polar local components become movement. |
| Orthogonal Step | cross product, normalization | 19 / 24 | `up x look` becomes a perpendicular step. |
| Quarter Turn | `C4` element and group action | 17 / 24 | One quarter-turn rotates the look vector. |
| Quadrature Leap | sampled sine, Simpson quadrature | 23 / 24 | Half the estimated area becomes vertical movement. |

All four graphs terminate in the existing `push_self` effect. This is
intentional evidence that the mathematics is compositional rather than four
new opaque movement executors.

## Laboratory Surface

Six new Rune Forms are appended after all previously persisted enum ordinals:

- Sine and Cosine.
- Cross With Up.
- Project Onto Look.
- Reflect Across Up.
- Quarter-Turn Vector.

Appending is a save-compatibility requirement. Existing action ordinals must
never be reordered. Numerical calculus now has parameterized finite-difference
and Simpson forms: the Laboratory exposes independent lower/upper bounds and
sample values, clamps them to each rune's declared domain, and adds a precision
surcharge based on the resulting calculation. It still does not claim symbolic
integration of arbitrary expressions.

## Resource Defaults

The item mapping is a configurable default, not universal lore:

| Material | Role | Attributes |
| --- | --- | --- |
| Quartz | Catalyst | Resonance 2, Precision 1 |
| Copper Ingot | Consumed witness | Continuity 2, Mechanical 1 |
| Lapis Lazuli | Catalyst | Symmetry 2, Information 1 |
| Prismarine Crystals | Catalyst | Orientation 2, Spatial 1 |

These specialist materials are tier 2 so they do not replace established tier
1 recommendations for older spells. KubeJS may change selectors, contribution,
consumption, tier, budget, and rune requirements.

## Runtime Boundaries

- Every executor key must be declared in `ProgramExecutionPolicy`.
- Non-finite inputs, zero divisors, zero projection axes, invalid cyclic
  parameters, and mixed cyclic groups fail as math errors.
- Validation and resource planning complete before execution.
- Failed evaluation does not consume witnesses or catalysts.
- Existing push magnitude limits still constrain all four theorem conclusions.
- Cyclic order is bounded even if a graph is authored outside the GUI.

The integrated-client acceptance harness also exercises one complete advanced
proof. `advanced-harmonic-cast` selects and inscribes Harmonic Step through the
real Programmer, casts it through the public talisman route, consumes one
Feather, preserves one Quartz catalyst, and requires a horizontal impulse.
Dedicated-server coverage remains a separate future acceptance gate.

## Visual Assets

The first slice derives 16x16 rune textures from the existing icon vocabulary,
so every graph and catalog row has a valid resource immediately. Dedicated
symbols for sine/cosine, quadrature, cross product, and cyclic action are a
future visual pass; replacing those PNGs must not change rune ids.

## Observable Reduction Foundation

The functional-language foundation now distinguishes pure computation,
world-backed observation, and effects. Supported closed scalar/vector subgraphs
are evaluated before runtime without changing the saved graph codec.

The new `NumberList` pipeline exposes entity velocity, vector magnitudes,
`sum`, `mean`, and `max`. Empty sensor results reduce to zero rather than
misrepresenting absence as an execution failure. Kinetic Transducer composes
these primitives into an anchor signal snapshot:

```text
clamp(round(mean(length(entity_velocities(E))) * 40), 0, 15)
```

This remains instantaneous observation, not temporal differentiation or a
first-class function. The complete contract and next slices are in
`docs/FUNCTIONAL_LANGUAGE.md`.

## Next Slices

### A. Scalar Functions (Implemented Foundation)

- P1 contract is frozen in `docs/P1_SCALAR_CONTRACT.md`: `abs`, `min`, `max`,
  `power`, `sqrt`, `log`, `exp`, `atan2`, interpolation, a minimal threshold
  bridge, explicit domain errors, and shared scalar output bounds.
- The implementation uses declarative descriptors and covers the named pure,
  graph, normalizer, and cost cases in that contract. Live dedicated-server
  acceptance remains part of P12.
- Threshold Beacon is the first non-movement theorem; it turns a bounded anchor
  velocity observation into an anchor-local redstone signal.

### B. Matrices And Affine Maps

- Introduce a typed `Matrix3` or affine-transform value.
- Add identity, multiplication, determinant, transpose, inverse, and
  matrix-vector action.
- Reject singular inverse and non-finite transforms.
- Demonstrate rotation and reflection of a reusable building or region pattern.

P8 has now frozen rotational geometry independently of general affine matrices:
a solid uses an oriented axis and bounded radial-band profile, while its
transient construct body exposes center of mass and scalar inertia about a
selected spin axis. Exact semantics and counterexamples are in
`docs/P8_GEOMETRY_SEMANTIC_REVIEW.md`.

### C. Functions, Fields, And Derivatives (Implemented Foundation)

- Bounded scoped scalar functions, sample planning, one scalar world field,
  and centered spatial gradient now exist with explicit step and cost bounds.
- Gradient Lantern demonstrates a world-derived gradient rather than another
  fixed movement constant.
- P13/P15 add a true vector-field graph type, vector providers, executable
  divergence/curl, and directed effects. They must not infer that functionality
  merely from the existing `CalculusOperator` enum.
- The mathematical definitions, physical readings, sample bounds, quantity
  metadata, and first theorem acceptance gate are frozen in
  `docs/P5_FIELDS_AND_CALCULUS_CONTRACT.md`; implementation must follow that
  contract rather than treating calculus names as opaque effects.

### D. Integration And Distribution

- Add bounded multi-panel quadrature with explicit sample count.
- Normalize weights safely and distribute one fixed effect budget across a
  capped target collection.
- Reject empty support and zero total weight without partial mutation.

Spatial field integration over a constructive region remains coupled to P8's
candidate-plan limits. A region integral may reuse the deterministic lattice,
but it may not reuse a fill plan as authority to sample or mutate the world.
See `docs/P8_CONSTRUCTIVE_REGIONS_CONTRACT.md`.

### E. More Symmetry

- Add dihedral symmetry only when a distinct typed element and safe action are
  justified by building, region, or targeting gameplay.
- Do not introduce a generic arbitrary-group executor.
- Preserve explicit order bounds and group-compatibility checks.

### F. Units And Dimensions

- Decide whether angle, length, time, velocity, and acceleration become distinct
  types or validated metadata.
- Add conversions explicitly; do not silently mix degrees/radians or
  blocks/ticks/seconds.
- Migrate existing numeric graphs only with a codec/version plan.

## Acceptance Matrix For The Next Slice

Every future mathematical subsystem must provide:

1. A reusable typed primitive with documented domain and failure behavior.
2. A pure unit test for the mathematical law or approximation.
3. A graph-validation test proving its input and output types.
4. One executable theorem that composes with an existing effect rune.
5. One honest Laboratory Form, or a written reason why the current guided
   editor cannot represent its premises.
6. Default resource attributes plus documented KubeJS override behavior.
7. EN/PT-BR names, Patchouli instruction, valid rune icons, and compact GUI
   capture coverage.
8. A dedicated-server execution test before the subsystem is treated as
   survival-ready.
