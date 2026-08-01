# L0-TM-05 Handoff

## Resultado

Implementação pronta para revisão de Sol. A correção autorizada do blocker
normaliza valores ausentes do candidato transacional; os 43 GameTests e a
bateria focada passam.

## Causa e correção do blocker

`ScopedFunctionalInscriptionService` construía um candidato ausente com
`name=""`/`hasName=false` (e poderia reter lista vazia de recursos). A
transação remove o componente e recaptura `null`, rejeitando corretamente a
igualdade exata antes da primeira mutação. O serviço agora usa `null` quando
`PROGRAM_NAME` ou `PROGRAM_RESOURCES` estão ausentes, preservando as flags,
rechecks e a única máquina de transação.

## Evidência funcional

- Fonte canônica: 113 passos; 12 nós, 12 arestas; orçamento 21/24.
- Persistência usa o grafo compilado da fonte, envelope schema-1 e recursos
  recomendados; `PROGRAM_NAME` permanece ausente.
- O candidato sobrevive captura direta e `ItemStack.copy`.
- As 12 injeções BEFORE/AFTER dos seis componentes atingem a transação e
  restauram o estado completo, incluindo bytes de envelope de schema futuro.
- O vetor de execução comprova o delta para frente e para cima. O mock player
  pode lançar o erro conhecido `sync_attachments` somente depois do efeito;
  o teste o reconhece e ainda exige o delta real.

## GameTests L0-TM-05 (10)

`factoredLeapMenuRoutePersistsExactSourceGraphAndResources`,
`factoredLeapExecutesForwardAndUpwardOutcome`,
`factoredLeapMissingKnowledgeRejectsWithoutMutation`,
`factoredLeapStaleTargetRejectsWithoutMutation`,
`factoredLeapCancelledRequestRejectsWithoutMutation`,
`factoredLeapStaleGenerationRejectsWithoutMutation`,
`factoredLeapStaleKnowledgeRejectsWithoutMutation`,
`factoredLeapStaleMaterialsRejectsWithoutMutation`,
`factoredLeapAllCommitFaultsRollbackExactSourceBytes`,
`factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess`.

Contagens: 10 novos L0, 29 L0 totais, 43 GameTests globais.

## Arquivos TM alterados

- Produção: `FactoredLeapTheorem`, `ScopedFunctionalInscriptionEntryPoint`,
  `ScopedFunctionalInscriptionService` (amendment Sol), `ProgramPresets`,
  `RuneProgrammerMenu`, `L0FactoredLeapGameTests`.
- Testes: `FactoredLeapTheoremTest`,
  `ScopedFunctionalInscriptionEntryPointTest`,
  `RuneProgrammerFunctionalTheoremTest`, `ProgramPresetsTest`.
- Documentação: este handoff.

Não houve alteração em `ScopedProgramComponentTransaction`, API pública,
networking, cliente/UI Java, ProgramGraph, schemas, Data Components, migração
ou leitura persistente. Patchouli continua fora do escopo TM.

## Comandos

Todos concluíram com sucesso:

```powershell
./gradlew.bat cleanTest test --no-build-cache [9 classes focadas]
# 45 testes, BUILD SUCCESSFUL
./gradlew.bat runGameTestServer --no-daemon
# All 43 required tests passed
./gradlew.bat build --no-daemon
# BUILD SUCCESSFUL
```

## Limitações e escalations

O GameTest usa o mock player do NeoForge; após o efeito bem-sucedido ele pode
falhar ao sincronizar `neoforge:sync_attachments`. Isto é delimitado no vetor
e não representa falha do executor ou sucesso falso. Nenhuma escalation
pendente para L0-TM-05.
