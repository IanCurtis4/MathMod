# L0-LU-01 / L0-LU-01F — Final Gate Acceptance

**Reviewed tasks:** `L0-LU-01`, `L0-LU-01F`  
**Date:** 2026-07-29  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Reviewed handoff:** `docs/handoffs/L0_LU_01F_HANDOFF.md`

## 1. Decision

The bilingual functional-teaching content and visual evidence are accepted.
`L0-LU-01` and `L0-LU-01F` are `DONE` with `ACCEPT`.

This acceptance closes the Luna dependency of `L0-TM-05`. It does not yet
authorize `L0-TM-05`, because the separate Sol-owned exact internal
integration-readiness amendment remains unresolved.

The only next task that becomes `READY` is `L0-SOL-07`.

## 2. Accepted ownership and content

The bounded F correction changed only:

```text
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
docs/handoffs/L0_LU_01F_HANDOFF.md
```

Pages 0–3 were shortened or redistributed. Pages 4–7 are byte-semantically
unchanged from the repository baseline. Both locale entries:

- parse as UTF-8 JSON;
- retain exactly eight pages;
- preserve the 29-theorem catalog and all five catalog areas;
- preserve the existing theorem claims across pages 3–7;
- keep `Factored Leap` / `Salto fatorado` teaching-only rather than claiming a
  runtime card or inscription route;
- distinguish authored source, checked form and compiled graph;
- state that `halve` is pure, defined once and applied twice;
- keep observations outside the lambda;
- teach beta reduction/compilation lowering before execution;
- end at the existing `push_self` terminal effect;
- state that errors or stale authority do not partially inscribe an item;
- retain keyboard, narrator, diagnostics and compact-view guidance.

The language files remain at 854 keys each with identical key sets. No Java,
test, schema, Data Component, networking, public API, semantic id or gameplay
implementation was changed by Luna.

## 3. Visual acceptance

Sol directly inspected all eight regenerated 1024x800 spreads:

```text
mathmod-patchouli-matrix-programming-beta_theorems-p0-en_us-preview.png
mathmod-patchouli-matrix-programming-beta_theorems-p2-en_us-preview.png
mathmod-patchouli-matrix-programming-beta_theorems-p4-en_us-preview.png
mathmod-patchouli-matrix-programming-beta_theorems-p6-en_us-preview.png
mathmod-patchouli-matrix-programming-beta_theorems-p0-pt_br-preview.png
mathmod-patchouli-matrix-programming-beta_theorems-p2-pt_br-preview.png
mathmod-patchouli-matrix-programming-beta_theorems-p4-pt_br-preview.png
mathmod-patchouli-matrix-programming-beta_theorems-p6-pt_br-preview.png
```

The final images show:

- the expected language in each locale;
- no lower-boundary clipping;
- no heading collision or text overlap;
- no mojibake;
- no untranslated ordinary player copy;
- no raw `mathmod:` semantic id;
- readable retained claims on p4 and p6.

## 4. Reproduced verification

The required focused command passed:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.assets.PortugueseLocalizationQualityTest `
  --tests com.mathmod.client.PatchouliPreviewMatrixTest `
  --tests com.mathmod.integration.patchouli.PatchouliFieldManualTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Result:

| Suite | Passing methods |
|---|---:|
| `PortugueseLocalizationQualityTest` | 3 |
| `PatchouliPreviewMatrixTest` | 2 |
| `PatchouliFieldManualTest` | 3 |
| `UiPreviewMatrixTest` | 5 |
| `ServerSideIsolationTest` | 2 |
| **Total** | **15** |

Failures, errors and skipped methods are all zero.

The required standard command also passed:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`.

## 5. Next gate

`L0-SOL-07` must freeze the exact internal integration shape between
`com.mathmod.screen.RuneProgrammerMenu` and the deliberately package-private
`com.mathmod.program.ScopedFunctionalInscriptionService`.

That amendment must select exact ownership without:

- making functional inscription a supported public extension API;
- creating a second compile or transaction authority;
- routing `mathmod:factored_leap` through graph-only `ProgramStorage`;
- moving compile, knowledge, material or commit authority to the client;
- changing networking, schemas, accepted Data Component identities,
  `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode` or public APIs.

```text
L0-LU-01 DONE (ACCEPT)
L0-LU-01F DONE (ACCEPT)
    -> L0-SOL-07 READY

L0-TM-05 BLOCKED
    -> accepted L0-SOL-07 integration-readiness amendment
```

No Terra Medium theorem implementation may begin before that Sol gate is
accepted.
