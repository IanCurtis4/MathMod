package com.mathmod.field;

/** Small closed quantity vocabulary for P5 field signatures. */
public enum FieldQuantity {
    SCALAR("1"),
    BLOCK("block"),
    TICK("tick"),
    BLOCK_PER_TICK("block/tick"),
    SIGNAL("signal"),
    COUNT_PER_BLOCK("count/block^3"),
    CORRESPONDENCE("correspondence");

    private final String symbol;

    FieldQuantity(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
