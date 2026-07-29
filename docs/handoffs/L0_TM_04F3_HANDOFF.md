# L0-TM-04F3 — Handoff para Sol

## Resultado

F3R1–F3R5 foram corrigidos e reavaliados. Este documento não altera o
aceite no Delivery Board.

## Fechamento

- **F3R1:** `RuneInspectorScreen` usa uma geometria única de seções para
  bounds de selector, hit testing, heading e linhas. Selectors possuem linha
  própria; seus rótulos são curtos e os três significados completos são
  renderizados abaixo, sem sobreposição. As três capturas foram regeneradas.
- **F3R2:** a ordem determinística inclui Close; Tab/Shift+Tab, clique,
  structural path, kind, valor, limite sem wrap, scroll e Escape são
  verificados no harness. A matriz runtime também exige narrativas explícitas
  para mismatch, conflict, unreadable, unsupported, stale e graph-absent;
  diagnósticos agora narram código e caminho, não só contagem.
- **F3R3:** o preview integrado executa o seam real de `openingSnapshot` no
  `ServerPlayer` vivo: conta uma compilação, altera `PlayerKnowledge` depois
  da construção, exige `STALE` sem linhas e restaura o conhecimento antes de
  abrir o menu normal.
- **F3R4:** `writeFailClosed` codifica primeiro no buffer temporário e, em
  overflow, escreve uma única projeção stale mínima. O GameTest decodifica o
  resultado e exige ausência de linhas e bytes restantes.
- **F3R5:** contagens focadas: 7 + 2 + 1 + 4 + 4 + 5 + 2 = **25**. Esta
  afirmação é separada de UI: o harness é a evidência runtime; PNGs são
  inspeção visual, não prova de teclado.

## Evidência

Comandos finais aprovados:

```text
cleanTest test --no-build-cache (sete classes): BUILD SUCCESSFUL
runGameTestServer: 33 GAME TESTS COMPLETE; BUILD SUCCESSFUL
build: BUILD SUCCESSFUL
```

GameTests L0 funcionais (5):
`projectionMenuCodecRoundTripAndBounds`,
`projectionMalformedFramesFailClosed`,
`projectionReadCompileMatrixMutatesNothing`,
`projectionAuthorityRechecksBecomeStale`,
`projectionMenuBindingInvalidatesAfterTargetChange`.

Persistência L0: 14; total global: 33.

Previews runtime aprovados: `rune-inspector-functional` EN 1024×800,
PT-BR 1024×800 e PT-BR 640×480. Capturas:

- `run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png`

## Escopo

Não houve alteração em ProgramGraph, Guided schema, ProgramStorage, Data
Components, fonte/schema, networking, persistência, migração ou API pública.
ProgramGraph permanece a única autoridade executável.
