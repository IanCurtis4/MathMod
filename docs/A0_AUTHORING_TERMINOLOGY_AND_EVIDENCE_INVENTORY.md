# A0 — Authoring Terminology and Evidence Inventory

**Status:** inventário read-only — 2026-07-26  
**Escopo:** terminologia, localização, Patchouli, previews, ícones/glyphs e evidência de acessibilidade.  
**Não realizado:** nenhuma alteração em Java, assets, traduções, Patchouli, ids persistentes, codecs ou semântica.

## 1. Baseline e regras de leitura

O contrato A0 separa `ProgramGraph` (execução) de apresentação, localização,
ícones, fórmulas, categorias, notação e estado de canvas. A tabela de
compatibilidade mantém 67 Rune Forms e 11 categorias; `GuidedWorkspaceState`
continua schema 1. Fontes: `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`
§§5.1–5.3, 8, 11, Appendix A; `docs/FOUNDATION_BETA_A0_ASSIGNMENT.md` §§3–4;
`docs/DELIVERY_BOARD.md`, tarefa `A0-LU-INVENTORY`.

“Atual” abaixo significa texto/identificador observado no repositório. “Proposta”
é somente recomendação editorial futura; não é congelamento de id nem decisão
semântica.

## 2. Tabela EN/PT-BR

| Conceito | Termo atual observado | Chave/arquivo exato | Proposta EN | Proposta PT-BR | Finding |
|---|---|---|---|---|---|
| Guided | `Laboratory` para composição e `Theorems` para construções preservadas | `en_us.json`: `screen.mathmod.rune_programmer.tab_custom`, `.tab_presets`, `.tab_custom_lore`; `pt_br.json`: mesmas keys | `Guided` como nome técnico de superfície; manter `Laboratory` como nome diegético se necessário | `Guiado`; manter `Laboratório` como nome diegético se necessário | Não há chave explícita `guided`; não substituir os rótulos atuais sem decisão de produto. |
| Inspector | `Rune Inspector` | `en_us.json`: `screen.mathmod.rune_inspector.title`; `pt_br.json`: mesma key | `Inspector` ou `Rune Inspector`, consistentemente | `Inspetor` ou `Inspetor de runas`, consistentemente | EN usa “Rune Inspector”; PT usa “Inspetor de runas”; o contrato chama a projeção de inspector. |
| Advanced | `Advanced Movement` em conteúdo de teorema; não há modo `ADVANCED` implementado | `.../entries/programming/beta_theorems.json` key `name`/texto; `docs/ADVANCED_EDITOR.md` §§1, 58–73; `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md` §13.1 | `Advanced` somente para a futura superfície direta | `Avançado` somente para a futura superfície direta | Não usar “Advanced” para o Inspector atual; o contrato diz que o editor mutável e sua persistência são futuros. |
| Source | `SOURCES` como categoria de formas; “source” aparece como linguagem/contrato | `en_us.json`/`pt_br.json`: `screen.mathmod.rune_programmer.custom.category.sources`; `docs/FUNCTIONAL_LANGUAGE.md` §§88–116 | `Source` para fonte funcional/textual, `Sources` apenas para categoria | `Fonte` para fonte funcional/textual, `Fontes` apenas para categoria | Categoria `SOURCES` não é a mesma coisa que `ScopedProgramSource`; separar por contexto. |
| Function | `f(x)` em notação de recursos; `Function[A, B]` na documentação | `en_us.json`: `screen.mathmod.rune_programmer.notation.function`; `docs/FUNCTIONAL_LANGUAGE.md` §§5, 104–116 | `Function` | `Função` | Há apresentação atual de `f(x)`, mas source funcional persistido/DSL continuam roadmap; não descrever como disponível. |
| Discipline | `typed discipline` em Patchouli; “Physical profile” em recursos | `.../entries/programming/alchemical_effects.json` key `pages[0].text`; `en_us.json`: `screen.mathmod.talisman_resources.construct_physics_estimate`; `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md` §7.5 | `Discipline` para especialização futura | `Disciplina` | Não há chave de UI para a especialização D0; “physical profile” não deve ser traduzido automaticamente como Discipline. |
| Notation | `notation` em lore e keys `notation.function`, `notation.symbol`, `notation.sum` | `.../entries/lore/runes_and_types.json` keys `pages[0..1].text`; `en_us.json`/`pt_br.json`: `screen.mathmod.rune_programmer.notation.*` e `screen.mathmod.talisman_resources.notation.*` | `Notation` para perfil/projeção de apresentação | `Notação` | O uso atual é local/compacto; perfis S0 são futuros e não devem ser apresentados como seleção disponível. |

