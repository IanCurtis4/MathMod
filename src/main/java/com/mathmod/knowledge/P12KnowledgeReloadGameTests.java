package com.mathmod.knowledge;

import com.mathmod.MathMod;
import com.mathmod.runes.MathModRuneBootstrap;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P12KnowledgeReloadGameTests {
    private P12KnowledgeReloadGameTests() {
    }

    @GameTest(template = "empty")
    public static void p12KnowledgeReloadRealLoadRejectsMalformedResourcesAtomically(GameTestHelper helper) {
        MathModRuneBootstrap.bootstrap();
        KnowledgeDefinitionReloadListener.apply(new KnowledgeDefinitionReloadListener.CandidatePublication.LoadResult(
                Map.of(), Map.of(), Map.of(), List.of()));
        ResourceLocation validId = ResourceLocation.fromNamespaceAndPath(
                "mathmod", "mathmod/epiphanies/vital_correspondence.json");
        KnowledgeDefinitionReloadListener.apply(KnowledgeDefinitionReloadListener.load(
                resources(validId, validJson(), "mathmod/epiphanies")));
        helper.assertTrue(successfulCasts() == 3, "real valid same-resource replacement must publish successful_casts=3");
        KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot();
        KnowledgeAliasRegistry aliases = KnowledgeAliases.current();

        applyAndRetain(helper, resources(validId, "{\"schema_version\":2}", "mathmod/epiphanies"), definitions, aliases, "malformed schema");
        applyAndRetain(helper, resources(validId, "{", "mathmod/epiphanies"), definitions, aliases, "malformed JSON");
        ResourceLocation invalidPath = ResourceLocation.fromNamespaceAndPath("mathmod", "not_epiphanies/bad.json");
        applyAndRetain(helper, resources(invalidPath, validJson(), "mathmod/epiphanies"), definitions, aliases, "invalid path");

        KnowledgeDefinitionReloadListener.apply(new KnowledgeDefinitionReloadListener.CandidatePublication.LoadResult(
                Map.of(), Map.of(), Map.of(), List.of()));
        helper.assertTrue(successfulCasts() == 2, "cleanup must restore built-in vital correspondence");
        helper.succeed();
    }

    private static void applyAndRetain(GameTestHelper helper, ResourceManager resources,
                                       KnowledgeDefinitionSnapshot definitions, KnowledgeAliasRegistry aliases, String label) {
        KnowledgeDefinitionReloadListener.apply(KnowledgeDefinitionReloadListener.load(resources));
        helper.assertTrue(KnowledgeDefinitions.snapshot() == definitions && KnowledgeAliases.current() == aliases && successfulCasts() == 3,
                label + " must retain exact prior definition and alias objects");
    }

    private static int successfulCasts() {
        return KnowledgeDefinitions.epiphany(KnowledgeDefinitions.VITAL_CORRESPONDENCE).orElseThrow()
                .studies().getFirst().successfulCasts();
    }

    private static ResourceManager resources(ResourceLocation id, String json, String directory) {
        return new ResourceManager() {
            @Override public Set<String> getNamespaces() { return Set.of("mathmod"); }
            @Override public Optional<Resource> getResource(ResourceLocation ignored) { return Optional.empty(); }
            @Override public List<Resource> getResourceStack(ResourceLocation ignored) { return List.of(); }
            @Override public Map<ResourceLocation, Resource> listResources(String requested, Predicate<ResourceLocation> filter) {
                return requested.equals(directory) ? Map.of(id, new StringResource(json)) : Map.of();
            }
            @Override public Map<ResourceLocation, List<Resource>> listResourceStacks(String ignored, Predicate<ResourceLocation> filter) { return Map.of(); }
            @Override public Stream<PackResources> listPacks() { return Stream.empty(); }
        };
    }

    private static final class StringResource extends Resource {
        private final byte[] bytes;
        private StringResource(String json) { super(null, () -> null); bytes = json.getBytes(StandardCharsets.UTF_8); }
        @Override public InputStream open() { return new ByteArrayInputStream(bytes); }
        @Override public String sourcePackId() { return "p12-gametest"; }
    }

    private static String validJson() {
        return "{\"schema_version\":1,\"title_key\":\"test.vital\",\"correlation\":\"mathmod:living\",\"studies\":[{\"material\":\"mathmod:one\",\"tier\":1,\"successful_casts\":3},{\"material\":\"mathmod:two\",\"tier\":2,\"successful_casts\":2}],\"grants\":[{\"kind\":\"rune\",\"id\":\"mathmod:vital_infusion_plan\"}]}";
    }
}
