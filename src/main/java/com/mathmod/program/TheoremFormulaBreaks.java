package com.mathmod.program;

public final class TheoremFormulaBreaks {
    private TheoremFormulaBreaks() {
    }

    public static int outerArgumentSeparator(String formula) {
        int depth = 0;
        for (int index = 0; index < formula.length(); index++) {
            char symbol = formula.charAt(index);
            if (symbol == '(') {
                depth++;
            } else if (symbol == ')') {
                depth = Math.max(0, depth - 1);
            } else if (symbol == ',' && depth == 1) {
                return index;
            }
        }
        return -1;
    }
}
