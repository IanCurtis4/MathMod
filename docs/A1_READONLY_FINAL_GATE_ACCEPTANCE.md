# A1 Read-only Canvas Final Gate Acceptance

**Task:** `A1-TM-READONLY-F2`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `ACCEPT`

## Residual findings closed

### A1-F-R1 — edge input labels

Accepted.

- Every edge renders `edge.inputName()`.
- Label placement derives from the transformed target socket.
- `boundedLabelRect` keeps the label bounds inside the inner content rectangle.
- Labels scale with the same viewport transform and remain under the content
  scissor.
- Tests cover distinct socket rows and bounded labels at minimum, default, and
  maximum zoom.

### A1-F-R2 — semantic bilingual narration

Accepted.

Only the three authorized keys were added:

```text
screen.mathmod.rune_inspector.input_sockets
screen.mathmod.rune_inspector.output_socket
screen.mathmod.rune_inspector.viewport
```

Socket bindings are no longer passed through the Formula key. Read-only state,
focus position, input sockets, output socket, zoom, and pan have explicit
component labels. EN/PT-BR contain the same format-token counts.

### A1-F-R3 — inset-aware content rectangle

Accepted.

`contentRect()` supplies the inner origin and dimensions used for:

- scissor;
- render origin;
- pointer admission and hit-testing;
- pan and zoom bounds;
- focus reveal;
- content width/height;
- transformed nodes, sockets, edges, and labels.

Nonzero-origin/inset tests prove focused node and socket visibility at the zoom
extrema.

## Verification

Locale validation:

- EN keys: 807;
- PT-BR keys: 807;
- EN-only: 0;
- PT-BR-only: 0;
- both locale files parse as UTF-8 JSON.

Focused suite executed without cache:

```text
GRADLE_USER_HOME=C:\codex-gradle-a0
.\gradlew.bat test --tests 'com.mathmod.client.screen.*' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; 124 tests, 0 failures, 0 errors, 0 skipped.

Standard build:

```text
GRADLE_USER_HOME=C:\codex-gradle-a0
.\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

Existing deprecation warnings are unrelated and non-blocking.

## Boundaries and ownership

No `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data
Component, networking, execution, inscription, preview-harness, canvas
persistence, or public API boundary changed.

All A1/F/F2 screen, test, and locale ownership is released.

The complete parent slice transitions:

```text
A1-TM-READONLY    NEEDS_FIX -> DONE
A1-TM-READONLY-F  NEEDS_FIX -> DONE
A1-TM-READONLY-F2 READY     -> DONE (ACCEPT)
```

Mutable Advanced editing, node/edge mutation, undo/redo, and canvas persistence
remain deferred.
