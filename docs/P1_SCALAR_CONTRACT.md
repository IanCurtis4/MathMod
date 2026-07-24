# P1 Scalar Contract

This document freezes the P1 mathematical contract. It is intentionally more
specific than a feature list: Terra Medium must implement these semantics and
test cases without changing the saved-graph version or inventing alternate
edge-case behavior.

## Implementation Status

The scalar registry, built-in runes, Laboratory forms, server executor,
closed-expression validation, normalization, magnitude costs, bilingual names,
and Threshold Beacon are implemented. The remaining acceptance gate is a real
dedicated-server execution of Threshold Beacon with below/at/above-threshold
entity motion; that observation must not be substituted with an integrated
client preview.

## Scope And Authority

P1 adds pure scalar primitives and the smallest Boolean bridge needed for a
threshold theorem. It does not add functions, fields, symbolic algebra,
unbounded iteration, unit conversion, or client-authoritative evaluation.

Every scalar input and result is a finite IEEE-754 `double`. The common scalar
boundary is:

```text
EPSILON = 1e-8
MAX_ABSOLUTE_SCALAR = 1,048,576
```

Inputs may equal either numeric boundary. A successful result must be finite
and have absolute value at most `MAX_ABSOLUTE_SCALAR`; otherwise evaluation
fails before witness consumption. This boundary applies to the new primitives
and is also the target boundary for existing closed scalar normalization.

Domain errors are deterministic server-visible math errors, not clamping.
Literal editing may clamp to its descriptor range, but an already persisted or
pack-authored graph is validated and executed against the domain directly.

## Declarative Descriptor Rule

Terra Medium must consolidate P1 operations behind one common scalar
descriptor. A descriptor owns the stable rune id, executor key, typed ports,
literal-form defaults, domain predicate, result-bound rule, budget cost,
attribute requirements, and localized domain-error key. `ProgramExecutor`,
`ProgramNormalizer`, the Laboratory form, and the resource estimator must use
that common contract rather than independent `switch` semantics.

New `CustomSpellAction` constants are appended, never inserted or reordered:

```text
ABS_NUMBER
MIN_NUMBER
MAX_NUMBER
POWER_NUMBER
SQRT_NUMBER
LOG_NUMBER
EXP_NUMBER
ATAN2_NUMBER
LERP_NUMBER
AT_LEAST_NUMBER
SELECT_NUMBER
```

The canonical rune ids are `mathmod:number_abs`, `number_min`, `number_max`,
`number_power`, `number_sqrt`, `number_log`, `number_exp`, `number_atan2`,
`number_lerp`, `number_at_least`, and `number_select`.

## Primitive Semantics

| Rune | Signature | Result | Domain error | Base budget / attributes |
| --- | --- | --- | --- | --- |
| `number_abs` | `Number -> Number` | `abs(x)` | none beyond finite/result bound | 1 / none |
| `number_min` | `(Number, Number) -> Number` | `min(a, b)` | none beyond finite/result bound | 1 / none |
| `number_max` | `(Number, Number) -> Number` | `max(a, b)` | none beyond finite/result bound | 1 / none |
| `number_power` | `(base, exponent) -> Number` | `base^exponent` | zero base with negative exponent; negative base with non-integral exponent; result bound | 2 / Precision 1 |
| `number_sqrt` | `Number -> Number` | principal square root | `x < 0` | 2 / Precision 1 |
| `number_log` | `(value, base) -> Number` | `ln(value) / ln(base)` | `value <= 0`; `base <= 0`; `abs(base - 1) < EPSILON` | 2 / Precision 1 |
| `number_exp` | `Number -> Number` | `e^x` | result bound | 2 / Precision 1 |
| `number_atan2` | `(y, x) -> Number` | `atan2(y, x)` in radians | both inputs have magnitude below `EPSILON` | 2 / Precision 1 |
| `number_lerp` | `(a, b, t) -> Number` | `a + (b-a)t` | `t < 0` or `t > 1`; result bound | 2 / Precision 1 |
| `number_at_least` | `(value, threshold) -> Bool` | `value >= threshold` | none beyond finite inputs | 1 / Information 1 |
| `number_select` | `(Bool, when_true, when_false) -> Number` | selected scalar value | either scalar input is non-finite/out of bound | 1 / Precision 1 |

For `number_power`, an exponent is integral exactly when
`abs(exponent - rint(exponent)) < EPSILON`. `0^0` is defined as `1`; it is the
only zero-base boundary that succeeds without a positive exponent. `number_lerp`
is deliberately interpolation, not extrapolation. Pack authors who need an
unbounded affine combination must compose existing arithmetic explicitly.

`number_select` is strict: both scalar inputs are evaluated and domain-checked
before it chooses the result. Lazy branches and short-circuit effects require
the later Bool-flow/function contract; P1 must not imply that they already
exist.

## Validation, Normalization, And Runtime

Structural validation checks the signatures above. A separate scalar-domain
pass evaluates closed pure subgraphs during inscription validation:

- A closed invalid expression is rejected with its localized domain error.
- A graph with observation-derived inputs remains valid structurally; the same
  domain check happens on the server at cast time.
- `ProgramNormalizer` and `ProgramExecutor` must agree exactly for every
  successful closed P1 operation and every rejection.
