# P4 Scoped Function Language Contract

Status: Sol architecture/migration and Terra High semantic review implemented
on 2026-07-22. The typed checker, codecs, capture-safe reducer, lowering
compiler, persistence component, editor, and gameplay theorem remain staged
implementation slices.

## Architectural Decision

Functions are first-class in the authoring language but not in the executable
runtime graph. A functional proof is type-checked, bounded, beta-reduced, and
lowered to the existing first-order `ProgramGraph` before inscription. The
server validates both the source and the lowered graph and remains the only
authority allowed to persist the pair.

The existing `mathmod:program` component remains the executable authority.
A future optional scoped-source component preserves the editable functional
expression. Removing or failing to decode that source must never make a valid
compiled graph uncastable.

This boundary deliberately avoids runtime closures, captured Minecraft state,
recursive call stacks, and save migration of every existing talisman.

## Type Grammar

```text
T ::= RuneType | Function[T, T]
```

`RuneType` is one existing first-order type such as `Number`, `Vec3`,
`EntityList`, or `Unit`. Function nesting is limited to four levels. The first
functional slice does not add polymorphism, inference variables, recursive
types, overloads, implicit conversions, tuples, or user-defined nominal types.

An inscribable source must end in a first-order value type. Existing executable
policy still requires the lowered talisman or anchor graph to end in `Unit`.
Standalone function libraries and function-valued item components are deferred.

## Expression Grammar

```text
e ::= literal(T, value)
    | parameter(index)
    | rune(id, named arguments)
    | lambda(name hint, T, body)
    | apply(function, argument)
    | let(name hint, value, body)
```

Parameter identity uses De Bruijn indices. Index zero denotes the nearest
enclosing lambda or let binder; larger indices walk outward. Persisted name
hints exist only for presentation and diagnostics. Renaming a binder cannot
capture a variable or alter the mathematical term.

For `let x = value in body`, the binder is visible only in `body`. It is not in
scope inside `value`, so direct recursive definitions are structurally
impossible. General recursion, fixpoint combinators, and mutually recursive
definitions are not part of P4.

## Typing And Purity

The future type checker resolves rune calls from the active immutable rune
registry and requires exact input/output equality. Applying `Function[A, B]`
to a value other than `A` is an error; successful application produces `B`.
No client-provided output type or purity claim is trusted.

First-generation lambda bodies may call only `PURE` runes. They may not contain
observations, effect-plan constructors currently classified as effects, or
world mutations. A pure function may still be applied to a value produced by a
world observation outside its body. An effect rune may occur only in the final
tail position; it cannot appear as a `let` value, application value, or nested
rune argument.

Effects remain in the first-order tail of the proof. The lowered graph passes
the existing `ProgramExecutionPolicy`, resource planner, and server validator
before inscription. P4 does not create a second execution route.

## Reduction Semantics

Reduction is capture-safe and sharing-preserving. Conceptually:

```text
(lambda x. body) argument  ->  let x = argument in body
```

The compiler must not copy an observation or effectful expression every time a
parameter occurs. It evaluates or lowers the argument once and binds every
reference to that same result node. Pure closed terms may then be normalized.

The reducer operates only after structural validation and type checking. It
uses index shifting and substitution appropriate to De Bruijn terms; textual
replacement and variable-name matching are forbidden. Reduction stops with a
diagnostic before exceeding any source, application, or evaluation-step limit.

`let` may be retained internally as an administrative sharing form even when
player-facing notation presents ordinary named bindings.

## Limits

| Boundary | Maximum |
| --- | ---: |
| Source AST nodes | 256 |
| Lexical binding depth | 16 |
| Function type nesting | 4 |
| Arguments per rune call | 16 |
| Function applications | 64 |
| Literal payload | 160 characters |
| Collection bound | 64 elements |
| Compile/evaluation steps | 4,096 |
| Existing graph budget | 128 |

Higher-order collection operators use:

```text
cost = base cost + declared bound * function body cost
```

The bound is validated before evaluation and is based on the maximum possible
work, not the observed runtime list size. Resulting resource requirements are
recomputed server-side from the lowered graph. Metamagic may modify material
payment under its existing rules but cannot raise language safety limits.

## Validation Pipeline

The server compilation pipeline is ordered and atomic:

1. Decode a bounded versioned source envelope.
2. Validate AST shape, lexical scope, and structural limits.
3. Resolve rune ids, types, purity, and enabled state from the server registry.
4. Reject recursion, impure lambda bodies, and unbounded higher-order work.
5. Perform capture-safe, sharing-preserving beta reduction.
6. Lower the closed first-order term to `ProgramGraph`.
7. Run existing graph, executable, budget, resource, and knowledge validation.
8. Persist scoped source and compiled graph together only if every stage passes.

No partially reduced source or partially lowered graph becomes active.

## Persistence And Migration

`ProgramGraph.CURRENT_VERSION` remains `1` during P4. Functions compile away, so
the executable codec and network payload do not need a format bump merely to
support functional authoring.

The future scoped-source component starts at schema version `1` and is optional.
Read behavior is fixed as follows:

| Persisted state | Execution | Functional editing | Read mutation |
| --- | --- | --- | --- |
| Graph only | Existing graph remains authoritative | Unavailable until explicit import | None |
| Graph + valid current source | Existing graph remains authoritative | Available | None |
| Graph + unknown source version | Existing graph remains authoritative | Disabled with diagnostic | None |
| Graph + malformed source | Existing graph remains authoritative | Disabled with diagnostic | None |
| Source without valid graph | Not executable | Recovery/export only | None |

Reading, rendering, tooltip generation, and client synchronization never rewrite
an item. A legacy graph may be imported explicitly as a flat first-order source,
but MathMod must not invent lambdas or named lets from graph topology. Import
creates a new candidate and requires normal server validation before inscription.

For one migration window, a functional save writes both representations. The
compiled graph remains sufficient for older behavior and for recovery if a
pack removes a rune used only by the editable source. Resource selections,
authored names, and guided-workspace data retain their existing ownership and
are changed only by an explicit successful inscription.

## Implementation Slices

### P4-Sol: Contract And Pure Boundaries (Implemented)

- Generic value/function type expressions.
- Lexically scoped AST with De Bruijn references.
- Structural and purity-boundary validation.
- Bounded collection cost estimator.
- Non-mutating migration/read policy.
- Scope, purity, cost, and migration tests.

### P4-Terra High: Semantic Review

- Implemented: typing judgment, purity/tail judgment, De Bruijn shifting and
  substitution reference operations, alpha-equivalence review, nested-let
  counterexamples, observed-argument sharing, effect-tail rule, and error
  taxonomy.
- Review record: `docs/P4_SEMANTIC_REVIEW.md`.

### P4-Terra: Staged Implementation

- Add codecs and bounded network representation.
- Implement type checking, reducer, and lowering compiler.
- Add optional scoped-source Data Component and atomic dual-write.
- Add server validation and dedicated-server tests.
- Ship one theorem that visibly reuses a function.

### P4-Luna: Teaching And Preview Coverage

- Add bilingual terminology and Patchouli examples.
- Add inspector representation for binders, applications, and reduced form.
- Add deterministic standard/compact/error previews.

## Deferred To P5

Scalar/vector fields, dimensions, derivative, gradient, divergence, curl,
quadrature over arbitrary functions, and world-derived field sampling remain
P5. P4 supplies the bounded function substrate but does not imply those
operators are available.
