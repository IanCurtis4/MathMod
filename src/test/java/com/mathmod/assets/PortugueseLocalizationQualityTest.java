package com.mathmod.assets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PortugueseLocalizationQualityTest {
    private static final Path ASSET_ROOT = Path.of("src/main/resources/assets/mathmod");
    private static final Path LANGUAGE = ASSET_ROOT.resolve("lang/pt_br.json");
    private static final Path PATCHOULI = ASSET_ROOT.resolve("patchouli_books/field_manual/pt_br");

    private static final Set<String> UNACCENTED_WORDS = Set.of(
            "nao", "talisma", "orcamento", "ancora", "runico", "runica",
            "construcao", "inscricao", "preparacao", "sequencia", "laboratorio",
            "posicao", "posicoes", "regiao", "regioes", "numero", "numeros",
            "proximo", "proxima", "proximos", "proximas", "vinculo", "vinculos",
            "saida", "tecnico", "tecnicos", "formula", "valido", "validos",
            "invalido", "invalidos", "direcao", "voce", "media", "ultimo",
            "maximo", "ligacao", "ligacoes", "arvore", "padrao", "logica",
            "espaco", "colecoes", "familias", "demonstracao", "tradicao",
            "convergencia", "operacao", "operacoes", "execucao", "tambem",
            "atraves", "possivel", "disponivel", "disponiveis", "estavel",
            "estaveis", "relacao", "relacoes", "condicao", "pratica", "portatil",
            "substancia", "precisao", "observacao", "alteracao", "composicao",
            "computacao", "divisao", "acoes", "catalogo", "codigo", "epicos",
            "implementacao", "seguranca", "serializacao", "cabecalho", "dominios"
    );

    private static final Set<String> ENGLISH_UI_RESIDUE = Set.of(
            "preset", "presets", "preview", "hover", "runtime", "budget",
            "default", "defaults", "feature", "features", "casting", "cooldown",
            "drop", "tooltip", "pack", "packs", "items"
    );

    private static final Set<String> RAW_TYPE_IDS = Set.of(
            "unit", "bool", "number", "vec3", "effect_plan"
    );

    private static final Set<String> TITLE_CASE_CONNECTORS = Set.of(
            "De", "Da", "Do", "Das", "Dos", "E", "Em", "Na", "No", "Nas",
            "Nos", "Ou", "Para", "Por", "Com", "Ao", "À"
    );

    @Test
    void playerFacingPortugueseAvoidsKnownAsciiAndEnglishResidue() throws IOException {
        Map<String, String> strings = new LinkedHashMap<>();
        collectStrings(readJson(LANGUAGE), "lang", strings);
        for (Path path : patchouliFiles()) {
            collectStrings(readJson(path), PATCHOULI.relativize(path).toString(), strings);
        }

        for (Map.Entry<String, String> entry : strings.entrySet()) {
            String value = entry.getValue();
            assertFalse(value.contains("Ã"), () -> "Possible mojibake at " + entry);
            assertNoForbiddenWord(entry, UNACCENTED_WORDS);
            assertNoForbiddenWord(entry, ENGLISH_UI_RESIDUE);
            if (entry.getKey().startsWith("lang.")) {
                assertNoForbiddenWord(entry, RAW_TYPE_IDS);
            }
        }
    }

    @Test
    void patchouliHeadingsUsePortugueseSentenceCase() throws IOException {
        List<Map.Entry<String, String>> headings = new ArrayList<>();
        for (Path path : patchouliFiles()) {
            collectNamedStrings(
                    readJson(path),
                    PATCHOULI.relativize(path).toString(),
                    Set.of("name", "title"),
                    headings
            );
        }

        for (Map.Entry<String, String> heading : headings) {
            String[] words = heading.getValue().split("\\s+");
            for (int index = 1; index < words.length; index++) {
                String word = words[index].replaceAll("[^\\p{L}À-ÿ]", "");
                assertFalse(
                        TITLE_CASE_CONNECTORS.contains(word),
                        () -> "Title-case connector at " + heading
                );
            }
        }
    }

    @Test
    void coreInterfaceVocabularyKeepsTheReviewedPortugueseForms() throws IOException {
        JsonObject language = readJson(LANGUAGE).getAsJsonObject();
        assertEquals("Talismã programável", text(language, "item.mathmod.programmed_talisman"));
        assertEquals("Giz rúnico", text(language, "item.mathmod.chalk"));
        assertEquals("Âncora rúnica", text(language, "block.mathmod.rune_anchor"));
        assertEquals("Programador rúnico", text(language, "screen.mathmod.rune_programmer"));
        assertEquals("Laboratório", text(language, "screen.mathmod.rune_programmer.tab_custom"));
        assertEquals("Formas rúnicas", text(language, "screen.mathmod.rune_programmer.forms"));
        assertEquals("Nome da magia", text(language, "screen.mathmod.rune_programmer.custom_name"));
        assertEquals("Válido. Orçamento: %s / %s.",
                text(language, "screen.mathmod.rune_programmer.valid"));
        assertEquals("∴ %s produz efeito.",
                text(language, "item.mathmod.programmed_talisman.executed_named"));
        assertEquals("Pronto. Feche e use o talismã para conjurar.",
                text(language, "screen.mathmod.talisman_resources.ready"));
        assertEquals("Σ(itens)",
                text(language, "screen.mathmod.talisman_resources.notation.symbol"));
    }

    private static void assertNoForbiddenWord(
            Map.Entry<String, String> entry,
            Set<String> forbiddenWords
    ) {
        String value = entry.getValue();
        for (String word : forbiddenWords) {
            Pattern pattern = Pattern.compile(
                    "(?i)(?<![\\p{L}\\p{N}_])" + Pattern.quote(word)
                            + "(?![\\p{L}\\p{N}_])"
            );
            assertFalse(
                    pattern.matcher(value).find(),
                    () -> "Unreviewed word '" + word + "' at " + entry
            );
        }
    }

    private static String text(JsonObject object, String key) {
        return object.get(key).getAsString();
    }

    private static List<Path> patchouliFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(PATCHOULI)) {
            return paths
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted()
                    .toList();
        }
    }

    private static JsonElement readJson(Path path) throws IOException {
        return JsonParser.parseString(Files.readString(path));
    }

    private static void collectStrings(
            JsonElement element,
            String location,
            Map<String, String> result
    ) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            result.put(location, element.getAsString());
            return;
        }
        if (element.isJsonArray()) {
            for (int index = 0; index < element.getAsJsonArray().size(); index++) {
                collectStrings(element.getAsJsonArray().get(index), location + "[" + index + "]", result);
            }
            return;
        }
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> child : element.getAsJsonObject().entrySet()) {
                collectStrings(child.getValue(), location + "." + child.getKey(), result);
            }
        }
    }

    private static void collectNamedStrings(
            JsonElement element,
            String location,
            Set<String> fieldNames,
            List<Map.Entry<String, String>> result
    ) {
        if (element.isJsonArray()) {
            for (int index = 0; index < element.getAsJsonArray().size(); index++) {
                collectNamedStrings(
                        element.getAsJsonArray().get(index),
                        location + "[" + index + "]",
                        fieldNames,
                        result
                );
            }
            return;
        }
        if (!element.isJsonObject()) {
            return;
        }
        for (Map.Entry<String, JsonElement> child : element.getAsJsonObject().entrySet()) {
            String childLocation = location + "." + child.getKey();
            if (fieldNames.contains(child.getKey())
                    && child.getValue().isJsonPrimitive()
                    && child.getValue().getAsJsonPrimitive().isString()) {
                result.add(Map.entry(childLocation, child.getValue().getAsString()));
            }
            collectNamedStrings(child.getValue(), childLocation, fieldNames, result);
        }
    }
}
