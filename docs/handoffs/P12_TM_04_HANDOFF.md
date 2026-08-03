# P12-TM-04 Handoff

## Resultado

Implementação concluída para revisão de Sol. P12-TM-04 fecha DS01-R1 e DS01-R2 e apresenta a evidência R6 requerida. P12-DS permanece bloqueada até aceite e o futuro P12-SOL-03 resolve separadamente a apresentação de Factored Leap.

## DS01-R1 — repetição explícita de Self

`CustomSpellWorkspace.apply(SELF)` agora usa `addExplicitSelf()`: cada invocação explícita acrescenta uma rune `mathmod:self_player` e é a saída nova. `ensureSelf()` permanece reservado às premissas inferidas. Os vetores `repeatedExplicitSelfAlwaysAddsOneRuneAndBecomesTheOutput` e `everyLaboratoryActionRemainsPreviewableAfterItsOwnRepeatedApplication` cobrem repetição, preview, saída, undo e todas as ações.

## DS01-R2 — alvo capturado por componentes

`RuneProgrammerMenu.canMutateWorkspace` exige menu ativo, validade e `ItemStack.isSameItemSameComponents` contra `capturedProjectionTarget`. O GameTest `ordinaryMutationsRejectComponentDistinctReplacement` troca o talismã por outro do mesmo item, mas com nome e recursos distintos; nome, preset, clear, save, reset, undo, ação e invocação são rejeitados, ambos os stacks permanecem equivalentes e o jogador permanece conectado no menu.

## R6 — cliente real

`laboratory-self-repeat` seleciona a aba Laboratory, aplica `SELF` duas vezes, verifica a sequência e deixa hover na primeira ação antes da captura. O modo é o único isento dos dois preflights theorem-only conforme `P12_TM_04_HARNESS_PREFLIGHT_CLARIFICATION.md`; seus corpos e todos os demais modos permanecem inalterados. `UiPreviewMatrixTest.selfRepeatExemptsOnlyTheUnrelatedCatalogFormulaPreflight` é o oracle de fonte/matriz.

- EN-US, 1024x800: log `Saved screenshot as mathmod-laboratory-self-repeat-preview.png`.
- PT-BR, 1024x800: log `Tela capturada como mathmod-laboratory-self-repeat-preview.png`.

O nome estável é referenciado apenas no harness/matriz/teste; não há consumidor externo. A segunda execução sobrescreve o PNG, mas os dois logs de execução constituem a evidência de ambas as capturas. Recomenda-se apenas, para futura ergonomia de auditoria, nomear por locale; isto não é requerido para a correção nem foi alterado aqui.

## Validação

- `gradlew cleanTest test --no-build-cache`: sucesso.
- `gradlew build --no-build-cache`: sucesso.
- `gradlew test --no-build-cache --tests com.mathmod.program.CustomSpellWorkspaceTest`: 22/22, sucesso.
- `gradlew test --no-build-cache --tests com.mathmod.client.UiPreviewMatrixTest`: sucesso.
- `gradlew runGameTestServer --no-daemon`: 60/60 GameTests obrigatórios aprovados.

## Arquivos alterados

- `src/main/java/com/mathmod/program/CustomSpellWorkspace.java`
- `src/main/java/com/mathmod/screen/RuneProgrammerMenu.java`
- `src/main/java/com/mathmod/program/P12DsProgrammerGameTests.java`
- `src/test/java/com/mathmod/program/CustomSpellWorkspaceTest.java`
- `src/main/java/com/mathmod/client/UiPreviewHarness.java`
- `src/main/java/com/mathmod/client/UiPreviewMatrix.java`
- `src/test/java/com/mathmod/client/UiPreviewMatrixTest.java`
- este handoff.

## Limites

Não foram alterados ProgramGraph, schemas/Data Components, APIs públicas, rede, fórmulas, localização, `RuneProgrammerScreen` ou apresentação de teorema. A falha de apresentação de Factored Leap está registrada em `P12_FACTORED_LEAP_STATEMENT_PRESENTATION_FINDING.md` e não é reivindicada como resolvida.
