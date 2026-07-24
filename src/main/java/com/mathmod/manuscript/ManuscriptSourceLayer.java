package com.mathmod.manuscript;

public enum ManuscriptSourceLayer {
    BUILT_IN(0),
    KUBEJS(1),
    DATA_PACK(2);

    private final int precedence;

    ManuscriptSourceLayer(int precedence) {
        this.precedence = precedence;
    }

    int precedence() {
        return precedence;
    }
}
