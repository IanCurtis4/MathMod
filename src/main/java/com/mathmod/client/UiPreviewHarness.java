package com.mathmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mathmod.MathMod;
import com.mathmod.block.RuneAnchorBlockEntity;
import com.mathmod.client.screen.RuneProgrammerScreen;
import com.mathmod.client.screen.RuneInspectorScreen;
import com.mathmod.client.screen.ProgrammerLayout;
import com.mathmod.client.screen.ResourcesLayout;
import com.mathmod.client.screen.TalismanResourcesScreen;
import com.mathmod.client.screen.MathButton;
import com.mathmod.client.screen.FieldLedgerScreen;
import com.mathmod.client.screen.TheoremStatementPresentation;
import com.mathmod.item.ChalkPresetStorage;
import com.mathmod.item.ProgrammedTalismanItem;
import com.mathmod.knowledge.FieldLedgerView;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.KnowledgeProgress;
import com.mathmod.knowledge.KnowledgeService;
import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.program.AnchorProgramPreset;
import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.CustomSpellWorkspace;
import com.mathmod.program.ProgramNames;
import com.mathmod.program.ProgramMessageComponents;
import com.mathmod.program.ProgramPresets;
import com.mathmod.program.ProgramResources;
import com.mathmod.program.ProgramStorage;
import com.mathmod.program.ProgramSurface;
import com.mathmod.program.PlayerProgramCosts;
import com.mathmod.program.ResourceSelection;
import com.mathmod.program.ScopedSourceEnvelope;
import com.mathmod.program.ScopedFunctionalProjection;
import com.mathmod.program.TalismanPreset;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.registry.ModBlocks;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModMobEffects;
import com.mathmod.screen.RuneProgrammerMenu;
import com.mathmod.screen.TalismanResourcesMenu;
import com.mathmod.screen.FieldLedgerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = MathMod.MOD_ID, value = Dist.CLIENT)
public final class UiPreviewHarness {
    private static final String PREVIEW = System.getProperty("mathmod.uiPreview", "").trim();
    private static final String PREVIEW_LOCALE = System.getenv("MATHMOD_UI_PREVIEW_LOCALE") == null ? "" : System.getenv("MATHMOD_UI_PREVIEW_LOCALE").trim();
    private static final String MAX_LENGTH_PROOF_NAME = "Hipotese da Convergencia Celeste";
    private static int ticks;
    private static boolean opened;
    private static boolean captureReady;
    private static boolean frameReady;
    private static boolean captured;
    private static int stableCaptureTicks;
    private static int renderedScreensBeforeCapture;
    private static boolean respawnRequested;
    private static boolean clearFirstClickSent;
    private static boolean clearSecondClickSent;
    private static boolean savedPaletteScrolled;
    private static int liveReadinessStep;
    private static RuneProgrammerScreen liveReadinessScreen;
    private static int customRenameStep;
    private static int laboratoryResetStep;
    private static int laboratoryResetStepTick;
    private static List<CustomSpellAction> laboratoryResetInitialActions = List.of();
    private static String laboratoryResetInitialName = "";
    private static int resourceMutationStep;
    private static boolean functionalInspectorClickSent;
    private static boolean functionalSavedTabSelected;
    private static boolean functionalInteractionAudited;
    private static boolean functionalNarrationAudited;
    private static boolean previewLocaleConfigured;
    private static volatile boolean previewLocaleReloadComplete;
    private static String mouseCatalogTargetId = "";
    private static String keyboardCatalogTargetId = "";
    private static boolean sawResourceQuantityTwo;
    private static List<ResourceSelection> resourceClearInitialSelections = List.of();
    private static int resourceClearArmedTick;
    private static int scrollbarAuditStep;
    private static List<ResourceSelection> scrollbarInitialResources = List.of();
    private static String scrollbarInitialPresetId = "";
    private static boolean resourceHelpClickSent;
    private static boolean programmerHelpClickSent;
    private static boolean resourceBackClickSent;
    private static int firstSpellStep;
    private static int firstSpellStepTicks;
    private static volatile boolean firstSpellCastComplete;
    private static volatile String firstSpellFailure = "";
    private static int anchorJourneyStep;
    private static int anchorJourneyStepTicks;
    private static boolean anchorJourneyActionSent;
    private static volatile int anchorJourneyServerStep = -1;
    private static volatile String anchorJourneyFailure = "";
    private static volatile BlockPos anchorJourneyPos;
    private static int patchouliMatrixIndex;
    private static int patchouliMatrixTicks;
    private static boolean patchouliMatrixCommandSent;
    private static boolean patchouliMatrixCaptureRequested;
    private static boolean patchouliMatrixCaptureInFlight;
    private static boolean patchouliMatrixFrameReady;
    private static int patchouliMatrixOpenAttempts;
    private static int authoringRegistryPreviewStep;
    private static int authoringRegistryPreviewStepTick;
    private static final Field OVERLAY_MESSAGE_FIELD = overlayMessageField();
    private static final Field CUSTOM_WORKSPACE_FIELD = programmerField("customWorkspace");
    private static final Field CUSTOM_SPELL_NAME_FIELD = programmerField("customSpellName");
    private static final Field PRESET_SCROLL_FIELD = programmerField("presetPaletteScroll");
    private static final Field SELECTED_PRESET_FIELD = programmerField("selectedPreset");
    private static final Field PARAMETER_ACTION_FIELD = programmerField("parameterAction");
    private static final Field PARAMETER_BOXES_FIELD = programmerField("parameterBoxes");
    private static final Field MATERIAL_SCROLL_FIELD = resourcesField("materialScroll");
    private static final Method MOUSE_MOVE_CALLBACK = mouseMoveCallback();

