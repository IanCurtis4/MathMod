package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;

import java.util.Arrays;
import java.util.Optional;

public enum AnchorProgramPreset {
    ANCHOR_PULSE(
            "anchor_pulse",
            "item.mathmod.chalk.anchor_saved",
            "block.mathmod.rune_anchor.preset.anchor_pulse"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.anchorPulse();
        }
    },
    SACRIFICE_PULSE(
            "sacrifice_pulse",
            "item.mathmod.chalk.anchor_sacrifice_saved",
            "block.mathmod.rune_anchor.preset.sacrifice_pulse"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.sacrificePulse();
        }
    },
    OFFERING_SPARK(
            "offering_spark",
            "item.mathmod.chalk.anchor_offering_saved",
            "block.mathmod.rune_anchor.preset.offering_spark"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.offeringSpark();
        }
    },
    WARDING_PULSE(
            "warding_pulse",
            "item.mathmod.chalk.anchor_ward_saved",
            "block.mathmod.rune_anchor.preset.warding_pulse"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.wardingPulse();
        }
    },
    KINETIC_TRANSDUCER(
            "kinetic_transducer",
            "item.mathmod.chalk.anchor_kinetic_saved",
            "block.mathmod.rune_anchor.preset.kinetic_transducer"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.kineticTransducer();
        }
    },
    THRESHOLD_BEACON(
            "threshold_beacon",
            "item.mathmod.chalk.anchor_threshold_saved",
            "block.mathmod.rune_anchor.preset.threshold_beacon"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.thresholdBeacon();
        }
    },
    GRADIENT_LANTERN(
            "gradient_lantern",
            "item.mathmod.chalk.anchor_gradient_saved",
            "block.mathmod.rune_anchor.preset.gradient_lantern"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.gradientLantern();
        }
    },
    DIMENSIONAL_SURVEY(
            "dimensional_survey",
            "item.mathmod.chalk.anchor_dimensional_survey_saved",
            "block.mathmod.rune_anchor.preset.dimensional_survey"
    ) {
        @Override
        public ProgramGraph graph() {
            return ProgramPresets.dimensionalSurvey();
        }
    };

    private final String id;
    private final String saveMessageKey;
    private final String displayNameKey;

    AnchorProgramPreset(String id, String saveMessageKey, String displayNameKey) {
        this.id = id;
        this.saveMessageKey = saveMessageKey;
        this.displayNameKey = displayNameKey;
    }

    public String id() {
        return id;
    }

    public String saveMessageKey() {
        return saveMessageKey;
    }

    public String displayNameKey() {
        return displayNameKey;
    }

    public abstract ProgramGraph graph();

    public AnchorProgramPreset next() {
        AnchorProgramPreset[] presets = values();
        return presets[(ordinal() + 1) % presets.length];
    }

    public static Optional<AnchorProgramPreset> fromId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(preset -> preset.id.equals(id))
                .findFirst();
    }

    public static Optional<AnchorProgramPreset> infer(ProgramGraph graph) {
        if (graph == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(preset -> preset.matches(graph))
                .findFirst();
    }

    private boolean matches(ProgramGraph graph) {
        if (graph().equals(graph)) {
            return true;
        }
        return switch (this) {
            case ANCHOR_PULSE -> false;
            case SACRIFICE_PULSE -> ProgramPresets.sacrificePulse(AnchorPresetConfig.DEFAULT_SACRIFICE_SELECTOR).equals(graph)
                    || ProgramPresets.sacrificePulse(ProgramPresets.LEGACY_AMETHYST_SACRIFICE_SELECTOR).equals(graph);
            case OFFERING_SPARK -> ProgramPresets.offeringSpark(AnchorPresetConfig.DEFAULT_SACRIFICE_SELECTOR).equals(graph)
                    || ProgramPresets.offeringSpark(ProgramPresets.LEGACY_AMETHYST_SACRIFICE_SELECTOR).equals(graph);
            case WARDING_PULSE -> ProgramPresets.wardingPulse(AnchorPresetConfig.DEFAULT_SACRIFICE_SELECTOR).equals(graph)
                    || ProgramPresets.wardingPulse(ProgramPresets.LEGACY_AMETHYST_SACRIFICE_SELECTOR).equals(graph);
            case KINETIC_TRANSDUCER, THRESHOLD_BEACON, GRADIENT_LANTERN, DIMENSIONAL_SURVEY -> false;
        };
    }
}
