# P12-TM-05F Handoff - Statement Presentation Regression Correction

## Result

Ready for Sol review. P12-TM-05F closes only the three findings from
`docs/P12_TM_05_GATE_REVIEW.md`. The selected theorem statement has an
effective range of two to three lines, the catalog exception is the frozen
id-and-formula pair, and focused tests now exercise executable geometry
oracles. `docs/DELIVERY_BOARD.md` was not modified.

## R1 - one-line geometry

`TheoremStatementGeometry` is package-private and is the sole geometry rule
used by `RuneProgrammerScreen`. It maps rendered counts 1, 2 and 3 to effective
counts 2, 2 and 3 respectively, rejects 4+, yields widget heights 22, 22 and
33 pixels, and yields statement viewport offsets 37, 37 and 48 pixels. The
screen continues to add its fixed panel origin, so the real one-line graph
origin remains 115 and the third-line origin is 126. No public API was added.

The new real client vector `fs-06` selected `mathmod:hop` in EN-US at
1024x800, GUI scale 2. Its log recorded: available width 133, exact line count
1, line width 92, statement hitbox `140x22`, and graph origin `115`.
Screenshot: `run/client/screenshots/mathmod-fs-06-en_us-preview.png`.

## R2 - exact catalog exception

The catalog preflight now delegates only to
`FactoredLeapCatalogException.isExact`: both `mathmod:factored_leap` and the
frozen catalog formula `push(halve(look)+halve(up))` must match. The focused
behavioral test accepts the registered pair and rejects (a) that id with a
different formula and (b) that formula with a different id. `ProgramPresets`
and frozen formula content were not changed.

## R3 - executable regression coverage

`FactoredLeapStatementPresentationTest.headerGeometryRetainsTheLegacyMinimumForOneAndTwoLines`
is an executable deterministic oracle for all one-, two- and three-line
effective counts, heights and viewport offsets, including an explicit fourth
line rejection. `UiPreviewMatrixTest.catalogExceptionRequiresBothTheFrozenIdAndFrozenFormula`
is the R2 behavioral oracle. `fs-06` is included in the preview matrix while
FS-01 through FS-05 are retained unchanged.

## Changed task-owned files

- Production: `src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java`.
- Client harness and matrix: `src/main/java/com/mathmod/client/UiPreviewHarness.java`,
  `src/main/java/com/mathmod/client/UiPreviewMatrix.java`.
- Focused tests: `src/test/java/com/mathmod/client/UiPreviewMatrixTest.java`,
  `src/test/java/com/mathmod/client/screen/FactoredLeapStatementPresentationTest.java`.
- Documentation: `docs/UX_AUDIT.md` and this handoff.

No `ProgramGraph`, preset, formula, localization, persistence, networking,
menu, server, component, schema, guided-state, or public API file was changed.

## Retained and new runtime evidence

The prior accepted FS-01 through FS-05 artifacts remain available and unchanged:

| Vector | Retained result |
| --- | --- |
| FS-01 | EN-US Factored Leap: 103 px, 3 lines (100/101/79), 110x33, origin 126. |
| FS-02 | PT-BR Factored Leap: same complete three-line geometry. |
| FS-03 | PT-BR 640x480: 101 px, 3 lines (100/101/79), 108x33, origin 126. |
| FS-04 | PT-BR 640x480: hover derives from the shared graph viewport origin. |
| FS-05 | EN-US Right Angle: 2 lines, 140x22, origin 115. |
| FS-06 | EN-US Hop: 133 px, 1 line (92), 140x22, origin 115; new capture. |

## Commands and results

With `GRADLE_USER_HOME=C:\codex-gradle-a0`:

```text
gradlew cleanTest test --no-build-cache \
  --tests com.mathmod.program.ProgramPresetsTest \
  --tests com.mathmod.client.UiPreviewMatrixTest \
  --tests com.mathmod.client.screen.ProgrammerLayoutTest \
  --tests com.mathmod.client.screen.FactoredLeapStatementPresentationTest \
  --tests com.mathmod.server.ServerSideIsolationTest
```

Result: `BUILD SUCCESSFUL`; XML under
`C:\mathmod-build\MathMod\test-results\test` reports 38 tests, 0 failures,
0 errors.

```text
gradlew cleanTest test --no-build-cache
```

Result: `BUILD SUCCESSFUL`; 533 tests, 0 failures, 0 errors.

```text
gradlew build --no-build-cache
```

Result: `BUILD SUCCESSFUL`.

```text
MATHMOD_UI_PREVIEW=fs-06
MATHMOD_UI_PREVIEW_LOCALE=en_us
MATHMOD_UI_PREVIEW_WIDTH=1024
MATHMOD_UI_PREVIEW_HEIGHT=800
gradlew runClient --no-daemon --no-build-cache
```

Result: `BUILD SUCCESSFUL`; the client wrote the FS-06 screenshot and the
runtime measurement quoted above. `git diff --check` is clean apart from the
repository's existing line-ending warnings.

## Boundaries and non-claims

This correction does not redesign the accepted compact catalog clipping, alter
any frozen statement, or claim a P12-DS rerun or an immutable artifact. It does
not add migration, persistent read-time mutation, loader/reload behavior, or
GameTest coverage because those are outside R1-R3. Acceptance remains Sol's
decision; `P12-DS` remains blocked pending that decision and its separate clean
rerun.

