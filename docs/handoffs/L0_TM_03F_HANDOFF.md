# L0-TM-03F Handoff

## Resultado

Implementada a correção delimitada para autoridade viva de conhecimento, janela
de precommit, transação de seis componentes, retorno de falha de commit e limite
externo do envelope. O build e as provas executadas estão verdes. Este handoff
não altera o estado de aceite no board.

## R1–R6

- **R1:** `ScopedCommitAuthority` agora recebe `Supplier<PlayerKnowledge>`;
  o coordenador captura a instância para compilação e a lê novamente no limite
  final. `staleLiveKnowledgeMutatesNothing` prova `KNOWLEDGE_STALE` sem mutação.
- **R2:** fonte canônica, nome, recursos e `State` candidato são todos
  construídos antes de todos os rechecks; cancelamento, identidade/item/cópia,
  seis componentes, geração, snapshot/knowledge e materiais são rechecados
  imediatamente antes de `apply`.
- **R3:** `ScopedProgramComponentTransaction` constrói patches forward/rollback,
  aplica o candidato primeiro a uma cópia e usa uma única máquina para produção
  e testes. A injeção cobre `BEFORE` e `AFTER` nos índices 0–5; rollback compara
  os seis componentes (incluindo igualdade byte-content do envelope). Falha de
  rollback gera `Logger.severe` e o resultado permanece falha.
- **R4:** as duas rotas `ProgramStorage.saveValidated*` convertem falha da
  transação em `ValidationResult` com erro; não podem expor inscrição válida.
- **R5:** o codec persistente usa `Codec.BYTE.listOf(0, 262_144)`; o vetor do
  codec aceita 262.144 elementos e rejeita 262.145 antes da construção do
  envelope/payload completo.
- **R6:** os dois arquivos focados autorizados foram criados. A execução
  filtrada contém **22 métodos JUnit**, todos aprovados. O servidor dedicado
  descobriu **28 GameTests globais**; os **14 L0** são:
  `validFutureAndConflictReadsNeverRewriteGraph`,
  `existingInscriptionRoutesClearScopedSourceAtomically`,
  `injectedBeforeAndAfterComponentFailuresRestoreCompleteSnapshot`,
  `cancelledFunctionalRequestMutatesNothing`,
  `cancellationAfterPureCompilationMutatesNothing`,
  `staleLiveKnowledgeMutatesNothing`,
  `staleRuneGenerationMutatesNothing`,
  `staleMaterialCatalogMutatesNothing`,
  `functionalSuccessWritesCompleteStateAndClearsGuided`,
  `staleFunctionalTargetMutatesNothing`,
  `explicitClearRemovesCompleteProgramState` e
  `storageCommitFailureIsNeverReportedValid`,
  `completeReadStateMatrixNeverMutatesOrCompiles` e
  `itemCodecRoundTripRetainsOpaqueUnreadableAndFutureBytes`.

## Arquivos alterados nesta correção

Produção: `ScopedCommitAuthority.java`, `ScopedSourceWireCodec.java`, `ScopedFunctionalInscriptionService.java`,
`ScopedProgramComponentTransaction.java`, `ScopedSourceEnvelope.java`,
`ProgramStorage.java`, `L0ScopedSourcePersistenceGameTests.java`.

Testes: `ScopedSourceEnvelopeTest.java`, `ScopedSourceWireCodecTest.java`,
`ScopedProgramComponentTransactionTest.java`,
`ScopedProgramPersistenceTest.java`.

Documentação: este arquivo.

## Desenho final

`ScopedFunctionalInscriptionService` preserva o binding interno source/result.
Ele captura alvo, cópia, seis valores e knowledge do servidor; compila aquela
fonte exata; constrói o candidato off-item; e somente então revalida todas as
autoridades. A transação pré-valida a mesma patch completa em cópia e aplica o
alvo através da ordem fixa program/source/name/resources/guided/actions. Em
falha, restaura a patch capturada e verifica igualdade exata. `ProgramGraph`
permanece a única autoridade executável.

Os vetores focados incluem cópia/equality, schemas signed, limite externo
262.144/262.145, formato canônico, UTF-8/campos/tags/inteiros, limites de
strings/argumentos/AST/budget/tipo/binding/application e classificação de
leitura. O GameTest de falha percorre 12 pontos (antes e depois de cada um dos
seis componentes), verificando snapshot completo e bytes do source. Os casos
stale de cancelamento, alvo e knowledge verificam ausência de mutação.

## Comandos e resultados

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --tests com.mathmod.program.ScopedSourceEnvelopeTest --tests com.mathmod.program.ScopedSourceWireCodecTest --tests com.mathmod.program.ScopedProgramComponentTransactionTest --tests com.mathmod.program.ScopedProgramPersistenceTest
```

`BUILD SUCCESSFUL`; 22 JUnit, 0 falhas/skips.

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

`BUILD SUCCESSFUL`; o log do servidor dedicado registra `All 28 required tests
passed` (28 global; 14 L0 nomeados acima).

```powershell
.\gradlew.bat build
```

`BUILD SUCCESSFUL`; 31 tarefas acionáveis, 2 executadas, 29 up-to-date.

## Não reivindicado / limitações

Não há migração, reparo, canonicalização, compilação ou alteração persistente
durante leitura. Não foram alterados networking, cliente, menus, UI,
`ProgramGraph`, `GuidedWorkspaceState`, schema, identidade do Data Component ou
API pública. A prova é de servidor dedicado/item codec; não reivindica cliente,
reconnect nem visibilidade de rede. Nenhuma escalation de autoridade foi
necessária.

## Complemento R6 após revisão Sol

O codec agora tem vetores para BOM, truncamento, multi-root, campos ausentes e
tipos errados, além dos limites multibyte; o caso de `kind` ausente revelou e
fechou um `NullPointerException`, substituído por diagnóstico legível. Os
contadores reais privados são exercitados em 4.096/4.097 valores e
1.024/1.025 tipos porque a gramática válida atinge antes limites estruturais
menores. A política de recursos é testada para preservar exatamente a seleção
anterior apenas com grafo igual e usar recomendações no caminho alterado.

O limite de profundidade de expressão 256/257 é exercitado diretamente no
contador `Limits.expression`: uma representação JSON equivalente atinge antes
o limite de containers. Para literal/binder hint, os limites de comprimento
Java tornam algumas bordas UTF-8 nominais inalcançáveis; os testes registram
essa precedência explicitamente (inclusive 160/161 caracteres de três bytes),
em vez de alegar uma aceitação inexistente. Há vetores ativos de blank e
whitespace para rune id, input name, type id e binder hint, impedindo trim ou
default por construtores.

O mesmo precedente está ativo para binder hints: 32 caracteres de três bytes
são aceitos e 33 retornam `SOURCE_FIELD_INVALID`; portanto o limite Java de 32
caracteres é comprovadamente anterior à borda UTF-8 nominal inalcançável.

Os GameTests suplementares provam cancelamento após compilação, geração de rune
stale, catálogo de materiais stale, invalidação de `ProgramStorage` após falha
injetada e matriz de leitura source-only/ausente/conflito para payload válido,
ilegível e futuro. Todos verificam ausência de mutação; leitura não compila,
migra ou reescreve componentes.
