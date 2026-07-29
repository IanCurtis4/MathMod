# Handoff: L0-TM-04F

## Resultado

Implementação limitada à projeção funcional somente-leitura no buffer já
existente de abertura do Rune Programmer. Não há alteração em networking,
`ProgramStorage`, Data Components, `ProgramGraph`, schema, persistência ou API
pública. Este handoff pede revisão de Sol; não altera o estado de aceite no
board.

## Fechamento R1–R7

- **R1 — runtime/GameTest:**
  `L0FunctionalProjectionGameTests` contém exatamente os cinco vetores
  autorizados: `projectionMenuCodecRoundTripAndBounds`,
  `projectionMalformedFramesFailClosed`, `projectionReadCompileMatrixMutatesNothing`,
  `projectionAuthorityRechecksBecomeStale` e
  `projectionMenuBindingInvalidatesAfterTargetChange`. O primeiro faz
  round-trip por `RegistryFriendlyByteBuf`, inclusive um frame válido de
  exatamente 65.536 bytes. O terceiro percorre absent/current/malformed/future/
  conflict e compara o `ItemStack` antes/depois. Os dois últimos provam no
  servidor que uma troca do alvo invalida a projeção e suprime as linhas.
  A rotina interna única de aceite, chamada por `openingSnapshot`, é executada
  pelo GameTest com alterações independentes de conhecimento, geração de
  runas, definições de conhecimento, materiais e alvo; cada uma produz
  `STALE/AUTHORITY_STALE` sem linhas.
- **R2 — navegação/narração:** Inspector usa painéis Authored/Checked/Graph,
  Tab e Shift+Tab determinísticos, setas por linha, scroll limitado, foco
  visível e Escape para o pai. Todos os valores fechados de fonte, tentativa,
  grafo, relação, diagnóstico e linha possuem cópia EN/PT-BR; não há enum cru
  no texto funcional. O teste de locale exige paridade para todas as chaves.
- **R3 — preview:** `rune-inspector-functional` monta no servidor um talismã
  com fonte persistida, abre o Programmer pela rota normal e aciona o Inspector
  real. A matriz exige EN 1024x800, PT-BR 1024x800 e PT-BR 640x480.
- **R4 — graph-only:** construtor sem snapshot e rotas PRESETS/CUSTOM usam
  `graphOnly()` (`ABSENT`/`NOT_RUN`), sem atribuir stale falso.
- **R5 — binding:** menu guarda cópia do alvo, revalida por `DataSlot` e pelo
  accessor do servidor, e invalida após mutações do Programmer. Troca de item
  resulta em `STALE/AUTHORITY_STALE`, sem linhas autorais ou verificadas.
- **R6 — DTO/codec:** invariantes rejeitam nulos, combinações inseparáveis,
  limites e stale com linhas; tipos são tokens semânticos recursivos. Codec usa
  buffer temporário limitado, valida pré-alocação, truncamento, residuais,
  65.536 aceito e 65.537 rejeitado.
- **R7 — isolamento:** as três novas autoridades comuns constam do inventário
  de `ServerSideIsolationTest`; os dois checks de import client passaram.

## Arquivos L0 alterados

- Produção: `ScopedFunctionalProjection.java`,
  `ScopedFunctionalProjectionService.java`,
  `ScopedFunctionalProjectionWireCodec.java`, `ProgrammedTalismanItem.java`,
  `RuneProgrammerMenu.java`, `RuneProgrammerScreen.java`,
  `RuneInspectorScreen.java`, `ProgramInspectorPresentation.java`,
  `UiPreviewHarness.java`, `UiPreviewMatrix.java`, `en_us.json`, `pt_br.json`.
- Testes: `ScopedFunctionalProjectionTest.java`,
  `ScopedFunctionalProjectionWireCodecTest.java`,
  `RuneProgrammerProjectionTest.java`, `RuneInspectorScreenSourceTest.java`,
  `ProgramInspectorPresentationTest.java`, `UiPreviewMatrixTest.java`,
  `ServerSideIsolationTest.java`,
  `L0FunctionalProjectionGameTests.java`.
- Documentação: `docs/UI_PREVIEWS.md` e este handoff.

`ProgramGraphPresentation.java` não é parte do delta L0 descrito acima: ele já
estava modificado no worktree e permanece fora do ownership desta tarefa.

## Evidências executadas

### Focados

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.program.ScopedFunctionalProjectionTest `
  --tests com.mathmod.program.ScopedFunctionalProjectionWireCodecTest `
  --tests com.mathmod.screen.RuneProgrammerProjectionTest `
  --tests com.mathmod.client.screen.ProgramInspectorPresentationTest `
  --tests com.mathmod.client.screen.RuneInspectorScreenSourceTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Resultado: `BUILD SUCCESSFUL`. As sete classes contêm 22 vetores declarados
(4, 2, 1, 4, 4, 5 e 2, respectivamente), todos selecionados pelo comando.

### GameTest

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

O processo wrapper excedeu o timeout da ferramenta após o encerramento do
servidor, mas `run/gameTestServer/logs/latest.log` registra a execução e o
shutdown completos: `33 tests are now running`, `33 GAME TESTS COMPLETE` e
`All 33 required tests passed`. A classe nova tem 5 métodos GameTest; a classe
de persistência L0 preexistente tem 14; o total global observado é 33.

### Build

```powershell
.\gradlew.bat build
```

Resultado: `BUILD SUCCESSFUL`.

## Preview real

- `run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png`

As capturas são do fluxo server-backed do harness, não da construção de DTO no
cliente. As duas PT-BR exibiram a cópia localizada, incluindo `Fechar`.

## Migração e limites

Não há migração, reparo ou escrita na leitura; nenhuma fonte/envelope/JSON é
transportada; não foi adicionado payload, C2S, loader/reload, editor, inscrição
ou importação de grafo. O snapshot é descartável e `ProgramGraph` permanece a
única autoridade executável.

## Limitações e próximo responsável

Não se reivindica redução beta, normalização avaliativa, tipos inferidos por
nó, mutação funcional ou persistência de snapshot. Próximo responsável:
**Sol**, para revisão do delta e decisão do gate; L0-LU-01 e L0-TM-05
permanecem bloqueadas até ACCEPT explícito.
