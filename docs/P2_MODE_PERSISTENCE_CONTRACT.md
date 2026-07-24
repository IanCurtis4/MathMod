# P2 Mode And Persistence Contract

Status: Sol boundary slice implemented on 2026-07-22.

## Purpose

P2 adds inspection to the existing Rune Programmer without turning a visual
canvas into a second spell language. This contract separates execution data,
editable Rune Form recipes, modes, and transient presentation state before the
GUI work begins.

## Sources Of Truth

There are two persisted artifacts with different authority:

1. `ProgramGraph` is the sole executable proof. Validation, cost planning,
   inscription, and server execution read this graph.
2. `GuidedWorkspaceState` is a versioned recipe of Rune Form invocations. It is
   editable only when complete replay produces a graph exactly equal to the
   persisted `ProgramGraph`.

The workspace never overrides, repairs, or approximates the execution graph.
An unknown or malformed invocation makes the recipe unreplayable. The talisman
may still expose and execute its intact graph, but the Laboratory must remain
read-only until an explicit future conversion creates a new workspace.

## Mode Capabilities

| Mode | Mutate workspace | Inscribe | Persisted role |
| --- | --- | --- | --- |
| `THEOREM` | No | Yes | None; built-in preview |
| `GUIDED` | Yes | Yes | Versioned Rune Form workspace |
| `INSCRIBED` | No | No | Authoritative execution graph |
| `INSPECTOR` | No | No | None; transient projection only |

Buttons and packets must consult these capabilities. A tab name, widget state,
or client request is not permission to mutate an item.

## Migration

`mathmod:program_guided_workspace` is the versioned component introduced by
this slice. During the first migration window, custom saves dual-write the old
`program_name` and `program_custom_actions` fields. Reads prefer the versioned
component and migrate the legacy pair when necessary.

Legacy migration is exact and bounded:

- schema version is currently `1`;
- names use the existing 32-character canonical form;
- at most 128 invocations are accepted;
- each invocation keeps the existing 512-character bound;
- unknown invocation text is preserved by the codec but disables replay;
- no partial list is returned to an editor.

Removing the legacy fields requires a later save-format migration and pack
compatibility review. It is not part of P2.

## Presentation Boundary

`ProgramSurface.inspect()` retains the exact `ProgramGraph` and drops mutable
workspace access. Inspector state is not a Data Component and is never sent as
an inscription or execution payload.

The following remain client/session presentation only:

- selected node and focused socket;
- automatic node coordinates;
- pan, zoom, scroll, and collapsed sections;
- hover, tooltip, and narration state;
- formula line wrapping and localized labels;
- normalized-value display caches.

None of those fields may participate in graph equality, validation, costs,
resource requirements, execution, or server authorization.

## Terra Handoff

The next P2 implementation slice may:

1. map the existing Theorem, Laboratory, and Inscribed tabs to
   `ProgramSurfaceMode`;
2. expose an Inspector action that creates a transient `ProgramSurface`;
3. render a read-only auto-arranged DAG with keyboard node selection;
4. derive purity, formula, normalized values, dependencies, and node costs from
   the authoritative graph and registries;
5. show an explicit unreplayable-workspace diagnostic instead of opening an
   empty or partial Laboratory;
6. recheck mode capability and held-item identity in every server mutation
   handler.

Direct node mutation, canvas persistence, `ADVANCED` save state, and graph
conversion remain later rollout stages from `ADVANCED_EDITOR.md`.

## Acceptance Invariants

- Inspector entry and exit do not change graph equality or object content.
- A guided recipe round-trips with exact invocation ids and numeric arguments.
- Missing Rune Forms never produce partial reconstructed proofs.
- Version, count, and encoded-length limits fail closed.
- Old custom talismans migrate without changing their `ProgramGraph`.
- Dedicated-server execution has no dependency on client or canvas classes.
