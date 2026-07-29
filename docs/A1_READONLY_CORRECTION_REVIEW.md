# A1 Read-only Canvas Correction Review

**Reviewed task:** `A1-TM-READONLY-F`  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `REJECT` — second bounded correction required

## Findings closed

- A1-R1 is substantially closed: node position, size, text scaling, socket
  geometry, edge endpoints, and hit rectangles now share a zoom transform.
- A1-R2 is closed: middle-button drag supports positive and negative bounded
  deltas and releases drag state.
- The socket portion of A1-R3 is closed: declared input names are deterministic,
  input sockets are rendered, and edges anchor to matching sockets.
- A1-R4 now includes read-only state, node identity, focus position, bindings,
  output type, zoom, and pan values.
- A1-R5 now uses scissoring and rejects pointer selection outside the outer
  canvas.

The focused screen suite passes without cache: 122 tests, 0 failures, 0 errors,
0 skipped. The standard build passes. No forbidden boundary changed.

## Residual blocking findings

### A1-F-R1 — edge input labels regressed

The original implementation drew `edge.inputName()` beside each edge. The
correction uses that value only to choose the target socket and removed the
edge-label draw call.

Named sockets and edge input labels are separate, simultaneous task
requirements. A socket label inside the node does not identify the edge along
its route when several edges converge.

Required correction:

- restore a bounded label for every edge input name;
- derive its position from the same transformed edge/socket geometry;
- keep it inside the scissored content viewport;
- test two converging inputs with distinct labels at minimum/default/maximum
  zoom.

### A1-F-R2 — socket narration is semantically mislabeled

`getNarrationMessage()` passes `socketBindings()` to
`screen.mathmod.rune_inspector.formula`. Narrator output therefore announces
socket bindings as a formula. Zoom and pan are appended as unlabeled numbers.

Required correction:

- add dedicated bilingual presentation keys for input sockets, output socket,
  and viewport state;
- narrate bindings as sockets/inputs, not as formula;
- label zoom and pan values;
- retain read-only state and focus position;
- test the composed components/keys and EN/PT-BR key parity.

The only new authorized keys are:

```text
screen.mathmod.rune_inspector.input_sockets
screen.mathmod.rune_inspector.output_socket
screen.mathmod.rune_inspector.viewport
```

They are presentation only and must be added identically to EN/PT-BR key sets.

### A1-F-R3 — viewport bounds ignore the rendered insets

Rendering starts at `canvasX + 8`, `canvasY + 10` and receives an inner size of
`canvasWidth - 16`, `canvasHeight - 20`. Pan, zoom, reveal, content bounds, and
the tests still use the larger outer `canvasWidth()`/`canvasHeight()`.

A focused node may therefore be considered fully revealed while its rightmost
8 or bottommost 10 pixels remain outside the scissor. The current viewport
tests use origin zero and do not model this inset mismatch.

Required correction:

- define one inner content rectangle;
- use its origin, width, and height consistently for scissor, pan, zoom,
  reveal, render geometry, edge labels, and hit-testing;
- add nonzero-origin/inset tests proving full focused-node and socket visibility
  at minimum and maximum supported viewports.

## Second bounded correction

Create `A1-TM-READONLY-F2`, owned by Terra Medium.

Exact write ownership:

```text
src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java
src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java
src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java
src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java
src/main/resources/assets/mathmod/lang/en_us.json
src/main/resources/assets/mathmod/lang/pt_br.json
docs/handoffs/A1_TM_READONLY_F2_HANDOFF.md
```

Only the three named translation keys may be added or changed in the locale
files. No other file or key is authorized.

Forbidden boundaries remain unchanged: no graph mutation, persistence,
`ADVANCED` mode, preview harness, Data Components, networking, `ProgramGraph`,
`GuidedWorkspaceState`, execution, inscription, or public API changes.

The handoff must map A1-F-R1 through A1-F-R3 to code and executable tests, run
the focused screen suite without relying only on source-substring assertions,
parse both locale files, prove key parity, run the standard build, list exact
files, and release ownership.

## Downstream result

`A1-TM-READONLY-F` is `NEEDS_FIX`. `A1-TM-READONLY-F2` is `READY`.

`A0-TM-03` remains `BLOCKED`.
