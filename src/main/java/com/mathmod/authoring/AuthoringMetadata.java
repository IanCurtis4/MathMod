package com.mathmod.authoring;

import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Pure, immutable authoring metadata. None of these types confer execution authority. */
public final class AuthoringMetadata {
    public static final NamespacedId NUMBER_TYPE = NamespacedId.parse("mathmod:number");
    public static final int MAX_RUNE_PRESENTATIONS = 2_048;
    public static final int MAX_FORMS = 1_024;
    public static final int MAX_CATEGORIES = 128;
    public static final int MAX_PARAMETERS = 16;
    public static final int MAX_INPUT_HINTS = 16;
    public static final int MAX_KEY_LENGTH = 160;
    public static final int MAX_DIAGNOSTICS = 1_024;

    private AuthoringMetadata() {}

    public enum Severity { INFO, WARNING, ERROR, FATAL }
    public enum RecordKind { RUNE_PRESENTATION, RUNE_FORM, CATEGORY, ALIAS, SNAPSHOT }
    public enum SourceKind { BUILT_IN, KUBEJS, DATA_PACK, NETWORK }
    public enum DiagnosticCode {
        DECODE_FAILED, UNSUPPORTED_SCHEMA, DUPLICATE_ID, UNKNOWN_RUNE, UNKNOWN_FORM,
        UNKNOWN_CATEGORY, UNKNOWN_ICON, UNKNOWN_ADAPTER, SEMANTIC_COLLISION,
        ALIAS_CYCLE, ALIAS_SHADOWS_CANONICAL, INVALID_PARAMETER, INVALID_TEMPLATE,
        LIMIT_EXCEEDED, SNAPSHOT_NOT_PUBLISHED, WORKSPACE_UNREPLAYABLE, GRAPH_REPLAY_MISMATCH
    }

    /** Stable candidate diagnostic; the message is technical detail, never a persistence or network format. */
    public record Diagnostic(Severity severity, DiagnosticCode code, RecordKind recordKind,
                             Optional<NamespacedId> id, SourceKind sourceKind,
                             String sourceName, String message) {
        public Diagnostic {
            Objects.requireNonNull(severity); Objects.requireNonNull(code); Objects.requireNonNull(recordKind);
            id = id == null ? Optional.empty() : id;
            Objects.requireNonNull(sourceKind);
            sourceName = requireBounded(sourceName, "sourceName");
            message = requireBounded(message, "message");
        }
    }

    /** A rejected candidate retains its structured diagnostics instead of making exception text a protocol. */
    public static final class CandidateFailure extends IllegalArgumentException {
        private final List<Diagnostic> diagnostics;
        private CandidateFailure(List<Diagnostic> diagnostics) {
            super("invalid authoring metadata candidate");
            this.diagnostics = List.copyOf(diagnostics);
        }
        public List<Diagnostic> diagnostics() { return diagnostics; }
    }

    public record Category(NamespacedId categoryId, String translationKey, int sortOrder) {
        public Category { Objects.requireNonNull(categoryId); translationKey = requireBounded(translationKey, "translationKey"); }
    }

    public record RuneIcon(NamespacedId runeId) { public RuneIcon { Objects.requireNonNull(runeId); } }

    /** Optional projection of an executable rune; it deliberately contains no signature or executor data. */
    public record RunePresentation(NamespacedId runeId, String nameTranslationKey, NamespacedId categoryId,
                                   RuneIcon icon, Formula formula, int sortOrder,
                                   Optional<String> descriptionTranslationKey) {
        public RunePresentation {
            Objects.requireNonNull(runeId); nameTranslationKey = requireBounded(nameTranslationKey, "nameTranslationKey");
            Objects.requireNonNull(categoryId); Objects.requireNonNull(icon); Objects.requireNonNull(formula);
            descriptionTranslationKey = descriptionTranslationKey == null ? Optional.empty() : descriptionTranslationKey;
            descriptionTranslationKey.ifPresent(key -> requireBounded(key, "descriptionTranslationKey"));
            validateFormula(formula);
        }
        public static RunePresentation technicalFallback(NamespacedId runeId, List<String> inputNames) {
            String readablePath = runeId.path().replace('/', ' ').replace('_', ' ');
            List<Formula> nodes = new ArrayList<>(); nodes.add(new Symbol(readablePath));
            for (String input : inputNames) nodes.add(new Symbol(input));
            Formula formula = nodes.size() == 1 ? nodes.getFirst() : new Sequence(nodes);
            return new RunePresentation(runeId, "mathmod.authoring.technical." + runeId.path().replace('/', '.'),
                    NamespacedId.parse("mathmod:uncategorized"), new RuneIcon(runeId), formula,
                    Integer.MAX_VALUE, Optional.empty());
        }
    }

