# L0-LU-02 — Gate Acceptance

**Task:** `L0-LU-02` — Post-implementation Factored Leap Content Alignment  
**Date:** 2026-07-30  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Reviewed handoff:** `docs/handoffs/L0_LU_02_HANDOFF.md`

## 1. Decision

`L0-LU-02` is `DONE` with `ACCEPT`.

The obsolete pre-implementation statement was replaced in both localized
Patchouli entries with the exact bounded availability claim:

```text
Factored Leap is available through the Rune Programmer.
Salto fatorado está disponível pelo Programador rúnico.
```

The content now agrees with the accepted runtime theorem gate without claiming
client compile authority, new behavior or implementation detail.

## 2. Ownership and preservation

The correction is confined to:

```text
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
docs/handoffs/L0_LU_02_HANDOFF.md
```

Only `pages[0].text` changed relative to the accepted L0-LU-01/F content. Both
entries remain valid UTF-8 JSON with exactly eight pages. They preserve:

- the 29-theorem catalog and five catalog areas;
- all previously accepted functional-teaching claims;
- `Factored Leap` / `Salto fatorado` terminology;
- the persistent `mathmod:factored_leap` identity;
- EN-US/PT-BR semantic parity.

No Java, test source, schema, networking, Data Component, semantic identity,
public API, compiler or gameplay file is in the L0-LU-02 delta.

## 3. Visual acceptance

Sol inspected the two final 1024x800 captures:

```text
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-en_us-preview.png
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-pt_br-preview.png
```

Both show the expected localized availability sentence and the adjacent
formula page. Neither shows clipping, overlap, mojibake, untranslated ordinary
copy or a raw semantic id in the affected content.

## 4. Reproduced verification

The required focused command was reproduced:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --no-daemon `
  --tests com.mathmod.assets.PortugueseLocalizationQualityTest `
  --tests com.mathmod.client.PatchouliPreviewMatrixTest `
  --tests com.mathmod.integration.patchouli.PatchouliFieldManualTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Result:

| Suite | Tests |
|---|---:|
| `PortugueseLocalizationQualityTest` | 3 |
| `PatchouliPreviewMatrixTest` | 2 |
| `PatchouliFieldManualTest` | 3 |
| `UiPreviewMatrixTest` | 5 |
| `ServerSideIsolationTest` | 2 |
| **Total** | **15** |

Failures, errors and skipped tests are all zero.

The standard build was reproduced:

```powershell
.\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## 5. Downstream

This acceptance closes the documented L0 gameplay and content sequence. No
later L0 task is named or dependency-ready in the repository, so none becomes
`READY`.

`A0-6` remains `BACKLOG`. It cannot be dispatched until a separate Sol-owned
contract freezes its external-source authority, ownership and gates.

```text
L0-TM-05/F/F2 DONE (ACCEPT)
    -> L0-LU-02 DONE (ACCEPT)
    -> L0 gameplay/content sequence closed

A0-6 BACKLOG
    -> requires a new Sol-owned contract
```
