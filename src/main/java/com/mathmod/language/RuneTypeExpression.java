package com.mathmod.language;

import com.mathmod.runes.RuneType;

import java.util.Objects;

public sealed interface RuneTypeExpression
        permits RuneTypeExpression.ValueType, RuneTypeExpression.FunctionType {
    int nestingDepth();

    record ValueType(RuneType type) implements RuneTypeExpression {
        public ValueType {
            type = Objects.requireNonNull(type, "type");
        }

        @Override
        public int nestingDepth() {
            return 0;
        }
    }

    record FunctionType(
            RuneTypeExpression parameterType,
            RuneTypeExpression resultType
    ) implements RuneTypeExpression {
        public FunctionType {
            parameterType = Objects.requireNonNull(parameterType, "parameterType");
            resultType = Objects.requireNonNull(resultType, "resultType");
        }

        @Override
        public int nestingDepth() {
            return 1 + Math.max(parameterType.nestingDepth(), resultType.nestingDepth());
        }
    }

    static ValueType value(RuneType type) {
        return new ValueType(type);
    }

    static FunctionType function(
            RuneTypeExpression parameterType,
            RuneTypeExpression resultType
    ) {
        return new FunctionType(parameterType, resultType);
    }
}
