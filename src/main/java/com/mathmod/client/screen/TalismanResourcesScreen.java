package com.mathmod.client.screen;

import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.network.OpenResourceHelpPayload;
import com.mathmod.program.PlayerProgramCosts;
import com.mathmod.program.ProgramCostLine;
import com.mathmod.program.ProgramCostPlan;
import com.mathmod.program.ProgramCosts;
import com.mathmod.program.ProgramMessageComponents;
import com.mathmod.program.ProgramNameComponents;
import com.mathmod.program.ProgramResources;
import com.mathmod.program.ProgramStorage;
import com.mathmod.program.ResourceSelection;
import com.mathmod.screen.TalismanResourcesMenu;
import com.mathmod.runes.RuneTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TalismanResourcesScreen extends AbstractContainerScreen<TalismanResourcesMenu> {
    private static final int TEXT_COLOR = MathGuiTheme.IVORY;
    private static final int HEADER_COLOR = MathGuiTheme.TEAL;
    private static final int GOOD_COLOR = MathGuiTheme.GREEN;
    private static final int BAD_COLOR = MathGuiTheme.CORAL;
    private static final int MUTED_COLOR = MathGuiTheme.MUTED;
    private static final int LINE_HEIGHT = 11;
    private static final int ROW_HEIGHT = 24;
    private static final int PANEL_TITLE_Y = 58;
    private static final int LEFT_SCROLL_STEP = LINE_HEIGHT * 2;
    private static final int MATERIAL_SCROLL_STEP = ROW_HEIGHT;
    private static final int PREPARED_ACTION_SIZE = 9;
    private static final int MATERIAL_ACTION_SIZE = 11;

    private ResourcesLayout layout = ResourcesLayout.forViewport(512, 400);
    private int PANEL_Y = layout.leftPanel().y();
    private int PANEL_BOTTOM_PADDING = layout.bottomPadding();
    private int LEFT_X = layout.leftPanel().x();
    private int LEFT_W = layout.leftPanel().width();
    private int RIGHT_X = layout.rightPanel().x();
    private int RIGHT_W = layout.rightPanel().width();

    private int leftScroll;
    private int materialScroll;
    private final PaletteCursor addedMaterialCursor = new PaletteCursor(0);
    private final PaletteCursor materialCursor = new PaletteCursor(0);
    private Button clearButton;
    private boolean clearResourcesArmed;
    private ResourceListNavigator addedMaterialsNavigator;
    private ResourceListNavigator materialCatalogNavigator;
    private ScrollTarget draggedScrollbar = ScrollTarget.NONE;
    private int scrollbarDragOffset;

    public TalismanResourcesScreen(TalismanResourcesMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 360;
        this.imageHeight = 248;
        this.inventoryLabelY = this.imageHeight + 10;
    }

    @Override
    protected void init() {
        clearResourcesArmed = false;
        draggedScrollbar = ScrollTarget.NONE;
        applyLayout(ResourcesLayout.forViewport(this.width, this.height, itemOverlayLoaded()));
        super.init();
        List<ProgrammerLayout.Rect> actions = layout.actions();
        ProgrammerLayout.Rect programmer = actions.get(0);
        ProgrammerLayout.Rect clear = actions.get(1);
        ProgrammerLayout.Rect close = actions.get(2);
        Button programmerButton = MathButton.action(
                leftPos + programmer.x(), topPos + programmer.y(), programmer.width(),
                Component.translatable("screen.mathmod.talisman_resources.back_to_proof"),
                button -> openProgrammer(),
                MathButton.Tone.NEUTRAL
        );
        programmerButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.talisman_resources.back_to_proof_hint")
        ));
        addRenderableWidget(programmerButton);
        clearButton = MathButton.action(
                leftPos + clear.x(), topPos + clear.y(), clear.width(),
                Component.translatable("screen.mathmod.talisman_resources.clear_resources"),
                button -> requestClearResources(),
                MathButton.Tone.DANGER
        );
        clearButton.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.talisman_resources.clear_resources_hint")
        ));
        addRenderableWidget(clearButton);
        addRenderableWidget(MathButton.action(
                leftPos + close.x(), topPos + close.y(), close.width(),
                Component.translatable("screen.mathmod.talisman_resources.close"),
                button -> onClose(),
                MathButton.Tone.NEUTRAL
        ));
        addedMaterialsNavigator = addRenderableWidget(new ResourceListNavigator(
                leftPos + LEFT_X - 4,
                topPos + PANEL_Y - 4,
                LEFT_W + 8,
                panelHeight() + 8,
                false
        ));
        materialCatalogNavigator = addRenderableWidget(new ResourceListNavigator(
                leftPos + RIGHT_X - 4,
                topPos + PANEL_Y - 4,
                RIGHT_W + 8,
                panelHeight() + 8,
                true
        ));
        materialCatalogNavigator.setTabOrderGroup(-1);
        Component sumMark = Component.translatable(
                "screen.mathmod.talisman_resources.notation.symbol"
        );
        HeaderNotationLayout headerNotation = HeaderNotationLayout.alignedRight(
                imageWidth,
                titleLabelY,
                font.width(sumMark),
                font.lineHeight
        );
        boolean patchouliLoaded = ModList.get().isLoaded("patchouli");
        ProgrammerLayout.Rect helpBounds = headerNotation.help();
        MathButton helpButton = MathButton.iconAction(
                leftPos + helpBounds.x(),
                topPos + helpBounds.y(),
                helpBounds.width(),
                helpBounds.height(),
                Component.translatable("screen.mathmod.talisman_resources.help_action"),
                Component.literal("?"),
                button -> PacketDistributor.sendToServer(OpenResourceHelpPayload.INSTANCE),
                MathButton.Tone.RESOURCE
        );
        helpButton.active = patchouliLoaded;
        helpButton.setTooltip(Tooltip.create(
                Component.translatable(patchouliLoaded
                        ? "screen.mathmod.talisman_resources.help"
                        : "screen.mathmod.talisman_resources.help_unavailable")
        ));
        addRenderableWidget(helpButton);
        ProgrammerLayout.Rect notationBounds = headerNotation.notation();
        addRenderableWidget(new NotationWidget(
                leftPos + notationBounds.x(),
                topPos + notationBounds.y(),
                notationBounds.width(),
                notationBounds.height(),
                sumMark,
                Component.translatable("screen.mathmod.talisman_resources.notation.sum"),
                MathGuiTheme.GOLD
        ));
        updateClearButton();
        updateResourceNavigators();
    }

    private static boolean itemOverlayLoaded() {
        ModList modList = ModList.get();
        return modList.isLoaded("jei") || modList.isLoaded("emi") || modList.isLoaded("roughlyenoughitems");
    }

    private void applyLayout(ResourcesLayout nextLayout) {
        this.layout = nextLayout;
        this.imageWidth = nextLayout.width();
        this.imageHeight = nextLayout.height();
        this.inventoryLabelY = this.imageHeight + 10;
        this.PANEL_Y = nextLayout.leftPanel().y();
        this.PANEL_BOTTOM_PADDING = nextLayout.bottomPadding();
        this.LEFT_X = nextLayout.leftPanel().x();
        this.LEFT_W = nextLayout.leftPanel().width();
        this.RIGHT_X = nextLayout.rightPanel().x();
        this.RIGHT_W = nextLayout.rightPanel().width();
    }

    private void openProgrammer() {
        disarmClearResources();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(
                    menu.containerId,
                    TalismanResourcesMenu.PROGRAMMER_BUTTON
            );
        }
    }

    private void requestClearResources() {
        if (!clearResourcesArmed) {
            clearResourcesArmed = true;
            updateClearButton();
            return;
        }
        clearResources();
    }

    private void clearResources() {
        disarmClearResources();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, TalismanResourcesMenu.CLEAR_RESOURCES_BUTTON);
        }
    }

    private void disarmClearResources() {
        if (!clearResourcesArmed) {
            return;
        }
        clearResourcesArmed = false;
        updateClearButton();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (!UiPreviewHoverPolicy.suppressesContextualHover()) {
            renderHoverDetails(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        MathGuiTheme.fillChamfered(guiGraphics, x, y, imageWidth, imageHeight, MathGuiTheme.INK);
        MathGuiTheme.outlineChamfered(guiGraphics, x, y, imageWidth, imageHeight, MathGuiTheme.GOLD);
        guiGraphics.hLine(x + 10, x + imageWidth - 11, y + PANEL_Y - 7, MathGuiTheme.GRID);
        guiGraphics.fill(x + 10, y + PANEL_Y - 8, x + 42, y + PANEL_Y - 6, MathGuiTheme.GOLD);
        MathGuiTheme.panel(guiGraphics, x + LEFT_X - 4, y + PANEL_Y - 4, LEFT_W + 8, imageHeight - PANEL_Y - PANEL_BOTTOM_PADDING + 4);
        MathGuiTheme.panel(guiGraphics, x + RIGHT_X - 4, y + PANEL_Y - 4, RIGHT_W + 8, imageHeight - PANEL_Y - PANEL_BOTTOM_PADDING + 4);
        MathGuiTheme.drawProofGrid(guiGraphics, x + LEFT_X - 3, y + PANEL_Y - 3, LEFT_W + 6, imageHeight - PANEL_Y - PANEL_BOTTOM_PADDING + 2);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, MathGuiTheme.IVORY, false);
        renderLoadoutHeading(guiGraphics);
        guiGraphics.drawString(font, Component.translatable("screen.mathmod.talisman_resources.materials"), RIGHT_X, PANEL_TITLE_Y, MathGuiTheme.GOLD, false);
        renderLeftPanel(guiGraphics, mouseX, mouseY);
        renderMaterialPanel(guiGraphics, mouseX, mouseY);
    }

    private void renderLoadoutHeading(GuiGraphics guiGraphics) {
        Component label = Component.translatable("screen.mathmod.talisman_resources.loadout");
        guiGraphics.drawString(font, label, LEFT_X, PANEL_TITLE_Y, MathGuiTheme.TEAL, false);

        Component proofName = displayedProofName();
        if (proofName.getString().isEmpty()) {
            return;
        }

        String separator = ": ";
        LoadoutHeadingLayout heading = loadoutHeadingLayout(label, separator, proofName);
        if (heading.availableNameWidth() <= 0) {
            return;
        }
        guiGraphics.drawString(font, separator, heading.separatorX(), PANEL_TITLE_Y, MathGuiTheme.MUTED, false);
        drawClipped(
                guiGraphics,
                proofName.getString(),
                heading.nameX(),
                PANEL_TITLE_Y,
                heading.availableNameWidth(),
                MathGuiTheme.IVORY
        );
    }

    private LoadoutHeadingLayout loadoutHeadingLayout(
            Component label,
            String separator,
            Component proofName
    ) {
        return LoadoutHeadingLayout.forWidths(
                LEFT_X,
                LEFT_W,
                font.width(label),
                font.width(separator),
                font.width(proofName)
        );
    }

    public Component displayedProofName() {
        if (minecraft == null || minecraft.player == null) {
            return Component.empty();
        }
        ItemStack stack = minecraft.player.getItemInHand(menu.hand());
        return ProgramStorage.get(stack)
                .map(graph -> ProgramNameComponents.displayName(stack, graph))
                .orElse(Component.empty());
    }

    public ProgrammerLayout.Rect loadoutNameBounds() {
        Component proofName = displayedProofName();
        Component label = Component.translatable("screen.mathmod.talisman_resources.loadout");
        LoadoutHeadingLayout heading = loadoutHeadingLayout(label, ": ", proofName);
        return new ProgrammerLayout.Rect(
                leftPos + heading.nameX(),
                topPos + PANEL_TITLE_Y - 1,
                heading.visibleNameWidth(),
                font.lineHeight + 2
        );
    }

    public boolean isLoadoutNameClipped() {
        Component proofName = displayedProofName();
        Component label = Component.translatable("screen.mathmod.talisman_resources.loadout");
        return loadoutHeadingLayout(label, ": ", proofName).nameClipped();
    }

    private void renderLeftPanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<Line> lines = resourceLines();
        List<Integer> selectionIndices = lines.stream().map(Line::selectionIndex).toList();
        int height = leftViewportHeight();
        int contentHeight = lines.size() * LINE_HEIGHT;
        int maxScroll = Math.max(0, contentHeight - height);
        leftScroll = Math.min(leftScroll, maxScroll);
        int hoveredSelection = isOverLeft(mouseX, mouseY) ? selectedIndexAt(mouseY) : -1;
        Map<Integer, Integer> actionRows = new HashMap<>();
        for (int selectionIndex = 0; selectionIndex < currentSelections().size(); selectionIndex++) {
            Optional<SelectableLineLayout.RowBounds> visibleRow = SelectableLineLayout.firstVisibleRow(
                    selectionIndices,
                    selectionIndex,
                    PANEL_Y,
                    LINE_HEIGHT,
                    leftScroll,
                    height
            );
            if (visibleRow.isPresent()) {
                actionRows.put(selectionIndex, visibleRow.orElseThrow().y());
            }
        }

        guiGraphics.enableScissor(leftPos + LEFT_X - 2, topPos + PANEL_Y - 2, leftPos + LEFT_X + LEFT_W + 2, topPos + PANEL_Y + height);
        int row = PANEL_Y - leftScroll;
        for (Line line : lines) {
            if (PaletteCursor.rowsFit(row, LINE_HEIGHT, line.requiredVisibleRows(), PANEL_Y, height)) {
                boolean keyboardFocused = line.selectionIndex() >= 0
                        && addedMaterialsNavigator != null
                        && addedMaterialsNavigator.isFocused()
                        && line.selectionIndex() == addedMaterialCursor.index();
                boolean hovered = line.selectionIndex() >= 0
                        && line.selectionIndex() == hoveredSelection;
                if (hovered || keyboardFocused) {
                    guiGraphics.fill(
                            LEFT_X - 2,
                            row - 1,
                            LEFT_X + LEFT_W,
                            row + LINE_HEIGHT,
                            MathGuiTheme.SURFACE_SELECTED_SOFT
                    );
                    guiGraphics.fill(LEFT_X, row + 1, LEFT_X + 2, row + LINE_HEIGHT - 2, MathGuiTheme.CORAL);
                }
                if (keyboardFocused) {
                    MathGuiTheme.outlineChamfered(
                            guiGraphics,
                            LEFT_X - 2,
                            row - 1,
                            LEFT_W,
                            LINE_HEIGHT + 1,
                            MathGuiTheme.IVORY
                    );
                }
                guiGraphics.drawString(font, line.text(), LEFT_X + (line.selectionIndex() >= 0 ? 5 : 0), row, line.color(), false);
                if (line.selectionIndex() >= 0
                        && actionRows.getOrDefault(line.selectionIndex(), Integer.MIN_VALUE) == row) {
                    renderRowAction(
                            guiGraphics,
                            RowActionAffordance.layout(
                                    LEFT_X,
                                    LEFT_W,
                                    row,
                                    LINE_HEIGHT,
                                    PREPARED_ACTION_SIZE
                            ),
                            "-",
                            MathGuiTheme.CORAL,
                            hovered || keyboardFocused
                    );
                }
            }
            row += LINE_HEIGHT;
        }
        guiGraphics.disableScissor();
        renderScrollbar(
                guiGraphics,
                ScrollbarLayout.geometry(
                        LEFT_X + LEFT_W - 3,
                        PANEL_Y,
                        height,
                        height,
                        contentHeight,
                        leftScroll,
                        maxScroll
                ),
                MathGuiTheme.TEAL,
                mouseX - leftPos,
                mouseY - topPos,
                draggedScrollbar == ScrollTarget.LEFT
        );
    }

    private void renderMaterialPanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<RuneMaterialDefinition> materials = displayedMaterials();
        int height = materialViewportHeight();
        int contentHeight = materials.size() * ROW_HEIGHT;
        int maxScroll = Math.max(0, contentHeight - height);
        materialScroll = Math.min(materialScroll, maxScroll);

        guiGraphics.enableScissor(leftPos + RIGHT_X - 2, topPos + PANEL_Y - 2, leftPos + RIGHT_X + RIGHT_W + 2, topPos + PANEL_Y + height);
        int row = PANEL_Y - materialScroll;
        for (int index = 0; index < materials.size(); index++) {
            RuneMaterialDefinition material = materials.get(index);
            if (PaletteCursor.rowFits(row, ROW_HEIGHT, PANEL_Y, height)) {
                boolean keyboardFocused = materialCatalogNavigator != null
                        && materialCatalogNavigator.isFocused()
                        && index == materialCursor.index();
                boolean hovered = mouseX >= leftPos + RIGHT_X
                        && mouseX < leftPos + RIGHT_X + RIGHT_W
                        && mouseY >= topPos + row
                        && mouseY < topPos + row + ROW_HEIGHT;
                MathGuiTheme.fillChamfered(
                        guiGraphics,
                        RIGHT_X - 1,
                        row - 1,
                        RIGHT_W,
                        ROW_HEIGHT - 2,
                        hovered || keyboardFocused
                                ? MathGuiTheme.SURFACE_RAISED
                                : MathGuiTheme.SURFACE_ROW
                );
                MathGuiTheme.outlineChamfered(
                        guiGraphics,
                        RIGHT_X - 1,
                        row - 1,
                        RIGHT_W,
                        ROW_HEIGHT - 2,
                        keyboardFocused
                                ? MathGuiTheme.IVORY
                                : hovered ? MathGuiTheme.GOLD : MathGuiTheme.BORDER_SUBTLE
                );
                guiGraphics.fill(RIGHT_X + 2, row + 3, RIGHT_X + 4, row + ROW_HEIGHT - 6, material.consumed() ? MathGuiTheme.CORAL : MathGuiTheme.GREEN);
                RowActionAffordance.Geometry action = RowActionAffordance.layout(
                        RIGHT_X,
                        RIGHT_W,
                        row,
                        ROW_HEIGHT,
                        MATERIAL_ACTION_SIZE
                );
                int textX = RIGHT_X + 8;
                int textWidth = RowActionAffordance.textWidth(textX, action);
                drawClipped(
                        guiGraphics,
                        materialDisplayName(material).getString(),
                        textX,
                        row + 2,
                        textWidth,
                        TEXT_COLOR
                );
                drawClipped(
                        guiGraphics,
                        materialSummary(material),
                        textX,
                        row + LINE_HEIGHT + 1,
                        textWidth,
                        MUTED_COLOR
                );
                renderRowAction(
                        guiGraphics,
                        action,
                        "+",
                        MathGuiTheme.TEAL,
                        hovered || keyboardFocused
                );
            }
            row += ROW_HEIGHT;
        }
        guiGraphics.disableScissor();
        renderScrollbar(
                guiGraphics,
                ScrollbarLayout.geometry(
                        RIGHT_X + RIGHT_W - 3,
                        PANEL_Y,
                        height,
                        height,
                        contentHeight,
                        materialScroll,
                        maxScroll
                ),
                MathGuiTheme.GOLD,
                mouseX - leftPos,
                mouseY - topPos,
                draggedScrollbar == ScrollTarget.MATERIALS
        );
    }

    private List<Line> resourceLines() {
        List<Line> lines = new ArrayList<>();
        if (minecraft == null || minecraft.player == null) {
            return lines;
        }

        ItemStack stack = minecraft.player.getItemInHand(menu.hand());
        List<ResourceSelection> selections = ProgramResources.get(stack);
        var graph = ProgramStorage.get(stack);
        var plan = graph.map(value -> PlayerProgramCosts.planFor(minecraft.player, value, selections));
        if (plan.isPresent()) {
            ProgramCostPlan costPlan = plan.orElseThrow();
            addWrapped(lines, Component.translatable("screen.mathmod.talisman_resources.status"), HEADER_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.budget",
                    costPlan.budgetUsed(),
                    costPlan.effectiveBudgetLimit(),
                    costPlan.baseBudgetLimit(),
                    costPlan.budgetBonus()
            ), costPlan.missingBudget() ? BAD_COLOR : GOOD_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.tier",
                    Component.translatable(costPlan.requiredTier().translationKey()),
                    Component.translatable(costPlan.providedTier().translationKey())
            ), costPlan.missingTier() ? BAD_COLOR : GOOD_COLOR);
            addWrapped(lines, Component.translatable(costPlan.success()
                    ? "screen.mathmod.talisman_resources.ready"
                    : "screen.mathmod.talisman_resources.missing"), costPlan.success() ? GOOD_COLOR : BAD_COLOR);
            addOutstandingRequirements(lines, costPlan);
        } else {
            addWrapped(lines, Component.translatable("screen.mathmod.talisman_resources.empty"), TEXT_COLOR);
        }

        addSection(lines, "screen.mathmod.talisman_resources.selected");
        if (selections.isEmpty()) {
            addWrapped(lines, Component.translatable("screen.mathmod.talisman_resources.none"), MUTED_COLOR);
        } else {
            for (int i = 0; i < selections.size(); i++) {
                ResourceSelection selection = selections.get(i);
                addWrappedSelectable(
                        lines,
                        Component.literal(selection.quantity() + "x ")
                                .append(materialDisplayName(selection.materialId())),
                        TEXT_COLOR,
                        i
                );
            }
        }

        plan.ifPresent(costPlan -> {
            addSection(lines, "screen.mathmod.talisman_resources.consumed");
            addCostLines(lines, costPlan.lines().stream().filter(ProgramCostLine::consumed).toList(), TEXT_COLOR);
            addSection(lines, "screen.mathmod.talisman_resources.catalysts");
            addCostLines(lines, costPlan.lines().stream().filter(line -> !line.consumed()).toList(), TEXT_COLOR);

            addSection(lines, "screen.mathmod.talisman_resources.attributes");
            addMap(lines, "screen.mathmod.talisman_resources.required", costPlan.attributeRequirements(), TEXT_COLOR);
            addMap(lines, "screen.mathmod.talisman_resources.provided", costPlan.providedAttributes(), GOOD_COLOR);
        });
        graph.ifPresent(value -> addConstructPreview(lines, value));
        return lines;
    }

    private void addConstructPreview(List<Line> lines, com.mathmod.runes.ProgramGraph graph) {
        if (graph.nodes().stream().noneMatch(node -> node.runeId().equals("mathmod:materialize_construct"))) {
            return;
        }
        try {
            var preview = com.mathmod.program.ConstructPreviewModel.from(graph);
            addSection(lines, "screen.mathmod.talisman_resources.construct_preview");
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_material", preview.materialId()), TEXT_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_scale", preview.scale()), TEXT_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_mass", preview.maximumMassEquivalent()), TEXT_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_motion",
                    preview.angularSpeed(), preview.maximumLaunchSpeed()), TEXT_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_lifetime", preview.maximumLifetimeTicks()), TEXT_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_server"), GOOD_COLOR);
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_physics_estimate"), MUTED_COLOR);
        } catch (IllegalArgumentException ignored) {
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.construct_preview_unavailable"), BAD_COLOR);
        }
    }

    private void addOutstandingRequirements(List<Line> lines, ProgramCostPlan plan) {
        boolean hasMissingItems = !plan.missingItems().isEmpty();
        boolean hasMissingAttributes = !plan.missingAttributes().isEmpty();
        if (!hasMissingItems && !hasMissingAttributes && !plan.missingTier() && !plan.missingBudget()) {
            return;
        }

        addSection(lines, "screen.mathmod.talisman_resources.outstanding");
        if (hasMissingItems) {
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.missing_items",
                    ProgramMessageComponents.selectors(plan.missingItems())
            ), BAD_COLOR);
        }
        if (hasMissingAttributes) {
            addMap(lines, "screen.mathmod.talisman_resources.missing_attributes", plan.missingAttributes(), BAD_COLOR);
        }
        if (plan.missingTier()) {
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.missing_tier",
                    Component.translatable(plan.requiredTier().translationKey()),
                    Component.translatable(plan.providedTier().translationKey())
            ), BAD_COLOR);
        }
        if (plan.missingBudget()) {
            addWrapped(lines, Component.translatable(
                    "screen.mathmod.talisman_resources.missing_budget",
                    plan.missingBudgetAmount()
            ), BAD_COLOR);
        }
    }

    private void addCostLines(List<Line> lines, List<ProgramCostLine> costLines, int color) {
        if (costLines.isEmpty()) {
            addWrapped(lines, Component.translatable("screen.mathmod.talisman_resources.none"), MUTED_COLOR);
            return;
        }
        for (ProgramCostLine line : costLines) {
            addWrapped(lines, ProgramMessageComponents.selectors(
                    Map.of(line.selector(), line.quantity())
            ), color);
        }
    }

    private String materialSummary(RuneMaterialDefinition material) {
        return Component.translatable(
                material.consumed()
                        ? "screen.mathmod.talisman_resources.material_summary_consumed"
                        : "screen.mathmod.talisman_resources.material_summary_catalyst",
                material.budgetBonus(),
                material.attributes().size()
        ).getString();
    }

    private Component materialDisplayName(String materialId) {
        return MaterialPresentation.displayName(materialId);
    }

    private List<RuneMaterialDefinition> displayedMaterials() {
        String languageCode = minecraft == null
                ? "en_us"
                : minecraft.getLanguageManager().getSelected();
        Locale locale = Locale.forLanguageTag(languageCode.replace('_', '-'));
        return MaterialCatalogOrder.localized(
                ProgramResources.materials(),
                material -> materialDisplayName(material).getString(),
                locale
        );
    }

    private Component materialDisplayName(RuneMaterialDefinition material) {
        return MaterialPresentation.displayName(material);
    }

    private void addSection(List<Line> lines, String translationKey) {
        lines.add(new Line("", 0));
        List<FormattedCharSequence> heading = font.split(
                Component.translatable(translationKey),
                Math.max(20, LEFT_W - 6)
        );
        for (int index = 0; index < heading.size(); index++) {
            int remainingHeadingRows = heading.size() - index;
            lines.add(new Line(
                    heading.get(index),
                    HEADER_COLOR,
                    -1,
                    remainingHeadingRows + 1
            ));
        }
    }

    private void addMap(List<Line> lines, String translationKey, Map<String, Integer> values, int color) {
        if (values.isEmpty()) {
            addWrapped(lines, Component.translatable(translationKey, Component.translatable("screen.mathmod.talisman_resources.none")), MUTED_COLOR);
        } else {
            addWrapped(lines, Component.translatable(
                    translationKey,
                    ProgramMessageComponents.attributes(values)
            ), color);
        }
    }

    private void addWrapped(List<Line> lines, Component text, int color) {
        for (FormattedCharSequence sequence : font.split(text, Math.max(20, LEFT_W - 6))) {
            lines.add(new Line(sequence, color));
        }
    }

    private void addWrappedSelectable(List<Line> lines, Component text, int color, int selectionIndex) {
        RowActionAffordance.Geometry action = RowActionAffordance.layout(
                LEFT_X,
                LEFT_W,
                0,
                LINE_HEIGHT,
                PREPARED_ACTION_SIZE
        );
        int width = RowActionAffordance.textWidth(LEFT_X + 5, action);
        for (FormattedCharSequence sequence : font.split(text, Math.max(20, width))) {
            lines.add(new Line(sequence, color, selectionIndex));
        }
    }

    private void renderRowAction(
            GuiGraphics guiGraphics,
            RowActionAffordance.Geometry geometry,
            String symbol,
            int color,
            boolean emphasized
    ) {
        MathGuiTheme.fillChamfered(
                guiGraphics,
                geometry.x(),
                geometry.y(),
                geometry.width(),
                geometry.height(),
                emphasized ? MathGuiTheme.SURFACE_SELECTED_SOFT : MathGuiTheme.SURFACE
        );
        MathGuiTheme.outlineChamfered(
                guiGraphics,
                geometry.x(),
                geometry.y(),
                geometry.width(),
                geometry.height(),
                emphasized ? MathGuiTheme.IVORY : color
        );
        guiGraphics.drawString(
                font,
                symbol,
                geometry.x() + (geometry.width() - font.width(symbol)) / 2,
                geometry.y() + (geometry.height() - font.lineHeight) / 2,
                color,
                false
        );
    }

    private void renderScrollbar(
            GuiGraphics guiGraphics,
            ScrollbarLayout.Geometry geometry,
            int color,
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
                hot ? MathGuiTheme.IVORY : color
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && beginScrollbarDrag(mouseX, mouseY)) {
            return true;
        }
        if (button == 0 && isOverMaterials(mouseX, mouseY)) {
            int index = materialIndexAt(mouseY);
            if (index >= 0 && index < displayedMaterials().size()) {
                materialCursor.select(index);
                focusNavigator(materialCatalogNavigator);
                addDisplayedMaterial(index);
                return true;
            }
        }
        if (button == 0 && isOverLeft(mouseX, mouseY)) {
            int index = selectedIndexAt(mouseY);
            if (index >= 0) {
                addedMaterialCursor.select(index);
                focusNavigator(addedMaterialsNavigator);
                removeAddedMaterial(index);
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isOverMaterials(mouseX, mouseY)) {
            int height = materialViewportHeight();
            int maxScroll = Math.max(0, displayedMaterials().size() * ROW_HEIGHT - height);
            materialScroll = Math.max(
                    0,
                    Math.min(maxScroll, materialScroll - (int) Math.signum(scrollY) * MATERIAL_SCROLL_STEP)
            );
            return true;
        }
        if (isOverLeft(mouseX, mouseY)) {
            int height = leftViewportHeight();
            int maxScroll = Math.max(0, resourceLines().size() * LINE_HEIGHT - height);
            leftScroll = Math.max(
                    0,
                    Math.min(maxScroll, leftScroll - (int) Math.signum(scrollY) * LEFT_SCROLL_STEP)
            );
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private int selectedIndexAt(double mouseY) {
        if (minecraft == null || minecraft.player == null) {
            return -1;
        }
        int localY = (int) mouseY - topPos;
        return SelectableLineLayout.selectionAt(
                resourceSelectionIndices(),
                localY,
                PANEL_Y,
                LINE_HEIGHT,
                leftScroll,
                leftViewportHeight()
        );
    }

    public Optional<ProgrammerLayout.Rect> addedMaterialBounds(int selectionIndex) {
        return SelectableLineLayout.firstVisibleRow(
                resourceSelectionIndices(),
                selectionIndex,
                PANEL_Y,
                LINE_HEIGHT,
                leftScroll,
                leftViewportHeight()
        ).map(row -> new ProgrammerLayout.Rect(
                leftPos + LEFT_X,
                topPos + row.y(),
                LEFT_W,
                row.height()
        ));
    }

    private List<Integer> resourceSelectionIndices() {
        return resourceLines().stream()
                .map(Line::selectionIndex)
                .toList();
    }

    private int materialIndexAt(double mouseY) {
        int relative = (int) mouseY - topPos - PANEL_Y + materialScroll;
        return relative < 0 ? -1 : relative / ROW_HEIGHT;
    }

    private boolean isOverMaterials(double mouseX, double mouseY) {
        return mouseX >= leftPos + RIGHT_X
                && mouseX < leftPos + RIGHT_X + RIGHT_W
                && mouseY >= topPos + PANEL_Y
                && mouseY < topPos + PANEL_Y + materialViewportHeight();
    }

    private boolean isOverLeft(double mouseX, double mouseY) {
        return mouseX >= leftPos + LEFT_X
                && mouseX < leftPos + LEFT_X + LEFT_W
                && mouseY >= topPos + PANEL_Y
                && mouseY < topPos + PANEL_Y + leftViewportHeight();
    }

    private int panelHeight() {
        return imageHeight - PANEL_Y - PANEL_BOTTOM_PADDING - 4;
    }

    private int leftViewportHeight() {
        return PaletteCursor.wholeRowsHeight(panelHeight(), LINE_HEIGHT);
    }

    private int materialViewportHeight() {
        return PaletteCursor.wholeRowsHeight(panelHeight(), ROW_HEIGHT);
    }

    private boolean beginScrollbarDrag(double mouseX, double mouseY) {
        double localX = mouseX - leftPos;
        double localY = mouseY - topPos;
        ScrollbarLayout.Geometry materials = materialScrollbarGeometry();
        if (materials.contains(localX, localY)) {
            beginScrollbarDrag(ScrollTarget.MATERIALS, materials, localY);
            return true;
        }
        ScrollbarLayout.Geometry left = leftScrollbarGeometry();
        if (left.contains(localX, localY)) {
            beginScrollbarDrag(ScrollTarget.LEFT, left, localY);
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
        ScrollbarLayout.Geometry geometry = draggedScrollbar == ScrollTarget.MATERIALS
                ? materialScrollbarGeometry()
                : leftScrollbarGeometry();
        setScrollbarScroll(
                draggedScrollbar,
                geometry.scrollAt(mouseY - topPos, scrollbarDragOffset)
        );
    }

    private void setScrollbarScroll(ScrollTarget target, int requested) {
        if (target == ScrollTarget.MATERIALS) {
            materialScroll = ScrollbarLayout.nearestStep(
                    requested,
                    ROW_HEIGHT,
                    materialScrollbarGeometry().maxScroll()
            );
        } else if (target == ScrollTarget.LEFT) {
            leftScroll = ScrollbarLayout.nearestStep(
                    requested,
                    LINE_HEIGHT,
                    leftScrollbarGeometry().maxScroll()
            );
        }
    }

    private ScrollbarLayout.Geometry materialScrollbarGeometry() {
        int height = materialViewportHeight();
        int contentHeight = displayedMaterials().size() * ROW_HEIGHT;
        int maxScroll = Math.max(0, contentHeight - height);
        return ScrollbarLayout.geometry(
                RIGHT_X + RIGHT_W - 3,
                PANEL_Y,
                height,
                height,
                contentHeight,
                materialScroll,
                maxScroll
        );
    }

    private ScrollbarLayout.Geometry leftScrollbarGeometry() {
        int height = leftViewportHeight();
        int contentHeight = resourceLines().size() * LINE_HEIGHT;
        int maxScroll = Math.max(0, contentHeight - height);
        return ScrollbarLayout.geometry(
                LEFT_X + LEFT_W - 3,
                PANEL_Y,
                height,
                height,
                contentHeight,
                leftScroll,
                maxScroll
        );
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateClearButton();
        updateResourceNavigators();
    }

    private void updateClearButton() {
        if (clearButton == null || minecraft == null || minecraft.player == null) {
            return;
        }
        boolean hasResources = !ProgramResources.get(
                minecraft.player.getItemInHand(menu.hand())
        ).isEmpty();
        if (!hasResources) {
            clearResourcesArmed = false;
        }
        clearButton.active = hasResources;
        clearButton.setMessage(Component.translatable(clearResourcesArmed
                ? "screen.mathmod.talisman_resources.clear_resources_confirm"
                : "screen.mathmod.talisman_resources.clear_resources"));
        String tooltipKey;
        if (!hasResources) {
            tooltipKey = "screen.mathmod.talisman_resources.clear_resources_empty_hint";
        } else if (clearResourcesArmed) {
            tooltipKey = "screen.mathmod.talisman_resources.clear_resources_confirm_hint";
        } else {
            tooltipKey = "screen.mathmod.talisman_resources.clear_resources_hint";
        }
        clearButton.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
    }

    private enum ScrollTarget {
        NONE,
        LEFT,
        MATERIALS
    }

    private void updateResourceNavigators() {
        if (minecraft == null || minecraft.player == null) {
            return;
        }
        int addedCount = currentSelections().size();
        addedMaterialCursor.resize(addedCount);
        List<RuneMaterialDefinition> displayedMaterials = displayedMaterials();
        materialCursor.resize(displayedMaterials.size());
        if (addedMaterialsNavigator != null) {
            addedMaterialsNavigator.active = addedCount > 0;
        }
        if (materialCatalogNavigator != null) {
            materialCatalogNavigator.active = !displayedMaterials.isEmpty();
        }
        if (addedCount == 0
                && addedMaterialsNavigator != null
                && addedMaterialsNavigator.isFocused()
                && materialCatalogNavigator != null
                && materialCatalogNavigator.active) {
            focusNavigator(materialCatalogNavigator);
        }
    }

    private List<ResourceSelection> currentSelections() {
        if (minecraft == null || minecraft.player == null) {
            return List.of();
        }
        return ProgramResources.get(minecraft.player.getItemInHand(menu.hand()));
    }

    private void focusNavigator(ResourceListNavigator navigator) {
        if (navigator == null || !navigator.active) {
            return;
        }
        setFocused(navigator);
    }

    private void addMaterial(int index) {
        disarmClearResources();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(
                    menu.containerId,
                    TalismanResourcesMenu.ADD_RESOURCE_BUTTON_BASE + index
            );
        }
    }

    private void addDisplayedMaterial(int displayedIndex) {
        List<RuneMaterialDefinition> displayed = displayedMaterials();
        if (displayedIndex < 0 || displayedIndex >= displayed.size()) {
            return;
        }
        List<RuneMaterialDefinition> canonical = ProgramResources.materials();
        int canonicalIndex = MaterialCatalogOrder.canonicalIndex(
                canonical,
                displayed.get(displayedIndex)
        );
        if (canonicalIndex >= 0) {
            addMaterial(canonicalIndex);
        }
    }

    private void removeAddedMaterial(int index) {
        disarmClearResources();
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(
                    menu.containerId,
                    TalismanResourcesMenu.REMOVE_RESOURCE_BUTTON_BASE + index
            );
        }
    }

    private void moveResourceCursor(boolean materialCatalog, int distance) {
        PaletteCursor cursor = materialCatalog ? materialCursor : addedMaterialCursor;
        cursor.resize(materialCatalog ? displayedMaterials().size() : currentSelections().size());
        cursor.move(distance);
        ensureResourceCursorVisible(materialCatalog);
    }

    private void moveResourceCursorToEdge(boolean materialCatalog, boolean last) {
        PaletteCursor cursor = materialCatalog ? materialCursor : addedMaterialCursor;
        cursor.resize(materialCatalog ? displayedMaterials().size() : currentSelections().size());
        if (last) {
            cursor.last();
        } else {
            cursor.first();
        }
        ensureResourceCursorVisible(materialCatalog);
    }

    private void ensureResourceCursorVisible(boolean materialCatalog) {
        if (materialCatalog) {
            int height = materialViewportHeight();
            int contentHeight = displayedMaterials().size() * ROW_HEIGHT;
            int maxScroll = Math.max(0, contentHeight - height);
            materialScroll = PaletteCursor.revealRow(
                    materialScroll,
                    materialCursor.index() * ROW_HEIGHT,
                    ROW_HEIGHT,
                    height,
                    maxScroll
            );
            return;
        }

        List<Line> lines = resourceLines();
        Optional<SelectableLineLayout.SelectionSpan> selectedSpan = SelectableLineLayout.span(
                lines.stream().map(Line::selectionIndex).toList(),
                addedMaterialCursor.index()
        );
        if (selectedSpan.isEmpty()) {
            return;
        }
        SelectableLineLayout.SelectionSpan span = selectedSpan.orElseThrow();
        int height = leftViewportHeight();
        int maxScroll = Math.max(0, lines.size() * LINE_HEIGHT - height);
        leftScroll = PaletteCursor.revealRow(
                leftScroll,
                span.firstLine() * LINE_HEIGHT,
                span.lineCount() * LINE_HEIGHT,
                height,
                maxScroll
        );
    }

    private void activateResourceCursor(boolean materialCatalog) {
        ResourceListNavigator navigator = materialCatalog
                ? materialCatalogNavigator
                : addedMaterialsNavigator;
        if (navigator == null || !navigator.active) {
            return;
        }
        if (minecraft != null) {
            navigator.playDownSound(minecraft.getSoundManager());
        }
        if (materialCatalog) {
            addDisplayedMaterial(materialCursor.index());
        } else {
            removeAddedMaterial(addedMaterialCursor.index());
        }
    }

    private Component resourceCursorTitle(boolean materialCatalog) {
        if (materialCatalog) {
            List<RuneMaterialDefinition> materials = displayedMaterials();
            if (materials.isEmpty()) {
                return Component.translatable("screen.mathmod.talisman_resources.none");
            }
            int index = Math.min(materialCursor.index(), materials.size() - 1);
            return materialDisplayName(materials.get(index));
        }
        List<ResourceSelection> selections = currentSelections();
        if (selections.isEmpty()) {
            return Component.translatable("screen.mathmod.talisman_resources.none");
        }
        int index = Math.min(addedMaterialCursor.index(), selections.size() - 1);
        ResourceSelection selection = selections.get(index);
        return Component.literal(selection.quantity() + "x ")
                .append(materialDisplayName(selection.materialId()));
    }

    public Optional<String> selectedCatalogMaterialId() {
        List<RuneMaterialDefinition> materials = displayedMaterials();
        if (materials.isEmpty()) {
            return Optional.empty();
        }
        int index = Math.min(materialCursor.index(), materials.size() - 1);
        return Optional.of(materials.get(index).id());
    }

    private int resourceCursorCount(boolean materialCatalog) {
        return materialCatalog ? displayedMaterials().size() : currentSelections().size();
    }

    private void renderHoverDetails(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component proofName = displayedProofName();
        if (isOverClippedLoadoutName(mouseX, mouseY, proofName)) {
            if (!proofName.getString().isEmpty()) {
                MathTooltipRenderer.render(
                        guiGraphics,
                        font,
                        List.of(Component.translatable(
                                "item.mathmod.programmed_talisman.tooltip.name",
                                proofName
                        )),
                        mouseX,
                        mouseY
                );
            }
            return;
        }

        if (isOverMaterials(mouseX, mouseY)) {
            List<RuneMaterialDefinition> materials = displayedMaterials();
            int index = materialIndexAt(mouseY);
            if (index >= 0 && index < materials.size()) {
                MathTooltipRenderer.render(
                        guiGraphics,
                        font,
                        materialTooltip(materials.get(index), false),
                        mouseX,
                        mouseY
                );
            }
            return;
        }

        if (!isOverLeft(mouseX, mouseY) || minecraft == null || minecraft.player == null) {
            return;
        }
        int selectedIndex = selectedIndexAt(mouseY);
        if (selectedIndex < 0) {
            return;
        }
        ItemStack stack = minecraft.player.getItemInHand(menu.hand());
        List<ResourceSelection> selections = ProgramResources.get(stack);
        if (selectedIndex >= selections.size()) {
            return;
        }
        ProgramResources.material(selections.get(selectedIndex).materialId())
                .ifPresent(material -> MathTooltipRenderer.render(
                        guiGraphics,
                        font,
                        materialTooltip(material, true),
                        mouseX,
                        mouseY
                ));
    }

    private boolean isOverClippedLoadoutName(double mouseX, double mouseY, Component proofName) {
        if (proofName.getString().isEmpty()
                || mouseY < topPos + PANEL_TITLE_Y - 1
                || mouseY >= topPos + PANEL_TITLE_Y + font.lineHeight + 1) {
            return false;
        }
        Component label = Component.translatable("screen.mathmod.talisman_resources.loadout");
        LoadoutHeadingLayout heading = loadoutHeadingLayout(label, ": ", proofName);
        return heading.isOverClippedName(mouseX - leftPos);
    }

    private List<Component> materialTooltip(RuneMaterialDefinition material, boolean selected) {
        List<Component> tooltip = new ArrayList<>();
        Component displayName = materialDisplayName(material);
        tooltip.add(MathGuiTheme.tooltip(displayName.copy(), MathGuiTheme.GOLD));
        if (!displayName.getString().equals(material.id())) {
            tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                    "screen.mathmod.talisman_resources.tooltip.material_id",
                    material.id()
            )));
        }
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.talisman_resources.tooltip.selector",
                material.itemOrTag()
        )));
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                "screen.mathmod.talisman_resources.tooltip.tier",
                material.tier() >= 1 && material.tier() <= 4
                        ? Component.translatable(RuneTier.byLevel(material.tier()).translationKey())
                        : Component.literal(Integer.toString(material.tier()))
        )));
        tooltip.add(MathGuiTheme.tooltip(Component.translatable(
                "screen.mathmod.talisman_resources.tooltip.budget",
                material.budgetBonus()
        ), MathGuiTheme.TEAL));
        tooltip.add(MathGuiTheme.tooltip(Component.translatable(
                        "screen.mathmod.talisman_resources.tooltip.mode",
                        Component.translatable(material.consumed()
                                ? "screen.mathmod.talisman_resources.tooltip.consumed"
                                : "screen.mathmod.talisman_resources.tooltip.catalyst"))
                , material.consumed() ? MathGuiTheme.CORAL : MathGuiTheme.GREEN));
        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(material.consumed()
                        ? "screen.mathmod.talisman_resources.tooltip.lore_consumed"
                        : "screen.mathmod.talisman_resources.tooltip.lore_catalyst")));

        if (material.attributes().isEmpty()) {
            tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(
                    "screen.mathmod.talisman_resources.tooltip.no_attributes"
            )));
        } else {
            tooltip.add(MathGuiTheme.tooltip(
                    Component.translatable("screen.mathmod.talisman_resources.tooltip.attributes"),
                    MathGuiTheme.BLUE
            ));
            material.attributes().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> tooltip.add(MathGuiTheme.tooltipSecondary(
                            ProgramMessageComponents.attribute(entry.getKey())
                                    .copy()
                                    .append(": " + entry.getValue())
                    )));
        }

        tooltip.add(MathGuiTheme.tooltipSecondary(Component.translatable(selected
                        ? "screen.mathmod.talisman_resources.tooltip.remove"
                        : "screen.mathmod.talisman_resources.tooltip.add")));
        return tooltip;
    }

    private void drawClipped(GuiGraphics guiGraphics, String text, int x, int y, int width, int color) {
        if (text.isEmpty() || width <= 0) {
            return;
        }
        String clipped = text;
        if (font.width(clipped) > width) {
            String ellipsis = font.plainSubstrByWidth("...", width);
            int prefixWidth = Math.max(0, width - font.width(ellipsis));
            clipped = font.plainSubstrByWidth(text, prefixWidth) + ellipsis;
        }
        guiGraphics.drawString(font, clipped, x, y, color, false);
    }

    private final class ResourceListNavigator extends AbstractWidget {
        private final boolean materialCatalog;

        private ResourceListNavigator(
                int x,
                int y,
                int width,
                int height,
                boolean materialCatalog
        ) {
            super(
                    x,
                    y,
                    width,
                    height,
                    Component.translatable(materialCatalog
                            ? "screen.mathmod.talisman_resources.materials"
                            : "screen.mathmod.talisman_resources.selected")
            );
            this.materialCatalog = materialCatalog;
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
            if (keyCode == GLFW.GLFW_KEY_UP) {
                moveResourceCursor(materialCatalog, -1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                moveResourceCursor(materialCatalog, 1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_HOME) {
                moveResourceCursorToEdge(materialCatalog, false);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_END) {
                moveResourceCursorToEdge(materialCatalog, true);
                return true;
            }
            if (CommonInputs.selected(keyCode)) {
                activateResourceCursor(materialCatalog);
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            int count = resourceCursorCount(materialCatalog);
            PaletteCursor cursor = materialCatalog ? materialCursor : addedMaterialCursor;
            output.add(
                    NarratedElementType.TITLE,
                    Component.translatable(
                            materialCatalog
                                    ? "screen.mathmod.talisman_resources.keyboard_material"
                                    : "screen.mathmod.talisman_resources.keyboard_added",
                            resourceCursorTitle(materialCatalog)
                    )
            );
            output.add(
                    NarratedElementType.POSITION,
                    Component.translatable(
                            "screen.mathmod.talisman_resources.keyboard_position",
                            count == 0 ? 0 : cursor.index() + 1,
                            count
                    )
            );
            output.add(
                    NarratedElementType.USAGE,
                    Component.translatable(materialCatalog
                            ? "screen.mathmod.talisman_resources.keyboard_material_usage"
                            : "screen.mathmod.talisman_resources.keyboard_added_usage")
            );
        }
    }

    private record Line(
            FormattedCharSequence text,
            int color,
            int selectionIndex,
            int requiredVisibleRows
    ) {
        private Line(FormattedCharSequence text, int color) {
            this(text, color, -1, 1);
        }

        private Line(FormattedCharSequence text, int color, int selectionIndex) {
            this(text, color, selectionIndex, 1);
        }

        private Line(String text, int color) {
            this(FormattedCharSequence.forward(text, net.minecraft.network.chat.Style.EMPTY), color, -1, 1);
        }
    }
}
