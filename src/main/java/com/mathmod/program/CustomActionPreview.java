package com.mathmod.program;

import com.mathmod.runes.RuneType;

import java.util.List;

public record CustomActionPreview(
        List<Input> inputs,
        int addedRunes,
        int addedBindings,
        RuneType resultType
) {
    public CustomActionPreview {
        inputs = List.copyOf(inputs);
        if (addedRunes < 1) {
            throw new IllegalArgumentException("A Laboratory form must add at least one rune");
        }
        if (addedBindings < 0) {
            throw new IllegalArgumentException("Added bindings cannot be negative");
        }
    }

    public List<CustomInputSlot> currentInputs() {
        return inputs.stream()
                .filter(Input::current)
                .map(Input::slot)
                .toList();
    }

    public List<CustomInputSlot> inferredInputs() {
        return inputs.stream()
                .filter(input -> !input.current())
                .map(Input::slot)
                .toList();
    }

    public record Input(CustomInputSlot slot, boolean current) {
    }
}
