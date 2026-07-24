package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public final class FieldLedgerViewCodec {
    private static final int MAX_TEXT = 160;

    private FieldLedgerViewCodec() {
    }

    public static void write(RegistryFriendlyByteBuf buffer, FieldLedgerView view) {
        writeEntries(buffer, view.epiphanies());
        writeEntries(buffer, view.discoveries());
    }

    public static FieldLedgerView read(RegistryFriendlyByteBuf buffer) {
        return new FieldLedgerView(readEntries(buffer), readEntries(buffer));
    }

    private static void writeEntries(
            RegistryFriendlyByteBuf buffer,
            List<FieldLedgerView.Entry> entries
    ) {
        buffer.writeVarInt(entries.size());
        entries.forEach(entry -> {
            buffer.writeEnum(entry.kind());
            buffer.writeUtf(entry.id().toString(), MAX_TEXT);
            buffer.writeUtf(entry.titleTranslationKey(), MAX_TEXT);
            buffer.writeUtf(entry.routeTranslationKey(), MAX_TEXT);
            buffer.writeBoolean(entry.complete());
            buffer.writeVarInt(entry.studies().size());
            entry.studies().forEach(study -> {
                buffer.writeUtf(study.materialId().toString(), MAX_TEXT);
                buffer.writeVarInt(study.progress());
                buffer.writeVarInt(study.required());
            });
            buffer.writeVarInt(entry.grants().size());
            entry.grants().forEach(grant -> {
                buffer.writeEnum(grant.kind());
                buffer.writeUtf(grant.id().toString(), MAX_TEXT);
            });
        });
    }

    private static List<FieldLedgerView.Entry> readEntries(RegistryFriendlyByteBuf buffer) {
        int count = readCount(buffer, FieldLedgerView.maximumEntries(), "ledger entries");
        List<FieldLedgerView.Entry> entries = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            KnowledgeKind kind = buffer.readEnum(KnowledgeKind.class);
            NamespacedId id = NamespacedId.parse(buffer.readUtf(MAX_TEXT));
            String titleKey = buffer.readUtf(MAX_TEXT);
            String routeKey = buffer.readUtf(MAX_TEXT);
            boolean complete = buffer.readBoolean();
            int studyCount = readCount(
                    buffer,
                    FieldLedgerView.maximumStudies(),
                    "ledger studies"
            );
            List<FieldLedgerView.Study> studies = new ArrayList<>(studyCount);
            for (int study = 0; study < studyCount; study++) {
                studies.add(new FieldLedgerView.Study(
                        NamespacedId.parse(buffer.readUtf(MAX_TEXT)),
                        buffer.readVarInt(),
                        buffer.readVarInt()
                ));
            }
            int grantCount = readCount(
                    buffer,
                    FieldLedgerView.maximumGrants(),
                    "ledger grants"
            );
            List<KnowledgeGrant> grants = new ArrayList<>(grantCount);
            for (int grant = 0; grant < grantCount; grant++) {
                grants.add(new KnowledgeGrant(
                        buffer.readEnum(KnowledgeKind.class),
                        NamespacedId.parse(buffer.readUtf(MAX_TEXT))
                ));
            }
            entries.add(new FieldLedgerView.Entry(
                    kind,
                    id,
                    titleKey,
                    routeKey,
                    complete,
                    studies,
                    grants
            ));
        }
        return List.copyOf(entries);
    }

    private static int readCount(RegistryFriendlyByteBuf buffer, int maximum, String label) {
        int count = buffer.readVarInt();
        if (count < 0 || count > maximum) {
            throw new IllegalArgumentException("Invalid " + label + " count " + count);
        }
        return count;
    }
}