- A math failure cannot consume witnesses, catalysts, trigger an effect, or
  partially update an anchor signal.

The existing logarithmic `precision` surcharge remains a result-magnitude
mechanism, not a replacement for each rune's base requirement. It applies once
per normalized P1 non-linear result (`power`, `sqrt`, `log`, `exp`, `atan2`,
or `lerp`), using the existing `ceil(log2(abs(result)))` rule capped at 8.
`abs`, `min`, `max`, comparisons, and selection do not add a surcharge.

## Threshold Theorem

P1's required non-movement gameplay proof is **Threshold Beacon**, available
only to a Rune Anchor. It is a one-shot observation, not a sustained controller:

```text
E = sense_nearby_entities(anchor, radius = 8, limit = 8)
V = entity_velocities(E)
S = vector_lengths(V)
M = mean_number(S)
B = number_at_least(M, 0.25)
P = number_select(B, 15, 0)
emit_anchor_redstone(P, 200 ticks)
```

The theorem has a visible non-movement result: the anchor emits power 15 for
ten seconds when the average observed speed is at least 0.25 blocks/tick,
otherwise power 0. It reuses the existing loaded-anchor, radius, entity-limit,
and duration caps. Its result is an anchor-local signal; it must not place or
force-load redstone blocks.

The initial budget target is 20 or less. It must require `Information 1` and
`Precision 1` in addition to the existing signal requirements; final material
selectors remain KubeJS-configurable.

## Required Test Cases

Terra Medium should add these tests as executable JUnit cases. The names are
part of the acceptance contract so failures can be read as mathematical
regressions rather than generic spell failures.

| Test | Fixture / expected result |
| --- | --- |
| `scalarPrimitivesObeyReferenceValues` | `abs(-3)=3`, `min(4,-2)=-2`, `max(4,-2)=4`, `power(2,10)=1024`, `sqrt(9)=3`, `log(8,2)=3`, `exp(0)=1`, `atan2(1,0)=pi/2`, `lerp(2,10,0.25)=4`. |
| `powerAcceptsIntegerNegativeBasesAndRejectsUndefinedRealPowers` | `power(-2,3)=-8`, `power(-2,0.5)` fails, `power(0,-1)` fails, `power(0,0)=1`. |
| `scalarDomainsRejectBoundaryViolations` | Reject `sqrt(-EPSILON)`, `log(0,2)`, `log(2,1)`, `log(2,1+EPSILON/2)`, `atan2(0,0)`, `lerp(0,1,-EPSILON)`, and `lerp(0,1,1+EPSILON)`. |
| `scalarBoundaryAcceptsFiniteEndpoints` | Accept `sqrt(0)`, `log(1,2)=0`, `lerp(0,1,0)`, `lerp(0,1,1)`, and `number_at_least(2,2)=true`. |
| `scalarOperationsRejectNonFiniteAndOversizedResults` | Reject NaN and infinities at every public scalar boundary; reject `exp(20)` and a power result whose magnitude exceeds `MAX_ABSOLUTE_SCALAR`. |
| `scalarSelectIsStrictAndStructurallyTyped` | `select(false, invalidPower, 7)` fails with the invalid-power error; a non-Number branch remains a graph validation error. |
| `scalarDescriptorMatchesRuneAndExecutorContracts` | Every descriptor has one registered rune, supported executor key, matching port signature, localized error keys, and appended Laboratory action. |
| `closedInvalidScalarGraphIsRejectedBeforeInscription` | A literal `sqrt(-1)` proof fails validation and does not write an inscription. |
| `normalizerAndExecutorAgreeForClosedScalarGraphs` | Each successful reference vector yields the same value; each domain vector yields the same error class. |
| `scalarPrecisionSurchargeIsBoundedAndSelective` | Verify surcharge thresholds at results `1`, `2`, `4`, `256`, and `MAX_ABSOLUTE_SCALAR`; verify no surcharge for `abs`, `min`, `max`, comparison, or select. |
| `thresholdBeaconGraphIsTypedAndWithinBudget` | The theorem validates to `Unit`, contains its scalar threshold chain, uses only supported executor keys, and meets the budget/attribute contract. |
| `thresholdBeaconSetsOnlyAnchorLocalSignal` | Server execution with average velocity below 0.25 produces 0; at or above 0.25 produces 15 for 200 ticks, consumes exactly the planned witnesses, and changes no other block. |
| `thresholdBeaconMathFailureConsumesNothing` | A malformed dynamic scalar result produces a localized server error with no witness/catalyst consumption and no signal update. |

The last two cases are the dedicated-server verification gate for the next
Terra Medium pass. They must run with a separate client/server process or an
equivalent dedicated-server harness, not merely an integrated-client preview.

## Deferred Deliberately

- `number_less_than`, equality with tolerance, Boolean combinators, and
  branching effects are postponed until a separate Bool-flow contract exists.
- Extrapolation, arbitrary-base logarithm UI presets beyond the descriptor,
  symbolic simplification, matrices, functions, gradients, divergence, curl,
  and multi-panel integration remain later slices.
- The read-only normalized-value inspector belongs to P2. P1 exposes enough
  descriptor metadata for it but does not add an inspector screen.
