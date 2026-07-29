# Revisão Semântica do Adapter Legado A0-TH-02

**Tarefa:** A0-TH-02  
**Revisor:** Terra High  
**Escopo:** revisão read-only do delta A0-TM-02.  
**Decisão:** **APPROVE** para o gate A0-SOL-03.

## Conclusão

O adapter legado é uma fronteira interna, limitada e determinística que reproduz a expansão caracterizada sem alterar identidade persistente, schema, autoridade de execução ou ProgramGraph. A evidência cobre todos os 67 forms em defaults, formas numéricas representativas, sequência incremental e falhas fechadas de form desconhecido e mismatch de grafo.

O adapter não está conectado ainda aos fluxos de UI, rede ou persistência; isso é uma limitação declarada e não uma rota alternativa de execução. O trabalho de A0-4/A0-5 continua necessário. Não encontrei bloqueador semântico para o gate A0-3.

## Materiais revisados

- docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md, seções 6.3, 6.6, 10, 11.3–11.4, 12.4–12.6, 16/A0-3 e 17.3–17.5
- docs/A0_METADATA_FOUNDATION_GATE_ACCEPTANCE.md
- docs/handoffs/A0_TM_02_HANDOFF.md
- docs/A0_POST_ADAPTER_DELIVERY_PLAN.md, tarefa 2
- docs/DELIVERY_BOARD.md
- src/main/java/com/mathmod/authoring/AuthoringMetadata.java
- src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
- src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java
- src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java
- src/main/java/com/mathmod/program/CustomSpellAction.java
- src/main/java/com/mathmod/program/CustomSpellInvocation.java
- src/main/java/com/mathmod/program/CustomSpellWorkspace.java
- src/main/java/com/mathmod/runes/ProgramGraph.java

O handoff registra sucesso da suíte focada do adapter, da suíte authoring e do build com o cache Gradle isolado aceito em C:\codex-gradle-a0. Esta revisão não alterou Java nem reexecutou build.

## Verificação obrigatória

| Critério | Resultado | Evidência |
| --- | --- | --- |
| Mapeamento dos 67 forms | Conforme | everyBuiltInDefaultFormProducesExactlyTheLegacyGraph percorre CustomSpellAction.values(); cada caso passa por canonicalInvocation, que exige form no snapshot e adapter id igual ao mapeamento built-in. Uma ausência ou id divergente falha antes da igualdade. |
| Form id para adapter id | Conforme | BUILT_IN_ADAPTER_IDS é criado a partir da snapshot built-in congelada e compara o adapterId esperado antes de expandir. |
| Defaults | Conforme | Cada uma das 67 ações é expandida com Map.of(); CustomSpellInvocation e descriptor usam seus defaults atuais. |
| Missing key | Conforme | Map.of() para todas as formas, inclusive NUMBER_ONE, FINITE_DIFFERENCE e SIMPSON_INTEGRAL, aciona default por descriptor. |
| Unknown key | Conforme | O vetor NUMBER_ONE inclui unknown; canonicalArguments só emite chaves declaradas e CustomSpellInvocation também sanitiza somente parâmetros declarados. |
| Não finitos | Conforme quanto à semântica | Descriptor canonicaliza não finito para default; o teste do adapter cobre NaN e +infinito, e a base A0 cobre também -infinito. |
| Out of range | Conforme | finite difference inclui start abaixo do mínimo; descriptor faz clamp inclusivo, igual ao CustomNumericParameter legado. |
| Parâmetros múltiplos | Conforme | finite difference e Simpson integral são comparados com valores fornecidos em todos os campos, preservando ordem declarada dos descriptors. |
| Ordem de nodes/edges | Conforme | ProgramGraph é record; igualdade compara listas de nodes/edges na ordem, além de outputNodeId e budgetLimit. O adapter compara equals, não isomorfismo, normalização ou custo. |
| Sequência representativa | Conforme | A sequência crescente SELF → LOOK_VECTOR → RAYCAST → RAY_HIT_POSITION → BLINK → NUMBER_ONE → ADD_ONE → SPHERE_REGION → NEARBY_LIVING → PUSH_TARGETS_PLAN → EXECUTE_PLAN é comparada após cada prefixo. |
| Exact graph equality | Conforme | replayExactly cria workspace novo, aplica cada invocation canonicalizada, chama toGraph e falha se authoritativeGraph.equals(replayed) for falso. |
| Form desconhecido | Conforme, falha fechada | snapshot.find falha com UNKNOWN_FORM antes de mutar workspace; teste cobre mathmod:not_a_form. |
| Replay mismatch | Conforme, falha fechada | grafo de NUMBER_ONE é apresentado contra replay de SELF; ReplayMismatch é lançado, sem normalização ou reparo. |
| Adapter id não persistido | Conforme | CustomSpellInvocation persiste somente action.persistentId e argumentos hex; GuidedWorkspaceState armazena invocationIds. Adapter ids só existem em metadata e no mapa interno do adapter. |
| Dependências proibidas | Conforme por inspeção | Adapter importa apenas tipos de authoring, workspace/invocation/action, ProgramGraph, NamespacedId e coleções Java. Não depende de player, level, item, rede, relógio, random, arquivo, command, callback ou executor. |
| Limites | Conforme no adapter | replayExactly limita invocations a 128, igual ao limite da workspace Guided; parâmetros são limitados e canonicalizados pela metadata aceita. |
| Persistência e schema | Conforme | Nenhuma alteração em GuidedWorkspaceState, ProgramGraph, Data Components, codecs ou payloads; schema Guided permanece 1. |

