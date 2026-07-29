# Revisão Semântica A0-TH-01

**Tarefa:** A0-TH-01  
**Revisor:** Terra High  
**Escopo:** somente leitura de A0-1/A0-2; nenhum arquivo Java foi alterado.  
**Decisão:** **REJECT para A0-3**, até fechar os bloqueadores semânticos abaixo.

## Resultado

A0-1/A0-2 preserva corretamente a maior parte da compatibilidade built-in: a tabela de 67 forms é explícita e orientada por nome, as 11 categorias estão presentes, a canonicalização numérica coincide com o comportamento legado e as coleções publicadas são defensivamente imutáveis.

Contudo, a operação que decide compatibilidade semântica não é segura para servir como gate de override/replay: ela usa concatenação ambígua de texto e não distingue input hints semânticos de hints meramente descritivos. Além disso, o modelo não aplica vários limites obrigatórios do contrato e não publica diagnósticos estáveis observáveis. A0-3 não deve começar enquanto essas garantias da fundação não forem corrigidas e caracterizadas.

## Materiais revisados

- docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md
- docs/handoffs/A0_TM_01_HANDOFF.md
- docs/handoffs/A0_TM_01_SOL_REVIEW.md
- src/main/java/com/mathmod/authoring/AuthoringMetadata.java
- src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
- src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
- src/main/java/com/mathmod/program/CustomNumericParameter.java
- src/main/java/com/mathmod/program/CustomSpellAction.java
- docs/DELIVERY_BOARD.md, A0-TH-01

O handoff e a revisão Sol registram a execução focada com sucesso (6 testes, sem falhas) e build bem-sucedido no cache Gradle isolado. Esta revisão tentou repetir a suíte com o mesmo cache, mas o processo excedeu 64 segundos sem saída final e foi encerrado pelo limite da ferramenta. Não há evidência de falha de teste a partir desse timeout; a evidência aceita por Sol permanece a referência de execução.

## Evidência aceita

| Área | Resultado da revisão | Evidência |
| --- | --- | --- |
| Canonicalização numérica | Conforme para a regra principal: não finitos retornam default; finitos fazem clamp inclusivo. | NumberConstraints.canonicalize e CustomNumericParameter.clamp são equivalentes; teste cobre NaN, +infinito, -infinito e clamps de number_one. |
| Bounds do número | Correto para min/max finitos e default finito dentro do intervalo. | Construtores de NumberConstraints e Parameter. |
| Imutabilidade | Conforme nas coleções já expostas: List.copyOf e Map.copyOf protegem listas/maps do snapshot e da form. | AuthoringMetadata e teste orderingIsFrozenByCompatibilityTableAndCollectionsAreImmutable. |
| Ordenação de forms | Determinística para uma categoria: sortOrder, depois formId. | Snapshot.orderedForms e teste de empate. |
| 67 forms | Tabela explícita, sem usar ordinal para lookup; snapshot confirma enum por nome e persistentId atual. | BuiltInAuthoringMetadata.TABLE e snapshot(). |
| 11 categorias | Mapeamento atual e ids congelados estão caracterizados. | categories(), teste expectedCategories e construção da snapshot. |
| Fórmula Cycle 2 | Subconjunto Symbol/Sequence respeita token, filhos, nós e profundidade no objeto construído. | Formula, Symbol e Sequence. |
| Duplicatas de form/categoria | A factory de listas rejeita duplicatas e categoria inexistente. | AuthoringMetadata.snapshot e candidateRejectsDuplicateAndUnknownCategory. |

## Findings priorizados

### P1 — Fingerprint pode declarar compatíveis duas definições semanticamente distintas

Form.semanticFingerprint concatena componentes com delimitadores sem escaping, tamanho ou estrutura canônica. Input hints aceitam qualquer String não nula porque Form apenas aplica List.copyOf.

Contraprova mínima, mantendo formId, adapter e todos os demais campos iguais:

- definição A: inputHints = [ "a|input:b" ]
- definição B: inputHints = [ "a", "b" ]

As duas produzem o mesmo sufixo textual:

    |input:a|input:b

