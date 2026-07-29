# Re-revisão Semântica A0-TH-01R

**Tarefa:** A0-TH-01R (9B)  
**Revisor:** Terra High  
**Escopo:** somente o delta A0-TM-01F. Nenhuma alteração Java foi feita.  
**Decisão:** **REJECT para A0-3.**

## Conclusão

O delta corrige de forma adequada os três defeitos que motivaram a revisão anterior:

- a compatibilidade deixou de usar concatenação ambígua e passou a usar valor estruturado;
- inputs consumidos pela expansão foram separados de hints descritivos;
- candidate failures passaram a expor diagnósticos estruturados e as queries de ordenação são explícitas.

Há, porém, um bloqueador remanescente contra a resolução Sol: a Snapshot não limita a quantidade de runePresentations a 2.048. A seção 10 do contrato torna esse limite obrigatório, e a resolução Sol determina que os limites daquela seção são pré-requisito para A0-3. Logo, o delta não fecha o gate.

## Material revisado

- docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md
- docs/A0_METADATA_SEMANTIC_REVIEW.md
- docs/A0_METADATA_REVIEW_SOL_RESOLUTION.md
- docs/handoffs/A0_TM_01F_HANDOFF.md
- src/main/java/com/mathmod/authoring/AuthoringMetadata.java
- src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
- src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
- docs/DELIVERY_BOARD.md, 9A e 9B

O handoff registra focused test e build bem-sucedidos com o cache Gradle isolado aceito. Não repeti o build nesta re-revisão: a última repetição pela revisão anterior expirou no limite da ferramenta antes de produzir resultado final, enquanto a evidência aceita por Sol é suficiente para verificar a execução declarada.

## Verificação dos requisitos da resolução Sol

| Requisito | Resultado | Evidência |
| --- | --- | --- |
| Fingerprint estruturada, não persistida | Conforme | SemanticFingerprint e ParameterSemantics são records imutáveis; não há codec, componente ou payload. |
| FP-1: colisão por delimitador | Conforme | O teste compara [a\|input:b] contra [a,b] como listas estruturadas e exige fingerprints diferentes. |
| FP-2: hint descritivo excluído | Conforme | Form separa consumedInputIds de inputHints; semanticFingerprint usa somente o primeiro. |
| Sensibilidade de parâmetro/adapter | Conforme | Testes cobrem ordem, bound, type e adapter; a estrutura inclui key, type, default e constraints. |
| Máximo 16 parâmetros | Conforme | Form rejeita mais de 16 com LIMIT_EXCEEDED; vetor de 17 existe. |
| Máximo 16 inputs consumidos | Conforme | Form rejeita mais de 16; vetor de 17 existe. |
| Máximo 1.024 forms | Conforme | snapshot valida a lista antes de publicar; vetor de 1.025 existe. |
| Máximo 128 categorias | Conforme | snapshot valida a lista antes de publicar; vetor de 129 existe. |
| Chaves de até 160 caracteres | Conforme no modelo coberto | requireBounded é aplicado às chaves relevantes; vetor de 161 para parameter key existe. |
| Diagnósticos estruturados | Conforme para os caminhos de candidate cobertos | CandidateFailure contém lista imutável de Diagnostic com severidade, código, kind, id opcional, source kind/name e mensagem. |
| DUPLICATE_ID, UNKNOWN_CATEGORY, LIMIT_EXCEEDED | Conforme | candidateFailuresExposeStableStructuredDiagnostics. |
| Ordenação de categorias/forms | Conforme | orderedCategories e orderedForms usam sortOrder e id; teste de empate de categoria existe. |
| Enum-order-independent identity | Conforme | A tabela continua explícita; frozenEnumNameToFormId compara mapeamento por nome, não a sequência de values(). |
| Sem A0-3/persistência/rede | Conforme por inspeção | Apenas três arquivos authoring/teste continuam no pacote; não há adapter, ProgramGraph, GuidedWorkspaceState, componentes ou rede. |

## Finding bloqueador

### P1 — Limite de RunePresentationDescriptor não é imposto

A seção 10 do contrato limita rune presentation descriptors a 2.048. A Snapshot recebe runePresentations, mas seu construtor verifica apenas:

