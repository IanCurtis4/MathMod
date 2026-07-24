package com.mathmod.kubejs;

public final class MathModKubeJS {
    private MathModKubeJS() {
    }

    public static void material(String id, String itemOrTag, int budgetBonus, int tier) {
        KubeJsCompat.configure(api -> api.material(id, itemOrTag, budgetBonus, tier));
    }

    public static void addMaterialAttribute(String id, String attribute, int amount) {
        KubeJsCompat.configure(api -> api.addMaterialAttribute(id, attribute, amount));
    }

    public static void setMaterialConsumed(String id, boolean consumed) {
        KubeJsCompat.configure(api -> api.setMaterialConsumed(id, consumed));
    }

    public static void setMaterialTier(String id, int tier) {
        KubeJsCompat.configure(api -> api.setMaterialTier(id, tier));
    }

    public static void setMaterialTranslationKey(String id, String translationKey) {
        KubeJsCompat.configure(api -> api.setMaterialTranslationKey(id, translationKey));
    }

    public static void setRuneBudget(String id, int budgetCost) {
        KubeJsCompat.configure(api -> api.setRuneBudget(id, budgetCost));
    }

    public static void setRuneTier(String id, int tier) {
        KubeJsCompat.configure(api -> api.setRuneTier(id, tier));
    }

    public static void addRuneMaterialRequirement(String id, String itemOrTag, int quantity) {
        KubeJsCompat.configure(api -> api.addRuneMaterialRequirement(id, itemOrTag, quantity));
    }

    public static void clearRuneMaterialRequirements(String id) {
        KubeJsCompat.configure(api -> api.clearRuneMaterialRequirements(id));
    }

    public static void addRuneAttributeRequirement(String id, String attribute, int amount) {
        KubeJsCompat.configure(api -> api.addRuneAttributeRequirement(id, attribute, amount));
    }

    public static void clearRuneAttributeRequirements(String id) {
        KubeJsCompat.configure(api -> api.clearRuneAttributeRequirements(id));
    }

    public static void enableRune(String id) {
        KubeJsCompat.configure(api -> api.enableRune(id));
    }

    public static void disableRune(String id) {
        KubeJsCompat.configure(api -> api.disableRune(id));
    }

    public static void setAnchorSacrifice(String selector, int count, double radius) {
        KubeJsCompat.configure(api -> api.setAnchorSacrifice(selector, count, radius));
    }

    public static void setOfferingSparkDrop(String itemId, int count) {
        KubeJsCompat.configure(api -> api.setOfferingSparkDrop(itemId, count));
    }

    public static void setWardingPulse(double radius, double strength) {
        KubeJsCompat.configure(api -> api.setWardingPulse(radius, strength));
    }

    public static KubeJsRuneSpec rune(String id) {
        return new KubeJsRuneSpec(id);
    }

    public static KubeJsEpiphanySpec epiphany(String id) {
        return new KubeJsEpiphanySpec(id);
    }

    public static KubeJsDiscoverySpec discovery(String id) {
        return new KubeJsDiscoverySpec(id);
    }

    public static KubeJsTraditionSpec tradition(String id) {
        return new KubeJsTraditionSpec(id);
    }

    public static KubeJsManuscriptSpec manuscript(String id) {
        return new KubeJsManuscriptSpec(id);
    }

    public static void manuscriptAlias(int schemaVersion, String from, String to) {
        KubeJsCompat.registerManuscriptAlias(new com.mathmod.manuscript.ManuscriptAliasDefinition(
                schemaVersion,
                com.mathmod.util.NamespacedId.parse(from),
                com.mathmod.util.NamespacedId.parse(to)
        ));
    }

    public static void knowledgeAlias(String kind, String alias, String target) {
        KubeJsCompat.configure(api -> api.registerKnowledgeAlias(kind, alias, target));
    }
}
