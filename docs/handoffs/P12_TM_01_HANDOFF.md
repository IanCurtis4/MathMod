# P12-TM-01 — blocked: P8 survival-boundary evidence

## Resultado

**BLOCKED por dois defeitos reprodutíveis de produção.** Nenhum arquivo de produção foi alterado. Conforme `P12_FOUNDATION_BETA_COMPLETION_CONTRACT.md`, a correção não prossegue enquanto Sol não autorizar ownership de produção.

## Delta realizado

- `src/main/java/com/mathmod/program/P8GameTests.java` — sete vetores P12 exigidos, sem alterar comportamento de produção.
- `docs/handoffs/P12_TM_01_HANDOFF.md` — este registro de bloqueio.

`P8AuthoritySurfaceTest.java` não foi criado: a execução dos vetores obrigatórios encontrou defeitos de produção; o contrato ordena parar e escalar nesse ponto, em vez de apresentar uma prova parcial como fechamento P12.

## Vetores P8

Os três nomes preexistentes foram preservados:

1. `fillRollbackRestoresEscrowAfterCommitFailure` — passou no servidor.
2. `fillAdmissionFailureConsumesNothing` — passou no servidor.
3. `constructBlockCollisionCapturesSnapshotAndDoesNotMutateTerrain` — passou no servidor.

Os sete novos vetores foram adicionados e executados:

4. `fillUnloadedCandidateFailsClosedWithoutLoadingOrConsumption` — passou.
5. `fillFluidCandidateFailsClosedWithoutMutationOrConsumption` — passou.
6. `fillBlockEntityCandidateFailsClosedWithoutMutationOrConsumption` — passou.
7. `fillProtectionDenialNeverCommitsOrConsumes` — passou.
8. `constructUnloadedFlightStopsWithoutTicketOrTerrainMutation` — **falhou**.
9. `constructSecondLaunchForSameOwnerFailsClosed` — passou.
10. `constructRejectsUnboundedMotionAndUsesServerDerivedBodyCost` — **falhou**.

O servidor registrou 50 GameTests globais (a contagem P8 pretendida passou de 3 para 10), com 48 aprovados e os dois vetores acima reprovados. O total global não é usado como substituto da matriz P8.

## Bloqueadores reprodutíveis

### P12-P8-BOUND-01 — velocidade não-finita aceita

`constructRejectsUnboundedMotionAndUsesServerDerivedBodyCost` chama a rota real:

```text
ConstructFlightManager.launch(player, body, player.position(), new Vec3(NaN, 0, 0))
```

O GameTest falha na asserção de que o lançamento deve retornar `false`:

```text
non-finite, zero, or over-limit motion must reject: (NaN, 0.0, 0.0)
```

O defeito é coerente com a guarda atual de `ConstructFlightManager.launch`: comparações `NaN > 2.0` e `NaN <= EPSILON` são ambas falsas, portanto a rota aceita a velocidade. Este é um `BOUND_FAILURE`; a asserção falha antes de poder classificar pagamento/flight-state posteriores como aprovados.

### P12-P8-PROTECTION-01 — voo no limite descarregado não é descartado

`constructUnloadedFlightStopsWithoutTicketOrTerrainMutation` localiza um bloco carregado cujo vizinho leste não está carregado, confirma essa pré-condição, lança um corpo de uma unidade na borda e executa `tickServer` duas vezes.

O GameTest falha em:

```text
flight approaching unloaded terrain must be discarded
```

Logo, o voo continua ativo depois de alcançar a fronteira descarregada. As asserções posteriores de chunk/terreno não são executadas após essa falha e, portanto, este handoff não alega que ticket, carregamento ou mutação foram aprovados. Este é um `PROTECTION_FAILURE` até que a rota seja corrigida e todas as observações pós-condição passem.

## Observações antes/depois incorporadas aos vetores

- fills: snapshot de inventário; para fluido, estado de bloco; para block entity, estado e `saveWithFullMetadata`; para proteção, contador de callback de commit; para descarregado, estado `hasChunkAt` antes/depois;
- flights: contagem de flights ativos, versão do primeiro flight, inventário e estado de bloco; o vetor descarregado confirma a pré-condição de chunk descarregado antes do lançamento;
- o vetor de movimento limpa `ConstructFlightManager` em `finally`, mesmo quando a asserção de regressão falha, para não contaminar outros GameTests.

## Comandos executados

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat compileJava --no-daemon --no-build-cache
# BUILD SUCCESSFUL

.\gradlew.bat runGameTestServer --no-daemon
# BUILD FAILED: 50 GameTests; 2 falhas nomeadas acima
```

Uma primeira tentativa de GameTest excedeu o limite porque o fixture de teste procurava uma posição carregada mantendo uma coordenada distante. Foram encerrados apenas os processos Gradle dessa tentativa; o fixture foi corrigido para partir do chunk da estrutura. A execução seguinte terminou normalmente e produziu as duas falhas de produção acima.

Os comandos focado, build e a prova negativa de payload não foram executados: fazê-los não fecharia os dois vetores runtime reprovados e o contrato exige parar diante do defeito de produção.

## Escalation solicitada ao Sol

Autorizar, no mínimo, uma correção de produção em:

```text
src/main/java/com/mathmod/program/ConstructFlightManager.java
```

O escopo mínimo deve:

1. rejeitar explicitamente componentes de velocidade não-finitos antes de qualquer pagamento ou criação de flight;
2. impedir que uma trajetória atravesse/avance para chunk descarregado, descartando-a sem carregamento/ticket ou mutação;
3. preservar os limites atuais, pagamento derivado de `ConstructBody.massEquivalent`, uma-flight-per-owner e ausência de mutação de terreno.

Após essa decisão, Terra Medium deve rerodar os dez P8 GameTests, criar a prova `P8AuthoritySurfaceTest`, executar o comando focado, os 50 GameTests e o build antes de qualquer pedido de aceite.
