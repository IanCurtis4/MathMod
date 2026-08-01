# L0-TM-04F5 — Handoff para Sol

## Resultado

A matriz Patchouli agora só abre uma entrada depois do reload do locale configurado. As quatro capturas PT-BR de `beta_theorems` renderizam português, não EN-US.

O overflow de texto observado nas páginas PT-BR p0 e p2 é reportado abaixo
como defeito editorial fora do ownership F5. Conforme a seção 4.1 do gate,
ele não bloqueia a revisão técnica desta correção de harness.

## Correção

- `previewLocaleReady(Minecraft)` configura o locale, inicia `reloadResourcePacks()` e só fica pronta após o future concluir e confirmar `minecraft.options.languageCode`.
- O ramo `patchouli-matrix` passa pela barreira antes de `runPatchouliMatrixTick`; não pode abrir/capturar no locale anterior.
- O caminho comum de preview reutiliza a mesma barreira.
- `PatchouliPreviewMatrixTest` prova que a barreira está antes da navegação e que a conclusão do reload é exigida.

## Evidência de captura

Matrizes completas EN-US e PT-BR foram executadas em 1024×800. As oito spreads arquivadas são:

- `mathmod-patchouli-matrix-programming-beta_theorems-p0-en_us-preview.png`
- `mathmod-patchouli-matrix-programming-beta_theorems-p2-en_us-preview.png`
- `mathmod-patchouli-matrix-programming-beta_theorems-p4-en_us-preview.png`
- `mathmod-patchouli-matrix-programming-beta_theorems-p6-en_us-preview.png`
- `mathmod-patchouli-matrix-programming-beta_theorems-p0-pt_br-preview.png`
- `mathmod-patchouli-matrix-programming-beta_theorems-p2-pt_br-preview.png`
- `mathmod-patchouli-matrix-programming-beta_theorems-p4-pt_br-preview.png`
- `mathmod-patchouli-matrix-programming-beta_theorems-p6-pt_br-preview.png`

As quatro PT-BR foram abertas individualmente: mostram `Teoremas da beta`/`Notação das fórmulas`, `Teclado e narrador`/`Movimento e Salto fatorado`, `Movimento local`/`Movimento de ordem superior` e `Leitura`/`Controle`. Não há fallback EN-US, mojibake ou ids brutos.

## Verificação

```text
cleanTest test --no-build-cache com as cinco classes do gate: BUILD SUCCESSFUL
build: BUILD SUCCESSFUL
```

Contagem focada: 14 métodos (3 `PortugueseLocalizationQuality`, 2 `PatchouliPreviewMatrix`, 3 `PatchouliFieldManual`, 4 `UiPreviewMatrix`, 2 `ServerSideIsolation`). Não há GameTest autorizado ou necessário.

## Limitação editorial transferida

PT-BR p0 e p2 ainda mostram cópia ordinária ultrapassando a base da página. Trata-se de comprimento do texto em `src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json`, não de locale.

F5 só autoriza `UiPreviewHarness.java`, `PatchouliPreviewMatrixTest.java` e este handoff; o conteúdo Patchouli é read-only. Pelo gate atualizado, a redução ou redistribuição dessa cópia passa para `L0-LU-01F`, de Luna, depois do aceite técnico de F5. Essa tarefa deve preservar as oito páginas e todas as alegações congeladas. Nenhum arquivo de conteúdo, runtime, schema, componente, networking, API pública ou L0-TM-05 foi alterado.
