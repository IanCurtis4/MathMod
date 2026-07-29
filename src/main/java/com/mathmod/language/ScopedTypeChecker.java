package com.mathmod.language;

import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Checks the functional authoring language before it is lowered into a ProgramGraph. */
public final class ScopedTypeChecker {
    private final RuneRegistry runeRegistry;
    private final ScopedCompileBudget budget;

    public ScopedTypeChecker(RuneRegistry runeRegistry) {
        this(runeRegistry, null);
    }

    ScopedTypeChecker(RuneRegistry runeRegistry, ScopedCompileBudget budget) {
        this.runeRegistry = runeRegistry;
        this.budget = budget;
    }

    public ScopedTypeCheckResult check(ScopedProgramSource source) {
        if (budget == null) {
            return new ScopedTypeChecker(runeRegistry, new ScopedCompileBudget()).check(source);
        }
        List<ScopedLanguageIssue> issues = new ArrayList<>(
                ScopedStructureValidator.validate(source, id -> runeRegistry.find(id).map(RuneDefinition::purity), budget).issues()
        );
        if (!issues.isEmpty()) return new ScopedTypeCheckResult(Optional.empty(), issues);
        Optional<RuneTypeExpression> inferred = infer(source.expression(), List.of(), "$", issues);
        if (inferred.isPresent() && !inferred.get().equals(source.resultType())) {
            issue(issues, ScopedLanguageIssue.Code.TYPE_MISMATCH, "$",
                    "Program result declares " + source.resultType() + " but expression produces " + inferred.get());
        }
        if (inferred.filter(RuneTypeExpression.FunctionType.class::isInstance).isPresent()) {
            issue(issues, ScopedLanguageIssue.Code.FUNCTION_RESULT_FORBIDDEN, "$",
                    "An inscription must lower to a concrete rune value, not a function");
        }
        return new ScopedTypeCheckResult(inferred, issues);
    }

    private Optional<RuneTypeExpression> infer(
            ScopedExpression expression,
            List<RuneTypeExpression> environment,
            String path,
            List<ScopedLanguageIssue> issues
    ) {
        budget.charge(ScopedCompileBudget.Event.TYPE_NODE);
        if (expression instanceof ScopedExpression.Literal literal) return Optional.of(literal.type());
        if (expression instanceof ScopedExpression.ParameterReference parameter) {
            if (parameter.deBruijnIndex() >= environment.size()) return Optional.empty();
            return Optional.of(environment.get(parameter.deBruijnIndex()));
        }
        if (expression instanceof ScopedExpression.Lambda lambda) {
            return infer(lambda.body(), bind(environment, lambda.parameterType()), path + ".body", issues)
                    .map(result -> RuneTypeExpression.function(lambda.parameterType(), result));
        }
        if (expression instanceof ScopedExpression.Application application) {
            Optional<RuneTypeExpression> function = infer(application.function(), environment, path + ".function", issues);
            Optional<RuneTypeExpression> argument = infer(application.argument(), environment, path + ".argument", issues);
            if (function.filter(RuneTypeExpression.FunctionType.class::isInstance).isEmpty()) {
                issue(issues, ScopedLanguageIssue.Code.NON_FUNCTION_APPLICATION, path,
                        "Application requires a function on its left side");
                return Optional.empty();
            }
            RuneTypeExpression.FunctionType functionType = (RuneTypeExpression.FunctionType) function.orElseThrow();
            if (argument.isPresent() && !functionType.parameterType().equals(argument.get())) {
                issue(issues, ScopedLanguageIssue.Code.TYPE_MISMATCH, path + ".argument",
                        "Function expects " + functionType.parameterType() + " but received " + argument.get());
            }
            return Optional.of(functionType.resultType());
        }
        if (expression instanceof ScopedExpression.Let let) {
            Optional<RuneTypeExpression> value = infer(let.value(), environment, path + ".value", issues);
            return value.flatMap(type -> infer(let.body(), bind(environment, type), path + ".body", issues));
        }

        ScopedExpression.RuneCall call = (ScopedExpression.RuneCall) expression;
        Optional<RuneDefinition> definition = runeRegistry.find(call.runeId());
        if (definition.isEmpty()) return Optional.empty();
        if (!definition.get().enabled()) {
            issue(issues, ScopedLanguageIssue.Code.DISABLED_RUNE, path,
                    "Rune " + call.runeId() + " is disabled");
        }
        for (int index = 0; index < call.arguments().size(); index++) {
            ScopedExpression.Argument argument = call.arguments().get(index);
            Optional<com.mathmod.runes.RuneInput> input = definition.get().input(argument.inputName());
            if (input.isEmpty()) {
                issue(issues, ScopedLanguageIssue.Code.UNEXPECTED_RUNE_INPUT, path,
                        "Rune " + call.runeId() + " has no input " + argument.inputName());
                continue;
            }
            String argumentPath = path + ".arguments[" + index + "]";
            Optional<RuneTypeExpression> argumentType = infer(argument.expression(), environment, argumentPath, issues);
            RuneTypeExpression expected = RuneTypeExpression.value(input.get().type());
            if (argumentType.isPresent() && !expected.equals(argumentType.get())) {
                issue(issues, ScopedLanguageIssue.Code.TYPE_MISMATCH, argumentPath,
                        "Input " + argument.inputName() + " expects " + expected + " but received " + argumentType.get());
            }
        }
        definition.get().inputs().forEach(input -> {
            boolean present = call.arguments().stream().anyMatch(argument -> argument.inputName().equals(input.name()));
            if (!present) issue(issues, ScopedLanguageIssue.Code.MISSING_RUNE_INPUT, path,
                    "Rune " + call.runeId() + " requires input " + input.name());
        });
        return Optional.of(RuneTypeExpression.value(definition.get().outputType()));
    }

    private static List<RuneTypeExpression> bind(List<RuneTypeExpression> environment, RuneTypeExpression type) {
        List<RuneTypeExpression> result = new ArrayList<>(environment.size() + 1);
        result.add(type);
        result.addAll(environment);
        return List.copyOf(result);
    }

    private static void issue(List<ScopedLanguageIssue> issues, ScopedLanguageIssue.Code code, String path, String message) {
        issues.add(new ScopedLanguageIssue(code, path, message));
    }
}
