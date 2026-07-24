package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiPreviewHoverPolicyTest {
    @Test
    void ordinaryPreviewSuppressesIncidentalContextualHover() {
        assertTrue(UiPreviewHoverPolicy.suppressesContextualHover("resources"));
        assertTrue(UiPreviewHoverPolicy.suppressesContextualHover(" resources-add-remove "));
        assertTrue(UiPreviewHoverPolicy.suppressesContextualHover("minimum-viewport"));
    }

    @Test
    void tooltipPreviewKeepsContextualHover() {
        assertFalse(UiPreviewHoverPolicy.suppressesContextualHover("resources-material-tooltip"));
        assertFalse(UiPreviewHoverPolicy.suppressesContextualHover("theorem-node-tooltip"));
    }

    @Test
    void normalGameplayNeverSuppressesHover() {
        assertFalse(UiPreviewHoverPolicy.suppressesContextualHover(""));
        assertFalse(UiPreviewHoverPolicy.suppressesContextualHover("  "));
        assertFalse(UiPreviewHoverPolicy.suppressesContextualHover(null));
    }
}
