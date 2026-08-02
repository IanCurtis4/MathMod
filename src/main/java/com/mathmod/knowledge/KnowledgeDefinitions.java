package com.mathmod.knowledge;

import com.mathmod.MathMod;
import com.mathmod.program.ProgramPresets;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.util.NamespacedId;

import java.util.List;
import java.util.Optional;

public final class KnowledgeDefinitions {
    public static final NamespacedId HARMONIC_CORRESPONDENCE =
            NamespacedId.of(MathMod.MOD_ID, "harmonic_correspondence");
    public static final NamespacedId HARMONIC_MOTION =
            NamespacedId.of(MathMod.MOD_ID, "harmonic_motion");
    public static final NamespacedId ROTATED_HORIZON =
            NamespacedId.of(MathMod.MOD_ID, "rotated_horizon");
    public static final NamespacedId BOUND_MEASURE =
            NamespacedId.of(MathMod.MOD_ID, "bound_measure");
    public static final NamespacedId LEDGER_OF_REMAINDERS =
            NamespacedId.of(MathMod.MOD_ID, "ledger_of_remainders");
    public static final NamespacedId VITAL_CORRESPONDENCE =
            NamespacedId.of(MathMod.MOD_ID, "vital_correspondence");
    public static final NamespacedId LIVING_CORRELATION =
            NamespacedId.of(MathMod.MOD_ID, "living_correlation");
    public static final NamespacedId CONSERVED_REMAINDER =
            NamespacedId.of(MathMod.MOD_ID, "conserved_remainder");
    public static final NamespacedId ECONOMY_CORRELATION =
            NamespacedId.of(MathMod.MOD_ID, "economy_correlation");

    public static final EpiphanyDefinition HARMONIC_MOTION_EPIPHANY = new EpiphanyDefinition(
            HARMONIC_MOTION,
            "epiphany.mathmod.harmonic_motion.title",
            HARMONIC_CORRESPONDENCE,
            List.of(
                    new MaterialStudyRequirement(NamespacedId.of(MathMod.MOD_ID, "feather"), 1, 2),
                    new MaterialStudyRequirement(NamespacedId.of(MathMod.MOD_ID, "quartz"), 2, 2)
            ),
            List.of(
                    grantRune("number_sin"),
                    grantRune("number_cos"),
                    grantTheorem("harmonic_step")
            )
    );

    public static final DiscoveryDefinition ROTATED_HORIZON_DISCOVERY = new DiscoveryDefinition(
            ROTATED_HORIZON,
            ROTATED_HORIZON,
            "discovery.mathmod.rotated_horizon.title",
            NamespacedId.of(MathMod.MOD_ID, "lore/rotated_horizon"),
            List.of(
                    grantRune("cyclic_element"),
                    grantRune("cyclic_rotate_y"),
                    grantTheorem("quarter_turn")
            )
    );

    public static final DiscoveryDefinition BOUND_MEASURE_DISCOVERY = new DiscoveryDefinition(
            BOUND_MEASURE,
            BOUND_MEASURE,
            "discovery.mathmod.bound_measure.title",
            NamespacedId.of(MathMod.MOD_ID, "lore/bound_measure"),
            List.of(
                    grantRune("soul_bind_entities_plan"),
                    grantTheorem("soul_constraint")
            )
    );

    public static final DiscoveryDefinition LEDGER_OF_REMAINDERS_DISCOVERY = new DiscoveryDefinition(
            LEDGER_OF_REMAINDERS,
            LEDGER_OF_REMAINDERS,
            "discovery.mathmod.ledger_of_remainders.title",
            NamespacedId.of(MathMod.MOD_ID, "lore/ledger_of_remainders"),
            List.of(
                    grantRune("parsimony_plan"),
                    grantTheorem("axiom_of_parsimony")
            )
    );

