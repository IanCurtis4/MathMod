# P12-TM-04 Final Gate Acceptance

**Date:** 2026-08-02  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Task:** `P12-TM-04` is `DONE`  
**Next task:** `P12-SOL-03` is `READY`  
**P12-DS:** remains `NEEDS_FIX / BLOCKED`

## 1. Scope reviewed

Sol reviewed `docs/handoffs/P12_TM_04_HANDOFF.md`, the real repository delta,
the raw ignored runtime evidence and the executable test paths. The accepted
delta is limited to the ownership frozen by
`docs/P12_DS_01_GATE_REVIEW.md` and
`docs/P12_TM_04_HARNESS_PREFLIGHT_CLARIFICATION.md`:

```text
src/main/java/com/mathmod/program/CustomSpellWorkspace.java
src/main/java/com/mathmod/screen/RuneProgrammerMenu.java
src/main/java/com/mathmod/program/P12DsProgrammerGameTests.java
src/test/java/com/mathmod/program/CustomSpellWorkspaceTest.java
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
docs/handoffs/P12_TM_04_HANDOFF.md
```

No `ProgramGraph`, Data Component/schema, networking, payload, public API,
formula, localization, `RuneProgrammerScreen` or theorem-presentation
production file changed.

## 2. Finding closure

### DS01-R1 — closed

Explicit `SELF` now appends a new `mathmod:self_player` node through a private
`addExplicitSelf` path and makes it the output. Inferred premises continue to
reuse `ensureSelf`. The preview invariant remains unchanged: every Laboratory
form reports at least one added rune.

The focused tests prove blank preview, repeated explicit preview, two distinct
Self nodes/steps, newest output, undo/replay and same-action previewability for
every current Laboratory action.

### DS01-R2 — closed

Every ordinary Laboratory mutation now requires the held stack to be
component-exactly equal to the target captured when the menu opened, in
addition to the active-menu and item-type checks.

The new GameTest
`ordinaryMutationsRejectComponentDistinctReplacement` reaches the real
`RuneProgrammerMenu` and attempts name, preset, clear, custom save, reset,
undo, ordinary action and parameterized invocation mutations. All reject; the
captured and replacement stack components/resources remain exact; the player
remains connected in the same menu. The accepted no-schema limitation remains:
component-for-component indistinguishable physical copies are outside this
task.

### R6 — closed for the Self-repeat correction

The real client harness selects Laboratory, applies `SELF` twice, verifies the
exact action sequence, hovers the first Self row and remains alive through
render and screenshot capture. Only `laboratory-self-repeat` bypasses the two
unrelated theorem-only preflights; their bodies and all other current call
coverage remain unchanged.

Ignored runtime evidence inspected by Sol:

```text
EN-US archived debug log SHA-256:
825F6C8EFCCC82335FBBF22AC44961543FF5CA3798567CD4ACC08AA6C5F2E4CE
result: Saved screenshot as mathmod-laboratory-self-repeat-preview.png

EN-US archived concise log SHA-256:
2EAB9A004666CD811E29B5772B41B34ECDB073164EC20CC2A037FAA0E6BD04DC

PT-BR latest log SHA-256:
3B0640C522C14AB4A112FE9AFC321709741C6DFE8CEA693F1B44DA797497B5CE
result: Tela capturada como mathmod-laboratory-self-repeat-preview.png

preserved PT-BR PNG SHA-256:
4F310BA0E38FB3D81082ADA1580346828C9EC13C613E463B42EC426D2CF9F373
dimensions: 1024x800
```

The stable filename caused the later PT-BR run to overwrite the EN-US PNG.
This is accepted as an evidence-retention limitation for this no-crash gate:
the localized EN-US completion log, deterministic pre-capture assertions,
preserved PT-BR image and identical source path prove both successful runs.
Future multi-locale evidence should include locale in the output filename.
Raw logs, screenshots and `logs/` remain ignored runtime material and must not
be committed.

The independent Factored Leap statement presentation failure discovered by
the theorem preflight is not waived or accepted here. It remains recorded in
`docs/P12_FACTORED_LEAP_STATEMENT_PRESENTATION_FINDING.md`.

## 3. Independently reproduced verification

Focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --no-daemon `
  --tests com.mathmod.program.CustomSpellWorkspaceTest `
  --tests com.mathmod.client.UiPreviewMatrixTest
```

Result:

```text
CustomSpellWorkspaceTest: 22
UiPreviewMatrixTest: 6
focused total: 28/28; 0 failures; 0 errors; 0 skipped
```

Global ordinary suite:

```powershell
.\gradlew.bat cleanTest test --no-build-cache --no-daemon
```

```text
529/529 tests; 0 failures; 0 errors; 0 skipped; 135 classes
```

Runtime suite:

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

```text
P12-TM-04 named GameTests: 1
ordinaryMutationsRejectComponentDistinctReplacement: PASS
global GameTests: 60/60 required; PASS
```

The previous accepted total was 59; discovery of the new test and the 60/60
completion prove it participated in the executed suite.

Standard build:

```powershell
.\gradlew.bat build --no-daemon
```

```text
BUILD SUCCESSFUL
```

`git diff --check` also passes. Build success is supplementary to the focused,
GameTest and real-client evidence above.

## 4. Gate transition

`P12-TM-04` is `DONE` with `ACCEPT`. This does not release P12-DS and does not
authorize creation of the next immutable JAR. `P12-SOL-03` is now `READY` and
must freeze the Factored Leap statement presentation correction first. After
that contract and its resulting implementation gate are accepted, Sol may
create a new immutable artifact and rerun DS-01 from a clean checkpoint.

DS-02 through DS-05 and DS-07 through DS-09 remain blocked. DS-06 remains the
separate, unpassed `P12-DS-MP BACKLOG` item.