Logo produzem a mesma fingerprint, embora a sequência de input hints seja diferente. Isso viola a exigência de representação canônica para a comparação de replay e pode permitir que um overlay semanticamente conflitante passe como compatível.

O mesmo desenho também tem o defeito inverso: o fingerprint inclui todo inputHint, mas o contrato determina incluir somente identidades consumidas pela expansão. Uma alteração puramente descritiva deveria ser compatível; o modelo não consegue representá-la como tal.

**Impacto:** bloqueador de A0-3. O adapter depende da fronteira que garante que um formId preserva significado; não é seguro aprovar replay sobre uma comparação que pode ter falso positivo ou falso negativo.

**Correção exigida para o próximo owner:** uma representação estruturada, não ambígua e determinística; distinguir explicitamente hints consumidos por expansão de hints de apresentação. Não persistir a fingerprint, não introduzir schema e não escolher um formato público sem Sol.

### P1 — Limites obrigatórios do snapshot não são aplicados

O contrato exige máximos de 1.024 forms, 128 categorias, 2.048 aliases, 16 parâmetros por form, 16 input hints, 160 caracteres para chave de tradução/source e 1.024 diagnósticos, além dos limites da fórmula. A implementação atual valida apenas o subconjunto de fórmula e bounds numéricos.

Em particular:

- Form aceita listas ilimitadas de parameters e inputHints.
- Category, Form e Parameter aceitam translationKey/key sem limite máximo.
- snapshot aceita listas ilimitadas de forms e categories.
- Snapshot aceita aliases e diagnostics sem limite ou validação de agregados.
- Não há estratégia de falha com LIMIT_EXCEEDED antes de publicação.

**Impacto:** bloqueador de A0-3. O adapter futuro receberá forms de uma snapshot e parâmetros canonicalizados; a fundação precisa impor seus próprios limites antes de servir como entrada de replay.

### P1 — Diagnósticos não cumprem a fronteira estável do contrato

AuthoringMetadata.snapshot coleta Diagnostic internamente, mas em qualquer erro lança IllegalArgumentException cuja mensagem contém a lista. Assim o consumidor não recebe um resultado estruturado com diagnóstico estável. O tipo Diagnostic também só tem code, subject e detail; faltam severidade, kind do registro e fonte, e seus códigos não correspondem ao conjunto mínimo do contrato (por exemplo, INVALID_REFERENCE é genérico para categoria desconhecida).

O teste atual verifica apenas que uma exceção é lançada; não verifica código, identificação ou ausência de publicação.

**Impacto:** bloqueador de gate da metadata. Antes de adapter/reload, colisão, limite e referência inválida devem ser comunicáveis sem tornar texto de exceção protocolo.

### P2 — A garantia de “enum reordering” é contrariada pelo teste de caracterização

A construção de BuiltInAuthoringMetadata.snapshot resolve cada action por enumName, portanto não usa ordinal como identidade. Porém o teste frozenTableCharacterizesEveryLegacyFormWithoutUsingOrdinalIdentity compara a sequência inteira de CustomSpellAction.values() com a sequência da tabela congelada.

Reordenar a enum sem mudar nenhum nome ou persistentId não alteraria lookup/saved identity, mas faria esse teste falhar. Isso contraria o critério de aceite de que reordenação de enum não muda lookup ou identidade salva e transforma uma mudança de implementação num falso bloqueio.

**Impacto:** não altera comportamento de produção atual, mas a caracterização precisa afirmar o contrato correto: comparar o mapeamento enumName -> id e a ordenação explícita da tabela, não a ordem incidental de values().

### P2 — Ordenação determinística está incompleta na superfície de snapshot

orderedForms é determinístico, mas Snapshot não expõe uma ordenação de categorias. Os mapas retornados por Map.copyOf são imutáveis, porém sua ordem de iteração não deve ser usada como contrato de palette. O contrato exige ordenação por sortOrder e id também no nível de categorias.

**Impacto:** não bloqueia a construção do adapter puro, mas bloqueia o uso do snapshot como fonte de UI/registry até existir query ordenada/cached e teste de empate de categorias. O código consumidor não deve iterar categories() ou runeForms() para definir apresentação.

