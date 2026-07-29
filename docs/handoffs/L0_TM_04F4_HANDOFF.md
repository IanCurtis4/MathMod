# L0-TM-04F4 — Handoff para Sol

## Resultado

F4R1 foi corrigido e verificado. Este handoff não altera o aceite nem o estado
do `DELIVERY_BOARD`.

## Fechamento de F4R1

- `RuneInspectorScreen` passa a usar `font.split` para os headings semânticos,
  com a mesma largura interna usada pela renderização.
- `FunctionalLayout` reserva a altura exata das linhas produzidas por
  `font.split`; selector, heading, linhas visíveis, hit-test e botões usam a
  mesma geometria.
- Em largura compacta, os três seletores ficam em linhas próprias e somente o
  conteúdo do painel selecionado é exibido. Assim, cada estado permanece
  acessível por ponteiro/Tab, tem heading integral e uma linha visível, sem
  exceder a área disponível de 640×480.
- `functionalLayoutContained` é o oráculo executável: mede os headings com a
  fonte de produção e exige que os limites de Authored, Checked e Graph fiquem
  antes do fim do painel. O harness o executa antes da auditoria funcional.
- O hit-test foi restrito ao retângulo do selector efetivamente renderizado;
  isto evita que a área de conteúdo ativo sobreponha o clique de outro selector.

## Capturas revisadas

As três execuções `rune-inspector-functional` concluíram com `BUILD
SUCCESSFUL`, incluindo oráculo de contenção, ponteiro, Tab/Shift+Tab,
narração, navegação de linha e retorno por Escape. As imagens foram abertas e
inspecionadas após a execução:

- `run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png`

Em EN/PT-BR 1024×800 os headings completos e as linhas ficam no painel de
detalhes. Em PT-BR 640×480 os três seletores permanecem visíveis e o conteúdo
ativo (heading e linha) fica contido no painel direito; não há cruzamento com
o painel do grafo ou com a janela.

## Arquivos F4 alterados

- `src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java`
- `src/main/java/com/mathmod/client/UiPreviewHarness.java`
- `src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java`
- `docs/handoffs/L0_TM_04F4_HANDOFF.md`

Os PNGs acima foram regenerados como artefatos de evidência. Mudanças já
existentes no worktree fora dessa lista não foram modificadas por F4.

## Vetores e resultados

Teste focado, executado com `cleanTest test --no-build-cache` e os sete filtros:

```text
ScopedFunctionalProjectionTest             7
ScopedFunctionalProjectionWireCodecTest    2
RuneProgrammerProjectionTest               1
ProgramInspectorPresentationTest           4
RuneInspectorScreenSourceTest              4
UiPreviewMatrixTest                        5
ServerSideIsolationTest                    2
Total                                     25
BUILD SUCCESSFUL
```

GameTests:

```text
L0 functional projection GameTests: 5
L0 persistence GameTests:          14
Total global descoberto:           33
33 GAME TESTS COMPLETE
All 33 required tests passed
```

Os cinco GameTests funcionais são
`projectionMenuCodecRoundTripAndBounds`, `projectionMalformedFramesFailClosed`,
`projectionReadCompileMatrixMutatesNothing`,
`projectionAuthorityRechecksBecomeStale` e
`projectionMenuBindingInvalidatesAfterTargetChange`.

Comandos finais executados:

```text
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --tests com.mathmod.program.ScopedFunctionalProjectionTest --tests com.mathmod.program.ScopedFunctionalProjectionWireCodecTest --tests com.mathmod.screen.RuneProgrammerProjectionTest --tests com.mathmod.client.screen.ProgramInspectorPresentationTest --tests com.mathmod.client.screen.RuneInspectorScreenSourceTest --tests com.mathmod.client.UiPreviewMatrixTest --tests com.mathmod.ServerSideIsolationTest
BUILD SUCCESSFUL

.\gradlew.bat runGameTestServer --no-daemon
All 33 required tests passed; BUILD SUCCESSFUL

.\gradlew.bat build --no-daemon
BUILD SUCCESSFUL
```

## Limites e não-alterações

F4 não altera `ProgramGraph`, `GuidedWorkspaceState`, Data Components,
`ProgramStorage`, fonte/schema, networking, persistência, migração, loader ou
API pública. Não reivindica edição funcional: o Inspector continua somente
leitura e `ProgramGraph` permanece a autoridade executável. Não há escalation
aberta.
