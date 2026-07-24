# Advanced Graph Editor

This document defines the boundary between the implemented guided Rune Form composer and a possible direct node-and-edge editor. It is a planning contract, not a claim that direct editing exists.

## Current Decision

Rune Forms remain the default Laboratory interaction. They provide deterministic graph expansion, compact controls, stable action-sequence persistence, simple undo, and a usable keyboard path.

The UI must describe that model honestly:

- A palette row is a Rune Form and may expand into several primitive typed runes.
- Hover and narration preview reused inputs, inferred premises, added runes, and added bindings before activation.
- The graph panel remains the authoritative explanation of what the form produced.

Direct editing should be introduced only as an explicit advanced mode. It must coexist with Rune Forms rather than silently changing their behavior.

## Advanced Mode Requirements

### Nodes And Parameters

- Add, select, move, duplicate, and delete a primitive rune node.
- Edit supported constants through typed controls rather than raw JSON.
- Show rune name, exact output type, cost contribution, and technical id.
- Keep the final output node explicit and restrict executable proofs to one `Unit` output.

### Typed Edges

- Begin an edge from an output socket and complete it only on a compatible named input.
- Preview compatible targets while dragging without relying on color alone.
- Explain type mismatches before dropping the edge.
- Replace or remove an existing input binding deliberately; never discard one because a node moved.
- Make edge direction, input name, and source type available to hover, focus, and narration.

### Validation And Cost

- Revalidate after every graph mutation.
- Preserve complete cycle, missing-input, output, budget, and resource diagnostics.
- Attribute budget and resource requirements to the responsible nodes where possible.
- Keep world mutation, queries, pure transforms, and effect plans visually and semantically distinct.

### History

- Undo and redo are mutation based: add/remove node, move node, edit parameter, add/remove/replace edge, and change output.
- A drag operation becomes one history entry.
- Inscription, resource preparation, and program erasure remain server-confirmed item operations outside graph undo.

### Accessibility

- Every canvas operation needs a non-pointer path.
- Keyboard users can move between nodes, sockets, parameters, and the output designation.
- Narration announces node position in the graph order, socket compatibility, edge source/destination, validation changes, and destructive actions.
- Zoom and pan cannot trap keyboard focus or hide the focused element.

## Persistence And Migration

The P2 boundary and migration policy are now frozen in
`P2_MODE_PERSISTENCE_CONTRACT.md`. `ProgramSurfaceMode` owns mode capabilities,
`GuidedWorkspaceState` owns the versioned Rune Form recipe, and
`ProgramSurface` creates transient read-only inspection projections.

The saved `ProgramGraph` is the execution source of truth. The current `CustomSpellAction` sequence is an editable recipe for graphs produced by Rune Forms.

An advanced edit creates a graph that may no longer be representable as that action sequence. The implementation plan must choose and version one explicit state:

- `GUIDED`: graph plus replayable Rune Form actions.
- `ADVANCED`: graph plus canvas metadata, with no claim that Rune Form replay can reproduce it.

Opening an advanced proof in guided mode must be read-only or require an explicit conversion that starts a new workspace. It must never approximate the graph by guessing an action sequence.

Canvas positions, zoom, and collapsed presentation are editor metadata and must not affect graph equality, execution, or inscription costs. Removed rune ids require the same missing-definition diagnostics as any other saved graph.

For the P2 read-only rollout, canvas metadata is not persisted at all. The
future `ADVANCED` format must use a separate versioned component or document;
it must not add layout fields to `ProgramGraph`.

## Rollout

1. Build a read-only node canvas using existing theorem and Laboratory graphs.
2. Add keyboard-accessible node selection, pan, zoom, and complete inspection.
3. Add parameter editing for constants with no edge mutation.
4. Add typed edge creation/removal and output designation.
5. Add full undo/redo and `GUIDED` / `ADVANCED` persistence.
6. Run migration, dedicated-server, narrator, GUI-scale, JEI/EMI, and bilingual visual tests.

## Acceptance

- Guided proofs round-trip with the same action sequence and graph.
- Advanced proofs round-trip with the same graph and editor metadata.
- No conversion silently loses nodes, edges, constants, names, or resource preparation.
- Pointer and keyboard paths can construct the same minimal executable proof.
- Invalid edge attempts explain expected and actual types.
- The complete supported viewport matrix contains every toolbar, socket tooltip, modal, and diagnostic.
- Server execution remains independent from canvas layout and client-only editor state.

## Open Decisions

- Whether advanced mode is unlocked immediately, through a config option, or through in-game progression.
- Whether node placement uses a free canvas, ordered columns, or an automatically arranged DAG.
- Whether Rune Forms can be inserted as grouped subgraphs in advanced mode.
- Whether named functions arrive before or after direct edge editing.
- How modpack-defined runes expose typed parameter editors without arbitrary client code.
