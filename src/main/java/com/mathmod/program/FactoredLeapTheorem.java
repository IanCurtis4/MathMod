package com.mathmod.program;

import com.mathmod.language.RuneTypeExpression;
import com.mathmod.language.ScopedExpression;
import com.mathmod.language.ScopedProgramSource;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneType;

import java.util.List;
import java.util.Map;

/** Frozen built-in source fixture; the lowered graph remains the executable authority. */
final class FactoredLeapTheorem {
    static final String ID = "mathmod:factored_leap";
    static final int BUDGET_LIMIT = 24;
    private FactoredLeapTheorem() { }

    static ScopedProgramSource source() {
        ScopedExpression halve = new ScopedExpression.Lambda("vector", RuneTypeExpression.value(RuneType.VEC3),
                call("mathmod:scale_vector", arg("vector", ref(0)), arg("factor", number("0.5"))));
        ScopedExpression self = call("mathmod:self_player");
        ScopedExpression forward = new ScopedExpression.Application(ref(1), call("mathmod:look_vector", arg("player", ref(0))));
        ScopedExpression liftVector = call("mathmod:vector_from_numbers",
                arg("x", number("0")), arg("y", number("1")), arg("z", number("0")));
        ScopedExpression lift = new ScopedExpression.Application(ref(2), liftVector);
        ScopedExpression result = call("mathmod:push_self", arg("player", ref(2)),
                arg("vector", call("mathmod:vector_add", arg("a", ref(1)), arg("b", ref(0)))));
        return new ScopedProgramSource(ScopedProgramSource.CURRENT_VERSION,
                new ScopedExpression.Let("halve", halve,
                        new ScopedExpression.Let("self", self,
                                new ScopedExpression.Let("forward", forward,
                                        new ScopedExpression.Let("lift", lift, result)))),
                RuneTypeExpression.value(RuneType.UNIT), BUDGET_LIMIT);
    }

    static ProgramGraph presentationGraph() {
        return new ProgramGraph(List.of(
                node("self", "mathmod:self_player"), node("look", "mathmod:look_vector"),
                node("halfLook", "mathmod:constant_number", "0.5"), node("scaledLook", "mathmod:scale_vector"),
                node("x", "mathmod:constant_number", "0"), node("y", "mathmod:constant_number", "1"),
                node("z", "mathmod:constant_number", "0"), node("up", "mathmod:vector_from_numbers"),
                node("halfUp", "mathmod:constant_number", "0.5"), node("scaledUp", "mathmod:scale_vector"),
                node("sum", "mathmod:vector_add"), node("push", "mathmod:push_self")),
                List.of(edge("self", "look", "player"), edge("look", "scaledLook", "vector"), edge("halfLook", "scaledLook", "factor"),
                        edge("x", "up", "x"), edge("y", "up", "y"), edge("z", "up", "z"), edge("up", "scaledUp", "vector"),
                        edge("halfUp", "scaledUp", "factor"), edge("scaledLook", "sum", "a"), edge("scaledUp", "sum", "b"),
                        edge("self", "push", "player"), edge("sum", "push", "vector")), "push", BUDGET_LIMIT);
    }

    private static ScopedExpression.ParameterReference ref(int index) { return new ScopedExpression.ParameterReference(index); }
    private static ScopedExpression.Literal number(String value) { return new ScopedExpression.Literal(RuneTypeExpression.value(RuneType.NUMBER), value); }
    private static ScopedExpression.Argument arg(String name, ScopedExpression expression) { return new ScopedExpression.Argument(name, expression); }
    private static ScopedExpression.RuneCall call(String id, ScopedExpression.Argument... arguments) { return new ScopedExpression.RuneCall(id, List.of(arguments)); }
    private static ProgramNode node(String id, String rune) { return new ProgramNode(id, rune); }
    private static ProgramNode node(String id, String rune, String value) { return new ProgramNode(id, rune, Map.of("value", value)); }
    private static ProgramEdge edge(String from, String to, String input) { return new ProgramEdge(from, to, input); }
}
