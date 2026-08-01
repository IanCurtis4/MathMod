# L0-TM-05F2 — residual theorem evidence correction

## Resultado

**Pronto para revisão do Sol; nenhum estado de aceite foi alterado.**

Esta é uma correção exclusivamente de testes/harness. Ela fecha individualmente os dois resíduos de `L0-TM-05F`: o oráculo semântico deixa de depender de ids gerados, e a rota real do menu passa a ter observação de feedback e sincronização no `EmbeddedChannel` do mock.

## F-R1 — matcher semanticamente bijetivo

`FactoredLeapTheoremTest.assertSemanticIsomorphism` não contém mais o mapeamento `f0`…`f11` nem deriva identidade por `node.id()`. O matcher faz busca de bijeção usando apenas:

- identidade da runa;
- chaves e significado das constantes; para `constant_number.value`, valores finitos numericamente equivalentes são aceitos (`0`/`0.0`, `1`/`1.0`);
- conectividade de sockets nomeados;
- nó de saída;
- unicidade do `self_player` compartilhado.

O vetor `semanticMatcherRejectsAdversarialGraphsWhileIgnoringNamesAndOrder` prova que renomear todos os nós compilados e reordenar nós/arestas preserva equivalência. Ele também exige falha para nó ou aresta ausente/adicional, NUMBER alterado, socket alterado, saída alterada e `self_player` duplicado.

## F-R2 — leitura equivalente a tooltip e ausência observável de falso sucesso

`factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess` agora executa `ProgrammedTalismanItem.appendHoverText` sobre o item Factored Leap reconstituído via `ItemStack.CODEC`; a lista de tooltip é produzida e o item permanece componente-a-componente idêntico ao snapshot anterior.

O harness instala um `ChannelOutboundHandlerAdapter` no `EmbeddedChannel` real criado por `GameTestHelper.makeMockServerPlayerInLevel()`, antes do encoder. Portanto observa objetos de pacote de servidor, não texto-fonte:

- a rota real de menu sem conhecimento não emite `ClientboundSystemChatPacket` cuja chave seja `item.mathmod.programmed_talisman.saved`, nem `ClientboundContainerSetSlotPacket` de sincronização de sucesso;
- uma falha `BEFORE` injetada no componente 0 percorre botão 37 na `RuneProgrammerMenu` real, dispara o injetor, preserva o item e também não emite esse feedback nem a sincronização de sucesso;
- a matriz direta existente conserva os 12 pontos (antes/depois × seis componentes) e o rollback exato.

## Arquivos alterados

- `src/main/java/com/mathmod/program/L0FactoredLeapGameTests.java` — somente harness/GameTests autorizados.
- `src/test/java/com/mathmod/program/FactoredLeapTheoremTest.java` — matcher e vetor adversarial autorizados.
- `docs/handoffs/L0_TM_05F2_HANDOFF.md`.

`RuneProgrammerFunctionalTheoremTest` permaneceu sem mudança. Não houve alteração em Java de produção funcional, schema, rede, UI, APIs, `ProgramGraph`, persistência ou migração.

## Comandos reproduzidos

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --no-daemon --tests com.mathmod.program.FactoredLeapTheoremTest --tests com.mathmod.program.ScopedFunctionalInscriptionEntryPointTest --tests com.mathmod.screen.RuneProgrammerFunctionalTheoremTest --tests com.mathmod.program.ProgramPresetsTest --tests com.mathmod.program.ScopedSourceEnvelopeTest --tests com.mathmod.program.ScopedSourceWireCodecTest --tests com.mathmod.program.ScopedProgramComponentTransactionTest --tests com.mathmod.program.ScopedProgramPersistenceTest --tests com.mathmod.ServerSideIsolationTest
# BUILD SUCCESSFUL (14 s)

.\gradlew.bat runGameTestServer --no-daemon
# BUILD SUCCESSFUL (32 s); 43/43 GameTests em 1.568 s

.\gradlew.bat build --no-daemon
# BUILD SUCCESSFUL (20 s)
```

O comando focado agora contém **49 testes**: os 48 reproduzidos por Sol em F mais o novo vetor adversarial de `FactoredLeapTheoremTest`. O servidor ainda descobre **43 GameTests globais**; a família L0 delimitada continua sendo 29 (os mesmos 10 Factored Leap, 14 de persistência e 5 de projeção), e o total global não foi usado como substituto da contagem L0.

## Limites e não alegações

- A captura observa precisamente os pacotes de feedback de sistema e de sincronização de slot enviados pela rota de menu no mock servidor. Não reivindica cliente gráfico, reconexão ou uma sessão de rede remota.
- A exceção `sync_attachments` da attachment de conhecimento do mock continua limitada à preparação anterior à captura; ela não é tratada como sucesso de inscrição.
- Não há escalation pendente: o `EmbeddedChannel` fornecido pelo próprio `GameTestHelper` ofereceu observação suficiente sem nova seam de produção.
