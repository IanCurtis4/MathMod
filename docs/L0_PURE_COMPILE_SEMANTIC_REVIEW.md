# L0-TH-01 — Revisão Semântica do Compilador Puro

**Owner:** Terra High  
**Escopo:** revisão read-only/documental do L0-TM-01F3 aceito  
**Recomendação:** **APPROVE**

## Decisão

O compilador puro satisfaz o recorte autorizado por `L0-SOL-01`. Ele recebe uma
`ScopedProgramSource` já construída, captura uma visão imutável do registro,
faz validação estrutural/pureza/tipos, lowering com ambiente De Bruijn e
compartilhamento explícito, e só devolve um grafo depois da validação do
`ProgramGraph`. Nenhuma etapa persiste, executa, altera item, altera registro,
cria codec, Data Component, payload ou schema.

Esta aprovação é apenas para o candidato de compilação puro. Ela não aprova
inscrição, executabilidade, recursos, conhecimento, protocolo, persistência ou
um segundo caminho de execução. Em especial, `TAIL-8` continua sendo somente
um candidato puro; a admissão pelo `ProgramExecutionPolicy` pertence ao
posterior L0-TM-02.

## Caso de uso revisado

O caso de uso é transformar uma expressão funcional limitada, escrita pelo
jogador, em um `ProgramGraph` candidato de primeira ordem. A fronteira impede
que a autoria introduza trabalho de compilação sem limite, texto numérico
ambíguo, duplicação implícita de observações ou efeitos escondidos por
abstração. A saída não é autoridade executável e não substitui um grafo
persistido.

## Matriz semântica

| Aspecto | Evidência revisada | Resultado |
|---|---|---|
| Medidor | Um único `ScopedCompileBudget` percorre estrutura, tipos, lowering, literal, closures/bindings, aplicação e nós/arestas; o método público cria medidor novo por tentativa. | Conforme |
| Limite | A 4.096ª cobrança é aceita; a tentativa de 4.097 lança `COMPILE_STEP_LIMIT`, mantém a contagem em 4.096 e não retorna grafo. A aplicação identidade custa 17 cobranças no pipeline completo. | Conforme |
| Fail closed | Falha do checker, literal, lowering, limite ou `ProgramValidator` constrói resultado sem grafo. Não há grafo parcial. | Conforme |
| Literal NUMBER | Gramática decimal estrita; `NaN`, infinito, hexadecimal, unidade, vírgula, sufixo e whitespace são rejeitados. `-0.0` torna-se `0.0`; a constante exige descriptor `constant_number` puro e executor correspondente. | Conforme |
| Efeito em tail | Efeito somente no tail de primeira ordem; não pode ocorrer em valor de `let`, função/argumento de aplicação, argumento de rune ou corpo de lambda. | Conforme |
| Compartilhamento | `let` e beta administrativo vinculam o resultado uma vez e reutilizam o mesmo nó. Referências De Bruijn são resolvidas pelo ambiente lexical. | Conforme |
| Ausência de CSE | Duas observações sintaticamente repetidas e sem binder produzem dois nós; não existe deduplicação por equivalência estrutural. | Conforme |
| Diagnóstico | `ScopedLanguageIssue.normalize` elimina duplicata por fase/caminho/código e ordena fase, caminho numérico e código. O caminho da body da lambda continua sendo o caminho autorado após beta. | Conforme |
| Isolamento | `ScopedRuneSnapshot` copia as definições usadas pelo compilador; não há mutação de `RuneRegistry`, item, jogador ou storage. | Conforme |

## Vetores auditados

