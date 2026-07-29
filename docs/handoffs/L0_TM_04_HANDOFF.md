# L0-TM-04 — handoff de implementação (NÃO PRONTO PARA ACEITE)

## Resultado

Foi implementada a fundação da projeção funcional somente-leitura: DTO imutável,
codec de frame limitado, captura no servidor no caminho de abertura do menu e
apresentação sem rota C2S. Esta entrega **não solicita aceite**. A cobertura
obrigatória de transporte e rechecks no servidor não pode ser concluída dentro
do ownership congelado atual.

## Implementado

- `ScopedFunctionalProjection` contém schema 1, estados de fonte/tentativa/
  grafo/relação, linhas, diagnósticos e passos cobrados, com todos os limites
  de cardinalidade, UTF-8, profundidade, binding e passos.
- `ScopedFunctionalProjectionService.openingSnapshot` captura item/hand,
  conhecimento vivo, geração de runas, definições de conhecimento e materiais;
  constrói o candidato e revalida tudo imediatamente antes de aceitar o
  snapshot. Qualquer mudança retorna `STALE`, sem linhas funcionais.
- `ProgrammedTalismanItem` usa somente o buffer já existente de abertura do
  menu. Não há payload, registro de rede, C2S nem fonte crua no fio.
- `ScopedFunctionalProjectionWireCodec` primeiro codifica em buffer temporário
  limitado a 65.536 bytes; overflow converte-se em snapshot indisponível no
  chamador. O decoder limita tamanho antes de alocar listas/textos e rejeita
  enums, limites, truncamento e bytes residuais inválidos.
- O Inspector mostra as três superfícies rotuladas: fonte autoral não
  executável, forma verificada de binding canônico não persistida e grafo como
  autoridade executável. Não foi adicionada ação de editar, salvar, compilar,
  reparar ou migrar.

## Arquivos alterados neste slice

Produção: `ProgrammedTalismanItem.java`, `RuneProgrammerMenu.java`,
`RuneProgrammerScreen.java`, `RuneInspectorScreen.java`, `UiPreviewHarness.java`,
`UiPreviewMatrix.java`, os dois locale JSONs e os novos
`ScopedFunctionalProjection.java`, `ScopedFunctionalProjectionService.java` e
`ScopedFunctionalProjectionWireCodec.java`.

Testes: `ScopedFunctionalProjectionTest.java` (2),
`ScopedFunctionalProjectionWireCodecTest.java` (2) e
`RuneProgrammerProjectionTest.java` (1).

Documentação: `UI_PREVIEWS.md` e este handoff.

## Evidência executada

O comando focado obrigatório passou em 2026-07-28:

```text
cleanTest test --no-build-cache [sete classes exigidas]  PASS
```

Contagens declaradas: projeção 2; codec 2; menu 1; apresentação 4; fonte da
tela 3; matriz de preview 4; isolamento de servidor 2.

`runGameTestServer --no-daemon` iniciou e seus logs registram `28 tests are now
running` e `All 28 required tests passed`. O wrapper externo atingiu timeout
depois de o log registrar o desligamento normal do servidor; por isso o comando
não tem exit code verde utilizável como prova independente. O scan estático
encontrou 15 métodos L0 em `L0ScopedSourcePersistenceGameTests` e 35 anotações
`@GameTest` no código; o total efetivamente descoberto/executado pelo servidor
foi 28. Nenhum GameTest novo de L0-TM-04 foi autorizado.

`gradlew.bat build` passou após a bateria focada.

## Escalada necessária antes do aceite

O contrato exige vetores de servidor para: round-trip real do codec de menu,
frame exatamente 65.536/65.537, dados malformados/truncados/residuais, uma
compilação para fonte válida, ausência de compilação nos demais estados,
mudança de item/hand/conhecimento/geração/definições/materiais e igualdade
byte-exata de todos os componentes antes/depois. Os três testes JUnit novos
autorizados rodam sem o classpath de `FriendlyByteBuf`/menu do Minecraft; uma
tentativa de round-trip ali falhou por `ClassNotFoundException`, não por uma
falha do codec. O único local existente de GameTests L0,
`L0ScopedSourcePersistenceGameTests.java`, está fora do ownership L0-TM-04.

É necessária autorização explícita para criar/alterar um GameTest de L0-TM-04
no servidor (ou autorização equivalente para incluir o classpath Minecraft nos
testes focados). Sem ela, não é correto afirmar a evidência obrigatória nem
pedir ACCEPT.

## Não reivindicado

Não há migração, escrita durante leitura, alteração de Data Component, mudança
de `ProgramGraph`, `GuidedWorkspaceState`, `ProgramStorage`, networking, API
pública, loader/reload externo, UI mutável funcional ou avanço de L0-TM-05.
