package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ManuscriptSnapshotBuilder {
    public static final int MAX_TRADITIONS = 256;
    public static final int MAX_MANUSCRIPTS = 1_024;
    public static final int MAX_ALIASES = 2_048;
    public static final int MAX_ALIAS_DEPTH = 16;

    private static final NamespacedId SNAPSHOT_ID = NamespacedId.of("mathmod", "snapshot");
    private static final ManuscriptDefinitionSource SNAPSHOT_SOURCE =
            new ManuscriptDefinitionSource(ManuscriptSourceLayer.BUILT_IN, 0, "snapshot");

    private final Predicate<NamespacedId> theoremExists;
    private final Map<NamespacedId, SourcedManuscriptValue<TraditionDefinition>> traditions =
            new LinkedHashMap<>();
    private final Map<NamespacedId, SourcedManuscriptValue<ManuscriptDefinition>> manuscripts =
            new LinkedHashMap<>();
    private final Map<NamespacedId, SourcedManuscriptValue<ManuscriptAliasDefinition>> aliases =
            new LinkedHashMap<>();
    private final Set<NamespacedId> ambiguousTraditions = new HashSet<>();
    private final Set<NamespacedId> ambiguousManuscripts = new HashSet<>();
    private final Set<NamespacedId> ambiguousAliases = new HashSet<>();
    private final List<ManuscriptDiagnostic> diagnostics = new ArrayList<>();

    public ManuscriptSnapshotBuilder(Predicate<NamespacedId> theoremExists) {
        this.theoremExists = Objects.requireNonNull(theoremExists, "theoremExists");
    }

    public ManuscriptSnapshotBuilder() {
        this(id -> true);
    }

    public ManuscriptSnapshotBuilder addTradition(
            TraditionDefinition definition,
            ManuscriptDefinitionSource source
    ) {
        addCandidate(
                traditions,
                ambiguousTraditions,
                new SourcedManuscriptValue<>(definition, source),
                TraditionDefinition::id,
                ManuscriptDiagnostic.RecordKind.TRADITION
        );
        return this;
    }

    public ManuscriptSnapshotBuilder addManuscript(
            ManuscriptDefinition definition,
            ManuscriptDefinitionSource source
    ) {
        addCandidate(
                manuscripts,
                ambiguousManuscripts,
                new SourcedManuscriptValue<>(definition, source),
                ManuscriptDefinition::id,
                ManuscriptDiagnostic.RecordKind.MANUSCRIPT
        );
        return this;
    }

    public ManuscriptSnapshotBuilder addAlias(
            ManuscriptAliasDefinition definition,
            ManuscriptDefinitionSource source
    ) {
        addCandidate(
                aliases,
                ambiguousAliases,
                new SourcedManuscriptValue<>(definition, source),
                ManuscriptAliasDefinition::from,
                ManuscriptDiagnostic.RecordKind.ALIAS
        );
        return this;
    }

    public ManuscriptSnapshotBuilder reject(
            ManuscriptDiagnostic.Code code,
            ManuscriptDiagnostic.RecordKind recordKind,
            NamespacedId id,
            ManuscriptDefinitionSource source,
            String message
    ) {
        diagnostic(
                ManuscriptDiagnostic.Severity.ERROR,
                code,
                recordKind,
                id,
                source,
                message
        );
        return this;
    }

    public ManuscriptSnapshotBuildResult build() {
        if (exceedsGlobalLimits()) {
            return new ManuscriptSnapshotBuildResult(
                    ManuscriptSnapshot.empty(),
                    diagnostics,
                    false
            );
        }

        Map<NamespacedId, SourcedManuscriptValue<TraditionDefinition>> validTraditions =
                withoutAmbiguous(traditions, ambiguousTraditions);
        Map<NamespacedId, SourcedManuscriptValue<ManuscriptDefinition>> validManuscripts =
                validateManuscriptReferences(validTraditions);
        Map<NamespacedId, NamespacedId> validAliases = validateAliases(validManuscripts);
        return new ManuscriptSnapshotBuildResult(
                new ManuscriptSnapshot(validTraditions, validManuscripts, validAliases),
                diagnostics,
                true
        );
    }

    private Map<NamespacedId, SourcedManuscriptValue<ManuscriptDefinition>>
            validateManuscriptReferences(
                    Map<NamespacedId, SourcedManuscriptValue<TraditionDefinition>> validTraditions
            ) {
        Map<NamespacedId, SourcedManuscriptValue<ManuscriptDefinition>> valid =
                withoutAmbiguous(manuscripts, ambiguousManuscripts);
        valid.entrySet().removeIf(entry -> {
            ManuscriptDefinition definition = entry.getValue().value();
            if (!validTraditions.containsKey(definition.traditionId())) {
                diagnostic(
                        ManuscriptDiagnostic.Severity.ERROR,
                        ManuscriptDiagnostic.Code.UNKNOWN_TRADITION,
                        ManuscriptDiagnostic.RecordKind.MANUSCRIPT,
                        entry.getKey(),
                        entry.getValue().source(),
                        "Unknown tradition " + definition.traditionId()
                );
                return true;
            }
            if (definition.theoremId().isPresent()
                    && !theoremExists.test(definition.theoremId().orElseThrow())) {
                diagnostic(
                        ManuscriptDiagnostic.Severity.ERROR,
                        ManuscriptDiagnostic.Code.UNKNOWN_THEOREM,
                        ManuscriptDiagnostic.RecordKind.MANUSCRIPT,
                        entry.getKey(),
                        entry.getValue().source(),
                        "Unknown theorem " + definition.theoremId().orElseThrow()
                );
                return true;
            }
            return false;
        });
        return valid;
    }

    private Map<NamespacedId, NamespacedId> validateAliases(
            Map<NamespacedId, SourcedManuscriptValue<ManuscriptDefinition>> validManuscripts
    ) {
        Map<NamespacedId, SourcedManuscriptValue<ManuscriptAliasDefinition>> candidates =
                withoutAmbiguous(aliases, ambiguousAliases);
        Map<NamespacedId, NamespacedId> resolved = new LinkedHashMap<>();
        candidates.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    NamespacedId from = entry.getKey();
                    SourcedManuscriptValue<ManuscriptAliasDefinition> sourced = entry.getValue();
                    if (validManuscripts.containsKey(from)) {
                        diagnostic(
                                ManuscriptDiagnostic.Severity.ERROR,
                                ManuscriptDiagnostic.Code.ALIAS_SHADOWS_MANUSCRIPT,
                                ManuscriptDiagnostic.RecordKind.ALIAS,
                                from,
                                sourced.source(),
                                "Alias source is already a current manuscript id"
                        );
                        return;
                    }
                    resolveAlias(from, candidates, validManuscripts.keySet())
                            .ifPresentOrElse(
                                    target -> resolved.put(from, target),
                                    () -> diagnoseInvalidAlias(
                                            from,
                                            sourced.source(),
                                            candidates,
                                            validManuscripts.keySet()
                                    )
                            );
                });
        return Map.copyOf(resolved);
    }

    private Optional<NamespacedId> resolveAlias(
            NamespacedId from,
            Map<NamespacedId, SourcedManuscriptValue<ManuscriptAliasDefinition>> candidates,
            Set<NamespacedId> manuscriptIds
    ) {
        NamespacedId current = from;
        Set<NamespacedId> visited = new HashSet<>();
        for (int depth = 0; depth <= MAX_ALIAS_DEPTH; depth++) {
            if (manuscriptIds.contains(current)) {
                return Optional.of(current);
            }
            if (!visited.add(current)) {
                return Optional.empty();
            }
            SourcedManuscriptValue<ManuscriptAliasDefinition> next = candidates.get(current);
            if (next == null) {
                return Optional.empty();
            }
            current = next.value().to();
        }
        return Optional.empty();
    }

    private void diagnoseInvalidAlias(
            NamespacedId from,
            ManuscriptDefinitionSource source,
            Map<NamespacedId, SourcedManuscriptValue<ManuscriptAliasDefinition>> candidates,
            Set<NamespacedId> manuscriptIds
    ) {
        NamespacedId current = from;
        Set<NamespacedId> visited = new HashSet<>();
        for (int depth = 0; depth <= MAX_ALIAS_DEPTH; depth++) {
            if (manuscriptIds.contains(current)) {
                return;
            }
            if (!visited.add(current)) {
                diagnostic(
                        ManuscriptDiagnostic.Severity.ERROR,
                        ManuscriptDiagnostic.Code.ALIAS_CYCLE,
                        ManuscriptDiagnostic.RecordKind.ALIAS,
                        from,
                        source,
                        "Alias chain contains a cycle at " + current
                );
                return;
            }
            SourcedManuscriptValue<ManuscriptAliasDefinition> next = candidates.get(current);
            if (next == null) {
                diagnostic(
                        ManuscriptDiagnostic.Severity.ERROR,
                        ManuscriptDiagnostic.Code.ALIAS_MISSING_TARGET,
                        ManuscriptDiagnostic.RecordKind.ALIAS,
                        from,
                        source,
                        "Alias chain ends at missing manuscript " + current
                );
                return;
            }
            current = next.value().to();
        }
        diagnostic(
                ManuscriptDiagnostic.Severity.ERROR,
                ManuscriptDiagnostic.Code.ALIAS_TOO_DEEP,
                ManuscriptDiagnostic.RecordKind.ALIAS,
                from,
                source,
                "Alias chain exceeds " + MAX_ALIAS_DEPTH + " hops"
        );
    }

    private boolean exceedsGlobalLimits() {
        boolean exceeded = false;
        exceeded |= checkLimit(
                traditions.size(),
                MAX_TRADITIONS,
                "traditions"
        );
        exceeded |= checkLimit(
                manuscripts.size(),
                MAX_MANUSCRIPTS,
                "manuscripts"
        );
        exceeded |= checkLimit(
                aliases.size(),
                MAX_ALIASES,
                "aliases"
        );
        return exceeded;
    }

    private boolean checkLimit(int actual, int maximum, String label) {
        if (actual <= maximum) {
            return false;
        }
        diagnostic(
                ManuscriptDiagnostic.Severity.FATAL,
                ManuscriptDiagnostic.Code.RECORD_LIMIT_EXCEEDED,
                ManuscriptDiagnostic.RecordKind.SNAPSHOT,
                SNAPSHOT_ID,
                SNAPSHOT_SOURCE,
                "Too many " + label + ": " + actual + " > " + maximum
        );
        return true;
    }

    private <T> void addCandidate(
            Map<NamespacedId, SourcedManuscriptValue<T>> target,
            Set<NamespacedId> ambiguous,
            SourcedManuscriptValue<T> candidate,
            Function<T, NamespacedId> idFunction,
            ManuscriptDiagnostic.RecordKind kind
    ) {
        NamespacedId id = idFunction.apply(candidate.value());
        SourcedManuscriptValue<T> current = target.get(id);
        if (current == null) {
            target.put(id, candidate);
            return;
        }
        int comparison = candidate.source().compareTo(current.source());
        if (comparison == 0 && !candidate.value().equals(current.value())) {
            ambiguous.add(id);
            diagnostic(
                    ManuscriptDiagnostic.Severity.ERROR,
                    ManuscriptDiagnostic.Code.AMBIGUOUS_SOURCE,
                    kind,
                    id,
                    candidate.source(),
                    "Conflicting records have identical source precedence"
            );
            return;
        }
        if (comparison > 0) {
            ambiguous.remove(id);
        }
        SourcedManuscriptValue<T> winner = comparison > 0 ? candidate : current;
        SourcedManuscriptValue<T> loser = comparison > 0 ? current : candidate;
        target.put(id, winner);
        diagnostic(
                ManuscriptDiagnostic.Severity.INFO,
                ManuscriptDiagnostic.Code.SHADOWED,
                kind,
                id,
                loser.source(),
                "Record is shadowed by " + winner.source().sourceName()
        );
    }

    private static <T> Map<NamespacedId, SourcedManuscriptValue<T>> withoutAmbiguous(
            Map<NamespacedId, SourcedManuscriptValue<T>> values,
            Set<NamespacedId> ambiguous
    ) {
        Map<NamespacedId, SourcedManuscriptValue<T>> copy = new LinkedHashMap<>(values);
        ambiguous.forEach(copy::remove);
        return copy;
    }

    private void diagnostic(
            ManuscriptDiagnostic.Severity severity,
            ManuscriptDiagnostic.Code code,
            ManuscriptDiagnostic.RecordKind recordKind,
            NamespacedId id,
            ManuscriptDefinitionSource source,
            String message
    ) {
        diagnostics.add(new ManuscriptDiagnostic(
                severity,
                code,
                recordKind,
                id,
                source,
                message
        ));
    }
}
