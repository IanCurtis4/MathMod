package com.mathmod.field;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldProviderRegistryTest {
    @Test
    void publishesCompleteImmutableSnapshots() {
        FieldProviderDefinition first = provider("first", FieldValueKind.SCALAR);
        FieldProviderDefinition second = provider("second", FieldValueKind.VECTOR);
        FieldProviderRegistry registry = new FieldProviderRegistry();

        registry.publish(FieldProviderSnapshot.of(List.of(second, first)));

        assertEquals(List.of(first, second), registry.snapshot().definitions());
        assertThrows(UnsupportedOperationException.class,
                () -> registry.snapshot().definitions().add(first));
    }

    @Test
    void duplicateIdsNeverProduceAnAmbiguousSnapshot() {
        assertThrows(IllegalArgumentException.class, () -> FieldProviderSnapshot.of(List.of(
                provider("same", FieldValueKind.SCALAR),
                provider("same", FieldValueKind.VECTOR)
        )));
    }

    @Test
    void definitionsAndPrivateSamplersPublishAsOneCoherentSnapshot() {
        FieldProviderDefinition definition = provider("field", FieldValueKind.SCALAR);
        FieldProviderPublication publication = new FieldProviderPublication(
                FieldProviderSnapshot.of(List.of(definition)),
                new FieldProviderRuntimeRegistry(Map.of(
                        definition.id(), context -> ignored -> new FieldSampleValue.Scalar(1)
                ))
        );
        FieldProviderPublicationStore store = new FieldProviderPublicationStore();
        store.publish(publication);

        assertEquals(definition, store.snapshot().definitions().find(definition.id()).orElseThrow());
        assertEquals(1, store.snapshot().runtime().ids().size());
        assertThrows(IllegalArgumentException.class, () -> new FieldProviderPublication(
                FieldProviderSnapshot.of(List.of(definition)),
                new FieldProviderRuntimeRegistry(Map.of())
        ));
    }

    static FieldProviderDefinition provider(String path, FieldValueKind kind) {
        return new FieldProviderDefinition(
                NamespacedId.of("test", path), kind, FieldQuantity.SIGNAL, 8.0D, 2
        );
    }
}
