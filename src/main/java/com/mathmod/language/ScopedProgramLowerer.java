package com.mathmod.language;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Closure-based lowering performs beta reduction while preserving let-bound node sharing. */
public final class ScopedProgramLowerer {
    private final RuneRegistry runeRegistry;
    private final ScopedCompileBudget budget;
    private final ScopedLiteralResolver literalResolver;

    public ScopedProgramLowerer(RuneRegistry runeRegistry) {
        this(runeRegistry, null);
    }

    ScopedProgramLowerer(RuneRegistry runeRegistry, ScopedCompileBudget budget) {
        this.runeRegistry = runeRegistry;
        this.budget = budget;
        this.literalResolver = new ScopedLiteralResolver(runeRegistry);
    }

    public ScopedLoweringResult lower(ScopedProgramSource source) {
        if (budget == null) {
            return new ScopedProgramLowerer(runeRegistry, new ScopedCompileBudget()).lower(source);
        }
        try {
            ScopedTypeCheckResult checked = new ScopedTypeChecker(runeRegistry, budget).check(source);
            if (!checked.valid()) return new ScopedLoweringResult(Optional.empty(), checked.issues());
            return lowerChecked(source);
        } catch (ScopedCompileBudget.LimitExceeded exceeded) {
            return new ScopedLoweringResult(Optional.empty(), List.of(new ScopedLanguageIssue(
                    ScopedLanguageIssue.Code.COMPILE_STEP_LIMIT, "$", "Scoped compilation exceeded its step limit")));
        }
    }

    ScopedLoweringResult lowerChecked(ScopedProgramSource source) {
        State state = new State(budget);
        Optional<Value> value = lower(source.expression(), List.of(), "$", state);
        if (value.filter(NodeValue.class::isInstance).isEmpty()) {
            if (!state.issues.isEmpty()) return new ScopedLoweringResult(Optional.empty(), state.issues);
            state.issue(ScopedLanguageIssue.Code.FUNCTION_RESULT_FORBIDDEN, "$", "Program result did not lower to a graph node");
            return new ScopedLoweringResult(Optional.empty(), state.issues);
        }
        if (!state.issues.isEmpty()) return new ScopedLoweringResult(Optional.empty(), state.issues);
        return new ScopedLoweringResult(Optional.of(new ProgramGraph(state.nodes, state.edges,
                ((NodeValue) value.orElseThrow()).nodeId(), source.budgetLimit())), state.issues);
    }

    private Optional<Value> lower(ScopedExpression expression, List<Value> environment, String path, State state) {
        budget.charge(ScopedCompileBudget.Event.LOWERING_EXPRESSION);
        if (expression instanceof ScopedExpression.ParameterReference reference) {
            return reference.deBruijnIndex() < environment.size()
                    ? Optional.of(environment.get(reference.deBruijnIndex())) : Optional.empty();
        }
        if (expression instanceof ScopedExpression.Literal literal) {
            ScopedLiteralResolver.Resolution resolved = literalResolver.resolve(literal, budget);
            if (resolved.failure().isPresent()) {
                ScopedLiteralResolver.Failure failure = resolved.failure().orElseThrow();
                state.issue(failure.code(), path, failure.message());
                return Optional.empty();
            }
            ScopedLiteralResolver.LoweredLiteral lowered = resolved.literal().orElseThrow();
            return Optional.of(state.node(lowered.runeId(), lowered.constants()));
        }
        if (expression instanceof ScopedExpression.Lambda lambda) {
            budget.charge(ScopedCompileBudget.Event.CLOSURE_OR_BINDING);
            return Optional.of(new Closure(lambda.body(), environment, path + ".body"));
        }
        if (expression instanceof ScopedExpression.Let let) {
            Optional<Value> value = lower(let.value(), environment, path + ".value", state);
            if (value.isEmpty()) return Optional.empty();
            budget.charge(ScopedCompileBudget.Event.CLOSURE_OR_BINDING);
            return lower(let.body(), bind(environment, value.orElseThrow()), path + ".body", state);
        }
        if (expression instanceof ScopedExpression.Application application) {
            Optional<Value> function = lower(application.function(), environment, path + ".function", state);
            Optional<Value> argument = lower(application.argument(), environment, path + ".argument", state);
            if (function.filter(Closure.class::isInstance).isEmpty() || argument.isEmpty()) return Optional.empty();
            budget.charge(ScopedCompileBudget.Event.APPLICATION);
            Closure closure = (Closure) function.orElseThrow();
            budget.charge(ScopedCompileBudget.Event.CLOSURE_OR_BINDING);
            return lower(closure.body(), bind(closure.environment(), argument.get()), closure.bodyPath(), state);
        }
        ScopedExpression.RuneCall call = (ScopedExpression.RuneCall) expression;
        Map<String, NodeValue> inputs = new LinkedHashMap<>();
        for (int index = 0; index < call.arguments().size(); index++) {
            ScopedExpression.Argument argument = call.arguments().get(index);
            Optional<Value> lowered = lower(argument.expression(), environment, path + ".arguments[" + index + "]", state);
            if (lowered.filter(NodeValue.class::isInstance).isEmpty()) return Optional.empty();
            inputs.put(argument.inputName(), (NodeValue) lowered.orElseThrow());
        }
        NodeValue target = state.node(call.runeId(), Map.of());
        inputs.forEach((input, value) -> state.edge(value.nodeId(), target.nodeId(), input));
        return Optional.of(target);
    }

    private static List<Value> bind(List<Value> environment, Value value) {
        List<Value> result = new ArrayList<>(environment.size() + 1);
        result.add(value);
        result.addAll(environment);
        return List.copyOf(result);
    }

    private sealed interface Value permits NodeValue, Closure { }
    private record NodeValue(String nodeId) implements Value { }
    private record Closure(ScopedExpression body, List<Value> environment, String bodyPath) implements Value {
        private Closure { environment = List.copyOf(environment); }
    }

    private static final class State {
        private final ScopedCompileBudget budget;
        private final List<ProgramNode> nodes = new ArrayList<>();
        private final List<ProgramEdge> edges = new ArrayList<>();
        private final List<ScopedLanguageIssue> issues = new ArrayList<>();
        private int nextNode;

        private State(ScopedCompileBudget budget) {
            this.budget = budget;
        }

        private NodeValue node(String runeId, Map<String, String> constants) {
            budget.charge(ScopedCompileBudget.Event.GRAPH_NODE);
            String id = "f" + nextNode++;
            nodes.add(new ProgramNode(id, runeId, constants));
            return new NodeValue(id);
        }

        private void edge(String from, String to, String input) {
            budget.charge(ScopedCompileBudget.Event.GRAPH_EDGE);
            edges.add(new ProgramEdge(from, to, input));
        }

        private void issue(ScopedLanguageIssue.Code code, String path, String message) {
            issues.add(new ScopedLanguageIssue(code, path, message));
        }
    }
}
