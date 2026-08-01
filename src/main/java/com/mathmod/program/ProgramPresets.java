package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ProgramPresets {
    public static final int STARTER_BUDGET = 16;
    public static final int TARGETING_BUDGET = 24;
    public static final int HOP_PRESET_ID = 0;
    public static final int DASH_PRESET_ID = 1;
    public static final int RAY_MARKER_PRESET_ID = 2;
    public static final int BLINK_PRESET_ID = 3;
    public static final int LIFT_PRESET_ID = 4;
    public static final int ARC_LEAP_PRESET_ID = 9;
    public static final int RECOIL_PRESET_ID = 10;
    public static final int ORE_CENTROID_PRESET_ID = 11;
    public static final int LIFE_CENTROID_PRESET_ID = 12;
    public static final int HOSTILE_LIFT_PRESET_ID = 13;
    public static final int VECTOR_WAVE_PRESET_ID = 14;
    public static final int HORIZON_WARD_PRESET_ID = 15;
    public static final int RIGHT_ANGLE_PRESET_ID = 16;
    public static final int PLANAR_DASH_PRESET_ID = 17;
    public static final int OBLIQUE_LEAP_PRESET_ID = 18;
    public static final int HARMONIC_STEP_PRESET_ID = 19;
    public static final int ORTHOGONAL_STEP_PRESET_ID = 20;
    public static final int QUARTER_TURN_PRESET_ID = 21;
    public static final int QUADRATURE_LEAP_PRESET_ID = 22;
    public static final int RESTORATION_EQUATION_PRESET_ID = 23;
    public static final int MERCURIAL_STEP_PRESET_ID = 24;
    public static final int UMBRAL_VEIL_PRESET_ID = 25;
    public static final int NOCTILUCENT_SIGHT_PRESET_ID = 26;
    public static final int WITHERING_COROLLARY_PRESET_ID = 27;
    public static final int SOUL_CONSTRAINT_PRESET_ID = 28;
    public static final int VITAL_INFUSION_PRESET_ID = 29;
    public static final int ALCHEMICAL_MANTLE_PRESET_ID = 30;
    public static final int AXIOM_OF_PARSIMONY_PRESET_ID = 31;
    public static final int CONSERVATION_LEMMA_PRESET_ID = 32;
    public static final int CAVALIERI_PROJECTILE_PRESET_ID = 33;
    public static final int CLEANSING_PROPOSITION_PRESET_ID = 34;
    public static final int RESISTANCE_LEMMA_PRESET_ID = 35;
    public static final int ABSORPTION_MANTLE_PRESET_ID = 36;
    static final int FACTORED_LEAP_PRESET_ID = 37;
    public static final String AMETHYST_SACRIFICE_SELECTOR =
            AnchorPresetConfig.DEFAULT_SACRIFICE_SELECTOR;
    static final String LEGACY_AMETHYST_SACRIFICE_SELECTOR = "minecraft:amethyst_shard";

    private static final List<TalismanPreset> TALISMAN_PRESETS = List.of(
            preset(HOP_PRESET_ID, "hop", TalismanPreset.Category.MOVEMENT, "hop", "push(self,(0,.35,0))", "push(up*.35)", "mathmod:push_self", TalismanPreset.Provenance.CONVERGENT_EXERCISE, ProgramPresets::hop),
            preset(DASH_PRESET_ID, "dash", TalismanPreset.Category.MOVEMENT, "dash", "push(self,look*.8)", "push(look*.8)", "mathmod:scale_vector", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::dash),
            preset(ARC_LEAP_PRESET_ID, "arc_leap", TalismanPreset.Category.MOVEMENT, "arc_leap", "push(self,norm(look)*.65+up)", "push(look+up)", "mathmod:vector_add", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::arcLeap),
            preset(RECOIL_PRESET_ID, "recoil", TalismanPreset.Category.MOVEMENT, "recoil", "push(self,look*-.65)", "push(-L*.65)", "mathmod:vector_subtract", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::recoil),
            preset(RIGHT_ANGLE_PRESET_ID, "right_angle", TalismanPreset.Category.MOVEMENT, "right_angle", "push(self,frame(self)*(.7,.08,0))", "push(frame·x)", "mathmod:right_basis_vector", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::rightAngle),
            preset(PLANAR_DASH_PRESET_ID, "planar_dash", TalismanPreset.Category.MOVEMENT, "planar_dash", "push(self,frame(self)*(0,.1,.85))", "push(frame·z)", "mathmod:forward_basis_vector", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::planarDash),
            preset(OBLIQUE_LEAP_PRESET_ID, "oblique_leap", TalismanPreset.Category.MOVEMENT, "oblique_leap", "push(self,frame(self)*(-.5,.3,.45))", "push(F·xz)", "mathmod:oblique_basis_vector", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::obliqueLeap),
            preset(HARMONIC_STEP_PRESET_ID, "harmonic_step", TalismanPreset.Category.MOVEMENT, "harmonic_step", "push(self,F*(cos(a),.12,sin(a)))", "push(F*polar)", "mathmod:number_cos", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::harmonicStep),
            preset(ORTHOGONAL_STEP_PRESET_ID, "orthogonal_step", TalismanPreset.Category.MOVEMENT, "orthogonal_step", "push(self,norm(cross(up,look))*.65)", "push(upxlook)", "mathmod:vector_cross", TalismanPreset.Provenance.HORIZON_MEASURERS, ProgramPresets::orthogonalStep),
            preset(QUARTER_TURN_PRESET_ID, "quarter_turn", TalismanPreset.Category.MOVEMENT, "quarter_turn", "push(self,C4(1)*look*.65)", "push(C4*look)", "mathmod:cyclic_rotate_y", TalismanPreset.Provenance.HORIZON_BOUNDARY_SYNTHESIS, ProgramPresets::quarterTurn),
            preset(QUADRATURE_LEAP_PRESET_ID, "quadrature_leap", TalismanPreset.Category.MOVEMENT, "quadrature_leap", "push(self,(0,S(sin,0..pi)/2,0))", "push(S(sin)/2)", "mathmod:simpson_integral", TalismanPreset.Provenance.GATHERERS_OF_MEANS, ProgramPresets::quadratureLeap),
            preset(BLINK_PRESET_ID, "blink", TalismanPreset.Category.MOVEMENT, "blink", "blink(self,hit(ray(self,12)))", "blink(ray12)", "mathmod:blink_self_to_hit", TalismanPreset.Provenance.HORIZON_BOUNDARY_SYNTHESIS, ProgramPresets::blink),
            preset(RAY_MARKER_PRESET_ID, "ray_marker", TalismanPreset.Category.SENSING, "ray_marker", "mark(hit(ray(self,16)))", "mark(ray16)", "mathmod:raycast_block", TalismanPreset.Provenance.HORIZON_BOUNDARY_SYNTHESIS, ProgramPresets::rayMarker),
            preset(ORE_CENTROID_PRESET_ID, "ore_centroid", TalismanPreset.Category.SENSING, "ore_centroid", "mark(mean(ores(5)))", "mark(meanO5)", "mathmod:average_position", TalismanPreset.Provenance.GATHERERS_OF_MEANS, ProgramPresets::oreCentroid),
            preset(LIFE_CENTROID_PRESET_ID, "life_centroid", TalismanPreset.Category.SENSING, "life_centroid", "mark(mean(living(8)))", "mark(meanL8)", "mathmod:entity_positions", TalismanPreset.Provenance.GATHERERS_OF_MEANS, ProgramPresets::lifeCentroid),
            preset(LIFT_PRESET_ID, "lift", TalismanPreset.Category.CONTROL, "lift", "push(nearest(4),up)", "push(N4,up)", "mathmod:push_entities_plan", TalismanPreset.Provenance.GATHERERS_OF_MEANS, ProgramPresets::liftNearbyEntities),
            preset(HOSTILE_LIFT_PRESET_ID, "hostile_lift", TalismanPreset.Category.CONTROL, "hostile_lift", "push(hostile(6),up)", "push(H6,up)", "mathmod:filter_entities", TalismanPreset.Provenance.GATHERERS_OF_MEANS, ProgramPresets::hostileLift),
            preset(VECTOR_WAVE_PRESET_ID, "vector_wave", TalismanPreset.Category.CONTROL, "vector_wave", "push(nearest(6),look*.55)", "push(near6,v)", "mathmod:execute_effect_plan", TalismanPreset.Provenance.HORIZON_MEANS_SYNTHESIS, ProgramPresets::vectorWave),
            preset(HORIZON_WARD_PRESET_ID, "horizon_ward", TalismanPreset.Category.CONTROL, "horizon_ward", "push(farthest(4),up)", "push(far4,up)", "mathmod:farthest_entities", TalismanPreset.Provenance.HORIZON_MEANS_SYNTHESIS, ProgramPresets::horizonWard),
            preset(RESTORATION_EQUATION_PRESET_ID, "restoration_equation", TalismanPreset.Category.ALCHEMY, "restoration_equation", "heal(self,6)", "heal(self,6)", "mathmod:heal_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::restorationEquation),
            preset(CLEANSING_PROPOSITION_PRESET_ID, "cleansing_proposition", TalismanPreset.Category.ALCHEMY, "cleansing_proposition", "cleanse(self)", "cleanse(self)", "mathmod:cleanse_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::cleansingProposition),
            preset(RESISTANCE_LEMMA_PRESET_ID, "resistance_lemma", TalismanPreset.Category.ALCHEMY, "resistance_lemma", "resist(self,30,II)", "resist(self,II)", "mathmod:resistance_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::resistanceLemma),
            preset(ABSORPTION_MANTLE_PRESET_ID, "absorption_mantle", TalismanPreset.Category.ALCHEMY, "absorption_mantle", "absorb(self,30,II)", "absorb(self,II)", "mathmod:absorption_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::absorptionMantle),
            preset(FACTORED_LEAP_PRESET_ID, "factored_leap", TalismanPreset.Category.MOVEMENT, "factored_leap", "let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))", "push(halve(look)+halve(up))", "mathmod:scale_vector", TalismanPreset.Provenance.HORIZON_MEASURERS, FactoredLeapTheorem::presentationGraph),
            preset(MERCURIAL_STEP_PRESET_ID, "mercurial_step", TalismanPreset.Category.ALCHEMY, "mercurial_step", "apply(speed,self,20,2)", "speed(self,II)", "mathmod:speed_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::mercurialStep),
            preset(UMBRAL_VEIL_PRESET_ID, "umbral_veil", TalismanPreset.Category.ALCHEMY, "umbral_veil", "apply(invis,self,30,1)", "invis(self,30)", "mathmod:invisibility_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::umbralVeil),
            preset(NOCTILUCENT_SIGHT_PRESET_ID, "noctilucent_sight", TalismanPreset.Category.ALCHEMY, "noctilucent_sight", "apply(night,self,60,1)", "night(self,60)", "mathmod:night_vision_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::noctilucentSight),
            preset(WITHERING_COROLLARY_PRESET_ID, "withering_corollary", TalismanPreset.Category.ALCHEMY, "withering_corollary", "apply(wither,H4,8,2)", "wither(H4,II)", "mathmod:wither_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::witheringCorollary),
            preset(SOUL_CONSTRAINT_PRESET_ID, "soul_constraint", TalismanPreset.Category.ALCHEMY, "soul_constraint", "bind(H4,pos(self),12)", "bind(H4,12)", "mathmod:soul_bind_entities_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::soulConstraint),
            preset(VITAL_INFUSION_PRESET_ID, "vital_infusion", TalismanPreset.Category.ALCHEMY, "vital_infusion", "infuse(vital,self,30,1)", "infuse(self,30)", "mathmod:vital_infusion_plan", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::vitalInfusion),
            preset(ALCHEMICAL_MANTLE_PRESET_ID, "alchemical_mantle", TalismanPreset.Category.ALCHEMY, "alchemical_mantle", "exec(speed(self)+night(self))", "exec(S+N)", "mathmod:combine_effect_plans", TalismanPreset.Provenance.COMPOUNDERS_OF_CORRESPONDENCE, ProgramPresets::alchemicalMantle),
            preset(AXIOM_OF_PARSIMONY_PRESET_ID, "axiom_of_parsimony", TalismanPreset.Category.METAMAGIC, "axiom_of_parsimony", "apply(parsimony,self,120,1)", "pars(self,I)", "mathmod:parsimony_plan", TalismanPreset.Provenance.KEEPERS_OF_THE_REMAINDER, ProgramPresets::axiomOfParsimony),
            preset(CONSERVATION_LEMMA_PRESET_ID, "conservation_lemma", TalismanPreset.Category.METAMAGIC, "conservation_lemma", "apply(conservation,self,120,1)", "cons(self,I)", "mathmod:conservation_plan", TalismanPreset.Provenance.KEEPERS_OF_THE_REMAINDER, ProgramPresets::conservationLemma),
            preset(CAVALIERI_PROJECTILE_PRESET_ID, "cavalieri_projectile", TalismanPreset.Category.CONTROL, "cavalieri_projectile", "launch(revolve(stone)*.5,look)", "launch(Cav)", "mathmod:launch_construct", TalismanPreset.Provenance.HORIZON_MEANS_SYNTHESIS, ProgramPresets::cavalieriProjectile)
    );
    private static final Map<String, TalismanPreset> TALISMAN_PRESETS_BY_ID = TALISMAN_PRESETS.stream()
            .collect(Collectors.toUnmodifiableMap(TalismanPreset::id, Function.identity()));

    private ProgramPresets() {
    }

    public static List<TalismanPreset> talismanPresets() {
        return TALISMAN_PRESETS;
    }

    public static Optional<TalismanPreset> presetForButton(int buttonId) {
        return TALISMAN_PRESETS.stream()
                .filter(preset -> preset.buttonId() == buttonId)
                .findFirst();
    }

    public static Optional<TalismanPreset> presetForId(String theoremId) {
        if (theoremId == null || theoremId.isBlank()) {
            return Optional.empty();
        }
        String normalized = theoremId.contains(":")
                ? theoremId.trim()
                : MathMod.MOD_ID + ":" + theoremId.trim();
        return Optional.ofNullable(TALISMAN_PRESETS_BY_ID.get(normalized));
    }

    public static Optional<TalismanPreset> presetForGraph(ProgramGraph graph) {
        return graph == null ? Optional.empty() : TALISMAN_PRESETS.stream()
                .filter(preset -> preset.graph().equals(graph))
                .findFirst();
    }

    private static TalismanPreset preset(
            int buttonId,
            String id,
            TalismanPreset.Category category,
            String translationSuffix,
            String formula,
            String catalogFormula,
            String iconRuneId,
            TalismanPreset.Provenance provenance,
            java.util.function.Supplier<ProgramGraph> graphFactory
    ) {
        String prefix = "screen.mathmod.rune_programmer.";
        return new TalismanPreset(
                buttonId,
                MathMod.MOD_ID + ":" + id,
                category,
                prefix + "preset_" + translationSuffix,
                prefix + translationSuffix + "_hint",
                formula,
                catalogFormula,
                iconRuneId,
                provenance,
                graphFactory
        );
    }

    public static ProgramGraph hop() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("x", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("y", "mathmod:constant_number", Map.of("value", "0.35")),
                        new ProgramNode("z", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("vector", "mathmod:vector_from_numbers"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("x", "vector", "x"),
                        new ProgramEdge("y", "vector", "y"),
                        new ProgramEdge("z", "vector", "z"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("vector", "push", "vector")
                ),
                "push",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph dash() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("factor", "mathmod:constant_number", Map.of("value", "0.8")),
                        new ProgramNode("vector", "mathmod:scale_vector"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("look", "vector", "vector"),
                        new ProgramEdge("factor", "vector", "factor"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("vector", "push", "vector")
                ),
                "push",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph arcLeap() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("normalized", "mathmod:vector_normalize"),
                        new ProgramNode("factor", "mathmod:constant_number", Map.of("value", "0.65")),
                        new ProgramNode("forward", "mathmod:scale_vector"),
                        new ProgramNode("x", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("y", "mathmod:constant_number", Map.of("value", "0.38")),
                        new ProgramNode("z", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("up", "mathmod:vector_from_numbers"),
                        new ProgramNode("arc", "mathmod:vector_add"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("look", "normalized", "vector"),
                        new ProgramEdge("normalized", "forward", "vector"),
                        new ProgramEdge("factor", "forward", "factor"),
                        new ProgramEdge("x", "up", "x"),
                        new ProgramEdge("y", "up", "y"),
                        new ProgramEdge("z", "up", "z"),
                        new ProgramEdge("forward", "arc", "a"),
                        new ProgramEdge("up", "arc", "b"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("arc", "push", "vector")
                ),
                "push",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph recoil() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("factor", "mathmod:constant_number", Map.of("value", "-0.65")),
                        new ProgramNode("vector", "mathmod:scale_vector"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("look", "vector", "vector"),
                        new ProgramEdge("factor", "vector", "factor"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("vector", "push", "vector")
                ),
                "push",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph rightAngle() {
        return localMovement(0.7D, 0.08D, 0.0D);
    }

    public static ProgramGraph planarDash() {
        return localMovement(0.0D, 0.1D, 0.85D);
    }

    public static ProgramGraph obliqueLeap() {
        return localMovement(-0.5D, 0.3D, 0.45D);
    }

    private static ProgramGraph localMovement(double right, double up, double forward) {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("frame", "mathmod:player_frame"),
                        new ProgramNode("right", "mathmod:constant_number", Map.of("value", Double.toString(right))),
                        new ProgramNode("up", "mathmod:constant_number", Map.of("value", Double.toString(up))),
                        new ProgramNode("forward", "mathmod:constant_number", Map.of("value", Double.toString(forward))),
                        new ProgramNode("local", "mathmod:vector_from_numbers"),
                        new ProgramNode("world", "mathmod:transform_local_vector"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "frame", "player"),
                        new ProgramEdge("right", "local", "x"),
                        new ProgramEdge("up", "local", "y"),
                        new ProgramEdge("forward", "local", "z"),
                        new ProgramEdge("frame", "world", "frame"),
                        new ProgramEdge("local", "world", "vector"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("world", "push", "vector")
                ),
                "push",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph harmonicStep() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("frame", "mathmod:player_frame"),
                        new ProgramNode("angle", "mathmod:constant_number", Map.of("value", Double.toString(Math.PI / 4.0D))),
                        new ProgramNode("right", "mathmod:number_cos"),
                        new ProgramNode("forward", "mathmod:number_sin"),
                        new ProgramNode("up", "mathmod:constant_number", Map.of("value", "0.12")),
                        new ProgramNode("local", "mathmod:vector_from_numbers"),
                        new ProgramNode("world", "mathmod:transform_local_vector"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "frame", "player"),
                        new ProgramEdge("angle", "right", "angle"),
                        new ProgramEdge("angle", "forward", "angle"),
                        new ProgramEdge("right", "local", "x"),
                        new ProgramEdge("up", "local", "y"),
                        new ProgramEdge("forward", "local", "z"),
                        new ProgramEdge("frame", "world", "frame"),
                        new ProgramEdge("local", "world", "vector"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("world", "push", "vector")
                ),
                "push",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph orthogonalStep() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("zero", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("one", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("up", "mathmod:vector_from_numbers"),
                        new ProgramNode("cross", "mathmod:vector_cross"),
                        new ProgramNode("direction", "mathmod:vector_normalize"),
                        new ProgramNode("factor", "mathmod:constant_number", Map.of("value", "0.65")),
                        new ProgramNode("movement", "mathmod:scale_vector"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("zero", "up", "x"),
                        new ProgramEdge("one", "up", "y"),
                        new ProgramEdge("zero", "up", "z"),
                        new ProgramEdge("up", "cross", "a"),
                        new ProgramEdge("look", "cross", "b"),
                        new ProgramEdge("cross", "direction", "vector"),
                        new ProgramEdge("direction", "movement", "vector"),
                        new ProgramEdge("factor", "movement", "factor"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("movement", "push", "vector")
                ),
                "push",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph quarterTurn() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("order", "mathmod:constant_number", Map.of("value", "4")),
                        new ProgramNode("value", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("turn", "mathmod:cyclic_element"),
                        new ProgramNode("rotated", "mathmod:cyclic_rotate_y"),
                        new ProgramNode("factor", "mathmod:constant_number", Map.of("value", "0.65")),
                        new ProgramNode("movement", "mathmod:scale_vector"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("order", "turn", "order"),
                        new ProgramEdge("value", "turn", "value"),
                        new ProgramEdge("turn", "rotated", "element"),
                        new ProgramEdge("look", "rotated", "vector"),
                        new ProgramEdge("rotated", "movement", "vector"),
                        new ProgramEdge("factor", "movement", "factor"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("movement", "push", "vector")
                ),
                "push",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph quadratureLeap() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("t0", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("t1", "mathmod:constant_number", Map.of("value", Double.toString(Math.PI / 4.0D))),
                        new ProgramNode("t2", "mathmod:constant_number", Map.of("value", Double.toString(Math.PI / 2.0D))),
                        new ProgramNode("f0", "mathmod:number_sin"),
                        new ProgramNode("f1", "mathmod:number_sin"),
                        new ProgramNode("f2", "mathmod:number_sin"),
                        new ProgramNode("area", "mathmod:simpson_integral"),
                        new ProgramNode("half", "mathmod:constant_number", Map.of("value", "0.5")),
                        new ProgramNode("height", "mathmod:number_multiply"),
                        new ProgramNode("movement", "mathmod:vector_from_numbers"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("t0", "f0", "angle"),
                        new ProgramEdge("t1", "f1", "angle"),
                        new ProgramEdge("t2", "f2", "angle"),
                        new ProgramEdge("f0", "area", "start"),
                        new ProgramEdge("f1", "area", "midpoint"),
                        new ProgramEdge("f2", "area", "end"),
                        new ProgramEdge("t2", "area", "width"),
                        new ProgramEdge("area", "height", "a"),
                        new ProgramEdge("half", "height", "b"),
                        new ProgramEdge("t0", "movement", "x"),
                        new ProgramEdge("height", "movement", "y"),
                        new ProgramEdge("t0", "movement", "z"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("movement", "push", "vector")
                ),
                "push",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph rayMarker() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("range", "mathmod:constant_number", Map.of("value", "16")),
                        new ProgramNode("hit", "mathmod:raycast_block"),
                        new ProgramNode("position", "mathmod:ray_hit_position"),
                        new ProgramNode("marker", "mathmod:debug_marker")
                ),
                List.of(
                        new ProgramEdge("self", "hit", "player"),
                        new ProgramEdge("range", "hit", "range"),
                        new ProgramEdge("hit", "position", "hit"),
                        new ProgramEdge("position", "marker", "position")
                ),
                "marker",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph blink() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("range", "mathmod:constant_number", Map.of("value", "12")),
                        new ProgramNode("hit", "mathmod:raycast_block"),
                        new ProgramNode("blink", "mathmod:blink_self_to_hit")
                ),
                List.of(
                        new ProgramEdge("self", "hit", "player"),
                        new ProgramEdge("range", "hit", "range"),
                        new ProgramEdge("self", "blink", "player"),
                        new ProgramEdge("hit", "blink", "hit")
                ),
                "blink",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph liftNearbyEntities() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", "any_living",
                                "radius", "5",
                                "limit", "8"
                        )),
                        new ProgramNode("filtered", "mathmod:filter_entities", Map.of("predicate", "non_player_living")),
                        new ProgramNode("nearest", "mathmod:nearest_entities", Map.of("limit", "4")),
                        new ProgramNode("x", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("y", "mathmod:constant_number", Map.of("value", "0.55")),
                        new ProgramNode("z", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("vector", "mathmod:vector_from_numbers"),
                        new ProgramNode("plan", "mathmod:push_entities_plan"),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "filtered", "entities"),
                        new ProgramEdge("filtered", "nearest", "entities"),
                        new ProgramEdge("center", "nearest", "origin"),
                        new ProgramEdge("x", "vector", "x"),
                        new ProgramEdge("y", "vector", "y"),
                        new ProgramEdge("z", "vector", "z"),
                        new ProgramEdge("nearest", "plan", "entities"),
                        new ProgramEdge("vector", "plan", "vector"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph oreCentroid() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("ores", "mathmod:nearby_blocks", Map.of(
                                "selector", "#c:ores",
                                "radius", "5",
                                "limit", "64"
                        )),
                        new ProgramNode("positions", "mathmod:block_positions"),
                        new ProgramNode("centroid", "mathmod:average_position"),
                        new ProgramNode("marker", "mathmod:debug_marker")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "ores", "center"),
                        new ProgramEdge("ores", "positions", "blocks"),
                        new ProgramEdge("positions", "centroid", "positions"),
                        new ProgramEdge("centroid", "marker", "position")
                ),
                "marker",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph lifeCentroid() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", "any_living",
                                "radius", "8",
                                "limit", "16"
                        )),
                        new ProgramNode("filtered", "mathmod:filter_entities", Map.of("predicate", "non_player_living")),
                        new ProgramNode("positions", "mathmod:entity_positions"),
                        new ProgramNode("centroid", "mathmod:average_position"),
                        new ProgramNode("marker", "mathmod:debug_marker")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "filtered", "entities"),
                        new ProgramEdge("filtered", "positions", "entities"),
                        new ProgramEdge("positions", "centroid", "positions"),
                        new ProgramEdge("centroid", "marker", "position")
                ),
                "marker",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph hostileLift() {
        return entityPushPreset("hostile", false, 6, 6, 0.0D, 0.48D, 0.0D);
    }

    public static ProgramGraph vectorWave() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", "any_living",
                                "radius", "6",
                                "limit", "12"
                        )),
                        new ProgramNode("filtered", "mathmod:filter_entities", Map.of("predicate", "non_player_living")),
                        new ProgramNode("nearest", "mathmod:nearest_entities", Map.of("limit", "6")),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("factor", "mathmod:constant_number", Map.of("value", "0.55")),
                        new ProgramNode("vector", "mathmod:scale_vector"),
                        new ProgramNode("plan", "mathmod:push_entities_plan"),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "filtered", "entities"),
                        new ProgramEdge("filtered", "nearest", "entities"),
                        new ProgramEdge("center", "nearest", "origin"),
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("look", "vector", "vector"),
                        new ProgramEdge("factor", "vector", "factor"),
                        new ProgramEdge("nearest", "plan", "entities"),
                        new ProgramEdge("vector", "plan", "vector"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph horizonWard() {
        return entityPushPreset("hostile", true, 9, 4, 0.0D, 0.42D, 0.0D);
    }

    public static ProgramGraph restorationEquation() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("targets", "mathmod:player_as_entity_list"),
                        new ProgramNode("amount", "mathmod:constant_number", Map.of("value", "6")),
                        new ProgramNode("plan", "mathmod:heal_entities_plan"),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "targets", "player"),
                        new ProgramEdge("targets", "plan", "entities"),
                        new ProgramEdge("amount", "plan", "amount"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph mercurialStep() {
        return selfStatusPreset("mathmod:speed_entities_plan", 20, 2);
    }

    public static ProgramGraph umbralVeil() {
        return selfStatusPreset("mathmod:invisibility_entities_plan", 30, 1);
    }

    public static ProgramGraph noctilucentSight() {
        return selfStatusPreset("mathmod:night_vision_entities_plan", 60, 1);
    }

    public static ProgramGraph witheringCorollary() {
        return hostileStatusPreset("mathmod:wither_entities_plan", 8, 2);
    }

    public static ProgramGraph soulConstraint() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", "hostile",
                                "radius", "8",
                                "limit", "8"
                        )),
                        new ProgramNode("targets", "mathmod:nearest_entities", Map.of("limit", "4")),
                        new ProgramNode("duration", "mathmod:constant_number", Map.of("value", "12")),
                        new ProgramNode("plan", "mathmod:soul_bind_entities_plan"),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "targets", "entities"),
                        new ProgramEdge("center", "targets", "origin"),
                        new ProgramEdge("targets", "plan", "entities"),
                        new ProgramEdge("center", "plan", "anchor"),
                        new ProgramEdge("duration", "plan", "duration"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph vitalInfusion() {
        return selfStatusPreset("mathmod:vital_infusion_plan", 30, 1);
    }

    public static ProgramGraph cleansingProposition() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("targets", "mathmod:player_as_entity_list"),
                        new ProgramNode("plan", "mathmod:cleanse_entities_plan"),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "targets", "player"),
                        new ProgramEdge("targets", "plan", "entities"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph resistanceLemma() {
        return selfStatusPreset("mathmod:resistance_entities_plan", 30, 2);
    }

    public static ProgramGraph absorptionMantle() {
        return selfStatusPreset("mathmod:absorption_entities_plan", 30, 2);
    }

    public static ProgramGraph axiomOfParsimony() {
        return selfStatusPreset("mathmod:parsimony_plan", 120, 1);
    }

    public static ProgramGraph conservationLemma() {
        return selfStatusPreset("mathmod:conservation_plan", 120, 1);
    }

    public static ProgramGraph alchemicalMantle() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("targets", "mathmod:player_as_entity_list"),
                        new ProgramNode("speed_duration", "mathmod:constant_number", Map.of("value", "20")),
                        new ProgramNode("speed_level", "mathmod:constant_number", Map.of("value", "2")),
                        new ProgramNode("night_duration", "mathmod:constant_number", Map.of("value", "60")),
                        new ProgramNode("night_level", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("speed", "mathmod:speed_entities_plan"),
                        new ProgramNode("night", "mathmod:night_vision_entities_plan"),
                        new ProgramNode("combined", "mathmod:combine_effect_plans"),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "targets", "player"),
                        new ProgramEdge("targets", "speed", "entities"),
                        new ProgramEdge("speed_duration", "speed", "duration"),
                        new ProgramEdge("speed_level", "speed", "level"),
                        new ProgramEdge("targets", "night", "entities"),
                        new ProgramEdge("night_duration", "night", "duration"),
                        new ProgramEdge("night_level", "night", "level"),
                        new ProgramEdge("speed", "combined", "first"),
                        new ProgramEdge("night", "combined", "second"),
                        new ProgramEdge("combined", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    private static ProgramGraph selfStatusPreset(String runeId, int duration, int level) {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("targets", "mathmod:player_as_entity_list"),
                        new ProgramNode("duration", "mathmod:constant_number", Map.of("value", Integer.toString(duration))),
                        new ProgramNode("level", "mathmod:constant_number", Map.of("value", Integer.toString(level))),
                        new ProgramNode("plan", runeId),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "targets", "player"),
                        new ProgramEdge("targets", "plan", "entities"),
                        new ProgramEdge("duration", "plan", "duration"),
                        new ProgramEdge("level", "plan", "level"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    private static ProgramGraph hostileStatusPreset(String runeId, int duration, int level) {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", "hostile",
                                "radius", "8",
                                "limit", "8"
                        )),
                        new ProgramNode("targets", "mathmod:nearest_entities", Map.of("limit", "4")),
                        new ProgramNode("duration", "mathmod:constant_number", Map.of("value", Integer.toString(duration))),
                        new ProgramNode("level", "mathmod:constant_number", Map.of("value", Integer.toString(level))),
                        new ProgramNode("plan", runeId),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "targets", "entities"),
                        new ProgramEdge("center", "targets", "origin"),
                        new ProgramEdge("targets", "plan", "entities"),
                        new ProgramEdge("duration", "plan", "duration"),
                        new ProgramEdge("level", "plan", "level"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    private static ProgramGraph entityPushPreset(
            String predicate,
            boolean farthest,
            int radius,
            int limit,
            double x,
            double y,
            double z
    ) {
        String selectorRune = farthest ? "mathmod:farthest_entities" : "mathmod:nearest_entities";
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", predicate,
                                "radius", Integer.toString(radius),
                                "limit", "12"
                        )),
                        new ProgramNode("selected", selectorRune, Map.of("limit", Integer.toString(limit))),
                        new ProgramNode("x", "mathmod:constant_number", Map.of("value", Double.toString(x))),
                        new ProgramNode("y", "mathmod:constant_number", Map.of("value", Double.toString(y))),
                        new ProgramNode("z", "mathmod:constant_number", Map.of("value", Double.toString(z))),
                        new ProgramNode("vector", "mathmod:vector_from_numbers"),
                        new ProgramNode("plan", "mathmod:push_entities_plan"),
                        new ProgramNode("execute", "mathmod:execute_effect_plan")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "selected", "entities"),
                        new ProgramEdge("center", "selected", "origin"),
                        new ProgramEdge("x", "vector", "x"),
                        new ProgramEdge("y", "vector", "y"),
                        new ProgramEdge("z", "vector", "z"),
                        new ProgramEdge("selected", "plan", "entities"),
                        new ProgramEdge("vector", "plan", "vector"),
                        new ProgramEdge("plan", "execute", "plan")
                ),
                "execute",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph anchorPulse() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("marker", "mathmod:debug_marker")
                ),
                List.of(new ProgramEdge("origin", "marker", "position")),
                "marker",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph sacrificePulse() {
        return sacrificePulse(
                AnchorPresetConfig.sacrificeSelector(),
                AnchorPresetConfig.sacrificeCount(),
                AnchorPresetConfig.sacrificeRadius()
        );
    }

    static ProgramGraph sacrificePulse(String sacrificeSelector) {
        return sacrificePulse(sacrificeSelector, AnchorPresetConfig.DEFAULT_SACRIFICE_COUNT, AnchorPresetConfig.DEFAULT_SACRIFICE_RADIUS);
    }

    static ProgramGraph sacrificePulse(String sacrificeSelector, int sacrificeCount, double sacrificeRadius) {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("sacrifice", "mathmod:consume_nearby_item", Map.of(
                                "item", sacrificeSelector,
                                "count", Integer.toString(sacrificeCount),
                                "radius", Double.toString(sacrificeRadius)
                        )),
                        new ProgramNode("marker", "mathmod:debug_marker")
                ),
                List.of(
                        new ProgramEdge("origin", "sacrifice", "position"),
                        new ProgramEdge("sacrifice", "marker", "position")
                ),
                "marker",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph offeringSpark() {
        return offeringSpark(
                AnchorPresetConfig.sacrificeSelector(),
                AnchorPresetConfig.sacrificeCount(),
                AnchorPresetConfig.sacrificeRadius(),
                AnchorPresetConfig.offeringItem(),
                AnchorPresetConfig.offeringCount()
        );
    }

    static ProgramGraph offeringSpark(String sacrificeSelector) {
        return offeringSpark(
                sacrificeSelector,
                AnchorPresetConfig.DEFAULT_SACRIFICE_COUNT,
                AnchorPresetConfig.DEFAULT_SACRIFICE_RADIUS,
                AnchorPresetConfig.DEFAULT_OFFERING_ITEM,
                AnchorPresetConfig.DEFAULT_OFFERING_COUNT
        );
    }

    static ProgramGraph offeringSpark(
            String sacrificeSelector,
            int sacrificeCount,
            double sacrificeRadius,
            String offeringItem,
            int offeringCount
    ) {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("sacrifice", "mathmod:consume_nearby_item", Map.of(
                                "item", sacrificeSelector,
                                "count", Integer.toString(sacrificeCount),
                                "radius", Double.toString(sacrificeRadius)
                        )),
                        new ProgramNode("drop", "mathmod:spawn_item", Map.of(
                                "item", offeringItem,
                                "count", Integer.toString(offeringCount)
                        ))
                ),
                List.of(
                        new ProgramEdge("origin", "sacrifice", "position"),
                        new ProgramEdge("sacrifice", "drop", "position")
                ),
                "drop",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph wardingPulse() {
        return wardingPulse(
                AnchorPresetConfig.sacrificeSelector(),
                AnchorPresetConfig.sacrificeCount(),
                AnchorPresetConfig.sacrificeRadius(),
                AnchorPresetConfig.wardRadius(),
                AnchorPresetConfig.wardStrength()
        );
    }

    static ProgramGraph wardingPulse(String sacrificeSelector) {
        return wardingPulse(
                sacrificeSelector,
                AnchorPresetConfig.DEFAULT_SACRIFICE_COUNT,
                AnchorPresetConfig.DEFAULT_SACRIFICE_RADIUS,
                AnchorPresetConfig.DEFAULT_WARD_RADIUS,
                AnchorPresetConfig.DEFAULT_WARD_STRENGTH
        );
    }

    static ProgramGraph wardingPulse(
            String sacrificeSelector,
            int sacrificeCount,
            double sacrificeRadius,
            double wardRadius,
            double wardStrength
    ) {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("sacrifice", "mathmod:consume_nearby_item", Map.of(
                                "item", sacrificeSelector,
                                "count", Integer.toString(sacrificeCount),
                                "radius", Double.toString(sacrificeRadius)
                        )),
                        new ProgramNode("pulse", "mathmod:pulse_nearby_entities", Map.of(
                                "radius", Double.toString(wardRadius),
                                "strength", Double.toString(wardStrength)
                        ))
                ),
                List.of(
                        new ProgramEdge("origin", "sacrifice", "position"),
                        new ProgramEdge("sacrifice", "pulse", "position")
                ),
                "pulse",
                STARTER_BUDGET
        );
    }

    public static ProgramGraph kineticTransducer() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("entities", "mathmod:sense_nearby_entities", Map.of(
                                "predicate", "any_living",
                                "radius", "8",
                                "limit", "8"
                        )),
                        new ProgramNode("velocities", "mathmod:entity_velocities"),
                        new ProgramNode("speeds", "mathmod:vector_lengths"),
                        new ProgramNode("mean", "mathmod:mean_number"),
                        new ProgramNode("gain", "mathmod:constant_number", Map.of("value", "40")),
                        new ProgramNode("scaled", "mathmod:number_multiply"),
                        new ProgramNode("rounded", "mathmod:number_round"),
                        new ProgramNode("minimum", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("maximum", "mathmod:constant_number", Map.of("value", "15")),
                        new ProgramNode("power", "mathmod:number_clamp"),
                        new ProgramNode("duration", "mathmod:constant_number", Map.of("value", "10")),
                        new ProgramNode("emit", "mathmod:emit_anchor_redstone")
                ),
                List.of(
                        new ProgramEdge("origin", "entities", "center"),
                        new ProgramEdge("entities", "velocities", "entities"),
                        new ProgramEdge("velocities", "speeds", "vectors"),
                        new ProgramEdge("speeds", "mean", "values"),
                        new ProgramEdge("mean", "scaled", "a"),
                        new ProgramEdge("gain", "scaled", "b"),
                        new ProgramEdge("scaled", "rounded", "value"),
                        new ProgramEdge("rounded", "power", "value"),
                        new ProgramEdge("minimum", "power", "min"),
                        new ProgramEdge("maximum", "power", "max"),
                        new ProgramEdge("power", "emit", "power"),
                        new ProgramEdge("duration", "emit", "duration")
                ),
                "emit",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph thresholdBeacon() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("entities", "mathmod:sense_nearby_entities", Map.of(
                                "predicate", "any_living", "radius", "8", "limit", "8"
                        )),
                        new ProgramNode("velocities", "mathmod:entity_velocities"),
                        new ProgramNode("speeds", "mathmod:vector_lengths"),
                        new ProgramNode("mean", "mathmod:mean_number"),
                        new ProgramNode("threshold", "mathmod:constant_number", Map.of("value", "0.25")),
                        new ProgramNode("at_least", "mathmod:number_at_least"),
                        new ProgramNode("on", "mathmod:constant_number", Map.of("value", "15")),
                        new ProgramNode("off", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("power", "mathmod:number_select"),
                        new ProgramNode("duration", "mathmod:constant_number", Map.of("value", "10")),
                        new ProgramNode("emit", "mathmod:emit_anchor_redstone")
                ),
                List.of(
                        new ProgramEdge("origin", "entities", "center"),
                        new ProgramEdge("entities", "velocities", "entities"),
                        new ProgramEdge("velocities", "speeds", "vectors"),
                        new ProgramEdge("speeds", "mean", "values"),
                        new ProgramEdge("mean", "at_least", "value"),
                        new ProgramEdge("threshold", "at_least", "threshold"),
                        new ProgramEdge("at_least", "power", "condition"),
                        new ProgramEdge("on", "power", "when_true"),
                        new ProgramEdge("off", "power", "when_false"),
                        new ProgramEdge("power", "emit", "power"),
                        new ProgramEdge("duration", "emit", "duration")
                ),
                "emit",
                24
        );
    }

    public static ProgramGraph gradientLantern() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("field", "mathmod:living_density_field"),
                        new ProgramNode("step", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("gradient", "mathmod:field_gradient"),
                        new ProgramNode("magnitude", "mathmod:vector_length"),
                        new ProgramNode("gain", "mathmod:constant_number", Map.of("value", "120")),
                        new ProgramNode("scaled", "mathmod:number_multiply"),
                        new ProgramNode("rounded", "mathmod:number_round"),
                        new ProgramNode("minimum", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("maximum", "mathmod:constant_number", Map.of("value", "15")),
                        new ProgramNode("power", "mathmod:number_clamp"),
                        new ProgramNode("duration", "mathmod:constant_number", Map.of("value", "10")),
                        new ProgramNode("emit", "mathmod:emit_anchor_redstone")
                ),
                List.of(
                        new ProgramEdge("field", "gradient", "field"),
                        new ProgramEdge("origin", "gradient", "point"),
                        new ProgramEdge("step", "gradient", "step"),
                        new ProgramEdge("gradient", "magnitude", "vector"),
                        new ProgramEdge("magnitude", "scaled", "a"),
                        new ProgramEdge("gain", "scaled", "b"),
                        new ProgramEdge("scaled", "rounded", "value"),
                        new ProgramEdge("rounded", "power", "value"),
                        new ProgramEdge("minimum", "power", "min"),
                        new ProgramEdge("maximum", "power", "max"),
                        new ProgramEdge("power", "emit", "power"),
                        new ProgramEdge("duration", "emit", "duration")
                ),
                "emit",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph dimensionalSurvey() {
        return new ProgramGraph(
                List.of(new ProgramNode("survey", "mathmod:dimensional_survey")),
                List.of(),
                "survey",
                TARGETING_BUDGET
        );
    }

    public static ProgramGraph cavalieriProjectile() {
        return new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("origin", "mathmod:player_position"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("launchFactor", "mathmod:constant_number", Map.of("value", "1.2")),
                        new ProgramNode("velocity", "mathmod:scale_vector"),
                        new ProgramNode("axisX", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("axisY", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("axisZ", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("axis", "mathmod:vector_from_numbers"),
                        new ProgramNode("solid", "mathmod:solid_of_revolution", Map.of(
                                "inner", "0", "outer", "0.75", "lower", "-0.5", "upper", "0.5")),
                        new ProgramNode("body", "mathmod:materialize_construct", Map.of("material", "minecraft:stone")),
                        new ProgramNode("scale", "mathmod:constant_number", Map.of("value", "0.5")),
                        new ProgramNode("compressed", "mathmod:compress_construct"),
                        new ProgramNode("spin", "mathmod:constant_number", Map.of("value", "0.35")),
                        new ProgramNode("spun", "mathmod:spin_construct"),
                        new ProgramNode("launch", "mathmod:launch_construct")
                ),
                List.of(
                        new ProgramEdge("self", "origin", "player"),
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("look", "velocity", "vector"),
                        new ProgramEdge("launchFactor", "velocity", "factor"),
                        new ProgramEdge("axisX", "axis", "x"), new ProgramEdge("axisY", "axis", "y"),
                        new ProgramEdge("axisZ", "axis", "z"),
                        new ProgramEdge("origin", "solid", "origin"), new ProgramEdge("axis", "solid", "axis"),
                        new ProgramEdge("solid", "body", "region"),
                        new ProgramEdge("body", "compressed", "body"), new ProgramEdge("scale", "compressed", "scale"),
                        new ProgramEdge("compressed", "spun", "body"), new ProgramEdge("axis", "spun", "axis"),
                        new ProgramEdge("spin", "spun", "speed"),
                        new ProgramEdge("spun", "launch", "body"), new ProgramEdge("origin", "launch", "origin"),
                        new ProgramEdge("velocity", "launch", "velocity")
                ),
                "launch",
                40
        );
    }
}
