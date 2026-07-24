package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ManuscriptReaderViewCodec {
    private ManuscriptReaderViewCodec() { }

    public static void write(RegistryFriendlyByteBuf buffer, ManuscriptReaderView view) {
        writeId(buffer, view.requestedId());
        writeOptionalId(buffer, view.canonicalId());
        buffer.writeEnum(view.status());
        writeText(buffer, view.titleTranslationKey());
        writeText(buffer, view.traditionNameTranslationKey());
        writeText(buffer, view.traditionSummaryTranslationKey());
        buffer.writeEnum(view.rarity());
        buffer.writeVarInt(view.pageTranslationKeys().size());
        view.pageTranslationKeys().forEach(key -> writeText(buffer, key));
        writeOptionalId(buffer, view.patchouliEntry());
        writeOptionalId(buffer, view.theoremId());
    }

    public static ManuscriptReaderView read(RegistryFriendlyByteBuf buffer) {
        NamespacedId requested = readId(buffer);
        Optional<NamespacedId> canonical = readOptionalId(buffer);
        ManuscriptReaderView.Status status = buffer.readEnum(ManuscriptReaderView.Status.class);
        String title = readText(buffer);
        String tradition = readText(buffer);
        String summary = readText(buffer);
        ManuscriptRarity rarity = buffer.readEnum(ManuscriptRarity.class);
        int pages = buffer.readVarInt();
        if (pages < 0 || pages > ManuscriptDefinition.MAX_PAGES) throw new IllegalArgumentException("Invalid manuscript page count");
        List<String> pageKeys = new ArrayList<>(pages);
        for (int index = 0; index < pages; index++) pageKeys.add(readText(buffer));
        return new ManuscriptReaderView(requested, canonical, status, title, tradition, summary,
                rarity, pageKeys, readOptionalId(buffer), readOptionalId(buffer));
    }

    private static void writeOptionalId(RegistryFriendlyByteBuf buffer, Optional<NamespacedId> value) {
        buffer.writeBoolean(value.isPresent());
        value.ifPresent(id -> writeId(buffer, id));
    }
    private static Optional<NamespacedId> readOptionalId(RegistryFriendlyByteBuf buffer) {
        return buffer.readBoolean() ? Optional.of(readId(buffer)) : Optional.empty();
    }
    private static void writeId(RegistryFriendlyByteBuf buffer, NamespacedId id) { writeText(buffer, id.toString()); }
    private static NamespacedId readId(RegistryFriendlyByteBuf buffer) { return NamespacedId.parse(readText(buffer)); }
    private static void writeText(RegistryFriendlyByteBuf buffer, String value) { buffer.writeUtf(value, ManuscriptReaderView.MAX_TEXT); }
    private static String readText(RegistryFriendlyByteBuf buffer) { return buffer.readUtf(ManuscriptReaderView.MAX_TEXT); }
}
