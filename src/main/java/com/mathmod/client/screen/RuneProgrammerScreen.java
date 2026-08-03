package com.mathmod.client.screen;

import com.mathmod.MathMod;
import com.mathmod.authoring.AuthoringMetadata;
import com.mathmod.network.OpenProgrammerHelpPayload;
import com.mathmod.network.ApplyCustomSpellInvocationPayload;
import com.mathmod.network.UpdateCustomSpellNamePayload;
import com.mathmod.knowledge.KnowledgePolicy;
import com.mathmod.knowledge.KnowledgeRequirement;
import com.mathmod.knowledge.KnowledgeService;
import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.program.CustomActionPreview;
import com.mathmod.program.CustomInputSlot;
import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.CustomSpellInvocation;
import com.mathmod.program.CustomSpellStep;
import com.mathmod.program.CustomSpellWorkspace;
import com.mathmod.program.GuidedWorkspaceState;
import com.mathmod.program.PlayerProgramCosts;
import com.mathmod.program.ProgramCostPlan;
import com.mathmod.program.ProgramCosts;
import com.mathmod.program.ProgramNames;
import com.mathmod.program.ProgramMessageComponents;
import com.mathmod.program.ProgramNameComponents;
import com.mathmod.program.ProgramPresets;
import com.mathmod.program.ProgramResources;
import com.mathmod.program.ProgramStorage;
import com.mathmod.program.ProgramSurface;
import com.mathmod.program.ProgramTiers;
import com.mathmod.program.ResourceSelection;
import com.mathmod.program.TalismanPreset;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.ProgramValidator;
import com.mathmod.runes.RuneType;
import com.mathmod.runes.ValidationIssue;
import com.mathmod.runes.ValidationResult;
import com.mathmod.screen.RuneProgrammerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RuneProgrammerScreen extends AbstractContainerScreen<RuneProgrammerMenu> {
    private static final int TEXT_PADDING = 8;
    private static final int LINE_HEIGHT = 11;
    private static final int RUNE_ICON_SIZE = 10;
    private static final int SCROLL_STEP = LINE_HEIGHT * 2;
    private static final int PALETTE_ROW_HEIGHT = 16;
    private static final int PRESET_ROW_HEIGHT = 30;
    private static final int CATEGORY_ROW_HEIGHT = 16;
    private static final int CUSTOM_PALETTE_CONTENT_OFFSET = 43;
    private static final int WORKFLOW_SEAL_SIZE = 10;
    private static final int TYPE_LEGEND_SIZE = 12;
    private static final int TYPE_LEGEND_TITLE_RESERVE = 18;
    private static final int THEOREM_STATEMENT_HEIGHT = LINE_HEIGHT * 2;
    private static final int THEOREM_GRAPH_VIEWPORT_OFFSET = 37;
    private static final int DEFAULT_GRAPH_VIEWPORT_OFFSET = 24;

    private ProgrammerLayout layout = ProgrammerLayout.forViewport(512, 400);
    private int PANEL_TOP = layout.panelTop();
    private int PANEL_BOTTOM_PADDING = layout.bottomPadding();
    private int PALETTE_X = layout.palette().x();
    private int PALETTE_WIDTH = layout.palette().width();
    private int GRAPH_X = layout.graph().x();
    private int GRAPH_WIDTH = layout.graph().width();

    private ProgramGraph preview;
    private ValidationResult validation;
    private TalismanPreset selectedPreset = ProgramPresets.talismanPresets().getFirst();
    private int graphScroll;
    private int savedPaletteScroll;
    private int customPaletteScroll;
    private int presetPaletteScroll;
    private ProgrammerTab currentTab = ProgrammerTab.PRESETS;
    private final CustomSpellWorkspace customWorkspace = new CustomSpellWorkspace();
    private final List<Button> savedButtons = new ArrayList<>();
    private final List<Button> presetButtons = new ArrayList<>();
    private final List<Button> customButtons = new ArrayList<>();
    private final List<Button> clearProgramButtons = new ArrayList<>();
    private final InscriptionFeedbackTracker inscriptionFeedback = new InscriptionFeedbackTracker();
    private final AuthoringPalettePresentation authoringPalette = AuthoringPalettePresentation.builtIns();
    private final PaletteCursor presetCursor = new PaletteCursor(ProgramPresets.talismanPresets().size());
    private final PaletteCursor customCursor = new PaletteCursor(authoringPalette.forms().size());
    private PaletteNavigator paletteNavigator;
    private WorkflowSealWidget workflowSeal;
    private TypeLegendWidget typeLegend;
    private TheoremStatementWidget theoremStatement;
    private MathButton savedTabButton;
    private MathButton presetTabButton;
    private MathButton customTabButton;
    private MathButton inspectButton;
    private Button editSavedCustomButton;
    private MathButton savedResourcesButton;
    private MathButton presetResourcesButton;
    private MathButton customResourcesButton;
    private Button savedClearButton;
    private Button presetClearButton;
    private Button savePresetButton;
    private Button saveCustomButton;
    private Button undoCustomButton;
    private MathButton resetCustomButton;
    private EditBox customNameBox;
    private EditBox customSearchBox;
    private final List<EditBox> parameterBoxes = new ArrayList<>();
    private Button parameterApplyButton;
    private Button parameterCancelButton;
    private CustomSpellAction parameterAction;
    private String parameterError = "";
    private String customSpellName = "";
    private String customSearch = "";
    private String inscriptionName = "";
    private boolean clearProgramArmed;
    private boolean resetCustomArmed;
    private InscriptionTarget observedInscription = InscriptionTarget.empty();
    private boolean observedInscriptionInitialized;
    private ProofWorkflowState observedWorkflow;
    private ScrollTarget draggedScrollbar = ScrollTarget.NONE;
    private int scrollbarDragOffset;

    public RuneProgrammerScreen(RuneProgrammerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 396;
        this.imageHeight = 264;
        this.inventoryLabelY = this.imageHeight + 10;
    }

    @Override
    protected void init() {
        resetCustomArmed = false;
        draggedScrollbar = ScrollTarget.NONE;
        applyLayout(ProgrammerLayout.forViewport(this.width, this.height, itemOverlayLoaded()));
        super.init();
        savedButtons.clear();
        presetButtons.clear();
        customButtons.clear();
        clearProgramButtons.clear();
        List<ProgrammerLayout.Rect> saved = layout.savedActions();
        List<ProgrammerLayout.Rect> presets = layout.presetActions();
        List<ProgrammerLayout.Rect> custom = layout.customActions();

        presetTabButton = addRenderableWidget(tabButton(
                layout.theoremTab(),
                Component.translatable("screen.mathmod.rune_programmer.tab_presets"),
                button -> switchTab(ProgrammerTab.PRESETS),
                () -> currentTab == ProgrammerTab.PRESETS,
                Component.translatable("screen.mathmod.rune_programmer.tab_presets_lore")
        ));

        customTabButton = addRenderableWidget(tabButton(
                layout.laboratoryTab(),
                Component.translatable(layout.compact()
                        ? "screen.mathmod.rune_programmer.tab_custom_short"
                        : "screen.mathmod.rune_programmer.tab_custom"),
                button -> switchTab(ProgrammerTab.CUSTOM),
                () -> currentTab == ProgrammerTab.CUSTOM,
                Component.translatable("screen.mathmod.rune_programmer.tab_custom_lore")
        ));

        savedTabButton = addRenderableWidget(tabButton(
                layout.inscribedTab(),
                Component.translatable("screen.mathmod.rune_programmer.tab_saved"),
                button -> switchTab(ProgrammerTab.SAVED),
                () -> currentTab == ProgrammerTab.SAVED,
                Component.translatable("screen.mathmod.rune_programmer.tab_saved_lore")
        ));

        ProgrammerLayout.Rect searchBounds = layout.customSearchContent();
        customSearchBox = addRenderableWidget(new EditBox(
                font,
                leftPos + searchBounds.x(),
                topPos + searchBounds.y(),
                searchBounds.width(),
                searchBounds.height(),
                Component.translatable("screen.mathmod.rune_programmer.search")
        ));
        customSearchBox.setMaxLength(48);
        customSearchBox.setValue(customSearch);
        customSearchBox.setResponder(this::updateCustomSearch);
        customSearchBox.setHint(Component.translatable("screen.mathmod.rune_programmer.search_hint"));
        customSearchBox.setBordered(false);
        customSearchBox.setTextColor(MathGuiTheme.IVORY);

        initializeParameterDialog();

        paletteNavigator = addRenderableWidget(new PaletteNavigator(
                leftPos + PALETTE_X,
                topPos + PANEL_TOP,
                PALETTE_WIDTH,
                imageHeight - PANEL_TOP - PANEL_BOTTOM_PADDING
        ));
        workflowSeal = addRenderableWidget(new WorkflowSealWidget(
                leftPos + GRAPH_X + TEXT_PADDING,
                topPos + PANEL_TOP + 4,
                workflowSealWidth()
        ));
        typeLegend = addRenderableWidget(new TypeLegendWidget(
                leftPos + GRAPH_X + GRAPH_WIDTH - TEXT_PADDING - TYPE_LEGEND_SIZE,
                topPos + PANEL_TOP + 4
        ));
        theoremStatement = addRenderableWidget(new TheoremStatementWidget(
                leftPos + GRAPH_X + TEXT_PADDING,
                topPos + PANEL_TOP + 14,
                GRAPH_WIDTH - TEXT_PADDING * 2 - theoremStatementInspectorReserve()
        ));
        updateTheoremStatementGeometry();

        inspectButton = addRenderableWidget(MathButton.iconAction(
                leftPos + GRAPH_X + GRAPH_WIDTH - inspectButtonWidth() - 20,
                topPos + PANEL_TOP + 2,
                inspectButtonWidth(),
                18,
                Component.translatable("screen.mathmod.rune_inspector.open"),
                Component.literal("i"),
                button -> openInspector(),
                MathButton.Tone.INSPECTION
        ));
        inspectButton.setTooltip(Tooltip.create(Component.translatable("screen.mathmod.rune_inspector.open_hint")));

        editSavedCustomButton = addRenderableWidget(actionButton(
                saved.get(0),
                Component.translatable(layout.compact()
                        ? "screen.mathmod.rune_programmer.edit_short"
                        : "screen.mathmod.rune_programmer.edit_custom"),
                button -> switchTab(ProgrammerTab.CUSTOM),
                MathButton.Tone.PRIMARY
        ));
        savedButtons.add(editSavedCustomButton);

        MathButton replaceSavedButton = addRenderableWidget(actionButton(
                saved.get(1),
                Component.translatable(layout.compact()
                        ? "screen.mathmod.rune_programmer.replace_proof_short"
                        : "screen.mathmod.rune_programmer.replace_proof"),
                button -> switchTab(ProgrammerTab.PRESETS),
                MathButton.Tone.NEUTRAL
        ));
        replaceSavedButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.replace_proof_hint")
        ));
        savedButtons.add(replaceSavedButton);

        savedResourcesButton = addRenderableWidget(actionButton(
                saved.get(2),
                Component.translatable("screen.mathmod.rune_programmer.resources"),
                button -> openResources(),
                MathButton.Tone.RESOURCE
        ));
        savedResourcesButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.resources_inscribed_hint")
        ));
        savedButtons.add(savedResourcesButton);

        savedClearButton = addRenderableWidget(actionButton(
                saved.get(3),
                Component.translatable("screen.mathmod.rune_programmer.clear"),
                button -> requestClearProgram(),
                MathButton.Tone.DANGER
        ));
        savedClearButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.clear_hint")
        ));
        savedButtons.add(savedClearButton);
        clearProgramButtons.add(savedClearButton);

        savePresetButton = addRenderableWidget(actionButton(
                presets.get(0),
                Component.translatable("screen.mathmod.rune_programmer.inscribe"),
                button -> savePreset(),
                MathButton.Tone.PRIMARY
        ));
        savePresetButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.inscribe_hint")
        ));
        presetButtons.add(savePresetButton);

        presetResourcesButton = addRenderableWidget(actionButton(
                presets.get(1),
                Component.translatable("screen.mathmod.rune_programmer.resources"),
                button -> openResources(),
                MathButton.Tone.RESOURCE
        ));
        presetResourcesButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.resources_preview_hint")
        ));
        presetButtons.add(presetResourcesButton);

        presetClearButton = addRenderableWidget(actionButton(
                presets.get(2),
                Component.translatable("screen.mathmod.rune_programmer.clear"),
                button -> requestClearProgram(),
                MathButton.Tone.DANGER
        ));
        presetClearButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.clear_hint")
        ));
        presetButtons.add(presetClearButton);
        clearProgramButtons.add(presetClearButton);

        saveCustomButton = addRenderableWidget(actionButton(
                custom.get(0),
                Component.translatable(layout.compact()
                        ? "screen.mathmod.rune_programmer.save"
                        : "screen.mathmod.rune_programmer.save_custom"),
                button -> saveCustom(),
                MathButton.Tone.PRIMARY
        ));
        saveCustomButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.save_custom_hint")
        ));
        customButtons.add(saveCustomButton);

        ProgrammerLayout.Rect nameBounds = layout.customNameContent();
        customNameBox = addRenderableWidget(new EditBox(
                font,
                leftPos + nameBounds.x(),
                topPos + nameBounds.y(),
                nameBounds.width(),
                nameBounds.height(),
                Component.translatable("screen.mathmod.rune_programmer.custom_name")
        ));
        customNameBox.setMaxLength(ProgramNames.MAX_LENGTH);
        customNameBox.setValue(customSpellName);
        customNameBox.setCursorPosition(0);
        customNameBox.setHighlightPos(0);
        customNameBox.setResponder(this::updateCustomName);
        customNameBox.setHint(customNameHint(nameBounds.width()));
        customNameBox.setBordered(false);
        customNameBox.setTextColor(MathGuiTheme.IVORY);
        updateCustomNameTooltip();

        undoCustomButton = addRenderableWidget(compactIconActionButton(
                custom.get(1),
                Component.translatable("screen.mathmod.rune_programmer.undo_custom"),
                Component.literal("<-"),
                button -> undoCustom(),
                MathButton.Tone.NEUTRAL
        ));
        undoCustomButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.undo_custom_hint")
        ));
        customButtons.add(undoCustomButton);

        resetCustomButton = addRenderableWidget(compactIconActionButton(
                custom.get(2),
                Component.translatable("screen.mathmod.rune_programmer.reset_custom"),
                Component.literal("0"),
                button -> requestResetCustom(),
                MathButton.Tone.DANGER
        ));
        resetCustomButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.reset_custom_hint")
        ));
        customButtons.add(resetCustomButton);

        customResourcesButton = addRenderableWidget(compactIconActionButton(
                custom.get(3),
                Component.translatable("screen.mathmod.rune_programmer.resources"),
                Component.literal("\u03A3"),
                button -> openResources(),
                MathButton.Tone.RESOURCE
        ));
        customResourcesButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.rune_programmer.resources_preview_hint")
        ));
        customButtons.add(customResourcesButton);

        Component functionMark = Component.literal("f(x)");
        HeaderNotationLayout headerNotation = HeaderNotationLayout.alignedRight(
                imageWidth,
                titleLabelY,
                font.width(functionMark),
                font.lineHeight
        );
        boolean patchouliLoaded = ModList.get().isLoaded("patchouli");
        ProgrammerLayout.Rect closeBounds = headerNotation.leadingAction();
        MathButton closeButton = MathButton.iconAction(
                leftPos + closeBounds.x(),
                topPos + closeBounds.y(),
                closeBounds.width(),
                closeBounds.height(),
                Component.translatable("screen.mathmod.rune_programmer.close_action"),
                Component.literal("X"),
                button -> onClose(),
                MathButton.Tone.NEUTRAL
        );
        closeButton.setTooltip(Tooltip.create(Component.translatable(
                "screen.mathmod.rune_programmer.close_hint"
        )));
        addRenderableWidget(closeButton);
        ProgrammerLayout.Rect helpBounds = headerNotation.help();
        MathButton helpButton = MathButton.iconAction(
                leftPos + helpBounds.x(),
                topPos + helpBounds.y(),
                helpBounds.width(),
                helpBounds.height(),
                Component.translatable("screen.mathmod.rune_programmer.help_action"),
                Component.literal("?"),
                button -> PacketDistributor.sendToServer(OpenProgrammerHelpPayload.INSTANCE),
                MathButton.Tone.RESOURCE
        );
        helpButton.active = patchouliLoaded;
        helpButton.setTooltip(Tooltip.create(Component.translatable(patchouliLoaded
                ? "screen.mathmod.rune_programmer.help"
                : "screen.mathmod.rune_programmer.help_unavailable")));
        addRenderableWidget(helpButton);
        ProgrammerLayout.Rect notationBounds = headerNotation.notation();
        addRenderableWidget(new NotationWidget(
                leftPos + notationBounds.x(),
                topPos + notationBounds.y(),
                notationBounds.width(),
                notationBounds.height(),
                functionMark,
                Component.translatable("screen.mathmod.rune_programmer.notation.function"),
                MathGuiTheme.GOLD
        ));

        loadStoredProgramPreview();
        if (currentTab == ProgrammerTab.PRESETS && preview == null) {
            refreshPresetPreview();
        }
        updateModeButtons();
    }

    private void initializeParameterDialog() {
        parameterBoxes.clear();
        for (int index = 0; index < 5; index++) {
            EditBox box = addRenderableWidget(new EditBox(
                    font,
                    0,
                    0,
                    92,
                    18,
                    Component.translatable("screen.mathmod.rune_programmer.parameter.value")
            ));
            box.setMaxLength(32);
            box.setBordered(false);
            box.setTextColor(MathGuiTheme.IVORY);
            box.setVisible(false);
            parameterBoxes.add(box);
        }
        parameterApplyButton = addRenderableWidget(Button.builder(
                Component.translatable("screen.mathmod.rune_programmer.parameter.apply"),
                button -> applyParameterDialog()
        ).bounds(0, 0, 74, 20).build());
        parameterCancelButton = addRenderableWidget(Button.builder(
                Component.translatable("screen.mathmod.rune_programmer.parameter.cancel"),
                button -> closeParameterDialog()
        ).bounds(0, 0, 74, 20).build());
        parameterApplyButton.visible = false;
        parameterCancelButton.visible = false;
    }

    private void openParameterDialog(CustomSpellAction action) {
        parameterAction = action;
        parameterError = "";
        List<AuthoringMetadata.Parameter> parameters = authoringPalette.find(action)
                .map(form -> form.metadata().parameters()).orElse(List.of());
        int dialogX = leftPos + (imageWidth - 252) / 2;
        int dialogY = topPos + (imageHeight - parameterDialogHeight(parameters.size())) / 2;
        for (int index = 0; index < parameterBoxes.size(); index++) {
            EditBox box = parameterBoxes.get(index);
            boolean visible = index < parameters.size();
            box.setVisible(visible);
            box.active = visible;
            if (visible) {
                AuthoringMetadata.Parameter parameter = parameters.get(index);
                box.setPosition(dialogX + 142, dialogY + 35 + index * 24);
                box.setValue(formatNumber(parameter.defaultValue()));
                box.setTooltip(Tooltip.create(Component.translatable(
                        "screen.mathmod.rune_programmer.parameter.range",
                        formatNumber(parameter.constraints().minimum()),
                        formatNumber(parameter.constraints().maximum())
                )));
            }
        }
        int buttonY = dialogY + parameterDialogHeight(parameters.size()) - 28;
        parameterApplyButton.setPosition(dialogX + 88, buttonY);
        parameterCancelButton.setPosition(dialogX + 168, buttonY);
        parameterApplyButton.visible = true;
        parameterApplyButton.active = true;
        parameterCancelButton.visible = true;
        parameterCancelButton.active = true;
        if (!parameterBoxes.isEmpty()) {
            setFocused(parameterBoxes.getFirst());
        }
    }

    private void closeParameterDialog() {
        parameterAction = null;
        parameterError = "";
        for (EditBox box : parameterBoxes) {
            box.setVisible(false);
            box.active = false;
        }
        parameterApplyButton.visible = false;
        parameterApplyButton.active = false;
        parameterCancelButton.visible = false;
        parameterCancelButton.active = false;
        setFocused(paletteNavigator);
    }

    private void applyParameterDialog() {
        if (parameterAction == null) {
            return;
        }
        Map<String, Double> values = new LinkedHashMap<>();
        List<AuthoringMetadata.Parameter> parameters = authoringPalette.find(parameterAction)
                .map(form -> form.metadata().parameters()).orElse(List.of());
        for (int index = 0; index < parameters.size(); index++) {
            AuthoringMetadata.Parameter parameter = parameters.get(index);
            double value;
            try {
                value = Double.parseDouble(parameterBoxes.get(index).getValue().trim());
            } catch (NumberFormatException exception) {
                parameterError = Component.translatable(
                        "screen.mathmod.rune_programmer.parameter.invalid",
                        Component.translatable(parameter.translationKey())
                ).getString();
                return;
            }
            values.put(parameter.key(), parameter.canonicalize(value));
        }
        if (parameterAction == CustomSpellAction.FINITE_DIFFERENCE
                && Math.abs(values.get("step")) < 1.0E-9D) {
            parameterError = Component.translatable("screen.mathmod.rune_programmer.parameter.zero_step").getString();
            return;
        }
        if (parameterAction == CustomSpellAction.SIMPSON_INTEGRAL
                && Math.abs(values.get("upper") - values.get("lower")) < 1.0E-9D) {
            parameterError = Component.translatable("screen.mathmod.rune_programmer.parameter.equal_bounds").getString();
            return;
        }
        Map<String, Double> canonical = authoringPalette.find(parameterAction)
                .orElseThrow().canonicalArguments(values);
        CustomSpellInvocation invocation = new CustomSpellInvocation(parameterAction, canonical);
        customWorkspace.apply(invocation);
        PacketDistributor.sendToServer(new ApplyCustomSpellInvocationPayload(invocation.persistentId()));
        closeParameterDialog();
        refreshCustomPreview();
    }

    private static int parameterDialogHeight(int parameterCount) {
        return 72 + parameterCount * 24;
    }

    private static String formatNumber(double value) {
        return value == Math.rint(value) ? Long.toString((long) value) : Double.toString(value);
    }

    private static boolean itemOverlayLoaded() {
        ModList modList = ModList.get();
        return modList.isLoaded("jei") || modList.isLoaded("emi") || modList.isLoaded("roughlyenoughitems");
    }

    private void applyLayout(ProgrammerLayout nextLayout) {
        this.layout = nextLayout;
        this.imageWidth = nextLayout.width();
        this.imageHeight = nextLayout.height();
        this.inventoryLabelY = this.imageHeight + 10;
        this.PANEL_TOP = nextLayout.panelTop();
        this.PANEL_BOTTOM_PADDING = nextLayout.bottomPadding();
        this.PALETTE_X = nextLayout.palette().x();
        this.PALETTE_WIDTH = nextLayout.palette().width();
        this.GRAPH_X = nextLayout.graph().x();
        this.GRAPH_WIDTH = nextLayout.graph().width();
    }

    private MathButton tabButton(
            ProgrammerLayout.Rect bounds,
            Component message,
            Button.OnPress onPress,
            java.util.function.BooleanSupplier selected,
            Component tooltip
    ) {
        MathButton button = MathButton.tab(
                leftPos + bounds.x(),
                topPos + bounds.y(),
                bounds.width(),
                message,
                onPress,
                selected
        );
        button.setTooltip(Tooltip.create(tooltip));
        return button;
    }

    private MathButton actionButton(
            ProgrammerLayout.Rect bounds,
            Component message,
            Button.OnPress onPress,
            MathButton.Tone tone
    ) {
        return MathButton.action(
                leftPos + bounds.x(),
                topPos + bounds.y(),
                bounds.width(),
                message,
                onPress,
                tone
        );
    }

    private MathButton compactIconActionButton(
            ProgrammerLayout.Rect bounds,
            Component message,
            Component icon,
            Button.OnPress onPress,
            MathButton.Tone tone
    ) {
        if (!layout.compact()) {
            return actionButton(bounds, message, onPress, tone);
        }
        return MathButton.iconAction(
                leftPos + bounds.x(),
                topPos + bounds.y(),
                bounds.width(),
                message,
                icon,
                onPress,
                tone
        );
    }

    private void assemblePreset(TalismanPreset preset) {
        disarmClearProgram();
        disarmResetCustom();
        currentTab = ProgrammerTab.PRESETS;
        selectedPreset = preset;
        preview = preset.graph();
        validation = validatePreview(preview, ProgramResources.recommendedFor(preview));
        graphScroll = 0;
        updateTheoremStatementGeometry();
        updateModeButtons();
    }

    private void savePreset() {
        if (!isPresetUnlocked(selectedPreset)) {
            explainLocked(KnowledgePolicy.requirementFor(selectedPreset).orElseThrow());
            return;
        }
        if (preview == null) {
            assemblePreset(selectedPreset);
        }
        disarmClearProgram();
        if (minecraft != null && minecraft.gameMode != null) {
            inscriptionName = Component.translatable(selectedPreset.nameKey()).getString();
            inscriptionFeedback.begin(InscriptionTarget.preset(preview));
            updateModeButtons();
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, selectedPreset.buttonId());
        }
    }

    private void saveCustom() {
        disarmClearProgram();
        disarmResetCustom();
        if (minecraft != null && minecraft.gameMode != null) {
            inscriptionName = customSpellDisplayName();
            inscriptionFeedback.begin(InscriptionTarget.custom(
                    preview,
                    customSpellName,
                    customWorkspace.actions()
            ));
            updateModeButtons();
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RuneProgrammerMenu.SAVE_CUSTOM_BUTTON);
        }
    }

    private void openResources() {
        disarmClearProgram();
        disarmResetCustom();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RuneProgrammerMenu.OPEN_RESOURCES_BUTTON);
        }
    }

    private void requestResetCustom() {
        if (!resetCustomArmed) {
            resetCustomArmed = true;
            updateResetCustomButton();
            return;
        }
        resetCustom();
    }

    private void resetCustom() {
        resetCustomArmed = false;
        customWorkspace.clear();
        customSpellName = "";
        if (customNameBox != null) {
            setCustomNameBoxValue(customSpellName);
        }
        refreshCustomPreview();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RuneProgrammerMenu.RESET_CUSTOM_BUTTON);
        }
    }

    private void undoCustom() {
        disarmResetCustom();
        if (!customWorkspace.undoLast()) {
            return;
        }
        refreshCustomPreview();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RuneProgrammerMenu.UNDO_CUSTOM_BUTTON);
        }
    }

    private void clearProgram() {
        disarmClearProgram();
        inscriptionFeedback.reset();
        inscriptionName = "";
        if (currentTab != ProgrammerTab.CUSTOM) {
            currentTab = ProgrammerTab.PRESETS;
            refreshPresetPreview();
        }
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RuneProgrammerMenu.CLEAR_BUTTON);
        }
        updateModeButtons();
    }

    private void requestClearProgram() {
        disarmResetCustom();
        if (!clearProgramArmed) {
            clearProgramArmed = true;
            updateClearProgramButtons();
            return;
        }
        clearProgram();
    }

    private void disarmClearProgram() {
        if (!clearProgramArmed) {
            return;
        }
        clearProgramArmed = false;
        updateClearProgramButtons();
    }

    private void updateClearProgramButtons() {
        Component message = Component.translatable(clearProgramArmed
                ? "screen.mathmod.rune_programmer.clear_confirm"
                : "screen.mathmod.rune_programmer.clear");
        for (Button button : clearProgramButtons) {
            button.setMessage(message);
        }
    }

    private void switchTab(ProgrammerTab tab) {
        disarmClearProgram();
        disarmResetCustom();
        currentTab = tab;
        graphScroll = 0;
        if (tab == ProgrammerTab.SAVED) {
            savedPaletteScroll = 0;
            loadStoredProgramPreview();
        } else if (tab == ProgrammerTab.CUSTOM) {
            refreshCustomPreview();
        } else {
            refreshPresetPreview();
        }
        updateModeButtons();
    }

    private void refreshPresetPreview() {
        preview = selectedPreset.graph();
        validation = validatePreview(preview, ProgramResources.recommendedFor(preview));
        updateTheoremStatementGeometry();
    }

    private void updateModeButtons() {
        updateTabFocusOrder();
        if (workflowSeal != null) {
            workflowSeal.setWidth(workflowSealWidth());
        }
        boolean savedVisible = currentTab == ProgrammerTab.SAVED;
        boolean presetsVisible = currentTab == ProgrammerTab.PRESETS;
        boolean customVisible = currentTab == ProgrammerTab.CUSTOM;
        if (theoremStatement != null) {
            theoremStatement.visible = presetsVisible;
            theoremStatement.active = presetsVisible;
        }
        if (inspectButton != null) {
            inspectButton.visible = preview != null;
            inspectButton.active = preview != null;
        }
        InscriptionTarget storedInscription = storedInscription();
        Optional<ProgramGraph> storedProgram = storedInscription.program();
        boolean hasStoredProgram = storedProgram.isPresent();
        boolean previewIsInscribed = preview != null && storedProgram.filter(preview::equals).isPresent();
        boolean inscriptionPending = inscriptionFeedback.pending();
        boolean customMatchesStored = customMatchesStored(previewIsInscribed);
        ProofWorkflowState workflow = workflowState();
        observedWorkflow = workflow;
        MathButton.Tone resourceTone = workflow == ProofWorkflowState.CAST_READY
                ? MathButton.Tone.INSPECTION
                : MathButton.Tone.RESOURCE;
        observedInscription = storedInscription;
        observedInscriptionInitialized = true;
        for (Button button : savedButtons) {
            button.visible = savedVisible;
            button.active = savedVisible;
        }
        if (editSavedCustomButton != null) {
            boolean editableProof = !ProgramStorage.getCustomActions(currentStack()).isEmpty();
            editSavedCustomButton.active = savedVisible && editableProof;
            editSavedCustomButton.setTooltip(Tooltip.create(Component.translatable(editableProof
                    ? "screen.mathmod.rune_programmer.edit_custom_hint"
                    : "screen.mathmod.rune_programmer.edit_theorem_disabled_hint")));
        }
        if (savedResourcesButton != null) {
            savedResourcesButton.active = savedVisible && hasStoredProgram;
            savedResourcesButton.setTone(resourceTone);
            savedResourcesButton.setTooltip(Tooltip.create(Component.translatable(hasStoredProgram
                    ? resourceTooltipKey(true, inscriptionPending, workflow)
                    : "screen.mathmod.rune_programmer.resources_missing_program_hint")));
        }
        if (savedClearButton != null) {
            savedClearButton.active = savedVisible && hasStoredProgram && !inscriptionPending;
            savedClearButton.setTooltip(Tooltip.create(Component.translatable(
                    ProgrammerActionGuidance.clearTooltipKey(hasStoredProgram, inscriptionPending)
            )));
        }
        for (Button button : presetButtons) {
            button.visible = presetsVisible;
            button.active = presetsVisible;
        }
        if (savePresetButton != null) {
            boolean presetUnlocked = isPresetUnlocked(selectedPreset);
            savePresetButton.active = presetsVisible
                    && presetUnlocked
                    && !inscriptionPending
                    && !previewIsInscribed;
            savePresetButton.setMessage(Component.translatable(previewIsInscribed
                    ? "screen.mathmod.rune_programmer.inscribed_button"
                    : "screen.mathmod.rune_programmer.inscribe"));
            savePresetButton.setTooltip(Tooltip.create(Component.translatable(
                    !presetUnlocked
                            ? "screen.mathmod.rune_programmer.conjecture_locked_hint"
                            : inscriptionPending
                            ? "screen.mathmod.rune_programmer.inscription_pending_hint"
                            : previewIsInscribed
                                    ? "screen.mathmod.rune_programmer.already_inscribed_hint"
                                    : "screen.mathmod.rune_programmer.inscribe_hint"
            )));
        }
        if (presetResourcesButton != null) {
            presetResourcesButton.active = presetsVisible && previewIsInscribed && !inscriptionPending;
            presetResourcesButton.setTone(resourceTone);
            presetResourcesButton.setTooltip(Tooltip.create(Component.translatable(
                    resourceTooltipKey(previewIsInscribed, inscriptionPending, workflow)
            )));
        }
        if (presetClearButton != null) {
            presetClearButton.active = presetsVisible && hasStoredProgram && !inscriptionPending;
            presetClearButton.setTooltip(Tooltip.create(Component.translatable(
                    ProgrammerActionGuidance.clearTooltipKey(hasStoredProgram, inscriptionPending)
            )));
        }
        for (Button button : customButtons) {
            button.visible = customVisible;
            button.active = customVisible;
        }
        if (customNameBox != null) {
            customNameBox.visible = customVisible;
            customNameBox.active = customVisible;
        }
        if (customSearchBox != null) {
            customSearchBox.visible = customVisible;
            customSearchBox.active = customVisible;
        }
        if (paletteNavigator != null) {
            paletteNavigator.visible = savedVisible || presetsVisible || customVisible;
            paletteNavigator.active = savedVisible
                    || presetsVisible
                    || (customVisible && !orderedActions().isEmpty());
        }
        if (saveCustomButton != null) {
            boolean customKnowledgeAvailable = KnowledgePolicy.canEdit(
                    playerKnowledge(),
                    customWorkspace.actions()
            );
            saveCustomButton.active = customVisible
                    && validation != null
                    && validation.valid()
                    && customKnowledgeAvailable
                    && !inscriptionPending
                    && !customMatchesStored;
            saveCustomButton.setMessage(Component.translatable(customMatchesStored
                    ? "screen.mathmod.rune_programmer.inscribed_button"
                    : "screen.mathmod.rune_programmer.save_custom"));
            saveCustomButton.setTooltip(Tooltip.create(Component.translatable(
                    !customKnowledgeAvailable
                            ? "screen.mathmod.rune_programmer.custom_locked_hint"
                            : inscriptionPending
                            ? "screen.mathmod.rune_programmer.inscription_pending_hint"
                            : customMatchesStored
                                    ? "screen.mathmod.rune_programmer.already_inscribed_custom_hint"
                                    : validation == null || !validation.valid()
                                            ? "screen.mathmod.rune_programmer.save_custom_hint"
                                            : "screen.mathmod.rune_programmer.inscribe_hint"
            )));
        }
        if (undoCustomButton != null) {
            boolean hasWorkspace = !customWorkspace.isEmpty();
            undoCustomButton.active = customVisible && hasWorkspace;
            undoCustomButton.setTooltip(Tooltip.create(Component.translatable(
                    ProgrammerActionGuidance.undoTooltipKey(hasWorkspace)
            )));
        }
        updateResetCustomButton();
        if (customResourcesButton != null) {
            customResourcesButton.active = customVisible && previewIsInscribed && !inscriptionPending;
            customResourcesButton.setTone(resourceTone);
            customResourcesButton.setTooltip(Tooltip.create(Component.translatable(
                    resourceTooltipKey(previewIsInscribed, inscriptionPending, workflow)
            )));
        }
    }

    private void updateTabFocusOrder() {
        if (savedTabButton == null || presetTabButton == null || customTabButton == null) {
            return;
        }
        savedTabButton.setTabOrderGroup(currentTab == ProgrammerTab.SAVED ? -1 : 0);
        presetTabButton.setTabOrderGroup(currentTab == ProgrammerTab.PRESETS ? -1 : 0);
        customTabButton.setTabOrderGroup(currentTab == ProgrammerTab.CUSTOM ? -1 : 0);
    }

    private boolean customMatchesStored(boolean previewIsInscribed) {
        ItemStack stack = currentStack();
        if (!previewIsInscribed || stack == null) {
            return false;
        }
        return ProgramStorage.getName(stack).orElse("")
                .equals(ProgramNames.sanitizeOptional(customSpellName))
                && ProgramStorage.getCustomInvocations(stack).equals(customWorkspace.invocations());
    }

    private static String resourceTooltipKey(
            boolean previewIsInscribed,
            boolean inscriptionPending,
            ProofWorkflowState workflow
    ) {
        if (inscriptionPending) {
            return "screen.mathmod.rune_programmer.resources_pending_hint";
        }
        if (previewIsInscribed && workflow == ProofWorkflowState.CAST_READY) {
            return "screen.mathmod.rune_programmer.resources_ready_hint";
        }
        return previewIsInscribed
                ? "screen.mathmod.rune_programmer.resources_inscribed_hint"
                : "screen.mathmod.rune_programmer.resources_preview_hint";
    }

    private void refreshCustomPreview() {
        if (customWorkspace.isEmpty()) {
            preview = null;
            validation = null;
        } else {
            preview = customWorkspace.toGraph();
            validation = validatePreview(preview, resourcesForPreview());
        }
        updateModeButtons();
    }

    private void loadStoredProgramPreview() {
        ItemStack stack = currentStack();
        if (stack == null) {
            return;
        }
        ProgramStorage.get(stack).ifPresentOrElse(graph -> {
            preview = graph;
            validation = validatePreview(graph, ProgramResources.get(stack));
            currentTab = ProgrammerTab.SAVED;
            List<CustomSpellInvocation> invocations = ProgramStorage.getCustomInvocations(stack);
            if (!invocations.isEmpty()) {
                customWorkspace.loadInvocations(invocations);
                customSpellName = ProgramStorage.getName(stack).orElse("");
                if (customNameBox != null) {
                    setCustomNameBoxValue(customSpellName);
                }
            }
        }, () -> {
            if (currentTab == ProgrammerTab.SAVED) {
                preview = null;
                validation = null;
            }
        });
    }

    private ValidationResult validatePreview(ProgramGraph graph, List<ResourceSelection> resources) {
        if (minecraft != null && minecraft.player != null) {
            ProgramCostPlan plan = PlayerProgramCosts.planFor(minecraft.player, graph, resources);
            return ProgramStorage.validateExecutable(graph, plan.budgetBonus());
        }
        return ProgramStorage.validateExecutable(graph);
    }

    private ItemStack currentStack() {
        if (minecraft == null || minecraft.player == null) {
            return null;
        }
        return minecraft.player.getItemInHand(menu.hand());
    }

    private Optional<ProgramGraph> storedProgram() {
        return storedInscription().program();
    }

    private InscriptionTarget storedInscription() {
        return InscriptionTarget.fromStack(currentStack());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        InscriptionFeedbackTracker.Event event = inscriptionFeedback.tick(storedInscription());
        if (event == InscriptionFeedbackTracker.Event.CONFIRMED) {
            graphScroll = 0;
            loadStoredProgramPreview();
            updateModeButtons();
        } else if (event == InscriptionFeedbackTracker.Event.TIMED_OUT) {
            updateModeButtons();
        }
        InscriptionTarget currentInscription = storedInscription();
        if (!observedInscriptionInitialized || !observedInscription.equals(currentInscription)) {
            if (currentTab == ProgrammerTab.SAVED && !inscriptionFeedback.pending()) {
                loadStoredProgramPreview();
            }
            updateModeButtons();
        }
        if (observedWorkflow != workflowState()) {
            updateModeButtons();
        }
    }

    private void updateCustomName(String value) {
        String sanitized = ProgramNames.sanitizeOptional(value);
        if (!sanitized.equals(customSpellName)) {
            disarmResetCustom();
        }
        customSpellName = sanitized;
        updateCustomNameTooltip();
        updateModeButtons();
        sendCustomNameToServer(customSpellName);
    }

    private void disarmResetCustom() {
        if (!resetCustomArmed) {
            return;
        }
        resetCustomArmed = false;
        updateResetCustomButton();
    }

    private void updateResetCustomButton() {
        if (resetCustomButton == null) {
            return;
        }
        boolean hasWorkspace = !customWorkspace.isEmpty() || !customSpellName.isBlank();
        if (!hasWorkspace) {
            resetCustomArmed = false;
        }
        resetCustomButton.active = currentTab == ProgrammerTab.CUSTOM && hasWorkspace;
        resetCustomButton.setMessage(Component.translatable(resetCustomArmed
                ? "screen.mathmod.rune_programmer.reset_custom_confirm"
                : "screen.mathmod.rune_programmer.reset_custom"));
        resetCustomButton.setFixedDisplayMessage(resetCustomArmed
                ? Component.literal("0?")
                : layout.compact() ? Component.literal("0") : null);
        String tooltipKey;
        if (!hasWorkspace) {
            tooltipKey = "screen.mathmod.rune_programmer.reset_custom_empty_hint";
        } else if (resetCustomArmed) {
            tooltipKey = "screen.mathmod.rune_programmer.reset_custom_confirm_hint";
        } else {
            tooltipKey = "screen.mathmod.rune_programmer.reset_custom_hint";
        }
        resetCustomButton.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
    }

    private void setCustomNameBoxValue(String value) {
        customNameBox.setValue(value);
        customNameBox.setCursorPosition(0);
        customNameBox.setHighlightPos(0);
        updateCustomNameTooltip();
    }

    private void updateCustomNameTooltip() {
        if (customNameBox != null) {
            customNameBox.setTooltip(Tooltip.create(Component.translatable(
                    "screen.mathmod.rune_programmer.custom_name_value",
                    customSpellDisplayName()
            )));
        }
    }

    private String customSpellDisplayName() {
        return customSpellName.isBlank()
                ? Component.translatable("screen.mathmod.rune_programmer.default_custom_name").getString()
                : customSpellName;
    }

    private Component customNameHint(int availableWidth) {
        String hint = Component.translatable("screen.mathmod.rune_programmer.default_custom_name").getString();
        if (font.width(hint) <= availableWidth) {
            return Component.literal(hint);
        }
        String ellipsis = "...";
        int textWidth = Math.max(0, availableWidth - font.width(ellipsis));
        return Component.literal(font.plainSubstrByWidth(hint, textWidth) + ellipsis);
    }

    private void updateCustomSearch(String value) {
        customSearch = value;
        customPaletteScroll = 0;
        customCursor.resize(orderedActions().size());
        updateModeButtons();
    }

    private void sendCustomNameToServer(String name) {
        if (minecraft != null && minecraft.getConnection() != null) {
            PacketDistributor.sendToServer(new UpdateCustomSpellNamePayload(name));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (parameterAction != null) {
            guiGraphics.flush();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
            try {
                renderParameterDialog(guiGraphics, leftPos, topPos);
                for (EditBox box : parameterBoxes) {
                    if (box.visible) {
                        box.render(guiGraphics, mouseX, mouseY, partialTick);
                    }
                }
                parameterApplyButton.render(guiGraphics, mouseX, mouseY, partialTick);
                parameterCancelButton.render(guiGraphics, mouseX, mouseY, partialTick);
            } finally {
                guiGraphics.pose().popPose();
            }
            renderTooltip(guiGraphics, mouseX, mouseY);
            return;
        }
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (UiPreviewHoverPolicy.suppressesContextualHover()) {
            return;
        }
        if (currentTab == ProgrammerTab.PRESETS && isMouseOverPalette(mouseX, mouseY)) {
            TalismanPreset hovered = presetAt(mouseY);
            if (hovered != null) {
                MathTooltipRenderer.render(
                        guiGraphics,
                        font,
                        theoremTooltip(hovered),
                        mouseX,
                        mouseY
                );
            }
        } else if (currentTab == ProgrammerTab.CUSTOM && isMouseOverPalette(mouseX, mouseY)) {
            CustomSpellAction hovered = actionAt(mouseY);
            if (hovered != null) {
                MathTooltipRenderer.render(
                        guiGraphics,
                        font,
                        customActionTooltip(hovered),
                        mouseX,
                        mouseY
                );
            }
        }
        if (currentTab == ProgrammerTab.PRESETS
                && theoremStatement != null
                && theoremStatement.visible
                && theoremStatement.isHovered()) {
            MathTooltipRenderer.render(
                    guiGraphics,
                    font,
                    theoremTooltip(selectedPreset),
                    mouseX,
                    mouseY
            );
            return;
        }
        if (workflowSeal != null && workflowSeal.isHovered()) {
            MathTooltipRenderer.render(guiGraphics, font, workflowTooltip(workflowState()), mouseX, mouseY);
            return;
        }
        if (typeLegend != null && typeLegend.isHovered()) {
            MathTooltipRenderer.render(guiGraphics, font, typeLegendTooltip(), mouseX, mouseY);
            return;
        }
        if (currentTab != ProgrammerTab.CUSTOM && isMouseOverGraph(mouseX, mouseY)) {
            GraphLine hoveredLine = graphLineAt(graphLines(), mouseY);
            if (hoveredLine != null && (!hoveredLine.tooltip().isEmpty() || isGraphLineClipped(hoveredLine))) {
                List<Component> tooltip = hoveredLine.tooltip().isEmpty()
                        ? List.of(MathGuiTheme.tooltipSecondary(Component.literal(hoveredLine.text())))
                        : hoveredLine.tooltip();
                MathTooltipRenderer.render(guiGraphics, font, tooltip, mouseX, mouseY);
            }
        } else if (currentTab == ProgrammerTab.CUSTOM && isMouseOverGraph(mouseX, mouseY)) {
            GraphLine hoveredLine = graphLineAt(customGraphLines(), mouseY);
            if (hoveredLine != null && (!hoveredLine.tooltip().isEmpty() || isGraphLineClipped(hoveredLine))) {
                List<Component> tooltip = hoveredLine.tooltip().isEmpty()
                        ? List.of(MathGuiTheme.tooltipSecondary(Component.literal(hoveredLine.text())))
                        : hoveredLine.tooltip();
                MathTooltipRenderer.render(
                        guiGraphics,
                        font,
                        tooltip,
                        mouseX,
                        mouseY
                );
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        MathGuiTheme.fillChamfered(guiGraphics, x, y, imageWidth, imageHeight, MathGuiTheme.INK);
        MathGuiTheme.outlineChamfered(guiGraphics, x, y, imageWidth, imageHeight, MathGuiTheme.GOLD);
        guiGraphics.hLine(x + 10, x + imageWidth - 11, y + PANEL_TOP - 7, MathGuiTheme.GRID);
        guiGraphics.fill(x + 10, y + PANEL_TOP - 8, x + 42, y + PANEL_TOP - 6, MathGuiTheme.TEAL);

        MathGuiTheme.panel(guiGraphics, x + PALETTE_X, y + PANEL_TOP, PALETTE_WIDTH, imageHeight - PANEL_TOP - PANEL_BOTTOM_PADDING);
        MathGuiTheme.panel(guiGraphics, x + GRAPH_X, y + PANEL_TOP, GRAPH_WIDTH, imageHeight - PANEL_TOP - PANEL_BOTTOM_PADDING);
        MathGuiTheme.drawProofGrid(guiGraphics, x + GRAPH_X + 1, y + PANEL_TOP + 1, GRAPH_WIDTH - 2, imageHeight - PANEL_TOP - PANEL_BOTTOM_PADDING - 2);

        if (currentTab == ProgrammerTab.CUSTOM && customNameBox != null) {
            ProgrammerLayout.Rect nameBounds = layout.customName();
            MathGuiTheme.fillChamfered(
                    guiGraphics,
                    x + nameBounds.x(),
                    y + nameBounds.y(),
                    nameBounds.width(),
                    nameBounds.height(),
                    MathGuiTheme.SURFACE
            );
            MathGuiTheme.outlineChamfered(
                    guiGraphics,
                    x + nameBounds.x(),
                    y + nameBounds.y(),
                    nameBounds.width(),
                    nameBounds.height(),
                    MathGuiTheme.textFieldOutline(
                            customNameBox.isFocused(),
                            customNameBox.isHovered()
                    )
            );
        }
        if (currentTab == ProgrammerTab.CUSTOM && customSearchBox != null) {
            ProgrammerLayout.Rect searchBounds = layout.customSearch();
            MathGuiTheme.fillChamfered(
                    guiGraphics,
                    x + searchBounds.x(),
                    y + searchBounds.y(),
                    searchBounds.width(),
                    searchBounds.height(),
                    MathGuiTheme.SURFACE
            );
            MathGuiTheme.outlineChamfered(
                    guiGraphics,
                    x + searchBounds.x(),
                    y + searchBounds.y(),
                    searchBounds.width(),
                    searchBounds.height(),
                    MathGuiTheme.textFieldOutline(
                            customSearchBox.isFocused(),
                            customSearchBox.isHovered()
                    )
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int workflowWidth = workflowSealWidth();
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, MathGuiTheme.IVORY, false);
        guiGraphics.drawString(font, paletteTitle(), PALETTE_X + TEXT_PADDING, PANEL_TOP + 5, MathGuiTheme.TEAL, false);
        drawClipped(
                guiGraphics,
                graphTitle().getString(),
                GRAPH_X + TEXT_PADDING + workflowWidth + ProofWorkflowPresentation.SEAL_GAP,
                PANEL_TOP + 5,
                GRAPH_WIDTH
                        - TEXT_PADDING * 2
                        - inspectorHeaderReserve()
                        - workflowWidth
                        - ProofWorkflowPresentation.SEAL_GAP,
                MathGuiTheme.GOLD
        );
        renderPalette(guiGraphics, mouseX, mouseY);

        if (currentTab == ProgrammerTab.SAVED) {
            if (preview == null) {
                renderGraphViewport(guiGraphics, wrappedGraphLines(
                        Component.translatable("screen.mathmod.rune_programmer.saved_empty"),
                        MathGuiTheme.IVORY,
                        Integer.MAX_VALUE
                ), mouseX, mouseY);
            } else {
                renderGraphViewport(guiGraphics, graphLines(), mouseX, mouseY);
            }
            return;
        }

        if (currentTab == ProgrammerTab.CUSTOM) {
            renderGraphViewport(guiGraphics, customGraphLines(), mouseX, mouseY);
            return;
        }

        if (preview == null) {
            renderGraphViewport(guiGraphics, wrappedGraphLines(
                    Component.translatable("screen.mathmod.rune_programmer.empty"),
                    MathGuiTheme.IVORY,
                    Integer.MAX_VALUE
            ), mouseX, mouseY);
            return;
        }

        renderGraphViewport(guiGraphics, graphLines(), mouseX, mouseY);
    }

    private void renderParameterDialog(GuiGraphics guiGraphics, int originX, int originY) {
        if (parameterAction == null) {
            return;
        }
        List<AuthoringMetadata.Parameter> parameters = authoringPalette.find(parameterAction)
                .map(form -> form.metadata().parameters()).orElse(List.of());
        int dialogWidth = 252;
        int dialogHeight = parameterDialogHeight(parameters.size());
        int dialogX = originX + (imageWidth - dialogWidth) / 2;
        int dialogY = originY + (imageHeight - dialogHeight) / 2;
        guiGraphics.fill(originX, originY, originX + imageWidth, originY + imageHeight, MathGuiTheme.MODAL_BACKDROP);
        MathGuiTheme.fillChamfered(guiGraphics, dialogX, dialogY, dialogWidth, dialogHeight, MathGuiTheme.INK);
        MathGuiTheme.outlineChamfered(guiGraphics, dialogX, dialogY, dialogWidth, dialogHeight, MathGuiTheme.GOLD);
        guiGraphics.drawString(
                font,
                Component.translatable("screen.mathmod.rune_programmer.parameter.title",
                        Component.translatable(parameterAction.translationKey())),
                dialogX + 10,
                dialogY + 10,
                MathGuiTheme.IVORY,
                false
        );
        for (int index = 0; index < parameters.size(); index++) {
            AuthoringMetadata.Parameter parameter = parameters.get(index);
            int rowY = dialogY + 39 + index * 24;
            guiGraphics.drawString(
                    font,
                    Component.translatable(parameter.translationKey()),
                    dialogX + 10,
                    rowY,
                    MathGuiTheme.MUTED,
                    false
            );
            MathGuiTheme.fillChamfered(
                    guiGraphics,
                    dialogX + 138,
                    dialogY + 32 + index * 24,
                    100,
                    20,
                    MathGuiTheme.SURFACE
            );
            MathGuiTheme.outlineChamfered(
                    guiGraphics,
                    dialogX + 138,
                    dialogY + 32 + index * 24,
                    100,
                    20,
                    MathGuiTheme.textFieldOutline(
                            parameterBoxes.get(index).isFocused(),
                            parameterBoxes.get(index).isHovered()
                    )
            );
        }
        if (!parameterError.isBlank()) {
            drawClipped(
                    guiGraphics,
                    parameterError,
                    dialogX + 10,
                    dialogY + dialogHeight - 24,
                    72,
                    MathGuiTheme.CORAL
            );
        }
    }

    private int workflowSealWidth() {
        if (currentTab == ProgrammerTab.CUSTOM) {
            return ProofWorkflowPresentation.COMPACT_SEAL_WIDTH;
        }
        int fixedHeaderWidth = TEXT_PADDING * 2 + inspectorHeaderReserve();
        return ProofWorkflowPresentation.sealWidth(
                GRAPH_WIDTH,
                fixedHeaderWidth,
                font.width(graphTitle())
        );
    }

    private int inspectorHeaderReserve() {
        return TYPE_LEGEND_TITLE_RESERVE + 38;
    }

    private int theoremStatementInspectorReserve() {
        return layout.compact() ? 26 : inspectorHeaderReserve();
    }

    private int inspectButtonWidth() {
        return layout.compact() ? 20 : 34;
    }

    private void openInspector() {
        if (minecraft == null || preview == null) {
            return;
        }
        ProgramSurface source = switch (currentTab) {
            case SAVED -> ProgramSurface.inscribed(preview);
            case PRESETS -> ProgramSurface.theorem(preview);
            case CUSTOM -> ProgramSurface.guided(GuidedWorkspaceState.create(customSpellName, customWorkspace.invocations()));
        };
        minecraft.setScreen(new RuneInspectorScreen(this, source.inspect(),
                currentTab == ProgrammerTab.SAVED ? menu.functionalProjection() : com.mathmod.program.ScopedFunctionalProjection.graphOnly()));
    }

    private Component paletteTitle() {
        return switch (currentTab) {
            case SAVED -> Component.translatable("screen.mathmod.rune_programmer.saved");
            case CUSTOM -> Component.translatable("screen.mathmod.rune_programmer.forms");
            case PRESETS -> Component.translatable("screen.mathmod.rune_programmer.theorems");
        };
    }

    private Component graphTitle() {
        return switch (currentTab) {
            case CUSTOM -> customAssemblyTitle();
            case PRESETS -> Component.translatable(selectedPreset.nameKey());
            case SAVED -> Component.translatable("screen.mathmod.rune_programmer.graph");
        };
    }

    private Component theoremStatementComponent(TalismanPreset preset) {
        return MathGuiTheme.tooltip(
                Component.translatable("screen.mathmod.rune_programmer.theorem_statement"),
                MathGuiTheme.GOLD
        ).append(MathGuiTheme.tooltipSecondary(Component.literal(": " + preset.formula())));
    }

    private Component theoremProvenanceComponent(TalismanPreset preset) {
        return MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.rune_programmer.theorem_provenance",
                Component.translatable(preset.provenance().translationKey())
        ));
    }

    private Component customAssemblyTitle() {
        if (preview == null) {
            return Component.translatable("screen.mathmod.rune_programmer.assembly");
        }
        return preview.nodes().stream()
                .filter(node -> node.id().equals(preview.outputNodeId()))
                .findFirst()
                .flatMap(node -> ProgramStorage.definition(node.runeId()))
                .<Component>map(definition -> Component.translatable(
                        "screen.mathmod.rune_programmer.assembly_output",
                        RuneTypePresentation.displayName(definition.outputType())
                ))
                .orElseGet(() -> Component.translatable("screen.mathmod.rune_programmer.assembly"));
    }

    private List<GraphLine> graphLines() {
        List<GraphLine> lines = new ArrayList<>();
        appendInscriptionFeedback(lines);
        for (ProgramGraphPresentation.Node presented : ProgramGraphPresentation.nodes(preview)) {
            ProgramNode node = presented.node();
            RuneType outputType = ProgramStorage.definition(node.runeId())
                    .map(definition -> definition.outputType())
                    .orElse(RuneType.UNIT);
            lines.add(GraphLine.withIcon(
                    "#" + presented.number() + " " + runeLabel(node) + " -> "
                            + RuneTypePresentation.displayName(outputType).getString(),
                    iconForRune(node.runeId()),
                    typeColor(outputType),
                    theoremNodeTooltip(presented, outputType)
            ));
        }

        lines.add(GraphLine.spacer(4));
        if (validation != null && validation.valid()) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable(
                            "screen.mathmod.rune_programmer.valid",
                            validation.budgetUsed(),
                            preview.budgetLimit()
                    ),
                    MathGuiTheme.GREEN,
                    2
            ));
        } else if (validation != null) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable("screen.mathmod.rune_programmer.invalid", validation.issues().size()),
                    MathGuiTheme.CORAL,
                    1
            ));
            for (ValidationIssue issue : validation.issues()) {
                lines.addAll(wrappedGraphLines(
                        validationIssueComponent(issue),
                        MathGuiTheme.CORAL_SOFT,
                        Integer.MAX_VALUE
                ));
            }
        }
        appendResourceLines(lines, resourcesForPreview());
        return lines;
    }

    private List<GraphLine> customGraphLines() {
        if (customWorkspace.isEmpty()) {
            return wrappedGraphLines(
                    Component.translatable("screen.mathmod.rune_programmer.custom_empty"),
                    MathGuiTheme.IVORY,
                    Integer.MAX_VALUE
            );
        }

        List<GraphLine> lines = new ArrayList<>();
        appendInscriptionFeedback(lines);
        lines.add(GraphLine.text(
                Component.translatable("screen.mathmod.rune_programmer.custom_tree").getString(),
                MathGuiTheme.MUTED
        ));
        int index = 1;
        for (CustomSpellStep step : customWorkspace.steps()) {
                lines.add(GraphLine.withIcon(
                    index + ". " + Component.translatable(step.action().translationKey()).getString()
                            + customStepArguments(step),
                    iconForRune(step.action().iconRuneId()),
                    MathGuiTheme.IVORY
            ));
            index++;
        }

        lines.add(GraphLine.spacer(4));
        lines.add(GraphLine.text(
                Component.translatable("screen.mathmod.rune_programmer.custom_links").getString(),
                MathGuiTheme.MUTED
        ));
        List<CustomGraphPresentation.Binding> bindings = CustomGraphPresentation.bindings(
                preview,
                customWorkspace.steps()
        );
        if (bindings.isEmpty()) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable("screen.mathmod.rune_programmer.custom_no_links"),
                    MathGuiTheme.MUTED,
                    2
            ));
        } else {
            for (CustomGraphPresentation.Binding binding : bindings) {
                RuneType valueType = ProgramStorage.definition(binding.source().runeId())
                        .map(definition -> definition.outputType())
                        .orElse(RuneType.UNIT);
                String text = bindingText(binding);
                String verboseText = bindingVerboseText(binding);
                lines.add(GraphLine.text(
                        text,
                        typeColor(valueType),
                        List.of(
                                MathGuiTheme.tooltipPrimary(Component.literal(verboseText)),
                                MathGuiTheme.tooltip(Component.translatable(
                                        "screen.mathmod.rune_programmer.binding_explanation",
                                        RuneTypePresentation.displayName(valueType),
                                        bindingInputLabel(binding.inputName())
                                ), MathGuiTheme.TEAL),
                                MathGuiTheme.tooltipSecondary(Component.translatable(
                                        "screen.mathmod.rune_programmer.binding_technical",
                                        binding.source().id(),
                                        binding.target().id(),
                                        binding.inputName()
                                ))
                        )
                ));
            }
        }

        lines.add(GraphLine.spacer(4));
        appendValidationLines(lines);
        appendResourceLines(lines, resourcesForPreview());
        return lines;
    }

    private static String customStepArguments(CustomSpellStep step) {
        if (step.arguments().isEmpty()) {
            return "";
        }
        return step.action().numericParameters().stream()
                .map(parameter -> parameter.key() + "=" + formatNumber(step.arguments()
                        .getOrDefault(parameter.key(), parameter.defaultValue())))
                .collect(java.util.stream.Collectors.joining(", ", " [", "]"));
    }

    private String bindingText(CustomGraphPresentation.Binding binding) {
        String target = binding.targetStep() > 0
                ? "#" + binding.targetStep()
                : CustomGraphPresentation.symbol(binding.target());
        String source;
        if (binding.sourceStep() > 0) {
            source = "#" + binding.sourceStep();
        } else if (binding.sourceLiteral() != null) {
            source = binding.sourceLiteral();
        } else {
            source = CustomGraphPresentation.symbol(binding.source());
        }
        return target + "[" + bindingInputLabel(binding.inputName()) + "] <- " + source;
    }

    private String bindingVerboseText(CustomGraphPresentation.Binding binding) {
        String target = binding.targetStep() > 0
                ? bindingStepLabel(binding.targetStep())
                : runeLabel(binding.target());
        String source;
        if (binding.sourceStep() > 0) {
            source = bindingStepLabel(binding.sourceStep());
        } else if (binding.sourceLiteral() != null) {
            source = binding.sourceLiteral();
        } else {
            source = runeLabel(binding.source());
        }
        return target + "[" + bindingInputLabel(binding.inputName()) + "] <- " + source;
    }

    private String bindingStepLabel(int stepNumber) {
        int index = stepNumber - 1;
        if (index < 0 || index >= customWorkspace.steps().size()) {
            return "#" + stepNumber;
        }
        return "#" + stepNumber + " "
                + Component.translatable(customWorkspace.steps().get(index).action().translationKey()).getString();
    }

    private static String runeLabel(ProgramNode node) {
        String path = shortRuneId(node.runeId());
        String key = "rune.mathmod." + path;
        return I18n.exists(key) ? Component.translatable(key).getString() : humanize(path);
    }

    private static String bindingInputLabel(String inputName) {
        String key = "screen.mathmod.rune_programmer.input." + inputName;
        return I18n.exists(key) ? Component.translatable(key).getString() : humanize(inputName);
    }

    private static String humanize(String value) {
        StringBuilder result = new StringBuilder(value.length());
        boolean capitalize = true;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character == '_' || character == '-') {
                result.append(' ');
                capitalize = true;
            } else if (capitalize) {
                result.append(Character.toUpperCase(character));
                capitalize = false;
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    private void appendResourceLines(List<GraphLine> lines, List<ResourceSelection> resources) {
        if (preview == null || minecraft == null || minecraft.player == null) {
            return;
        }
        ProgramCostPlan plan = PlayerProgramCosts.planFor(minecraft.player, preview, resources);
        boolean inscribed = storedProgram().filter(preview::equals).isPresent();
        lines.add(GraphLine.spacer(4));
        lines.add(GraphLine.sectionHeading(Component.translatable(inscribed
                ? "screen.mathmod.rune_programmer.resources_inscribed"
                : "screen.mathmod.rune_programmer.resources_projected").getString(), MathGuiTheme.MUTED));
        if (!plan.fixedRequirements().isEmpty()) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable(
                            "screen.mathmod.rune_programmer.fixed_cost",
                            ProgramMessageComponents.selectors(plan.fixedRequirements())
                    ),
                    MathGuiTheme.IVORY,
                    2
            ));
        }
        if (!plan.attributeRequirements().isEmpty()) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable(
                             "screen.mathmod.rune_programmer.attribute_cost",
                             ProgramMessageComponents.attributes(plan.attributeRequirements())
                    ),
                    plan.missingAttributes().isEmpty() ? MathGuiTheme.IVORY : MathGuiTheme.CORAL,
                    2
            ));
        }
        if (!plan.missingItems().isEmpty()) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable(
                            "screen.mathmod.rune_programmer.missing_items",
                            ProgramMessageComponents.selectors(plan.missingItems())
                    ),
                    MathGuiTheme.CORAL,
                    2
            ));
        }
        lines.addAll(wrappedGraphLines(
                Component.translatable(
                        "screen.mathmod.talisman_resources.budget",
                        plan.budgetUsed(),
                        plan.effectiveBudgetLimit(),
                        plan.baseBudgetLimit(),
                        plan.budgetBonus()
                ),
                plan.missingBudget() ? MathGuiTheme.CORAL : MathGuiTheme.GREEN,
                2
        ));
        if (!plan.missingAttributes().isEmpty()) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable(
                             "screen.mathmod.talisman_resources.missing_attributes",
                             ProgramMessageComponents.attributes(plan.missingAttributes())
                    ),
                    MathGuiTheme.CORAL,
                    2
            ));
        }
        if (!resources.isEmpty()) {
            lines.addAll(wrappedGraphLines(
                    Component.literal(resources.stream()
                            .map(selection -> selection.quantity() + "x "
                                    + MaterialPresentation.displayName(selection.materialId()).getString())
                            .collect(java.util.stream.Collectors.joining(", "))),
                    MathGuiTheme.IVORY,
                    3
            ));
        }
    }

    private void appendInscriptionFeedback(List<GraphLine> lines) {
        switch (inscriptionFeedback.status()) {
            case PENDING -> lines.addAll(wrappedGraphLines(
                    Component.translatable("screen.mathmod.rune_programmer.inscription_pending"),
                    MathGuiTheme.GOLD,
                    Integer.MAX_VALUE
            ));
            case SUCCESS -> lines.addAll(wrappedGraphLines(
                    Component.translatable(
                            "screen.mathmod.rune_programmer.inscription_success",
                            inscriptionName
                    ),
                    MathGuiTheme.GREEN,
                    Integer.MAX_VALUE
            ));
            case FAILED -> lines.addAll(wrappedGraphLines(
                    Component.translatable("screen.mathmod.rune_programmer.inscription_failed"),
                    MathGuiTheme.CORAL,
                    Integer.MAX_VALUE
            ));
            case NONE -> {
            }
        }
        if (inscriptionFeedback.status() != InscriptionFeedbackTracker.Status.NONE) {
            lines.add(GraphLine.spacer(3));
        }
    }

    private List<ResourceSelection> resourcesForPreview() {
        ItemStack stack = currentStack();
        if (stack != null && preview != null && ProgramStorage.get(stack).filter(preview::equals).isPresent()) {
            return ProgramResources.get(stack);
        }
        return preview == null ? List.of() : ProgramResources.recommendedFor(preview);
    }

    private void appendValidationLines(List<GraphLine> lines) {
        if (preview == null || validation == null) {
            return;
        }
        if (validation.valid()) {
            lines.addAll(wrappedGraphLines(
                    Component.translatable(
                            "screen.mathmod.rune_programmer.valid",
                            validation.budgetUsed(),
                            preview.budgetLimit()
                    ),
                    MathGuiTheme.GREEN,
                    2
            ));
        } else {
            lines.addAll(wrappedGraphLines(
                    Component.translatable("screen.mathmod.rune_programmer.invalid", validation.issues().size()),
                    MathGuiTheme.CORAL,
                    1
            ));
            for (ValidationIssue issue : validation.issues()) {
                lines.addAll(wrappedGraphLines(
                        validationIssueComponent(issue),
                        MathGuiTheme.CORAL_SOFT,
                        Integer.MAX_VALUE
                ));
            }
        }
    }

    private Component validationIssueComponent(ValidationIssue issue) {
        if (issue.localized()) {
            return Component.translatable(
                    issue.messageKey(),
                    localizedValidationArguments(issue)
            );
        }
        return Component.literal(issue.message());
    }

    private Object[] localizedValidationArguments(ValidationIssue issue) {
        List<String> arguments = issue.messageArguments();
        Object[] localized = arguments.toArray();
        if ("validation.mathmod.type_mismatch".equals(issue.messageKey()) && arguments.size() >= 3) {
            localized[1] = RuneTypePresentation.displayName(arguments.get(1));
            localized[2] = RuneTypePresentation.displayName(arguments.get(2));
        } else if ("validation.mathmod.output_not_unit".equals(issue.messageKey()) && arguments.size() >= 2) {
            localized[0] = RuneTypePresentation.displayName(arguments.get(0));
            localized[1] = RuneTypePresentation.displayName(arguments.get(1));
        }
        return localized;
    }

    private void renderGraphViewport(
            GuiGraphics guiGraphics,
            List<GraphLine> lines,
            int mouseX,
            int mouseY
    ) {
        int x = GRAPH_X + TEXT_PADDING;
        int y = graphViewportY();
        int width = GRAPH_WIDTH - TEXT_PADDING * 2 - 6;
        int height = imageHeight - PANEL_BOTTOM_PADDING - y - 4;
        int contentHeight = contentHeight(lines);
        int maxScroll = maxGraphScroll(contentHeight);
        graphScroll = Math.min(graphScroll, maxScroll);

        guiGraphics.enableScissor(leftPos + GRAPH_X + 1, topPos + y - 2, leftPos + GRAPH_X + GRAPH_WIDTH - 1, topPos + y + height);
        int row = y - graphScroll;
        for (GraphLine line : lines) {
            if (line.height() == 0) {
                continue;
            }
            if (PaletteCursor.rowFits(row, line.requiredVisibleHeight(), y, height)) {
                if (line.icon() != null) {
                    MathGuiTheme.fillChamfered(
                            guiGraphics,
                            x,
                            row,
                            width,
                            line.height() - 2,
                            MathGuiTheme.SURFACE_RAISED_SOFT
                    );
                    MathGuiTheme.outlineChamfered(
                            guiGraphics,
                            x,
                            row,
                            width,
                            line.height() - 2,
                            MathGuiTheme.BORDER_STRONG
                    );
                    guiGraphics.fill(x + 3, row + 3, x + 5, row + line.height() - 5, line.color());
                    guiGraphics.blit(line.icon(), x + 8, row + 2, 0.0F, 0.0F, RUNE_ICON_SIZE, RUNE_ICON_SIZE, 16, 16);
                    drawClipped(guiGraphics, line.text(), x + RUNE_ICON_SIZE + 12, row + 3, width - RUNE_ICON_SIZE - 16, MathGuiTheme.IVORY);
                } else if (line.sequence() != null) {
                    guiGraphics.drawString(font, line.sequence(), x, row, line.color(), false);
                } else {
                    drawClipped(guiGraphics, line.text(), x, row, width, line.color());
                }
            }
            row += line.height();
        }
        guiGraphics.disableScissor();

        renderScrollbar(
                guiGraphics,
                ScrollbarLayout.geometry(
                        GRAPH_X + GRAPH_WIDTH - 5,
                        y - 2,
                        height + 2,
                        height,
                        contentHeight,
                        graphScroll,
                        maxScroll
                ),
                MathGuiTheme.MUTED,
                mouseX - leftPos,
                mouseY - topPos,
                draggedScrollbar == ScrollTarget.GRAPH
        );
    }

    private List<GraphLine> wrappedGraphLines(Component text, int color, int maxLines) {
        List<GraphLine> lines = new ArrayList<>();
        int width = GRAPH_WIDTH - TEXT_PADDING * 2 - 6;
        int count = 0;
        for (FormattedCharSequence line : font.split(text, width)) {
            if (count >= maxLines) {
                break;
            }
            lines.add(GraphLine.text(line, color));
            count++;
        }
        return lines;
    }

    private int contentHeight(List<GraphLine> lines) {
        return lines.stream().mapToInt(GraphLine::height).sum();
    }

    private int maxGraphScroll(int contentHeight) {
        int viewportHeight = imageHeight - PANEL_BOTTOM_PADDING - graphViewportY() - 4;
        return Math.max(0, contentHeight - viewportHeight);
    }

    private int graphViewportY() {
        return PANEL_TOP + (currentTab == ProgrammerTab.PRESETS
                ? TheoremStatementGeometry.graphViewportOffsetForRenderedLineCount(theoremStatementLineCount())
                : DEFAULT_GRAPH_VIEWPORT_OFFSET);
    }

    private int theoremStatementLineCount() {
        if (font == null || selectedPreset == null || theoremStatement == null) {
            return 2;
        }
        return TheoremStatementGeometry.effectiveLineCount(theoremStatementLines().size());
    }

    private List<FormattedCharSequence> theoremStatementLines() {
        int width = Math.max(0, theoremStatement.getWidth() - 7);
        List<FormattedCharSequence> lines = TheoremStatementPresentation.lines(
                font,
                selectedPreset.formula(),
                width
        );
        return lines;
    }

    private void updateTheoremStatementGeometry() {
        if (theoremStatement != null) {
            theoremStatement.setHeight(TheoremStatementGeometry.heightForRenderedLineCount(
                    theoremStatementLines().size()
            ));
        }
    }

    private void renderPalette(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (currentTab == ProgrammerTab.SAVED) {
            renderSavedPalette(guiGraphics, mouseX, mouseY);
            return;
        }
        if (currentTab == ProgrammerTab.CUSTOM) {
            renderCustomPalette(guiGraphics, mouseX, mouseY);
            return;
        }
        renderPresetPalette(guiGraphics, mouseX, mouseY);
    }

    private void renderPresetPalette(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = PALETTE_X + 5;
        int y = PANEL_TOP + 22;
        int width = PALETTE_WIDTH - 10;
        int height = imageHeight - PANEL_BOTTOM_PADDING - y - 4;
        int contentHeight = presetContentHeight();
        int maxScroll = maxPresetPaletteScroll();
        presetPaletteScroll = Math.min(presetPaletteScroll, maxScroll);

        guiGraphics.enableScissor(leftPos + PALETTE_X + 1, topPos + y - 2, leftPos + PALETTE_X + PALETTE_WIDTH - 1, topPos + y + height);
        int row = y - presetPaletteScroll;
        for (TalismanPreset.Category category : TalismanPreset.Category.values()) {
            if (PaletteCursor.rowFits(
                    row,
                    CATEGORY_ROW_HEIGHT + PRESET_ROW_HEIGHT,
                    y,
                    height
            )) {
                guiGraphics.drawString(
                        font,
                        Component.translatable(category.translationKey()),
                        x + 3,
                        row + 3,
                        categoryColor(category),
                        false
                );
                guiGraphics.hLine(x + 74, x + width - 4, row + 7, MathGuiTheme.BORDER_STRONG);
            }
            row += CATEGORY_ROW_HEIGHT;

            for (TalismanPreset preset : presetsIn(category)) {
                boolean unlocked = isPresetUnlocked(preset);
                boolean hovered = mouseX >= leftPos + x
                        && mouseX < leftPos + x + width
                        && mouseY >= topPos + row
                        && mouseY < topPos + row + PRESET_ROW_HEIGHT;
                boolean selected = preset.equals(selectedPreset);
                boolean keyboardFocused = paletteNavigator != null
                        && paletteNavigator.isFocused()
                        && preset.equals(keyboardPreset());
                if (PaletteCursor.rowFits(row, PRESET_ROW_HEIGHT, y, height)) {
                    int background = selected
                            ? MathGuiTheme.SURFACE_SELECTED
                            : hovered || keyboardFocused
                                    ? MathGuiTheme.SURFACE_RAISED
                                    : MathGuiTheme.SURFACE_ROW;
                    MathGuiTheme.fillChamfered(guiGraphics, x, row, width, PRESET_ROW_HEIGHT - 3, background);
                    MathGuiTheme.outlineChamfered(
                            guiGraphics,
                            x,
                            row,
                            width,
                            PRESET_ROW_HEIGHT - 3,
                            keyboardFocused
                                    ? MathGuiTheme.IVORY
                                    : selected || hovered
                                            ? unlocked ? categoryColor(category) : MathGuiTheme.MUTED
                                            : MathGuiTheme.BORDER_SUBTLE
                    );
                    if (selected) {
                        guiGraphics.fill(x + 3, row + 4, x + 5, row + PRESET_ROW_HEIGHT - 7, categoryColor(category));
                    }
                    if (unlocked) {
                        guiGraphics.blit(iconForRune(preset.iconRuneId()), x + 8, row + 6, 0.0F, 0.0F, 14, 14, 16, 16);
                    } else {
                        guiGraphics.drawString(font, "?", x + 12, row + 9, MathGuiTheme.MUTED, false);
                    }
                    String tierLabel = unlocked
                            ? ProgramTiers.requiredTier(preset.graph()).compactLabel()
                            : "?";
                    guiGraphics.drawString(
                            font,
                            tierLabel,
                            x + width - font.width(tierLabel) - 5,
                            row + 4,
                            unlocked ? categoryColor(category) : MathGuiTheme.MUTED,
                            false
                    );
                    drawClipped(
                            guiGraphics,
                            Component.translatable(preset.nameKey()).getString(),
                            x + 27,
                            row + 4,
                            width - 49,
                            unlocked ? MathGuiTheme.IVORY : MathGuiTheme.MUTED
                    );
                    drawClipped(
                            guiGraphics,
                            preset.catalogFormula(),
                            x + 27,
                            row + 15,
                            width - 49,
                            MathGuiTheme.MUTED
                    );
                }
                row += PRESET_ROW_HEIGHT;
            }
        }
        guiGraphics.disableScissor();

        renderScrollbar(
                guiGraphics,
                ScrollbarLayout.geometry(
                        PALETTE_X + PALETTE_WIDTH - 5,
                        y - 2,
                        height + 2,
                        height,
                        contentHeight,
                        presetPaletteScroll,
                        maxScroll
                ),
                categoryColor(selectedPreset.category()),
                mouseX - leftPos,
                mouseY - topPos,
                draggedScrollbar == ScrollTarget.PALETTE
        );
    }

    private void renderSavedPalette(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = PALETTE_X + TEXT_PADDING;
        int y = savedPaletteY();
        int width = PALETTE_WIDTH - TEXT_PADDING * 2 - 6;
        int height = savedPaletteViewportHeight();
        List<GraphLine> lines = savedPaletteLines();
        int contentHeight = contentHeight(lines);
        int maxScroll = maxSavedPaletteScroll(lines);
        savedPaletteScroll = Math.min(savedPaletteScroll, maxScroll);

        guiGraphics.enableScissor(
                leftPos + PALETTE_X + 1,
                topPos + y - 2,
                leftPos + PALETTE_X + PALETTE_WIDTH - 1,
                topPos + y + height
        );
        int row = y - savedPaletteScroll;
        for (GraphLine line : lines) {
            if (PaletteCursor.rowFits(row, line.requiredVisibleHeight(), y, height)) {
                if (line.sequence() != null) {
                    guiGraphics.drawString(font, line.sequence(), x, row, line.color(), false);
                } else {
                    drawClipped(guiGraphics, line.text(), x, row, width, line.color());
                }
            }
            row += line.height();
        }
        guiGraphics.disableScissor();

        renderScrollbar(
                guiGraphics,
                ScrollbarLayout.geometry(
                        PALETTE_X + PALETTE_WIDTH - 5,
                        y - 2,
                        height + 2,
                        height,
                        contentHeight,
                        savedPaletteScroll,
                        maxScroll
                ),
                MathGuiTheme.TEAL,
                mouseX - leftPos,
                mouseY - topPos,
                draggedScrollbar == ScrollTarget.PALETTE
        );
    }

    private List<GraphLine> savedPaletteLines() {
        List<GraphLine> lines = new ArrayList<>();
        ItemStack stack = currentStack();
        if (stack == null || ProgramStorage.get(stack).isEmpty()) {
            lines.addAll(wrappedSavedPaletteLines(
                    Component.translatable("screen.mathmod.rune_programmer.saved_empty"),
                    MathGuiTheme.IVORY,
                    Integer.MAX_VALUE
            ));
            return lines;
        }

        Component name = ProgramStorage.get(stack)
                .map(graph -> ProgramNameComponents.displayName(stack, graph))
                .orElse(Component.translatable("screen.mathmod.rune_programmer.saved_unnamed"));
        lines.addAll(wrappedSavedPaletteLines(name, MathGuiTheme.IVORY, Integer.MAX_VALUE));
        lines.add(GraphLine.spacer(4));

        int customSteps = ProgramStorage.getCustomActions(stack).size();
        Component type = customSteps > 0
                ? Component.translatable("screen.mathmod.rune_programmer.saved_custom", customSteps)
                : Component.translatable("screen.mathmod.rune_programmer.saved_preset");
        lines.addAll(wrappedSavedPaletteLines(
                type,
                MathGuiTheme.MUTED,
                Integer.MAX_VALUE
        ));
        lines.add(GraphLine.spacer(4));
        lines.addAll(wrappedSavedPaletteLines(
                Component.translatable(SavedProofGuidance.translationKey(
                        workflowState(),
                        customSteps > 0
                )),
                MathGuiTheme.IVORY,
                Integer.MAX_VALUE
        ));
        return lines;
    }

    private Component savedPaletteNarration() {
        ItemStack stack = currentStack();
        if (stack == null || ProgramStorage.get(stack).isEmpty()) {
            return Component.translatable("screen.mathmod.rune_programmer.saved_empty");
        }
        Component name = ProgramStorage.get(stack)
                .map(graph -> ProgramNameComponents.displayName(stack, graph))
                .orElse(Component.translatable("screen.mathmod.rune_programmer.saved_unnamed"));
        int customSteps = ProgramStorage.getCustomActions(stack).size();
        Component type = customSteps > 0
                ? Component.translatable("screen.mathmod.rune_programmer.saved_custom", customSteps)
                : Component.translatable("screen.mathmod.rune_programmer.saved_preset");
        Component hint = Component.translatable(SavedProofGuidance.translationKey(
                workflowState(),
                customSteps > 0
        ));
        return Component.empty()
                .append(name)
                .append(". ")
                .append(type)
                .append(". ")
                .append(hint);
    }

    private List<GraphLine> wrappedSavedPaletteLines(Component text, int color, int maxLines) {
        List<GraphLine> lines = new ArrayList<>();
        int width = PALETTE_WIDTH - TEXT_PADDING * 2 - 6;
        int count = 0;
        for (FormattedCharSequence line : font.split(text, width)) {
            if (count >= maxLines) {
                break;
            }
            lines.add(GraphLine.text(line, color));
            count++;
        }
        return lines;
    }

    private int savedPaletteY() {
        return PANEL_TOP + 24;
    }

    private int savedPaletteViewportHeight() {
        return imageHeight - PANEL_BOTTOM_PADDING - savedPaletteY() - 4;
    }

    private int[] savedPaletteRowStarts(List<GraphLine> lines) {
        int[] starts = new int[lines.size()];
        int row = 0;
        for (int index = 0; index < lines.size(); index++) {
            starts[index] = row;
            row += lines.get(index).height();
        }
        return starts;
    }

    private int maxSavedPaletteScroll(List<GraphLine> lines) {
        return PaletteCursor.alignedMaxScroll(
                contentHeight(lines),
                savedPaletteViewportHeight(),
                savedPaletteRowStarts(lines)
        );
    }

    private void moveSavedPaletteScroll(int direction) {
        List<GraphLine> lines = savedPaletteLines();
        int maxScroll = maxSavedPaletteScroll(lines);
        savedPaletteScroll = PaletteCursor.moveAlignedScroll(
                savedPaletteScroll,
                direction,
                maxScroll,
                savedPaletteRowStarts(lines)
        );
    }

    private void moveSavedPaletteScrollPage(int direction) {
        List<GraphLine> lines = savedPaletteLines();
        int maxScroll = maxSavedPaletteScroll(lines);
        int desired = Math.max(
                0,
                Math.min(maxScroll, savedPaletteScroll + direction * savedPaletteViewportHeight())
        );
        int next = savedPaletteScroll;
        int[] rowStarts = savedPaletteRowStarts(lines);
        while (next != desired) {
            int candidate = PaletteCursor.moveAlignedScroll(
                    next,
                    Integer.compare(desired, next),
                    maxScroll,
                    rowStarts
            );
            if (candidate == next || (direction > 0 && candidate >= desired)) {
                next = candidate;
                break;
            }
            if (direction < 0 && candidate <= desired) {
                next = candidate;
                break;
            }
            next = candidate;
        }
        savedPaletteScroll = next;
    }

    private void renderCustomPalette(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = PALETTE_X + TEXT_PADDING;
        int y = customPaletteY();
        int width = PALETTE_WIDTH - TEXT_PADDING * 2;
        int height = imageHeight - PANEL_BOTTOM_PADDING - y - 4;
        int contentHeight = customContentHeight();
        int maxScroll = maxCustomPaletteScroll(contentHeight);
        customPaletteScroll = Math.min(customPaletteScroll, maxScroll);
        List<AuthoringPalettePresentation.Form> visibleActions = orderedForms();
        if (visibleActions.isEmpty()) {
            renderWrapped(
                    guiGraphics,
                    Component.translatable("screen.mathmod.rune_programmer.search_empty", customSearch),
                    x,
                    y + 3,
                    width,
                    MathGuiTheme.MUTED,
                    4
            );
            return;
        }

        guiGraphics.enableScissor(leftPos + PALETTE_X + 1, topPos + y - 2, leftPos + PALETTE_X + PALETTE_WIDTH - 1, topPos + y + height);
        int row = y - customPaletteScroll;
        for (AuthoringMetadata.Category category : authoringPalette.categories()) {
            List<AuthoringPalettePresentation.Form> categoryActions = formsIn(category);
            if (categoryActions.isEmpty()) {
                continue;
            }
            if (PaletteCursor.rowsFit(row, CATEGORY_ROW_HEIGHT, 2, y, height)) {
                guiGraphics.drawString(
                        font,
                        Component.translatable(category.translationKey()),
                        x,
                        row + 3,
                        customCategoryColor(category.categoryId()),
                        false
                );
                guiGraphics.hLine(x + 68, x + width - 3, row + 7, MathGuiTheme.BORDER_STRONG);
            }
            row += CATEGORY_ROW_HEIGHT;

            for (AuthoringPalettePresentation.Form form : categoryActions) {
                CustomSpellAction action = form.legacyAction().orElse(null);
                if (PaletteCursor.rowFits(row, PALETTE_ROW_HEIGHT, y, height)) {
                    boolean unlocked = action != null && isActionUnlocked(action);
                    boolean hovered = mouseX >= leftPos + x
                            && mouseX < leftPos + x + width
                            && mouseY >= topPos + row
                            && mouseY < topPos + row + PALETTE_ROW_HEIGHT;
                    boolean keyboardFocused = paletteNavigator != null
                            && paletteNavigator.isFocused()
                            && action == keyboardAction();
                    if (hovered || keyboardFocused) {
                        MathGuiTheme.fillChamfered(guiGraphics, x - 3, row - 2, width + 3, PALETTE_ROW_HEIGHT, MathGuiTheme.SURFACE_RAISED);
                    }
                    if (keyboardFocused) {
                        MathGuiTheme.outlineChamfered(
                                guiGraphics,
                                x - 3,
                                row - 2,
                                width + 3,
                                PALETTE_ROW_HEIGHT,
                                MathGuiTheme.IVORY
                        );
                    }
                    guiGraphics.fill(
                            x - 1,
                            row + 1,
                            x + 1,
                            row + PALETTE_ROW_HEIGHT - 4,
                            unlocked ? typeColor(action.resultType()) : MathGuiTheme.MUTED
                    );
                    if (unlocked) {
                        guiGraphics.blit(iconForRune(form.metadata().icon().runeId().toString()), x + 3, row, 0.0F, 0.0F, RUNE_ICON_SIZE, RUNE_ICON_SIZE, 16, 16);
                    } else {
                        guiGraphics.drawString(font, "?", x + 5, row + 1, MathGuiTheme.MUTED, false);
                    }
                    drawClipped(
                            guiGraphics,
                            layout.compact()
                                    ? form.compactFormula()
                                    : formDisplayName(form),
                            x + RUNE_ICON_SIZE + 7,
                            row,
                            width - RUNE_ICON_SIZE - 7,
                            unlocked ? MathGuiTheme.IVORY : MathGuiTheme.MUTED
                    );
                }
                row += PALETTE_ROW_HEIGHT;
            }
        }
        guiGraphics.disableScissor();

        renderScrollbar(
                guiGraphics,
                ScrollbarLayout.geometry(
                        PALETTE_X + PALETTE_WIDTH - 5,
                        y - 2,
                        height + 2,
                        height,
                        contentHeight,
                        customPaletteScroll,
                        maxScroll
                ),
                MathGuiTheme.TEAL,
                mouseX - leftPos,
                mouseY - topPos,
                draggedScrollbar == ScrollTarget.PALETTE
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (currentTab == ProgrammerTab.SAVED && isMouseOverPalette(mouseX, mouseY)) {
            moveSavedPaletteScroll(-(int) Math.signum(scrollY));
            return true;
        }
        if (currentTab == ProgrammerTab.PRESETS && isMouseOverPalette(mouseX, mouseY)) {
            int maxScroll = maxPresetPaletteScroll();
            presetPaletteScroll = PaletteCursor.moveAlignedScroll(
                    presetPaletteScroll,
                    -(int) Math.signum(scrollY),
                    maxScroll,
                    presetRowStarts()
            );
            return true;
        }
        if (currentTab == ProgrammerTab.CUSTOM && isMouseOverPalette(mouseX, mouseY)) {
            int maxScroll = maxCustomPaletteScroll(customContentHeight());
            customPaletteScroll = Math.max(
                    0,
                    Math.min(maxScroll, customPaletteScroll - (int) Math.signum(scrollY) * PALETTE_ROW_HEIGHT)
            );
            return true;
        }
        if (isMouseOverGraph(mouseX, mouseY) && preview != null) {
            int maxScroll = maxGraphScroll(contentHeight(currentTab == ProgrammerTab.CUSTOM ? customGraphLines() : graphLines()));
            graphScroll = Math.max(0, Math.min(maxScroll, graphScroll - (int) Math.signum(scrollY) * SCROLL_STEP));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (parameterAction != null) {
            boolean overDialogControl = parameterBoxes.stream()
                    .filter(box -> box.visible)
                    .anyMatch(box -> box.isMouseOver(mouseX, mouseY))
                    || parameterApplyButton.isMouseOver(mouseX, mouseY)
                    || parameterCancelButton.isMouseOver(mouseX, mouseY);
            return overDialogControl ? super.mouseClicked(mouseX, mouseY, button) : true;
        }
        if (button == 0 && beginScrollbarDrag(mouseX, mouseY)) {
            return true;
        }
        if (currentTab == ProgrammerTab.PRESETS && button == 0 && isMouseOverPalette(mouseX, mouseY)) {
            TalismanPreset preset = presetAt(mouseY);
            if (preset != null) {
                presetCursor.select(orderedPresets().indexOf(preset));
                setFocused(paletteNavigator);
                playPaletteSound();
                assemblePreset(preset);
                return true;
            }
        }
        if (currentTab == ProgrammerTab.CUSTOM && button == 0 && isMouseOverPalette(mouseX, mouseY)) {
            CustomSpellAction action = actionAt(mouseY);
            if (action != null) {
                customCursor.select(orderedActions().indexOf(action));
                setFocused(paletteNavigator);
                playPaletteSound();
                if (!isActionUnlocked(action)) {
                    explainLocked(KnowledgePolicy.requirementFor(action).orElseThrow());
                    return true;
                }
                disarmResetCustom();
                if (hasRegistryParameters(action)) {
                    openParameterDialog(action);
                } else {
                    customWorkspace.apply(action);
                    refreshCustomPreview();
                }
                if (!hasRegistryParameters(action) && minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(
                            menu.containerId,
                            RuneProgrammerMenu.CUSTOM_ACTION_BUTTON_BASE + action.ordinal()
                    );
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (button == 0 && draggedScrollbar != ScrollTarget.NONE) {
            updateDraggedScrollbar(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggedScrollbar != ScrollTarget.NONE) {
            draggedScrollbar = ScrollTarget.NONE;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private TalismanPreset presetAt(double mouseY) {
        int y = topPos + PANEL_TOP + 22;
        int viewportY = (int) mouseY - y;
        int viewportHeight = presetPaletteViewportHeight();
        if (viewportY < 0 || viewportY >= viewportHeight) {
            return null;
        }
        int relativeY = viewportY + presetPaletteScroll;
        int row = 0;
        for (TalismanPreset.Category category : TalismanPreset.Category.values()) {
            row += CATEGORY_ROW_HEIGHT;
            for (TalismanPreset preset : presetsIn(category)) {
                if (relativeY >= row
                        && relativeY < row + PRESET_ROW_HEIGHT
                        && PaletteCursor.rowFits(
                                row - presetPaletteScroll,
                                PRESET_ROW_HEIGHT,
                                0,
                                viewportHeight
                        )) {
                    return preset;
                }
                row += PRESET_ROW_HEIGHT;
            }
        }
        return null;
    }

    private CustomSpellAction actionAt(double mouseY) {
        int y = topPos + customPaletteY();
        int viewportY = (int) mouseY - y;
        if (viewportY < 0 || viewportY >= customPaletteViewportHeight()) {
            return null;
        }
        int relativeY = viewportY + customPaletteScroll;
        int row = 0;
        for (AuthoringMetadata.Category category : authoringPalette.categories()) {
            List<AuthoringPalettePresentation.Form> categoryActions = formsIn(category);
            if (categoryActions.isEmpty()) {
                continue;
            }
            row += CATEGORY_ROW_HEIGHT;
            for (AuthoringPalettePresentation.Form form : categoryActions) {
                CustomSpellAction action = form.legacyAction().orElse(null);
                if (relativeY >= row && relativeY < row + PALETTE_ROW_HEIGHT) {
                    return action;
                }
                row += PALETTE_ROW_HEIGHT;
            }
        }
        return null;
    }

    private GraphLine graphLineAt(List<GraphLine> lines, double mouseY) {
        int graphY = graphViewportY();
        int viewportHeight = imageHeight - PANEL_BOTTOM_PADDING - graphY - 4;
        int viewportY = (int) mouseY - (topPos + graphY);
        if (viewportY < 0 || viewportY >= viewportHeight) {
            return null;
        }
        int relativeY = viewportY + graphScroll;
        int row = 0;
        for (GraphLine line : lines) {
            if (relativeY >= row && relativeY < row + line.height()) {
                int displayRow = row - graphScroll;
                return PaletteCursor.rowFits(displayRow, line.height(), 0, viewportHeight)
                        ? line
                        : null;
            }
            row += line.height();
        }
        return null;
    }

    private boolean isGraphLineClipped(GraphLine line) {
        if (line.text().isBlank()) {
            return false;
        }
        int width = GRAPH_WIDTH - TEXT_PADDING * 2 - 6;
        int textWidth = line.icon() == null ? width : width - RUNE_ICON_SIZE - 16;
        return font.width(line.text()) > textWidth;
    }

    private List<Component> theoremNodeTooltip(ProgramGraphPresentation.Node presented, RuneType outputType) {
        ProgramNode node = presented.node();
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(MathGuiTheme.tooltipPrimary(Component.literal(
                "#" + presented.number() + " " + runeLabel(node)
        )));
        tooltip.add(MathGuiTheme.tooltip(Component.translatable(
                "screen.mathmod.rune_programmer.tooltip.output",
                RuneTypePresentation.displayName(outputType)
        ), typeColor(outputType)));
        if (presented.output()) {
            tooltip.add(MathGuiTheme.tooltip(Component.translatable(
                    "screen.mathmod.rune_programmer.tooltip.proof_result"
            ), MathGuiTheme.GOLD));
        }
        ProgramStorage.definition(node.runeId()).ifPresent(definition -> {
            if (!definition.inputs().isEmpty()) {
                tooltip.add(MathGuiTheme.tooltip(
                        Component.translatable("screen.mathmod.rune_programmer.tooltip.inputs"),
                        MathGuiTheme.TEAL
                ));
                definition.inputs().forEach(input -> {
                    ProgramGraphPresentation.InputBinding binding = presented.binding(input.name());
                    if (binding == null) {
                        tooltip.add(MathGuiTheme.tooltip(Component.translatable(
                                 "screen.mathmod.rune_programmer.tooltip.input_unbound",
                                 bindingInputLabel(input.name()),
                                 RuneTypePresentation.displayName(input.type())
                        ), MathGuiTheme.CORAL));
                    } else {
                        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                                "screen.mathmod.rune_programmer.tooltip.input_binding",
                                 bindingInputLabel(input.name()),
                                 binding.sourceNumber(),
                                 runeLabel(binding.source()),
                                 RuneTypePresentation.displayName(input.type())
                        )));
                    }
                });
            }
        });
        if (!node.constants().isEmpty()) {
            tooltip.add(MathGuiTheme.tooltip(
                    Component.translatable("screen.mathmod.rune_programmer.tooltip.constants"),
                    MathGuiTheme.GOLD
            ));
            node.constants().entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByKey())
                    .forEach(entry -> tooltip.add(MathGuiTheme.tooltipSecondary(Component.literal(
                            entry.getKey() + " = " + entry.getValue()
                    ))));
        }
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.rune_programmer.tooltip.technical_node",
                node.id(),
                node.runeId()
        )));
        return tooltip;
    }

    private boolean isMouseOverGraph(double mouseX, double mouseY) {
        int x = leftPos + GRAPH_X;
        int y = topPos + PANEL_TOP;
        return mouseX >= x
                && mouseX < x + GRAPH_WIDTH
                && mouseY >= y
                && mouseY < topPos + imageHeight - PANEL_BOTTOM_PADDING;
    }

    private boolean isMouseOverPalette(double mouseX, double mouseY) {
        int x = leftPos + PALETTE_X;
        int y = topPos + PANEL_TOP;
        return mouseX >= x
                && mouseX < x + PALETTE_WIDTH
                && mouseY >= y
                && mouseY < topPos + imageHeight - PANEL_BOTTOM_PADDING;
    }

    private int maxCustomPaletteScroll(int contentHeight) {
        int viewportHeight = customPaletteViewportHeight();
        return Math.max(0, contentHeight - viewportHeight);
    }

    private List<TalismanPreset> presetsIn(TalismanPreset.Category category) {
        return ProgramPresets.talismanPresets().stream()
                .filter(preset -> preset.category() == category)
                .toList();
    }

    private List<AuthoringPalettePresentation.Form> formsIn(AuthoringMetadata.Category category) {
        return authoringPalette.forms(category.categoryId()).stream()
                .filter(this::matchesCustomSearch)
                .toList();
    }

    private boolean matchesCustomSearch(AuthoringPalettePresentation.Form form) {
        CustomSpellAction action = form.legacyAction().orElse(null);
        return PaletteSearch.matches(
                customSearch,
                formDisplayName(form),
                Component.translatable(categoryTranslationKey(form.metadata().categoryId())).getString(),
                form.metadata().outputHint().orElse(""),
                form.metadata().icon().runeId().toString(),
                form.metadata().formId().toString(),
                form.compactFormula(),
                action == null ? form.technicalName() : action.name()
        );
    }

    private List<TalismanPreset> orderedPresets() {
        List<TalismanPreset> presets = new ArrayList<>();
        for (TalismanPreset.Category category : TalismanPreset.Category.values()) {
            presets.addAll(presetsIn(category));
        }
        return presets;
    }

    private List<AuthoringPalettePresentation.Form> orderedForms() {
        return authoringPalette.categories().stream().flatMap(category -> formsIn(category).stream()).toList();
    }

    private List<CustomSpellAction> orderedActions() {
        return orderedForms().stream().flatMap(form -> form.legacyAction().stream()).toList();
    }

    private TalismanPreset keyboardPreset() {
        List<TalismanPreset> presets = orderedPresets();
        return presets.get(Math.min(presetCursor.index(), presets.size() - 1));
    }

    private CustomSpellAction keyboardAction() {
        List<CustomSpellAction> actions = orderedActions();
        return actions.isEmpty() ? null : actions.get(Math.min(customCursor.index(), actions.size() - 1));
    }

    private void movePaletteCursor(int distance) {
        if (currentTab == ProgrammerTab.PRESETS) {
            presetCursor.resize(orderedPresets().size());
            presetCursor.move(distance);
            ensurePresetCursorVisible();
        } else if (currentTab == ProgrammerTab.CUSTOM) {
            customCursor.resize(orderedActions().size());
            customCursor.move(distance);
            ensureCustomCursorVisible();
        }
    }

    private void movePaletteCursorToEdge(boolean last) {
        PaletteCursor cursor = currentTab == ProgrammerTab.PRESETS ? presetCursor : customCursor;
        if (last) {
            cursor.last();
        } else {
            cursor.first();
        }
        if (currentTab == ProgrammerTab.PRESETS) {
            ensurePresetCursorVisible();
        } else if (currentTab == ProgrammerTab.CUSTOM) {
            ensureCustomCursorVisible();
        }
    }

    private void activatePaletteCursor() {
        if (currentTab == ProgrammerTab.PRESETS) {
            playPaletteSound();
            assemblePreset(keyboardPreset());
            return;
        }
        if (currentTab == ProgrammerTab.CUSTOM) {
            CustomSpellAction action = keyboardAction();
            if (action == null) {
                return;
            }
            playPaletteSound();
            if (!isActionUnlocked(action)) {
                explainLocked(KnowledgePolicy.requirementFor(action).orElseThrow());
                return;
            }
            disarmResetCustom();
            if (hasRegistryParameters(action)) {
                openParameterDialog(action);
            } else {
                customWorkspace.apply(action);
                refreshCustomPreview();
            }
            if (!hasRegistryParameters(action) && minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(
                        menu.containerId,
                        RuneProgrammerMenu.CUSTOM_ACTION_BUTTON_BASE + action.ordinal()
                );
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (parameterAction != null && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeParameterDialog();
            return true;
        }
        if (parameterAction != null && keyCode == GLFW.GLFW_KEY_ENTER) {
            applyParameterDialog();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void playPaletteSound() {
        if (minecraft != null && paletteNavigator != null) {
            paletteNavigator.playDownSound(minecraft.getSoundManager());
        }
    }

    private boolean hasRegistryParameters(CustomSpellAction action) {
        return authoringPalette.find(action).map(form -> !form.metadata().parameters().isEmpty()).orElse(false);
    }

    private void ensurePresetCursorVisible() {
        TalismanPreset target = keyboardPreset();
        int row = 0;
        for (TalismanPreset.Category category : TalismanPreset.Category.values()) {
            row += CATEGORY_ROW_HEIGHT;
            for (TalismanPreset preset : presetsIn(category)) {
                if (preset.equals(target)) {
                    presetPaletteScroll = PaletteCursor.revealAlignedRow(
                            presetPaletteScroll,
                            row,
                            PRESET_ROW_HEIGHT,
                            presetPaletteViewportHeight(),
                            maxPresetPaletteScroll(),
                            presetRowStarts()
                    );
                    return;
                }
                row += PRESET_ROW_HEIGHT;
            }
        }
    }

    private void ensureCustomCursorVisible() {
        CustomSpellAction target = keyboardAction();
        if (target == null) {
            customPaletteScroll = 0;
            return;
        }
        int row = 0;
        for (AuthoringMetadata.Category category : authoringPalette.categories()) {
            List<AuthoringPalettePresentation.Form> categoryActions = formsIn(category);
            if (categoryActions.isEmpty()) {
                continue;
            }
            row += CATEGORY_ROW_HEIGHT;
            for (AuthoringPalettePresentation.Form form : categoryActions) {
                CustomSpellAction action = form.legacyAction().orElse(null);
                if (action == target) {
                    customPaletteScroll = PaletteCursor.revealRow(
                            customPaletteScroll,
                            row,
                            PALETTE_ROW_HEIGHT,
                            customPaletteViewportHeight(),
                            maxCustomPaletteScroll(customContentHeight())
                    );
                    return;
                }
                row += PALETTE_ROW_HEIGHT;
            }
        }
    }

    private Component paletteNarrationTitle() {
        if (currentTab == ProgrammerTab.PRESETS) {
            return Component.translatable(keyboardPreset().nameKey());
        }
        CustomSpellAction action = keyboardAction();
        return action == null
                ? Component.translatable("screen.mathmod.rune_programmer.search_empty", customSearch)
                : registryFormTitle(action);
    }

    private Component paletteNarrationHint() {
        if (currentTab == ProgrammerTab.PRESETS) {
            TalismanPreset preset = keyboardPreset();
            return Component.literal(preset.formula())
                    .append(". ")
                    .append(Component.translatable(preset.hintKey()))
                    .append(". ")
                    .append(Component.translatable(
                            "screen.mathmod.rune_programmer.theorem_provenance",
                            Component.translatable(preset.provenance().translationKey())
                    ));
        }
        CustomSpellAction action = keyboardAction();
        if (action == null) {
            return Component.translatable("screen.mathmod.rune_programmer.search_empty", customSearch);
        }
        CustomActionPreview preview = customWorkspace.preview(action);
        var hint = Component.translatable(
                "screen.mathmod.rune_programmer.tooltip.output",
                RuneTypePresentation.displayName(action.resultType())
        );
        hint.append(". ").append(Component.translatable(
                "screen.mathmod.rune_programmer.custom.expansion",
                preview.addedRunes(),
                preview.addedBindings()
        ));
        if (!preview.inferredInputs().isEmpty()) {
            hint.append(". ").append(Component.translatable(
                    "screen.mathmod.rune_programmer.custom.inferred_inputs",
                    inputNames(preview.inferredInputs())
            ));
        }
        return hint;
    }

    private List<Component> customActionTooltip(CustomSpellAction action) {
        CustomActionPreview preview = customWorkspace.preview(action);
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(MathGuiTheme.tooltipPrimary(registryFormTitle(action).copy()));
        KnowledgePolicy.requirementFor(action)
                .filter(requirement -> !requirement.isSatisfiedBy(playerKnowledge()))
                .ifPresent(requirement -> {
                    tooltip.add(MathGuiTheme.tooltip(Component.translatable(
                            "screen.mathmod.rune_programmer.conjecture_locked",
                            Component.translatable(requirement.titleTranslationKey())
                    ), MathGuiTheme.GOLD));
                    tooltip.add(MathGuiTheme.tooltipSecondary(
                            Component.translatable(requirement.routeTranslationKey())
                    ));
                });
        tooltip.add(Component.translatable(
                "screen.mathmod.rune_programmer.tooltip.output",
                RuneTypePresentation.displayName(preview.resultType())
        ).withStyle(style -> style.withColor(typeColor(preview.resultType()) & 0xFFFFFF)));
        if (preview.inputs().isEmpty()) {
            tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                    "screen.mathmod.rune_programmer.custom.no_inputs"
            )));
        }
        if (!preview.currentInputs().isEmpty()) {
            tooltip.add(Component.translatable(
                    "screen.mathmod.rune_programmer.custom.current_inputs",
                    inputNames(preview.currentInputs())
            ).withStyle(style -> style.withColor(MathGuiTheme.TEAL & 0xFFFFFF)));
        }
        if (!preview.inferredInputs().isEmpty()) {
            tooltip.add(Component.translatable(
                    "screen.mathmod.rune_programmer.custom.inferred_inputs",
                    inputNames(preview.inferredInputs())
            ).withStyle(style -> style.withColor(MathGuiTheme.GOLD & 0xFFFFFF)));
        }
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.rune_programmer.custom.expansion",
                preview.addedRunes(),
                preview.addedBindings()
        )));
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.rune_programmer.custom.append_hint"
        )));
        return tooltip;
    }

    private List<Component> theoremTooltip(TalismanPreset preset) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(MathGuiTheme.tooltipPrimary(Component.translatable(preset.nameKey())));
        KnowledgePolicy.requirementFor(preset)
                .filter(requirement -> !requirement.isSatisfiedBy(playerKnowledge()))
                .ifPresent(requirement -> {
                    tooltip.add(MathGuiTheme.tooltip(Component.translatable(
                            "screen.mathmod.rune_programmer.conjecture_locked",
                            Component.translatable(requirement.titleTranslationKey())
                    ), MathGuiTheme.GOLD));
                    tooltip.add(MathGuiTheme.tooltipSecondary(
                            Component.translatable(requirement.routeTranslationKey())
                    ));
                });
        tooltip.add(theoremStatementComponent(preset));
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(preset.hintKey())));
        tooltip.add(theoremProvenanceComponent(preset));
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.rune_programmer.theorem_formula_hint_first"
        )));
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.rune_programmer.theorem_formula_hint_second"
        )));
        return tooltip;
    }

    private PlayerKnowledge playerKnowledge() {
        return minecraft == null || minecraft.player == null
                ? PlayerKnowledge.empty()
                : KnowledgeService.get(minecraft.player);
    }

    private boolean isPresetUnlocked(TalismanPreset preset) {
        return KnowledgePolicy.canConstruct(playerKnowledge(), preset);
    }

    private boolean isActionUnlocked(CustomSpellAction action) {
        return KnowledgePolicy.canUse(playerKnowledge(), action);
    }

    private void explainLocked(KnowledgeRequirement requirement) {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.displayClientMessage(
                    Component.translatable(requirement.routeTranslationKey()),
                    true
            );
        }
    }

    private static String inputNames(List<CustomInputSlot> inputs) {
        return inputs.stream()
                .map(slot -> Component.translatable(slot.translationKey()).getString())
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }

    private int paletteCursorPosition() {
        if (currentTab == ProgrammerTab.CUSTOM && orderedActions().isEmpty()) {
            return 0;
        }
        return currentTab == ProgrammerTab.PRESETS ? presetCursor.index() + 1 : customCursor.index() + 1;
    }

    private int paletteEntryCount() {
        return currentTab == ProgrammerTab.PRESETS ? orderedPresets().size() : orderedActions().size();
    }

    private int customContentHeight() {
        int categories = 0;
        int actions = 0;
        for (AuthoringMetadata.Category category : authoringPalette.categories()) {
            int categorySize = formsIn(category).size();
            if (categorySize > 0) {
                categories++;
                actions += categorySize;
            }
        }
        return categories * CATEGORY_ROW_HEIGHT + actions * PALETTE_ROW_HEIGHT;
    }

    private int customPaletteY() {
        return PANEL_TOP + CUSTOM_PALETTE_CONTENT_OFFSET;
    }

    private int customPaletteViewportHeight() {
        int availableHeight = imageHeight - PANEL_BOTTOM_PADDING - customPaletteY() - 4;
        return PaletteCursor.wholeRowsHeight(availableHeight, PALETTE_ROW_HEIGHT);
    }

    private int presetContentHeight() {
        return TalismanPreset.Category.values().length * CATEGORY_ROW_HEIGHT
                + ProgramPresets.talismanPresets().size() * PRESET_ROW_HEIGHT;
    }

    private int presetPaletteViewportHeight() {
        return imageHeight - PANEL_BOTTOM_PADDING - (PANEL_TOP + 22) - 4;
    }

    private int[] presetRowStarts() {
        int[] starts = new int[
                TalismanPreset.Category.values().length + ProgramPresets.talismanPresets().size()
        ];
        int index = 0;
        int row = 0;
        for (TalismanPreset.Category category : TalismanPreset.Category.values()) {
            starts[index++] = row;
            row += CATEGORY_ROW_HEIGHT;
            for (TalismanPreset ignored : presetsIn(category)) {
                starts[index++] = row;
                row += PRESET_ROW_HEIGHT;
            }
        }
        return starts;
    }

    private int maxPresetPaletteScroll() {
        return PaletteCursor.alignedMaxScroll(
                presetContentHeight(),
                presetPaletteViewportHeight(),
                presetRowStarts()
        );
    }

    private boolean beginScrollbarDrag(double mouseX, double mouseY) {
        double localX = mouseX - leftPos;
        double localY = mouseY - topPos;
        ScrollbarLayout.Geometry palette = paletteScrollbarGeometry();
        if (palette.contains(localX, localY)) {
            beginScrollbarDrag(ScrollTarget.PALETTE, palette, localY);
            return true;
        }
        ScrollbarLayout.Geometry graph = graphScrollbarGeometry();
        if (graph.contains(localX, localY)) {
            beginScrollbarDrag(ScrollTarget.GRAPH, graph, localY);
            return true;
        }
        return false;
    }

    private void beginScrollbarDrag(
            ScrollTarget target,
            ScrollbarLayout.Geometry geometry,
            double localMouseY
    ) {
        draggedScrollbar = target;
        scrollbarDragOffset = geometry.dragOffset(localMouseY);
        setScrollbarScroll(target, geometry.scrollAt(localMouseY, scrollbarDragOffset));
    }

    private void updateDraggedScrollbar(double mouseY) {
        ScrollbarLayout.Geometry geometry = draggedScrollbar == ScrollTarget.PALETTE
                ? paletteScrollbarGeometry()
                : graphScrollbarGeometry();
        setScrollbarScroll(
                draggedScrollbar,
                geometry.scrollAt(mouseY - topPos, scrollbarDragOffset)
        );
    }

    private void setScrollbarScroll(ScrollTarget target, int requested) {
        if (target == ScrollTarget.GRAPH) {
            List<GraphLine> lines = displayedGraphLines();
            int maxScroll = maxGraphScroll(contentHeight(lines));
            graphScroll = PaletteCursor.nearestAlignedScroll(
                    requested,
                    maxScroll,
                    graphRowStarts(lines)
            );
            return;
        }
        if (target != ScrollTarget.PALETTE) {
            return;
        }
        if (currentTab == ProgrammerTab.PRESETS) {
            presetPaletteScroll = PaletteCursor.nearestAlignedScroll(
                    requested,
                    maxPresetPaletteScroll(),
                    presetRowStarts()
            );
        } else if (currentTab == ProgrammerTab.SAVED) {
            List<GraphLine> lines = savedPaletteLines();
            savedPaletteScroll = PaletteCursor.nearestAlignedScroll(
                    requested,
                    maxSavedPaletteScroll(lines),
                    savedPaletteRowStarts(lines)
            );
        } else {
            customPaletteScroll = ScrollbarLayout.nearestStep(
                    requested,
                    PALETTE_ROW_HEIGHT,
                    maxCustomPaletteScroll(customContentHeight())
            );
        }
    }

    private ScrollbarLayout.Geometry paletteScrollbarGeometry() {
        int y;
        int viewportHeight;
        int contentHeight;
        int scroll;
        int maxScroll;
        if (currentTab == ProgrammerTab.PRESETS) {
            y = PANEL_TOP + 22;
            viewportHeight = presetPaletteViewportHeight();
            contentHeight = presetContentHeight();
            scroll = presetPaletteScroll;
            maxScroll = maxPresetPaletteScroll();
        } else if (currentTab == ProgrammerTab.SAVED) {
            List<GraphLine> lines = savedPaletteLines();
            y = savedPaletteY();
            viewportHeight = savedPaletteViewportHeight();
            contentHeight = contentHeight(lines);
            scroll = savedPaletteScroll;
            maxScroll = maxSavedPaletteScroll(lines);
        } else {
            y = customPaletteY();
            viewportHeight = customPaletteViewportHeight();
            contentHeight = customContentHeight();
            scroll = customPaletteScroll;
            maxScroll = maxCustomPaletteScroll(contentHeight);
        }
        return ScrollbarLayout.geometry(
                PALETTE_X + PALETTE_WIDTH - 5,
                y - 2,
                viewportHeight + 2,
                viewportHeight,
                contentHeight,
                scroll,
                maxScroll
        );
    }

    private ScrollbarLayout.Geometry graphScrollbarGeometry() {
        List<GraphLine> lines = displayedGraphLines();
        int y = graphViewportY();
        int viewportHeight = imageHeight - PANEL_BOTTOM_PADDING - y - 4;
        int contentHeight = contentHeight(lines);
        return ScrollbarLayout.geometry(
                GRAPH_X + GRAPH_WIDTH - 5,
                y - 2,
                viewportHeight + 2,
                viewportHeight,
                contentHeight,
                graphScroll,
                maxGraphScroll(contentHeight)
        );
    }

    private List<GraphLine> displayedGraphLines() {
        if (currentTab == ProgrammerTab.SAVED) {
            return preview == null
                    ? wrappedGraphLines(
                            Component.translatable("screen.mathmod.rune_programmer.saved_empty"),
                            MathGuiTheme.IVORY,
                            Integer.MAX_VALUE
                    )
                    : graphLines();
        }
        if (currentTab == ProgrammerTab.CUSTOM) {
            return customGraphLines();
        }
        return preview == null
                ? wrappedGraphLines(
                        Component.translatable("screen.mathmod.rune_programmer.empty"),
                        MathGuiTheme.IVORY,
                        Integer.MAX_VALUE
                )
                : graphLines();
    }

    private int[] graphRowStarts(List<GraphLine> lines) {
        int[] starts = new int[lines.size()];
        int row = 0;
        for (int index = 0; index < lines.size(); index++) {
            starts[index] = row;
            row += lines.get(index).height();
        }
        return starts;
    }

    private void renderScrollbar(
            GuiGraphics guiGraphics,
            ScrollbarLayout.Geometry geometry,
            int accent,
            double mouseX,
            double mouseY,
            boolean dragging
    ) {
        if (!geometry.scrollable()) {
            return;
        }
        guiGraphics.fill(
                geometry.trackX(),
                geometry.trackY(),
                geometry.trackX() + ScrollbarLayout.VISUAL_WIDTH,
                geometry.trackY() + geometry.trackHeight(),
                MathGuiTheme.SCROLL_TRACK
        );
        boolean hot = dragging || geometry.contains(mouseX, mouseY);
        guiGraphics.fill(
                geometry.trackX() - (hot ? 1 : 0),
                geometry.thumbY(),
                geometry.trackX() + ScrollbarLayout.VISUAL_WIDTH,
                geometry.thumbY() + geometry.thumbHeight(),
                hot ? MathGuiTheme.IVORY : accent
        );
    }

    private static int categoryColor(TalismanPreset.Category category) {
        return switch (category) {
            case MOVEMENT -> MathGuiTheme.TEAL;
            case SENSING -> MathGuiTheme.GOLD;
            case CONTROL -> MathGuiTheme.CORAL;
            case ALCHEMY -> MathGuiTheme.GREEN;
            case METAMAGIC -> MathGuiTheme.GOLD;
        };
    }

    private enum ScrollTarget {
        NONE,
        PALETTE,
        GRAPH
    }

    private static int customCategoryColor(CustomSpellAction.Category category) {
        return switch (category) {
            case SOURCES -> MathGuiTheme.BLUE;
            case ALGEBRA -> MathGuiTheme.GOLD;
            case GEOMETRY -> MathGuiTheme.TEAL;
            case TRIGONOMETRY -> MathGuiTheme.GOLD;
            case CALCULUS -> MathGuiTheme.CORAL_SOFT;
            case LINEAR_ALGEBRA -> MathGuiTheme.TEAL;
            case SYMMETRY -> MathGuiTheme.BLUE;
            case ALCHEMY -> MathGuiTheme.GREEN;
            case METAMAGIC -> MathGuiTheme.GOLD;
            case QUERIES -> MathGuiTheme.GREEN;
            case EFFECTS -> MathGuiTheme.CORAL;
        };
    }

    private String formDisplayName(AuthoringPalettePresentation.Form form) {
        String key = form.metadata().translationKey();
        return form.presentationName(I18n.exists(key) ? Component.translatable(key).getString() : null);
    }

    private Component registryFormTitle(CustomSpellAction action) {
        return authoringPalette.find(action)
                .map(form -> Component.literal(formDisplayName(form)))
                .orElseGet(() -> Component.literal(action.persistentId()));
    }

    private String categoryTranslationKey(com.mathmod.util.NamespacedId categoryId) {
        return authoringPalette.categories().stream()
                .filter(category -> category.categoryId().equals(categoryId))
                .map(AuthoringMetadata.Category::translationKey)
                .findFirst()
                .orElse("mathmod.authoring.technical." + categoryId.path());
    }

    private static int customCategoryColor(com.mathmod.util.NamespacedId categoryId) {
        return switch (AuthoringPalettePresentation.categoryTone(categoryId)) {
            case BLUE -> MathGuiTheme.BLUE;
            case GOLD -> MathGuiTheme.GOLD;
            case TEAL -> MathGuiTheme.TEAL;
            case CORAL_SOFT -> MathGuiTheme.CORAL_SOFT;
            case GREEN -> MathGuiTheme.GREEN;
            case CORAL -> MathGuiTheme.CORAL;
            case MUTED -> MathGuiTheme.MUTED;
        };
    }

    private static int typeColor(RuneType type) {
        return RuneTypeFamily.of(type).color();
    }

    private ProofWorkflowState workflowState() {
        boolean hasPreview = preview != null;
        boolean inscribed = hasPreview && storedProgram().filter(preview::equals).isPresent();
        boolean valid = inscribed
                ? ProgramStorage.validateExecutable(preview, ProgramValidator.MAX_BUDGET_LIMIT).valid()
                : validation != null && validation.valid();
        boolean resourcesReady = false;
        if (inscribed && minecraft != null && minecraft.player != null) {
            resourcesReady = PlayerProgramCosts.planFor(
                    minecraft.player,
                    preview,
                    ProgramResources.get(currentStack())
            ).success();
        }
        return ProofWorkflowState.resolve(
                hasPreview,
                valid,
                inscriptionFeedback.pending(),
                inscribed,
                resourcesReady
        );
    }

    private List<Component> workflowTooltip(ProofWorkflowState state) {
        String key = ProofWorkflowPresentation.translationKey(state);
        return List.of(
                MathGuiTheme.tooltip(Component.translatable(key), workflowColor(state)),
                MathGuiTheme.tooltipSecondary(Component.translatable(key + ".hint"))
        );
    }

    private static int workflowColor(ProofWorkflowState state) {
        return switch (state) {
            case EMPTY -> MathGuiTheme.MUTED;
            case INCOMPLETE, WITNESSES_REQUIRED -> MathGuiTheme.CORAL;
            case DEMONSTRATED -> MathGuiTheme.TEAL;
            case INSCRIBING -> MathGuiTheme.GOLD;
            case CAST_READY -> MathGuiTheme.GREEN;
        };
    }

    private List<Component> typeLegendTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(MathGuiTheme.tooltipPrimary(Component.translatable(
                "screen.mathmod.rune_programmer.type_legend"
        )));
        for (RuneTypeFamily family : RuneTypeFamily.values()) {
            tooltip.add(Component.translatable(family.translationKey())
                    .withStyle(style -> style.withColor(family.color() & 0xFFFFFF)));
        }
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.rune_programmer.type_legend_hint"
        )));
        return tooltip;
    }

    private int renderWrapped(GuiGraphics guiGraphics, Component text, int x, int y, int width, int color) {
        return renderWrapped(guiGraphics, text, x, y, width, color, Integer.MAX_VALUE);
    }

    private int renderWrapped(GuiGraphics guiGraphics, Component text, int x, int y, int width, int color, int maxLines) {
        int row = y;
        int lines = 0;
        for (FormattedCharSequence line : font.split(text, width)) {
            if (lines >= maxLines) {
                break;
            }
            guiGraphics.drawString(font, line, x, row, color, false);
            row += LINE_HEIGHT;
            lines++;
        }
        return row;
    }

    private void drawClipped(GuiGraphics guiGraphics, String text, int x, int y, int width, int color) {
        String clipped = text;
        if (font.width(clipped) > width) {
            clipped = font.plainSubstrByWidth(text, Math.max(0, width - font.width("..."))) + "...";
        }
        guiGraphics.drawString(font, clipped, x, y, color, false);
    }

    private static String shortRuneId(String runeId) {
        int separator = runeId.indexOf(':');
        return separator >= 0 ? runeId.substring(separator + 1) : runeId;
    }

    private static ResourceLocation iconForRune(String runeId) {
        String path = runeId;
        int separator = runeId.indexOf(':');
        if (separator >= 0) {
            path = runeId.substring(separator + 1);
        }
        return ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, "textures/gui/runes/" + path + ".png");
    }

    private final class PaletteNavigator extends AbstractWidget {
        private PaletteNavigator(int x, int y, int width, int height) {
            super(x, y, width, height, Component.translatable("screen.mathmod.rune_programmer.palette"));
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (isFocused()) {
                MathGuiTheme.outlineChamfered(
                        guiGraphics,
                        getX(),
                        getY(),
                        getWidth(),
                        getHeight(),
                        MathGuiTheme.TEAL
                );
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!active) {
                return false;
            }
            if (currentTab == ProgrammerTab.SAVED) {
                if (keyCode == GLFW.GLFW_KEY_UP) {
                    moveSavedPaletteScroll(-1);
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_DOWN) {
                    moveSavedPaletteScroll(1);
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
                    moveSavedPaletteScrollPage(-1);
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
                    moveSavedPaletteScrollPage(1);
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_HOME) {
                    savedPaletteScroll = 0;
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_END) {
                    savedPaletteScroll = maxSavedPaletteScroll(savedPaletteLines());
                    return true;
                }
                return false;
            }
            if (keyCode == GLFW.GLFW_KEY_UP) {
                movePaletteCursor(-1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                movePaletteCursor(1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_HOME) {
                movePaletteCursorToEdge(false);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_END) {
                movePaletteCursorToEdge(true);
                return true;
            }
            if (CommonInputs.selected(keyCode)) {
                activatePaletteCursor();
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            if (currentTab == ProgrammerTab.SAVED) {
                output.add(
                        NarratedElementType.TITLE,
                        Component.translatable("screen.mathmod.rune_programmer.saved_palette_narration")
                );
                output.add(NarratedElementType.HINT, savedPaletteNarration());
                if (maxSavedPaletteScroll(savedPaletteLines()) > 0) {
                    output.add(
                            NarratedElementType.USAGE,
                            Component.translatable("screen.mathmod.rune_programmer.saved_palette_usage")
                    );
                }
                return;
            }
            output.add(
                    NarratedElementType.TITLE,
                    Component.translatable(
                            "screen.mathmod.rune_programmer.palette_narration",
                            paletteNarrationTitle()
                    )
            );
            output.add(
                    NarratedElementType.POSITION,
                    Component.translatable(
                            "screen.mathmod.rune_programmer.palette_position",
                            paletteCursorPosition(),
                            paletteEntryCount()
                    )
            );
            output.add(NarratedElementType.HINT, paletteNarrationHint());
            output.add(
                    NarratedElementType.USAGE,
                    Component.translatable("screen.mathmod.rune_programmer.palette_usage")
            );
        }
    }

    private final class WorkflowSealWidget extends AbstractWidget {
        private WorkflowSealWidget(int x, int y, int width) {
            super(
                    x,
                    y,
                    width,
                    WORKFLOW_SEAL_SIZE,
                    Component.translatable("screen.mathmod.rune_programmer.workflow")
            );
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ProofWorkflowState state = workflowState();
            int color = workflowColor(state);
            MathGuiTheme.fillChamfered(
                    guiGraphics,
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    MathGuiTheme.SURFACE_RAISED
            );
            drawWorkflowMark(guiGraphics, state, color);
            if (getWidth() > ProofWorkflowPresentation.COMPACT_SEAL_WIDTH) {
                drawClipped(
                        guiGraphics,
                        Component.translatable(
                                ProofWorkflowPresentation.shortTranslationKey(state)
                        ).getString(),
                        getX() + 12,
                        getY(),
                        getWidth() - 14,
                        color
                );
            }
            MathGuiTheme.outlineChamfered(
                    guiGraphics,
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    isFocused() ? MathGuiTheme.IVORY : isHovered() ? color : MathGuiTheme.GRID
            );
        }

        private void drawWorkflowMark(GuiGraphics guiGraphics, ProofWorkflowState state, int color) {
            int x = getX() + 2;
            int y = getY() + 2;
            switch (state) {
                case EMPTY -> {
                    guiGraphics.hLine(x + 1, x + 5, y + 1, color);
                    guiGraphics.hLine(x + 1, x + 5, y + 5, color);
                    guiGraphics.vLine(x + 1, y + 1, y + 5, color);
                    guiGraphics.vLine(x + 5, y + 1, y + 5, color);
                }
                case INCOMPLETE -> {
                    for (int offset = 0; offset < 5; offset++) {
                        guiGraphics.fill(x + 1 + offset, y + 1 + offset, x + 2 + offset, y + 2 + offset, color);
                        guiGraphics.fill(x + 5 - offset, y + 1 + offset, x + 6 - offset, y + 2 + offset, color);
                    }
                }
                case DEMONSTRATED -> {
                    guiGraphics.vLine(x + 1, y + 1, y + 5, color);
                    guiGraphics.hLine(x + 1, x + 5, y + 2, color);
                }
                case INSCRIBING -> {
                    guiGraphics.fill(x, y + 3, x + 2, y + 5, color);
                    guiGraphics.fill(x + 3, y + 3, x + 5, y + 5, color);
                    guiGraphics.fill(x + 6, y + 3, x + 7, y + 5, color);
                }
                case WITNESSES_REQUIRED -> {
                    guiGraphics.hLine(x + 1, x + 5, y + 1, color);
                    guiGraphics.hLine(x + 1, x + 5, y + 5, color);
                    guiGraphics.fill(x + 2, y + 2, x + 3, y + 3, color);
                    guiGraphics.fill(x + 3, y + 3, x + 4, y + 4, color);
                    guiGraphics.fill(x + 2, y + 4, x + 3, y + 5, color);
                }
                case CAST_READY -> guiGraphics.fill(x + 1, y + 1, x + 6, y + 6, color);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            ProofWorkflowState state = workflowState();
            String key = ProofWorkflowPresentation.translationKey(state);
            output.add(NarratedElementType.TITLE, Component.translatable(key));
            output.add(NarratedElementType.HINT, Component.translatable(key + ".hint"));
        }
    }

    private final class TheoremStatementWidget extends AbstractWidget {
        private TheoremStatementWidget(int x, int y, int width) {
            super(
                    x,
                    y,
                    width,
                    THEOREM_STATEMENT_HEIGHT,
                    Component.translatable("screen.mathmod.rune_programmer.theorem_statement")
            );
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (isHoveredOrFocused()) {
                MathGuiTheme.outlineChamfered(
                        guiGraphics,
                        getX(),
                        getY(),
                        getWidth(),
                        getHeight(),
                        isFocused() ? MathGuiTheme.TEAL : MathGuiTheme.GOLD
                );
            }
            guiGraphics.fill(
                    getX() + 1,
                    getY() + 2,
                    getX() + 3,
                    getY() + getHeight() - 2,
                    MathGuiTheme.GOLD
            );
            List<FormattedCharSequence> formulaLines = theoremStatementLines();
            for (int index = 0; index < formulaLines.size(); index++) {
                guiGraphics.drawString(
                        font,
                        formulaLines.get(index),
                        getX() + 6,
                        getY() + 1 + index * LINE_HEIGHT,
                        MathGuiTheme.MUTED,
                        false
                );
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(
                    NarratedElementType.TITLE,
                    Component.translatable(
                            "screen.mathmod.rune_programmer.theorem_statement_value",
                            Component.translatable(selectedPreset.nameKey()),
                            selectedPreset.formula()
                    )
            );
            output.add(
                    NarratedElementType.HINT,
                    Component.translatable("screen.mathmod.rune_programmer.theorem_formula_hint_first")
                            .append(" ")
                            .append(Component.translatable(
                                    "screen.mathmod.rune_programmer.theorem_formula_hint_second"
                            ))
            );
        }
    }

    private final class TypeLegendWidget extends AbstractWidget {
        private TypeLegendWidget(int x, int y) {
            super(
                    x,
                    y,
                    TYPE_LEGEND_SIZE,
                    TYPE_LEGEND_SIZE,
                    Component.translatable("screen.mathmod.rune_programmer.type_legend")
            );
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            MathGuiTheme.fillChamfered(
                    guiGraphics,
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    MathGuiTheme.SURFACE_RAISED
            );
            RuneTypeFamily[] families = RuneTypeFamily.values();
            for (int index = 0; index < families.length; index++) {
                int column = index % 2;
                int row = index / 2;
                int swatchX = getX() + 2 + column * 5;
                int swatchY = getY() + 2 + row * 5;
                guiGraphics.fill(
                        swatchX,
                        swatchY,
                        swatchX + 4,
                        swatchY + 4,
                        families[index].color()
                );
            }
            MathGuiTheme.outlineChamfered(
                    guiGraphics,
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    isFocused() ? MathGuiTheme.IVORY : isHovered() ? MathGuiTheme.TEAL : MathGuiTheme.GRID
            );
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(
                    NarratedElementType.TITLE,
                    Component.translatable("screen.mathmod.rune_programmer.type_legend")
            );
            for (RuneTypeFamily family : RuneTypeFamily.values()) {
                output.add(NarratedElementType.HINT, Component.translatable(family.translationKey()));
            }
            output.add(
                    NarratedElementType.HINT,
                    Component.translatable("screen.mathmod.rune_programmer.type_legend_hint")
            );
        }
    }

    private record GraphLine(
            String text,
            FormattedCharSequence sequence,
            ResourceLocation icon,
            int color,
            int height,
            int requiredVisibleHeight,
            List<Component> tooltip
    ) {
        private GraphLine {
            tooltip = List.copyOf(tooltip);
        }

        private static GraphLine withIcon(String text, ResourceLocation icon, int color) {
            return new GraphLine(text, null, icon, color, 16, 16, List.of());
        }

        private static GraphLine withIcon(
                String text,
                ResourceLocation icon,
                int color,
                List<Component> tooltip
        ) {
            return new GraphLine(text, null, icon, color, 16, 16, tooltip);
        }

        private static GraphLine text(FormattedCharSequence text, int color) {
            return new GraphLine("", text, null, color, LINE_HEIGHT, LINE_HEIGHT, List.of());
        }

        private static GraphLine text(String text, int color) {
            return new GraphLine(text, null, null, color, LINE_HEIGHT, LINE_HEIGHT, List.of());
        }

        private static GraphLine text(String text, int color, List<Component> tooltip) {
            return new GraphLine(text, null, null, color, LINE_HEIGHT, LINE_HEIGHT, tooltip);
        }

        private static GraphLine sectionHeading(String text, int color) {
            return new GraphLine(
                    text,
                    null,
                    null,
                    color,
                    LINE_HEIGHT,
                    LINE_HEIGHT * 2,
                    List.of()
            );
        }

        private static GraphLine spacer(int height) {
            return new GraphLine("", null, null, 0, height, height, List.of());
        }
    }

    private enum ProgrammerTab {
        SAVED,
        PRESETS,
        CUSTOM
    }
}

/** Package-private geometry oracle shared by the client screen and its focused regression test. */
final class TheoremStatementGeometry {
    private static final int LINE_HEIGHT = 11;
    private static final int TWO_LINE_GRAPH_VIEWPORT_OFFSET = 37;

    private TheoremStatementGeometry() {
    }

    static int effectiveLineCount(int renderedLineCount) {
        if (renderedLineCount > 3) {
            throw new IllegalStateException("Theorem statement exceeds the supported three-line presentation");
        }
        return Math.max(2, renderedLineCount);
    }

    static int heightForRenderedLineCount(int renderedLineCount) {
        return LINE_HEIGHT * effectiveLineCount(renderedLineCount);
    }

    static int graphViewportOffsetForRenderedLineCount(int renderedLineCount) {
        return TWO_LINE_GRAPH_VIEWPORT_OFFSET
                + Math.max(0, effectiveLineCount(renderedLineCount) - 2) * LINE_HEIGHT;
    }
}
