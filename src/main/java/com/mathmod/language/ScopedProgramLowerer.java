package com.mathmod.language;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Closure-based lowering performs beta reduction while preserving let-bound node sharing. */
public final class ScopedProgramLowerer {
    private final RuneRegistry runeRegistry;

    public ScopedProgramLowerer(RuneRegistry runeRegistry) {
        this.runeRegistry = runeRegistry;
    }

    public ScopedLoweringResult lower(ScopedProgramSource source) {
        ScopedTypeCheckResult checked = new ScopedTypeChecker(runeRegistry).check(source);
        if (!checked.valid()) return new ScopedLoweringResult(Optional.empty(), checked.issues());
        State state = new State();
        Optional<Value> value = lower(source.expression(), List.of(), "$", state);
        if (value.filter(NodeValue.class::isInstance).isEmpty()) {
            state.issue(ScopedLanguageIssue.Code.FUNCTION_RESULT_FORBIDDEN, "$", "Program result did not lower to a graph node");
            return new ScopedLoweringResult(Optional.empty(), state.issues);
        }
        return new ScopedLoweringResult(Optional.of(new ProgramGraph(state.nodes, state.edges,
                ((NodeValue) value.orElseThrow()).nodeId(), source.budgetLimit())), state.issues);
    }

    private Optional<Value> lower(ScopedExpression expression, List<Value> environment, String path, State state) {
        if (expression instanceof ScopedExpression.ParameterReference reference) {
            return reference.deBruijnIndex() < environment.size()
                    ? Optional.of(environment.get(reference.deBruijnIndex())) : Optional.empty();
        }
        if (expression instanceof ScopedExpression.Literal literal) {
            if (literal.type().type() != RuneType.NUMBER) {
                state.issue(ScopedLanguageIssue.Code.TYPE_MISMATCH, path, "Only number literals can currently lower to a rune graph");
                return Optional.empty();
            }
            try {
                double value = Double.parseDouble(literal.encodedValue());
                if (!Double.isFinite(value)) throw new NumberFormatException();
                return Optional.of(state.node("mathmod:constant_number", Map.of("value", Double.toString(value))));
            } catch (NumberFormatException exception) {
                state.issue(ScopedLanguageIssue.Code.TYPE_MISMATCH, path, "Number literal must be finite");
                return Optional.empty();
            }
        }
        if (expression instanceof ScopedExpression.Lambda lambda) return Optional.of(new Closure(lambda.body(), environment));
        if (expression instanceof ScopedExpression.Let let) {
            Optional<Value> value = lower(let.value(), environment, path + ".value", state);
            return value.flatMap(bound -> lower(let.body(), bind(environment, bound), path + ".body", state));
        }
        if (expression instanceof ScopedExpression.Application application) {
            Optional<Value> function = lower(application.function(), environment, path + ".function", state);
            Optional<Value> argument = lower(application.argument(), environment, path + ".argument", state);
            if (function.filter(Closure.class::isInstance).isEmpty() || argument.isEmpty()) return Optional.empty();
            Closure closure = (Closure) function.orElseThrow();
            return lower(closure.body(), bind(closure.environment(), argument.get()), path + ".beta", state);
        }
        ScopedExpression.RuneCall call = (ScopedExpression.RuneCall) expression;
        Map<String, NodeValue> inputs = new LinkedHashMap<>();
        for (ScopedExpression.Argument argument : call.arguments()) {
            Optional<Value> lowered = lower(argument.expression(), environment, path + "." + argument.inputName(), state);
            if (lowered.filter(NodeValue.class::isInstance).isEmpty()) return Optional.empty();
            inputs.put(argument.inputName(), (NodeValue) lowered.orElseThrow());
        }
        NodeValue target = state.node(call.runeId(), Map.of());
        inputs.forEach((input, value) -> state.edges.add(new ProgramEdge(value.nodeId(), target.nodeId(), input)));
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
    private record Closure(ScopedExpression body, List<Value> environment) implements Value {
        private Closure { environment = List.copyOf(environment); }
    }

    private static final class State {
        private final List<ProgramNode> nodes = new ArrayList<>();
        private final List<ProgramEdge> edges = new ArrayList<>();
        private final List<ScopedLanguageIssue> issues = new ArrayList<>();
        private int nextNode;

        private NodeValue node(String runeId, Map<String, String> constants) {
            String id = "f" + nextNode++;
            nodes.add(new ProgramNode(id, runeId, constants));
            return new NodeValue(id);
        }

        private void issue(ScopedLanguageIssue.Code code, String path, String message) {
            issues.add(new ScopedLanguageIssue(code, path, message));
        }
    }
}
