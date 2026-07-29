# L0-TM-04F2 — Handoff para re-review do Sol

**Resultado:** implementação e evidências de L0-TM-04-F2R1 a F2R6 concluídas.
Este handoff não altera o estado de aceite em `docs/DELIVERY_BOARD.md`; a decisão
do gate permanece com Sol.

## Fechamento individual

### F2R1 — matriz fechada do DTO

`ScopedFunctionalProjection` agora valida a combinação inteira de
`SourceState`, `AttemptState`, `GraphState`, `GraphRelation`, linhas e diagnósticos:

- fonte não atual (`ABSENT`, ilegível, versão não suportada ou conflito) só
  admite `NOT_RUN`, sem linhas e com o diagnóstico canônico aplicável;
- fonte atual exige linha autoral e tentativa executada; sucesso exige linhas
  autorais e verificadas, grafo presente e relação `MATCH` ou `MISMATCH`;
- rejeições não admitem linhas verificadas nem relação de grafo; stale remove
  todas as linhas e só conserva presença/ausência do grafo e
  `NOT_COMPARABLE`;
- diagnósticos são restringidos à fase/código permitido pelo estado.

`ScopedFunctionalProjectionTest.rejectsEveryImpossibleSourceRowAttemptAndRelationCombination`
rejeita as combinações impossíveis citadas pelo re-review. O vetor de limite
em `projectionEncodedAtLimit` é agora semanticamente válido:
`CURRENT_VALID/LANGUAGE_REJECTED`, 64 linhas autorais e diagnóstico de
linguagem; 65.536 bytes codificam e 65.537 falham fechados.

### F2R2 — cardinalidade de compilação

`ScopedFunctionalProjectionService.build(..., Runnable beforeCompile)` é um
seam package-private somente para medição imediatamente antes da chamada ao
mesmo compilador de produção. `projectionReadCompileMatrixMutatesNothing`
verifica exatamente uma chamada para `CURRENT_VALID` e zero para ausente,
ilegível, futura e conflito; também verifica o estado de tentativa esperado e
que o `ItemStack` permanece byte/componente-equivalente.

### F2R3 — vínculo do menu

`projectionMenuBindingInvalidatesAfterTargetChange` cobre:

- mutação do componente de fonte no mesmo talismã capturado;
- transição `DataSlot` 1→0 no receptor de menu cliente;
- withholding de todas as linhas após essa transição;
- uma mutação real pelo `RuneProgrammerMenu` (`CLEAR_BUTTON`), com invalidação
  e projeção stale sem linhas.

### F2R4 — stale sem contradição de autoridade

`unavailable(GraphState)` preserva a presença já conhecida do grafo ao
invalidar a projeção; `RuneProgrammerMenu` reutiliza esse estado. Há vetores
para match, mismatch e stale com `GraphState.PRESENT`; stale nunca retém
relação de igualdade nem linhas, mas não anuncia grafo ausente quando o canvas
continua a apresentar a autoridade executável.

### F2R5 — foco, geometria e narração executáveis

`RuneInspectorScreen` cria controles reais para Authored, Checked e Graph;
suas posições e os hit regions usam a mesma altura derivada por seção. A ordem
determinística é Close → Authored → Checked → Graph → Close (e inversa para
Shift+Tab). A narração anuncia sempre o painel selecionado, inclusive painel
vazio, e inclui a linha selecionada quando existe.

O harness `rune-inspector-functional` abre o menu servidor, seleciona a aba
SAVED real e audita em runtime: Tab, seta com mudança de linha autoral,
navegação repetida limitada, Tab para Checked/Graph, clique no controle
renderizado, Shift+Tab e Escape para o pai. O mesmo harness passou em 1024×800
e 640×480.

### F2R6 — PT-BR e evidência separada

O botão Close é dimensionado pela largura traduzida mais padding. As capturas
regeneradas PT-BR mostram `Fechar` completo em 1024×800 e 640×480. A evidência
de interação é a auditoria runtime acima, não uma inferência das imagens.

## Arquivos relevantes alterados

Produção: `ScopedFunctionalProjection`, `ScopedFunctionalProjectionService`,
`ScopedFunctionalProjectionWireCodec`, `RuneProgrammerMenu`,
`ProgrammedTalismanItem`, `RuneProgrammerScreen`, `RuneInspectorScreen`,
`UiPreviewHarness`, `UiPreviewMatrix`, traduções EN/PT-BR e os recursos de
preview/documentação já autorizados para L0-TM-04.

Testes: `ScopedFunctionalProjectionTest`,
`ScopedFunctionalProjectionWireCodecTest`, `RuneProgrammerProjectionTest`,
`ProgramInspectorPresentationTest`, `RuneInspectorScreenSourceTest`,
`UiPreviewMatrixTest`, `ServerSideIsolationTest` e
`L0FunctionalProjectionGameTests`.

Documentação/evidência: este handoff e
`run/client/screenshots/mathmod-rune-inspector-functional-{en_us-1024x800,pt_br-1024x800,pt_br-640x480}-preview.png`.

## Autoridade e limites

`ProgramGraph` continua sendo a única autoridade executável. A projeção é
somente uma visão servidor→menu de fonte/resultado; PRESETS e CUSTOM recebem
`graphOnly()`. Não foram adicionados C2S, `ModNetworking`, stream codec da
fonte, Data Component, migração, reload externo, edição funcional ou mutação
persistente durante leitura. O único transporte de projeção continua sendo o
buffer de abertura do menu.

## Vetores e resultados

Os cinco GameTests funcionais são:

1. `projectionMenuCodecRoundTripAndBounds`
2. `projectionMalformedFramesFailClosed`
3. `projectionReadCompileMatrixMutatesNothing`
4. `projectionAuthorityRechecksBecomeStale`
5. `projectionMenuBindingInvalidatesAfterTargetChange`

Resultado final: **5 funcionais L0 + 14 de persistência L0; 33 GameTests
globais executados, todos aprovados.**

Comandos finais executados:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat test --no-build-cache --no-daemon `
  --tests com.mathmod.program.ScopedFunctionalProjectionTest `
  --tests com.mathmod.program.ScopedFunctionalProjectionWireCodecTest `
  --tests com.mathmod.screen.RuneProgrammerProjectionTest `
  --tests com.mathmod.client.screen.ProgramInspectorPresentationTest `
  --tests com.mathmod.client.screen.RuneInspectorScreenSourceTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
# BUILD SUCCESSFUL

.\gradlew.bat runGameTestServer --no-daemon
# 33 GAME TESTS COMPLETE; BUILD SUCCESSFUL

.\gradlew.bat build --no-daemon
# BUILD SUCCESSFUL
```

Também foram executados com êxito `runClient` para
`rune-inspector-functional`: EN 1024×800, PT-BR 1024×800 e PT-BR 640×480.
Cada execução encerrou normalmente depois da auditoria de interação e da
captura.

## Limitações e não alegações

A projeção não repara, migra, salva ou executa Source; rejeição de admissão ou
linguagem continua visível apenas como resultado de leitura/compilação
servidor. O preview integrado constrói somente um fixture de desenvolvimento
para exercitar a rota existente; não adiciona transporte ou API de produção.
Não há escalation pendente dentro do ownership autorizado.
