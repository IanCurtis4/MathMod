package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptDefinitionSource;
import com.mathmod.manuscript.ManuscriptSnapshot;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class ManuscriptAcquisitionSnapshotBuilder {
    public static final int MAX_RECORDS = 2_048;

    private final ManuscriptSnapshot manuscripts;
    private final Map<NamespacedId, SourcedCandidate> candidates = new LinkedHashMap<>();
    private final Set<NamespacedId> ambiguousIds = new HashSet<>();
    private final List<AcquisitionDiagnostic> diagnostics = new ArrayList<>();

    public ManuscriptAcquisitionSnapshotBuilder(ManuscriptSnapshot manuscripts) {
        this.manuscripts = Objects.requireNonNull(manuscripts, "manuscripts");
    }

    public ManuscriptAcquisitionSnapshotBuilder add(
            ManuscriptAcquisitionDefinition definition,
            ManuscriptDefinitionSource source
    ) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(source, "source");
        NamespacedId id = definition.id();
        SourcedCandidate current = candidates.get(id);
        SourcedCandidate candidate = new SourcedCandidate(definition, source);
        if (current == null) {
            candidates.put(id, candidate);
            return this;
        }
        int comparison = source.compareTo(current.source());
        if (comparison == 0 && !definition.equals(current.definition())) {
            ambiguousIds.add(id);
            diagnostics.add(new AcquisitionDiagnostic(
                    AcquisitionDiagnostic.Code.AMBIGUOUS_SOURCE,
                    id,
                    source,
                    "Conflicting acquisition records have identical source precedence"
            ));
            return this;
        }
        SourcedCandidate winner = comparison > 0 ? candidate : current;
        SourcedCandidate loser = comparison > 0 ? current : candidate;
        candidates.put(id, winner);
        if (comparison != 0) {
            ambiguousIds.remove(id);
            diagnostics.add(new AcquisitionDiagnostic(
                    AcquisitionDiagnostic.Code.SHADOWED,
                    id,
                    loser.source(),
                    "Acquisition record is shadowed by " + winner.source().sourceName()
            ));
        }
        return this;
    }

    public ManuscriptAcquisitionSnapshotBuilder reject(
            NamespacedId id,
            ManuscriptDefinitionSource source,
            String message
    ) {
        diagnostics.add(new AcquisitionDiagnostic(
                AcquisitionDiagnostic.Code.DECODE_FAILED,
                id,
                source,
                message
        ));
        return this;
    }

    public ManuscriptAcquisitionBuildResult build() {
        if (candidates.size() > MAX_RECORDS) {
            diagnostics.add(new AcquisitionDiagnostic(
                    AcquisitionDiagnostic.Code.RECORD_LIMIT_EXCEEDED,
                    NamespacedId.of("mathmod", "acquisition_snapshot"),
                    new ManuscriptDefinitionSource(
                            com.mathmod.manuscript.ManuscriptSourceLayer.BUILT_IN,
                            0,
                            "acquisition_snapshot"
                    ),
                    "Too many acquisition records: " + candidates.size() + " > " + MAX_RECORDS
            ));
            return new ManuscriptAcquisitionBuildResult(ManuscriptAcquisitionSnapshot.empty(), diagnostics, false);
        }

        Map<NamespacedId, ManuscriptAcquisitionSnapshot.Candidate> valid = new LinkedHashMap<>();
        for (SourcedCandidate sourced : candidates.values().stream()
                .sorted(Comparator.comparing(candidate -> candidate.definition().id()))
                .toList()) {
            if (ambiguousIds.contains(sourced.definition().id())) {
                continue;
            }
            Optional<NamespacedId> canonical = manuscripts.migrateReference(sourced.definition().manuscriptId()).canonicalId();
            if (canonical.isEmpty()) {
                diagnostics.add(new AcquisitionDiagnostic(
                        AcquisitionDiagnostic.Code.UNKNOWN_MANUSCRIPT,
                        sourced.definition().id(),
                        sourced.source(),
                        "Unknown manuscript " + sourced.definition().manuscriptId()
                ));
                continue;
            }
            ManuscriptAcquisitionDefinition definition = sourced.definition();
            valid.put(definition.id(), new ManuscriptAcquisitionSnapshot.Candidate(
                    definition.id(),
                    canonical.orElseThrow(),
                    definition.lootPools(),
                    definition.lootWeight(),
                    definition.trade()
            ));
        }

        removePoolCollisions(valid);
        removeTradeCollisions(valid);
        Map<NamespacedId, List<ManuscriptAcquisitionSnapshot.Candidate>> lootPools = new LinkedHashMap<>();
        Map<Integer, List<ManuscriptAcquisitionSnapshot.Candidate>> tradeLevels = new LinkedHashMap<>();
        for (ManuscriptAcquisitionSnapshot.Candidate candidate : valid.values()) {
            for (NamespacedId pool : candidate.lootPools()) {
                lootPools.computeIfAbsent(pool, ignored -> new ArrayList<>()).add(candidate);
            }
            candidate.trade().ifPresent(trade -> tradeLevels
                    .computeIfAbsent(trade.level(), ignored -> new ArrayList<>())
                    .add(candidate));
        }
        return new ManuscriptAcquisitionBuildResult(
                new ManuscriptAcquisitionSnapshot(valid, lootPools, tradeLevels),
                diagnostics,
                true
        );
    }

    private void removePoolCollisions(Map<NamespacedId, ManuscriptAcquisitionSnapshot.Candidate> valid) {
        Map<String, NamespacedId> winners = new LinkedHashMap<>();
        for (ManuscriptAcquisitionSnapshot.Candidate candidate : sorted(valid)) {
            for (NamespacedId pool : candidate.lootPools()) {
                String key = candidate.manuscriptId() + "|" + pool;
                NamespacedId winner = winners.get(key);
                if (winner != null) {
                    diagnostics.add(new AcquisitionDiagnostic(
                            AcquisitionDiagnostic.Code.POOL_COLLISION,
                            candidate.id(),
                            candidates.get(candidate.id()).source(),
                            "Acquisition pool collision with " + winner
                    ));
                    valid.remove(candidate.id());
                    break;
                }
            }
            if (valid.containsKey(candidate.id())) {
                for (NamespacedId pool : candidate.lootPools()) {
                    winners.put(candidate.manuscriptId() + "|" + pool, candidate.id());
                }
            }
        }
    }

    private void removeTradeCollisions(Map<NamespacedId, ManuscriptAcquisitionSnapshot.Candidate> valid) {
        Map<String, NamespacedId> winners = new LinkedHashMap<>();
        for (ManuscriptAcquisitionSnapshot.Candidate candidate : sorted(valid)) {
            if (candidate.trade().isEmpty()) {
                continue;
            }
            int level = candidate.trade().orElseThrow().level();
            String key = candidate.manuscriptId() + "|" + level;
            NamespacedId winner = winners.putIfAbsent(key, candidate.id());
            if (winner != null) {
                diagnostics.add(new AcquisitionDiagnostic(
                        AcquisitionDiagnostic.Code.TRADE_COLLISION,
                        candidate.id(),
                        candidates.get(candidate.id()).source(),
                        "Acquisition trade collision with " + winner
                ));
                valid.remove(candidate.id());
            }
        }
    }

    private static List<ManuscriptAcquisitionSnapshot.Candidate> sorted(
            Map<NamespacedId, ManuscriptAcquisitionSnapshot.Candidate> candidates
    ) {
        return candidates.values().stream().sorted(Comparator.comparing(ManuscriptAcquisitionSnapshot.Candidate::id)).toList();
    }

    private record SourcedCandidate(
            ManuscriptAcquisitionDefinition definition,
            ManuscriptDefinitionSource source
    ) {
    }
}
