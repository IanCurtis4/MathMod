package com.mathmod.authoring;

import com.mathmod.program.CustomNumericParameter;
import com.mathmod.program.CustomSpellAction;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BuiltInAuthoringMetadataTest {
    @Test
    void frozenTableCharacterizesLegacyIdentityWithoutEnumOrder() {
        AuthoringMetadata.Snapshot snapshot = BuiltInAuthoringMetadata.snapshot();
        assertEquals(67, snapshot.runeForms().size());
        assertEquals(11, snapshot.categories().size());
        assertEquals(List.of("mathmod:alchemy", "mathmod:algebra", "mathmod:calculus", "mathmod:effects", "mathmod:geometry", "mathmod:linear_algebra", "mathmod:metamagic", "mathmod:queries", "mathmod:sources", "mathmod:symmetry", "mathmod:trigonometry"), snapshot.categories().keySet().stream().map(NamespacedId::toString).sorted().toList());
        assertEquals(67, BuiltInAuthoringMetadata.frozenFormIds().size());
        assertEquals(Arrays.stream(CustomSpellAction.values()).collect(java.util.stream.Collectors.toMap(Enum::name, CustomSpellAction::persistentId)), BuiltInAuthoringMetadata.frozenEnumNameToFormId());
        assertEquals("mathmod:self", BuiltInAuthoringMetadata.frozenFormIds().getFirst());
        assertEquals("mathmod:select_number", BuiltInAuthoringMetadata.frozenFormIds().getLast());
        for (CustomSpellAction action : CustomSpellAction.values()) {
            AuthoringMetadata.Form form = snapshot.find(NamespacedId.parse(action.persistentId())).orElseThrow();
            assertEquals(action.translationKey(), form.translationKey());
            assertEquals(action.iconRuneId(), form.icon().runeId().toString());
            assertEquals(action.compactNotation(), ((AuthoringMetadata.Symbol) form.formula()).token());
            assertEquals(action.inputs().stream().map(Enum::name).toList(), form.consumedInputIds());
            assertEquals(List.of(), form.inputHints());
            assertEquals(action.numericParameters().stream().map(CustomNumericParameter::key).toList(), form.parameters().stream().map(AuthoringMetadata.Parameter::key).toList());
        }
    }

    @Test
    void numericCanonicalizationAndStructuredFingerprintFollowReplayBoundary() {
        AuthoringMetadata.Form original = BuiltInAuthoringMetadata.snapshot().find(NamespacedId.parse("mathmod:number_one")).orElseThrow();
        AuthoringMetadata.Parameter value = original.parameters().getFirst();
        assertEquals(1.0D, value.canonicalize(Double.NaN));
        assertEquals(1.0D, value.canonicalize(Double.POSITIVE_INFINITY));
        assertEquals(1.0D, value.canonicalize(Double.NEGATIVE_INFINITY));
        assertEquals(-1024.0D, value.canonicalize(-9000.0D));
        assertEquals(1024.0D, value.canonicalize(9000.0D));

        AuthoringMetadata.Form presentationOnly = form("mathmod:fingerprint", List.of(), List.of("input"), List.of("different-description"), 0);
        AuthoringMetadata.Form sameSemantics = form("mathmod:fingerprint", List.of(), List.of("input"), List.of("other-description"), 9);
        AuthoringMetadata.Form fp1a = form("mathmod:fingerprint", List.of(), List.of("a|input:b"), List.of(), 0);
        AuthoringMetadata.Form fp1b = form("mathmod:fingerprint", List.of(), List.of("a", "b"), List.of(), 0);
        assertEquals(presentationOnly.semanticFingerprint(), sameSemantics.semanticFingerprint(), "descriptive hints are not replay semantics");
        assertNotEquals(fp1a.semanticFingerprint(), fp1b.semanticFingerprint(), "structured input lists cannot collide through delimiters");

        AuthoringMetadata.Parameter first = parameter("first", 0, -1, 1);
        AuthoringMetadata.Parameter second = parameter("second", 1, -2, 2);
        AuthoringMetadata.Form ordered = form("mathmod:parameter_order", List.of(first, second), List.of(), List.of(), 0);
        assertNotEquals(ordered.semanticFingerprint(), form("mathmod:parameter_order", List.of(second, first), List.of(), List.of(), 0).semanticFingerprint());
        assertNotEquals(ordered.semanticFingerprint(), form("mathmod:parameter_order", List.of(parameter("first", 0, -1, 2), second), List.of(), List.of(), 0).semanticFingerprint());
        assertNotEquals(ordered.semanticFingerprint(), new AuthoringMetadata.SemanticFingerprint(ordered.formId(), ordered.semanticFingerprint().parameters(), NamespacedId.parse("mathmod:legacy/other"), ordered.consumedInputIds()));
        assertNotEquals(ordered.semanticFingerprint(), new AuthoringMetadata.SemanticFingerprint(ordered.formId(), List.of(new AuthoringMetadata.ParameterSemantics("first", NamespacedId.parse("mathmod:other"), 0, new AuthoringMetadata.NumberConstraints(-1, 1)), secondSemantic(second)), ordered.expansion().adapterId(), ordered.consumedInputIds()));
    }

    @Test
    void snapshotsExposeDeterministicOrderingAndImmutableCollections() {
        AuthoringMetadata.Category a = new AuthoringMetadata.Category(NamespacedId.parse("mathmod:a"), "a", 3);
        AuthoringMetadata.Category b = new AuthoringMetadata.Category(NamespacedId.parse("mathmod:b"), "b", 3);
        AuthoringMetadata.Form first = form("mathmod:a_form", List.of(), List.of(), List.of(), 3);
        AuthoringMetadata.Form second = form("mathmod:b_form", List.of(), List.of(), List.of(), 3);
        AuthoringMetadata.Snapshot snapshot = AuthoringMetadata.snapshot(1, List.of(second, first), List.of(b, a, category()));
        assertEquals(List.of("mathmod:test", "mathmod:a", "mathmod:b"), snapshot.orderedCategories().stream().map(category -> category.categoryId().toString()).toList());
        assertEquals(List.of("mathmod:a_form", "mathmod:b_form"), snapshot.orderedForms(category().categoryId()).stream().map(form -> form.formId().toString()).toList());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.categories().clear());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.orderedCategories().clear());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.runeForms().values().iterator().next().consumedInputIds().clear());
    }

    @Test
    void candidateFailuresExposeStableStructuredDiagnostics() {
        AuthoringMetadata.Form form = form("mathmod:test_form", List.of(), List.of(), List.of(), 0);
        assertDiagnostic(AuthoringMetadata.DiagnosticCode.DUPLICATE_ID, () -> AuthoringMetadata.snapshot(1, List.of(form, form), List.of(category())));
        AuthoringMetadata.Form orphan = new AuthoringMetadata.Form(form.formId(), form.translationKey(), NamespacedId.parse("mathmod:missing"), form.icon(), form.formula(), form.parameters(), form.consumedInputIds(), form.inputHints(), form.outputHint(), form.sortOrder(), form.expansion());
        assertDiagnostic(AuthoringMetadata.DiagnosticCode.UNKNOWN_CATEGORY, () -> AuthoringMetadata.snapshot(1, List.of(orphan), List.of(category())));
        assertDiagnostic(AuthoringMetadata.DiagnosticCode.LIMIT_EXCEEDED, () -> AuthoringMetadata.snapshot(1, forms(1_025), List.of(category())));
        assertDiagnostic(AuthoringMetadata.DiagnosticCode.LIMIT_EXCEEDED, () -> AuthoringMetadata.snapshot(1, List.of(form), categories(129)));
        assertDiagnostic(AuthoringMetadata.DiagnosticCode.LIMIT_EXCEEDED, () -> form("mathmod:too_many_parameters", java.util.Collections.nCopies(17, parameter("p", 0, -1, 1)), List.of(), List.of(), 0));
        assertDiagnostic(AuthoringMetadata.DiagnosticCode.LIMIT_EXCEEDED, () -> form("mathmod:too_many_inputs", List.of(), java.util.Collections.nCopies(17, "input"), List.of(), 0));
        assertDiagnostic(AuthoringMetadata.DiagnosticCode.LIMIT_EXCEEDED, () -> parameter("x".repeat(161), 0, -1, 1));
    }

    @Test
    void formulaAndFallbackRemainBounded() {
        assertThrows(IllegalArgumentException.class, () -> new AuthoringMetadata.Symbol("x".repeat(65)));
        assertThrows(IllegalArgumentException.class, () -> new AuthoringMetadata.Sequence(java.util.Collections.nCopies(33, new AuthoringMetadata.Symbol("x"))));
        AuthoringMetadata.RunePresentation fallback = AuthoringMetadata.RunePresentation.technicalFallback(NamespacedId.parse("mathmod:number_add"), List.of("left", "right"));
        assertEquals("mathmod:uncategorized", fallback.categoryId().toString());
        assertTrue(fallback.formula().nodeCount() <= 128);
    }

    @Test
    void snapshotEnforcesRunePresentationDescriptorBoundBeforePublication() {
        AuthoringMetadata.Snapshot accepted = new AuthoringMetadata.Snapshot(1, presentations(2_048), Map.of(), Map.of(), Map.of(), List.of());
        assertEquals(2_048, accepted.runePresentations().size());

        AuthoringMetadata.Snapshot rejected = null;
        AuthoringMetadata.CandidateFailure failure;
        try {
            rejected = new AuthoringMetadata.Snapshot(2, presentations(2_049), Map.of(), Map.of(), Map.of(), List.of());
            fail("an over-limit presentation candidate must fail");
            return;
        } catch (AuthoringMetadata.CandidateFailure caught) {
            failure = caught;
        }
        assertEquals(AuthoringMetadata.DiagnosticCode.LIMIT_EXCEEDED, failure.diagnostics().getFirst().code());
        assertNull(rejected, "a rejected presentation candidate must not return a snapshot");
    }

    private static void assertDiagnostic(AuthoringMetadata.DiagnosticCode code, org.junit.jupiter.api.function.Executable executable) {
        AuthoringMetadata.CandidateFailure failure = assertThrows(AuthoringMetadata.CandidateFailure.class, executable);
        assertEquals(code, failure.diagnostics().getFirst().code());
        assertEquals(AuthoringMetadata.Severity.FATAL, failure.diagnostics().getFirst().severity());
    }
    private static AuthoringMetadata.Category category() { return new AuthoringMetadata.Category(NamespacedId.parse("mathmod:test"), "test", 0); }
    private static AuthoringMetadata.Parameter parameter(String key, double value, double min, double max) { return new AuthoringMetadata.Parameter(key, AuthoringMetadata.NUMBER_TYPE, "test", value, new AuthoringMetadata.NumberConstraints(min, max), Optional.empty()); }
    private static AuthoringMetadata.ParameterSemantics secondSemantic(AuthoringMetadata.Parameter parameter) { return new AuthoringMetadata.ParameterSemantics(parameter.key(), parameter.typeId(), parameter.defaultValue(), parameter.constraints()); }
    private static AuthoringMetadata.Form form(String id, List<AuthoringMetadata.Parameter> parameters, List<String> consumed, List<String> hints, int order) { return new AuthoringMetadata.Form(NamespacedId.parse(id), "test", category().categoryId(), new AuthoringMetadata.RuneIcon(NamespacedId.parse("mathmod:test_rune")), new AuthoringMetadata.Symbol("x"), parameters, consumed, hints, Optional.empty(), order, new AuthoringMetadata.LegacyAdapter(NamespacedId.parse("mathmod:legacy/test"))); }
    private static List<AuthoringMetadata.Form> forms(int count) { return java.util.stream.IntStream.range(0, count).mapToObj(index -> form("mathmod:f" + index, List.of(), List.of(), List.of(), index)).toList(); }
    private static List<AuthoringMetadata.Category> categories(int count) { return java.util.stream.IntStream.range(0, count).mapToObj(index -> new AuthoringMetadata.Category(NamespacedId.parse("mathmod:c" + index), "c" + index, index)).toList(); }
    private static Map<NamespacedId, AuthoringMetadata.RunePresentation> presentations(int count) {
        return java.util.stream.IntStream.range(0, count).boxed().collect(java.util.stream.Collectors.toMap(
                index -> NamespacedId.parse("mathmod:presentation_" + index),
                index -> new AuthoringMetadata.RunePresentation(NamespacedId.parse("mathmod:presentation_" + index), "test", category().categoryId(), new AuthoringMetadata.RuneIcon(NamespacedId.parse("mathmod:test_rune")), new AuthoringMetadata.Symbol("x"), index, Optional.empty())
        ));
    }
}
