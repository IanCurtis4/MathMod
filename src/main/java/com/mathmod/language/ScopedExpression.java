package com.mathmod.language;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public sealed interface ScopedExpression permits
        ScopedExpression.Literal,
        ScopedExpression.ParameterReference,
        ScopedExpression.RuneCall,
        ScopedExpression.Lambda,
        ScopedExpression.Application,
        ScopedExpression.Let {

    record Literal(RuneTypeExpression.ValueType type, String encodedValue)
            implements ScopedExpression {
        public Literal {
            type = Objects.requireNonNull(type, "type");
            if (encodedValue == null || encodedValue.isBlank()) {
                throw new IllegalArgumentException("encodedValue must not be blank");
            }
            encodedValue = encodedValue.trim();
        }
    }

    /** Index zero names the nearest enclosing lambda or let binder. */
    record ParameterReference(int deBruijnIndex) implements ScopedExpression {
        public ParameterReference {
            if (deBruijnIndex < 0) {
                throw new IllegalArgumentException("deBruijnIndex must not be negative");
            }
        }
    }

    record RuneCall(String runeId, List<Argument> arguments) implements ScopedExpression {
        public RuneCall {
            if (runeId == null || runeId.isBlank()) {
                throw new IllegalArgumentException("runeId must not be blank");
            }
            runeId = runeId.trim();
            arguments = List.copyOf(Objects.requireNonNull(arguments, "arguments"));
            Set<String> names = new HashSet<>();
            for (Argument argument : arguments) {
                if (!names.add(argument.inputName())) {
                    throw new IllegalArgumentException(
                            "Duplicate rune input argument " + argument.inputName()
                    );
                }
            }
        }
    }

    record Lambda(
            String nameHint,
            RuneTypeExpression parameterType,
            ScopedExpression body
    ) implements ScopedExpression {
        public Lambda {
            nameHint = boundedName(nameHint);
            parameterType = Objects.requireNonNull(parameterType, "parameterType");
            body = Objects.requireNonNull(body, "body");
        }
    }

    record Application(ScopedExpression function, ScopedExpression argument)
            implements ScopedExpression {
        public Application {
            function = Objects.requireNonNull(function, "function");
            argument = Objects.requireNonNull(argument, "argument");
        }
    }

    /** The binder is in scope only in body, never in value. */
    record Let(String nameHint, ScopedExpression value, ScopedExpression body)
            implements ScopedExpression {
        public Let {
            nameHint = boundedName(nameHint);
            value = Objects.requireNonNull(value, "value");
            body = Objects.requireNonNull(body, "body");
        }
    }

    record Argument(String inputName, ScopedExpression expression) {
        public Argument {
            if (inputName == null || inputName.isBlank()) {
                throw new IllegalArgumentException("inputName must not be blank");
            }
            inputName = inputName.trim();
            expression = Objects.requireNonNull(expression, "expression");
        }
    }

    private static String boundedName(String value) {
        if (value == null || value.isBlank()) {
            return "x";
        }
        String trimmed = value.trim();
        if (trimmed.length() > 32) {
            throw new IllegalArgumentException("Binder name hint exceeds 32 characters");
        }
        return trimmed;
    }
}
