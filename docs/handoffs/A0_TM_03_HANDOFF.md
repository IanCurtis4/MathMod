# Handoff: A0-TM-03F3

## Completed

- Closed A0-4-F2R1 only. The `authoring-registry-palette` runtime harness is now a state-asserted sequence instead of consecutive unverified input calls.
- Keyboard Enter activates non-parameterized `Self`; the harness requires the Guided workspace to contain exactly `Self` before continuing.
- The harness then focuses the actual Laboratory search field, types `simpson`, verifies that Simpson is the first filtered palette form, pointer-activates that row, and verifies the active descriptor dialog is `SIMPSON_INTEGRAL` with five finite numeric defaults.
- The final dialog cannot contain `1simpson`: the default-value assertion rejects non-numeric/non-finite values and the runtime searches only after the non-modal `Self` mutation.

## Decisions implemented

- The preview state machine is intentionally contained in `UiPreviewHarness`; it has no authority outside client preview validation.
- Existing F2 registry presentation, fallback, category-color parity, metadata ordering, persistence identity, and expansion behavior were not reopened.

## Files changed

- `src/main/java/com/mathmod/client/UiPreviewHarness.java`
- `src/test/java/com/mathmod/client/screen/RuneProgrammerRegistrySourceTest.java`
- `docs/UI_PREVIEWS.md`
- `docs/handoffs/A0_TM_03_HANDOFF.md`

## Tests and evidence

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.client.screen.*' --tests 'com.mathmod.client.UiPreviewMatrixTest' --tests 'com.mathmod.authoring.TrustedLegacyExpansionAdapterTest' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`.

The focused source test now requires the state assertions for `Self`, filtered Simpson, pointer activation, the Simpson dialog, and rejection of `1simpson`-style values.

Real integrated-client executions, each ending `BUILD SUCCESSFUL` and logging `UI preview result`:

| Matrix entry | Screenshot | Harness log |
|---|---|---|
| `authoring-registry-palette@en_us-1024x800-g2` | `run/client/screenshots/mathmod-authoring-registry-palette-en_us-1024x800-preview.png` | `run/client/logs/authoring-registry-palette-en_us-1024x800.log` |
| `authoring-registry-palette@pt_br-1024x800-g2` | `run/client/screenshots/mathmod-authoring-registry-palette-pt_br-1024x800-preview.png` | `run/client/logs/authoring-registry-palette-pt_br-1024x800.log` |
| `authoring-registry-palette@pt_br-640x480-g2` | `run/client/screenshots/mathmod-authoring-registry-palette-pt_br-640x480-preview.png` | `run/client/logs/authoring-registry-palette-pt_br-640x480.log` |

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## Contracts referenced

- `docs/A0_TM_03F2_FINAL_REVIEW.md`
- `docs/A0_TM_03F_GATE_REREVIEW.md`
- `docs/A0_TM_03_READINESS_ACCEPTANCE.md`
- `docs/UI_PREVIEWS.md`

## Migration impact

- None. No persistence/schema, graph, stable-id, Data Component, networking/payload, execution, Inspector, localization, or public API change.

## Known limitations

- Built-in metadata remains complete, so the synthetic technical-presentation fallback stays unit-proven in the earlier projection tests; this correction adds no incomplete runtime registry.
- A0-5 owns missing-descriptor/replay-mismatch recovery, reload/reconnect, and persistence hardening.

## Next owner

- Sol

## Exact next task

- Review `A0-TM-03F3` against `docs/A0_TM_03F2_FINAL_REVIEW.md`. If accepted, release the A0-TM-03 gate so A0-TM-04 can be dispatched under its own ownership.

## Released file ownership

- `src/main/java/com/mathmod/client/UiPreviewHarness.java`
- `src/test/java/com/mathmod/client/screen/RuneProgrammerRegistrySourceTest.java`
- `docs/UI_PREVIEWS.md`
- `docs/handoffs/A0_TM_03_HANDOFF.md`

## Files the next owner must not edit

- `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data Components, persistence codecs, networking/payloads, execution/inscription, stable ids, locale/Patchouli content, public APIs, and all A1 Inspector helpers.
