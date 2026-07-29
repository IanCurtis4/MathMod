# L0 — Auditoria de Lacunas da Linguagem Funcional com Escopo

**Tarefa:** L0-TH-AUDIT  
**Responsável:** Terra High  
**Escopo:** auditoria somente-leitura. Nenhuma alteração de produção, A0, componente, codec ou rede.  
**Estado:** handoff de auditoria; não aprova formato persistente.

## Resultado executivo

Há uma base semântica efetiva: AST com De Bruijn, tipos valor/função, validação estrutural, pureza/effect tail, checker, operações De Bruijn, estimador de coleção e lowering por closures que compartilha bindings.

L0 ainda não é uma autoria suportada. Não existem codec, representação de rede, componente opcional, compilação autoritativa do servidor, dual-write, inspector de fonte, editor ou theorem. O lowerer atual tampouco impõe 4.096 passos, não valida o grafo/executável final e só baixa literais NUMBER.

Portanto, ScopedProgramLowerer é protótipo interno: nunca deve se tornar rota de inscrição antes do pipeline server-side completo.

## Evidência

### Contratos lidos

- docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md
- docs/P4_FUNCTION_LANGUAGE_CONTRACT.md
- docs/P4_SEMANTIC_REVIEW.md
- docs/FUNCTIONAL_LANGUAGE.md
- docs/FOUNDATION_BETA_A0_ASSIGNMENT.md, seção 6.2
- docs/DELIVERY_BOARD.md, tarefa L0-TH-AUDIT

### Código e testes inspecionados