    /** The bounded Cycle 2 subset; a Symbol preserves the legacy compact palette text exactly. */
    public sealed interface Formula permits Symbol, Sequence { int nodeCount(); int depth(); }
    public record Symbol(String token) implements Formula {
        public Symbol { token = require(token, "token"); if (token.length() > 64) throw failure(DiagnosticCode.INVALID_TEMPLATE, RecordKind.RUNE_FORM, Optional.empty(), "formula token exceeds 64 characters"); }
        public int nodeCount() { return 1; } public int depth() { return 1; }
    }
    public record Sequence(List<Formula> children) implements Formula {
        public Sequence {
            children = List.copyOf(children);
            if (children.isEmpty() || children.size() > 32) throw failure(DiagnosticCode.INVALID_TEMPLATE, RecordKind.RUNE_FORM, Optional.empty(), "formula sequence must contain 1..32 children");
            if (1 + children.stream().mapToInt(Formula::nodeCount).sum() > 128 || 1 + children.stream().mapToInt(Formula::depth).max().orElse(0) > 16)
                throw failure(DiagnosticCode.INVALID_TEMPLATE, RecordKind.RUNE_FORM, Optional.empty(), "formula exceeds node or depth limit");
        }
        public int nodeCount() { return 1 + children.stream().mapToInt(Formula::nodeCount).sum(); }
        public int depth() { return 1 + children.stream().mapToInt(Formula::depth).max().orElse(0); }
    }
    public record NumberConstraints(double minimum, double maximum) {
        public NumberConstraints { if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || minimum > maximum) throw new IllegalArgumentException("invalid numeric bounds"); }
        public double canonicalize(double value, double defaultValue) { return !Double.isFinite(value) ? defaultValue : Math.max(minimum, Math.min(maximum, value)); }
    }
    public record Parameter(String key, NamespacedId typeId, String translationKey, double defaultValue,
                            NumberConstraints constraints, Optional<NamespacedId> editorHint) {
        public Parameter {
            key = requireBounded(key, "key"); Objects.requireNonNull(typeId); translationKey = requireBounded(translationKey, "translationKey");
            if (!NUMBER_TYPE.equals(typeId) || !Double.isFinite(defaultValue)) throw failure(DiagnosticCode.INVALID_PARAMETER, RecordKind.RUNE_FORM, Optional.empty(), "Cycle 2 supports finite mathmod:number parameters only");
            Objects.requireNonNull(constraints); editorHint = editorHint == null ? Optional.empty() : editorHint;
            if (defaultValue < constraints.minimum || defaultValue > constraints.maximum) throw failure(DiagnosticCode.INVALID_PARAMETER, RecordKind.RUNE_FORM, Optional.empty(), "default outside numeric bounds");
        }
        public double canonicalize(double value) { return constraints.canonicalize(value, defaultValue); }
    }

    public record LegacyAdapter(NamespacedId adapterId) { public LegacyAdapter { Objects.requireNonNull(adapterId); } }
    public record ParameterSemantics(String key, NamespacedId typeId, double defaultValue, NumberConstraints constraints) {
        public ParameterSemantics { key = requireBounded(key, "key"); Objects.requireNonNull(typeId); Objects.requireNonNull(constraints); }
    }
    /** Internal, structured semantic comparison value. It is never persisted or networked. */
    public record SemanticFingerprint(NamespacedId formId, List<ParameterSemantics> parameters,
                                      NamespacedId adapterId, List<String> consumedInputIds) {
        public SemanticFingerprint {
            Objects.requireNonNull(formId); parameters = List.copyOf(parameters); Objects.requireNonNull(adapterId);
            consumedInputIds = boundedStrings(consumedInputIds, "consumedInputId");
        }
    }
    public record Form(NamespacedId formId, String translationKey, NamespacedId categoryId, RuneIcon icon,
                       Formula formula, List<Parameter> parameters, List<String> consumedInputIds,
                       List<String> inputHints, Optional<String> outputHint, int sortOrder, LegacyAdapter expansion) {
        public Form {
            Objects.requireNonNull(formId); translationKey = requireBounded(translationKey, "translationKey");
            Objects.requireNonNull(categoryId); Objects.requireNonNull(icon); Objects.requireNonNull(formula);
            parameters = List.copyOf(parameters); consumedInputIds = boundedStrings(consumedInputIds, "consumedInputId"); inputHints = boundedStrings(inputHints, "inputHint");
            outputHint = outputHint == null ? Optional.empty() : outputHint; outputHint.ifPresent(hint -> requireBounded(hint, "outputHint")); Objects.requireNonNull(expansion);
            if (parameters.size() > MAX_PARAMETERS) throw failure(DiagnosticCode.LIMIT_EXCEEDED, RecordKind.RUNE_FORM, Optional.of(formId), "parameters per form exceeds 16");
            if (consumedInputIds.size() > MAX_INPUT_HINTS) throw failure(DiagnosticCode.LIMIT_EXCEEDED, RecordKind.RUNE_FORM, Optional.of(formId), "consumed inputs per form exceeds 16");
            if (inputHints.size() > MAX_INPUT_HINTS) throw failure(DiagnosticCode.LIMIT_EXCEEDED, RecordKind.RUNE_FORM, Optional.of(formId), "input hints per form exceeds 16");
            if (parameters.stream().map(Parameter::key).distinct().count() != parameters.size()) throw failure(DiagnosticCode.INVALID_PARAMETER, RecordKind.RUNE_FORM, Optional.of(formId), "duplicate parameter key");
            validateFormula(formula);
        }
        public SemanticFingerprint semanticFingerprint() {
            return new SemanticFingerprint(formId, parameters.stream().map(parameter -> new ParameterSemantics(parameter.key(), parameter.typeId(), parameter.defaultValue(), parameter.constraints())).toList(), expansion.adapterId(), consumedInputIds);
        }
    }

    public record Snapshot(long generation, Map<NamespacedId, RunePresentation> runePresentations,
                           Map<NamespacedId, Form> runeForms, Map<NamespacedId, Category> categories,
                           Map<String, NamespacedId> aliases, List<Diagnostic> diagnostics) {
        public Snapshot {
            runePresentations = Map.copyOf(runePresentations); runeForms = Map.copyOf(runeForms); categories = Map.copyOf(categories); aliases = Map.copyOf(aliases); diagnostics = List.copyOf(diagnostics);
            if (runePresentations.size() > MAX_RUNE_PRESENTATIONS || runeForms.size() > MAX_FORMS || categories.size() > MAX_CATEGORIES || aliases.size() > 2_048 || diagnostics.size() > MAX_DIAGNOSTICS)
                throw failure(DiagnosticCode.LIMIT_EXCEEDED, RecordKind.SNAPSHOT, Optional.empty(), "snapshot collection exceeds contract limit");
        }
        public Optional<Form> find(NamespacedId formId) { return Optional.ofNullable(runeForms.get(formId)); }
        public List<Category> orderedCategories() { return categories.values().stream().sorted(Comparator.comparingInt(Category::sortOrder).thenComparing(Category::categoryId)).toList(); }
        public Optional<NamespacedId> resolveFormId(String rawId) {
            if (rawId == null || rawId.isBlank()) return Optional.empty(); String raw = rawId.trim();
            NamespacedId direct = NamespacedId.tryParse(raw).orElse(null); if (direct != null && runeForms.containsKey(direct)) return Optional.of(direct);
            NamespacedId alias = aliases.get(raw); if (alias != null && runeForms.containsKey(alias)) return Optional.of(alias);
            NamespacedId legacy = NamespacedId.tryParse("mathmod:" + raw.toLowerCase(java.util.Locale.ROOT)).orElse(null);
            return legacy != null && runeForms.containsKey(legacy) ? Optional.of(legacy) : Optional.empty();
        }
        public List<Form> orderedForms(NamespacedId categoryId) { return runeForms.values().stream().filter(form -> form.categoryId().equals(categoryId)).sorted(Comparator.comparingInt(Form::sortOrder).thenComparing(Form::formId)).toList(); }
    }

    public static Snapshot snapshot(long generation, List<Form> forms, List<Category> categories) {
        List<Diagnostic> diagnostics = new ArrayList<>();
        if (forms.size() > MAX_FORMS) diagnostics.add(diagnostic(DiagnosticCode.LIMIT_EXCEEDED, RecordKind.SNAPSHOT, Optional.empty(), "forms exceeds 1024"));
        if (categories.size() > MAX_CATEGORIES) diagnostics.add(diagnostic(DiagnosticCode.LIMIT_EXCEEDED, RecordKind.SNAPSHOT, Optional.empty(), "categories exceeds 128"));
        Map<NamespacedId, Category> categoryMap = new LinkedHashMap<>();
        for (Category category : categories) putUnique(categoryMap, category.categoryId(), category, diagnostics, RecordKind.CATEGORY);
        Map<NamespacedId, Form> formMap = new LinkedHashMap<>();
        for (Form form : forms) {
            if (!categoryMap.containsKey(form.categoryId())) diagnostics.add(diagnostic(DiagnosticCode.UNKNOWN_CATEGORY, RecordKind.RUNE_FORM, Optional.of(form.formId()), "unknown category"));
            else putUnique(formMap, form.formId(), form, diagnostics, RecordKind.RUNE_FORM);
        }
        if (!diagnostics.isEmpty()) throw new CandidateFailure(diagnostics);
        return new Snapshot(generation, Map.of(), formMap, categoryMap, Map.of(), List.of());
    }

    private static <T> void putUnique(Map<NamespacedId, T> values, NamespacedId id, T value, List<Diagnostic> diagnostics, RecordKind kind) {
        if (values.putIfAbsent(id, value) != null) diagnostics.add(diagnostic(DiagnosticCode.DUPLICATE_ID, kind, Optional.of(id), "duplicate id"));
    }
    private static Diagnostic diagnostic(DiagnosticCode code, RecordKind kind, Optional<NamespacedId> id, String message) { return new Diagnostic(Severity.FATAL, code, kind, id, SourceKind.BUILT_IN, "authoring-model", message); }
    private static CandidateFailure failure(DiagnosticCode code, RecordKind kind, Optional<NamespacedId> id, String message) { return new CandidateFailure(List.of(diagnostic(code, kind, id, message))); }
    private static void validateFormula(Formula formula) { if (formula.nodeCount() > 128 || formula.depth() > 16) throw failure(DiagnosticCode.INVALID_TEMPLATE, RecordKind.RUNE_FORM, Optional.empty(), "formula exceeds node or depth limit"); }
    private static List<String> boundedStrings(List<String> values, String label) { return List.copyOf(values).stream().map(value -> requireBounded(value, label)).toList(); }
    private static String require(String value, String label) { if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " must not be blank"); return value; }
    private static String requireBounded(String value, String label) { value = require(value, label); if (value.length() > MAX_KEY_LENGTH) throw failure(DiagnosticCode.LIMIT_EXCEEDED, RecordKind.SNAPSHOT, Optional.empty(), label + " exceeds 160 characters"); return value; }
}