    public static final EpiphanyDefinition VITAL_CORRESPONDENCE_EPIPHANY = new EpiphanyDefinition(
            VITAL_CORRESPONDENCE,
            "epiphany.mathmod.vital_correspondence.title",
            LIVING_CORRELATION,
            List.of(
                    new MaterialStudyRequirement(NamespacedId.of(MathMod.MOD_ID, "vital_salt"), 2, 2),
                    new MaterialStudyRequirement(NamespacedId.of(MathMod.MOD_ID, "binding_resin"), 3, 2)
            ),
            List.of(
                    grantRune("vital_infusion_plan"),
                    grantTheorem("vital_infusion")
            )
    );

    public static final EpiphanyDefinition CONSERVED_REMAINDER_EPIPHANY = new EpiphanyDefinition(
            CONSERVED_REMAINDER,
            "epiphany.mathmod.conserved_remainder.title",
            ECONOMY_CORRELATION,
            List.of(
                    new MaterialStudyRequirement(NamespacedId.of(MathMod.MOD_ID, "quartz"), 2, 2),
                    new MaterialStudyRequirement(NamespacedId.of(MathMod.MOD_ID, "axiomatic_ink"), 4, 2)
            ),
            List.of(
                    grantRune("conservation_plan"),
                    grantTheorem("conservation_lemma")
            )
    );

    private static final KnowledgeDefinitionRegistry REGISTRY = new KnowledgeDefinitionRegistry(
            List.of(
                    HARMONIC_MOTION_EPIPHANY,
                    VITAL_CORRESPONDENCE_EPIPHANY,
                    CONSERVED_REMAINDER_EPIPHANY
            ),
            List.of(
                    ROTATED_HORIZON_DISCOVERY,
                    BOUND_MEASURE_DISCOVERY,
                    LEDGER_OF_REMAINDERS_DISCOVERY
            )
    );

    private KnowledgeDefinitions() {
    }

    public static List<EpiphanyDefinition> epiphanies() {
        return snapshot().epiphanies();
    }

    public static List<DiscoveryDefinition> discoveries() {
        return snapshot().discoveries();
    }

    public static Optional<EpiphanyDefinition> epiphany(NamespacedId id) {
        return snapshot().epiphany(id);
    }

    public static Optional<DiscoveryDefinition> discoveryForManuscript(NamespacedId manuscriptId) {
        return snapshot().discoveryForManuscript(manuscriptId);
    }

    public static Optional<DiscoveryDefinition> discovery(NamespacedId id) {
        return snapshot().discovery(id);
    }

    public static KnowledgeDefinitionSnapshot snapshot() {
        return KnowledgeReloadPublication.definitions();
    }

    public static void registerKube(EpiphanyDefinition definition) {
        KnowledgeReloadPublication.registerKube(definition);
    }

    public static void registerKube(DiscoveryDefinition definition) {
        KnowledgeReloadPublication.registerKube(definition);
    }

    static void publishData(
            java.util.Map<NamespacedId, EpiphanyDefinition> epiphanies,
            java.util.Map<NamespacedId, DiscoveryDefinition> discoveries
    ) {
        KnowledgeReloadPublication.publishDefinitions(epiphanies, discoveries);
    }

    static KnowledgeDefinitionRegistry.Prepared prepareData(
            java.util.Map<NamespacedId, EpiphanyDefinition> epiphanies,
            java.util.Map<NamespacedId, DiscoveryDefinition> discoveries
    ) {
        return REGISTRY.prepareData(epiphanies, discoveries);
    }

    static KnowledgeDefinitionRegistry.Prepared prepareCurrent() {
        return REGISTRY.prepareCurrent();
    }

    static KnowledgeDefinitionRegistry.Prepared prepareKube(EpiphanyDefinition definition) {
        return REGISTRY.prepareKube(definition);
    }

    static KnowledgeDefinitionRegistry.Prepared prepareKube(DiscoveryDefinition definition) {
        return REGISTRY.prepareKube(definition);
    }

    static void commit(KnowledgeDefinitionRegistry.Prepared prepared) {
        REGISTRY.commit(prepared);
    }