    private UiPreviewHarness() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (PREVIEW.isEmpty() || captured || captureReady) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }
        if (minecraft.player.isDeadOrDying()) {
            if (!respawnRequested) {
                minecraft.player.respawn();
                minecraft.setScreen(null);
                resetPatchouliMatrixNavigation();
                respawnRequested = true;
            }
            return;
        }
        respawnRequested = false;

        if (patchouliMatrixPreview()) {
            if (!previewLocaleReady(minecraft)) {
                return;
            }
            runPatchouliMatrixTick(minecraft);
            return;
        }

        ticks++;
        if (!previewLocaleReady(minecraft)) {
            ticks = 0;
            return;
        }
        if (!opened) {
            if (ticks < 40) {
                return;
            }
            openPreview(minecraft);
            opened = true;
            ticks = 0;
            return;
        }

        if (firstSpellPreview()) {
            runFirstSpellPreview(minecraft);
            return;
        }
        if (anchorJourneyPreview()) {
            runAnchorJourneyPreview(minecraft);
            return;
        }
        if (authoringRegistryPalettePreview()) {
            if (!(minecraft.screen instanceof RuneProgrammerScreen screen)) {
                throw new IllegalStateException("Authoring registry preview left the Laboratory screen");
            }
            runAuthoringRegistryPreview(screen);
            if (authoringRegistryPreviewStep < 3) {
                return;
            }
        }

        if (resourceHelpPreview()) {
            if (!resourceHelpClickSent
                    && minecraft.screen instanceof TalismanResourcesScreen screen
                    && ticks >= 5) {
                clickWidget(
                        screen,
                        Component.translatable("screen.mathmod.talisman_resources.help_action")
                );
                resourceHelpClickSent = true;
                ticks = 0;
                return;
            }
            if (!resourceHelpClickSent || !isPatchouliScreen(minecraft)) {
                if (ticks >= 100) {
                    throw new IllegalStateException("Resources help did not open its Patchouli entry");
                }
                return;
            }
            if (minecraft.player.containerMenu != minecraft.player.inventoryMenu) {
                throw new IllegalStateException("Resources help left the resource container open behind Patchouli");
            }
        }

        if (programmerHelpPreview()) {
            if (!programmerHelpClickSent
                    && minecraft.screen instanceof RuneProgrammerScreen screen
                    && ticks >= 5) {
                clickWidget(
                        screen,
                        Component.translatable("screen.mathmod.rune_programmer.help_action")
                );
                programmerHelpClickSent = true;
                ticks = 0;
                return;
            }
            if (!programmerHelpClickSent || !isPatchouliScreen(minecraft)) {
                if (ticks >= 100) {
                    throw new IllegalStateException("Programmer help did not open its Patchouli entry");
                }
                return;
            }
            if (minecraft.player.containerMenu != minecraft.player.inventoryMenu) {
                throw new IllegalStateException("Programmer help left the programmer container open behind Patchouli");
            }
        }

        if (resourceBackPreview()) {
            if (!resourceBackClickSent
                    && minecraft.screen instanceof TalismanResourcesScreen screen
                    && ticks >= 5) {
                MathMod.LOGGER.info("UI preview resources-back-to-proof: activating proof navigation");
                clickResourceAction(screen, 0);
                resourceBackClickSent = true;
                ticks = 0;
                return;
            }
            if (resourceBackClickSent && minecraft.screen instanceof RuneProgrammerScreen) {
                MathMod.LOGGER.info("UI preview resources-back-to-proof: programmer opened");
                captureReady = true;
                return;
            }
            if (ticks >= 100) {
                throw new IllegalStateException(
                        "Resources proof navigation did not open the Rune Programmer"
                );
            }
            return;
        }

        if (functionalProjectionPreview()) {
            if (!functionalNarrationAudited) {
                auditFunctionalNarrationStates();
                functionalNarrationAudited = true;
            }
            if (!functionalSavedTabSelected && minecraft.screen instanceof RuneProgrammerScreen screen && ticks >= 5) {
                clickWidget(screen, Component.translatable("screen.mathmod.rune_programmer.tab_saved"));
                functionalSavedTabSelected = true;
                ticks = 0;
                return;
            }
            if (functionalSavedTabSelected && !functionalInspectorClickSent
                    && minecraft.screen instanceof RuneProgrammerScreen screen && ticks >= 5) {
                clickWidget(screen, Component.translatable("screen.mathmod.rune_inspector.open"));
                functionalInspectorClickSent = true;
                ticks = 0;
                return;
            }
            if (!functionalInspectorClickSent) return;
            if (!functionalInteractionAudited && minecraft.screen instanceof RuneInspectorScreen screen && ticks >= 4) {
                auditFunctionalInspectorInteraction(minecraft, screen);
                functionalInteractionAudited = true;
                functionalInspectorClickSent = false;
                ticks = 0;
                return;
            }
        }

        if (!isExpectedScreen(minecraft)) {
            if (ticks % 10 == 0) {
                openPreview(minecraft);
            }
            return;
        }
        if (PREVIEW.equalsIgnoreCase("edit-theorem-disabled-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverSavedEdit(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("replace-proof-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverSavedReplace(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("saved-ready-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverSavedResources(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("saved-ready-live-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            if (!runLiveReadinessPreview(minecraft, screen)) {
                return;
            }
        }
        if (PREVIEW.equalsIgnoreCase("saved-palette-scrolled")
                && !savedPaletteScrolled
                && ticks >= 5
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            scrollSavedPaletteToEnd(screen);
            savedPaletteScrolled = true;
        }
        if (PREVIEW.equalsIgnoreCase("programmer-scrollbar-drag")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            runProgrammerScrollbarPreview(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("compact-toolbar-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverCustomAction(minecraft, screen, 1);
        }
        if (PREVIEW.equalsIgnoreCase("compact-palette-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverCustomPaletteBottomRow(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("laboratory-form-reuse-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverFirstCustomPaletteRow(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("resources-clear-tooltip")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            runResourceClearPreview(minecraft, screen, false);
        }
        if (PREVIEW.equalsIgnoreCase("resources-material-tooltip")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            hoverFirstMaterial(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("resources-notation-tooltip")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            hoverResourcesNotation(minecraft, screen);
        }
        if (longLoadoutNamePreview()
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            hoverLoadoutName(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("programmer-notation-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverProgrammerNotation(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("resources-add-remove")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            runResourceAddRemovePreview(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("resources-scrollbar-drag")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            runResourcesScrollbarPreview(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("resources-cleared")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            runResourceClearPreview(minecraft, screen, true);
        }
        if (PREVIEW.equalsIgnoreCase("keyboard-added-materials")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            runKeyboardAddedMaterialsPreview(screen);
        }
        if (PREVIEW.equalsIgnoreCase("keyboard-material-catalog")
                && minecraft.screen instanceof TalismanResourcesScreen screen) {
            runKeyboardMaterialCatalogPreview(screen);
        }
        if (PREVIEW.equalsIgnoreCase("laboratory-binding-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverFirstBinding(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("theorem-node-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverSecondTheoremNode(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("type-legend-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverTypeLegend(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("theorem-formula-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverTheoremFormula(minecraft, screen);
        }
        if ((PREVIEW.equalsIgnoreCase("workflow-demonstrated-tooltip")
                || PREVIEW.equalsIgnoreCase("workflow-witnesses-tooltip")
                || PREVIEW.equalsIgnoreCase("workflow-ready-tooltip"))
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverWorkflowSeal(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("already-inscribed-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverPresetAction(minecraft, screen, 0);
        }
        if (PREVIEW.equalsIgnoreCase("resources-active-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverPresetAction(minecraft, screen, 1);
        }
        if (PREVIEW.equalsIgnoreCase("blank-clear-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverPresetAction(minecraft, screen, 2);
        }
        if (PREVIEW.equalsIgnoreCase("laboratory-empty-undo-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            hoverCustomAction(minecraft, screen, 1);
        }
        if (PREVIEW.equalsIgnoreCase("clear-confirmation-tooltip")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            if (!clearFirstClickSent && ticks >= 5) {
                clickSavedClear(screen);
                clearFirstClickSent = true;
            }
            hoverSavedClear(minecraft, screen);
        }
        if (PREVIEW.equalsIgnoreCase("clear-confirmed")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            if (!clearFirstClickSent && ticks >= 5) {
                clickSavedClear(screen);
                clearFirstClickSent = true;
            } else if (clearFirstClickSent && !clearSecondClickSent && ticks >= 7) {
                clickSavedClear(screen);
                clearSecondClickSent = true;
            }
        }
        if (PREVIEW.equalsIgnoreCase("custom-name-reinscription")
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            if (customRenameStep == 0 && ticks >= 5) {
                clickSavedEdit(screen);
                customRenameStep = 1;
            } else if (customRenameStep == 1 && ticks >= 7) {
                replaceCustomName(screen, "Hipotese Renovada");
                customRenameStep = 2;
            } else if (customRenameStep == 2 && ticks >= 10) {
                clickCustomInscribe(screen);
                customRenameStep = 3;
            }
        }
        if (laboratoryResetPreview()
                && minecraft.screen instanceof RuneProgrammerScreen screen) {
            runLaboratoryResetPreview(
                    minecraft,
                    screen,
                    PREVIEW.equalsIgnoreCase("custom-name-reset")
            );
        }

        if (PREVIEW.equalsIgnoreCase("inscription-confirmed") && ticks == 5) {
            ProgramStorage.saveValidated(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND),
                    ProgramPresets.hop()
            );
        }
        if (PREVIEW.equalsIgnoreCase("clear-confirmation-tooltip") && !clearFirstClickSent) {
            return;
        }
        if (PREVIEW.equalsIgnoreCase("clear-confirmed")
                && ProgramStorage.get(minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)).isPresent()) {
            if (ticks >= 100) {
                throw new IllegalStateException("Cleared talisman did not synchronize back to the client");
            }
            return;
        }
        if (PREVIEW.equalsIgnoreCase("custom-name-reinscription")) {
            ItemStack stack = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean synchronizedTarget = ProgramStorage.getName(stack)
                    .filter("Hipotese Renovada"::equals)
                    .isPresent()
                    && ProgramStorage.getCustomActions(stack).equals(List.of(
                            CustomSpellAction.RIGHT_BASIS_VECTOR,
                            CustomSpellAction.PUSH_SELF
                    ));
            if (customRenameStep < 3 || !synchronizedTarget) {
                if (ticks >= 100) {
                    throw new IllegalStateException("Name-only custom reinscription did not synchronize");
                }
                return;
            }
        }
        if (PREVIEW.equalsIgnoreCase("laboratory-reset-confirmation-tooltip")) {
            boolean preserved = laboratoryResetStep >= 2
                    && laboratoryWorkspaceActions((RuneProgrammerScreen) minecraft.screen)
                    .equals(laboratoryResetInitialActions)
                    && laboratoryWorkspaceName((RuneProgrammerScreen) minecraft.screen)
                    .equals(laboratoryResetInitialName)
                    && laboratoryResetConfirmationVisible((RuneProgrammerScreen) minecraft.screen);
            if (!preserved) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "First Laboratory reset activation changed the workspace or hid confirmation"
                    );
                }
                return;
            }
        }
        if (PREVIEW.equalsIgnoreCase("custom-name-reset")) {
            ItemStack stack = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean talismanPreserved = ProgramStorage.getName(stack)
                    .filter("Hipotese de Gauss"::equals)
                    .isPresent()
                    && ProgramStorage.getCustomActions(stack).equals(laboratoryResetInitialActions);
            boolean localReset = laboratoryResetStep >= 3
                    && laboratoryWorkspaceActions((RuneProgrammerScreen) minecraft.screen).isEmpty()
                    && laboratoryWorkspaceName((RuneProgrammerScreen) minecraft.screen).isBlank();
            Component message = overlayMessage(minecraft);
            boolean serverConfirmed = message != null && message.equals(Component.translatable(
                    "item.mathmod.programmed_talisman.custom_reset"
            ));
            if (!talismanPreserved || !localReset || !serverConfirmed) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "Confirmed Laboratory reset did not synchronize or changed the talisman inscription"
                    );
                }
                return;
            }
            hoverCustomAction(minecraft, (RuneProgrammerScreen) minecraft.screen, 2);
        }
        if (PREVIEW.equalsIgnoreCase("resources-add-remove")) {
            List<ResourceSelection> selections = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            );
            boolean targetChosen = !mouseCatalogTargetId.isBlank();
            boolean synchronizedTarget = targetChosen && selections.equals(List.of(
                    new ResourceSelection(mouseCatalogTargetId, 1)
            ));
            if (resourceMutationStep < 2 || !sawResourceQuantityTwo || !synchronizedTarget) {
                if (ticks >= 100) {
                    throw new IllegalStateException("Resource add/remove mutations did not synchronize");
                }
                return;
            }
        }
        if (PREVIEW.equalsIgnoreCase("resources-clear-tooltip")) {
            List<ResourceSelection> selections = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            );
            boolean armed = minecraft.screen instanceof TalismanResourcesScreen screen
                    && resourceClearConfirmationVisible(screen);
            if (resourceMutationStep < 1
                    || !selections.equals(resourceClearInitialSelections)
                    || !armed) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "First resource-clear activation changed the preparation or did not expose confirmation"
                    );
                }
                return;
            }
        }
        if (PREVIEW.equalsIgnoreCase("resources-cleared")) {
            boolean synchronizedTarget = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            ).isEmpty();
            if (resourceMutationStep < 2 || !synchronizedTarget) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "Confirmed resource clear did not synchronize after preserving the first activation"
                    );
                }
                return;
            }
            if (minecraft.screen instanceof TalismanResourcesScreen screen) {
                hoverResourceClear(minecraft, screen);
            }
        }
        if (PREVIEW.equalsIgnoreCase("keyboard-added-materials")) {
            boolean synchronizedTarget = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            ).equals(List.of(new ResourceSelection(firstMaterialId(), 1)));
            boolean focusRetained = minecraft.screen instanceof TalismanResourcesScreen screen
                    && resourceListFocused(screen, false);
            if (resourceMutationStep < 1 || !synchronizedTarget || !focusRetained) {
                if (ticks >= 100) {
                    throw new IllegalStateException("Keyboard added-material removal did not synchronize");
                }
                return;
            }
        }
        if (PREVIEW.equalsIgnoreCase("keyboard-material-catalog")) {
            boolean targetChosen = !keyboardCatalogTargetId.isBlank();
            boolean synchronizedTarget = targetChosen && ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            ).equals(List.of(new ResourceSelection(keyboardCatalogTargetId, 1)));
            boolean focusRetained = minecraft.screen instanceof TalismanResourcesScreen screen
                    && resourceListFocused(screen, true);
            if (resourceMutationStep < 1 || !synchronizedTarget || !focusRetained) {
                if (ticks >= 100) {
                    throw new IllegalStateException("Keyboard material-catalog addition did not synchronize");
                }
                return;
            }
        }
        if ((PREVIEW.equalsIgnoreCase("programmer-scrollbar-drag")
                || PREVIEW.equalsIgnoreCase("resources-scrollbar-drag"))
                && scrollbarAuditStep < 2) {
            if (ticks >= 100) {
                throw new IllegalStateException("Scrollbar drag preview did not complete");
            }
            return;
        }
        if (PREVIEW.equalsIgnoreCase("cast-missing-attribute")) {
            Component expected = Component.translatable(
                    "item.mathmod.programmed_talisman.execute_missing_attributes",
                    ProgramMessageComponents.attributes(Map.of("motion", 1))
            );
            Component actual = overlayMessage(minecraft);
            if (actual == null || !actual.getString().equals(expected.getString())) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "Localized missing-attribute cast feedback did not arrive; expected '"
                                    + expected.getString()
                                    + "', saw '"
                                    + (actual == null ? "<none>" : actual.getString())
                                    + "'"
                    );
                }
                return;
            }
        }
        if (ticks < 30 || ++stableCaptureTicks < 4) {
            return;
        }

        captureReady = true;
        if (worldPreview()) {
            frameReady = true;
        }
    }

    @SubscribeEvent
    public static void onRenderScreen(ScreenEvent.Render.Post event) {
        if (patchouliMatrixPreview()) {
            if (patchouliMatrixCaptureRequested
                    && !patchouliMatrixCaptureInFlight
                    && isPatchouliScreen(Minecraft.getInstance())) {
                patchouliMatrixFrameReady = true;
            }
            return;
        }
        if (!captureReady || captured) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!isExpectedScreen(minecraft)) {
            return;
        }
        renderedScreensBeforeCapture++;
        frameReady = renderedScreensBeforeCapture >= 2;
    }

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Post event) {
        if (patchouliMatrixPreview()) {
            capturePatchouliMatrixFrame(Minecraft.getInstance());
            return;
        }
        if (!captureReady || !frameReady || captured) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!isExpectedScreen(minecraft)) {
            frameReady = false;
            return;
        }
        captured = true;
        RenderSystem.disableScissor();
        Screenshot.grab(
                minecraft.gameDirectory,
                "mathmod-" + PREVIEW + "-preview.png",
                minecraft.getMainRenderTarget(),
                message -> {
                    MathMod.LOGGER.info("UI preview result: {}", message.getString());
                    stopAfterCapture(minecraft);
                }
        );
    }

    private static void stopAfterCapture(Minecraft minecraft) {
        if (!PREVIEW.equalsIgnoreCase("saved-ready-live-tooltip")) {
            minecraft.execute(minecraft::stop);
            return;
        }
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            minecraft.execute(minecraft::stop);
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer != null) {
                serverPlayer.setInvulnerable(false);
            }
            minecraft.execute(minecraft::stop);
        });
    }

    private static void openPreview(Minecraft minecraft) {
        ItemStack talisman = previewTalisman();
        ItemStack heldItem;
        if (runeAnchorTooltipPreview()) {
            heldItem = new ItemStack(ModItems.RUNE_ANCHOR.get());
        } else if (chalkTooltipPreview() || anchorJourneyPreview()) {
            heldItem = new ItemStack(ModItems.CHALK.get());
        } else if (fieldLedgerPreview()) {
            heldItem = new ItemStack(ModItems.FIELD_LEDGER.get());
        } else {
            heldItem = talisman;
        }
        minecraft.player.setItemInHand(InteractionHand.MAIN_HAND, heldItem);
        if (fieldLedgerPreview()) {
            openFieldLedgerPreview(minecraft);
            return;
        }
        if (PREVIEW.equalsIgnoreCase("workflow-ready-tooltip")
                || PREVIEW.equalsIgnoreCase("saved-ready")
                || PREVIEW.equalsIgnoreCase("saved-ready-tooltip")) {
            minecraft.player.getInventory().add(new ItemStack(Items.FEATHER, 16));
        }
        if (firstSpellPreview()) {
            openFirstSpellProgrammer(minecraft);
            return;
        }
        if (patchouliPreview()) {
            openPatchouliPreview(minecraft);
            return;
        }
        if (runeInspectorPreview()) {
            if (functionalProjectionPreview()) {
                openFunctionalProjectionProgrammer(minecraft);
                return;
            }
            minecraft.setScreen(new RuneInspectorScreen(
                    null,
                    ProgramSurface.inscribed(ProgramPresets.hop()).inspect()
            ));
            return;
        }
        if (anchorJourneyPreview()) {
            minecraft.setScreen(null);
            dispatchAnchorJourneyStep(minecraft, 0);
            anchorJourneyActionSent = true;
            return;
        }
        if (worldPreview()) {
            triggerMissingCostCast(minecraft);
            return;
        }
        if (itemUseRoutePreview()) {
            triggerItemUseRoute(minecraft);
            return;
        }
        if (serverBackedProgrammerPreview()) {
            openServerProgrammer(minecraft);
            return;
        }
        if (serverBackedResourcesPreview()) {
            openServerResources(minecraft);
            return;
        }
        installServerPreviewItem(minecraft);

        if (itemTooltipPreview()) {
            minecraft.player.getInventory().selected = 0;
            minecraft.player.getInventory().setItem(0, heldItem);
            InventoryScreen screen = new InventoryScreen(minecraft.player);
            minecraft.setScreen(screen);
            int left = (screen.width - 176) / 2;
            int top = (screen.height - 166) / 2;
            movePreviewCursor(
                    minecraft,
                    screen.width,
                    screen.height,
                    left + 17.0D,
                    top + 151.0D
            );
            return;
        }

        if (PREVIEW.equalsIgnoreCase("resources")
                || PREVIEW.equalsIgnoreCase("minimum-resources")
                || PREVIEW.equalsIgnoreCase("resources-clear-tooltip")
                || PREVIEW.equalsIgnoreCase("resources-material-tooltip")
                || PREVIEW.equalsIgnoreCase("resources-notation-tooltip")
                || longLoadoutNamePreview()
                || PREVIEW.equalsIgnoreCase("keyboard-first-resources")) {
            TalismanResourcesMenu menu = new TalismanResourcesMenu(
                    0,
                    minecraft.player.getInventory(),
                    InteractionHand.MAIN_HAND
            );
            TalismanResourcesScreen screen = new TalismanResourcesScreen(
                    menu,
                    minecraft.player.getInventory(),
                    Component.translatable("screen.mathmod.talisman_resources")
            );
            minecraft.setScreen(screen);
            if (PREVIEW.equalsIgnoreCase("resources-clear-tooltip")) {
                hoverResourceClear(minecraft, screen);
            } else if (PREVIEW.equalsIgnoreCase("resources-material-tooltip")) {
                hoverFirstMaterial(minecraft, screen);
            } else if (PREVIEW.equalsIgnoreCase("resources-notation-tooltip")) {
                hoverResourcesNotation(minecraft, screen);
            } else if (longLoadoutNamePreview()) {
                hoverLoadoutName(minecraft, screen);
            } else if (PREVIEW.equalsIgnoreCase("keyboard-first-resources")) {
                focusFirstResourceTask(screen);
            } else if (PREVIEW.equalsIgnoreCase("minimum-resources")) {
                movePreviewCursor(minecraft, screen.width, screen.height, screen.width / 2.0D, 4.0D);
            }
            return;
        }

        RuneProgrammerMenu menu = new RuneProgrammerMenu(
                0,
                minecraft.player.getInventory(),
                InteractionHand.MAIN_HAND
        );
        RuneProgrammerScreen screen = new RuneProgrammerScreen(
                menu,
                minecraft.player.getInventory(),
                Component.translatable("screen.mathmod.rune_programmer")
        );
        minecraft.setScreen(screen);
        if (!PREVIEW.equalsIgnoreCase("authoring-registry-palette")) {
            requireTheoremCatalogFormulaFit(minecraft, screen);
        }
        requireTheoremStatementFit(minecraft, screen);
        if (PREVIEW.equalsIgnoreCase("keyboard-first-programmer")) {
            ProgrammerLayout previewLayout = previewLayout(screen);
            focusFirstFunctionalControl(
                    screen,
                    "Programmer",
                    Component.translatable("screen.mathmod.rune_programmer.help_action").getString(),
                    Component.translatable("screen.mathmod.rune_programmer.tab_presets").getString(),
                    Component.translatable(previewLayout.compact()
                            ? "screen.mathmod.rune_programmer.tab_custom_short"
                            : "screen.mathmod.rune_programmer.tab_custom").getString(),
                    Component.translatable("screen.mathmod.rune_programmer.tab_saved").getString()
            );
        } else if (PREVIEW.equalsIgnoreCase("programmer-notation-tooltip")) {
            hoverProgrammerNotation(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("edit-theorem-disabled-tooltip")) {
            hoverSavedEdit(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("replace-proof-tooltip")) {
            requireWidgetActive(
                    screen,
                    Component.translatable(previewLayout(screen).compact()
                            ? "screen.mathmod.rune_programmer.replace_proof_short"
                            : "screen.mathmod.rune_programmer.replace_proof")
            );
            hoverSavedReplace(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("saved-ready-tooltip")) {
            hoverSavedResources(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("saved-ready-live-tooltip")) {
            liveReadinessScreen = screen;
        } else if (PREVIEW.equalsIgnoreCase("clear-confirmation-tooltip")) {
            hoverSavedClear(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("workflow-demonstrated-tooltip")
                || PREVIEW.equalsIgnoreCase("workflow-witnesses-tooltip")
                || PREVIEW.equalsIgnoreCase("workflow-ready-tooltip")) {
            hoverWorkflowSeal(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("already-inscribed-tooltip")
                || PREVIEW.equalsIgnoreCase("resources-active-tooltip")) {
            openTheorems(screen);
            hoverPresetAction(
                    minecraft,
                    screen,
                    PREVIEW.equalsIgnoreCase("already-inscribed-tooltip") ? 0 : 1
            );
        } else if (PREVIEW.equalsIgnoreCase("blank-clear-tooltip")) {
            requireWidgetInactive(
                    screen,
                    Component.translatable("screen.mathmod.rune_programmer.clear")
            );
            hoverPresetAction(minecraft, screen, 2);
        } else if (PREVIEW.equalsIgnoreCase("laboratory-empty-undo-tooltip")) {
            openLaboratory(screen);
            requireWidgetInactive(
                    screen,
                    Component.translatable("screen.mathmod.rune_programmer.undo_custom")
            );
            hoverCustomAction(minecraft, screen, 1);
        } else if (PREVIEW.equalsIgnoreCase("keyboard-theorem")) {
            focusPaletteAndMove(screen, 8, true);
        } else if (PREVIEW.equalsIgnoreCase("theorem-catalog-control")) {
            focusPaletteAndMove(screen, 18, false);
        } else if (PREVIEW.equalsIgnoreCase("advanced-theorem-harmonic")) {
            focusPaletteAndMove(screen, 7, true);
        } else if (PREVIEW.equalsIgnoreCase("advanced-theorem-orthogonal")) {
            focusPaletteAndMove(screen, 8, true);
        } else if (PREVIEW.equalsIgnoreCase("advanced-theorem-quarter-turn")) {
            focusPaletteAndMove(screen, 9, true);
        } else if (PREVIEW.equalsIgnoreCase("advanced-theorem-quadrature")) {
            focusPaletteAndMove(screen, 10, true);
        } else if (PREVIEW.equalsIgnoreCase("keyboard-theorem-statement")) {
            focusTheoremStatement(screen);
        } else if (PREVIEW.equalsIgnoreCase("keyboard-laboratory")) {
            openLaboratory(screen);
            focusPaletteAndMove(screen, 18, true);
        } else if (PREVIEW.equalsIgnoreCase("compact-toolbar-tooltip")) {
            openLaboratory(screen);
            hoverCustomAction(minecraft, screen, 1);
        } else if (PREVIEW.equalsIgnoreCase("compact-palette-tooltip")) {
            openLaboratory(screen);
            focusPaletteAndMove(screen, 18, false);
            hoverCustomPaletteBottomRow(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("laboratory-form-reuse-tooltip")) {
            openLaboratory(screen);
            searchLaboratory(screen, "push_self");
            hoverFirstCustomPaletteRow(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("advanced-laboratory-symmetry")) {
            openLaboratory(screen);
            searchLaboratory(screen, "cyclic");
            hoverFirstCustomPaletteRow(minecraft, screen);
        } else if (PREVIEW.equalsIgnoreCase("laboratory-search")) {
            openLaboratory(screen);
            searchLaboratory(screen, "vector");
        } else if (PREVIEW.equalsIgnoreCase("laboratory-search-localized")) {
            openLaboratory(screen);
            searchLaboratory(screen, "vetor");
        } else if (PREVIEW.equalsIgnoreCase("laboratory-search-empty")) {
            openLaboratory(screen);
            searchLaboratory(screen, "nonexistent rune");
        } else if (PREVIEW.equalsIgnoreCase("laboratory-parameter-dialog")) {
            openLaboratory(screen);
            searchLaboratory(screen, "simpson");
            clickFirstCustomPaletteRow(screen);
        } else if (PREVIEW.equalsIgnoreCase("authoring-registry-palette")) {
            openLaboratory(screen);
        } else if (PREVIEW.equalsIgnoreCase("basis-icon-laboratory")) {
            openLaboratory(screen);
            focusPaletteAndMove(screen, 22, false);
        } else if (PREVIEW.equalsIgnoreCase("custom-name-explicit")) {
            openLaboratory(screen);
        } else if (PREVIEW.equalsIgnoreCase("text-field-focus")
                || PREVIEW.equalsIgnoreCase("minimum-viewport")) {
            openLaboratory(screen);
            focusCustomName(screen);
        } else if (PREVIEW.equalsIgnoreCase("inscription-pending")
                || PREVIEW.equalsIgnoreCase("inscription-confirmed")) {
            clickInscribe(screen);
        } else if (PREVIEW.equalsIgnoreCase("laboratory")
                || PREVIEW.equalsIgnoreCase("laboratory-binding-tooltip")
                || PREVIEW.equalsIgnoreCase("laboratory-invalid")) {
            openLaboratory(screen);
            if (PREVIEW.equalsIgnoreCase("laboratory-binding-tooltip")) {
                hoverFirstBinding(minecraft, screen);
            } else if (PREVIEW.equalsIgnoreCase("laboratory-invalid")) {
                focusPaletteAndMove(screen, 0, true);
                requireWidgetActive(
                        screen,
                        Component.translatable("screen.mathmod.rune_programmer.undo_custom")
                );
            }
        } else if (PREVIEW.equalsIgnoreCase("frame-theorem")
                || PREVIEW.equalsIgnoreCase("basis-icon-family")
                || PREVIEW.equalsIgnoreCase("theorem-node-tooltip")
                || PREVIEW.equalsIgnoreCase("theorem-formula-tooltip")
                || PREVIEW.equalsIgnoreCase("type-legend-tooltip")) {
            selectFrameTheorem(screen);
            if (PREVIEW.equalsIgnoreCase("theorem-node-tooltip")) {
                hoverSecondTheoremNode(minecraft, screen);
            } else if (PREVIEW.equalsIgnoreCase("theorem-formula-tooltip")) {
                hoverTheoremFormula(minecraft, screen);
            } else if (PREVIEW.equalsIgnoreCase("type-legend-tooltip")) {
                hoverTypeLegend(minecraft, screen);
            }
        }
    }

    private static void requireTheoremCatalogFormulaFit(
            Minecraft minecraft,
            RuneProgrammerScreen screen
    ) {
        int availableWidth = previewLayout(screen).palette().width() - 42;
        List<String> overflowing = ProgramPresets.talismanPresets().stream()
                .filter(preset -> minecraft.font.width(preset.catalogFormula()) > availableWidth)
                .map(preset -> preset.id() + "=" + preset.catalogFormula())
                .toList();
        if (!overflowing.isEmpty()) {
            throw new IllegalStateException(
                    "Theorem catalog formulas exceed " + availableWidth + " px: "
                            + String.join(", ", overflowing)
            );
        }
    }

    private static void requireTheoremStatementFit(
            Minecraft minecraft,
            RuneProgrammerScreen screen
    ) {
        int availableWidth = previewLayout(screen).graph().width() - 41;
        List<String> overflowing = ProgramPresets.talismanPresets().stream()
                .filter(preset -> TheoremStatementPresentation.lines(
                        minecraft.font,
                        preset.formula(),
                        availableWidth
                ).size() > 2)
                .map(preset -> preset.id() + "=" + preset.formula())
                .toList();
        if (!overflowing.isEmpty()) {
            throw new IllegalStateException(
                    "Theorem statements exceed two lines at " + availableWidth + " px: "
                            + String.join(", ", overflowing)
            );
        }
    }

    private static void openLaboratory(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect laboratoryTab = previewLayout.laboratoryTab();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + laboratoryTab.x() + laboratoryTab.width() / 2.0D,
                top + laboratoryTab.y() + laboratoryTab.height() / 2.0D,
                0
        );
    }

    private static void openTheorems(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect theoremTab = previewLayout.theoremTab();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + theoremTab.x() + theoremTab.width() / 2.0D,
                top + theoremTab.y() + theoremTab.height() / 2.0D,
                0
        );
    }

    private static void hoverPresetAction(
            Minecraft minecraft,
            RuneProgrammerScreen screen,
            int actionIndex
    ) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect action = previewLayout.presetActions().get(actionIndex);
        double guiX = (screen.width - previewLayout.width()) / 2.0D
                + action.x()
                + action.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + action.y()
                + action.height() / 2.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverCustomAction(
            Minecraft minecraft,
            RuneProgrammerScreen screen,
            int actionIndex
    ) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect action = previewLayout.customActions().get(actionIndex);
        double guiX = (screen.width - previewLayout.width()) / 2.0D
                + action.x()
                + action.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + action.y()
                + action.height() / 2.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverCustomPaletteBottomRow(
            Minecraft minecraft,
            RuneProgrammerScreen screen
    ) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect palette = previewLayout.palette();
        double guiX = (screen.width - previewLayout.width()) / 2.0D
                + palette.x()
                + palette.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + palette.bottom()
                - 17.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverFirstCustomPaletteRow(
            Minecraft minecraft,
            RuneProgrammerScreen screen
    ) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        double guiX = (screen.width - previewLayout.width()) / 2.0D
                + previewLayout.palette().x()
                + previewLayout.palette().width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + previewLayout.panelTop()
                + 43.0D
                + 16.0D
                + 8.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void clickFirstCustomPaletteRow(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        double guiX = (screen.width - previewLayout.width()) / 2.0D
                + previewLayout.palette().x()
                + previewLayout.palette().width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + previewLayout.panelTop()
                + 43.0D
                + 16.0D
                + 8.0D;
        screen.mouseClicked(guiX, guiY, 0);
    }

    private static void hoverSavedEdit(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect edit = previewLayout.savedActions().getFirst();
        double guiX = (screen.width - previewLayout.width()) / 2.0D + edit.x() + edit.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D + edit.y() + edit.height() / 2.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverSavedReplace(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect replace = previewLayout.savedActions().get(1);
        double guiX = (screen.width - previewLayout.width()) / 2.0D + replace.x() + replace.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D + replace.y() + replace.height() / 2.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverSavedResources(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect resources = previewLayout.savedActions().get(2);
        double guiX = (screen.width - previewLayout.width()) / 2.0D
                + resources.x() + resources.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + resources.y() + resources.height() / 2.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void scrollSavedPaletteToEnd(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect palette = previewLayout.palette();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        double x = left + palette.x() + palette.width() / 2.0D;
        double y = top + palette.y() + palette.height() / 2.0D;
        for (int index = 0; index < 16; index++) {
            screen.mouseScrolled(x, y, 0.0D, -1.0D);
        }
    }

    private static void clickSavedEdit(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect edit = previewLayout.savedActions().getFirst();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + edit.x() + edit.width() / 2.0D,
                top + edit.y() + edit.height() / 2.0D,
                0
        );
    }

    private static void replaceCustomName(RuneProgrammerScreen screen, String name) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect nameBounds = previewLayout.customNameContent();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + nameBounds.x() + nameBounds.width() / 2.0D,
                top + nameBounds.y() + nameBounds.height() / 2.0D,
                0
        );
        screen.keyPressed(GLFW.GLFW_KEY_END, 0, 0);
        for (int index = 0; index < 32; index++) {
            screen.keyPressed(GLFW.GLFW_KEY_BACKSPACE, 0, 0);
        }
        for (char character : name.toCharArray()) {
            screen.charTyped(character, 0);
        }
    }

    private static void focusCustomName(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect nameBounds = previewLayout.customNameContent();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + nameBounds.x() + nameBounds.width() / 2.0D,
                top + nameBounds.y() + nameBounds.height() / 2.0D,
                0
        );
    }

    private static void clickCustomInscribe(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect inscribe = previewLayout.customActions().getFirst();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + inscribe.x() + inscribe.width() / 2.0D,
                top + inscribe.y() + inscribe.height() / 2.0D,
                0
        );
    }

    private static void clickSavedClear(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect clear = previewLayout.savedActions().get(3);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + clear.x() + clear.width() / 2.0D,
                top + clear.y() + clear.height() / 2.0D,
                0
        );
    }

    private static void clickSavedResources(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect resources = previewLayout.savedActions().get(2);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + resources.x() + resources.width() / 2.0D,
                top + resources.y() + resources.height() / 2.0D,
                0
        );
    }

    private static void hoverSavedClear(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect clear = previewLayout.savedActions().get(3);
        double guiX = (screen.width - previewLayout.width()) / 2.0D + clear.x() + clear.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D + clear.y() + clear.height() / 2.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverResourceClear(Minecraft minecraft, TalismanResourcesScreen screen) {
        ResourcesLayout previewLayout = ResourcesLayout.forViewport(screen.width, screen.height, itemOverlayLoaded());
        ProgrammerLayout.Rect clear = previewLayout.actions().get(1);
        double guiX = (screen.width - previewLayout.width()) / 2.0D + clear.x() + clear.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D + clear.y() + clear.height() / 2.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverFirstMaterial(Minecraft minecraft, TalismanResourcesScreen screen) {
        ResourcesLayout previewLayout = ResourcesLayout.forViewport(screen.width, screen.height, itemOverlayLoaded());
        ProgrammerLayout.Rect materials = previewLayout.rightPanel();
        double guiX = (screen.width - previewLayout.width()) / 2.0D
                + materials.x()
                + materials.width() / 2.0D;
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + materials.y()
                + 12.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverProgrammerNotation(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        int symbolWidth = minecraft.font.width("f(x)");
        movePreviewCursor(
                minecraft,
                screen.width,
                screen.height,
                left + previewLayout.width() - 10.0D - symbolWidth / 2.0D,
                top + 8.0D + minecraft.font.lineHeight / 2.0D
        );
    }

    private static void hoverResourcesNotation(Minecraft minecraft, TalismanResourcesScreen screen) {
        ResourcesLayout previewLayout = ResourcesLayout.forViewport(
                screen.width,
                screen.height,
                itemOverlayLoaded()
        );
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        int symbolWidth = minecraft.font.width(Component.translatable(
                "screen.mathmod.talisman_resources.notation.symbol"
        ));
        movePreviewCursor(
                minecraft,
                screen.width,
                screen.height,
                left + previewLayout.width() - 10.0D - symbolWidth / 2.0D,
                top + 8.0D + minecraft.font.lineHeight / 2.0D
        );
    }

    private static void hoverLoadoutName(Minecraft minecraft, TalismanResourcesScreen screen) {
        ProgrammerLayout.Rect name = screen.loadoutNameBounds();
        if (!screen.isLoadoutNameClipped() || name.width() <= 0) {
            throw new IllegalStateException("Long loadout preview did not produce a visible clipped name");
        }
        movePreviewCursor(
                minecraft,
                screen.width,
                screen.height,
                name.x() + name.width() - 2.0D,
                name.y() + name.height() / 2.0D
        );
    }

    private static void focusFirstFunctionalControl(
            net.minecraft.client.gui.screens.Screen screen,
            String surface,
            String expectedContextHelp,
            String... expectedLeadingLabels
    ) {
        if (expectedLeadingLabels.length == 0) {
            throw new IllegalArgumentException("At least one leading focus label is required");
        }
        for (int index = 0; index < expectedLeadingLabels.length; index++) {
            screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
            String expectedLabel = expectedLeadingLabels[index];
            if (!(screen.getFocused() instanceof MathButton focusedButton)) {
                String focused = screen.getFocused() == null
                        ? "none"
                        : screen.getFocused().getClass().getSimpleName();
                throw new IllegalStateException(surface + " Tab " + (index + 1)
                        + " focused " + focused + " instead of a functional control");
            }
            if (!focusedButton.getMessage().getString().equals(expectedLabel)) {
                throw new IllegalStateException(surface + " Tab " + (index + 1) + " focused '"
                        + focusedButton.getMessage().getString() + "' instead of '" + expectedLabel + "'");
            }
        }
        boolean notationReached = false;
        String previousButtonLabel = "";
        for (int step = 0; step < 32; step++) {
            screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
            if (screen.getFocused() != null
                    && screen.getFocused().getClass().getSimpleName().equals("NotationWidget")) {
                if (!previousButtonLabel.equals(expectedContextHelp)) {
                    throw new IllegalStateException(surface + " notation followed '"
                            + previousButtonLabel + "' instead of contextual help '" + expectedContextHelp + "'");
                }
                notationReached = true;
                break;
            }
            if (screen.getFocused() instanceof MathButton focusedButton) {
                previousButtonLabel = focusedButton.getMessage().getString();
            } else {
                previousButtonLabel = "";
            }
        }
        if (!notationReached) {
            throw new IllegalStateException(surface + " notation is missing from the keyboard focus cycle");
        }
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        if (!(screen.getFocused() instanceof MathButton wrappedButton)
                || !wrappedButton.getMessage().getString().equals(expectedLeadingLabels[0])) {
            throw new IllegalStateException(surface + " focus cycle did not return to its first functional control");
        }
    }

    private static void focusFirstResourceTask(TalismanResourcesScreen screen) {
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        if (!resourceListFocused(screen, true)) {
            String focused = screen.getFocused() == null
                    ? "none"
                    : screen.getFocused().getClass().getSimpleName();
            throw new IllegalStateException(
                    "Resources first Tab focused " + focused + " instead of the material catalog"
            );
        }

        String expectedHelp = Component.translatable(
                "screen.mathmod.talisman_resources.help_action"
        ).getString();
        String previousButtonLabel = "";
        boolean notationReached = false;
        for (int step = 0; step < 32; step++) {
            screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
            if (screen.getFocused() != null
                    && screen.getFocused().getClass().getSimpleName().equals("NotationWidget")) {
                if (!previousButtonLabel.equals(expectedHelp)) {
                    throw new IllegalStateException(
                            "Resources notation followed '" + previousButtonLabel
                                    + "' instead of contextual help '" + expectedHelp + "'"
                    );
                }
                notationReached = true;
                break;
            }
            if (screen.getFocused() instanceof MathButton focusedButton) {
                previousButtonLabel = focusedButton.getMessage().getString();
            } else {
                previousButtonLabel = "";
            }
        }
        if (!notationReached) {
            throw new IllegalStateException(
                    "Resources notation is missing from the keyboard focus cycle"
            );
        }

        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        if (!resourceListFocused(screen, true)) {
            throw new IllegalStateException(
                    "Resources focus cycle did not return to the material catalog"
            );
        }
    }

    private static void runResourceAddRemovePreview(
            Minecraft minecraft,
            TalismanResourcesScreen screen
    ) {
        List<ResourceSelection> selections = ProgramResources.get(
                minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
        );
        if (resourceMutationStep == 0 && ticks >= 5) {
            mouseCatalogTargetId = screen.selectedCatalogMaterialId().orElseThrow(
                    () -> new IllegalStateException("Localized material catalog is empty")
            );
            clickFirstMaterial(screen);
            clickFirstMaterial(screen);
            resourceMutationStep = 1;
            return;
        }
        if (resourceMutationStep == 1
                && selections.equals(List.of(new ResourceSelection(mouseCatalogTargetId, 2)))) {
            sawResourceQuantityTwo = true;
            clickFirstSelectedResource(screen);
            resourceMutationStep = 2;
        }
    }

    private static void runResourceClearPreview(
            Minecraft minecraft,
            TalismanResourcesScreen screen,
            boolean executeConfirmation
    ) {
        if (resourceMutationStep == 0 && ticks >= 5) {
            resourceClearInitialSelections = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            );
            if (resourceClearInitialSelections.isEmpty()) {
                throw new IllegalStateException("Resource-clear preview requires prepared materials");
            }
            clickResourceAction(screen, 1);
            resourceMutationStep = 1;
            resourceClearArmedTick = ticks;
            hoverResourceClear(minecraft, screen);
            return;
        }
        if (resourceMutationStep == 1) {
            List<ResourceSelection> selections = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            );
            if (!selections.equals(resourceClearInitialSelections)) {
                throw new IllegalStateException("First resource-clear activation mutated the preparation");
            }
            if (!resourceClearConfirmationVisible(screen)) {
                throw new IllegalStateException("Resource-clear button did not enter its confirmation state");
            }
            hoverResourceClear(minecraft, screen);
            if (executeConfirmation && ticks >= resourceClearArmedTick + 6) {
                clickResourceAction(screen, 1);
                resourceMutationStep = 2;
            }
        }
    }

    private static boolean resourceClearConfirmationVisible(TalismanResourcesScreen screen) {
        String expected = Component.translatable(
                "screen.mathmod.talisman_resources.clear_resources_confirm"
        ).getString();
        return screen.children().stream()
                .filter(MathButton.class::isInstance)
                .map(MathButton.class::cast)
                .anyMatch(button -> button.getMessage().getString().equals(expected));
    }

    private static void runKeyboardAddedMaterialsPreview(TalismanResourcesScreen screen) {
        if (resourceMutationStep == 0 && ticks >= 5) {
            focusResourceListWithTab(screen, false);
            screen.keyPressed(GLFW.GLFW_KEY_HOME, 0, 0);
            screen.keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0);
            resourceMutationStep = 1;
        }
    }

    private static void runKeyboardMaterialCatalogPreview(TalismanResourcesScreen screen) {
        if (resourceMutationStep == 0 && ticks >= 5) {
            focusResourceListWithTab(screen, true);
            screen.keyPressed(GLFW.GLFW_KEY_END, 0, 0);
            keyboardCatalogTargetId = screen.selectedCatalogMaterialId().orElseThrow(
                    () -> new IllegalStateException("Localized material catalog is empty")
            );
            screen.keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0);
            resourceMutationStep = 1;
        }
    }

    private static void runProgrammerScrollbarPreview(
            Minecraft minecraft,
            RuneProgrammerScreen screen
    ) {
        if (scrollbarAuditStep == 0 && ticks >= 5) {
            scrollbarInitialPresetId = selectedPreset(screen).id();
            ProgrammerLayout previewLayout = previewLayout(screen);
            int left = (screen.width - previewLayout.width()) / 2;
            int top = (screen.height - previewLayout.height()) / 2;
            int viewportY = previewLayout.panelTop() + 22;
            int viewportHeight = previewLayout.height()
                    - previewLayout.bottomPadding()
                    - viewportY
                    - 4;
            double trackX = left + previewLayout.palette().x() + previewLayout.palette().width() - 4.0D;
            double trackTop = top + viewportY - 2.0D;
            double trackBottom = trackTop + viewportHeight + 2.0D;
            double pressY = trackTop + (trackBottom - trackTop) / 2.0D;
            screen.mouseClicked(trackX, pressY, 0);
            screen.mouseDragged(trackX, trackBottom - 2.0D, 0, 0.0D, trackBottom - pressY);
            screen.mouseReleased(trackX, trackBottom - 2.0D, 0);
            movePreviewCursor(
                    minecraft,
                    screen.width,
                    screen.height,
                    trackX,
                    trackBottom - 5.0D
            );
            scrollbarAuditStep = 1;
            return;
        }
        if (scrollbarAuditStep == 1 && ticks >= 12) {
            TalismanPreset selected = selectedPreset(screen);
            if (!selected.id().equals(scrollbarInitialPresetId)) {
                throw new IllegalStateException(
                        "Dragging the Theorem scrollbar changed selection from "
                                + scrollbarInitialPresetId
                                + " to "
                                + selected.id()
                );
            }
            if (intField(PRESET_SCROLL_FIELD, screen) <= 0) {
                throw new IllegalStateException("Dragging the Theorem scrollbar did not move its viewport");
            }
            scrollbarAuditStep = 2;
        }
    }

    private static void runResourcesScrollbarPreview(
            Minecraft minecraft,
            TalismanResourcesScreen screen
    ) {
        if (scrollbarAuditStep == 0 && ticks >= 5) {
            scrollbarInitialResources = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            );
            ResourcesLayout previewLayout = ResourcesLayout.forViewport(
                    screen.width,
                    screen.height,
                    itemOverlayLoaded()
            );
            ProgrammerLayout.Rect panel = previewLayout.rightPanel();
            int left = (screen.width - previewLayout.width()) / 2;
            int top = (screen.height - previewLayout.height()) / 2;
            int viewportHeight = panel.height() - panel.height() % 24;
            double trackX = left + panel.x() + panel.width() - 2.0D;
            double trackTop = top + panel.y();
            double trackBottom = trackTop + viewportHeight;
            double pressY = trackTop + viewportHeight / 2.0D;
            screen.mouseClicked(trackX, pressY, 0);
            screen.mouseDragged(trackX, trackBottom - 2.0D, 0, 0.0D, trackBottom - pressY);
            screen.mouseReleased(trackX, trackBottom - 2.0D, 0);
            movePreviewCursor(
                    minecraft,
                    screen.width,
                    screen.height,
                    trackX,
                    trackBottom - 5.0D
            );
            scrollbarAuditStep = 1;
            return;
        }
        if (scrollbarAuditStep == 1 && ticks >= 15) {
            List<ResourceSelection> current = ProgramResources.get(
                    minecraft.player.getItemInHand(InteractionHand.MAIN_HAND)
            );
            if (!current.equals(scrollbarInitialResources)) {
                throw new IllegalStateException(
                        "Dragging the Materials scrollbar mutated preparation: "
                                + scrollbarInitialResources
                                + " -> "
                                + current
                );
            }
            if (intField(MATERIAL_SCROLL_FIELD, screen) <= 0) {
                throw new IllegalStateException("Dragging the Materials scrollbar did not move its viewport");
            }
            scrollbarAuditStep = 2;
        }
    }

    private static void focusResourceListWithTab(
            TalismanResourcesScreen screen,
            boolean materialCatalog
    ) {
        for (int step = 0; step < 16; step++) {
            if (resourceListFocused(screen, materialCatalog)) {
                return;
            }
            screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        }
        throw new IllegalStateException("Resources list did not enter the keyboard focus cycle");
    }

    private static boolean resourceListFocused(
            TalismanResourcesScreen screen,
            boolean materialCatalog
    ) {
        if (!(screen.getFocused() instanceof AbstractWidget widget)
                || !widget.getClass().getSimpleName().equals("ResourceListNavigator")) {
            return false;
        }
        ResourcesLayout previewLayout = ResourcesLayout.forViewport(
                screen.width,
                screen.height,
                itemOverlayLoaded()
        );
        ProgrammerLayout.Rect panel = materialCatalog
                ? previewLayout.rightPanel()
                : previewLayout.leftPanel();
        int expectedX = (screen.width - previewLayout.width()) / 2 + panel.x() - 4;
        return widget.getX() == expectedX;
    }

    private static void clickFirstMaterial(TalismanResourcesScreen screen) {
        ResourcesLayout previewLayout = ResourcesLayout.forViewport(screen.width, screen.height, itemOverlayLoaded());
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        ProgrammerLayout.Rect materials = previewLayout.rightPanel();
        screen.mouseClicked(
                left + materials.x() + materials.width() / 2.0D,
                top + materials.y() + 12.0D,
                0
        );
    }

    private static void clickFirstSelectedResource(TalismanResourcesScreen screen) {
        ResourcesLayout previewLayout = ResourcesLayout.forViewport(screen.width, screen.height, itemOverlayLoaded());
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        ProgrammerLayout.Rect loadout = previewLayout.leftPanel();
        double panelX = left + loadout.x() + loadout.width() / 2.0D;
        double panelY = top + loadout.y() + loadout.height() / 2.0D;
        for (int attempt = 0; attempt < 64; attempt++) {
            var bounds = screen.addedMaterialBounds(0);
            if (bounds.isPresent()) {
                ProgrammerLayout.Rect row = bounds.orElseThrow();
                screen.mouseClicked(
                        row.x() + row.width() / 2.0D,
                        row.y() + row.height() / 2.0D,
                        0
                );
                return;
            }
            screen.mouseScrolled(panelX, panelY, 0.0D, -1.0D);
        }
        throw new IllegalStateException("Added material did not become visible after scrolling");
    }

    private static void clickResourceAction(TalismanResourcesScreen screen, int actionIndex) {
        ResourcesLayout previewLayout = ResourcesLayout.forViewport(screen.width, screen.height, itemOverlayLoaded());
        ProgrammerLayout.Rect action = previewLayout.actions().get(actionIndex);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + action.x() + action.width() / 2.0D,
                top + action.y() + action.height() / 2.0D,
                0
        );
    }

    private static void clickWidget(net.minecraft.client.gui.screens.Screen screen, Component message) {
        for (var child : screen.children()) {
            if (child instanceof AbstractWidget widget
                    && widget.visible
                    && widget.active
                    && widget.getMessage().equals(message)) {
                screen.mouseClicked(
                        widget.getX() + widget.getWidth() / 2.0D,
                        widget.getY() + widget.getHeight() / 2.0D,
                        0
                );
                return;
            }
        }
        throw new IllegalStateException("Could not find preview widget: " + message.getString());
    }

    private static void auditFunctionalInspectorInteraction(Minecraft minecraft, RuneInspectorScreen screen) {
        requireFunctionalContainment(screen);
        String authored = Component.translatable("screen.mathmod.rune_inspector.functional.panel.authored").getString();
        String checked = Component.translatable("screen.mathmod.rune_inspector.functional.panel.checked").getString();
        String graph = Component.translatable("screen.mathmod.rune_inspector.functional.panel.graph").getString();
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        requireNarration(screen, authored, "forward Tab must focus Authored");
        requireFocused(screen, Component.translatable("screen.mathmod.rune_inspector.functional.panel.authored"), "forward Tab authored focus");
        String firstAuthoredRow = screen.getNarrationMessage().getString();
        screen.keyPressed(GLFW.GLFW_KEY_DOWN, 0, 0);
        requireNarration(screen, authored, "arrow navigation must retain Authored row narration");
        if (firstAuthoredRow.equals(screen.getNarrationMessage().getString())) {
            throw new IllegalStateException("Authored row navigation did not advance a multi-row projection");
        }
        screen.keyPressed(GLFW.GLFW_KEY_DOWN, 0, 0);
        requireNarration(screen, "$.arguments[0].expression", "selected row narration must include structural path");
        requireNarration(screen, Component.translatable("screen.mathmod.rune_inspector.functional.row.literal").getString(), "selected row narration must include kind");
        requireNarration(screen, "1", "selected row narration must include displayed value");
        for (int index = 0; index < 16; index++) {
            screen.keyPressed(GLFW.GLFW_KEY_DOWN, 0, 0);
            requireNarration(screen, authored, "Authored scroll navigation must remain bounded");
        }
        String lastAuthoredRow = screen.getNarrationMessage().getString();
        screen.keyPressed(GLFW.GLFW_KEY_DOWN, 0, 0);
        if (!lastAuthoredRow.equals(screen.getNarrationMessage().getString())) {
            throw new IllegalStateException("Authored navigation wrapped past its final row");
        }
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        requireNarration(screen, checked, "forward Tab must focus Checked");
        requireFocused(screen, Component.translatable("screen.mathmod.rune_inspector.functional.panel.checked"), "forward Tab checked focus");
        clickWidget(screen, Component.translatable("screen.mathmod.rune_inspector.functional.panel.checked"));
        requireNarration(screen, checked, "pointer selector must use rendered Checked geometry");
        screen.keyPressed(GLFW.GLFW_KEY_DOWN, 0, 0);
        requireNarration(screen, checked, "arrow navigation must retain Checked row narration");
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        requireNarration(screen, graph, "forward Tab must focus Graph");
        requireFocused(screen, Component.translatable("screen.mathmod.rune_inspector.functional.panel.graph"), "forward Tab graph focus");
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        requireFocused(screen, Component.translatable("screen.mathmod.rune_inspector.close"), "forward Tab close focus");
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        requireFocused(screen, Component.translatable("screen.mathmod.rune_inspector.functional.panel.authored"), "forward cycle must return to Authored");
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 1);
        requireFocused(screen, Component.translatable("screen.mathmod.rune_inspector.close"), "backward cycle must return to Close");
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 1);
        requireFocused(screen, Component.translatable("screen.mathmod.rune_inspector.functional.panel.graph"), "backward cycle must return to Graph");
        screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 1);
        requireNarration(screen, checked, "Shift+Tab must return to Checked");
        screen.keyPressed(GLFW.GLFW_KEY_ESCAPE, 0, 0);
        if (!(minecraft.screen instanceof RuneProgrammerScreen)) {
            throw new IllegalStateException("functional Inspector Escape did not return to its Programmer parent");
        }
    }

    private static void requireFunctionalContainment(RuneInspectorScreen screen) {
        try {
            Method method = RuneInspectorScreen.class.getDeclaredMethod("functionalLayoutContained");
            method.setAccessible(true);
            if (!Boolean.TRUE.equals(method.invoke(screen))) {
                Method diagnostic = RuneInspectorScreen.class.getDeclaredMethod("functionalLayoutDiagnostic");
                diagnostic.setAccessible(true);
                throw new IllegalStateException("functional headings or rows escape the bounded details panel: " + diagnostic.invoke(screen));
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not inspect functional layout containment", exception);
        }
    }

    private static void auditFunctionalNarrationStates() {
        ScopedFunctionalProjection.Row row = new ScopedFunctionalProjection.Row("$.value",
                ScopedFunctionalProjection.RowKind.LITERAL, "1", "number", -1, 0);
        assertFunctionalNarration(new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                        ScopedFunctionalProjection.AttemptState.SUCCESS, ScopedFunctionalProjection.GraphState.PRESENT,
                        ScopedFunctionalProjection.GraphRelation.MISMATCH, List.of(row), List.of(row),
                        List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.MISMATCH,
                                ScopedFunctionalProjection.Code.MISMATCH, "$")), 0),
                "functional.source.current_valid", "functional.relation.mismatch", "functional.diagnostic.mismatch");
        assertFunctionalNarration(unavailableProjection(ScopedFunctionalProjection.SourceState.CONFLICT,
                        ScopedFunctionalProjection.Code.CONFLICT), "functional.source.conflict", "functional.diagnostic.conflict");
        assertFunctionalNarration(unavailableProjection(ScopedFunctionalProjection.SourceState.CURRENT_UNREADABLE,
                        ScopedFunctionalProjection.Code.UNREADABLE), "functional.source.current_unreadable", "functional.diagnostic.unreadable");
        assertFunctionalNarration(unavailableProjection(ScopedFunctionalProjection.SourceState.UNSUPPORTED_VERSION,
                        ScopedFunctionalProjection.Code.UNSUPPORTED), "functional.source.unsupported_version", "functional.diagnostic.unsupported");
        assertFunctionalNarration(ScopedFunctionalProjection.unavailable(ScopedFunctionalProjection.GraphState.PRESENT),
                "functional.source.stale", "functional.diagnostic.stale");
        assertFunctionalNarration(new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                        ScopedFunctionalProjection.AttemptState.SUCCESS, ScopedFunctionalProjection.GraphState.ABSENT,
                        ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE, List.of(row), List.of(row), List.of(), 0),
                "functional.graph.absent", "functional.relation.not_comparable");
    }

    private static ScopedFunctionalProjection unavailableProjection(ScopedFunctionalProjection.SourceState source,
                                                                     ScopedFunctionalProjection.Code code) {
        return new ScopedFunctionalProjection(1, source, ScopedFunctionalProjection.AttemptState.NOT_RUN,
                ScopedFunctionalProjection.GraphState.PRESENT, ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                List.of(), List.of(), List.of(new ScopedFunctionalProjection.Diagnostic(
                ScopedFunctionalProjection.Phase.PERSISTENCE, code, "$")), 0);
    }

    private static void assertFunctionalNarration(ScopedFunctionalProjection projection, String... expectedKeys) {
        String narration = new RuneInspectorScreen(null, ProgramSurface.inscribed(ProgramPresets.hop()).inspect(), projection)
                .getNarrationMessage().getString();
        for (String key : expectedKeys) {
            String expected = Component.translatable("screen.mathmod.rune_inspector." + key).getString();
            if (!narration.contains(expected)) {
                throw new IllegalStateException("functional narration omitted " + key + ": " + narration);
            }
        }
    }

    private static void requireNarration(RuneInspectorScreen screen, String expected, String message) {
        if (!screen.getNarrationMessage().getString().contains(expected)) {
            throw new IllegalStateException(message + ": " + screen.getNarrationMessage().getString());
        }
    }

    private static void requireFocused(RuneInspectorScreen screen, Component expected, String message) {
        if (!(screen.getFocused() instanceof AbstractWidget widget) || !widget.getMessage().equals(expected)) {
            throw new IllegalStateException(message + ": " + (screen.getFocused() == null ? "none" : screen.getFocused().toString()));
        }
    }

    private static void hoverWidget(
            Minecraft minecraft,
            net.minecraft.client.gui.screens.Screen screen,
            Component message
    ) {
        for (var child : screen.children()) {
            if (child instanceof AbstractWidget widget
                    && widget.visible
                    && widget.active
                    && widget.getMessage().equals(message)) {
                movePreviewCursor(
                        minecraft,
                        screen.width,
                        screen.height,
                        widget.getX() + widget.getWidth() / 2.0D,
                        widget.getY() + widget.getHeight() / 2.0D
                );
                return;
            }
        }
        throw new IllegalStateException("Could not hover preview widget: " + message.getString());
    }

    private static void requireWidgetActive(
            net.minecraft.client.gui.screens.Screen screen,
            Component message
    ) {
        for (var child : screen.children()) {
            if (child instanceof AbstractWidget widget
                    && widget.visible
                    && widget.getMessage().equals(message)) {
                if (!widget.active) {
                    throw new IllegalStateException("Preview widget is not active: " + message.getString());
                }
                return;
            }
        }
        throw new IllegalStateException("Could not find preview widget: " + message.getString());
    }

    private static void requireWidgetInactive(
            net.minecraft.client.gui.screens.Screen screen,
        Component message
    ) {
        for (var child : screen.children()) {
            if (child instanceof AbstractWidget widget
                    && widget.visible
                    && widget.getMessage().equals(message)) {
                if (widget.active) {
                    throw new IllegalStateException(
                            "Preview widget should be inactive: " + message.getString()
                    );
                }
                return;
            }
        }
        throw new IllegalStateException("Could not find preview widget: " + message.getString());
    }

    private static void hoverFirstBinding(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        double guiX = left + previewLayout.graph().x() + previewLayout.graph().width() / 2.0D;
        double guiY = top + previewLayout.panelTop() + 24 + 58 + 5;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverSecondTheoremNode(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        double guiX = left + previewLayout.graph().x() + previewLayout.graph().width() / 2.0D;
        double guiY = top + previewLayout.panelTop() + 37 + 16 + 5;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverTypeLegend(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        double guiX = left + previewLayout.graph().x() + previewLayout.graph().width() - 14.0D;
        double guiY = top + previewLayout.panelTop() + 10.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverTheoremFormula(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        double guiX = left + previewLayout.graph().x() + previewLayout.graph().width() / 2.0D;
        double guiY = top + previewLayout.panelTop() + 19.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void hoverWorkflowSeal(Minecraft minecraft, RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        double guiX = left + previewLayout.graph().x() + 13.0D;
        double guiY = top + previewLayout.panelTop() + 9.0D;
        movePreviewCursor(minecraft, screen.width, screen.height, guiX, guiY);
    }

    private static void movePreviewCursor(
            Minecraft minecraft,
            int screenWidth,
            int screenHeight,
            double guiX,
            double guiY
    ) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer windowWidth = stack.mallocInt(1);
            IntBuffer windowHeight = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(minecraft.getWindow().getWindow(), windowWidth, windowHeight);
            double windowX = guiX * windowWidth.get(0) / screenWidth;
            double windowY = guiY * windowHeight.get(0) / screenHeight;
            GLFW.glfwSetCursorPos(minecraft.getWindow().getWindow(), windowX, windowY);
            try {
                MOUSE_MOVE_CALLBACK.invoke(
                        minecraft.mouseHandler,
                        minecraft.getWindow().getWindow(),
                        windowX,
                        windowY
                );
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Could not synchronize the UI preview cursor", exception);
            }
        }
    }

    private static boolean itemOverlayLoaded() {
        ModList modList = ModList.get();
        return modList.isLoaded("jei") || modList.isLoaded("emi") || modList.isLoaded("roughlyenoughitems");
    }

    private static Method mouseMoveCallback() {
        try {
            Method callback = net.minecraft.client.MouseHandler.class.getDeclaredMethod(
                    "onMove",
                    long.class,
                    double.class,
                    double.class
            );
            callback.setAccessible(true);
            return callback;
        } catch (ReflectiveOperationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void searchLaboratory(RuneProgrammerScreen screen, String query) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect search = previewLayout.customSearch();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + search.x() + search.width() / 2.0D,
                top + search.y() + search.height() / 2.0D,
                0
        );
        for (char character : query.toCharArray()) {
            screen.charTyped(character, 0);
        }
    }

    private static void clickCustomReset(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect reset = previewLayout.customActions().get(2);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + reset.x() + reset.width() / 2.0D,
                top + reset.y() + reset.height() / 2.0D,
                0
        );
    }

    private static void runLaboratoryResetPreview(
            Minecraft minecraft,
            RuneProgrammerScreen screen,
            boolean executeConfirmation
    ) {
        if (laboratoryResetStep == 0 && ticks >= 5) {
            openLaboratory(screen);
            laboratoryResetStep = 1;
            laboratoryResetStepTick = ticks;
            return;
        }
        if (laboratoryResetStep == 1 && ticks >= laboratoryResetStepTick + 2) {
            laboratoryResetInitialActions = laboratoryWorkspaceActions(screen);
            laboratoryResetInitialName = laboratoryWorkspaceName(screen);
            if (laboratoryResetInitialActions.isEmpty() || laboratoryResetInitialName.isBlank()) {
                throw new IllegalStateException("Laboratory reset preview requires a named construction");
            }
            clickCustomReset(screen);
            laboratoryResetStep = 2;
            laboratoryResetStepTick = ticks;
            hoverCustomAction(minecraft, screen, 2);
            return;
        }
        if (laboratoryResetStep == 2) {
            if (!laboratoryWorkspaceActions(screen).equals(laboratoryResetInitialActions)
                    || !laboratoryWorkspaceName(screen).equals(laboratoryResetInitialName)) {
                throw new IllegalStateException("First Laboratory reset activation mutated the workspace");
            }
            if (!laboratoryResetConfirmationVisible(screen)) {
                throw new IllegalStateException("Laboratory reset did not expose its confirmation state");
            }
            hoverCustomAction(minecraft, screen, 2);
            if (executeConfirmation && ticks >= laboratoryResetStepTick + 6) {
                clickCustomReset(screen);
                laboratoryResetStep = 3;
                laboratoryResetStepTick = ticks;
            }
        }
    }

    private static boolean laboratoryResetConfirmationVisible(RuneProgrammerScreen screen) {
        String expected = Component.translatable(
                "screen.mathmod.rune_programmer.reset_custom_confirm"
        ).getString();
        return screen.children().stream()
                .filter(MathButton.class::isInstance)
                .map(MathButton.class::cast)
                .anyMatch(button -> button.getMessage().getString().equals(expected));
    }

    private static List<CustomSpellAction> laboratoryWorkspaceActions(RuneProgrammerScreen screen) {
        if (CUSTOM_WORKSPACE_FIELD == null) {
            throw new IllegalStateException("Could not inspect the Laboratory workspace");
        }
        try {
            return ((CustomSpellWorkspace) CUSTOM_WORKSPACE_FIELD.get(screen)).actions();
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Could not read the Laboratory workspace", exception);
        }
    }

    private static String laboratoryWorkspaceName(RuneProgrammerScreen screen) {
        if (CUSTOM_SPELL_NAME_FIELD == null) {
            throw new IllegalStateException("Could not inspect the Laboratory spell name");
        }
        try {
            return (String) CUSTOM_SPELL_NAME_FIELD.get(screen);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Could not read the Laboratory spell name", exception);
        }
    }

    private static void focusPaletteAndMove(RuneProgrammerScreen screen, int rows, boolean activate) {
        focusPaletteWithTab(screen);
        for (int index = 0; index < rows; index++) {
            screen.keyPressed(GLFW.GLFW_KEY_DOWN, 0, 0);
        }
        if (activate) {
            screen.keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0);
        }
    }

    private static boolean authoringRegistryPalettePreview() {
        return PREVIEW.equalsIgnoreCase("authoring-registry-palette");
    }

    private static void runAuthoringRegistryPreview(RuneProgrammerScreen screen) {
        if (authoringRegistryPreviewStep == 0 && ticks >= 5) {
            focusPaletteAndMove(screen, 0, true);
            authoringRegistryPreviewStep = 1;
            authoringRegistryPreviewStepTick = ticks;
            return;
        }
        if (authoringRegistryPreviewStep == 1 && ticks >= authoringRegistryPreviewStepTick + 2) {
            if (!laboratoryWorkspaceActions(screen).equals(List.of(CustomSpellAction.SELF))) {
                throw new IllegalStateException("Keyboard Enter did not apply the expected non-parameterized Self form");
            }
            searchLaboratory(screen, "simpson");
            if (firstCustomPaletteAction(screen) != CustomSpellAction.SIMPSON_INTEGRAL) {
                throw new IllegalStateException("Laboratory search did not filter Simpson as the first registry form");
            }
            authoringRegistryPreviewStep = 2;
            authoringRegistryPreviewStepTick = ticks;
            return;
        }
        if (authoringRegistryPreviewStep == 2 && ticks >= authoringRegistryPreviewStepTick + 2) {
            clickFirstCustomPaletteRow(screen);
            requireSimpsonParameterDialog(screen);
            authoringRegistryPreviewStep = 3;
        }
    }

    private static CustomSpellAction firstCustomPaletteAction(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        double guiY = (screen.height - previewLayout.height()) / 2.0D
                + previewLayout.panelTop()
                + 43.0D
                + 16.0D
                + 8.0D;
        try {
            Method actionAt = RuneProgrammerScreen.class.getDeclaredMethod("actionAt", double.class);
            actionAt.setAccessible(true);
            return (CustomSpellAction) actionAt.invoke(screen, guiY);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not inspect the filtered Laboratory palette", exception);
        }
    }

    private static void requireSimpsonParameterDialog(RuneProgrammerScreen screen) {
        if (PARAMETER_ACTION_FIELD == null || PARAMETER_BOXES_FIELD == null) {
            throw new IllegalStateException("Could not inspect the Laboratory parameter dialog");
        }
        try {
            if (PARAMETER_ACTION_FIELD.get(screen) != CustomSpellAction.SIMPSON_INTEGRAL) {
                throw new IllegalStateException("Pointer activation did not open the Simpson descriptor dialog");
            }
            List<?> boxes = (List<?>) PARAMETER_BOXES_FIELD.get(screen);
            List<EditBox> visible = boxes.stream().filter(EditBox.class::isInstance)
                    .map(EditBox.class::cast).filter(box -> box.visible).toList();
            if (visible.size() != 5 || visible.stream().anyMatch(box -> !isFiniteNumericDefault(box.getValue()))) {
                throw new IllegalStateException("Simpson descriptor dialog did not retain valid numeric defaults");
            }
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Could not read the Laboratory parameter dialog", exception);
        }
    }

    private static boolean isFiniteNumericDefault(String value) {
        try {
            return Double.isFinite(Double.parseDouble(value)) && !value.contains("simpson");
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private static void focusPaletteWithTab(RuneProgrammerScreen screen) {
        for (int step = 0; step < 24; step++) {
            if (screen.getFocused() != null
                    && screen.getFocused().getClass().getSimpleName().equals("PaletteNavigator")) {
                return;
            }
            screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        }
        throw new IllegalStateException("Rune palette did not enter the keyboard focus cycle");
    }

    private static void focusTheoremStatement(RuneProgrammerScreen screen) {
        for (int step = 0; step < 24; step++) {
            if (screen.getFocused() != null
                    && screen.getFocused().getClass().getSimpleName().equals("TheoremStatementWidget")) {
                return;
            }
            screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0);
        }
        throw new IllegalStateException("Theorem statement did not enter the keyboard focus cycle");
    }

    private static void clickInscribe(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect inscribe = previewLayout.presetActions().getFirst();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + inscribe.x() + inscribe.width() / 2.0D,
                top + inscribe.y() + inscribe.height() / 2.0D,
                0
        );
    }

    private static void clickFirstTheorem(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        ProgrammerLayout.Rect palette = previewLayout.palette();
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        screen.mouseClicked(
                left + palette.x() + palette.width() / 2.0D,
                top + palette.y() + 22.0D + 16.0D + 15.0D,
                0
        );
    }

    private static void runFirstSpellPreview(Minecraft minecraft) {
        firstSpellStepTicks++;
        ItemStack stack = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
        ProgramGraph expectedGraph = firstSpellJourneyGraph();

        if (firstSpellStep == 0 && minecraft.screen instanceof RuneProgrammerScreen screen) {
            if (ProgramStorage.get(stack).isPresent()) {
                throw new IllegalStateException("First-spell journey did not start with a blank talisman");
            }
            if (firstSpellStepTicks >= 5) {
                if (advancedHarmonicJourney()) {
                    focusPaletteAndMove(screen, 7, true);
                } else if (alchemicalVitalInfusionJourney()) {
                    focusPaletteAndMove(screen, 25, true);
                } else if (metamagicParsimonyJourney()) {
                    focusPaletteAndMove(screen, 27, true);
                } else if (metamagicConservationJourney()) {
                    focusPaletteAndMove(screen, 28, true);
                } else {
                    clickFirstTheorem(screen);
                }
                advanceFirstSpellStep();
            }
            return;
        }

        if (firstSpellStep == 1 && minecraft.screen instanceof RuneProgrammerScreen screen) {
            if (firstSpellStepTicks >= 2) {
                clickInscribe(screen);
                advanceFirstSpellStep();
            }
            return;
        }

        if (firstSpellStep == 2 && minecraft.screen instanceof RuneProgrammerScreen screen) {
            boolean proofSynchronized = ProgramStorage.get(stack)
                    .filter(expectedGraph::equals)
                    .isPresent();
            if (proofSynchronized && firstSpellStepTicks >= 8) {
                if (PREVIEW.equalsIgnoreCase("first-spell-inscribed")) {
                    minecraft.getToasts().clear();
                    captureReady = true;
                    return;
                }
                if (PREVIEW.equalsIgnoreCase("first-spell-close-tooltip")) {
                    hoverWidget(
                            minecraft,
                            screen,
                            Component.translatable("screen.mathmod.rune_programmer.close_action")
                    );
                    minecraft.getToasts().clear();
                    captureReady = true;
                    return;
                }
                if (PREVIEW.equalsIgnoreCase("first-spell-cast")
                        || advancedHarmonicJourney()
                        || alchemicalVitalInfusionJourney()) {
                    clickWidget(
                            screen,
                            Component.translatable("screen.mathmod.rune_programmer.close_action")
                    );
                    firstSpellStep = 4;
                    firstSpellStepTicks = 0;
                    return;
                }
                clickSavedResources(screen);
                advanceFirstSpellStep();
                return;
            }
            failFirstSpellStepAfterTimeout("Proof inscription did not synchronize");
            return;
        }

        if (firstSpellStep == 3 && minecraft.screen instanceof TalismanResourcesScreen screen) {
            String expectedProofName = Component.translatable(
                    ProgramPresets.presetForGraph(expectedGraph).orElseThrow().nameKey()
            ).getString();
            if (!screen.displayedProofName().getString().equals(expectedProofName)) {
                failFirstSpellStepAfterTimeout("Resource screen did not identify the selected proof");
                return;
            }
            List<ResourceSelection> expected = ProgramResources.recommendedFor(expectedGraph);
            if (!ProgramResources.get(stack).equals(expected)) {
                failFirstSpellStepAfterTimeout("Recommended first-spell preparation did not synchronize");
                return;
            }
            if (!PlayerProgramCosts.planFor(
                    minecraft.player,
                    expectedGraph,
                    ProgramResources.get(stack)
            ).success()) {
                failFirstSpellStepAfterTimeout("First-spell preparation never became cast-ready");
                return;
            }
            if (firstSpellStepTicks < 8) {
                return;
            }
            if (PREVIEW.equalsIgnoreCase("first-spell-ready")) {
                minecraft.getToasts().clear();
                captureReady = true;
                return;
            }
            clickResourceAction(screen, 2);
            advanceFirstSpellStep();
            return;
        }

        if (firstSpellStep == 4 && minecraft.screen == null) {
            if (ProgramStorage.get(stack).filter(expectedGraph::equals).isEmpty()) {
                throw new IllegalStateException("Closing the Programmer changed the proof inscription");
            }
            castFirstSpell(minecraft);
            advanceFirstSpellStep();
            return;
        }

        if (firstSpellStep == 5) {
            if (!firstSpellFailure.isEmpty()) {
                throw new IllegalStateException(firstSpellFailure);
            }
            if (firstSpellCastComplete
                    && (alchemicalVitalInfusionJourney() || metamagicJourney()
                    || minecraft.player.getInventory().countItem(Items.FEATHER) == 0)
                    && (!advancedHarmonicJourney()
                    || minecraft.player.getInventory().countItem(Items.QUARTZ) == 1)
                    && (!alchemicalVitalInfusionJourney()
                    || minecraft.player.getInventory().countItem(ModItems.HOMUNCULAR_MATRIX.get()) == 1)
                    && (!alchemicalVitalInfusionJourney()
                    || minecraft.player.getInventory().countItem(ModItems.VITAL_SALT.get()) == 0)
                    && (!metamagicParsimonyJourney()
                    || minecraft.player.getInventory().countItem(ModItems.AXIOMATIC_INK.get()) == 1)
                    && (!metamagicConservationJourney()
                    || minecraft.player.getInventory().countItem(ModItems.RECURSIVE_SEAL.get()) == 2)
                    && firstSpellStepTicks >= 6) {
                minecraft.getToasts().clear();
                captureReady = true;
                frameReady = true;
                return;
            }
        }

        failFirstSpellStepAfterTimeout("First-spell journey stalled at step " + firstSpellStep);
    }

    private static void advanceFirstSpellStep() {
        firstSpellStep++;
        firstSpellStepTicks = 0;
    }

    private static void failFirstSpellStepAfterTimeout(String message) {
        if (firstSpellStepTicks >= 120) {
            throw new IllegalStateException(message);
        }
    }

    private static void runAnchorJourneyPreview(Minecraft minecraft) {
        anchorJourneyStepTicks++;
        if (!anchorJourneyFailure.isEmpty()) {
            throw new IllegalStateException(anchorJourneyFailure);
        }
        if (!anchorJourneyActionSent) {
            dispatchAnchorJourneyStep(minecraft, anchorJourneyStep);
            anchorJourneyActionSent = true;
            return;
        }

        Component expected = expectedAnchorJourneyMessage(anchorJourneyStep);
        Component actual = overlayMessage(minecraft);
        boolean serverComplete = anchorJourneyServerStep >= anchorJourneyStep;
        boolean messageMatches = actual != null && actual.getString().equals(expected.getString());
        if (!serverComplete || !messageMatches) {
            if (anchorJourneyStepTicks >= 160) {
                String actualText = actual == null ? "<none>" : actual.getString();
                throw new IllegalStateException(
                        "Anchor journey stalled at step " + anchorJourneyStep
                                + "; expected '" + expected.getString()
                                + "', saw '" + actualText + "'"
                );
            }
            return;
        }

        MathMod.LOGGER.info(
                "Anchor journey step {} confirmed: {}",
                anchorJourneyStep,
                actual.getString()
        );
        if (anchorJourneyStep == 7) {
            minecraft.getToasts().clear();
            captureReady = true;
            frameReady = true;
            return;
        }

        anchorJourneyStep++;
        anchorJourneyStepTicks = 0;
        anchorJourneyActionSent = false;
    }

    private static Component expectedAnchorJourneyMessage(int step) {
        Component sacrificePulse = Component.translatable(AnchorProgramPreset.SACRIFICE_PULSE.displayNameKey());
        return switch (step) {
            case 0 -> Component.translatable("item.mathmod.chalk.mode_changed", sacrificePulse);
            case 1 -> Component.translatable(AnchorProgramPreset.SACRIFICE_PULSE.saveMessageKey());
            case 2 -> Component.translatable("block.mathmod.rune_anchor.status", sacrificePulse);
            case 3 -> Component.translatable("block.mathmod.rune_anchor.missing_sacrifice");
            case 4 -> Component.translatable("block.mathmod.rune_anchor.executed");
            case 5 -> Component.translatable("item.mathmod.chalk.anchor_cleared");
            case 6 -> Component.translatable("item.mathmod.chalk.anchor_clear_empty");
            case 7 -> Component.translatable("block.mathmod.rune_anchor.status_empty");
            default -> throw new IllegalArgumentException("Unknown anchor journey step " + step);
        };
    }

    private static void dispatchAnchorJourneyStep(Minecraft minecraft, int step) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            anchorJourneyFailure = "Integrated server was unavailable for the anchor journey";
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            try {
                ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
                if (serverPlayer == null) {
                    throw new IllegalStateException("Server player disappeared during the anchor journey");
                }
                performAnchorJourneyStep(serverPlayer, step);
                anchorJourneyServerStep = step;
            } catch (RuntimeException exception) {
                anchorJourneyFailure = "Anchor journey step " + step + " failed: " + exception.getMessage();
                MathMod.LOGGER.error(anchorJourneyFailure, exception);
            }
        });
    }

    private static void performAnchorJourneyStep(ServerPlayer serverPlayer, int step) {
        switch (step) {
            case 0 -> setUpAnchorJourney(serverPlayer);
            case 1 -> inscribeAnchorJourney(serverPlayer);
            case 2 -> useAnchor(serverPlayer, ItemStack.EMPTY, true);
            case 3 -> {
                useAnchor(serverPlayer, ItemStack.EMPTY, false);
                requireAnchor(serverPlayer);
            }
            case 4 -> enactAnchorJourney(serverPlayer);
            case 5 -> eraseAnchorJourney(serverPlayer);
            case 6 -> {
                useAnchor(serverPlayer, chalkFor(AnchorProgramPreset.SACRIFICE_PULSE), true);
                RuneAnchorBlockEntity anchor = requireAnchor(serverPlayer);
                if (anchor.hasProgram()) {
                    throw new IllegalStateException("No-op erasure restored an anchor inscription");
                }
            }
            case 7 -> {
                useAnchor(serverPlayer, ItemStack.EMPTY, true);
                RuneAnchorBlockEntity anchor = requireAnchor(serverPlayer);
                if (anchor.hasProgram()) {
                    throw new IllegalStateException("Empty inspection found a remaining inscription");
                }
                serverPlayer.setItemInHand(
                        InteractionHand.MAIN_HAND,
                        chalkFor(AnchorProgramPreset.SACRIFICE_PULSE)
                );
                serverPlayer.inventoryMenu.broadcastChanges();
            }
            default -> throw new IllegalArgumentException("Unknown anchor journey step " + step);
        }
    }

    private static void setUpAnchorJourney(ServerPlayer serverPlayer) {
        Direction facing = serverPlayer.getDirection();
        BlockPos playerPos = serverPlayer.blockPosition();
        BlockPos pos = playerPos.relative(facing, 2).above();
        for (int distance = 1; distance <= 2; distance++) {
            for (int height = 0; height <= 2; height++) {
                serverPlayer.serverLevel().setBlockAndUpdate(
                        playerPos.relative(facing, distance).above(height),
                        Blocks.AIR.defaultBlockState()
                );
            }
        }
        serverPlayer.serverLevel().setBlockAndUpdate(pos, ModBlocks.RUNE_ANCHOR.get().defaultBlockState());
        anchorJourneyPos = pos;

        Vec3 center = Vec3.atCenterOf(pos);
        AABB cleanupBounds = new AABB(center, center).inflate(3.0D);
        serverPlayer.serverLevel()
                .getEntitiesOfClass(ItemEntity.class, cleanupBounds)
                .forEach(ItemEntity::discard);

        RuneAnchorBlockEntity anchor = requireAnchor(serverPlayer);
        if (anchor.hasProgram()) {
            throw new IllegalStateException("Fresh preview anchor already had an inscription");
        }

        ItemStack chalk = chalkFor(AnchorProgramPreset.ANCHOR_PULSE);
        serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, chalk);
        serverPlayer.setShiftKeyDown(false);
        serverPlayer.gameMode.useItem(
                serverPlayer,
                serverPlayer.level(),
                chalk,
                InteractionHand.MAIN_HAND
        );
        if (ChalkPresetStorage.get(chalk) != AnchorProgramPreset.SACRIFICE_PULSE) {
            throw new IllegalStateException("Normal chalk use did not cycle to Sacrifice Pulse");
        }
        serverPlayer.inventoryMenu.broadcastChanges();
    }

    private static void inscribeAnchorJourney(ServerPlayer serverPlayer) {
        ItemStack chalk = serverPlayer.getItemInHand(InteractionHand.MAIN_HAND);
        if (!chalk.is(ModItems.CHALK.get())
                || ChalkPresetStorage.get(chalk) != AnchorProgramPreset.SACRIFICE_PULSE) {
            throw new IllegalStateException("Sacrifice Pulse chalk did not survive theorem cycling");
        }
        useAnchor(serverPlayer, chalk, false);

        RuneAnchorBlockEntity anchor = requireAnchor(serverPlayer);
        if (!anchor.hasProgram()
                || anchor.programPreset().orElse(null) != AnchorProgramPreset.SACRIFICE_PULSE) {
            throw new IllegalStateException("Anchor did not retain the inscribed theorem identity");
        }
        var level = serverPlayer.serverLevel();
        var registries = level.registryAccess();
        var persisted = anchor.saveWithFullMetadata(registries);
        String persistedPreset = persisted.getString("program_preset");
        if (!persistedPreset.equals(AnchorProgramPreset.SACRIFICE_PULSE.id())) {
            throw new IllegalStateException("Anchor NBT did not persist the Sacrifice Pulse identity");
        }

        BlockEntity loaded = BlockEntity.loadStatic(
                anchorJourneyPos,
                level.getBlockState(anchorJourneyPos),
                persisted,
                registries
        );
        if (!(loaded instanceof RuneAnchorBlockEntity reloaded)) {
            throw new IllegalStateException("Persisted Rune Anchor did not recreate its block entity");
        }
        level.removeBlockEntity(anchorJourneyPos);
        level.setBlockEntity(reloaded);
        if (reloaded == anchor
                || !reloaded.hasProgram()
                || reloaded.programPreset().orElse(null) != AnchorProgramPreset.SACRIFICE_PULSE
                || reloaded.program().filter(AnchorProgramPreset.SACRIFICE_PULSE.graph()::equals).isEmpty()) {
            throw new IllegalStateException("Reloaded Rune Anchor lost its proof or theorem identity");
        }
    }

    private static void enactAnchorJourney(ServerPlayer serverPlayer) {
        RuneAnchorBlockEntity anchor = requireAnchor(serverPlayer);
        if (anchor.programPreset().orElse(null) != AnchorProgramPreset.SACRIFICE_PULSE) {
            throw new IllegalStateException("Anchor theorem identity changed before enactment");
        }

        Vec3 center = Vec3.atCenterOf(anchorJourneyPos);
        ItemEntity witness = new ItemEntity(
                serverPlayer.serverLevel(),
                center.x,
                center.y + 0.75D,
                center.z,
                new ItemStack(Items.AMETHYST_SHARD)
        );
        if (!serverPlayer.serverLevel().addFreshEntity(witness)) {
            throw new IllegalStateException("Could not place the sacrifice witness");
        }
        useAnchor(serverPlayer, ItemStack.EMPTY, false);
        if (!witness.isRemoved() && !witness.getItem().isEmpty()) {
            throw new IllegalStateException("Successful anchor proof did not consume its witness");
        }
        if (!anchor.hasProgram()) {
            throw new IllegalStateException("Enactment unexpectedly erased the anchor inscription");
        }
    }

    private static void eraseAnchorJourney(ServerPlayer serverPlayer) {
        useAnchor(serverPlayer, chalkFor(AnchorProgramPreset.SACRIFICE_PULSE), true);
        RuneAnchorBlockEntity anchor = requireAnchor(serverPlayer);
        if (anchor.hasProgram()
                || anchor.saveWithoutMetadata(serverPlayer.serverLevel().registryAccess())
                .contains("program")) {
            throw new IllegalStateException("Sneak-use with chalk did not erase the persisted inscription");
        }
    }

    private static void useAnchor(ServerPlayer serverPlayer, ItemStack heldItem, boolean secondaryUse) {
        BlockPos pos = anchorJourneyPos;
        if (pos == null) {
            throw new IllegalStateException("Anchor journey position was not initialized");
        }
        serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, heldItem);
        serverPlayer.setShiftKeyDown(secondaryUse);
        try {
            BlockHitResult hit = new BlockHitResult(
                    Vec3.atCenterOf(pos),
                    Direction.UP,
                    pos,
                    false
            );
            serverPlayer.gameMode.useItemOn(
                    serverPlayer,
                    serverPlayer.level(),
                    heldItem,
                    InteractionHand.MAIN_HAND,
                    hit
            );
        } finally {
            serverPlayer.setShiftKeyDown(false);
        }
        serverPlayer.inventoryMenu.broadcastChanges();
    }

    private static ItemStack chalkFor(AnchorProgramPreset preset) {
        ItemStack chalk = new ItemStack(ModItems.CHALK.get());
        ChalkPresetStorage.set(chalk, preset);
        return chalk;
    }

    private static RuneAnchorBlockEntity requireAnchor(ServerPlayer serverPlayer) {
        BlockPos pos = anchorJourneyPos;
        if (pos == null
                || !(serverPlayer.serverLevel().getBlockEntity(pos) instanceof RuneAnchorBlockEntity anchor)) {
            throw new IllegalStateException("Rune Anchor block entity was unavailable");
        }
        return anchor;
    }

    private static Component overlayMessage(Minecraft minecraft) {
        if (OVERLAY_MESSAGE_FIELD == null) {
            throw new IllegalStateException("Could not inspect the client action-bar message");
        }
        try {
            return (Component) OVERLAY_MESSAGE_FIELD.get(minecraft.gui);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Could not read the client action-bar message", exception);
        }
    }

    private static Field overlayMessageField() {
        try {
            Field field = Gui.class.getDeclaredField("overlayMessageString");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            MathMod.LOGGER.error("Could not prepare action-bar inspection for UI previews", exception);
            return null;
        }
    }

    private static Field programmerField(String name) {
        try {
            Field field = RuneProgrammerScreen.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            MathMod.LOGGER.error("Could not prepare Programmer field inspection for {}", name, exception);
            return null;
        }
    }

    private static Field resourcesField(String name) {
        try {
            Field field = TalismanResourcesScreen.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            MathMod.LOGGER.error("Could not prepare Resources field inspection for {}", name, exception);
            return null;
        }
    }

    private static int intField(Field field, Object target) {
        if (field == null) {
            throw new IllegalStateException("Preview integer field was unavailable");
        }
        try {
            return field.getInt(target);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not inspect preview integer field", exception);
        }
    }

    private static TalismanPreset selectedPreset(RuneProgrammerScreen screen) {
        if (SELECTED_PRESET_FIELD == null) {
            throw new IllegalStateException("Selected Theorem preview field was unavailable");
        }
        try {
            return (TalismanPreset) SELECTED_PRESET_FIELD.get(screen);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not inspect selected Theorem", exception);
        }
    }

    private static void castFirstSpell(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            firstSpellFailure = "Integrated server was unavailable for the first cast";
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                firstSpellFailure = "Server player disappeared before the first cast";
                return;
            }
            ItemStack talisman = serverPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            int feathersBefore = serverPlayer.getInventory().countItem(Items.FEATHER);
            int quartzBefore = serverPlayer.getInventory().countItem(Items.QUARTZ);
            int matrixBefore = serverPlayer.getInventory().countItem(ModItems.HOMUNCULAR_MATRIX.get());
            int vitalSaltBefore = serverPlayer.getInventory().countItem(ModItems.VITAL_SALT.get());
            int inkBefore = serverPlayer.getInventory().countItem(ModItems.AXIOMATIC_INK.get());
            int sealsBefore = serverPlayer.getInventory().countItem(ModItems.RECURSIVE_SEAL.get());
            double maxHealthBefore = serverPlayer.getMaxHealth();
            Vec3 movementBefore = serverPlayer.getDeltaMovement();
            var costPlan = PlayerProgramCosts.planFor(
                    serverPlayer,
                    firstSpellJourneyGraph(),
                    ProgramResources.get(talisman)
            );
            if (!costPlan.success()) {
                firstSpellFailure = "First-spell cast was not resource-ready: "
                        + costPlan.messageKey()
                        + ", items=" + costPlan.missingItems()
                        + ", attributes=" + costPlan.missingAttributes()
                        + ", budget=" + costPlan.budgetUsed() + "/" + costPlan.effectiveBudgetLimit()
                        + ", selections=" + ProgramResources.get(talisman);
                return;
            }
            useHeldTalisman(serverPlayer, false);
            int feathersAfter = serverPlayer.getInventory().countItem(Items.FEATHER);
            int quartzAfter = serverPlayer.getInventory().countItem(Items.QUARTZ);
            int matrixAfter = serverPlayer.getInventory().countItem(ModItems.HOMUNCULAR_MATRIX.get());
            int vitalSaltAfter = serverPlayer.getInventory().countItem(ModItems.VITAL_SALT.get());
            int inkAfter = serverPlayer.getInventory().countItem(ModItems.AXIOMATIC_INK.get());
            int sealsAfter = serverPlayer.getInventory().countItem(ModItems.RECURSIVE_SEAL.get());
            Vec3 movementAfter = serverPlayer.getDeltaMovement();
            if (!alchemicalVitalInfusionJourney() && !metamagicJourney()
                    && (feathersBefore != 1 || feathersAfter != 0)) {
                firstSpellFailure = "Normal talisman use did not consume exactly one feather";
                return;
            }
            if (metamagicParsimonyJourney()) {
                if (inkBefore != 2 || inkAfter != 0) {
                    firstSpellFailure = "Parsimony financed its own cast instead of consuming two Axiomatic Ink";
                    return;
                }
                if (serverPlayer.getEffect(ModMobEffects.PARSIMONY) == null) {
                    firstSpellFailure = "Normal talisman use did not apply Parsimony";
                    return;
                }
                serverPlayer.getInventory().add(new ItemStack(ModItems.AXIOMATIC_INK.get(), 1));
                var nextPlan = PlayerProgramCosts.planFor(
                        serverPlayer,
                        ProgramPresets.axiomOfParsimony(),
                        List.of(new ResourceSelection("axiomatic_ink", 1))
                );
                if (!nextPlan.success()
                        || nextPlan.attributeRequirements().getOrDefault("metamagic", 0) != 3
                        || nextPlan.attributeRequirements().getOrDefault("economy", 0) != 3) {
                    firstSpellFailure = "Active Parsimony did not discount the following cast";
                    return;
                }
            } else if (metamagicConservationJourney()) {
                if (sealsBefore != 2 || sealsAfter != 2) {
                    firstSpellFailure = "Conservation did not preserve its Recursive Seal catalysts";
                    return;
                }
                if (serverPlayer.getEffect(ModMobEffects.CONSERVATION) == null) {
                    firstSpellFailure = "Normal talisman use did not apply Conservation";
                    return;
                }
            } else if (alchemicalVitalInfusionJourney()) {
                if (matrixBefore != 1 || matrixAfter != 1) {
                    firstSpellFailure = "Vital Infusion did not preserve its Homuncular Matrix catalyst";
                    return;
                }
                if (vitalSaltBefore != 2 || vitalSaltAfter != 0) {
                    firstSpellFailure = "Vital Infusion did not consume exactly two Vital Salt";
                    return;
                }
                if (serverPlayer.getEffect(ModMobEffects.VITAL_INFUSION) == null) {
                    firstSpellFailure = "Normal talisman use did not apply the Vital Infusion effect";
                    return;
                }
                if (serverPlayer.getMaxHealth() <= maxHealthBefore) {
                    firstSpellFailure = "Vital Infusion was present but did not increase maximum health from "
                            + maxHealthBefore + " (after " + serverPlayer.getMaxHealth() + ")";
                    return;
                }
            } else if (advancedHarmonicJourney()) {
                if (quartzBefore != 1 || quartzAfter != 1) {
                    firstSpellFailure = "The Harmonic Step did not preserve its quartz catalyst";
                    return;
                }
                Vec3 impulse = movementAfter.subtract(movementBefore);
                if (Math.hypot(impulse.x, impulse.z) < 0.4D) {
                    firstSpellFailure = "Normal talisman use did not apply the Harmonic Step movement";
                    return;
                }
            } else if (movementAfter.y < movementBefore.y + 0.3D) {
                firstSpellFailure = "Normal talisman use did not apply the Hop movement";
                return;
            }
            firstSpellCastComplete = true;
        });
    }

    private static void selectFrameTheorem(RuneProgrammerScreen screen) {
        ProgrammerLayout previewLayout = previewLayout(screen);
        int left = (screen.width - previewLayout.width()) / 2;
        int top = (screen.height - previewLayout.height()) / 2;
        ProgrammerLayout.Rect theoremTab = previewLayout.theoremTab();
        screen.mouseClicked(
                left + theoremTab.x() + theoremTab.width() / 2.0D,
                top + theoremTab.y() + theoremTab.height() / 2.0D,
                0
        );

        ProgrammerLayout.Rect palette = previewLayout.palette();
        double paletteX = left + palette.x() + palette.width() / 2.0D;
        double paletteY = top + palette.y() + 32.0D;
        for (int i = 0; i < 4; i++) {
            screen.mouseScrolled(paletteX, paletteY, 0.0D, -1.0D);
        }
        screen.mouseClicked(paletteX, top + palette.y() + 53.0D, 0);
    }

    private static ProgrammerLayout previewLayout(RuneProgrammerScreen screen) {
        ModList modList = ModList.get();
        boolean itemOverlay = modList.isLoaded("jei")
                || modList.isLoaded("emi")
                || modList.isLoaded("roughlyenoughitems");
        return ProgrammerLayout.forViewport(screen.width, screen.height, itemOverlay);
    }

    private static ItemStack previewTalisman() {
        ItemStack talisman = new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
        if (longLoadoutNamePreview()) {
            if (MAX_LENGTH_PROOF_NAME.length() != ProgramNames.MAX_LENGTH) {
                throw new IllegalStateException("Long loadout preview name must exercise the persisted name limit");
            }
            ProgramStorage.saveValidated(talisman, ProgramPresets.vectorWave(), MAX_LENGTH_PROOF_NAME);
            if (!ProgramStorage.getName(talisman).filter(MAX_LENGTH_PROOF_NAME::equals).isPresent()) {
                throw new IllegalStateException("Long loadout preview name did not survive persistence");
            }
        } else if (PREVIEW.equalsIgnoreCase("resources-clear-tooltip")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.vectorWave());
            ProgramResources.set(talisman, List.of(
                    new ResourceSelection(firstMaterialId(), 2),
                    new ResourceSelection("diamond", 1)
            ));
        } else if (PREVIEW.equalsIgnoreCase("resources")
                || resourceHelpPreview()
                || resourceBackPreview()
                || PREVIEW.equalsIgnoreCase("minimum-resources")
                || PREVIEW.equalsIgnoreCase("resources-material-tooltip")
                || PREVIEW.equalsIgnoreCase("resources-notation-tooltip")
                || PREVIEW.equalsIgnoreCase("keyboard-first-resources")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.vectorWave());
        } else if (PREVIEW.equalsIgnoreCase("keyboard-added-materials")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.vectorWave());
            ProgramResources.set(talisman, List.of(new ResourceSelection(firstMaterialId(), 2)));
        } else if (PREVIEW.equalsIgnoreCase("keyboard-material-catalog")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.vectorWave());
            ProgramResources.clear(talisman);
        } else if (PREVIEW.equalsIgnoreCase("resources-scrollbar-drag")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.vectorWave());
            ProgramResources.set(
                    talisman,
                    List.of(new ResourceSelection(firstMaterialId(), 1))
            );
        } else if (PREVIEW.equalsIgnoreCase("resources-add-remove")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            ProgramResources.clear(talisman);
        } else if (PREVIEW.equalsIgnoreCase("resources-cleared")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            ProgramResources.set(talisman, List.of(
                    new ResourceSelection(firstMaterialId(), 2),
                    new ResourceSelection("diamond", 1)
            ));
        } else if (PREVIEW.equalsIgnoreCase("cast-missing-item")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            ProgramResources.set(talisman, java.util.List.of(new ResourceSelection("diamond", 64)));
        } else if (PREVIEW.equalsIgnoreCase("cast-missing-attribute")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            ProgramResources.clear(talisman);
        } else if (PREVIEW.equalsIgnoreCase("item-sneak-use-resources")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
        } else if (talismanTooltipPreview()) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
        } else if (PREVIEW.equalsIgnoreCase("workflow-witnesses-tooltip")
                || PREVIEW.equalsIgnoreCase("saved-witnesses-guidance")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            ProgramResources.set(talisman, java.util.List.of(new ResourceSelection("diamond", 64)));
        } else if (PREVIEW.equalsIgnoreCase("workflow-ready-tooltip")
                || PREVIEW.equalsIgnoreCase("saved-ready")
                || PREVIEW.equalsIgnoreCase("saved-ready-tooltip")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
        } else if (PREVIEW.equalsIgnoreCase("saved-ready-live-tooltip")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
        } else if (PREVIEW.equalsIgnoreCase("already-inscribed-tooltip")
                || PREVIEW.equalsIgnoreCase("resources-active-tooltip")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            List<ResourceSelection> customResources = List.of(new ResourceSelection("diamond", 3));
            ProgramResources.set(talisman, customResources);
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            if (!ProgramResources.get(talisman).equals(customResources)) {
                throw new IllegalStateException("Reinscribing the same graph replaced its resource preparation");
            }
        } else if (PREVIEW.equalsIgnoreCase("clear-confirmation-tooltip")
                || PREVIEW.equalsIgnoreCase("clear-confirmed")
                || PREVIEW.equalsIgnoreCase("replace-proof-tooltip")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
        } else if (PREVIEW.equalsIgnoreCase("cost-summary")
                || PREVIEW.equalsIgnoreCase("saved-palette-scrolled")
                || PREVIEW.equalsIgnoreCase("edit-theorem-disabled-tooltip")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.blink());
        } else if (PREVIEW.equalsIgnoreCase("frame-theorem")
                || PREVIEW.equalsIgnoreCase("basis-icon-family")
                || PREVIEW.equalsIgnoreCase("theorem-node-tooltip")
                || PREVIEW.equalsIgnoreCase("theorem-formula-tooltip")
                || PREVIEW.equalsIgnoreCase("type-legend-tooltip")) {
            ProgramStorage.saveValidated(talisman, ProgramPresets.rightAngle());
        } else if (PREVIEW.equalsIgnoreCase("laboratory")
                || PREVIEW.equalsIgnoreCase("laboratory-binding-tooltip")
                || PREVIEW.equalsIgnoreCase("laboratory-form-reuse-tooltip")) {
            CustomSpellWorkspace workspace = new CustomSpellWorkspace();
            workspace.apply(CustomSpellAction.RIGHT_BASIS_VECTOR);
            workspace.apply(CustomSpellAction.PUSH_SELF);
            ProgramStorage.saveValidated(
                    talisman,
                    workspace.toGraph(),
                    "Right Basis",
                    workspace.actions()
            );
        } else if (PREVIEW.equalsIgnoreCase("custom-name-default")
                || PREVIEW.equalsIgnoreCase("custom-name-explicit")
                || PREVIEW.equalsIgnoreCase("custom-name-reset")
                || PREVIEW.equalsIgnoreCase("laboratory-reset-confirmation-tooltip")
                || PREVIEW.equalsIgnoreCase("text-field-focus")
                || PREVIEW.equalsIgnoreCase("minimum-viewport")) {
            CustomSpellWorkspace workspace = new CustomSpellWorkspace();
            workspace.apply(CustomSpellAction.RIGHT_BASIS_VECTOR);
            workspace.apply(CustomSpellAction.PUSH_SELF);
            String name = PREVIEW.equalsIgnoreCase("custom-name-default") ? "" : "Hipotese de Gauss";
            ProgramStorage.saveValidated(talisman, workspace.toGraph(), name, workspace.actions());
            if (PREVIEW.equalsIgnoreCase("custom-name-default") && ProgramStorage.getName(talisman).isPresent()) {
                throw new IllegalStateException("Default custom name must not be persisted as display text");
            }
            if ((PREVIEW.equalsIgnoreCase("custom-name-explicit")
                    || PREVIEW.equalsIgnoreCase("custom-name-reset"))
                    && !ProgramStorage.getName(talisman).filter(name::equals).isPresent()) {
                throw new IllegalStateException("Explicit custom name did not survive persistence");
            }
        } else if (PREVIEW.equalsIgnoreCase("custom-name-reinscription")) {
            CustomSpellWorkspace workspace = new CustomSpellWorkspace();
            workspace.apply(CustomSpellAction.RIGHT_BASIS_VECTOR);
            workspace.apply(CustomSpellAction.PUSH_SELF);
            ProgramStorage.saveValidated(
                    talisman,
                    workspace.toGraph(),
                    "Hipotese Antiga",
                    workspace.actions()
            );
        }
        return talisman;
    }

    private static void triggerMissingCostCast(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            ItemStack talisman = previewTalisman();
            serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, talisman);
            serverPlayer.getInventory().setChanged();
            useHeldTalisman(serverPlayer, false);
        });
    }

    private static void triggerItemUseRoute(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            boolean resourcesRoute = PREVIEW.equalsIgnoreCase("item-sneak-use-resources");
            ItemStack talisman = previewTalisman();
            serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, talisman);
            serverPlayer.getInventory().setChanged();
            serverPlayer.inventoryMenu.broadcastChanges();
            useHeldTalisman(serverPlayer, resourcesRoute);
        });
    }

    private static void useHeldTalisman(ServerPlayer serverPlayer, boolean secondaryUse) {
        serverPlayer.setShiftKeyDown(secondaryUse);
        try {
            ItemStack talisman = serverPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            ((ProgrammedTalismanItem) talisman.getItem()).use(
                    serverPlayer.level(),
                    serverPlayer,
                    InteractionHand.MAIN_HAND
            );
        } finally {
            serverPlayer.setShiftKeyDown(false);
        }
    }

    private static void installServerPreviewItem(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer != null) {
                ItemStack heldItem;
                if (runeAnchorTooltipPreview()) {
                    heldItem = new ItemStack(ModItems.RUNE_ANCHOR.get());
                } else if (chalkTooltipPreview()) {
                    heldItem = new ItemStack(ModItems.CHALK.get());
                } else {
                    heldItem = previewTalisman();
                }
                serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, heldItem);
                serverPlayer.getInventory().setChanged();
            }
        });
    }

    private static void openServerProgrammer(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            serverPlayer.setInvulnerable(PREVIEW.equalsIgnoreCase("saved-ready-live-tooltip"));
            if (PREVIEW.equalsIgnoreCase("saved-ready-live-tooltip")) {
                removeAllFeathers(serverPlayer);
            }
            serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, previewTalisman());
            serverPlayer.getInventory().setChanged();
            serverPlayer.inventoryMenu.broadcastChanges();
            ProgrammedTalismanItem.openProgrammer(serverPlayer, InteractionHand.MAIN_HAND);
        });
    }

    private static boolean runLiveReadinessPreview(
            Minecraft minecraft,
            RuneProgrammerScreen screen
    ) {
        if (liveReadinessScreen == null) {
            liveReadinessScreen = screen;
        }
        if (liveReadinessScreen != screen) {
            throw new IllegalStateException("Live readiness preview replaced the Programmer screen");
        }
        if (liveReadinessStep == 0) {
            if (minecraft.player.getInventory().contains(new ItemStack(Items.FEATHER))
                    || resourceButtonTone(screen) != MathButton.Tone.RESOURCE) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "Live readiness preview did not begin in required-preparation state"
                    );
                }
                return false;
            }
            setServerFeatherPresence(minecraft, true);
            liveReadinessStep = 1;
            ticks = 0;
            return false;
        }
        if (liveReadinessStep == 1) {
            if (!minecraft.player.getInventory().contains(new ItemStack(Items.FEATHER))) {
                if (ticks >= 100) {
                    throw new IllegalStateException("Live Feather addition did not synchronize to the client");
                }
                return false;
            }
            if (resourceButtonTone(screen) != MathButton.Tone.INSPECTION) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "Resources did not become optional inspection after live readiness changed"
                    );
                }
                return false;
            }
            setServerFeatherPresence(minecraft, false);
            liveReadinessStep = 2;
            ticks = 0;
            return false;
        }
        if (liveReadinessStep == 2) {
            if (minecraft.player.getInventory().contains(new ItemStack(Items.FEATHER))
                    || resourceButtonTone(screen) != MathButton.Tone.RESOURCE) {
                if (ticks >= 100) {
                    throw new IllegalStateException(
                            "Resources did not return to required preparation after live witness removal"
                    );
                }
                return false;
            }
            setServerFeatherPresence(minecraft, true);
            liveReadinessStep = 3;
            ticks = 0;
            return false;
        }
        if (!minecraft.player.getInventory().contains(new ItemStack(Items.FEATHER))
                || resourceButtonTone(screen) != MathButton.Tone.INSPECTION) {
            if (ticks >= 100) {
                throw new IllegalStateException(
                        "Resources did not return to optional inspection after witness restoration"
                );
            }
            return false;
        }
        liveReadinessStep = 4;
        hoverSavedResources(minecraft, screen);
        return true;
    }

    private static void setServerFeatherPresence(Minecraft minecraft, boolean present) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            throw new IllegalStateException("Live readiness preview requires an integrated server");
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            removeAllFeathers(serverPlayer);
            if (present) {
                serverPlayer.getInventory().add(new ItemStack(Items.FEATHER, 1));
            }
            serverPlayer.getInventory().setChanged();
            serverPlayer.inventoryMenu.broadcastChanges();
        });
    }

    private static void removeAllFeathers(ServerPlayer serverPlayer) {
        for (int slot = 0; slot < serverPlayer.getInventory().items.size(); slot++) {
            if (serverPlayer.getInventory().items.get(slot).is(Items.FEATHER)) {
                serverPlayer.getInventory().items.set(slot, ItemStack.EMPTY);
            }
        }
        for (int slot = 0; slot < serverPlayer.getInventory().offhand.size(); slot++) {
            if (serverPlayer.getInventory().offhand.get(slot).is(Items.FEATHER)) {
                serverPlayer.getInventory().offhand.set(slot, ItemStack.EMPTY);
            }
        }
    }

    private static MathButton.Tone resourceButtonTone(RuneProgrammerScreen screen) {
        try {
            Field buttonField = RuneProgrammerScreen.class.getDeclaredField("savedResourcesButton");
            buttonField.setAccessible(true);
            MathButton button = (MathButton) buttonField.get(screen);
            Field toneField = MathButton.class.getDeclaredField("tone");
            toneField.setAccessible(true);
            return (MathButton.Tone) toneField.get(button);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not inspect the Resources action tone", exception);
        }
    }

    private static void openFirstSpellProgrammer(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            serverPlayer.setInvulnerable(true);
            serverPlayer.removeEffect(ModMobEffects.VITAL_INFUSION);
            serverPlayer.removeEffect(ModMobEffects.PARSIMONY);
            serverPlayer.removeEffect(ModMobEffects.CONSERVATION);
            for (int slot = 0; slot < serverPlayer.getInventory().items.size(); slot++) {
                if (serverPlayer.getInventory().items.get(slot).is(Items.FEATHER)) {
                    serverPlayer.getInventory().items.set(slot, ItemStack.EMPTY);
                }
            }
            for (int slot = 0; slot < serverPlayer.getInventory().offhand.size(); slot++) {
                if (serverPlayer.getInventory().offhand.get(slot).is(Items.FEATHER)
                        || serverPlayer.getInventory().offhand.get(slot).is(Items.QUARTZ)
                        || serverPlayer.getInventory().offhand.get(slot).is(ModItems.HOMUNCULAR_MATRIX.get())
                        || serverPlayer.getInventory().offhand.get(slot).is(ModItems.VITAL_SALT.get())
                        || serverPlayer.getInventory().offhand.get(slot).is(ModItems.AXIOMATIC_INK.get())
                        || serverPlayer.getInventory().offhand.get(slot).is(ModItems.RECURSIVE_SEAL.get())) {
                    serverPlayer.getInventory().offhand.set(slot, ItemStack.EMPTY);
                }
            }
            for (int slot = 0; slot < serverPlayer.getInventory().items.size(); slot++) {
                if (serverPlayer.getInventory().items.get(slot).is(Items.QUARTZ)
                        || serverPlayer.getInventory().items.get(slot).is(ModItems.HOMUNCULAR_MATRIX.get())
                        || serverPlayer.getInventory().items.get(slot).is(ModItems.VITAL_SALT.get())
                        || serverPlayer.getInventory().items.get(slot).is(ModItems.AXIOMATIC_INK.get())
                        || serverPlayer.getInventory().items.get(slot).is(ModItems.RECURSIVE_SEAL.get())) {
                    serverPlayer.getInventory().items.set(slot, ItemStack.EMPTY);
                }
            }
            serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, previewTalisman());
            if (!alchemicalVitalInfusionJourney() && !metamagicJourney()) {
                serverPlayer.getInventory().add(new ItemStack(Items.FEATHER, 1));
            }
            if (advancedHarmonicJourney()) {
                serverPlayer.getInventory().add(new ItemStack(Items.QUARTZ, 1));
            }
            if (alchemicalVitalInfusionJourney()) {
                serverPlayer.getInventory().add(new ItemStack(ModItems.HOMUNCULAR_MATRIX.get(), 1));
                serverPlayer.getInventory().add(new ItemStack(ModItems.VITAL_SALT.get(), 2));
            }
            if (metamagicParsimonyJourney()) {
                serverPlayer.getInventory().add(new ItemStack(ModItems.AXIOMATIC_INK.get(), 2));
            }
            if (metamagicConservationJourney()) {
                serverPlayer.getInventory().add(new ItemStack(ModItems.RECURSIVE_SEAL.get(), 2));
            }
            serverPlayer.getInventory().setChanged();
            serverPlayer.inventoryMenu.broadcastChanges();
            ProgrammedTalismanItem.openProgrammer(serverPlayer, InteractionHand.MAIN_HAND);
        });
    }

    private static void openServerResources(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            serverPlayer.setInvulnerable(false);
            serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, previewTalisman());
            serverPlayer.getInventory().setChanged();
            ProgrammedTalismanItem.openResources(serverPlayer, InteractionHand.MAIN_HAND);
        });
    }

    private static boolean serverBackedResourcesPreview() {
        return PREVIEW.equalsIgnoreCase("resources-add-remove")
                || PREVIEW.equalsIgnoreCase("resources-cleared")
                || PREVIEW.equalsIgnoreCase("resources-scrollbar-drag")
                || resourceBackPreview()
                || PREVIEW.equalsIgnoreCase("keyboard-added-materials")
                || PREVIEW.equalsIgnoreCase("keyboard-material-catalog")
                || resourceHelpPreview();
    }

    private static boolean resourceBackPreview() {
        return PREVIEW.equalsIgnoreCase("resources-back-to-proof");
    }

    private static boolean resourceHelpPreview() {
        return PREVIEW.equalsIgnoreCase("resources-help-entry");
    }

    private static String firstMaterialId() {
        return ProgramResources.materials().getFirst().id();
    }

    private static boolean serverBackedProgrammerPreview() {
        return PREVIEW.equalsIgnoreCase("clear-confirmation-tooltip")
                || PREVIEW.equalsIgnoreCase("clear-confirmed")
                || PREVIEW.equalsIgnoreCase("custom-name-reinscription")
                || PREVIEW.equalsIgnoreCase("saved-ready-live-tooltip")
                || laboratoryResetPreview()
                || programmerHelpPreview();
    }

    private static boolean laboratoryResetPreview() {
        return PREVIEW.equalsIgnoreCase("laboratory-reset-confirmation-tooltip")
                || PREVIEW.equalsIgnoreCase("custom-name-reset");
    }

    private static boolean programmerHelpPreview() {
        return PREVIEW.equalsIgnoreCase("programmer-help-entry");
    }

    private static boolean firstSpellPreview() {
        return PREVIEW.equalsIgnoreCase("first-spell-inscribed")
                || PREVIEW.equalsIgnoreCase("first-spell-close-tooltip")
                || PREVIEW.equalsIgnoreCase("first-spell-ready")
                || PREVIEW.equalsIgnoreCase("first-spell-cast")
                || advancedHarmonicJourney()
                || alchemicalVitalInfusionJourney()
                || metamagicJourney();
    }

    private static boolean advancedHarmonicJourney() {
        return PREVIEW.equalsIgnoreCase("advanced-harmonic-cast");
    }

    private static boolean alchemicalVitalInfusionJourney() {
        return PREVIEW.equalsIgnoreCase("alchemy-vital-infusion-cast");
    }

    private static boolean metamagicParsimonyJourney() {
        return PREVIEW.equalsIgnoreCase("metamagic-parsimony-cast");
    }

    private static boolean metamagicConservationJourney() {
        return PREVIEW.equalsIgnoreCase("metamagic-conservation-cast");
    }

    private static boolean metamagicJourney() {
        return metamagicParsimonyJourney() || metamagicConservationJourney();
    }

    private static ProgramGraph firstSpellJourneyGraph() {
        if (advancedHarmonicJourney()) {
            return ProgramPresets.harmonicStep();
        }
        if (alchemicalVitalInfusionJourney()) {
            return ProgramPresets.vitalInfusion();
        }
        if (metamagicParsimonyJourney()) {
            return ProgramPresets.axiomOfParsimony();
        }
        if (metamagicConservationJourney()) {
            return ProgramPresets.conservationLemma();
        }
        return ProgramPresets.hop();
    }

    private static boolean itemUseRoutePreview() {
        return PREVIEW.equalsIgnoreCase("item-use-empty-programmer")
                || PREVIEW.equalsIgnoreCase("item-sneak-use-resources");
    }

    private static void openPatchouliPreview(Minecraft minecraft) {
        if (PREVIEW.equalsIgnoreCase("patchouli-landing")) {
            openPatchouliBook(minecraft);
            return;
        }
        if (PREVIEW.equalsIgnoreCase("patchouli-advanced-mathematics")) {
            openPatchouliEntry(minecraft, "programming/mathematical_runes", 4);
            return;
        }
        if (PREVIEW.equalsIgnoreCase("patchouli-alchemical-effects")) {
            openPatchouliEntry(minecraft, "programming/alchemical_effects", 6);
            return;
        }
        if (PREVIEW.equalsIgnoreCase("patchouli-inspector")) {
            openPatchouliEntry(minecraft, "programming/inspector", 0);
            return;
        }
        String entry = PREVIEW.equalsIgnoreCase("patchouli-resource-costs")
                ? "programming/resource_costs"
                : PREVIEW.equalsIgnoreCase("patchouli-parallel-proofs")
                        ? "lore/parallel_proofs"
                        : PREVIEW.equalsIgnoreCase("patchouli-custom-programmer-reset")
                                ? "programming/custom_programmer"
                                : PREVIEW.equalsIgnoreCase("patchouli-kubejs-materials")
                                        ? "programming/kubejs"
                                : "basics/current_state";
        int page = PREVIEW.equalsIgnoreCase("patchouli-resource-costs")
                ? 1
                : PREVIEW.equalsIgnoreCase("patchouli-custom-programmer-reset")
                        || PREVIEW.equalsIgnoreCase("patchouli-kubejs-materials") ? 4 : 0;
        openPatchouliEntry(minecraft, entry, page);
    }

    private static void openPatchouliBook(Minecraft minecraft) {
        if (!ModList.get().isLoaded("patchouli")) {
            throw new IllegalStateException("Patchouli preview requires Patchouli in run/client/mods");
        }
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            String command = "open-patchouli-book "
                    + serverPlayer.getGameProfile().getName()
                    + " mathmod:field_manual";
            server.getCommands().performPrefixedCommand(
                    serverPlayer.createCommandSourceStack().withPermission(2),
                    command
            );
        });
    }

    private static void openPatchouliEntry(Minecraft minecraft, String entry, int page) {
        if (!ModList.get().isLoaded("patchouli")) {
            throw new IllegalStateException("Patchouli preview requires Patchouli in run/client/mods");
        }
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) {
                return;
            }
            String command = "open-patchouli-book "
                    + serverPlayer.getGameProfile().getName()
                    + " mathmod:field_manual "
                    + "mathmod:" + entry
                    + " " + page;
            server.getCommands().performPrefixedCommand(
                    serverPlayer.createCommandSourceStack().withPermission(2),
                    command
            );
        });
    }

    private static void runPatchouliMatrixTick(Minecraft minecraft) {
        List<PatchouliPreviewMatrix.Target> targets = PatchouliPreviewMatrix.targets();
        if (patchouliMatrixIndex >= targets.size()) {
            return;
        }
        if (!patchouliMatrixCommandSent) {
            if (patchouliMatrixOpenAttempts >= 5) {
                throw new IllegalStateException(
                        "Patchouli matrix could not open "
                                + targets.get(patchouliMatrixIndex).entryId()
                                + " after " + patchouliMatrixOpenAttempts + " attempts"
                );
            }
            PatchouliPreviewMatrix.Target target = targets.get(patchouliMatrixIndex);
            openPatchouliEntry(minecraft, target.entryId(), target.page());
            patchouliMatrixCommandSent = true;
            patchouliMatrixOpenAttempts++;
            patchouliMatrixTicks = 0;
            return;
        }

        patchouliMatrixTicks++;
        if (!isPatchouliScreen(minecraft)) {
            if (patchouliMatrixTicks >= 60) {
                resetPatchouliMatrixNavigation();
            }
            return;
        }
        if (patchouliMatrixTicks >= 30) {
            patchouliMatrixCaptureRequested = true;
        }
    }

    /** Opens no preview content until its configured language resources are live. */
    private static boolean previewLocaleReady(Minecraft minecraft) {
        if (PREVIEW_LOCALE.isBlank()) {
            return true;
        }
        if (!previewLocaleConfigured) {
            minecraft.options.languageCode = PREVIEW_LOCALE;
            minecraft.getLanguageManager().setSelected(PREVIEW_LOCALE);
            previewLocaleConfigured = true;
            minecraft.reloadResourcePacks().thenRun(() -> previewLocaleReloadComplete = true);
            return false;
        }
        return previewLocaleReloadComplete && PREVIEW_LOCALE.equals(minecraft.options.languageCode);
    }

    private static void capturePatchouliMatrixFrame(Minecraft minecraft) {
        if (!patchouliMatrixCaptureRequested
                || !patchouliMatrixFrameReady
                || patchouliMatrixCaptureInFlight
                || !isPatchouliScreen(minecraft)) {
            return;
        }

        List<PatchouliPreviewMatrix.Target> targets = PatchouliPreviewMatrix.targets();
        PatchouliPreviewMatrix.Target target = targets.get(patchouliMatrixIndex);
        patchouliMatrixCaptureInFlight = true;
        RenderSystem.disableScissor();
        Screenshot.grab(
                minecraft.gameDirectory,
                "mathmod-" + target.screenshotId() + "-preview.png",
                minecraft.getMainRenderTarget(),
                message -> minecraft.execute(() -> {
                    MathMod.LOGGER.info(
                            "Patchouli matrix {}/{}: {}",
                            patchouliMatrixIndex + 1,
                            targets.size(),
                            message.getString()
                    );
                    patchouliMatrixIndex++;
                    if (patchouliMatrixIndex >= targets.size()) {
                        captured = true;
                        minecraft.stop();
                        return;
                    }
                    minecraft.setScreen(null);
                    patchouliMatrixOpenAttempts = 0;
                    resetPatchouliMatrixNavigation();
                })
        );
    }

    private static void resetPatchouliMatrixNavigation() {
        patchouliMatrixTicks = 0;
        patchouliMatrixCommandSent = false;
        patchouliMatrixCaptureRequested = false;
        patchouliMatrixCaptureInFlight = false;
        patchouliMatrixFrameReady = false;
    }

    private static boolean patchouliPreview() {
        return PREVIEW.equalsIgnoreCase("patchouli-landing")
                || PREVIEW.equalsIgnoreCase("patchouli-current-state")
                || PREVIEW.equalsIgnoreCase("patchouli-resource-costs")
                || PREVIEW.equalsIgnoreCase("patchouli-parallel-proofs")
                || PREVIEW.equalsIgnoreCase("patchouli-custom-programmer-reset")
                || PREVIEW.equalsIgnoreCase("patchouli-kubejs-materials")
                || PREVIEW.equalsIgnoreCase("patchouli-advanced-mathematics")
                || PREVIEW.equalsIgnoreCase("patchouli-alchemical-effects")
                || PREVIEW.equalsIgnoreCase("patchouli-inspector")
                || patchouliMatrixPreview();
    }

    private static boolean runeInspectorPreview() {
        return PREVIEW.equalsIgnoreCase("rune-inspector")
                || PREVIEW.equalsIgnoreCase("rune-inspector-functional");
    }

    private static boolean functionalProjectionPreview() {
        return PREVIEW.equalsIgnoreCase("rune-inspector-functional");
    }

    private static void openFunctionalProjectionProgrammer(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) return;
        var playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
            if (serverPlayer == null) return;
            ItemStack talisman = previewTalisman();
            ProgramStorage.saveValidated(talisman, ProgramPresets.hop());
            String source = functionalProjectionSource();
            talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),
                    new ScopedSourceEnvelope(1, source.getBytes(StandardCharsets.UTF_8)));
            serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, talisman);
            serverPlayer.getInventory().setChanged();
            auditLiveOpeningSnapshot(serverPlayer);
            minecraft.execute(() -> {
                if (minecraft.player != null) {
                    minecraft.player.setItemInHand(InteractionHand.MAIN_HAND, talisman.copy());
                }
            });
            ProgrammedTalismanItem.openProgrammer(serverPlayer, InteractionHand.MAIN_HAND);
        });
    }

    private static void auditLiveOpeningSnapshot(ServerPlayer player) {
        try {
            var method = com.mathmod.program.ScopedFunctionalProjectionService.class.getDeclaredMethod(
                    "openingSnapshot", ServerPlayer.class, InteractionHand.class, Runnable.class, Runnable.class);
            method.setAccessible(true);
            int[] compileCalls = {0};
            PlayerKnowledge original = KnowledgeService.get(player);
            Object result = method.invoke(null, player, InteractionHand.MAIN_HAND,
                    (Runnable) () -> compileCalls[0]++,
                    (Runnable) () -> KnowledgeService.replace(player, KnowledgeService.get(player).withSchemaVersion(original.schemaVersion() + 1)));
            KnowledgeService.replace(player, original);
            if (!(result instanceof com.mathmod.program.ScopedFunctionalProjection projection)
                    || compileCalls[0] != 1
                    || projection.sourceState() != com.mathmod.program.ScopedFunctionalProjection.SourceState.STALE
                    || !projection.authoredRows().isEmpty() || !projection.checkedRows().isEmpty()) {
                throw new IllegalStateException("live opening snapshot authority recheck did not fail closed");
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not audit live opening snapshot authority ordering", exception);
        }
    }

    private static String functionalProjectionSource() {
        StringBuilder arguments = new StringBuilder();
        for (int index = 0; index < 4; index++) {
            if (index > 0) arguments.append(',');
            arguments.append("{\"input_name\":\"x").append(index)
                    .append("\",\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"}}");
        }
        return "{\"expression\":{\"kind\":\"rune_call\",\"rune_id\":\"future:x\",\"arguments\":[" + arguments + "]}"
                + ",\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}";
    }

    private static boolean patchouliMatrixPreview() {
        return PREVIEW.equalsIgnoreCase("patchouli-matrix");
    }

    private static boolean isPatchouliScreen(Minecraft minecraft) {
        return minecraft.screen != null
                && minecraft.screen.getClass().getName()
                .startsWith("vazkii.patchouli.client.book.gui.GuiBook");
    }

    private static boolean isExpectedScreen(Minecraft minecraft) {
        if (firstSpellPreview()) {
            if (firstSpellStep >= 5) {
                return minecraft.screen == null;
            }
            if (firstSpellStep >= 3) {
                return minecraft.screen instanceof TalismanResourcesScreen;
            }
            return minecraft.screen instanceof RuneProgrammerScreen;
        }
        if (worldPreview()) {
            return minecraft.screen == null;
        }
        if (PREVIEW.equalsIgnoreCase("item-use-empty-programmer")) {
            return minecraft.screen instanceof RuneProgrammerScreen;
        }
        if (PREVIEW.equalsIgnoreCase("item-sneak-use-resources")) {
            return minecraft.screen instanceof TalismanResourcesScreen;
        }
        if (itemTooltipPreview()) {
            return minecraft.screen instanceof InventoryScreen;
        }
        if (fieldLedgerPreview()) {
            return minecraft.screen instanceof FieldLedgerScreen;
        }
        if (patchouliPreview()) {
            return isPatchouliScreen(minecraft);
        }
        if (runeInspectorPreview()) {
            return minecraft.screen instanceof RuneInspectorScreen;
        }
        if (resourceHelpPreview()) {
            return isPatchouliScreen(minecraft);
        }
        if (programmerHelpPreview()) {
            return isPatchouliScreen(minecraft);
        }
        if (resourceBackPreview()) {
            return resourceBackClickSent
                    ? minecraft.screen instanceof RuneProgrammerScreen
                    : minecraft.screen instanceof TalismanResourcesScreen;
        }
        return (PREVIEW.equalsIgnoreCase("resources")
                || PREVIEW.equalsIgnoreCase("minimum-resources")
                || PREVIEW.equalsIgnoreCase("resources-clear-tooltip")
                || PREVIEW.equalsIgnoreCase("resources-material-tooltip")
                || PREVIEW.equalsIgnoreCase("resources-notation-tooltip")
                || longLoadoutNamePreview()
                || PREVIEW.equalsIgnoreCase("keyboard-first-resources")
                || serverBackedResourcesPreview())
                ? minecraft.screen instanceof TalismanResourcesScreen
                : minecraft.screen instanceof RuneProgrammerScreen;
    }

    private static boolean worldPreview() {
        return PREVIEW.equalsIgnoreCase("cast-missing-item")
                || PREVIEW.equalsIgnoreCase("cast-missing-attribute")
                || PREVIEW.equalsIgnoreCase("first-spell-cast")
                || anchorJourneyPreview();
    }

    private static boolean fieldLedgerPreview() {
        return PREVIEW.equalsIgnoreCase("field-ledger-overview")
                || PREVIEW.equalsIgnoreCase("field-ledger-epiphanies");
    }

    private static void openFieldLedgerPreview(Minecraft minecraft) {
        PlayerKnowledge knowledge = PlayerKnowledge.empty();
        if (PREVIEW.equalsIgnoreCase("field-ledger-epiphanies")) {
            knowledge = KnowledgeProgress.advance(
                    knowledge,
                    java.util.Set.of(
                            KnowledgeDefinitions.HARMONIC_MOTION_EPIPHANY
                                    .studies()
                                    .getFirst()
                                    .materialId()
                    )
            ).knowledge();
        }
        FieldLedgerView view = FieldLedgerView.from(
                knowledge,
                KnowledgeDefinitions.snapshot()
        );
        FieldLedgerMenu menu = new FieldLedgerMenu(
                0,
                minecraft.player.getInventory(),
                InteractionHand.MAIN_HAND,
                view
        );
        FieldLedgerScreen screen = new FieldLedgerScreen(
                menu,
                minecraft.player.getInventory(),
                Component.translatable("screen.mathmod.field_ledger")
        );
        minecraft.setScreen(screen);
        if (PREVIEW.equalsIgnoreCase("field-ledger-epiphanies")) {
            clickWidget(
                    screen,
                    Component.translatable("screen.mathmod.field_ledger.tab.epiphanies")
            );
            movePreviewCursor(
                    minecraft,
                    screen.width,
                    screen.height,
                    screen.width / 2.0D,
                    screen.height / 2.0D
            );
        }
    }

    private static boolean anchorJourneyPreview() {
        return PREVIEW.equalsIgnoreCase("anchor-journey");
    }

    private static boolean talismanTooltipPreview() {
        return PREVIEW.equalsIgnoreCase("talisman-incomplete-tooltip");
    }

    private static boolean chalkTooltipPreview() {
        return PREVIEW.equalsIgnoreCase("chalk-tooltip");
    }

    private static boolean runeAnchorTooltipPreview() {
        return PREVIEW.equalsIgnoreCase("rune-anchor-tooltip");
    }

    private static boolean itemTooltipPreview() {
        return talismanTooltipPreview()
                || chalkTooltipPreview()
                || runeAnchorTooltipPreview();
    }

    private static boolean longLoadoutNamePreview() {
        return PREVIEW.equalsIgnoreCase("resources-long-name-tooltip");
    }
}
