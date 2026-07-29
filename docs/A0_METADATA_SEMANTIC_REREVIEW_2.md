# Re-revisão Semântica A0-TH-01R2 — Limite de Presentations

**Tarefa:** A0-TH-01R2 (9D)  
**Revisor:** Terra High  
**Escopo:** delta A0-TM-01P somente; nenhuma alteração Java nesta revisão.  
**Decisão:** **APPROVE para A0-3**, sujeito à aceitação deste gate por Sol.

## Resultado

O único bloqueador aberto pela re-revisão anterior foi fechado.

AuthoringMetadata define agora MAX_RUNE_PRESENTATIONS = 2.048 e o construtor imutável de Snapshot aplica esse máximo sobre runePresentations antes de devolver uma snapshot. O teste de fronteira prova aceitação em 2.048 e rejeição estruturada em 2.049, sem snapshot retornada. A inspeção do delta não identificou regressão nas correções anteriormente aceitas de fingerprint, inputs consumidos, diagnostics, bounds de forms/categorias, ordenação e caracterização de identidade.

## Materiais revisados

- docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md, seção 10
- docs/A0_METADATA_SEMANTIC_REREVIEW.md
- docs/A0_METADATA_REREVIEW_SOL_RESOLUTION.md
- docs/handoffs/A0_TM_01P_HANDOFF.md
- src/main/java/com/mathmod/authoring/AuthoringMetadata.java
- src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
- docs/DELIVERY_BOARD.md, 9C e 9D

O handoff registra focused test e build bem-sucedidos com o cache Gradle isolado aceito. Esta revisão usa essa evidência já registrada; não altera a implementação nem reexecuta tarefas de build.

## Verificação do delta

| Critério 9D | Resultado | Evidência |
| --- | --- | --- |
| Máximo explícito de presentations | Conforme | MAX_RUNE_PRESENTATIONS = 2.048. |
| Invariante da Snapshot | Conforme | O construtor compara runePresentations.size() ao máximo e lança CandidateFailure com LIMIT_EXCEEDED. |
| Vetor aceito | Conforme | snapshotEnforcesRunePresentationDescriptorBoundBeforePublication constrói 2.048 presentations e confirma o tamanho retornado. |
| Vetor rejeitado | Conforme | O mesmo teste tenta 2.049 presentations e captura CandidateFailure. |
| Diagnóstico estruturado | Conforme | O teste verifica DiagnosticCode.LIMIT_EXCEEDED; CandidateFailure preserva diagnostics imutáveis e não usa texto de exceção como protocolo. |
| Sem snapshot na rejeição | Conforme | A construção lança antes de atribuir a variável rejected; o teste confirma null. |
| Sem truncamento | Conforme | Não existe slice, clamp ou publicação parcial; o construtor falha. |
| Sem regressão A0-1F | Conforme por inspeção | As estruturas de SemanticFingerprint, consumedInputIds, diagnostics e queries ordenadas permanecem inalteradas no delta. |
| Sem ampliação de escopo | Conforme | Nenhum adapter, replay, persistência, componente, rede, ProgramGraph, GuidedWorkspaceState, tela ou API pública foi introduzido. |

## Contraprova de fronteira

| Id | Construção | Resultado exigido | Resultado revisado |
| --- | --- | --- | --- |
| PRESENTATION-2048 | Snapshot com 2.048 RunePresentations distintas | Snapshot válida com 2.048 entradas | Conforme |
| PRESENTATION-2049 | Snapshot com 2.049 RunePresentations distintas | CandidateFailure estruturada com LIMIT_EXCEEDED; nenhuma snapshot retornada | Conforme |

A contagem é aplicada no construtor de Snapshot, portanto também protege chamadas diretas a esse invariante, e não somente a factory built-in que atualmente publica mapa vazio de presentations.

## Ausência de regressão

O delta se limita a:

- uma constante de limite;
- uma condição adicional no invariante de Snapshot;
- um teste de fronteira e seu helper de presentations.

Não houve mudança em:

- fingerprint estruturada;
- separação entre consumedInputIds e inputHints;
- canonicalização numérica;
- limites de forms, categorias, parâmetros, inputs, aliases ou diagnostics;
- códigos/estrutura de Diagnostic e CandidateFailure;
- ordering de categoria/form;
- tabela de 67 forms e 11 categorias;
- persistência, autoridade de execução, graph, workspace, componentes ou rede.

As observações previamente adiadas sobre loaders externos e encapsulamento completo da construção de Snapshot permanecem adiadas pelo escopo de Sol e não são reabertas por 9D.

## Recomendação

**APPROVE para A0-3.**

A0-TM-02 pode tornar-se READY somente após Sol aceitar formalmente esta recomendação e avançar o gate. A aprovação cobre exclusivamente a fundação A0-1/A0-2 e a correção 9C; não aprova antecipadamente a implementação de adapter, replay ou igualdade de ProgramGraph, que continuam com critérios próprios na tarefa A0-TM-02.

## Limites da re-revisão

Não foram alterados arquivos Java, persistência, schema, Data Components, codecs, payloads, rede, ProgramGraph, GuidedWorkspaceState ou telas.