    static KnowledgeDefinitionSnapshot rawSnapshot() {
        return REGISTRY.snapshot();
    }

    public static PlayerKnowledge grantP2P3LegacyAccess(PlayerKnowledge knowledge) {
        PlayerKnowledge changed = knowledge
                .grant(KnowledgeKind.CORRELATION, HARMONIC_CORRESPONDENCE)
                .grant(KnowledgeKind.EPIPHANY, HARMONIC_MOTION)
                .grant(KnowledgeKind.DISCOVERY, ROTATED_HORIZON);
        for (KnowledgeGrant grant : HARMONIC_MOTION_EPIPHANY.grants()) {
            changed = grant.apply(changed);
        }
        for (KnowledgeGrant grant : ROTATED_HORIZON_DISCOVERY.grants()) {
            changed = grant.apply(changed);
        }
        return changed;
    }

    public static PlayerKnowledge grantP6LegacyAccess(PlayerKnowledge knowledge) {
        PlayerKnowledge changed = knowledge
                .grant(KnowledgeKind.DISCOVERY, BOUND_MEASURE)
                .grant(KnowledgeKind.DISCOVERY, LEDGER_OF_REMAINDERS)
                .grant(KnowledgeKind.CORRELATION, LIVING_CORRELATION)
                .grant(KnowledgeKind.CORRELATION, ECONOMY_CORRELATION)
                .grant(KnowledgeKind.EPIPHANY, VITAL_CORRESPONDENCE)
                .grant(KnowledgeKind.EPIPHANY, CONSERVED_REMAINDER);
        for (KnowledgeGrant grant : BOUND_MEASURE_DISCOVERY.grants()) {
            changed = grant.apply(changed);
        }
        for (KnowledgeGrant grant : LEDGER_OF_REMAINDERS_DISCOVERY.grants()) {
            changed = grant.apply(changed);
        }
        for (KnowledgeGrant grant : VITAL_CORRESPONDENCE_EPIPHANY.grants()) {
            changed = grant.apply(changed);
        }
        for (KnowledgeGrant grant : CONSERVED_REMAINDER_EPIPHANY.grants()) {
            changed = grant.apply(changed);
        }
        return changed;
    }

    public static PlayerKnowledge grantLegacyAccess(PlayerKnowledge knowledge) {
        return grantP6LegacyAccess(grantP2P3LegacyAccess(knowledge));
    }

    public static void validateRuntime(RuneRegistry runes) {
        for (EpiphanyDefinition epiphany : epiphanies()) {
            validateDefinition(epiphany, runes);
        }
        for (DiscoveryDefinition discovery : discoveries()) {
            validateDefinition(discovery, runes);
        }
    }

    static void validateDefinition(EpiphanyDefinition definition, RuneRegistry runes) {
        validateGrants(definition.id(), definition.grants(), runes);
    }

    static void validateDefinition(DiscoveryDefinition definition, RuneRegistry runes) {
        validateGrants(definition.id(), definition.grants(), runes);
    }

    private static void validateGrants(
            NamespacedId owner,
            List<KnowledgeGrant> grants,
            RuneRegistry runes
    ) {
        for (KnowledgeGrant grant : grants) {
            boolean exists = switch (grant.kind()) {
                case RUNE -> runes.find(grant.id().toString()).isPresent();
                case THEOREM -> ProgramPresets.presetForId(grant.id().toString()).isPresent();
                default -> false;
            };
            if (!exists) {
                throw new IllegalStateException(owner + " grants missing " + grant.kind() + " " + grant.id());
            }
        }
    }

    private static KnowledgeGrant grantRune(String path) {
        return new KnowledgeGrant(KnowledgeKind.RUNE, NamespacedId.of(MathMod.MOD_ID, path));
    }

    private static KnowledgeGrant grantTheorem(String path) {
        return new KnowledgeGrant(KnowledgeKind.THEOREM, NamespacedId.of(MathMod.MOD_ID, path));
    }
}
