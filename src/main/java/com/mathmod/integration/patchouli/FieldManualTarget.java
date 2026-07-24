package com.mathmod.integration.patchouli;

final class FieldManualTarget {
    static final String NAMESPACE = "mathmod";
    static final String BOOK_PATH = "field_manual";
    static final String FIRST_SPELL_ENTRY_PATH = "basics/can_i_make_spell";
    static final int FIRST_SPELL_PAGE = 0;
    static final String RESOURCE_COSTS_ENTRY_PATH = "programming/resource_costs";
    static final int RESOURCE_COSTS_PAGE = 1;
    static final String ROTATED_HORIZON_ENTRY_PATH = "lore/rotated_horizon";
    static final int ROTATED_HORIZON_PAGE = 0;

    private FieldManualTarget() {
    }

    static String bookId() {
        return NAMESPACE + ":" + BOOK_PATH;
    }
}
