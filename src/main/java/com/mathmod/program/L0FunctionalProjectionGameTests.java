package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.knowledge.KnowledgeDefinitionSnapshot;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.screen.RuneProgrammerMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.nio.charset.StandardCharsets;

/** Runtime-only proof for the bounded menu projection transport. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class L0FunctionalProjectionGameTests {
    private L0FunctionalProjectionGameTests() { }

    @GameTest(template = "empty")
    public static void projectionMenuCodecRoundTripAndBounds(GameTestHelper helper) {
        ScopedFunctionalProjection expected = projection();
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        ScopedFunctionalProjectionWireCodec.write(buffer, expected);
        helper.assertTrue(ScopedFunctionalProjectionWireCodec.read(buffer).equals(expected), "menu buffer must round-trip exact projection");
        ScopedFunctionalProjection atLimit = projectionEncodedAtLimit();
        helper.assertTrue(ScopedFunctionalProjectionWireCodec.encode(atLimit).length == ScopedFunctionalProjectionWireCodec.MAX_BYTES,
                "a valid projection frame at exactly 65,536 bytes must be accepted");
        RegistryFriendlyByteBuf limitBuffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        ScopedFunctionalProjectionWireCodec.write(limitBuffer, atLimit);
        helper.assertTrue(ScopedFunctionalProjectionWireCodec.read(limitBuffer).equals(atLimit),
                "the exact maximum projection frame must round-trip through the real menu buffer");
        RegistryFriendlyByteBuf overflowBuffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        ScopedFunctionalProjectionWireCodec.writeFailClosed(overflowBuffer, projectionEncodingOverflow());
        ScopedFunctionalProjection fallback = ScopedFunctionalProjectionWireCodec.read(overflowBuffer);
        helper.assertTrue(fallback.sourceState() == ScopedFunctionalProjection.SourceState.STALE
                        && fallback.authoredRows().isEmpty() && fallback.checkedRows().isEmpty()
                        && !overflowBuffer.isReadable(),
                "overflow must emit exactly one minimal stale frame without partial or trailing bytes");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void projectionMalformedFramesFailClosed(GameTestHelper helper) {
        RegistryFriendlyByteBuf oversized = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        oversized.writeVarInt(ScopedFunctionalProjectionWireCodec.MAX_BYTES + 1);
        helper.assertTrue(rejected(() -> ScopedFunctionalProjectionWireCodec.read(oversized)), "oversized frame must reject before allocation");
        RegistryFriendlyByteBuf truncated = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        truncated.writeVarInt(1);
        helper.assertTrue(rejected(() -> ScopedFunctionalProjectionWireCodec.read(truncated)), "truncated frame must reject");
        RegistryFriendlyByteBuf trailing = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        byte[] frame = ScopedFunctionalProjectionWireCodec.encode(projection());
        trailing.writeVarInt(frame.length + 1);
        trailing.writeBytes(frame);
        trailing.writeByte(0);
        helper.assertTrue(rejected(() -> ScopedFunctionalProjectionWireCodec.read(trailing)), "trailing frame byte must reject");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void projectionReadCompileMatrixMutatesNothing(GameTestHelper helper) {
        ItemStack talisman = new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
        assertProjection(helper, talisman, ScopedFunctionalProjection.SourceState.ABSENT, ScopedFunctionalProjection.AttemptState.NOT_RUN);
        talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(), new ScopedSourceEnvelope(1, sourceBytes()));
        assertProjection(helper, talisman, ScopedFunctionalProjection.SourceState.CURRENT_VALID, ScopedFunctionalProjection.AttemptState.ADMISSION_REJECTED);
        talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(), new ScopedSourceEnvelope(1, new byte[]{(byte) 0xC3, (byte) 0x28}));
        assertProjection(helper, talisman, ScopedFunctionalProjection.SourceState.CURRENT_UNREADABLE, ScopedFunctionalProjection.AttemptState.NOT_RUN);
        talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(), new ScopedSourceEnvelope(2, new byte[]{1}));
        assertProjection(helper, talisman, ScopedFunctionalProjection.SourceState.UNSUPPORTED_VERSION, ScopedFunctionalProjection.AttemptState.NOT_RUN);
        talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(), GuidedWorkspaceState.create("conflict", List.of()));
        assertProjection(helper, talisman, ScopedFunctionalProjection.SourceState.CONFLICT, ScopedFunctionalProjection.AttemptState.NOT_RUN);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void projectionAuthorityRechecksBecomeStale(GameTestHelper helper) {
        ItemStack talisman = new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
        MathModRuneBootstrap.bootstrap();
        ScopedFunctionalProjectionService.AuthoritySnapshot captured = new ScopedFunctionalProjectionService.AuthoritySnapshot(
                PlayerKnowledge.empty(), MathModRuneBootstrap.registry().generation(), KnowledgeDefinitions.snapshot(), ProgramResources.materials());
        assertStale(helper, talisman, captured, new ScopedFunctionalProjectionService.AuthoritySnapshot(
                PlayerKnowledge.empty().withSchemaVersion(4), captured.runeGeneration(), captured.definitions(), captured.materials()), "knowledge");
        assertStale(helper, talisman, captured, new ScopedFunctionalProjectionService.AuthoritySnapshot(
                captured.knowledge(), captured.runeGeneration() + 1, captured.definitions(), captured.materials()), "rune generation");
        assertStale(helper, talisman, captured, new ScopedFunctionalProjectionService.AuthoritySnapshot(
                captured.knowledge(), captured.runeGeneration(), new KnowledgeDefinitionSnapshot(List.of(), List.of()), captured.materials()), "knowledge definitions");
        var changedMaterials = new java.util.ArrayList<>(captured.materials());
        changedMaterials.add(new RuneMaterialDefinition("mathmod:gametest_recheck", "minecraft:stick", 0, 0));
        assertStale(helper, talisman, captured, new ScopedFunctionalProjectionService.AuthoritySnapshot(
                captured.knowledge(), captured.runeGeneration(), captured.definitions(), changedMaterials), "materials");
        ScopedFunctionalProjection staleTarget = ScopedFunctionalProjectionService.acceptCandidate(talisman, talisman.copy(), captured,
                projection(), true, new ItemStack(Items.STICK), captured);
        assertStale(helper, staleTarget, "held target");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void projectionMenuBindingInvalidatesAfterTargetChange(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemStack captured = new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
        captured.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(), new ScopedSourceEnvelope(1, sourceBytes()));
        player.setItemInHand(InteractionHand.MAIN_HAND, captured);
        RuneProgrammerMenu menu = new RuneProgrammerMenu(1, player.getInventory(), InteractionHand.MAIN_HAND, projection());
        captured.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(), new ScopedSourceEnvelope(1, new byte[]{1}));
        helper.assertTrue(dataSlotValue(menu) == 0, "same-item component mutation must flip the synchronized validity slot");
        ScopedFunctionalProjection invalid = menu.functionalProjection();
        helper.assertTrue(invalid.sourceState() == ScopedFunctionalProjection.SourceState.STALE,
                "same talisman component change must invalidate the captured menu projection");
        helper.assertTrue(invalid.authoredRows().isEmpty() && invalid.checkedRows().isEmpty(),
                "invalid menu binding cannot retain functional rows");
        RuneProgrammerMenu clientReceiver = new RuneProgrammerMenu(2, player.getInventory(), InteractionHand.MAIN_HAND, projection());
        clientReceiver.setData(0, 0);
        helper.assertTrue(clientReceiver.functionalProjection().sourceState() == ScopedFunctionalProjection.SourceState.STALE
                        && clientReceiver.functionalProjection().authoredRows().isEmpty(),
                "ordinary menu-data invalidation must withhold rows at the receiving menu");

        ServerPlayer mutationPlayer = helper.makeMockServerPlayerInLevel();
        ItemStack mutationTarget = new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
        mutationPlayer.setItemInHand(InteractionHand.MAIN_HAND, mutationTarget);
        RuneProgrammerMenu mutationMenu = new RuneProgrammerMenu(3, mutationPlayer.getInventory(), InteractionHand.MAIN_HAND, projection());
        mutationPlayer.containerMenu = mutationMenu;
        helper.assertTrue(mutationMenu.clickMenuButton(mutationPlayer, RuneProgrammerMenu.CLEAR_BUTTON),
                "real Programmer clear mutation must be accepted");
        helper.assertTrue(dataSlotValue(mutationMenu) == 0
                        && mutationMenu.functionalProjection().sourceState() == ScopedFunctionalProjection.SourceState.STALE,
                "real Programmer mutation must publish stale projection state");
        helper.succeed();
    }

    private static ScopedFunctionalProjection projection() {
        ScopedFunctionalProjection.Row row = new ScopedFunctionalProjection.Row("$.body", ScopedFunctionalProjection.RowKind.PARAMETER_REFERENCE, "#0", "hint", 0, 1);
        return new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                ScopedFunctionalProjection.AttemptState.SUCCESS, ScopedFunctionalProjection.GraphState.PRESENT,
                ScopedFunctionalProjection.GraphRelation.MATCH, List.of(row), List.of(row), List.of(), 1);
    }

    /** 63 maximal rows plus one 448-byte row make the encoded frame exactly 65,536 bytes. */
    private static ScopedFunctionalProjection projectionEncodedAtLimit() {
        var rows = new java.util.ArrayList<ScopedFunctionalProjection.Row>();
        for (int index = 0; index < 63; index++) {
            rows.add(new ScopedFunctionalProjection.Row("p".repeat(512), ScopedFunctionalProjection.RowKind.LITERAL,
                    "a".repeat(256), "b".repeat(256), -1, 0));
        }
        rows.add(new ScopedFunctionalProjection.Row("", ScopedFunctionalProjection.RowKind.LITERAL,
                "a".repeat(256), "b".repeat(180), -1, 0));
        return new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                ScopedFunctionalProjection.AttemptState.LANGUAGE_REJECTED, ScopedFunctionalProjection.GraphState.ABSENT,
                ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE, rows, List.of(),
                List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.LANGUAGE,
                        ScopedFunctionalProjection.Code.LANGUAGE_REJECTED, "$")), 0);
    }

    private static ScopedFunctionalProjection projectionEncodingOverflow() {
        var rows = new java.util.ArrayList<ScopedFunctionalProjection.Row>();
        for (int index = 0; index < 256; index++) {
            rows.add(new ScopedFunctionalProjection.Row("p".repeat(512), ScopedFunctionalProjection.RowKind.LITERAL,
                    "a".repeat(256), "b".repeat(256), -1, 0));
        }
        return new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                ScopedFunctionalProjection.AttemptState.LANGUAGE_REJECTED, ScopedFunctionalProjection.GraphState.PRESENT,
                ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE, rows, List.of(),
                List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.LANGUAGE,
                        ScopedFunctionalProjection.Code.LANGUAGE_REJECTED, "$")), 0);
    }

    private static boolean rejected(Runnable action) {
        try { action.run(); return false; } catch (IllegalArgumentException expected) { return true; }
    }

    @SuppressWarnings("unchecked")
    private static int dataSlotValue(RuneProgrammerMenu menu) {
        try {
            var field = net.minecraft.world.inventory.AbstractContainerMenu.class.getDeclaredField("dataSlots");
            field.setAccessible(true);
            return ((List<net.minecraft.world.inventory.DataSlot>) field.get(menu)).getFirst().get();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("unable to inspect the real menu validity DataSlot", exception);
        }
    }

    private static void assertStale(GameTestHelper helper, ItemStack target,
                                    ScopedFunctionalProjectionService.AuthoritySnapshot captured,
                                    ScopedFunctionalProjectionService.AuthoritySnapshot changed, String authority) {
        ScopedFunctionalProjection stale = ScopedFunctionalProjectionService.acceptCandidate(target, target.copy(), captured,
                projection(), true, target, changed);
        assertStale(helper, stale, authority);
    }

    private static void assertStale(GameTestHelper helper, ScopedFunctionalProjection stale, String authority) {
        helper.assertTrue(stale.sourceState() == ScopedFunctionalProjection.SourceState.STALE
                        && stale.attemptState() == ScopedFunctionalProjection.AttemptState.AUTHORITY_STALE,
                authority + " change after candidate build must become stale");
        helper.assertTrue(stale.authoredRows().isEmpty() && stale.checkedRows().isEmpty()
                        && stale.graphState() == ScopedFunctionalProjection.GraphState.PRESENT
                        && stale.graphRelation() == ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                authority + " stale recheck must expose no functional rows");
    }

    private static void assertProjection(GameTestHelper helper, ItemStack talisman,
                                         ScopedFunctionalProjection.SourceState source,
                                         ScopedFunctionalProjection.AttemptState attempt) {
        ItemStack before = talisman.copy();
        int[] compileCalls = {0};
        ScopedFunctionalProjection projection = ScopedFunctionalProjectionService.build(talisman, com.mathmod.knowledge.PlayerKnowledge.empty(), () -> compileCalls[0]++);
        helper.assertTrue(projection.sourceState() == source && projection.attemptState() == attempt,
                "projection must preserve the persisted read classification; actual=" + projection.sourceState() + "/" + projection.attemptState());
        helper.assertTrue(compileCalls[0] == (source == ScopedFunctionalProjection.SourceState.CURRENT_VALID ? 1 : 0),
                "only current-valid source may invoke exactly one compile");
        helper.assertTrue(ItemStack.isSameItemSameComponents(before, talisman),
                "projection read/compile must not mutate the registered item");
    }

    private static byte[] sourceBytes() {
        return "{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}".getBytes(StandardCharsets.UTF_8);
    }
}
