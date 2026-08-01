package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.item.ProgrammedTalismanItem;
import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.registry.ModAttachments;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.screen.RuneProgrammerMenu;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.function.Supplier;
import java.util.ArrayList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/** Dedicated-server evidence for the canonical Factored Leap authority path. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class L0FactoredLeapGameTests {
    private L0FactoredLeapGameTests() { }

    @GameTest(template = "empty") public static void factoredLeapMenuRoutePersistsExactSourceGraphAndResources(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel(); player.setItemInHand(InteractionHand.MAIN_HAND, talisman()); ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        try { grantConstructionKnowledge(player); }
        catch (Throwable harnessSync) { helper.assertTrue(String.valueOf(harnessSync.getMessage()).contains("sync_attachments"), "only mock-player knowledge sync may fail after attachment write: " + harnessSync); }
        RuneProgrammerMenu menu = new RuneProgrammerMenu(1, player.getInventory(), InteractionHand.MAIN_HAND); player.containerMenu = menu;
        assertCandidateSurvivesCopy(helper);
        boolean handled = false;
        try { handled = menu.clickMenuButton(player, 37); }
        catch (Throwable harnessSync) { helper.assertTrue(String.valueOf(harnessSync.getMessage()).contains("sync_attachments"), "only mock-player sync may fail after commit: " + harnessSync); handled = true; }
        var reference = new ScopedServerCompileService(MathModRuneBootstrap.registry()).compile(new ScopedServerCompileRequest(FactoredLeapTheorem.source(), com.mathmod.knowledge.KnowledgeService.get(player), () -> false));
        if (reference.candidate().isEmpty()) { helper.fail("granted live knowledge must admit the canonical candidate: language=" + reference.languageIssues() + ", service=" + reference.serviceIssues()); return; }
        var compiled = reference.candidate().orElseThrow();
        helper.assertTrue(handled, "button 37 must be consumed by the real menu route");
        var persisted = ProgramStorage.get(stack);
        if (persisted.isEmpty()) { helper.fail("real menu route did not persist: handled=" + handled + ", live knowledge=" + com.mathmod.knowledge.KnowledgeService.get(player) + ", graph=" + stack.get(ModDataComponents.PROGRAM.get()) + ", source=" + stack.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get()) + ", resources=" + stack.get(ModDataComponents.PROGRAM_RESOURCES.get())); return; }
        helper.assertTrue(compiled.equals(persisted.orElseThrow()), "canonical source must persist its exact compiled executable graph");
        helper.assertTrue(ScopedSourceWireCodec.encode(FactoredLeapTheorem.source()).equals(stack.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get())), "menu route must persist exact schema-1 canonical source bytes");
        helper.assertTrue(ScopedProgramPersistence.read(stack).status() == ScopedSourceRead.Status.CURRENT_VALID, "success must persist readable canonical source");
        helper.assertTrue(ProgramResources.recommendedFor(compiled).equals(stack.get(ModDataComponents.PROGRAM_RESOURCES.get())), "menu route must persist exact accepted resource list");
        helper.assertTrue(stack.get(ModDataComponents.PROGRAM_NAME.get()) == null && stack.get(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get()) == null && stack.get(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get()) == null, "functional menu success must leave non-authoritative components absent"); helper.succeed();
    }
    @GameTest(template = "empty") public static void factoredLeapExecutesForwardAndUpwardOutcome(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel(); player.getAbilities().instabuild = true; player.setYRot(0.0F);
        ItemStack stack = talisman(); var graph = FactoredLeapTheorem.presentationGraph(); stack.set(ModDataComponents.PROGRAM.get(), graph);
        stack.set(ModDataComponents.PROGRAM_RESOURCES.get(), ProgramResources.recommendedFor(graph));
        var before = player.getDeltaMovement(); ProgramExecutionResult result = null;
        try { result = ProgramExecutor.execute(stack, player); }
        catch (RuntimeException harnessSync) { helper.assertTrue(String.valueOf(harnessSync.getMessage()).contains("sync_attachments"), "only the mock-player attachment sync may fail after execution: " + harnessSync); }
        var after = player.getDeltaMovement();
        helper.assertTrue(result == null || result.success(), "Factored Leap must execute through the existing graph executor: " + (result == null ? "mock-player sync completed after execution" : result.messageKey()));
        helper.assertTrue(after.y > before.y && after.horizontalDistanceSqr() > before.horizontalDistanceSqr(), "Factored Leap must add upward and forward movement"); helper.succeed();
    }
    @GameTest(template = "empty") public static void factoredLeapMissingKnowledgeRejectsWithoutMutation(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel(); ItemStack stack = talisman(); player.setItemInHand(InteractionHand.MAIN_HAND, stack); ItemStack before = stack.copy();
        RuneProgrammerMenu menu = new RuneProgrammerMenu(1, player.getInventory(), InteractionHand.MAIN_HAND); player.containerMenu = menu;
        PacketCapture packets = PacketCapture.install(player);
        try { helper.assertTrue(menu.clickMenuButton(player, 37), "missing theorem knowledge must be handled by the real menu route"); }
        catch (RuntimeException harnessSync) { helper.assertTrue(String.valueOf(harnessSync.getMessage()).contains("sync_attachments"), "only mock-player feedback sync may fail: " + harnessSync); }
        helper.assertTrue(ItemStack.isSameItemSameComponents(before, stack), "missing theorem knowledge must reject without mutation");
        helper.assertTrue(!packets.hasSavedFeedback(), "missing theorem knowledge must not emit saved feedback: " + packets.messages);
        helper.assertTrue(!packets.hasSuccessOnlySynchronization(), "missing theorem knowledge must not emit success-only stack synchronization: " + packets.messages); helper.succeed();
    }
    @GameTest(template = "empty") public static void factoredLeapStaleTargetRejectsWithoutMutation(GameTestHelper helper) {
        ItemStack first = talisman(), before = first.copy(), replacement = talisman(); AtomicInteger calls = new AtomicInteger(); MathModRuneBootstrap.bootstrap();
        var result = new ScopedFunctionalInscriptionService(MathModRuneBootstrap.registry()).inscribe(FactoredLeapTheorem.source(), "", new ScopedCommitAuthority(() -> calls.getAndIncrement() == 0 ? first : replacement, PlayerKnowledge.empty(), () -> false));
        helper.assertTrue(result == ScopedCommitResult.TARGET_STALE && ItemStack.isSameItemSameComponents(before, first), "stale target must not mutate"); helper.succeed();
    }
    @GameTest(template = "empty") public static void factoredLeapCancelledRequestRejectsWithoutMutation(GameTestHelper helper) { stale(helper, () -> true, ScopedCommitResult.REQUEST_CANCELLED); }
    @GameTest(template = "empty") public static void factoredLeapStaleGenerationRejectsWithoutMutation(GameTestHelper helper) {
        ItemStack stack = talisman(), before = stack.copy(); MathModRuneBootstrap.bootstrap(); RuneRegistry isolated = copyRunes(); AtomicInteger calls = new AtomicInteger();
        var result = new ScopedFunctionalInscriptionService(isolated).inscribe(FactoredLeapTheorem.source(), "", new ScopedCommitAuthority(() -> { if (calls.getAndIncrement() > 0) isolated.setEnabled("mathmod:push_self", false); return stack; }, PlayerKnowledge.empty(), () -> false));
        helper.assertTrue(result == ScopedCommitResult.REGISTRY_GENERATION_STALE && ItemStack.isSameItemSameComponents(before, stack), "changed rune generation must reject before mutation"); helper.succeed();
    }
    @GameTest(template = "empty") public static void factoredLeapStaleKnowledgeRejectsWithoutMutation(GameTestHelper helper) {
        ItemStack stack = talisman(), before = stack.copy(); AtomicInteger calls = new AtomicInteger();
        var result = inscribe(stack, () -> calls.getAndIncrement() == 0 ? PlayerKnowledge.empty() : PlayerKnowledge.empty().withSchemaVersion(4), () -> false);
        helper.assertTrue(result == ScopedCommitResult.KNOWLEDGE_STALE && ItemStack.isSameItemSameComponents(before, stack), "changed player knowledge must reject before mutation"); helper.succeed();
    }
    @GameTest(template = "empty") public static void factoredLeapStaleMaterialsRejectsWithoutMutation(GameTestHelper helper) {
        ItemStack stack = talisman(), before = stack.copy(); MathModRuneBootstrap.bootstrap(); List<com.mathmod.kubejs.RuneMaterialDefinition> original = ProgramResources.materials(); AtomicInteger reads = new AtomicInteger();
        Supplier<List<com.mathmod.kubejs.RuneMaterialDefinition>> live = () -> reads.getAndIncrement() == 0 ? original : List.of(); var runes = MathModRuneBootstrap.registry();
        var compiler = new ScopedServerCompileService(runes, live, com.mathmod.knowledge.KnowledgeDefinitions::snapshot);
        var result = new ScopedFunctionalInscriptionService(runes, compiler, live).inscribe(FactoredLeapTheorem.source(), "", new ScopedCommitAuthority(() -> stack, PlayerKnowledge.empty(), () -> false));
        helper.assertTrue(result == ScopedCommitResult.MATERIALS_STALE && ItemStack.isSameItemSameComponents(before, stack), "changed material catalog must reject before mutation"); helper.succeed();
    }
    @GameTest(template = "empty", batch = "zz_l0_factored_leap_commit_faults") public static void factoredLeapAllCommitFaultsRollbackExactSourceBytes(GameTestHelper helper) {
        ItemStack stack = talisman(); stack.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(), new ScopedSourceEnvelope(77, new byte[]{1, 2, 3})); ItemStack before = stack.copy();
        for (int component = 0; component < 6; component++) for (ScopedProgramComponentTransaction.Phase phase : ScopedProgramComponentTransaction.Phase.values()) {
            int injected = component; AtomicBoolean fired = new AtomicBoolean(); ScopedProgramComponentTransaction.setTestInjector((index, at) -> { if (index == injected && at == phase) { fired.set(true); throw new IllegalStateException("fault"); } });
            try { ScopedCommitResult result = inscribe(stack, () -> false); helper.assertTrue(result == ScopedCommitResult.COMMIT_FAILED && fired.get(), "fault must reach and reject " + component + phase + ": " + result + ", fired=" + fired.get()); }
            finally { ScopedProgramComponentTransaction.setTestInjector(null); }
            helper.assertTrue(ItemStack.isSameItemSameComponents(before, stack), "rollback must restore exact bytes " + component + phase);
        } helper.succeed();
    }
    @GameTest(template = "empty") public static void factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess(GameTestHelper helper) {
        ItemStack stack = talisman();
        helper.assertTrue(inscribe(stack, () -> false) == ScopedCommitResult.SUCCESS, "setup must create a successful Factored Leap inscription");
        var ops = RegistryOps.create(NbtOps.INSTANCE, helper.getLevel().registryAccess());
        ItemStack reloaded = ItemStack.CODEC.parse(ops, ItemStack.CODEC.encodeStart(ops, stack).getOrThrow()).getOrThrow();
        helper.assertTrue(ProgramStorage.get(stack).equals(ProgramStorage.get(reloaded)), "item codec reload must retain the admitted executable graph");
        helper.assertTrue(java.util.Objects.equals(stack.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get()), reloaded.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get())), "item codec reload must retain exact canonical source envelope bytes");
        helper.assertTrue(java.util.Objects.equals(stack.get(ModDataComponents.PROGRAM_RESOURCES.get()), reloaded.get(ModDataComponents.PROGRAM_RESOURCES.get())), "item codec reload must retain accepted resources");
        ItemStack beforeRead = reloaded.copy();
        helper.assertTrue(ScopedProgramPersistence.read(reloaded).status() == ScopedSourceRead.Status.CURRENT_VALID, "reloaded source must remain current and readable");
        helper.assertTrue(ProgramStorage.get(reloaded).isPresent(), "inspection read must retain executable graph authority");
        var tooltip = new ArrayList<net.minecraft.network.chat.Component>();
        ((ProgrammedTalismanItem) reloaded.getItem()).appendHoverText(reloaded, Item.TooltipContext.of(helper.getLevel()), tooltip, TooltipFlag.NORMAL);
        helper.assertTrue(!tooltip.isEmpty(), "tooltip-equivalent item read must describe the reloaded theorem item");
        helper.assertTrue(ItemStack.isSameItemSameComponents(beforeRead, reloaded), "persistence and graph inspection reads must not compile, migrate, repair, or mutate");

        ItemStack emptyResources = talisman();
        RuneRegistry resourceFreeRunes = new RuneRegistry();
        resourceFreeRunes.register(com.mathmod.runes.RuneDefinition.builder("test:emit").output(com.mathmod.runes.RuneType.UNIT).purity(com.mathmod.runes.RunePurity.EFFECT).executorKey("debug_marker").build());
        var resourceFreeSource = new com.mathmod.language.ScopedProgramSource(1, new com.mathmod.language.ScopedExpression.RuneCall("test:emit", List.of()), com.mathmod.language.RuneTypeExpression.value(com.mathmod.runes.RuneType.UNIT), 16);
        var noMaterials = new ScopedServerCompileService(resourceFreeRunes, List::of, com.mathmod.knowledge.KnowledgeDefinitions::snapshot);
        var noMaterialsService = new ScopedFunctionalInscriptionService(resourceFreeRunes, noMaterials, List::of);
        ScopedCommitResult emptyResult = noMaterialsService.inscribe(resourceFreeSource, "", new ScopedCommitAuthority(() -> emptyResources, PlayerKnowledge::empty, () -> false));
        helper.assertTrue(emptyResult == ScopedCommitResult.SUCCESS, "resource-free candidate must commit through the same coordinator: " + emptyResult);
        helper.assertTrue(emptyResources.get(ModDataComponents.PROGRAM_RESOURCES.get()) == null, "an empty accepted resource list must be represented by an absent component");

        ServerPlayer faultPlayer = helper.makeMockServerPlayerInLevel(); ItemStack faultStack = talisman(); faultPlayer.setItemInHand(InteractionHand.MAIN_HAND, faultStack);
        try { grantConstructionKnowledge(faultPlayer); }
        catch (Throwable harnessSync) { helper.assertTrue(String.valueOf(harnessSync.getMessage()).contains("sync_attachments"), "only mock-player knowledge sync may fail after attachment write: " + harnessSync); }
        RuneProgrammerMenu faultMenu = new RuneProgrammerMenu(2, faultPlayer.getInventory(), InteractionHand.MAIN_HAND); faultPlayer.containerMenu = faultMenu;
        PacketCapture faultPackets = PacketCapture.install(faultPlayer); ItemStack beforeFault = faultStack.copy(); AtomicBoolean injected = new AtomicBoolean();
        ScopedProgramComponentTransaction.setTestInjector((index, phase) -> { if (index == 0 && phase == ScopedProgramComponentTransaction.Phase.BEFORE) { injected.set(true); throw new IllegalStateException("menu fault"); } });
        try { helper.assertTrue(faultMenu.clickMenuButton(faultPlayer, 37), "injected commit failure must remain handled by the real menu route"); }
        finally { ScopedProgramComponentTransaction.setTestInjector(null); }
        helper.assertTrue(injected.get(), "real menu route must reach the injected transaction fault");
        helper.assertTrue(ItemStack.isSameItemSameComponents(beforeFault, faultStack), "injected menu commit failure must roll back without mutation");
        helper.assertTrue(!faultPackets.hasSavedFeedback(), "injected menu commit failure must not emit saved feedback: " + faultPackets.messages);
        helper.assertTrue(!faultPackets.hasSuccessOnlySynchronization(), "injected menu commit failure must not emit success-only stack synchronization: " + faultPackets.messages);
        helper.succeed();
    }

    private static ItemStack talisman() { return new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); }
    private static void grantConstructionKnowledge(ServerPlayer player) { PlayerKnowledge knowledge = PlayerKnowledge.empty(); var theorem = com.mathmod.knowledge.KnowledgePolicy.requirementFor(ProgramPresets.presetForButton(37).orElseThrow()); if (theorem.isPresent()) knowledge = knowledge.grant(theorem.get().kind(), theorem.get().id()); for (var node : FactoredLeapTheorem.presentationGraph().nodes()) { com.mathmod.util.NamespacedId id = com.mathmod.util.NamespacedId.parse(node.runeId()); var requirement = com.mathmod.knowledge.KnowledgeDefinitions.snapshot().requirementFor(com.mathmod.knowledge.KnowledgeKind.RUNE, id); if (requirement.isPresent()) knowledge = knowledge.grant(requirement.get().kind(), requirement.get().id()); } player.setData(ModAttachments.PLAYER_KNOWLEDGE, knowledge); }
    private static ScopedCommitResult inscribe(ItemStack stack, ScopedCompileCancellation cancellation) { return inscribe(stack, PlayerKnowledge::empty, cancellation); }
    private static ScopedCommitResult inscribe(ItemStack stack, Supplier<PlayerKnowledge> knowledge, ScopedCompileCancellation cancellation) { MathModRuneBootstrap.bootstrap(); return new ScopedFunctionalInscriptionService(MathModRuneBootstrap.registry()).inscribe(FactoredLeapTheorem.source(), "", new ScopedCommitAuthority(() -> stack, knowledge, cancellation)); }
    private static void stale(GameTestHelper helper, ScopedCompileCancellation cancellation, ScopedCommitResult expected) { ItemStack stack = talisman(), before = stack.copy(); helper.assertTrue(inscribe(stack, cancellation) == expected && ItemStack.isSameItemSameComponents(before, stack), "rejected request must not mutate"); helper.succeed(); }
    private static RuneRegistry copyRunes() { RuneRegistry isolated = new RuneRegistry(); MathModRuneBootstrap.registry().definitions().forEach(isolated::register); return isolated; }
    private static void assertCandidateSurvivesCopy(GameTestHelper helper) {
        MathModRuneBootstrap.bootstrap(); var result = new ScopedServerCompileService(MathModRuneBootstrap.registry()).compile(new ScopedServerCompileRequest(FactoredLeapTheorem.source(), PlayerKnowledge.empty(), () -> false));
        var graph = result.candidate().orElseThrow(); var source = ScopedSourceWireCodec.encode(FactoredLeapTheorem.source()); var resources = result.recommendations();
        var candidate = new ScopedProgramComponentTransaction.State(graph, true, source, true, null, false, resources, !resources.isEmpty(), null, false, null, false);
        ItemStack probe = talisman(); probe.set(ModDataComponents.PROGRAM.get(), graph); probe.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(), source); if (!resources.isEmpty()) probe.set(ModDataComponents.PROGRAM_RESOURCES.get(), resources);
        var direct = ScopedProgramComponentTransaction.State.capture(probe); var copied = ScopedProgramComponentTransaction.State.capture(probe.copy());
        helper.assertTrue(direct.equals(candidate), "candidate direct mismatch: " + stateDifference(candidate, direct));
        helper.assertTrue(copied.equals(candidate), "candidate copy mismatch: " + stateDifference(candidate, copied));
    }
    private static String stateDifference(ScopedProgramComponentTransaction.State expected, ScopedProgramComponentTransaction.State actual) { return "program=" + expected.program().equals(actual.program()) + ",source=" + expected.source().equals(actual.source()) + ",name=" + java.util.Objects.equals(expected.name(), actual.name()) + ",resources=" + java.util.Objects.equals(expected.resources(), actual.resources()) + ",guided=" + java.util.Objects.equals(expected.guided(), actual.guided()) + ",actions=" + java.util.Objects.equals(expected.actions(), actual.actions()); }
    private static final class PacketCapture extends ChannelOutboundHandlerAdapter {
        private final List<Packet<?>> messages = new ArrayList<>();
        static PacketCapture install(ServerPlayer player) {
            PacketCapture capture = new PacketCapture();
            player.connection.getConnection().channel().pipeline().addLast(capture);
            return capture;
        }
        @Override public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
            if (message instanceof Packet<?> packet) messages.add(packet);
            super.write(context, message, promise);
        }
        boolean hasSavedFeedback() {
            return messages.stream().filter(ClientboundSystemChatPacket.class::isInstance).map(ClientboundSystemChatPacket.class::cast)
                    .map(ClientboundSystemChatPacket::content).map(net.minecraft.network.chat.Component::getContents)
                    .filter(TranslatableContents.class::isInstance).map(TranslatableContents.class::cast)
                    .anyMatch(contents -> contents.getKey().equals("item.mathmod.programmed_talisman.saved"));
        }
        boolean hasSuccessOnlySynchronization() { return messages.stream().anyMatch(ClientboundContainerSetSlotPacket.class::isInstance); }
    }
}
