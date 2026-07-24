package com.mathmod.client.screen;

import com.mathmod.kubejs.RuneMaterialDefinition;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

final class MaterialCatalogOrder {
    private MaterialCatalogOrder() {
    }

    static List<RuneMaterialDefinition> localized(
            List<RuneMaterialDefinition> materials,
            Function<RuneMaterialDefinition, String> displayName,
            Locale locale
    ) {
        Collator collator = Collator.getInstance(locale);
        List<RuneMaterialDefinition> ordered = new ArrayList<>(materials);
        ordered.sort((first, second) -> {
            int byName = collator.compare(displayName.apply(first), displayName.apply(second));
            return byName != 0 ? byName : first.id().compareTo(second.id());
        });
        return List.copyOf(ordered);
    }

    static int canonicalIndex(
            List<RuneMaterialDefinition> canonical,
            RuneMaterialDefinition selected
    ) {
        for (int index = 0; index < canonical.size(); index++) {
            if (canonical.get(index).id().equals(selected.id())) {
                return index;
            }
        }
        return -1;
    }
}
