package com.mathmod.authoring;

import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.CustomSpellInvocation;
import com.mathmod.program.CustomSpellWorkspace;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.util.NamespacedId;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Trusted built-in bridge to the characterized legacy workspace expansion.
 *
 * <p>This type deliberately has no player, level, item, network, clock, random,
 * file, command, callback, or executor dependency. Persisted identity remains the
 * form id; adapter ids are checked only as internal trusted implementation ids.</p>
 */
final class TrustedLegacyExpansionAdapter {
    private static final int MAX_INVOCATIONS = 128;
    private static final Map<NamespacedId, NamespacedId> BUILT_IN_ADAPTER_IDS = builtInAdapterIds();

    private TrustedLegacyExpansionAdapter() {}

    record FormInvocation(NamespacedId formId, Map<String, Double> suppliedArguments) {
        FormInvocation {
            Objects.requireNonNull(formId);
            suppliedArguments = suppliedArguments == null ? Map.of() : Map.copyOf(suppliedArguments);
        }
    }

    static final class ReplayMismatch extends IllegalStateException {
        private ReplayMismatch() { super("GRAPH_REPLAY_MISMATCH"); }
    }

    static void apply(AuthoringMetadata.Snapshot snapshot, CustomSpellWorkspace workspace,
                      NamespacedId formId, Map<String, Double> suppliedArguments) {
        Objects.requireNonNull(workspace);
        workspace.apply(canonicalInvocation(snapshot, formId, suppliedArguments));
    }

    static CustomSpellInvocation canonicalInvocation(AuthoringMetadata.Snapshot snapshot,
                                                      NamespacedId formId,
                                                      Map<String, Double> suppliedArguments) {
        Objects.requireNonNull(snapshot);
        Objects.requireNonNull(formId);
        AuthoringMetadata.Form form = snapshot.find(formId)
                .orElseThrow(() -> new IllegalArgumentException("UNKNOWN_FORM: " + formId));
        NamespacedId expectedAdapterId = BUILT_IN_ADAPTER_IDS.get(formId);
        if (expectedAdapterId == null || !expectedAdapterId.equals(form.expansion().adapterId())) {
            throw new IllegalArgumentException("UNKNOWN_ADAPTER: " + formId);
        }
        CustomSpellAction action = CustomSpellAction.fromPersistentId(formId.toString())
                .orElseThrow(() -> new IllegalArgumentException("UNKNOWN_FORM: " + formId));
        return new CustomSpellInvocation(action, canonicalArguments(form, suppliedArguments));
    }

    static ProgramGraph replayExactly(AuthoringMetadata.Snapshot snapshot,
                                      List<FormInvocation> invocations,
                                      ProgramGraph authoritativeGraph) {
        Objects.requireNonNull(invocations);
        Objects.requireNonNull(authoritativeGraph);
        if (invocations.size() > MAX_INVOCATIONS) {
            throw new IllegalArgumentException("too many guided invocations");
        }
        CustomSpellWorkspace replay = new CustomSpellWorkspace();
        for (FormInvocation invocation : invocations) {
            apply(snapshot, replay, invocation.formId(), invocation.suppliedArguments());
        }
        ProgramGraph replayed = replay.toGraph();
        if (!authoritativeGraph.equals(replayed)) {
            throw new ReplayMismatch();
        }
        return replayed;
    }

    private static Map<String, Double> canonicalArguments(AuthoringMetadata.Form form,
                                                            Map<String, Double> suppliedArguments) {
        Map<String, Double> source = suppliedArguments == null ? Map.of() : suppliedArguments;
        Map<String, Double> canonical = new LinkedHashMap<>();
        for (AuthoringMetadata.Parameter parameter : form.parameters()) {
            canonical.put(parameter.key(), parameter.canonicalize(
                    source.getOrDefault(parameter.key(), parameter.defaultValue())));
        }
        return Collections.unmodifiableMap(canonical);
    }

    private static Map<NamespacedId, NamespacedId> builtInAdapterIds() {
        Map<NamespacedId, NamespacedId> mapping = new LinkedHashMap<>();
        for (AuthoringMetadata.Form form : BuiltInAuthoringMetadata.snapshot().runeForms().values()) {
            NamespacedId previous = mapping.putIfAbsent(form.formId(), form.expansion().adapterId());
            if (previous != null) {
                throw new IllegalStateException("duplicate built-in legacy adapter mapping: " + form.formId());
            }
        }
        return Map.copyOf(mapping);
    }
}