## Matriz de canonicalização

| Caso | Caminho do adapter | Resultado |
| --- | --- | --- |
| Sem chave declarada | source.getOrDefault(parameter.key, default) | default do descriptor; depois sanitização legada idêntica |
| Chave desconhecida | canonicalArguments percorre somente form.parameters | ignorada; não entra em CustomSpellInvocation nem persistência |
| NaN | Parameter.canonicalize | default finito declarado |
| +infinito | Parameter.canonicalize | default finito declarado |
| -infinito | Parameter.canonicalize | default finito declarado; comportamento compartilhado com CustomNumericParameter |
| Finito abaixo do mínimo | NumberConstraints.canonicalize | mínimo inclusivo |
| Finito acima do máximo | NumberConstraints.canonicalize | máximo inclusivo |
| Finito no intervalo | NumberConstraints.canonicalize | valor preservado |
| Form sem parâmetros | iteração vazia | invocation sem argumentos; identidade continua sendo só form id |

A ordem de emissão do mapa canonicalizado é a ordem de parameters da Form, preservada em LinkedHashMap. A persistência de invocations continua usando a ordem de parâmetros da ação legada e Double.toHexString; nenhum adapter id é incluído.

## Avaliação de replay

A comparação usa igualdade de record de ProgramGraph. Isso exige igualdade de:

- ordem e conteúdo da lista de nodes;
- ordem e conteúdo da lista de edges;
- ids de node;
- rune ids e constantes;
- nomes de input;
- outputNodeId;
- budgetLimit.

Assim, a evidência não aceita equivalência semântica fraca, renomeação, isomorfismo ou normalização. A criação de CustomSpellWorkspace novo também evita que estado parcial de uma tentativa anterior contamine a prova de igualdade.

## Avaliação de falhas fechadas

- Form não presente na snapshot: nenhuma invocation é aplicada.
- Form sem adapter id esperado ou com adapter id divergente: nenhuma invocation é aplicada.
- Mais de 128 invocations: replay é recusado antes de criar/aplicar a lista completa.
- Replay diferente: ReplayMismatch encerra a operação; não há fallback para enum, substituição, replay parcial ou alteração do grafo autoritativo.

A integração de UI deverá converter UNKNOWN_FORM e GRAPH_REPLAY_MISMATCH em diagnóstico de workspace não-replayable e manter inspeção somente leitura, conforme seções 11.3 e 12.4–12.5. Essa integração não faz parte desta implementação isolada.

## Vetores adversariais ainda recomendados

Nenhum deles bloqueia o gate atual, mas devem entrar em A0-5 ou no teste do adapter quando o fluxo de Guided for integrado:

1. -infinito como argumento bruto do adapter, não somente no teste de metadata.
2. Overflow positivo bruto para cada forma numérica, não apenas underflow de finite difference.
3. Formas com múltiplos parâmetros e somente uma chave ausente, para confirmar defaults independentes.
4. Exatamente 128 invocations aceitas e 129 recusadas com diagnóstico de limite estável.
5. Adapter id divergente em snapshot candidata, para confirmar UNKNOWN_ADAPTER antes de workspace mutation.
6. Invocation persistida com form legacy qualificado, não qualificado e alias quando a rota de leitura A0 for conectada.
7. Mismatch em constante, node id, ordem de node, ordem de edge, input name, output e budget individualmente, para caracterizar a igualdade exata.
8. Falha no meio de uma lista de invocations contra uma workspace pré-existente, verificando que o fluxo integrado mantém a workspace/item anterior intacto.

## Questões para Sol

1. Aceitar este APPROVE e registrar o gate A0-SOL-03, liberando a declaração de ownership de tela para A1 read-only.
2. Fixar a superfície de diagnóstico player-facing para UNKNOWN_FORM e GRAPH_REPLAY_MISMATCH quando A0-4/A0-5 conectar o adapter à leitura Guided; a implementação atual é intencionalmente interna e não define protocolo de UI.
3. Manter a exigência de vetores de save/reload/reconnect e de atomicidade de item para A0-5; a igualdade do adapter não substitui essa evidência.

## Recomendação

**APPROVE.** O adapter A0-TM-02 satisfaz a fronteira confiável de expansão legada e a prova de replay exato exigidas para o gate A0-SOL-03.

A aprovação não libera automaticamente A1 ou A0-4: Sol deve primeiro aceitar formalmente o gate e atribuir os arquivos de tela conforme o plano pós-adapter.

## Limites da revisão

Nenhum arquivo Java, persistência, schema, Data Component, codec, payload, rede, ProgramGraph, GuidedWorkspaceState ou tela foi alterado nesta tarefa.

