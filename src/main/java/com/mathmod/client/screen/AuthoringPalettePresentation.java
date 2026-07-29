package com.mathmod.client.screen;

import com.mathmod.authoring.AuthoringMetadata;
import com.mathmod.authoring.BuiltInAuthoringMetadata;
import com.mathmod.program.CustomSpellAction;
import com.mathmod.util.NamespacedId;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Immutable screen projection of the trusted authoring snapshot; it grants no execution authority. */
final class AuthoringPalettePresentation {
    private final AuthoringMetadata.Snapshot snapshot;
    private final Map<NamespacedId, CustomSpellAction> legacyActions;

    private AuthoringPalettePresentation(AuthoringMetadata.Snapshot snapshot, Map<NamespacedId, CustomSpellAction> legacyActions) {
        this.snapshot = snapshot;
        this.legacyActions = Map.copyOf(legacyActions);
    }

    static AuthoringPalettePresentation builtIns() {
        AuthoringMetadata.Snapshot snapshot = BuiltInAuthoringMetadata.snapshot();
        Map<NamespacedId, CustomSpellAction> actions = CustomSpellAction.values().length == snapshot.runeForms().size()
                ? Arrays.stream(CustomSpellAction.values()).collect(java.util.stream.Collectors.toMap(
                        action -> NamespacedId.parse(action.persistentId()), action -> action))
                : Map.of();
        return new AuthoringPalettePresentation(snapshot, actions);
    }

    List<AuthoringMetadata.Category> categories() { return snapshot.orderedCategories(); }
    List<Form> forms() { return categories().stream().flatMap(category -> forms(category.categoryId()).stream()).toList(); }
    List<Form> forms(NamespacedId categoryId) {
        return snapshot.orderedForms(categoryId).stream().map(this::form).toList();
    }
    Optional<Form> find(NamespacedId id) { return snapshot.find(id).map(this::form); }
    Optional<Form> find(CustomSpellAction action) { return find(NamespacedId.parse(action.persistentId())); }
    List<Form> visibleForms(String query, java.util.function.Function<Form, Boolean> matches) {
        return forms().stream().filter(matches::apply).toList();
    }

    private Form form(AuthoringMetadata.Form form) {
        return new Form(form, Optional.ofNullable(legacyActions.get(form.formId())));
    }

    static CategoryTone categoryTone(NamespacedId categoryId) {
        return switch (categoryId.path()) {
            case "sources", "symmetry" -> CategoryTone.BLUE;
            case "algebra", "trigonometry", "metamagic" -> CategoryTone.GOLD;
            case "geometry", "linear_algebra" -> CategoryTone.TEAL;
            case "calculus" -> CategoryTone.CORAL_SOFT;
            case "alchemy", "queries" -> CategoryTone.GREEN;
            case "effects" -> CategoryTone.CORAL;
            default -> CategoryTone.MUTED;
        };
    }

    enum CategoryTone { BLUE, GOLD, TEAL, CORAL_SOFT, GREEN, CORAL, MUTED }

    record Form(AuthoringMetadata.Form metadata, Optional<CustomSpellAction> legacyAction) {
        Form {
            legacyAction = legacyAction == null ? Optional.empty() : legacyAction;
        }
        Map<String, Double> canonicalArguments(Map<String, Double> supplied) {
            return metadata.parameters().stream().collect(java.util.stream.Collectors.toMap(
                    AuthoringMetadata.Parameter::key,
                    parameter -> parameter.canonicalize(supplied == null ? parameter.defaultValue()
                            : supplied.getOrDefault(parameter.key(), parameter.defaultValue())),
                    (left, right) -> left,
                    java.util.LinkedHashMap::new
            ));
        }
        String technicalName() { return metadata.formId().path().replace('_', ' '); }
        String presentationName(String translated) {
            return translated == null || translated.isBlank() ? technicalName() : translated;
        }
        String compactFormula() {
            return metadata.formula() instanceof AuthoringMetadata.Symbol symbol ? symbol.token() : technicalName();
        }
    }
}
