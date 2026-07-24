# P4 Semantic Review And Counterexamples

Status: Terra High semantic review completed on 2026-07-22. This document
freezes the typing, reduction, sharing, and effect-boundary decisions that the
next implementation slice must obey.

## Review Result

The Sol architecture is retained with one safety refinement: an `EFFECT` rune
may appear only in the terminal tail position of a scoped proof. It cannot be a
lambda body, an application function or argument, a rune-call argument, or a
`let` value. This prevents a reduction strategy from moving, duplicating, or
silently discarding an effect.

P4 functional sources therefore target pure reusable mathematics feeding one
existing first-order effect tail. Existing effect-plan constructors are currently
classified as `EFFECT`; they remain outside first-generation functional sources
until a later slice distinguishes inert plan construction from world mutation.

## Typing Judgment

Let `Gamma` be a stack of types, with `Gamma[i]` resolving De Bruijn index `i`,
and `Sigma(r)` be the active server rune signature.

```text
Gamma |- parameter(i) : Gamma[i]
Gamma |- literal(T, value) : T

Sigma(r) = (a1:T1, ..., an:Tn) -> R
Gamma |- e1 : T1 ... Gamma |- en : Tn
------------------------------------
Gamma |- rune(r, a1=e1, ..., an=en) : R

Gamma, A |- body : B
-------------------
Gamma |- lambda(name, A, body) : Function[A, B]

Gamma |- function : Function[A, B]    Gamma |- argument : A
---------------------------------------------------------
Gamma |- apply(function, argument) : B

Gamma |- value : A    Gamma, A |- body : B
-------------------------------------------
Gamma |- let(name, value, body) : B
```

Input names must match a rune signature exactly once. There are no implicit
conversions, overloaded functions, inferred generic variables, or function
results at the inscribable root. The eventual checker reports stable error
codes for unknown/disabled runes, missing or unexpected inputs, non-function
applications, exact type mismatches, and forbidden function results.

## Purity And Tail Judgment

`PURE` rune calls and literals may appear in lambda bodies. Observations may be
bound or supplied as application arguments outside lambdas, then shared through
administrative lets. Effects are allowed only as the final first-order tail:

```text
tail ::= pure-or-observed-value
       | let(name, value, tail)
       | effect-rune(pure-or-observed-value arguments)
```

No nested effect is valid. In particular, `apply(lambda x. 1, push(...))` and
`let x = push(...) in 1` are rejected even though a call-by-value evaluator
could execute the push once. The language should make the world boundary
visible, not merely technically deterministic.

## Reduction Order

The reviewed beta step is administrative call-by-value:

```text
apply(lambda x. body, argument) -> let(x, argument, body)
```

The body retains its De Bruijn indices unchanged because `let` introduces the
same nearest binder as the lambda. The lowering compiler creates one graph node
for `argument`; every `parameter(0)` reference reaches that node. This is the
required sharing guarantee for observations.

General capture-safe substitution remains specified for transformations that
need it:

```text
substituteTop(s, t) = shift(-1, substitute(0, shift(1, s), t))
```

Shifting crosses a lambda body and a let body with cutoff plus one, but crosses
a let value with the unchanged cutoff. The reference implementation in
`ScopedDeBruijn` is deliberately pure and not yet an inscription reducer.

## Counterexample Matrix

| Counterexample | Incorrect behavior | Frozen outcome |
| --- | --- | --- |
| Rename `x` to `y` | Textual substitution changes a binding | Alpha-equivalent; names are presentation only |
| `lambda x. lambda y. x` applied to an outer variable | Free variable becomes captured by `y` | Shift replacement under the inner binder; result remains free to the outer scope |
| `let x = parameter(0) in parameter(0)` | Treats `x` as visible in its own value | Rejected as a free parameter; direct recursion impossible |
| `apply(lambda x. distance(x, x), player_position())` | Two world reads after substitution | Rewrites to one let-bound observation referenced twice |
| `apply(lambda x. 1, push(...))` | Hidden effect crosses functional boundary | Rejected: `EFFECT_NOT_IN_TAIL` |
| `let x = push(...) in 1` | Effect can be discarded or reordered | Rejected: `EFFECT_NOT_IN_TAIL` |
| `apply(number(1), number(2))` | Arbitrary value treated as callable | `NON_FUNCTION_APPLICATION` |
| `apply(Function[Number, Vec3], Bool)` | Coercion invents a value | `TYPE_MISMATCH` |
| Self application `lambda x. apply(x, x)` | Untyped recursion/divergence | Rejected because finite non-recursive types cannot satisfy `T = Function[T, B]` |
| Function root on a talisman | Runtime closure leaks into graph | `FUNCTION_RESULT_FORBIDDEN` before lowering |

## Reviewed Error Taxonomy

Structural errors: `FREE_PARAMETER`, `AST_LIMIT`, `BINDING_DEPTH_LIMIT`,
`TYPE_DEPTH_LIMIT`, `ARGUMENT_LIMIT`, `APPLICATION_LIMIT`, and `LITERAL_LIMIT`.

Registry and purity errors: `UNKNOWN_RUNE`, `DISABLED_RUNE`,
`IMPURE_LAMBDA_BODY`, and `EFFECT_NOT_IN_TAIL`.

Typing errors: `MISSING_RUNE_INPUT`, `UNEXPECTED_RUNE_INPUT`,
`TYPE_MISMATCH`, `NON_FUNCTION_APPLICATION`, and
`FUNCTION_RESULT_FORBIDDEN`.

The eventual UI maps these codes to localized explanations; raw Java exception
text is never part of the player protocol.

## Implementation Gate

Before the staged Terra implementation adds codecs or a data component, it must
implement these tests against the actual checker/reducer:

1. Every counterexample above rejects or reduces exactly as stated.
2. Lowering an administrative let produces one shared compiled node for an
   observed argument used multiple times.
3. No accepted scoped source contains an effect below the tail.
4. A valid compiled graph remains executable if scoped source is removed,
   malformed, future-versioned, or unavailable on the client.
5. All server-side limits are recomputed from decoded source, never trusted from
   client editor state.
