# A1 Read-only Canvas Gate Review

**Reviewed task:** `A1-TM-READONLY`  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `REJECT` — bounded correction required

## Accepted evidence

- The handoff exists and lists only files within the granted ownership.
- Canvas state is client-only and is not persisted.
- No `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data
  Component, networking, execution, or inscription file changed.
- Layout construction is deterministic for the same graph.
- Zoom values and pan offsets have numeric bounds.
- Keyboard selection invokes focused-node reveal.
- Edge input names are rendered.
- The focused screen suite passes without cache: 119 tests, 0 failures,
  0 errors, 0 skipped.
- The standard build passes.

These facts establish build integrity and boundary isolation, but they do not
satisfy the complete A1 read-only slice.

## Blocking findings

### A1-R1 — zoom does not transform complete canvas geometry

`RuneInspectorScreen` applies zoom only when calculating node positions.
`NODE_WIDTH`, `NODE_HEIGHT`, text, edge endpoints, output marker, hover bounds,
and hitboxes remain fixed pixel sizes.

At minimum zoom, the logical layer gap becomes 64 screen pixels while node
width remains 104 pixels. Adjacent layers can therefore overlap. Pointer
selection continues to use the unscaled 104x34 bounds. This is not a coherent
zoomed canvas and does not evidence pointer/keyboard equivalence across zoom.

Required correction:

- define one logical-to-screen transform for positions and geometry;
- keep render geometry, edge endpoints, socket positions, clipping, and
  hit-testing consistent at every supported zoom;
- add overlap and hit-test vectors at minimum, default, and maximum zoom.

### A1-R2 — pan interaction is not bidirectional

Middle-click performs one fixed `(-16, -16)` pan step. There is no drag state,
pointer delta, keyboard pan path, or positive-direction control. A user can
move away from the origin but cannot deliberately pan back through the same
interaction.

Required correction:

- provide bounded bidirectional pan through pointer drag and/or explicit
  keyboard controls;
- test both extrema and return toward origin;
- keep selection and focused reveal usable after manual pan.

### A1-R3 — named input sockets are absent

The implementation draws the edge input name near the target and a literal
`out` marker on each node. It does not render an input socket marker or expose
socket geometry/state. The task explicitly requires both named input sockets
and edge input labels; an edge label is not the socket itself.

Required correction:

- represent and render target input sockets with their input names;
- anchor each edge to the corresponding socket;
- preserve deterministic socket ordering;
- test multiple named inputs on one node.

### A1-R4 — narration omits required socket and read-only context

`getNarrationMessage()` contains selected node id, output type, purity, and
zoom only. It does not narrate named input/socket context or the current
read-only state. It also does not demonstrate focus position/traversal state.

Required correction:

- narrate read-only state, focused node identity, focus position, output
  socket, named input sockets/bindings, and zoom/pan state as applicable;
- use localized components where player-facing copy is required;
- add behavioral or presentation-model tests, not only source substring
  assertions.

### A1-R5 — viewport clipping and ATM10 evidence are incomplete

The canvas render path has no demonstrated clipping boundary. Panned or zoomed
nodes, edges, and labels may draw into the details panel or outside the canvas.
The 320x240 and 1920x1080 test values exercise only viewport arithmetic; they
do not verify rendered bounds, non-overlap, labels, sockets, focus reveal, or
pointer targets at those viewports.

Required correction:

- constrain drawing and interaction to the canvas viewport;
- add focused viewport-model/render-geometry tests for 320x240 and 1920x1080;
- cover non-overlap, clipping, socket/edge alignment, reveal, and hit-testing.

## Test quality finding

`RuneInspectorScreenSourceTest` proves that selected source strings exist, not
that the required interactions work. It may remain as a boundary smoke test,
but it cannot be the primary evidence for zoom, pan, pointer/keyboard
equivalence, sockets, or narration.

## Bounded correction task

Create `A1-TM-READONLY-F`, owned by Terra Medium.

Exact write ownership:

```text
src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java
src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java
src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java
src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java
docs/handoffs/A1_TM_READONLY_F_HANDOFF.md
```

No new file and no preview-harness edit is authorized without a prior Sol
ownership update.

The correction must not add graph mutation, canvas persistence, `ADVANCED`
mode, Data Components, networking, or changes to `ProgramGraph`,
`GuidedWorkspaceState`, execution, or inscription.

Required output:

```text
docs/handoffs/A1_TM_READONLY_F_HANDOFF.md
```

The handoff must map each A1-R1 through A1-R5 finding to code and executable
test evidence, list exact files, run the focused screen suite and standard
build, and release all granted files.

## Downstream decision

`A1-TM-READONLY` becomes `NEEDS_FIX`. `A1-TM-READONLY-F` is `READY`.

`A0-TM-03` remains `BLOCKED`. A passing build alone does not authorize the
overlapping screen integration.
