# L0-TM-04 Final Gate Acceptance

**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Accepted tasks:** `L0-TM-04`, `L0-TM-04F`, `L0-TM-04F2`,
`L0-TM-04F3`, `L0-TM-04F4`  
**Accepted handoff:** `docs/handoffs/L0_TM_04F4_HANDOFF.md`

## Decision

The read-only functional projection gate is accepted. The repository delta
closes L0-04-R1 through L0-04-R7, F2R1 through F2R6, F3R1 through F3R5 and the
final F4R1 presentation-containment finding.

All five implementation/correction tasks transition to `DONE` with `ACCEPT`.
This decision is based on the implementation, executable evidence and captures
in the repository, not only on the textual handoff.

## Final F4R1 closure

`RuneInspectorScreen` now uses the production `Font.split` width authority for
each authored-source, checked-form and compiled-graph semantic heading. The
same split result determines the reserved heading height in
`functionalLayout`; selectors, heading origins, visible rows and pointer hit
targets therefore share one geometry model.

In compact layout all three selectors remain visible while only the active
panel body is rendered. Pointer panel selection is restricted to the selector
rectangles. Rows remain width-bounded with the production font width function.

`functionalLayoutContained` is an executable runtime oracle over the same
production geometry and font measurements. `UiPreviewHarness` invokes it
before the functional interaction audit and fails with a geometry diagnostic
if a section exceeds the bounded details panel.

The following repository captures were visually inspected:

- `run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png`;
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png`;
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png`.

They show complete selectors and contained functional headings at both required
viewports, including the compact PT-BR case.

## Prior findings retained as closed

- **L0-04-R1-R7:** real menu-opening service/codec GameTests, functional
  keyboard/narration behavior, server-backed preview transport, correct
  graph-only/non-saved behavior, target binding invalidation, closed DTO/codec
  invariants and dedicated-server isolation are present.
- **F2R1-F2R6:** impossible DTO combinations fail closed; compile cardinality
  is measured; same-stack target changes invalidate the binding; stale
  projection preserves known graph presence; interaction evidence is
  executable; EN-US/PT-BR captures are real.
- **F3R1-F3R5:** the shared selector/content geometry is now fully bounded;
  focus and narration audits inspect the real widgets; the live
  `openingSnapshot` path exercises knowledge and final rechecks; encode
  overflow falls back to a decodable unavailable projection; focused,
  functional GameTest, persistence GameTest and global counts are reported
  separately and accurately.
- **F4R1:** complete semantic headings are wrapped and reserved by production
  font measurements, with executable containment and visual evidence.

No earlier closure is waived by this acceptance.

## Reproduced evidence

Focused suite:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --no-daemon `
  --tests com.mathmod.program.ScopedFunctionalProjectionTest `
  --tests com.mathmod.program.ScopedFunctionalProjectionWireCodecTest `
  --tests com.mathmod.screen.RuneProgrammerProjectionTest `
  --tests com.mathmod.client.screen.ProgramInspectorPresentationTest `
  --tests com.mathmod.client.screen.RuneInspectorScreenSourceTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Result: `BUILD SUCCESSFUL`; 25 active methods, 0 failures, 0 errors and 0
skipped:

| Class | Methods |
|---|---:|
| `ScopedFunctionalProjectionTest` | 7 |
| `ScopedFunctionalProjectionWireCodecTest` | 2 |
| `RuneProgrammerProjectionTest` | 1 |
| `ProgramInspectorPresentationTest` | 4 |
| `RuneInspectorScreenSourceTest` | 4 |
| `UiPreviewMatrixTest` | 5 |
| `ServerSideIsolationTest` | 2 |

The reports were read from the configured ASCII-safe build directory
`C:\mathmod-build\MathMod\test-results\test`.

Dedicated GameTest server:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; `All 33 required tests passed`.

Five methods belong to `L0FunctionalProjectionGameTests`:

1. `projectionMenuCodecRoundTripAndBounds`
2. `projectionMalformedFramesFailClosed`
3. `projectionReadCompileMatrixMutatesNothing`
4. `projectionAuthorityRechecksBecomeStale`
5. `projectionMenuBindingInvalidatesAfterTargetChange`

Fourteen methods remain separately owned by
`L0ScopedSourcePersistenceGameTests`. The five functional methods, fourteen
persistence methods and total of 33 are not conflated.

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## Ownership and exclusions

The F4 implementation delta after the F3 re-review is confined to:

```text
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java
docs/handoffs/L0_TM_04F4_HANDOFF.md
run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png
run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png
run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png
```

The worktree contains older accumulated changes without a useful Git baseline;
they are not attributed to F4. The file timestamps after the F3 review, the
handoff inventory and direct source inspection agree on the bounded F4 delta.
Generated runtime logs/world state and the local `.vscode/launch.json` change
are workspace artifacts outside the accepted product delta and grant no
ownership or product authority.

No accepted F4 change touches `ProgramGraph`, `GuidedWorkspaceState`, accepted
Data Component identities or schemas, networking, public APIs,
`ProgramSurfaceMode`, inscription/persistence authority or mutable functional
editing. The accepted projection remains read-only and server-authored through
the existing menu-opening transport.

## Downstream decision

The accepted read-only projection dependency is now closed. No downstream
implementation task becomes `READY` yet:

- `L0-LU-01` also requires a separately frozen first-theorem specification;
  no such specification exists in the repository;
- `L0-TM-05` requires the accepted UI, Luna and theorem gates, so its Luna and
  theorem dependencies remain open;
- A0-6 external sources remain `BACKLOG` behind a separate Sol-owned contract.

Therefore Luna and Terra Medium remain idle for downstream L0 implementation.
The next architectural action is Sol-owned documentation to freeze the
first-theorem specification before `L0-LU-01` can move to `READY`.

```text
L0-TM-04 DONE (ACCEPT)
    -> L0-TM-04F DONE (ACCEPT)
    -> L0-TM-04F2 DONE (ACCEPT)
    -> L0-TM-04F3 DONE (ACCEPT)
    -> L0-TM-04F4 DONE (ACCEPT)

L0-LU-01 BLOCKED on separately frozen first-theorem specification
L0-TM-05 BLOCKED on accepted Luna + theorem gates
```
