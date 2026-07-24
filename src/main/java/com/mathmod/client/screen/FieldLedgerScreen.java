package com.mathmod.client.screen;

import com.mathmod.knowledge.FieldLedgerView;
import com.mathmod.knowledge.KnowledgeGrant;
import com.mathmod.knowledge.KnowledgeKind;
import com.mathmod.program.ProgramAttributes;
import com.mathmod.screen.FieldLedgerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public final class FieldLedgerScreen extends AbstractContainerScreen<FieldLedgerMenu> {
    private static final int PANEL_X = 12;
    private static final int PANEL_Y = 55;
    private static final int PANEL_BOTTOM = 12;
    private static final int ROW_HEIGHT = 38;
    private static final int LINE_HEIGHT = 11;

    private LedgerTab tab = LedgerTab.OVERVIEW;
    private int scroll;
    private FieldLedgerView.Entry hoveredEntry;

    public FieldLedgerScreen(
            FieldLedgerMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);
        imageWidth = 380;
        imageHeight = 248;
        inventoryLabelY = imageHeight + 10;
    }

    @Override
    protected void init() {
        imageWidth = Math.min(380, Math.max(300, width - 24));
        imageHeight = Math.min(248, Math.max(220, height - 36));
        super.init();
        int closeWidth = 28;
        int tabWidth = Math.max(74, (imageWidth - 24 - closeWidth - 18) / 3);
        int x = leftPos + 12;
        addRenderableWidget(tabButton(x, LedgerTab.OVERVIEW, tabWidth));
        x += tabWidth + 6;
        addRenderableWidget(tabButton(x, LedgerTab.EPIPHANIES, tabWidth));
        x += tabWidth + 6;
        addRenderableWidget(tabButton(x, LedgerTab.DISCOVERIES, tabWidth));
        MathButton close = MathButton.iconAction(
                leftPos + imageWidth - closeWidth - 12,
                topPos + 24,
                closeWidth,
                Component.translatable("screen.mathmod.field_ledger.close"),
                Component.literal("X"),
                button -> onClose(),
                MathButton.Tone.NEUTRAL
        );
        close.setTooltip(Tooltip.create(
                Component.translatable("screen.mathmod.field_ledger.close")
        ));
        addRenderableWidget(close);
    }

    private Button tabButton(int x, LedgerTab target, int width) {
        return MathButton.tab(
                x,
                topPos + 24,
                width,
                Component.translatable(target.translationKey),
                button -> {
                    tab = target;
                    scroll = 0;
                },
                () -> tab == target
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredEntry = null;
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (hoveredEntry != null) {
            MathTooltipRenderer.render(
                    graphics,
                    font,
                    tooltip(hoveredEntry),
                    mouseX,
                    mouseY
            );
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        MathGuiTheme.fillChamfered(
                graphics,
                leftPos,
                topPos,
                imageWidth,
                imageHeight,
                MathGuiTheme.INK
        );
        MathGuiTheme.outlineChamfered(
                graphics,
                leftPos,
                topPos,
                imageWidth,
                imageHeight,
                MathGuiTheme.GOLD
        );
        MathGuiTheme.panel(
                graphics,
                leftPos + PANEL_X,
                topPos + PANEL_Y,
                imageWidth - PANEL_X * 2,
                imageHeight - PANEL_Y - PANEL_BOTTOM
        );
        MathGuiTheme.drawProofGrid(
                graphics,
                leftPos + PANEL_X + 1,
                topPos + PANEL_Y + 1,
                imageWidth - PANEL_X * 2 - 2,
                imageHeight - PANEL_Y - PANEL_BOTTOM - 2
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, MathGuiTheme.IVORY, false);
        if (tab == LedgerTab.OVERVIEW) {
            renderOverview(graphics);
        } else {
            renderEntries(graphics, mouseX, mouseY);
        }
    }

    private void renderOverview(GuiGraphics graphics) {
        FieldLedgerView view = menu.view();
        int x = PANEL_X + 10;
        int y = PANEL_Y + 10;
        graphics.drawString(
                font,
                Component.translatable(
                        "screen.mathmod.field_ledger.summary",
                        view.completedCount(),
                        view.totalCount()
                ),
                x,
                y,
                MathGuiTheme.GREEN,
                false
        );
        y += LINE_HEIGHT * 2;
        drawWrapped(
                graphics,
                Component.translatable("screen.mathmod.field_ledger.overview"),
                x,
                y,
                MathGuiTheme.MUTED
        );
        y += LINE_HEIGHT * 4;
        graphics.drawString(
                font,
                Component.translatable(
                        "screen.mathmod.field_ledger.epiphany_count",
                        completed(menu.view().epiphanies()),
                        menu.view().epiphanies().size()
                ),
                x,
                y,
                MathGuiTheme.TEAL,
                false
        );
        y += LINE_HEIGHT + 4;
        graphics.drawString(
                font,
                Component.translatable(
                        "screen.mathmod.field_ledger.discovery_count",
                        completed(menu.view().discoveries()),
                        menu.view().discoveries().size()
                ),
                x,
                y,
                MathGuiTheme.GOLD,
                false
        );
    }

    private void renderEntries(GuiGraphics graphics, int mouseX, int mouseY) {
        List<FieldLedgerView.Entry> entries = visibleEntries();
        int viewportHeight = imageHeight - PANEL_Y - PANEL_BOTTOM - 12;
        int maximumScroll = Math.max(0, entries.size() * ROW_HEIGHT - viewportHeight);
        scroll = Math.min(scroll, maximumScroll);
        int panelLeft = PANEL_X + 6;
        int panelRight = imageWidth - PANEL_X - 6;
        int top = PANEL_Y + 6;
        int bottom = imageHeight - PANEL_BOTTOM - 6;

        graphics.enableScissor(
                leftPos + panelLeft,
                topPos + top,
                leftPos + panelRight,
                topPos + bottom
        );
        for (int index = 0; index < entries.size(); index++) {
            FieldLedgerView.Entry entry = entries.get(index);
            int y = top + index * ROW_HEIGHT - scroll;
            if (y + ROW_HEIGHT <= top || y >= bottom) {
                continue;
            }
            boolean hovered = mouseX >= leftPos + panelLeft
                    && mouseX < leftPos + panelRight
                    && mouseY >= topPos + y
                    && mouseY < topPos + y + ROW_HEIGHT - 2;
            int background = hovered ? MathGuiTheme.SURFACE_SELECTED_SOFT : MathGuiTheme.SURFACE_ROW;
            MathGuiTheme.fillChamfered(
                    graphics,
                    panelLeft,
                    y,
                    panelRight - panelLeft,
                    ROW_HEIGHT - 3,
                    background
            );
            int accent = entry.complete() ? MathGuiTheme.GREEN : MathGuiTheme.GOLD;
            graphics.fill(panelLeft + 3, y + 4, panelLeft + 5, y + ROW_HEIGHT - 7, accent);
            graphics.drawString(
                    font,
                    Component.translatable(entry.titleTranslationKey()),
                    panelLeft + 10,
                    y + 5,
                    entry.complete() ? MathGuiTheme.IVORY : MathGuiTheme.MUTED,
                    false
            );
            graphics.drawString(
                    font,
                    status(entry),
                    panelLeft + 10,
                    y + 18,
                    entry.complete() ? MathGuiTheme.GREEN : MathGuiTheme.GOLD,
                    false
            );
            if (hovered) {
                hoveredEntry = entry;
            }
        }
        graphics.disableScissor();
    }

    private Component status(FieldLedgerView.Entry entry) {
        if (entry.complete()) {
            return Component.translatable("screen.mathmod.field_ledger.complete");
        }
        if (!entry.studies().isEmpty()) {
            int progress = entry.studies().stream().mapToInt(FieldLedgerView.Study::progress).sum();
            int required = entry.studies().stream().mapToInt(FieldLedgerView.Study::required).sum();
            return Component.translatable(
                    "screen.mathmod.field_ledger.progress",
                    progress,
                    required
            );
        }
        return Component.translatable("screen.mathmod.field_ledger.undiscovered");
    }

    private List<Component> tooltip(FieldLedgerView.Entry entry) {
        List<Component> lines = new ArrayList<>();
        lines.add(MathGuiTheme.tooltip(
                Component.translatable(entry.titleTranslationKey()),
                entry.complete() ? MathGuiTheme.GREEN : MathGuiTheme.GOLD
        ));
        lines.add(MathGuiTheme.tooltipSecondary(Component.literal(entry.id().toString())));
        if (!entry.complete()) {
            lines.add(MathGuiTheme.tooltipSecondary(
                    Component.translatable(entry.routeTranslationKey())
            ));
        }
        entry.studies().forEach(study -> lines.add(MathGuiTheme.tooltipSecondary(
                Component.translatable(
                        "screen.mathmod.field_ledger.study",
                        MaterialPresentation.displayName(study.materialId().path()),
                        study.progress(),
                        study.required()
                )
        )));
        if (!entry.grants().isEmpty()) {
            lines.add(MathGuiTheme.tooltip(
                    Component.translatable("screen.mathmod.field_ledger.grants"),
                    MathGuiTheme.TEAL
            ));
            entry.grants().forEach(grant -> lines.add(MathGuiTheme.tooltipSecondary(
                    grantName(grant)
            )));
        }
        return lines;
    }

    private net.minecraft.network.chat.MutableComponent grantName(KnowledgeGrant grant) {
        return Component.translatable(
                "screen.mathmod.field_ledger.grant",
                Component.translatable(grant.kind().translationKey()),
                ProgramAttributes.fallbackLabel(grant.id().path())
        );
    }

    private void drawWrapped(
            GuiGraphics graphics,
            Component component,
            int x,
            int y,
            int color
    ) {
        int width = imageWidth - PANEL_X * 2 - 20;
        int line = 0;
        for (var sequence : font.split(component, width)) {
            graphics.drawString(font, sequence, x, y + line * LINE_HEIGHT, color, false);
            line++;
        }
    }

    private List<FieldLedgerView.Entry> visibleEntries() {
        return tab == LedgerTab.EPIPHANIES
                ? menu.view().epiphanies()
                : menu.view().discoveries();
    }

    private static long completed(List<FieldLedgerView.Entry> entries) {
        return entries.stream().filter(FieldLedgerView.Entry::complete).count();
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollX,
            double scrollY
    ) {
        if (tab != LedgerTab.OVERVIEW) {
            int viewportHeight = imageHeight - PANEL_Y - PANEL_BOTTOM - 12;
            int maximumScroll = Math.max(0, visibleEntries().size() * ROW_HEIGHT - viewportHeight);
            scroll = Math.max(
                    0,
                    Math.min(maximumScroll, scroll - (int) Math.signum(scrollY) * ROW_HEIGHT)
            );
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private enum LedgerTab {
        OVERVIEW("screen.mathmod.field_ledger.tab.overview"),
        EPIPHANIES("screen.mathmod.field_ledger.tab.epiphanies"),
        DISCOVERIES("screen.mathmod.field_ledger.tab.discoveries");

        private final String translationKey;

        LedgerTab(String translationKey) {
            this.translationKey = translationKey;
        }
    }
}