## 3. Páginas Patchouli que precisarão de atualização

Os seguintes arquivos têm conteúdo diretamente atingido quando a terminologia
A0/A1/L0/D0/S0 for exposta. A lista indica necessidade de revisão futura, não
uma autorização para editar agora.

| Página | Evidência exata | Motivo do finding |
|---|---|---|
| `entries/programming/custom_programmer.json` | `name`/textos sobre `The Laboratory`, `Theorems`, `Inscribed` | É a principal superfície Guided atual; deve explicar qualquer distinção futura sem prometer editor Advanced. |
| `entries/programming/inspector.json` | `name` “The Rune Inspector” e textos de inspeção | Deve manter explícito que é read-only; não chamar o Inspector de Advanced. |
| `entries/programming/beta_theorems.json` | texto `This compact statement is not executable source`; página `Advanced Movement` | Precisa separar fórmula compacta, fonte e eventual “Advanced”; hoje são conceitos misturados no mesmo entry. |
| `entries/programming/typed_graphs.json` | texto `Theorem nodes read ...`; `exact technical ids` | Deve referenciar ids técnicos somente como inspeção, não como terminologia de jogador. |
| `entries/lore/runes_and_types.json` | textos sobre `notation` e grafo como demonstração | Candidato para glossário de Notation e relação entre projeção e grafo. |
| `entries/programming/alchemical_effects.json` | texto `typed discipline` | Evitar que “discipline” lore seja confundido com D0 antes de existir a superfície de Discipline. |
| `entries/basics/current_state.json` | texto `playable item and world carriers`, `Laboratory proofs` | Deve ser rechecado contra o estado real antes de prometer novas superfícies. |
| `entries/basics/can_i_make_spell.json` | instruções `Theorems`, `Inscribe`, `Resources`, `Proof` | É o primeiro-use entry e precisa acompanhar qualquer mudança de vocabulário. |

Cada página existe em `en_us` e `pt_br`; o par correspondente deve ser tratado
como unidade. Fonte dos paths: `src/main/resources/assets/mathmod/patchouli_books/field_manual/{en_us,pt_br}/entries/**`.

## 4. Gaps da matriz de previews

### Cobertura observada

`src/main/java/com/mathmod/client/UiPreviewMatrix.java` contém casos para
`rune-inspector` em EN/PT-BR, `minimum-viewport`, várias telas do Programmer/
Laboratory, jornadas de primeiro feitiço, tooltips, teclado, Patchouli,
`construct-preview` e recursos. `docs/UI_PREVIEWS.md` documenta, entre outros,
`advanced-theorem-*`, `advanced-laboratory-symmetry`, `patchouli-matrix` e
casos narrator/viewport.

### Gaps registrados

| Gap | Evidência exata | Classificação |
|---|---|---|
| Não existe caso explícito de superfície `Guided` nomeada como tal | `UiPreviewMatrix.java` cases `rune-inspector`, `minimum-viewport`; labels atuais `tab_presets`/`tab_custom` em `en_us.json` | Terminologia: falta cenário que prove o mapeamento editorial Guided → Theorems/Laboratory. |
| Não existe preview de editor Advanced mutável, pan/zoom, socket, edge mutation ou undo/redo | `docs/ADVANCED_EDITOR.md` §§17–47, 73–89; ausência de case correspondente em `UiPreviewMatrix.java` | Roadmap/evidência futura; não é falha do Inspector read-only. |
| Não existe preview de Source/Function textual ou round-trip parse/print | `docs/FUNCTIONAL_LANGUAGE.md` §§88–116; `docs/DELIVERY_BOARD.md`, `L0-TH-AUDIT`; ausência de case `source`/`function` | Futuro L0/L1; não inventar UI disponível. |
| Não existe preview de seleção/ordenação de Discipline | `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md` §7.5; ausência de case `discipline` | Futuro D0. |
| Não existe preview de seleção de perfil Notation | contrato A0 §13.3; ausência de case `notation-profile` | Futuro S0; há apenas compact notation em telas existentes. |
| A matriz prevê EN/PT-BR para muitos fluxos, mas não uma tabela de paridade para cada novo translation key de A0 | `UiPreviewMatrix.java`; `docs/UI_PREVIEWS.md` §cases de `advanced-*` e `patchouli-matrix` | Criar checklist de paridade quando keys forem adicionadas. |

## 5. Auditoria de reúso de ícones/glyphs

