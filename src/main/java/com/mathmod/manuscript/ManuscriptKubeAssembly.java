package com.mathmod.manuscript;

import com.mathmod.kubejs.KubeJsManuscriptDeclarationStore;

/** Common-side precedence helpers used by the reload adapter and pure tests. */
public final class ManuscriptKubeAssembly {
    private static final String MOD_ID = "mathmod";
    private ManuscriptKubeAssembly() {
    }

    public static void addKubeDeclarations(
            ManuscriptSnapshotBuilder builder,
            KubeJsManuscriptDeclarationStore.Snapshot declarations
    ) {
        declarations.traditions().values().forEach(definition ->
                builder.addTradition(definition, declarations.source()));
        declarations.manuscripts().values().forEach(definition ->
                builder.addManuscript(definition, declarations.source()));
        declarations.aliases().values().forEach(definition ->
                builder.addAlias(definition, declarations.source()));
    }

    public static ManuscriptDefinitionSource sourceForPackId(String sourcePackId) {
        return new ManuscriptDefinitionSource(
                isMathModBuiltInPack(sourcePackId)
                        ? ManuscriptSourceLayer.BUILT_IN
                        : ManuscriptSourceLayer.DATA_PACK,
                0,
                sourcePackId
        );
    }

    private static boolean isMathModBuiltInPack(String sourcePackId) {
        return MOD_ID.equals(sourcePackId)
                || ("mod/" + MOD_ID).equals(sourcePackId)
                || ("mod:" + MOD_ID).equals(sourcePackId);
    }
}
