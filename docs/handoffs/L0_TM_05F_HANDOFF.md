# L0-TM-05F — handoff para revisão do Sol

## Resultado

**Pronto para revisão técnica; sem alteração de aceite no DELIVERY_BOARD.**

L0-TM-05F fecha a evidência de teste delimitada para Factored Leap. O delta desta tarefa é somente harness/GameTest e testes focados autorizados; não há alteração de API, rede, cliente/UI, `ProgramGraph`, schema, migração ou código de produção funcional nesta correção.

## Fechamento R1–R5

| Requisito | Evidência |
| --- | --- |
| R1 — rota real de menu e conhecimento ausente | `factoredLeapMenuRoutePersistsExactSourceGraphAndResources` constrói `RuneProgrammerMenu`, aciona o botão 37 e concede conhecimento na attachment viva do `ServerPlayer`. `factoredLeapMissingKnowledgeRejectsWithoutMutation` percorre a mesma rota sem a concessão e prova rejeição sem mutação. |
| R2 — persistência exata | O GameTest de sucesso compara a fonte schema-1 codificada, o grafo compilado a partir daquela mesma fonte e `ProgramResources` com os componentes do item; também exige ausência de `PROGRAM_NAME`, `GUIDED_WORKSPACE_STATE` e `PROGRAM_ACTIONS`. |
| R3 — reload/leitura e falhas | `factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess` faz round-trip por `ItemStack.CODEC` com `RegistryOps`/NBT e compara componentes antes/depois de `ScopedProgramPersistence.read` e de leitura do grafo. As variantes stale/cancelada e as seis falhas de commit exigem item byte/componente idêntico ao anterior. |
| R4 — resources vazios | O mesmo GameTest usa uma fonte/registro de teste sem material por meio de `ScopedFunctionalInscriptionService`; após commit aceito exige que `PROGRAM_RESOURCES` esteja ausente, em vez de presente-vazio. |
| R5 — teorema, oráculo e catálogo congelado | `FactoredLeapTheoremTest` cobre AST/binders/De Bruijn, grafo executável, custos, wire schema-1 e isomorfismo semântico. O oráculo usa o mapeamento fixado por Sol (`f0`…`f11`), ignora ordem de nós/arestas e aceita somente a normalização numérica `0/1` → `0.0/1.0`. `ProgramPresetsTest` afirma 34 teoremas, Factored Leap como única adição e hash canônico dos 33 grafos anteriores. |

## Arquivos alterados nesta tarefa

- `src/main/java/com/mathmod/program/L0FactoredLeapGameTests.java` — harness de GameTest L0.
- `src/test/java/com/mathmod/program/FactoredLeapTheoremTest.java`
- `src/test/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPointTest.java`
- `src/test/java/com/mathmod/screen/RuneProgrammerFunctionalTheoremTest.java`
- `src/test/java/com/mathmod/program/ProgramPresetsTest.java`
- `docs/handoffs/L0_TM_05F_HANDOFF.md`

## Autoridade e transação verificadas

`ProgramGraph` continua sendo a única autoridade executável. A fonte canônica é compilada sob conhecimento vivo; o candidato completo é então persistido pela transação interna, que preserva o vínculo fonte/resultado. O precommit revalida os snapshots e as rejeições stale/cancelada ocorrem antes de qualquer escrita. A matriz de falha injeta antes/depois de cada um dos seis componentes e compara o estado de origem de forma exata após rollback.

Não houve migração de leitura, loader/reload externo, nem alteração persistente provocada por leitura. O round-trip de item e as leituras de persistência/grafo são cobertos sem mudança de componentes.

## Vetores focados

O comando focado passou com **48 métodos**:

- `FactoredLeapTheoremTest`: 4
- `ScopedFunctionalInscriptionEntryPointTest`: 2
- `RuneProgrammerFunctionalTheoremTest`: 1
- `ProgramPresetsTest`: 17
- `ScopedSourceEnvelopeTest`: 4
- `ScopedSourceWireCodecTest`: 12
- `ScopedProgramComponentTransactionTest`: 4
- `ScopedProgramPersistenceTest`: 2
- `ServerSideIsolationTest`: 2

Os dez GameTests Factored Leap são:

1. `factoredLeapMenuRoutePersistsExactSourceGraphAndResources`
2. `factoredLeapExecutesForwardAndUpwardOutcome`
3. `factoredLeapMissingKnowledgeRejectsWithoutMutation`
4. `factoredLeapStaleTargetRejectsWithoutMutation`
5. `factoredLeapCancelledRequestRejectsWithoutMutation`
6. `factoredLeapStaleGenerationRejectsWithoutMutation`
7. `factoredLeapStaleKnowledgeRejectsWithoutMutation`
8. `factoredLeapStaleMaterialsRejectsWithoutMutation`
9. `factoredLeapAllCommitFaultsRollbackExactSourceBytes`
10. `factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess`

A família L0 delimitada soma 29 GameTests (10 Factored Leap, 14 de persistência e 5 de projeção); o servidor descobriu **43 GameTests globais**, todos aprovados. O total global é reportado separadamente e não foi usado como substituto da contagem L0.

## Comandos finais

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --tests com.mathmod.program.FactoredLeapTheoremTest --tests com.mathmod.program.ScopedFunctionalInscriptionEntryPointTest --tests com.mathmod.screen.RuneProgrammerFunctionalTheoremTest --tests com.mathmod.program.ProgramPresetsTest --tests com.mathmod.program.ScopedSourceEnvelopeTest --tests com.mathmod.program.ScopedSourceWireCodecTest --tests com.mathmod.program.ScopedProgramComponentTransactionTest --tests com.mathmod.program.ScopedProgramPersistenceTest --tests com.mathmod.ServerSideIsolationTest
# BUILD SUCCESSFUL (34 s)

.\gradlew.bat runGameTestServer --no-daemon
# BUILD SUCCESSFUL (34 s); 43/43 GameTests, 1.821 s

.\gradlew.bat build --no-daemon
# BUILD SUCCESSFUL (21 s)
```

Uma primeira invocação de `build` ficou presa em launchers Gradle sem CPU; os dois PIDs explicitamente identificados como launchers daquele `build` foram encerrados e o mesmo comando foi repetido com `--no-daemon`, concluindo verde. Não houve processo de jogo interrompido nem alteração de artefatos do projeto por essa recuperação.

Após os três comandos acima, uma repetição **redundante** do comando focado com `--rerun-tasks`, feita apenas para regenerar XMLs de contagem, excedeu o limite externo de 124 s sem resultado de teste; seus dois launchers Gradle identificados foram encerrados. Ela não substitui nem invalida a execução focada verde já registrada acima.

## Limitações e escalations

- O mock de `ServerPlayer` do GameTest não suporta enviar `neoforge:sync_attachments`; o harness tolera exclusivamente essa exceção de sincronização posterior à gravação/conclusão esperada. Não a trata como êxito de inscrição.
- Não há teste de cliente/reconexão ou inspeção de pacote de feedback; esta tarefa não reivindica esse comportamento fora do servidor/GameTest.
- O teste unitário de `ScopedFunctionalInscriptionEntryPoint` permanece estrutural, pois o classpath JUnit isolado não carrega `InteractionHand`; a invocação real do menu é coberta no GameTest R1.
- A divergência nominal dos nós do grafo foi resolvida pela clarificação do oráculo do Sol, sem expansão de ownership. Não há escalation pendente.