| Finding | Evidência exata | Resultado read-only |
|---|---|---|
| Ícone de form atual é resolvido por rune-id técnico | `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md` §§5.1, 6.4 (`RuneIcon(runeId)`), 12.1; `src/main/resources/assets/mathmod/textures/gui/runes/**` | Reúso por `runeId` é compatível com A0; não renomear textura nem id. |
| Motivo de bases compartilha família visual, mas mantém glyphs distintos | `docs/UI_PREVIEWS.md` texto dos cases `basis-icon-family`/`basis-icon-laboratory`; texturas `right_basis_vector.png`, `forward_basis_vector.png`, `oblique_basis_vector.png` | Reúso de família é desejável; não colapsar arquivos distintos. |
| Não há catálogo separado de glyphs de Notation/Discipline | diretório `textures/gui/runes`; contrato A0 §13.2–13.3 | Gap de inventário de futuros assets, não ausência semântica. |
| Fallback técnico está especificado | contrato A0 §§6.4, 11.1–11.2 | A futura auditoria deve testar fallback sem substituir o ícone persistente. |

## 6. Translation keys ausentes ou inconsistentes

### Conjunto de chaves

Comparação read-only de `en_us.json` e `pt_br.json`: não há chaves exclusivas
de um locale; os conjuntos estão alinhados. Isso não prova qualidade de texto.

### Findings de valor/terminologia

| Finding | EN | PT-BR | Evidência |
|---|---|---|---|
| Mesmo valor em EN e PT-BR, provavelmente não localizado | `itemGroup.mathmod` e `material.mathmod.bronze`, entre outros | mesmo valor | comparação dos objetos JSON `en_us.json`/`pt_br.json` |
| Categoria técnica permanece em caixa alta em ambos | `screen.mathmod.rune_programmer.custom.category.sources`: `SOURCES` | mesma key: `FONTES` | `en_us.json`/`pt_br.json`, key exata |
| Tradução de Inspector tem sinais de acentuação/normalização inconsistente | `Read-only projection of the authoritative proof` | `Projecao somente leitura da prova autoritativa`; `inspecao`, `Nenhum no`, `Entradas dinamicas` | `pt_br.json`: `screen.mathmod.rune_inspector.read_only`, `.open_hint`, `.empty`, `.dependencies` |
| “Tier” não está localizado | `screen.mathmod.talisman_resources.tooltip.tier`: `Tier: %s`; `...tier`: `Tier: %s / witness %s` | `Tier: %s`; `Tier: %s / testemunha %s` | ambos os lang files, keys exatas |
| “Formula” e “Expressão” divergem no mesmo campo | `screen.mathmod.rune_inspector.formula`: `Formula: %s` | `...formula`: `Expressão: %s` | ambos os lang files, key exata |
| “Cast”/“conjurar” não segue um glossário único | `item.mathmod.programmed_talisman.tooltip.action.cast`: `Cast with witnesses` | `Conjurar com testemunhos` | ambos os lang files, key exata; registrar para decisão editorial |

Não foram inferidas keys ausentes para `Guided`, `Advanced`, `Source`,
`Function`, `Discipline` ou perfil `Notation`: a ausência observada é de
conceito/chave explícita, mas os rótulos futuros dependem de decisão de produto.

## 7. Narrator e acessibilidade

### Evidência existente

Há keys explícitas de narration para a paleta e prova salva:
`screen.mathmod.rune_programmer.palette_narration` e
`screen.mathmod.rune_programmer.saved_palette_narration`, em ambos os locales.
`docs/UI_PREVIEWS.md` também exige narration para estados de workflow, navegação
por teclado, fórmulas e viewport; `docs/ADVANCED_EDITOR.md` §Accessibility
descreve requisitos futuros de nó, socket, edge e ações destrutivas.

### Gaps

| Gap | Evidência exata | Próxima evidência necessária |
|---|---|---|
| Não há keys explícitas de narrator para `Guided`, `Advanced`, `Source`, `Function`, `Discipline` ou `Notation` | busca nos dois lang files; keys de narration existentes citadas acima | Definir cópia somente quando a superfície existir. |
| Narration do Inspector não tem um conjunto dedicado para posição, seleção, bounds e ações | `en_us.json`/`pt_br.json`: somente `screen.mathmod.rune_inspector.*` de estado/conteúdo; `docs/ADVANCED_EDITOR.md` §Accessibility | Preview/teste de foco e leitura completa do Inspector. |
| Narrator PT-BR precisa de revisão de acentos e completude | `pt_br.json`: `screen.mathmod.rune_inspector.open_hint`, `.read_only`, `.empty`, `.dependencies` | Corrigir em etapa de localização autorizada, não neste inventário. |
| Não há evidência de narrator para futuro canvas Advanced | `docs/ADVANCED_EDITOR.md` §Accessibility; ausência de modo Advanced no contrato A0 | Caso de foco, socket compatível/incompatível, origem/destino e erro de validação. |
| Não há evidência de narration para Source/Function textual, nem para perfis de Notation/Discipline | ausência de keys/cases; contratos L0/D0/S0 futuros | Casos EN/PT-BR com leitura do contexto e fallback técnico. |

