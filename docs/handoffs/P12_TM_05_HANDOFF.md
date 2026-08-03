# P12-TM-05 Handoff — Factored Leap Statement Presentation

## Resultado

Pronto para revisão de Sol. A tela do Programmer apresenta a declaração congelada de mathmod:factored_leap em até três linhas completas, move o viewport do grafo exatamente uma altura de linha quando necessário e preserva os teoremas de uma ou duas linhas. Nenhuma fórmula, identidade, grafo, persistência, componente de dados, rede, API pública ou localização foi alterada. DELIVERY_BOARD.md não foi editado por esta tarefa.

## Invariantes do contrato P12-SOL-03

1. Máximo de três linhas completas: theoremStatementLines usa a autoridade existente TheoremStatementPresentation e lança falha explícita acima de três; não há elipse, clip ou teto silencioso.
2. Texto congelado: o teste fixa let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0))). Os logs dos vetores registram essa fórmula e as três linhas em ordem a reconstituem apenas com quebras visuais.
3. Fonte real: FS-01/FS-02 mediram 100, 101 e 79 px em 103 px disponíveis; FS-03/FS-04 mediram as mesmas linhas em 101 px.
4. Geometria legada: só o caso acima de duas linhas desloca por LINE_HEIGHT. FS-05 selecionou mathmod:right_angle, manteve duas linhas e origem 115; Factored Leap usa 126, diferença exata de 11.
5. Deslocamento único: graphViewportY adiciona Math.max(0, theoremStatementLineCount() - 2) * LINE_HEIGHT.
6. Mesma origem: renderização, scissor, scroll máximo, scrollbar e hit-test chamam graphViewportY; FS-04 deriva o hover do mesmo método, sem o antigo 37 hardcoded.
7. Seleção e resize: init, assemblePreset e refreshPresetPreview chamam updateTheoremStatementGeometry; não há geometria stale.
8. Acessibilidade: o mesmo widget permanece renderizável, focável, narrado e com tooltip; sua altura agora contém todas as linhas.
9. Mínimo: FS-03 em 640x480/GUI 2 completou com hitbox 108x33 e origem 126 após a declaração; FS-04 confirmou o hover do segundo nó na origem deslocada.
10. Quarta linha: produção e preflight recusam mais de três linhas para qualquer teorema integrado.

## Preflight e catálogo

- requireTheoremStatementFit roda em todos os modos, inclusive laboratory-self-repeat, e aceita no máximo três linhas.
- O catálogo verifica todos os teoremas ordinários em layout padrão. A única exceção por identidade é mathmod:factored_leap, para a fórmula de catálogo congelada push(halve(look)+halve(up)).
- O catálogo compacto mantém o clipping já aceito e não executa a asserção de largura padrão. Assim, absorption_mantle e vital_infusion não viram falha nova no compacto; nenhuma exceção de identidade foi criada para eles.

## Vetores reais de cliente

| Vetor | Configuração | Resultado e artefato |
| --- | --- | --- |
| FS-01 | EN-US, 1024x800, GUI 2 | Factored Leap registrado; 103 px; 3 linhas 100/101/79; hitbox 110x33; origem 126. run/client/screenshots/mathmod-fs-01-en_us-preview.png |
| FS-02 | PT-BR, 1024x800, GUI 2 | Mesmo resultado geométrico; locale PT-BR ativo. run/client/screenshots/mathmod-fs-02-pt_br-preview.png |
| FS-03 | PT-BR, 640x480, GUI 2 | 101 px; 3 linhas 100/101/79; hitbox 108x33; origem 126. run/client/screenshots/mathmod-fs-03-pt_br-preview.png |
| FS-04 | PT-BR, 640x480, GUI 2 | Factored Leap e hover do segundo nó calculado por graphViewportY. run/client/screenshots/mathmod-fs-04-pt_br-preview.png |
| FS-05 | EN-US, 1024x800, GUI 2 | Legado mathmod:right_angle; 2 linhas; hitbox 140x22; origem preservada 115. run/client/screenshots/mathmod-fs-05-en_us-preview.png |

Cada nome contém vetor e locale. Os logs registram selectedTheoremId, largura disponível, quantidade e largura de cada linha, hitbox, origem e fórmula. Os avisos Windows/OSHI e ClosedChannelException posteriores ao encerramento normal não impediram nenhuma captura.

## Testes e comandos

O comando focado concluiu **38 testes, 0 falhas**:

- ProgramPresetsTest: 17.
- UiPreviewMatrixTest: 7.
- ProgrammerLayoutTest: 11.
- FactoredLeapStatementPresentationTest: 1.
- ServerSideIsolationTest: 2.

O comando focado exigido, cleanTest test --no-build-cache com essas cinco classes, retornou BUILD SUCCESSFUL. Também retornaram BUILD SUCCESSFUL:

- gradlew cleanTest test --no-build-cache
- gradlew build

A suíte completa registrou **531 testes, 0 falhas**. Os cinco previews usaram runClient --no-daemon --no-build-cache com MATHMOD_UI_PREVIEW=fs-01 até fs-05, locale e dimensões da tabela.

## Arquivos da tarefa

- Produção: src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java.
- Harness/matriz: src/main/java/com/mathmod/client/UiPreviewHarness.java e src/main/java/com/mathmod/client/UiPreviewMatrix.java.
- Testes: src/test/java/com/mathmod/client/UiPreviewMatrixTest.java e src/test/java/com/mathmod/client/screen/FactoredLeapStatementPresentationTest.java.
- Documentação: docs/UX_AUDIT.md e este handoff.

## Limites

Não foram alterados ProgramGraph, GuidedWorkspaceState, menus, rede, servidor, persistência, Data Components, fórmulas, identidades ou localizações. Não há migração nem escrita persistente durante leitura. O clipping aceito do catálogo compacto não é reivindicado como corrigido ou redesenhado. O aceite do gate continua sendo decisão exclusiva de Sol.

