package com.mathmod.language;

import java.util.ArrayList;
import java.util.List;

/** Pure reference operations for the reviewed De Bruijn reduction semantics. */
public final class ScopedDeBruijn {
    private ScopedDeBruijn() {
    }

    public static ScopedExpression shift(ScopedExpression expression, int delta) {
        return shift(expression, delta, 0);
    }

    public static ScopedExpression shift(ScopedExpression expression, int delta, int cutoff) {
        if (cutoff < 0) {
            throw new IllegalArgumentException("cutoff must not be negative");
        }
        if (expression instanceof ScopedExpression.ParameterReference parameter) {
            int index = parameter.deBruijnIndex();
            if (index < cutoff) {
                return parameter;
            }
            int shifted = index + delta;
            if (shifted < 0) {
                throw new IllegalArgumentException("Shift would create a negative parameter index");
            }
            return new ScopedExpression.ParameterReference(shifted);
        }
        if (expression instanceof ScopedExpression.Literal literal) {
            return literal;
        }
        if (expression instanceof ScopedExpression.RuneCall call) {
            return new ScopedExpression.RuneCall(call.runeId(), shiftArguments(call.arguments(), delta, cutoff));
        }
        if (expression instanceof ScopedExpression.Lambda lambda) {
            return new ScopedExpression.Lambda(
                    lambda.nameHint(),
                    lambda.parameterType(),
                    shift(lambda.body(), delta, cutoff + 1)
            );
        }
        if (expression instanceof ScopedExpression.Application application) {
            return new ScopedExpression.Application(
                    shift(application.function(), delta, cutoff),
                    shift(application.argument(), delta, cutoff)
            );
        }
        ScopedExpression.Let let = (ScopedExpression.Let) expression;
        return new ScopedExpression.Let(
                let.nameHint(),
                shift(let.value(), delta, cutoff),
                shift(let.body(), delta, cutoff + 1)
        );
    }

    /** Standard capture-safe substitution of the nearest binder in body. */
    public static ScopedExpression substituteTop(
            ScopedExpression replacement,
            ScopedExpression body
    ) {
        return shift(substitute(body, 0, shift(replacement, 1), 0), -1);
    }

    /**
     * The sharing-preserving beta step used by the future compiler. It keeps
     * the argument once as a let binding instead of copying its syntax.
     */
    public static ScopedExpression betaToLet(ScopedExpression.Application application) {
        if (!(application.function() instanceof ScopedExpression.Lambda lambda)) {
            throw new IllegalArgumentException("Beta reduction requires a lambda function");
        }
        return new ScopedExpression.Let(
                lambda.nameHint(),
                application.argument(),
                lambda.body()
        );
    }

    public static boolean alphaEquivalent(ScopedExpression left, ScopedExpression right) {
        if (left instanceof ScopedExpression.Literal leftLiteral
                && right instanceof ScopedExpression.Literal rightLiteral) {
            return leftLiteral.type().equals(rightLiteral.type())
                    && leftLiteral.encodedValue().equals(rightLiteral.encodedValue());
        }
        if (left instanceof ScopedExpression.ParameterReference leftParameter
                && right instanceof ScopedExpression.ParameterReference rightParameter) {
            return leftParameter.deBruijnIndex() == rightParameter.deBruijnIndex();
        }
        if (left instanceof ScopedExpression.RuneCall leftCall
                && right instanceof ScopedExpression.RuneCall rightCall) {
            if (!leftCall.runeId().equals(rightCall.runeId())
                    || leftCall.arguments().size() != rightCall.arguments().size()) {
                return false;
            }
            for (int index = 0; index < leftCall.arguments().size(); index++) {
                ScopedExpression.Argument leftArgument = leftCall.arguments().get(index);
                ScopedExpression.Argument rightArgument = rightCall.arguments().get(index);
                if (!leftArgument.inputName().equals(rightArgument.inputName())
                        || !alphaEquivalent(leftArgument.expression(), rightArgument.expression())) {
                    return false;
                }
            }
            return true;
        }
        if (left instanceof ScopedExpression.Lambda leftLambda
                && right instanceof ScopedExpression.Lambda rightLambda) {
            return leftLambda.parameterType().equals(rightLambda.parameterType())
                    && alphaEquivalent(leftLambda.body(), rightLambda.body());
        }
        if (left instanceof ScopedExpression.Application leftApplication
                && right instanceof ScopedExpression.Application rightApplication) {
            return alphaEquivalent(leftApplication.function(), rightApplication.function())
                    && alphaEquivalent(leftApplication.argument(), rightApplication.argument());
        }
        if (left instanceof ScopedExpression.Let leftLet && right instanceof ScopedExpression.Let rightLet) {
            return alphaEquivalent(leftLet.value(), rightLet.value())
                    && alphaEquivalent(leftLet.body(), rightLet.body());
        }
        return false;
    }

    private static ScopedExpression substitute(
            ScopedExpression expression,
            int target,
            ScopedExpression replacement,
            int cutoff
    ) {
        if (expression instanceof ScopedExpression.ParameterReference parameter) {
            return parameter.deBruijnIndex() == target + cutoff
                    ? shift(replacement, cutoff)
                    : parameter;
        }
        if (expression instanceof ScopedExpression.Literal literal) {
            return literal;
        }
        if (expression instanceof ScopedExpression.RuneCall call) {
            List<ScopedExpression.Argument> arguments = new ArrayList<>(call.arguments().size());
            for (ScopedExpression.Argument argument : call.arguments()) {
                arguments.add(new ScopedExpression.Argument(
                        argument.inputName(),
                        substitute(argument.expression(), target, replacement, cutoff)
                ));
            }
            return new ScopedExpression.RuneCall(call.runeId(), arguments);
        }
        if (expression instanceof ScopedExpression.Lambda lambda) {
            return new ScopedExpression.Lambda(
                    lambda.nameHint(),
                    lambda.parameterType(),
                    substitute(lambda.body(), target, replacement, cutoff + 1)
            );
        }
        if (expression instanceof ScopedExpression.Application application) {
            return new ScopedExpression.Application(
                    substitute(application.function(), target, replacement, cutoff),
                    substitute(application.argument(), target, replacement, cutoff)
            );
        }
        ScopedExpression.Let let = (ScopedExpression.Let) expression;
        return new ScopedExpression.Let(
                let.nameHint(),
                substitute(let.value(), target, replacement, cutoff),
                substitute(let.body(), target, replacement, cutoff + 1)
        );
    }

    private static List<ScopedExpression.Argument> shiftArguments(
            List<ScopedExpression.Argument> arguments,
            int delta,
            int cutoff
    ) {
        return arguments.stream()
                .map(argument -> new ScopedExpression.Argument(
                        argument.inputName(),
                        shift(argument.expression(), delta, cutoff)
                ))
                .toList();
    }
}
