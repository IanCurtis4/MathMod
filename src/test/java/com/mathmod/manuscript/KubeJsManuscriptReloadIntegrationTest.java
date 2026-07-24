package com.mathmod.manuscript;

import com.mathmod.kubejs.KubeJsManuscriptDeclarationStore;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KubeJsManuscriptReloadIntegrationTest {
    @Test
    void candidateAssemblyUsesBuiltInThenKubeJsThenDataPackPrecedence() {
        NamespacedId id = id("surveyors");
        TraditionDefinition builtIn = tradition(id, "tradition.test.built_in");
        TraditionDefinition kube = tradition(id, "tradition.test.kube");
        TraditionDefinition data = tradition(id, "tradition.test.data");
        KubeJsManuscriptDeclarationStore declarations = new KubeJsManuscriptDeclarationStore();
        declarations.register(kube);

        ManuscriptSnapshotBuilder builder = new ManuscriptSnapshotBuilder()
                .addTradition(builtIn, source(ManuscriptSourceLayer.BUILT_IN, "mod/mathmod"));
        ManuscriptKubeAssembly.addKubeDeclarations(builder, declarations.freeze());
        builder.addTradition(data, source(ManuscriptSourceLayer.DATA_PACK, "pack:override"));

        ManuscriptSnapshotBuildResult result = builder.build();
        assertEquals(data, result.snapshot().tradition(id).orElseThrow());
        assertEquals(source(ManuscriptSourceLayer.DATA_PACK, "pack:override"),
                result.snapshot().traditionSource(id).orElseThrow());
    }

    @Test
    void mathmodResourcePackIsClassifiedAsBuiltInInsteadOfDataPack() {
        assertEquals(ManuscriptSourceLayer.BUILT_IN,
                ManuscriptKubeAssembly.sourceForPackId("mod/mathmod").layer());
        assertEquals(ManuscriptSourceLayer.BUILT_IN,
                ManuscriptKubeAssembly.sourceForPackId("mathmod").layer());
        assertEquals(ManuscriptSourceLayer.DATA_PACK,
                ManuscriptKubeAssembly.sourceForPackId("pack:override").layer());
    }

    private static TraditionDefinition tradition(NamespacedId id, String nameKey) {
        return new TraditionDefinition(1, id, nameKey, nameKey + ".summary", id("paper"));
    }

    private static ManuscriptDefinitionSource source(ManuscriptSourceLayer layer, String name) {
        return new ManuscriptDefinitionSource(layer, 0, name);
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("test", path);
    }
}
