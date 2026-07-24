package com.mathmod.program;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Versioned, replayable source for a graph authored with Rune Forms.
 * The execution graph is persisted separately and remains authoritative.
 */
public record GuidedWorkspaceState(int version, String name, List<String> invocationIds) {
    public static final int CURRENT_VERSION = 1;
    public static final int MAX_INVOCATIONS = 128;

    private static final Codec<GuidedWorkspaceState> UNVALIDATED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("version").forGetter(GuidedWorkspaceState::version),
            Codec.STRING.optionalFieldOf("name", "").forGetter(GuidedWorkspaceState::name),
            Codec.STRING.listOf().fieldOf("invocations").forGetter(GuidedWorkspaceState::invocationIds)
    ).apply(instance, GuidedWorkspaceState::new));

    public static final Codec<GuidedWorkspaceState> CODEC = UNVALIDATED_CODEC.flatXmap(
            GuidedWorkspaceState::validateDecoded,
            GuidedWorkspaceState::validateDecoded
    );

    public GuidedWorkspaceState {
        invocationIds = invocationIds == null ? List.of() : List.copyOf(invocationIds);
    }

    public static GuidedWorkspaceState create(String name, List<CustomSpellInvocation> invocations) {
        List<String> encoded = invocations == null
                ? List.of()
                : invocations.stream().map(CustomSpellInvocation::persistentId).toList();
        GuidedWorkspaceState state = new GuidedWorkspaceState(
                CURRENT_VERSION,
                ProgramNames.sanitizeOptional(name),
                encoded
        );
        return validateDecoded(state).getOrThrow();
    }

    public static DataResult<GuidedWorkspaceState> migrateLegacy(String name, List<String> invocationIds) {
        return validateDecoded(new GuidedWorkspaceState(
                CURRENT_VERSION,
                ProgramNames.sanitizeOptional(name),
                invocationIds
        ));
    }

    /**
     * Returns empty unless every persisted invocation is known and valid.
     * Partial replay would create a different proof and is deliberately forbidden.
     */
    public Optional<List<CustomSpellInvocation>> replayableInvocations() {
        List<CustomSpellInvocation> decoded = new ArrayList<>(invocationIds.size());
        for (String invocationId : invocationIds) {
            Optional<CustomSpellInvocation> invocation = CustomSpellInvocation.fromPersistentId(invocationId);
            if (invocation.isEmpty()) {
                return Optional.empty();
            }
            decoded.add(invocation.get());
        }
        return Optional.of(List.copyOf(decoded));
    }

    public boolean replayable() {
        return replayableInvocations().isPresent();
    }

    public boolean supported() {
        return validateDecoded(this).result().isPresent();
    }

    private static DataResult<GuidedWorkspaceState> validateDecoded(GuidedWorkspaceState state) {
        if (state.version != CURRENT_VERSION) {
            return DataResult.error(() -> "Unsupported guided workspace version: " + state.version);
        }
        if (!ProgramNames.sanitizeOptional(state.name).equals(state.name)) {
            return DataResult.error(() -> "Guided workspace name is not canonical");
        }
        if (state.invocationIds.size() > MAX_INVOCATIONS) {
            return DataResult.error(() -> "Guided workspace exceeds invocation limit of " + MAX_INVOCATIONS);
        }
        for (String invocationId : state.invocationIds) {
            if (invocationId == null || invocationId.isBlank()) {
                return DataResult.error(() -> "Guided workspace contains a blank invocation");
            }
            if (invocationId.length() > CustomSpellInvocation.MAX_PERSISTENT_ID_LENGTH) {
                return DataResult.error(() -> "Guided workspace invocation exceeds encoded length limit");
            }
        }
        return DataResult.success(state);
    }
}