- runeForms;
- categories;
- aliases;
- diagnostics.

Não há constante correspondente nem condição sobre runePresentations.size(). Assim, a construção direta de uma Snapshot com 2.049 presentations é aceita, contrariando o limite do contrato. A factory snapshot atual publica Map.of() para presentations, o que apenas mascara a lacuna; não estabelece o invariante da boundary.

Também falta o vetor adversarial equivalente:

    2.049 RunePresentations -> LIMIT_EXCEEDED e nenhuma snapshot publicada

**Impacto:** bloqueador. A resolução Sol diz expressamente que os limites da seção 10 são pré-requisitos para A0-3 e que coleções semânticas devem falhar antes de publicação, nunca ser truncadas.

## Observações não bloqueadoras para A0-3

- O construtor público de Snapshot ainda permite criar diretamente uma combinação que a factory não validaria, como form referenciando categoria ausente. Enquanto só o builder/factory interno for a rota de publicação built-in isso não muda o resultado atual; antes de loaders externos, essa construção deve ser encapsulada ou receber validação integral.
- CandidateFailure é estruturado nos caminhos de candidate. Alguns construtores locais, como NumberConstraints inválido, ainda lançam IllegalArgumentException convencional. Isso não contradiz o vetor aceito para candidate publication, mas não deve virar protocolo de loader quando fontes externas forem autorizadas.
- Aliases e rune presentations continuam sem candidate assembly completa. Isso é compatível com o escopo built-in sem loaders; não autoriza A0-3 a introduzir precedência, fallback de camada ou alias migration além do contrato.

## Contraprovas verificadas

| Id | Caso | Resultado observado/esperado |
| --- | --- | --- |
| FP-1 | consumedInputIds [a\|input:b] versus [a,b] | records de lista diferentes; teste exige não igualdade. |
| FP-2 | mesmo input consumido, hints descritivos distintos | fingerprints iguais; teste exige igualdade. |
| BOUND-FORM | 1.025 forms | CandidateFailure com LIMIT_EXCEEDED. |
| BOUND-CATEGORY | 129 categorias | CandidateFailure com LIMIT_EXCEEDED. |
| BOUND-PARAM | 17 parâmetros | CandidateFailure com LIMIT_EXCEEDED. |
| BOUND-CONSUMED | 17 inputs consumidos | CandidateFailure com LIMIT_EXCEEDED. |
| DIAG-DUP | id de form duplicado | CandidateFailure e DUPLICATE_ID. |
| DIAG-CATEGORY | form referencia categoria ausente | CandidateFailure e UNKNOWN_CATEGORY. |
| ORDER-CATEGORY | categorias com mesmo sortOrder | ordenação por categoryId. |
| IDENTITY | enum declarada em ordem diferente da tabela | a asserção compara enumName -> id; não depende da ordem de values(). |
| BOUND-PRESENTATION | 2.049 presentations | **Ausente; Snapshot atual aceita.** |

## Recomendação

**REJECT para A0-3.**

A0-TM-01F precisa de uma correção mínima adicional, limitada a:

1. impor o máximo de 2.048 presentations no invariante de Snapshot;
2. produzir DiagnosticCode.LIMIT_EXCEEDED estruturado antes de publicação;
3. adicionar o vetor de 2.049 presentations e confirmar que nenhuma snapshot é devolvida;
4. atualizar o handoff com teste focado e build.

Não é autorizado iniciar adapter, replay, persistência, Data Component, rede, APIs públicas ou qualquer mudança de ProgramGraph durante essa correção.

Após esse delta mínimo e seu handoff aceito, uma re-revisão curta pode decidir A0-3 sem reabrir os achados já resolvidos.

## Questões para Sol

Nenhuma ambiguidade de contrato impede a correção: o máximo de 2.048 presentations já está congelado na seção 10. Sol deve manter A0-TM-02 bloqueada até que este finding seja fechado e uma recomendação APPROVE seja aceita.

## Limites da re-revisão

Este parecer não aprova o adapter legado nem a igualdade de replay, que são escopo próprio de A0-TM-02. Não foram alterados Java, arquivos A0 de produção, persistência, componentes, codecs, rede, telas ou schema.

