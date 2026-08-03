# P12-TM-05 Gate Review

**Date:** 2026-08-03  
**Reviewer:** Sol  
**Reviewed handoff:** `docs/handoffs/P12_TM_05_HANDOFF.md`  
**Decision:** `NEEDS_FIX`  
**Correction task:** `P12-TM-05F` is `READY`

> Superseded on 2026-08-03: R1-R3 are closed and `P12-TM-05F` is accepted in
> `docs/P12_TM_05F_FINAL_GATE_ACCEPTANCE.md`.

## 1. Result

The implementation is close, and its core three-line presentation works in the
retained client captures. It is not accepted because two frozen requirements
are contradicted by the repository delta. A green focused suite, global suite
and build do not close those contradictions.

`P12-DS` remains blocked. No new immutable P12-DS artifact may be generated
from this delta.

## 2. Evidence accepted from the delivery

- All five required images exist with distinct vector/locale filenames.
- FS-01 and FS-02 visibly render the complete frozen Factored Leap statement in
  three lines with the graph below it.
- FS-03 preserves a positive graph viewport at 640x480, GUI scale 2.
- FS-05 preserves the two-line Right Angle graph origin reported by the
  handoff.
- Production rendering, scroll range, scrollbar and graph hit-testing call the
  same `graphViewportY()` authority.
- `laboratory-self-repeat` no longer has its temporary statement-preflight
  bypass.
- No formula, localization, theorem identity, networking, persistence, Data
  Component, schema or public API delta was found in the task-owned change.

## 3. Blocking findings

### P12-TM-05-R1 — one-line statement geometry is not preserved

The contract requires one- and two-line statements to retain the legacy
two-line header geometry. Before this task, `TheoremStatementWidget` used the
fixed `THEOREM_STATEMENT_HEIGHT`, equal to two line heights. The delivered
`updateTheoremStatementGeometry()` instead sets:

```java
theoremStatement.setHeight(LINE_HEIGHT * theoremStatementLineCount());
```

Consequently, any built-in statement that fits on one line gets an 11-pixel
hitbox instead of the legacy 22-pixel hitbox. The graph origin happens to remain
unchanged, but the focusable, narrated and tooltip-bearing statement widget no
longer preserves its accepted geometry. FS-05 exercises only a two-line
statement, so it cannot close this regression.

Required correction: the effective statement line count used for widget height
must have a legacy minimum of two and a hard maximum of three. Add a real
one-line preset regression vector/oracle that proves the legacy height and graph
origin remain unchanged.

### P12-TM-05-R2 — catalog exception is not bound to the exact formula

The contract allows the catalog exception only for the exact pair:

```text
id: mathmod:factored_leap
formula: push(halve(look)+halve(up))
```

The delivered preflight filters only on the id:

```java
.filter(preset -> !preset.id().equals("mathmod:factored_leap"))
```

Any future or accidental catalog string under that id would therefore bypass
the preflight. `UiPreviewMatrixTest` asserts this id-only source fragment and
thus locks in the broader exception instead of proving the exact pair.

Required correction: express the exception as an exact id-and-formula predicate
and add a focused behavioral test proving that the exact pair is accepted while
the same id with a different formula and the same formula with a different id
are both rejected as exceptions. This must not modify `ProgramPresets` or either
frozen formula.

### P12-TM-05-R3 — focused coverage does not catch R1

`FactoredLeapStatementPresentationTest` is chiefly a set of source-text
assertions. It asserts the three-line expression and the two-line FS-05 path,
but it contains no one-line height oracle. That is why the focused suite passes
while R1 remains present.

Required correction: add a deterministic oracle for the effective header
height/origin of one-, two- and three-line cases. It may remain package-private
and client-only; no public API is authorized. Retain FS-01 through FS-05 and add
the smallest runtime evidence necessary for the one-line case.

## 4. Reproduced commands

With `GRADLE_USER_HOME=C:\codex-gradle-a0`:

```text
focused five-class suite: 38 tests, 0 failures, 0 errors, 0 skipped
global JUnit suite:        531 tests, 0 failures, 0 errors, 0 skipped
build:                     BUILD SUCCESSFUL
git diff --check:          clean (line-ending warnings only)
```

The XML counts were read from the configured build directory
`C:\mathmod-build\MathMod\test-results\test`, not the stale repository-local
`build/test-results/test` directory.

## 5. P12-TM-05F ownership

Terra Medium may modify only:

```text
src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
src/test/java/com/mathmod/client/screen/ProgrammerLayoutTest.java
src/test/java/com/mathmod/client/screen/FactoredLeapStatementPresentationTest.java
docs/UX_AUDIT.md
docs/handoffs/P12_TM_05F_HANDOFF.md
```

The existing P12-TM-05 delta and FS-01 through FS-05 evidence may be retained.
The correction must be limited to R1-R3. `TheoremStatementPresentation`,
`ProgramPresets`, formulas, localizations, `ProgramGraph`,
`GuidedWorkspaceState`, `ProgramSurfaceMode`, menus, networking, persistence,
Data Components, schemas, server code and public APIs remain read-only.

## 6. Recheck required for acceptance

Repeat the five focused classes, global JUnit suite and standard build from the
P12-SOL-03 contract. Preserve FS-01 through FS-05 and provide the new one-line
runtime measurement with locale, available width, exact line count, hitbox and
graph origin. The correction handoff must close R1, R2 and R3 individually.
