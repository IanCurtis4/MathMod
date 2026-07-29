package com.mathmod.language;

import com.mathmod.runes.RunePurity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class ScopedStructureValidator {
    private ScopedStructureValidator() {
    }

    public static ScopedValidationResult validateStructure(ScopedProgramSource source) {
        return validate(source, null);
    }

    public static ScopedValidationResult validate(
            ScopedProgramSource source,
            Function<String, Optional<RunePurity>> purityLookup
    ) {
        return validate(source, purityLookup, new ScopedCompileBudget());
    }

    static ScopedValidationResult validate(
            ScopedProgramSource source,
            Function<String, Optional<RunePurity>> purityLookup,
            ScopedCompileBudget budget
    ) {
        List<ScopedLanguageIssue> issues = new ArrayList<>();
        Counters counters = new Counters();
        try {
            checkType(source.resultType(), "$.result_type", issues);
            visit(source.expression(), "$", 0, 0, Position.TAIL, purityLookup, counters, issues, budget);
        } catch (ScopedCompileBudget.LimitExceeded exceeded) {
            issue(issues, ScopedLanguageIssue.Code.COMPILE_STEP_LIMIT, "$", "Scoped compilation exceeded its step limit");
            return new ScopedValidationResult(issues);
        }
        if (counters.nodes > ScopedLanguageLimits.MAX_AST_NODES) {
            issue(
                    issues,
                    ScopedLanguageIssue.Code.AST_LIMIT,
                    "$",
                    "Expression contains " + counters.nodes + " nodes; maximum is "
                            + ScopedLanguageLimits.MAX_AST_NODES
            );
        }
        if (counters.applications > ScopedLanguageLimits.MAX_APPLICATIONS) {
            issue(
                    issues,
                    ScopedLanguageIssue.Code.APPLICATION_LIMIT,
                    "$",
                    "Expression contains " + counters.applications + " applications; maximum is "
                            + ScopedLanguageLimits.MAX_APPLICATIONS
            );
        }
        return new ScopedValidationResult(issues);
    }

    private static void visit(
            ScopedExpression expression,
            String path,
            int bindingDepth,
            int lambdaDepth,
            Position position,
            Function<String, Optional<RunePurity>> purityLookup,
            Counters counters,
            List<ScopedLanguageIssue> issues,
            ScopedCompileBudget budget
    ) {
        budget.charge(ScopedCompileBudget.Event.STRUCTURAL_NODE);
        counters.nodes++;
        if (expression instanceof ScopedExpression.Literal literal) {
            checkType(literal.type(), path + ".type", issues);
            if (literal.encodedValue().length() > ScopedLanguageLimits.MAX_LITERAL_LENGTH) {
                issue(
                        issues,
                        ScopedLanguageIssue.Code.LITERAL_LIMIT,
                        path,
                        "Literal exceeds " + ScopedLanguageLimits.MAX_LITERAL_LENGTH + " characters"
                );
            }
            return;
        }
        if (expression instanceof ScopedExpression.ParameterReference parameter) {
            if (parameter.deBruijnIndex() >= bindingDepth) {
                issue(
                        issues,
                        ScopedLanguageIssue.Code.FREE_PARAMETER,
                        path,
                        "Parameter index " + parameter.deBruijnIndex()
                                + " is outside binding depth " + bindingDepth
                );
            }
            return;
        }
        if (expression instanceof ScopedExpression.RuneCall call) {
            if (call.arguments().size() > ScopedLanguageLimits.MAX_ARGUMENTS_PER_CALL) {
                issue(
                        issues,
                        ScopedLanguageIssue.Code.ARGUMENT_LIMIT,
                        path,
                        "Rune call exceeds " + ScopedLanguageLimits.MAX_ARGUMENTS_PER_CALL
                                + " arguments"
                );
            }
            if (purityLookup != null) {
                Optional<RunePurity> purity = purityLookup.apply(call.runeId());
                if (purity.isEmpty()) {
                    issue(
                            issues,
                            ScopedLanguageIssue.Code.UNKNOWN_RUNE,
                            path,
                            "Unknown rune " + call.runeId()
                    );
                } else if (lambdaDepth > 0 && purity.orElseThrow() != RunePurity.PURE) {
                    issue(
                            issues,
                            ScopedLanguageIssue.Code.IMPURE_LAMBDA_BODY,
                            path,
                            "Lambda bodies may call only pure runes"
                    );
                } else if (purity.orElseThrow() == RunePurity.EFFECT
                        && position != Position.TAIL) {
                    issue(
                            issues,
                            ScopedLanguageIssue.Code.EFFECT_NOT_IN_TAIL,
                            path,
                            "Effect runes may occur only in the terminal proof position"
                    );
                }
            }
            for (int index = 0; index < call.arguments().size(); index++) {
                visit(
                        call.arguments().get(index).expression(),
                        path + ".arguments[" + index + "]",
                        bindingDepth,
                        lambdaDepth,
                        Position.VALUE,
                        purityLookup,
                        counters,
                        issues,
                        budget
                );
            }
            return;
        }
        if (expression instanceof ScopedExpression.Lambda lambda) {
            checkType(lambda.parameterType(), path + ".parameter_type", issues);
            int nextDepth = bindingDepth + 1;
            checkBindingDepth(nextDepth, path, issues);
            visit(
                    lambda.body(),
                    path + ".body",
                    nextDepth,
                    lambdaDepth + 1,
                    Position.VALUE,
                    purityLookup,
                    counters,
                    issues,
                    budget
            );
            return;
        }
        if (expression instanceof ScopedExpression.Application application) {
            counters.applications++;
            visit(
                    application.function(),
                    path + ".function",
                    bindingDepth,
                    lambdaDepth,
                    Position.VALUE,
                    purityLookup,
                    counters,
                    issues,
                    budget
            );
            visit(
                    application.argument(),
                    path + ".argument",
                    bindingDepth,
                    lambdaDepth,
                    Position.VALUE,
                    purityLookup,
                    counters,
                    issues,
                    budget
            );
            return;
        }
        ScopedExpression.Let let = (ScopedExpression.Let) expression;
        visit(
                let.value(),
                path + ".value",
                bindingDepth,
                lambdaDepth,
                Position.VALUE,
                purityLookup,
                counters,
                issues,
                budget
        );
        int nextDepth = bindingDepth + 1;
        checkBindingDepth(nextDepth, path, issues);
        visit(
                let.body(),
                path + ".body",
                nextDepth,
                lambdaDepth,
                position,
                purityLookup,
                counters,
                issues,
                budget
        );
    }

    private static void checkType(
            RuneTypeExpression type,
            String path,
            List<ScopedLanguageIssue> issues
    ) {
        if (type.nestingDepth() > ScopedLanguageLimits.MAX_TYPE_DEPTH) {
            issue(
                    issues,
                    ScopedLanguageIssue.Code.TYPE_DEPTH_LIMIT,
                    path,
                    "Type nesting exceeds " + ScopedLanguageLimits.MAX_TYPE_DEPTH
            );
        }
    }

    private static void checkBindingDepth(
            int depth,
            String path,
            List<ScopedLanguageIssue> issues
    ) {
        if (depth > ScopedLanguageLimits.MAX_BINDING_DEPTH) {
            issue(
                    issues,
                    ScopedLanguageIssue.Code.BINDING_DEPTH_LIMIT,
                    path,
                    "Binding depth exceeds " + ScopedLanguageLimits.MAX_BINDING_DEPTH
            );
        }
    }

    private static void issue(
            List<ScopedLanguageIssue> issues,
            ScopedLanguageIssue.Code code,
            String path,
            String message
    ) {
        issues.add(new ScopedLanguageIssue(code, path, message));
    }

    private static final class Counters {
        private int nodes;
        private int applications;
    }

    private enum Position {
        TAIL,
        VALUE
    }
}
