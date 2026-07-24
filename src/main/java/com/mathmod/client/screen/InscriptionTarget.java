package com.mathmod.client.screen;

import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.ProgramNames;
import com.mathmod.program.ProgramStorage;
import com.mathmod.runes.ProgramGraph;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

record InscriptionTarget(
        Optional<ProgramGraph> program,
        Optional<String> name,
        List<CustomSpellAction> customActions
) {
    InscriptionTarget {
        program = program == null ? Optional.empty() : program;
        name = name == null
                ? Optional.empty()
                : name.map(ProgramNames::sanitizeOptional).filter(value -> !value.isBlank());
        customActions = customActions == null ? List.of() : List.copyOf(customActions);
    }

    static InscriptionTarget empty() {
        return new InscriptionTarget(Optional.empty(), Optional.empty(), List.of());
    }

    static InscriptionTarget preset(ProgramGraph program) {
        return new InscriptionTarget(Optional.of(program), Optional.empty(), List.of());
    }

    static InscriptionTarget custom(
            ProgramGraph program,
            String name,
            List<CustomSpellAction> customActions
    ) {
        return new InscriptionTarget(
                Optional.of(program),
                Optional.ofNullable(name),
                customActions
        );
    }

    static InscriptionTarget fromStack(ItemStack stack) {
        if (stack == null) {
            return empty();
        }
        return new InscriptionTarget(
                ProgramStorage.get(stack),
                ProgramStorage.getName(stack),
                ProgramStorage.getCustomActions(stack)
        );
    }
}