- Núcleo: src/main/java/com/mathmod/language/ScopedExpression.java, RuneTypeExpression.java, ScopedProgramSource.java, ScopedStructureValidator.java, ScopedTypeChecker.java, ScopedDeBruijn.java, ScopedProgramLowerer.java, ScopedLanguageLimits.java, BoundedFunctionCost.java e FunctionalProgramMigrationPolicy.java.
- Autoridade: src/main/java/com/mathmod/runes/ProgramGraph.java, ProgramValidator.java; src/main/java/com/mathmod/program/ProgramStorage.java e ProgramExecutionPolicy.java.
- Registro: src/main/java/com/mathmod/registry/ModDataComponents.java.
- Testes: src/test/java/com/mathmod/language/*Test.java.

O comando .\gradlew.bat test --tests com.mathmod.language.* compilou Java e testes, mas não executou JUnit. Os executores Gradle falharam ao carregar worker.org.gradle.process.internal.worker.GradleWorkerMain e fecharam o pipe. É falha de worker/ambiente; não há resultado de teste confiável.

## Matriz: implementado versus ausente

| Área | Implementado (evidência) | Ausente / insuficiente | Estado |
| --- | --- | --- | --- |
| AST | Variantes seladas Literal, parâmetro, chamada, lambda, aplicação, let; argumentos únicos e hints não semânticos (ScopedExpression). | Codec, decode limitado e posição de fonte. | Parcial/interno |
| Tipos | ValueType/FunctionType, profundidade e inferência exata (RuneTypeExpression, ScopedTypeChecker). | Validação semântica do payload de literal; origem diagnóstica. | Parcial |
| Escopo/estrutura | Limites de AST, binding, tipo, args, aplicação e literal; let não recursivo (ScopedStructureValidator). | Limite real de passos; dados hostis em decode. | Parcial |
| Pureza/tail | Registry de pureza; impuro em lambda e EFFECT fora do tail rejeitados. Coberto em ScopedStructureValidatorTest. | Integração checker+lowerer+política; decisão de construtor de plano. | Parcial |
| Checker | Rune ativo, entradas faltantes/inesperadas, tipos, aplicação não função, função raiz e desabilitado (ScopedTypeChecker). | Deduplicação/localização de issues, literais, passos, validação executável. | Parcial |
| De Bruijn | shift, substituição segura, alpha-equivalência e beta administrativo para let (ScopedDeBruijn/Test). | Não é o caminho de redução observável; sem contador/traço. | Referência |
| Lowering | Closures, ambiente lexical, let/aplicação compartilhando NodeValue e construção de grafo (ScopedProgramLowerer/Test). | Sem passo/nó/aresta limitados durante lowering; somente NUMBER; sem ProgramValidator/ProgramExecutionPolicy. | Protótipo |
| Custo de coleção | Fórmula base + body * bound, saturada acima de 4.096 (BoundedFunctionCost). | Não integrado ao AST/lowerer; combinadores ainda ausentes. | Isolado |
| Codec/rede | Nenhum codec ou stream codec de ScopedProgramSource encontrado. | Envelope bounded, decode fail-closed, sync. | Ausente |
| Persistência | Política pura de graph-only, fonte ruim/futura e source-only (FunctionalProgramMigrationPolicy). | Leitura, escrita, componente, recuperação e dual-write. | Só política |
| Servidor | ProgramStorage valida e persiste ProgramGraph. | Serviço L0, snapshot de registry, dedicated-server. | Ausente |
| Atomicidade | Política de leitura sem rewrite. | Commit unido de grafo/fonte; ProgramStorage comum limpa workspace guiado. | Ausente/alto risco |
| Inspector/editor | Inspector de grafo existe no produto. | Nenhuma referência cliente a ScopedProgramSource; binder, forma reduzida, diagnósticos e edição. | Ausente |
| Gameplay | Nenhuma theorem funcional encontrada. | Vertical slice, recursos, autoridade e UX. | Ausente |

## Achados prioritários

### H1 — Limite contratado não é aplicado

ScopedLanguageLimits define MAX_EVALUATION_STEPS = 4.096 e BoundedFunctionCost estima coleção, mas ScopedProgramLowerer não conta visitas, aplicações, criação de closure, nós nem arestas. O limite hoje não protege o caminho efetivo.

**Recomendação:** orçamento monotônico e cancelável por tentativa de compilação. Toda expansão de trabalho consome passos; ultrapassar o máximo devolve diagnóstico estável a ser nomeado pela Sol e nenhum grafo parcial. Esta auditoria não altera o máximo nem cria o código de diagnóstico.

### H1 — Lowering não fecha a validação executável

ScopedProgramLowerer retorna ProgramGraph sem ProgramValidator, ProgramExecutionPolicy, recursos ou conhecimento. Assim, lowering válido não implica programa inscribível: pode exceder budget/nós/arestas, não terminar em Unit ou violar a política de efeito.

**Recomendação:** lowerer puro, sem ItemStack; um serviço autoritativo deve orquestrar source candidata -> checker -> lowering -> validações de grafo/executável/recursos/conhecimento -> único commit.

### H1 — Literal genérico sem lowering genérico

O AST aceita qualquer ValueType; o lowerer aceita apenas NUMBER e converte string diretamente para mathmod:constant_number. Isso mistura semântica de literal e escolha de rune e deixa tipos válidos pelo checker sem rota de lowering.

**Recomendação:** futuro resolvedor confiável e servidor-controlado por tipo de literal. Ele canonicaliza e limita o dado, recusa não-finito/tipo sem suporte e produz lowering interno; não aceita callback, executor escolhido pelo cliente ou string genérica. Não se propõem nomes, campos, tags ou payload.

### H2 — Gaps de checker e diagnóstico

- Checker preserva issues estruturais e continua inferindo, sem política de supressão/deduplicação; o conjunto final depende da travessia.
- Caminhos são estruturais, sem posição textual; isso é aceitável até DSL, mas precisa fronteira explícita.
- Literal passa pelo tipo declarado sem validação de conteúdo; NUMBER com texto inválido falha tardiamente como TYPE_MISMATCH no lowerer.
- Não existem issues de envelope/versão/decode, de passos, de grafo baixado, de política executável, recurso/conhecimento ou commit.
- A pureza é lida do registry no checker; a futura tentativa deve usar snapshot único de registry para não misturar reload e commit.

## Contraprovas de lowering

Use p(i) para parâmetro De Bruijn e arg(nome,e) para argumento.

| Caso | AST mínimo | Implementação errada | Oráculo |
| --- | --- | --- | --- |
| Captura | lambda A. lambda B. p(1), aplicada a variável externa | substituição textual | valor externo permanece externo; shift/substituteTop já é referência |
| Let recursivo | let x = p(0) in p(0) | binder visível no value | FREE_PARAMETER pré-lowering |
| Observação duplicada | apply(lambda x. add(x,x), observe()) | substituir x duas vezes | um nó observe e duas arestas para add |
| Efeito descartado | let x = effect() in 1 | call-by-value oculto | EFFECT_NOT_IN_TAIL; sem grafo |
| Efeito escondido | apply(lambda x. 1, effect()) | avaliar e descartar argumento | EFFECT_NOT_IN_TAIL; sem grafo |
| Efeito filho | effect(arg(value,effect())) | aceitar efeito como argumento | efeito interno rejeitado como VALUE |
| Literal não numérico | literal(VEC3, texto) em entrada compatível | tratar como constante número ou grafo parcial | falha explícita até descriptor suportado |
| Grafo final inválido | fonte tipada que excede budget/política | tratar ScopedLoweringResult válido como inscrição | falha de validação final, sem mutação |
| Explosão de passos | fonte estruturalmente válida que passa o orçamento | confiar em AST/aplicações | falha de limite, sem grafo parcial |

## Vetores: compartilhamento de observações

Os vetores são testes puros de registry de teste + checker + lowerer. Não foram adicionados para não disputar ownership; não exigem schema.

| Id | Fonte/registro mínimo | Oráculo |
| --- | --- | --- |
| OBS-SHARE-1 | apply(lambda x:Number. add(left=x,right=x), observe_number()); observe_number é OBSERVATION. | 1 observe, 1 add e duas arestas de mesmo from para left/right |
| OBS-SHARE-2 | let x = observe_number() in add(x,x). | Mesmo oráculo; sharing por let |
| OBS-SHARE-3 | apply(lambda x. let y=x in add(y,x), observe_number()). | Uma observação; y e x apontam ao mesmo NodeValue |
| OBS-SHARE-4 | add(observe_number(), observe_number()). | Duas observações: sem binder não há CSE de observação |
| OBS-SHARE-5 | let x = pure_constant() in add(x,x). | Um constante, dois usos; não exigir CSE além do let |
| OBS-SHARE-6 | aplicar função cujo corpo chama observe_number. | IMPURE_LAMBDA_BODY; nenhuma observação baixada |

Comparar pureza, conectividade por socket e identidade de nó; não a ordem da lista nem ids internos fN.

## Vetores: effect tail

| Id | Fonte mínima | Resultado obrigatório |
| --- | --- | --- |
| TAIL-1 | effect(arg(value,pure(1))) na raiz | checker aceita; pipeline ainda exige grafo executável e Unit |
| TAIL-2 | let x = observe() in effect(arg(value,x)) | aceita; uma observação alimenta efeito terminal |
| TAIL-3 | let x = effect() in unitLiteral | EFFECT_NOT_IN_TAIL |
| TAIL-4 | apply(lambda x:Unit. unitLiteral, effect()) | EFFECT_NOT_IN_TAIL |
| TAIL-5 | pure(arg(value,effect())) | EFFECT_NOT_IN_TAIL |
| TAIL-6 | lambda x:Number. effect(arg(value,x)) | IMPURE_LAMBDA_BODY |
| TAIL-7 | effect(arg(value,effect())) | efeito interno rejeitado |
| TAIL-8 | raiz concreta não Unit, como observe_number() | pipeline de inscrição rejeita na política executável |

## Limites de passos e vetores

Limites contratuais: AST 256, binding 16, tipo 4, args 16, aplicações 64, literal 160 caracteres, coleção 64, passos 4.096 e budget 128. ProgramValidator impõe adicionalmente 64 nós e 128 arestas. O pipeline deve aplicar ambos; limites de fonte não substituem limites de grafo.

### Política recomendada

1. Limitar envelope antes de alocar árvore inteira.
2. Decodificar candidato e validar estrutura antes de registry/lowering.
3. Usar contador único por tentativa; aplicações, lets, criação de nós/arestas, resolução de literal e combinadores futuros consomem passos.
4. Falhar no passo 4.097; não truncar, aproximar ou retornar grafo parcial.
5. Reservar custo máximo declarado de coleção, não o tamanho observado.
6. Validar grafo final, recursos, conhecimento e execução antes do commit.

| Id | Construção | Resultado |
| --- | --- | --- |
| BOUND-1 | 257 nós AST | AST_LIMIT; sem lowering |
| BOUND-2 | 17 lambdas/lets | BINDING_DEPTH_LIMIT |
| BOUND-3 | 65 aplicações | APPLICATION_LIMIT |
| BOUND-4 | literal de 161 caracteres | LITERAL_LIMIT e decode limitado futuro |
| BOUND-5 | base + body * 64 = 4.096 | só aceita se contador real não exceder máximo |
| BOUND-6 | tentativa chega a 4.097 | diagnóstico de compile-step; sem grafo |
| BOUND-7 | fonte dentro de AST, lowering >64 nós ou >128 arestas | falha no validator do grafo; sem persistir |
| BOUND-8 | combinador futuro bound 65 ou ausente | falha pré-avaliação; não entra em P4 sem contrato |

BOUND-6 requer que Sol defina código e unidade de cobrança. O teto 4.096 já é contrato.

## Proposta de codec — NÃO APROVADA

> **NÃO APROVADA. Não implementar, registrar ou tratar como formato público sem decisão explícita de Sol. Esta proposta deliberadamente não fixa nome de campo, tag, componente, versão de schema ou payload.**

Um futuro envelope deve:

- ter versão de linguagem própria, raiz tipada e limites verificáveis;
- usar variantes fechadas de AST e listas limitadas;
- carregar De Bruijn não negativo, validar escopo e tratar hints só como apresentação;
- nunca confiar em tipo, pureza, custo ou resultado declarados pelo cliente;
- encaminhar literal limitado para descriptor servidor, não para executor escolhido pela fonte;
- falhar fechado para edição em dado malformado/desconhecido/futuro, preservando ProgramGraph válido;
- ter rede igualmente limitada, sem deserialização de classe/callback;
- nunca reescrever item em leitura, tooltip, renderização ou sync.

Sol deve decidir local de persistência, limite de bytes, versão futura e política source-only.

## Matriz de atomicidade fonte/grafo

| Evento | Fonte candidata | Grafo existente | Resultado obrigatório | Escrita |
| --- | --- | --- | --- | --- |
| Decode falha | malformada | válido | grafo executa; edição diagnostica | nenhuma |
| Versão futura | desconhecida | válido | grafo executa; edição desabilitada | nenhuma |
| Estrutura/tipo/pureza falha | inválida | válido | estado anterior | nenhuma |
| Limite/redução/lowering falha | não compilável | válido | estado anterior; sem parcial | nenhuma |
| Grafo/política/recurso/conhecimento falha | válida até lowering | válido | estado anterior e diagnóstico | nenhuma |
| Interrupção pré-commit | válida | válido | estado anterior integral | nenhuma/rollback |
| Só fonte é escrita | válida | válido | proibido no sucesso funcional | não |
| Só grafo é escrito | válida | válido | proibido: não perder/incompatibilizar fonte pela segunda escrita | não |
| Fonte removida após sucesso | ausente | grafo válido | grafo executa; edição indisponível | remoção explícita |
| Grafo removido com fonte | válida ou não | ausente | não executável; export/recovery | leitura não repara |
| Reload remove rune só da fonte | talvez inválida | grafo válido | grafo segue autoridade; fonte diagnostica | nenhuma em leitura |

**Integração crítica:** ProgramStorage.saveValidated grava o grafo e no fluxo comum limpa GuidedWorkspacePersistence. A futura rota L0 não pode chamar esse caminho de forma que apague/substitua autoria sem decisão Sol e commit conjunto. Nome e recursos também precisam coexistência explícita.

## Decisões exigidas de Sol

1. Fronteira persistente opcional e semântica de source-only, sem escolher agora componente/tags/campos/schema/payload.
2. Transação de inscrição fonte+grafo e coexistência com guided workspace, nome e recursos.
3. Manutenção ou mudança da classificação de construtores de effect-plan como EFFECT.
4. Código/unidade do diagnóstico de compile-step.
5. Política de literal por tipo e descriptor confiável antes de literais além de número.
6. Snapshot de registry e comportamento de reload durante compilar/commit.
7. Se sharing é somente de bindings explícitos (recomendado em P4) ou inclui CSE de puro; observações nunca são deduplicadas por sintaxe.
8. Representação de inspector para fonte, redução e erros sem tornar hints identidade.

## Handoff: futura implementação Terra Medium

### Sequência

1. Declarar ownership de src/test/java/com/mathmod/language/ e adicionar vetores OBS-SHARE, TAIL, BOUND e contraprovas desta auditoria. Permanecem testes puros e schema-neutral.
2. Após decisão Sol, adicionar orçamento real de passos e ausência de grafo parcial.
3. Separar literal lowering em resolvedor confiável; testar finito, inválido e tipo sem suporte. Sem persistência neste passo.
4. Criar serviço server-side: snapshot registry, estrutura, checker, lowering, ProgramValidator, política executável, recursos e conhecimento. Não mutar item antes do sucesso integral.
5. Só após aprovação Sol, criar codec, componente e rede, com ownership explícito e testes de decode/versionamento.
6. Implementar commit dual-write/rollback; preservar grafo válido em toda falha de fonte.
7. Entregar inspector/editor e theorem somente depois de evidência de autoridade em servidor dedicado.

### Aceite

- Counterexamples e vetores passam por conectividade/pureza, não ids internos.
- Limites são recomputados no servidor a partir do dado decodificado.
- Lowering válido nunca basta para inscrever sem validação executável final.
- Toda falha pré-commit preserva item e autoria anterior.
- Fonte ausente/malformada/futura jamais incapacita grafo válido.
- Nenhuma implementação/teste congela campos, tags, Data Component, schema version ou payload sem Sol.

## Limites da auditoria

Nenhum teste foi adicionado: os vetores são suficientes para o handoff e evitaram disputar ownership futuro. Não foram alterados arquivos A0, produção, ProgramGraph, registro de Data Components, codecs, rede ou persistência.