## 8. Documentação obsoleta ou contraditória

| Finding | Arquivos/keys exatos | Tratamento futuro |
|---|---|---|
| `Advanced Movement` pode ser lido como editor Advanced, embora o editor direto seja planejamento | `.../entries/programming/beta_theorems.json`, página/texto `Advanced Movement`; `docs/ADVANCED_EDITOR.md` §1 | Renomear/explicar no Patchouli quando houver escopo aprovado. |
| `docs/ADVANCED_EDITOR.md` descreve requisitos futuros, enquanto `screen.mathmod.rune_inspector.*` é read-only implementado | `docs/ADVANCED_EDITOR.md` §1; `en_us.json` keys `.read_only`, `.open_hint` | Manter ambos, mas marcar explicitamente “Inspector atual” versus “Advanced futuro”. |
| `docs/FUNCTIONAL_LANGUAGE.md` descreve arquitetura e slices futuras, mas a key `notation.function` já aparece na UI | `docs/FUNCTIONAL_LANGUAGE.md` §§88–116; `screen.mathmod.rune_programmer.notation.function` | Documentar `f(x)` como notação compacta atual, não como Source/Function persistido. |
| Patchouli usa `typed discipline` sem a especialização D0 | `.../entries/programming/alchemical_effects.json` texto `typed discipline`; roadmap §7.5 | Separar termo lore de termo de produto. |
| PT-BR tem títulos com mojibake no conteúdo carregado como texto | títulos de `.../pt_br/entries/**`, por exemplo `world_anchors.json` e `custom_programmer.json` | Confirmar encoding/renderização antes de qualquer reescrita; registrar como localização, não semântica. |

## 9. Work packages futuros dependentes apenas de ids congelados

Estes pacotes são de conteúdo/evidência e podem ser planejados sem criar novos
ids persistentes, desde que consumam os ids já congelados e não alterem
expansão, custo, tipo, executor ou `ProgramGraph`:

| Pacote | Dependência congelada | Evidência de entrada |
|---|---|---|
| LU-1 — glossário EN/PT-BR de superfície | form ids, category ids, rune ids e keys existentes | contrato A0 §§5, 6, Appendix A; `en_us.json`/`pt_br.json` |
| LU-2 — atualização bilateral de Patchouli Guided/Inspector | ids de entries e ids de forms existentes | paths `entries/basics/can_i_make_spell.json`, `entries/programming/custom_programmer.json`, `inspector.json` |
| LU-3 — catálogo de ícones por `RuneIcon(runeId)` | rune ids/texturas atuais | contrato A0 §6.4; `textures/gui/runes/**` |
| LU-4 — matriz EN/PT-BR de previews atuais | modes existentes (`rune-inspector`, Programmer/Laboratory, Patchouli) | `UiPreviewMatrix.java`, `docs/UI_PREVIEWS.md` |
| LU-5 — narrator do Inspector atual | keys `screen.mathmod.rune_inspector.*` e estado read-only | `en_us.json`/`pt_br.json`; `RuneInspectorScreen.java`; `docs/ADVANCED_EDITOR.md` |
| LU-6 — prova de fallback técnico | ids de forms/categories e fallback do contrato | A0 §§6.4, 11.1–11.2 |

Dependências que **não** são apenas ids congelados e portanto ficam fora desses
packages: novo schema Advanced, fonte persistida/DSL, seleção de Discipline,
perfis de Notation, novos parâmetros não representáveis no Guided schema 1,
novos glyph kinds ou mudança de expansão. Fontes: contrato A0 §§3, 6.3, 13;
`docs/DELIVERY_BOARD.md`, tarefas `A0-TH-01`, `A0-TM-02`, `L0-TH-AUDIT`.

## 10. Limites deste inventário

Este documento não aprova traduções, nomes de ids, novos assets, mudança de
Patchouli, modo Advanced, codec Source, Discipline, perfil Notation ou qualquer
comportamento semântico. Todos os findings acima são rastreáveis aos arquivos
e keys citados e devem ser reavaliados quando os gates A0-1/A0-2, A0-3/A0-4,
L0, D0 ou S0 mudarem de estado.
