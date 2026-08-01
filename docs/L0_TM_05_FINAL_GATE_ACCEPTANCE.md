# L0-TM-05 / F / F2 — Final Gate Acceptance

**Reviewed tasks:** `L0-TM-05`, `L0-TM-05F`, `L0-TM-05F2`  
**Date:** 2026-07-30  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Reviewed handoff:** `docs/handoffs/L0_TM_05F2_HANDOFF.md`

## 1. Decision

The first functional gameplay theorem gate is accepted. `L0-TM-05`,
`L0-TM-05F` and `L0-TM-05F2` are `DONE` with `ACCEPT`.

The accepted repository state implements `mathmod:factored_leap` through the
frozen scoped-source compile authority, the existing six-component transaction
machine and the real Rune Programmer route. The two residual findings in
`docs/L0_TM_05F_GATE_REVIEW.md` are individually closed:

| Finding | Repository evidence | Result |
|---|---|---|
| F-R1 | `FactoredLeapTheoremTest` finds an id- and order-independent semantic bijection and exercises renamed/reordered plus missing, extra and altered adversarial graphs | closed |
| F-R2 | `L0FactoredLeapGameTests` performs a tooltip-equivalent read and observes actual outbound packet objects for missing-knowledge and injected-commit failures | closed |

This acceptance also closes R1-R5 from `docs/L0_TM_05_GATE_REVIEW.md`. The
accepted F and F2 evidence executes the real button-37 menu route, exact
graph/source/resource persistence, empty-resource absence, reload without
compile/migration/mutation, all twelve transaction injection points, stale
authority rejection, functional execution and absence of false saved feedback
or success-only slot synchronization.

## 2. Accepted semantic and transactional evidence

The graph oracle compares rune identity, constant keys and values, named-socket
connectivity, the output node, a bijective node mapping and exactly one shared
`self_player`. Only finite NUMBER textual normalization is allowed. Generated
node ids and collection order are not semantic. The adversarial vector proves
that renaming every compiled id and reversing node and edge order still passes,
while missing, extra or altered graph structure fails.

The GameTest harness installs a `ChannelOutboundHandlerAdapter` on the actual
mock player's `EmbeddedChannel`. It observes `ClientboundSystemChatPacket` and
`ClientboundContainerSetSlotPacket` objects. Both missing knowledge and an
injected component-0 `BEFORE` failure preserve the item and emit neither saved
feedback nor success-only slot synchronization. The twelve-point direct matrix
continues to prove exact rollback, including scoped-source envelope bytes.

The tooltip-equivalent read uses `ProgrammedTalismanItem.appendHoverText` after
an `ItemStack.CODEC` round trip. Persistence, graph inspection and tooltip
access leave every item component unchanged.

## 3. Ownership and boundaries

The F2 delta is confined to the authorized test/harness surface:

```text
src/main/java/com/mathmod/program/L0FactoredLeapGameTests.java
src/test/java/com/mathmod/program/FactoredLeapTheoremTest.java
docs/handoffs/L0_TM_05F2_HANDOFF.md
```

`RuneProgrammerFunctionalTheoremTest` remained unchanged. F2 made no production
functional change and did not alter `ProgramGraph`, `GuidedWorkspaceState`,
accepted Data Component identities or schemas, networking, client/UI Java,
`ProgramSurfaceMode` or a public API. The accepted theorem implementation stays
inside the ownership frozen by
`docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md`.

The packet harness proves the server-side mock route; it does not claim a
remote client session, reconnection or graphical-client behavior. The known
mock attachment `sync_attachments` limitation remains limited to setup and is
not interpreted as inscription success.

## 4. Reproduced verification

The focused command was reproduced with the frozen Gradle home and
`--no-build-cache --no-daemon`:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --no-daemon `
  --tests com.mathmod.program.FactoredLeapTheoremTest `
  --tests com.mathmod.program.ScopedFunctionalInscriptionEntryPointTest `
  --tests com.mathmod.screen.RuneProgrammerFunctionalTheoremTest `
  --tests com.mathmod.program.ProgramPresetsTest `
  --tests com.mathmod.program.ScopedSourceEnvelopeTest `
  --tests com.mathmod.program.ScopedSourceWireCodecTest `
  --tests com.mathmod.program.ScopedProgramComponentTransactionTest `
  --tests com.mathmod.program.ScopedProgramPersistenceTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Result: 9 classes, 49 tests, 0 failures, 0 errors and 0 skipped.

The runtime command was reproduced:

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

Result: 43/43 global GameTests passed. Source inspection confirms:

```text
10 L0 Factored Leap GameTests
14 L0 scoped persistence GameTests
5 L0 functional projection GameTests
29 total L0 GameTests
43 global GameTests
```

The standard build was reproduced:

```powershell
.\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## 5. Downstream decision

The gameplay implementation is closed. The only documented post-implementation
dependency is the content follow-up named in
`docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md`: the accepted Patchouli
sentence saying Factored Leap is not a runtime card or inscription route is now
obsolete.

Therefore `L0-LU-02` — Post-implementation Factored Leap Content Alignment —
becomes `READY` for Luna. It is content-only.

Luna may edit only:

```text
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
docs/handoffs/L0_LU_02_HANDOFF.md
```

Luna may regenerate evidence only under:

```text
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-*
```

The correction must replace only the obsolete pre-implementation claim with an
accurate bilingual statement that Factored Leap is available through the Rune
Programmer; preserve eight pages, the 29-theorem catalog and existing teaching
claims; show both affected p0 spreads at 1024x800 without presentation defects;
and reproduce this focused command plus the standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.assets.PortugueseLocalizationQualityTest `
  --tests com.mathmod.client.PatchouliPreviewMatrixTest `
  --tests com.mathmod.integration.patchouli.PatchouliFieldManualTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest

.\gradlew.bat build
```

Its handoff must inventory changed files, exact per-suite counts, captures,
limitations and escalations.

No Java, test source, schema, networking, Data Component, semantic id, public
API or gameplay change is authorized. `A0-6` remains `BACKLOG` and still
requires a separate Sol-owned contract.

```text
L0-TM-05 DONE (ACCEPT)
L0-TM-05F DONE (ACCEPT)
L0-TM-05F2 DONE (ACCEPT)
    -> L0 gameplay implementation closed
    -> L0-LU-02 READY
```