### P2 — Colisões, aliases e apresentações ainda não têm a semântica contratada

A factory de snapshot só recebe listas de forms/categories e sempre publica maps vazios para runePresentations e aliases. Por isso não há validação de:

- duplicata/orfandade de RunePresentation;
- alias shadowing, ciclo, alvo inexistente, profundidade e canonicalização;
- conflito de fingerprint entre fontes;
- fallback de camada inferior;
- last-known-good e publicação atômica.

Isso é coerente com a ausência deliberada de loaders externos, mas significa que a API atual é um modelo built-in parcial, não a registry boundary completa definida no contrato. Esses itens devem ser mantidos fora de A0-3 ou ter uma tarefa de registry/candidate explicitamente aprovada.

## Contraprovas adicionais

| Id | Construção mínima | Resultado correto exigido | Cobertura atual |
| --- | --- | --- | --- |
| FP-1 | hints [a|input:b] versus [a,b] | fingerprints diferentes ou entrada inválida; nunca iguais por ambiguidade | Ausente |
| FP-2 | mudança apenas em hint não consumido pela expansão | mesma fingerprint | Ausente; modelo não distingue os casos |
| FP-3 | mesmo formId, parameter key/default/bound/type/order ou adapter distintos | fingerprint diferente | Parcial: default, bound, ordem e adapter são testados; key e type não |
| BOUND-1 | form com 17 parâmetros | LIMIT_EXCEEDED / candidato não publicado | Ausente |
| BOUND-2 | form com 17 input hints | LIMIT_EXCEEDED / candidato não publicado | Ausente |
| BOUND-3 | 1.025 forms ou 129 categorias | LIMIT_EXCEEDED / candidato não publicado | Ausente |
| BOUND-4 | translation/source key com 161 caracteres | rejeição limitada e diagnóstico estável | Ausente |
| DIAG-1 | form duplicada | DUPLICATE_ID estruturado, não texto de exceção | Ausente |
| DIAG-2 | categoria desconhecida | UNKNOWN_CATEGORY estruturado | Ausente |
| ORDER-1 | duas categorias no mesmo sortOrder | ordenação por categoryId | Ausente |
| ORDER-2 | enum reordenada com tabela fixa | snapshot mantém ids e palette ordenada pela tabela | Ausente; teste atual falha pelo motivo errado |
| IMM-1 | mutar aliases, runePresentations e inputHints devolvidos | UnsupportedOperationException / sem mutação | Ausente |
| NUM-1 | mínimo = máximo; valor no limite; -0.0 quando intervalo inclui zero | exatamente o clamp legado e representação numérica esperada | Ausente |
| FORM-1 | profundidade 17 e 129 nós agregados | rejeição | Ausente; apenas token, 33 filhos e sequência vazia são cobertos |

## Avaliação por dimensão

### Canonicalização e bounds numéricos

**Aprovar como base local.** A regra de não finito retorna default antes de clamp, exatamente como CustomNumericParameter. O construtor impede default não finito e default fora de bounds. A caracterização built-in reproduz key, default, min e max da enum.

A cobertura deve ampliar os limites de igualdade, o intervalo degenerado e -0.0, principalmente porque a continuidade com a codificação hexadecimal existente é requisito de replay. Isso não exige mudança de schema.

### Imutabilidade

**Aprovar para os dados já construídos.** Snapshot, Form, Sequence e as listas de parâmetros/hints usam cópias imutáveis. O teste já prova mutação de mapas, diagnostics, orderedForms e parameters.

Falta provar aliases, runePresentations e inputHints; as estruturas em si usam cópia, mas a API atualmente nunca publica os dois primeiros preenchidos. Nenhuma coleção deve ser exposta como base mutável durante A0-3.

### Determinismo e tabela frozen

**Aprovar a tabela de compatibilidade, rejeitar a caracterização de reordenação atual.** A tabela contém 67 entradas explícitas, seu constructor consulta enum por nome e confere persistentId/categoria. As 11 categorias do contrato são derivadas e conferidas. A ordenação dentro de categoria usa o sortOrder explícito da tabela e id como tie-breaker.