| Vetor | Oráculo observado | Resultado |
|---|---|---|
| OBS-SHARE-1 | Aplicação de lambda que referencia o argumento duas vezes cria uma só observação e duas arestas consumidoras. | Passa |
| OBS-SHARE-2 | `let` explícito compartilha a observação. | Passa |
| OBS-SHARE-3 | Binding aninhado preserva a mesma identidade de nó observacional. | Passa |
| OBS-SHARE-4 | Repetição não ligada não recebe CSE e gera duas observações. | Passa |
| OBS-SHARE-5 | Literal ligado cria uma única constante, conectada às duas sockets. | Passa |
| OBS-SHARE-6 | Observação em corpo de lambda falha com `IMPURE_LAMBDA_BODY` e sem grafo. | Passa |
| TAIL-1 | Efeito terminal `Unit` é candidato puro compilável. | Passa |
| TAIL-2 | Observação ligada alimenta o único efeito final; o output é o efeito. | Passa |
| TAIL-3 | Efeito em valor de `let` é rejeitado. | Passa |
| TAIL-4 | Efeito em argumento de aplicação é rejeitado. | Passa |
| TAIL-5 | Efeito aninhado em argumento de rune puro é rejeitado. | Passa |
| TAIL-6 | Corpos de lambda aceitam somente chamadas puras; observação e efeito falham. | Passa |
| TAIL-7 | Efeito aninhado sob outro efeito é rejeitado. | Passa |
| TAIL-8 | Resultado não-`Unit` só é aceito como candidato puro, sem alegação de admissão executável. | Passa / deferido corretamente |
| BOUND-1 | 256 nós AST aceitam; 257 falham com `AST_LIMIT`. | Passa |
| BOUND-2 | Profundidade de binding 16 aceita; 17 falha. | Passa |
| BOUND-3 | 64 aplicações aceitam; 65 falham. | Passa |
| BOUND-4 | Literal de 160 caracteres aceita; 161 falha. | Passa |
| BOUND-5 | Pipeline completo pré-carregado para terminar exatamente em 4.096 aceita. | Passa |
| BOUND-6 | Próxima cobrança falha somente com `COMPILE_STEP_LIMIT` e sem grafo. | Passa |
| BOUND-7 | Grafo baixado que o validador rejeita nunca escapa parcialmente. | Passa |
| BOUND-8 | Combinador futuro/desconhecido falha fechado com `UNKNOWN_RUNE`. | Passa |

## Contraprovas verificadas

- `let o = observe() in emit(o)` preserva um nó `observe`, um nó `emit`, uma
  ligação `observe -> emit.value` e output em `emit`; não duplica a leitura.
- `add(observe(), observe())` preserva duas leituras distintas: ausência de CSE
  é observável e intencional.
- `(lambda x. observe())(1)` não é baixado: o corpo reutilizável não pode
  capturar observação.
- `emit(add(1, 2))` permanece um candidato terminal; colocar esse efeito em
  valor, aplicação ou argumento o invalida antes do lowering.
- `-0.0` e outros spellings decimais aceitos chegam ao grafo pela forma
  canônica; `1e999`, `NaN`, `0x1.0p0` e ` 1` não chegam ao grafo.
- Quando a validação final do grafo falha, o resultado contém
  `LOWERED_GRAPH_INVALID` e `graph` vazio, em vez de expor a construção
  intermediária.

## Evidência de testes

O gate de aceitação de Sol registra execução sem cache da suíte
`com.mathmod.language.*`: sete suítes, 34 testes, zero falhas, erros ou skips.
Nesta revisão foi executado novamente:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.language.*' --no-daemon --console=plain
```

Resultado local: `BUILD SUCCESSFUL` (artefatos de teste disponíveis em cache).
Os testes centrais estão em `ScopedStructureValidatorTest` e
`ScopedProgramCompilerTest`; os últimos vetores aprovados em
`L0_TM_01_FINAL_GATE_ACCEPTANCE.md` cobrem a matriz acima.

## Limites e handoff

Não há finding bloqueante para L0-TH-01. Permanecem explicitamente fora deste
slice: wire format, limites de envelope, versão/schema, Data Component,
persistência, atomicidade source/grafo, geração de registro em servidor,
política executável, recursos/conhecimento, UI e teorema jogável.

**Handoff para Sol:** desbloquear `L0-SOL-02 — Scoped Source Wire-Format
Contract`. Esse próximo gate deve congelar somente o contrato de formato que
lhe foi reservado; não deve inferir desta aprovação qualquer autorização para
persistir ou executar a fonte.

