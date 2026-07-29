package com.mathmod.language;

import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RunePurity;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/** Trusted, pure literal boundary. L0 deliberately supports NUMBER only. */
public final class ScopedLiteralResolver {
    private static final Pattern DECIMAL = Pattern.compile("[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?");

    private final RuneRegistry registry;

    public ScopedLiteralResolver(RuneRegistry registry) {
        this.registry = registry;
    }

    public Resolution resolve(ScopedExpression.Literal literal, ScopedCompileBudget budget) {
        budget.charge(ScopedCompileBudget.Event.LITERAL_RESOLUTION);
        if (literal.type().type() != RuneType.NUMBER) {
            return Resolution.failure(ScopedLanguageIssue.Code.LITERAL_UNSUPPORTED, "Only NUMBER literals are supported by L0");
        }
        String encoded = literal.encodedValue();
        if (encoded.length() > ScopedLanguageLimits.MAX_LITERAL_LENGTH || !DECIMAL.matcher(encoded).matches()) {
            return Resolution.failure(ScopedLanguageIssue.Code.LITERAL_INVALID, "NUMBER literal must use bounded decimal syntax");
        }
        try {
            double value = new BigDecimal(encoded).doubleValue();
            if (!Double.isFinite(value)) {
                return Resolution.failure(ScopedLanguageIssue.Code.LITERAL_INVALID, "NUMBER literal must be finite");
            }
            String canonical = Double.toString(value == 0.0D ? 0.0D : value);
            Optional<RuneDefinition> constant = registry.find("mathmod:constant_number");
            if (constant.isEmpty() || !constant.orElseThrow().enabled()
                    || constant.orElseThrow().outputType() != RuneType.NUMBER
                    || !constant.orElseThrow().inputs().isEmpty()
                    || constant.orElseThrow().purity() != RunePurity.PURE
                    || !constant.orElseThrow().executorKey().equals("constant_number")) {
                return Resolution.failure(ScopedLanguageIssue.Code.LOWERED_GRAPH_INVALID,
                        "NUMBER literal constant rune is unavailable or incompatible");
            }
            return Resolution.success("mathmod:constant_number", Map.of("value", canonical));
        } catch (NumberFormatException exception) {
            return Resolution.failure(ScopedLanguageIssue.Code.LITERAL_INVALID, "NUMBER literal must be finite");
        }
    }

    public record Resolution(Optional<LoweredLiteral> literal, Optional<Failure> failure) {
        public static Resolution success(String runeId, Map<String, String> constants) {
            return new Resolution(Optional.of(new LoweredLiteral(runeId, Map.copyOf(constants))), Optional.empty());
        }
        public static Resolution failure(ScopedLanguageIssue.Code code, String message) {
            return new Resolution(Optional.empty(), Optional.of(new Failure(code, message)));
        }
    }
    public record LoweredLiteral(String runeId, Map<String, String> constants) { }
    public record Failure(ScopedLanguageIssue.Code code, String message) { }
}