A tabela deve continuar sendo a origem de ordem; CustomSpellAction.values() é apenas fonte para verificar presença e id, jamais fonte para exigir ordem idêntica.

### Colisões e duplicatas

**Parcial.** Duplicata de form/categoria e categoria ausente são rejeitadas, porém a informação é descartada em uma exceção. Não há cobertura de apresentações, aliases ou colisão semântica entre definições. A0-3 não deve introduzir um comportamento de “último ganha” para resolver essas lacunas.

## Alternativas rejeitadas

1. **APPROVE condicionado a o adapter usar apenas built-ins.** Rejeitado: A0-3 transforma a metadata em fronteira de replay; aprovar uma fingerprint ambígua e limites ausentes criaria dívida semântica justamente antes da parte que depende dessas garantias.
2. **Aceitar a string atual como fingerprint “apenas interna”.** Rejeitado: interna não significa semânticamente inofensiva. Ela é usada para decidir compatibilidade e a colisão FP-1 é concreta.
3. **Usar record equality para compatibilidade.** Rejeitado: incluiria campos de apresentação e quebraria overlays compatíveis, em desacordo com o contrato.
4. **Ordenar por iteração de Map.copyOf.** Rejeitado: imutabilidade não estabelece ordenação determinística de UI/replay.
5. **Truncar parâmetros, forms, hints ou diagnostics acima do máximo.** Rejeitado: o contrato proíbe truncar coleções semânticas; deve falhar antes da publicação.
6. **Resolver conflitos de semantic fingerprint por precedência.** Rejeitado: o contrato proíbe last-write-wins para semântica de form.
7. **Fixar componente, codec, schema ou payload junto com esta correção.** Rejeitado: A0-1/A0-2 são não persistentes; isso requer decisão separada de Sol.

## Recomendação para A0-3

**REJECT.**

A0-TH-01 só deve ser aceito após uma revisão curta de correção que demonstre:

1. fingerprint estruturada e não ambígua, com separação entre inputs consumidos e hints descritivos;
2. todos os limites de A0-1/A0-2 aplicados com falha antes de publicar;
3. resultado/diagnóstico estável e observável, sem expor mensagem de exceção como protocolo;
4. teste de reordenação da enum corrigido para validar id e ordem explícita da tabela;
5. queries ordenadas necessárias à snapshot, ou escopo explicitamente reduzido por Sol para impedir qualquer consumidor de depender da iteração de mapa;
6. vetores FP-1, FP-2, BOUND-1 a BOUND-4, DIAG-1/2 e ORDER-1/2 passando.

Depois disso, Terra High pode revisar apenas o delta semântico. A0-3 continuará precisando, em sua própria tarefa, de comparação exata de ProgramGraph para cada form e contextos representativos; este parecer não aprova adapter nem replay.

## Questões para Sol

1. A0-1/A0-2 deve completar agora a API de candidate/diagnóstico estável definida no contrato, ou Sol prefere fatiar explicitamente essa parte antes de qualquer A0-3? Em ambos os casos, A0-3 permanece bloqueado até haver fronteira verificável.
2. Qual estrutura interna Sol aprova para canonicalização da fingerprint? A decisão deve exigir injetividade e versionamento local de algoritmo se serializada para diagnóstico, sem persistir a fingerprint.
3. Como o modelo deve declarar quais input hints são consumidos pela expansão? A resposta deve impedir tanto falso positivo quanto falso conflito de replay.
4. A ordenação de categorias será exposta já na snapshot A0-1, ou a Sol impõe que A0-3/A0-4 usem query específica antes de a UI consumir metadata?
5. Quais campos mínimos de Diagnostic entram agora no resultado de candidate, preservando a regra de que nenhum texto de exceção é protocolo?
6. Sol confirma que os limites de snapshot ausentes são pré-requisito para A0-3, conforme a seção 10 do contrato?

## Limites da revisão

Não foram criados testes nem modificados arquivos Java, A0, persistência, ProgramGraph, componentes, rede ou telas. Este documento não congela nomes de campos, tags, schema version, Data Component ou payload.

