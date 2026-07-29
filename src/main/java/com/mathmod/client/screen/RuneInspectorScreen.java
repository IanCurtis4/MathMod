package com.mathmod.client.screen;

import com.mathmod.program.ProgramSurface;
import com.mathmod.program.ScopedFunctionalProjection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** First read-only DAG canvas. It owns no server state and sends no packets. */
public final class RuneInspectorScreen extends Screen {
    private static final int NODE_WIDTH = 104;
    private static final int NODE_HEIGHT = 48;
    private static final int LAYER_GAP = 128;
    private static final int ROW_GAP = 48;

    private final Screen parent;
    private final ProgramInspectorPresentation.Model model;
    private final ScopedFunctionalProjection functionalProjection;
    private String selectedNodeId;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private Viewport viewport = Viewport.initial();
    private boolean panning;
    private double lastPanX;
    private double lastPanY;
    private FunctionalPanel functionalPanel = FunctionalPanel.AUTHORED;
    private int functionalRow;
    private int functionalScroll;
    private MathButton closeButton;
    private MathButton authoredButton;
    private MathButton checkedButton;
    private MathButton graphButton;

    public RuneInspectorScreen(Screen parent, ProgramSurface surface) {
        this(parent,surface,ScopedFunctionalProjection.graphOnly());
    }
    public RuneInspectorScreen(Screen parent, ProgramSurface surface, ScopedFunctionalProjection functionalProjection) {
        super(Component.translatable("screen.mathmod.rune_inspector.title"));
        this.parent = parent;
        this.model = ProgramInspectorPresentation.build(surface);
        this.functionalProjection = functionalProjection;
        this.selectedNodeId = model.nodes().isEmpty() ? "" : model.orderedNodes().getFirst().id();
    }

    @Override
    protected void init() {
        panelWidth = Math.min(760, width - 20);
        panelHeight = Math.min(430, height - 20);
        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;
        int closeWidth = Math.max(64, font.width(Component.translatable("screen.mathmod.rune_inspector.close")) + 20);
        closeButton = addRenderableWidget(MathButton.action(
                panelX + panelWidth - closeWidth - 10,
                panelY + 10,
                closeWidth,
                Component.translatable("screen.mathmod.rune_inspector.close"),
                button -> onClose(),
                MathButton.Tone.NEUTRAL
        ));
        int detailsX = panelX + functionalCanvasWidth() + 8;
        int detailsWidth = panelX + panelWidth - 10 - detailsX;
        FunctionalLayout layout = functionalLayout(detailsX, detailsWidth, panelY + 64);
        authoredButton = addRenderableWidget(functionalPanelButton(layout.authored().selector(), FunctionalPanel.AUTHORED));
        checkedButton = addRenderableWidget(functionalPanelButton(layout.checked().selector(), FunctionalPanel.CHECKED));
        graphButton = addRenderableWidget(functionalPanelButton(layout.graph().selector(), FunctionalPanel.GRAPH));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        MathGuiTheme.fillChamfered(graphics, panelX, panelY, panelWidth, panelHeight, MathGuiTheme.INK);
        MathGuiTheme.outlineChamfered(graphics, panelX, panelY, panelWidth, panelHeight, MathGuiTheme.GOLD);
        graphics.drawString(font, title, panelX + 12, panelY + 15, MathGuiTheme.IVORY, false);
        graphics.drawString(font, Component.translatable("screen.mathmod.rune_inspector.read_only"), panelX + 12, panelY + 29, MathGuiTheme.MUTED, false);
        graphics.drawString(font, Component.translatable("screen.mathmod.rune_inspector.functional_snapshot", sourceLabel(functionalProjection.sourceState()), attemptLabel(functionalProjection.attemptState())), panelX + 12, panelY + 41, MathGuiTheme.MUTED, false);

        int canvasX = panelX + 10;
        int canvasY = panelY + 64;
        int canvasWidth = functionalCanvasWidth();
        int canvasHeight = panelHeight - 62;
        int detailsX = canvasX + canvasWidth + 8;
        int detailsWidth = panelX + panelWidth - 10 - detailsX;
        MathGuiTheme.panel(graphics, canvasX, canvasY, canvasWidth, canvasHeight);
        MathGuiTheme.drawProofGrid(graphics, canvasX + 1, canvasY + 1, canvasWidth - 2, canvasHeight - 2);
        MathGuiTheme.panel(graphics, detailsX, canvasY, detailsWidth, canvasHeight);

        Rect content = contentRect();
        graphics.enableScissor(content.x(), content.y(), content.x() + content.width(), content.y() + content.height());
        renderEdges(graphics, content);
        renderNodes(graphics, content, mouseX, mouseY);
        graphics.disableScissor();
        renderDetails(graphics, detailsX, canvasY, detailsWidth, canvasHeight);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            FunctionalPanel panel = functionalPanelAt(mouseX, mouseY);
            if (panel != null) {
                focusFunctionalPanel(panel);
                return true;
            }
            ProgramInspectorPresentation.Node hit = nodeAt(mouseX, mouseY);
            if (hit != null) {
                selectedNodeId = hit.id();
                return true;
            }
        }
        if (button == 2 && contentRect().contains(mouseX, mouseY)) {
            panning = true;
            lastPanX = mouseX;
            lastPanY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (panning && button == 2) {
            viewport = viewport.pan(mouseX - lastPanX, mouseY - lastPanY,
                    canvasWidth(), canvasHeight(), contentWidth(), contentHeight());
            lastPanX = mouseX;
            lastPanY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (panning && button == 2) {
            panning = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            onClose();
            return true;
        }
        if (keyCode == 258) {
            boolean backwards = (modifiers & 1) != 0;
            cycleFunctionalFocus(backwards);
            return true;
        }
        if (keyCode == 262 || keyCode == 264) {
            if (functionalPanel != FunctionalPanel.GRAPH) { moveFunctionalRow(1); return true; }
            moveSelection(1);
            return true;
        }
        if (keyCode == 263 || keyCode == 265) {
            if (functionalPanel != FunctionalPanel.GRAPH) { moveFunctionalRow(-1); return true; }
            moveSelection(-1);
            return true;
        }
        if (keyCode == 334 || keyCode == 61) {
            viewport = viewport.zoomBy(1.15D, canvasWidth(), canvasHeight(), contentWidth(), contentHeight());
            return true;
        }
        if (keyCode == 333 || keyCode == 45) {
            viewport = viewport.zoomBy(1 / 1.15D, canvasWidth(), canvasHeight(), contentWidth(), contentHeight());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private void renderEdges(GuiGraphics graphics, Rect content) {
        for (ProgramInspectorPresentation.Edge edge : model.edges()) {
            ProgramInspectorPresentation.Node from = model.node(edge.fromId());
            ProgramInspectorPresentation.Node to = model.node(edge.toId());
            if (from == null || to == null) {
                continue;
            }
            Rect fromRect = nodeRect(from, content.x(), content.y());
            Rect toRect = nodeRect(to, content.x(), content.y());
            int fromX = fromRect.x() + fromRect.width();
            int fromY = fromRect.y() + fromRect.height() / 2;
            int toX = toRect.x();
            int toY = inputSocketY(to, edge.inputName(), toRect);
            graphics.hLine(fromX, toX, fromY, MathGuiTheme.BORDER_STRONG);
            graphics.vLine(toX, Math.min(fromY, toY), Math.max(fromY, toY), MathGuiTheme.BORDER_STRONG);
            int labelWidth = viewport.screenLength(font.width(edge.inputName()));
            Rect label = boundedLabelRect(
                    content, toX + viewport.screenLength(4), toY - viewport.screenLength(8),
                    labelWidth, viewport.screenLength(9));
            drawScaledText(graphics, edge.inputName(), label.x(), label.y(), MathGuiTheme.MUTED);
        }
    }

    private void renderNodes(GuiGraphics graphics, Rect content, int mouseX, int mouseY) {
        for (ProgramInspectorPresentation.Node node : model.nodes()) {
            Rect rect = nodeRect(node, content.x(), content.y());
            int x = rect.x();
            int y = rect.y();
            boolean selected = node.id().equals(selectedNodeId);
            int accent = purityColor(node.purity());
            MathGuiTheme.fillChamfered(graphics, x, y, rect.width(), rect.height(), selected ? MathGuiTheme.SURFACE_SELECTED : MathGuiTheme.SURFACE_RAISED);
            MathGuiTheme.outlineChamfered(graphics, x, y, rect.width(), rect.height(), selected ? accent : MathGuiTheme.BORDER_STRONG);
            graphics.fill(x + viewport.screenLength(4), y + viewport.screenLength(5), x + viewport.screenLength(6), y + rect.height() - viewport.screenLength(5), accent);
            drawNodeText(graphics, node, rect);
            for (String inputName : node.inputNames()) {
                int socketY = inputSocketY(node, inputName, rect);
                int radius = viewport.screenLength(2);
                graphics.fill(x, socketY - radius, x + radius * 2, socketY + radius, MathGuiTheme.TEAL);
            }
            int outputRadius = viewport.screenLength(2);
            graphics.fill(x + rect.width() - outputRadius, y + rect.height() / 2 - outputRadius,
                    x + rect.width() + outputRadius, y + rect.height() / 2 + outputRadius, MathGuiTheme.GOLD);
            if (rect.contains(mouseX, mouseY)) {
                MathTooltipRenderer.render(graphics, font, tooltip(node), mouseX, mouseY);
            }
        }
    }

    private void renderDetails(GuiGraphics graphics, int x, int y, int width, int height) {
        int projectionBottom = renderFunctionalProjection(graphics, x, y, width, height);
        ProgramInspectorPresentation.Node node = model.node(selectedNodeId);
        if (node == null) {
            graphics.drawString(font, Component.translatable("screen.mathmod.rune_inspector.empty"), x + 8, projectionBottom + 4, MathGuiTheme.MUTED, false);
            return;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(node.id()));
        lines.add(Component.translatable("screen.mathmod.rune_inspector.rune", node.runeId()));
        lines.add(Component.translatable("screen.mathmod.rune_inspector.purity", node.purity().name()));
        lines.add(Component.translatable("screen.mathmod.rune_inspector.formula", node.formula()));
        lines.add(Component.translatable("screen.mathmod.rune_inspector.normalized", node.normalizedValue()));
        lines.add(Component.translatable("screen.mathmod.rune_inspector.budget", node.budgetCost()));
        if (!node.dynamicDependencies().isEmpty()) {
            lines.add(Component.translatable("screen.mathmod.rune_inspector.dependencies", String.join(", ", node.dynamicDependencies())));
        }
        if (!node.materials().isEmpty()) {
            lines.add(Component.translatable("screen.mathmod.rune_inspector.materials", String.join(", ", node.materials())));
        }
        if (!node.attributes().isEmpty()) {
            lines.add(Component.translatable("screen.mathmod.rune_inspector.attributes", String.join(", ", node.attributes())));
        }
        int row = projectionBottom + 5;
        for (Component line : lines) {
            for (var wrapped : font.split(line, Math.max(50, width - 16))) {
                if (row + 10 > y + height - 8) {
                    return;
                }
                graphics.drawString(font, wrapped, x + 8, row, row == y + 9 ? MathGuiTheme.IVORY : MathGuiTheme.MUTED, false);
                row += 11;
            }
            row += 2;
        }
    }

    private int renderFunctionalProjection(GuiGraphics graphics, int x, int y, int width, int height) {
        FunctionalLayout layout = functionalLayout(x, width, y);
        if (isCompactFunctionalLayout(width)) {
            return switch (functionalPanel) {
                case AUTHORED -> drawProjectionSection(graphics, layout.authored(), Component.translatable("screen.mathmod.rune_inspector.authored_source"), functionalProjection.authoredRows(), FunctionalPanel.AUTHORED);
                case CHECKED -> drawProjectionSection(graphics, layout.checked(), Component.translatable("screen.mathmod.rune_inspector.checked_binding"), functionalProjection.checkedRows(), FunctionalPanel.CHECKED);
                case GRAPH -> drawGraphProjection(graphics, layout.graph(), x, width, y, height);
            };
        }
        int row = drawProjectionSection(graphics, layout.authored(), Component.translatable("screen.mathmod.rune_inspector.authored_source"), functionalProjection.authoredRows(), FunctionalPanel.AUTHORED);
        row = drawProjectionSection(graphics, layout.checked(), Component.translatable("screen.mathmod.rune_inspector.checked_binding"), functionalProjection.checkedRows(), FunctionalPanel.CHECKED);
        return drawGraphProjection(graphics, layout.graph(), x, width, y, height);
    }

    private int drawGraphProjection(GuiGraphics graphics, Section graph, int x, int width, int y, int height) {
        int row = drawProjectionHeading(graphics, Component.translatable("screen.mathmod.rune_inspector.graph_authority"), graph.headingY(), x, width, FunctionalPanel.GRAPH);
        for (var line : font.split(Component.translatable("screen.mathmod.rune_inspector.functional_graph", graphStateLabel(functionalProjection.graphState()), relationLabel(functionalProjection.graphRelation())), Math.max(30, width - 16))) {
            graphics.drawString(font, line, x + 8, row, MathGuiTheme.MUTED, false);
            row += 11;
        }
        for (ScopedFunctionalProjection.Diagnostic diagnostic : functionalProjection.diagnostics()) {
            if (row + 10 > y + height / 2) break;
            graphics.drawString(font, Component.translatable("screen.mathmod.rune_inspector.functional_diagnostic", diagnosticLabel(diagnostic.code()), diagnostic.structuralPath()), x + 8, row, MathGuiTheme.CORAL, false);
            row += 11;
        }
        return row + 2;
    }

    private int drawProjectionSection(GuiGraphics graphics, Section section, Component heading, List<ScopedFunctionalProjection.Row> rows, FunctionalPanel panel) {
        int row = drawProjectionHeading(graphics, heading, section.headingY(), section.selector().x() - 4, section.selector().width() + 8, panel);
        return drawProjectionRows(graphics, rows, row, section.selector().x() - 4, section.selector().width() + 8, visibleRowLimit(section.selector().width()), panel);
    }

    private int drawProjectionHeading(GuiGraphics graphics, Component heading, int row, int x, int width, FunctionalPanel panel) {
        for (var line : font.split(heading, Math.max(30, width - 16))) {
            graphics.drawString(font, line, x + 8, row, functionalPanel == panel ? MathGuiTheme.TEAL : MathGuiTheme.IVORY, false);
            row += 11;
        }
        return row;
    }

    private int drawProjectionRows(GuiGraphics graphics, List<ScopedFunctionalProjection.Row> rows, int row, int x, int width, int limit, FunctionalPanel panel) {
        if (rows.isEmpty()) {
            graphics.drawString(font, Component.literal("—"), x + 8, row, MathGuiTheme.MUTED, false);
            return row + 11;
        }
        int first = panel == functionalPanel ? functionalScroll : 0;
        for (int index = first; index < Math.min(first + limit, rows.size()); index++) {
            ScopedFunctionalProjection.Row value = rows.get(index);
            Component text = Component.translatable("screen.mathmod.rune_inspector.functional_row", value.structuralPath(), rowKindLabel(value.kind()), value.primaryToken());
            graphics.drawString(font, font.plainSubstrByWidth(text.getString(), Math.max(30, width - 16)), x + 8, row, panel == functionalPanel && index == functionalRow ? MathGuiTheme.GOLD : MathGuiTheme.MUTED, false);
            row += 11;
        }
        return row;
    }

    private FunctionalPanel functionalPanelAt(double mouseX, double mouseY) {
        int detailsX = panelX + functionalCanvasWidth() + 8;
        FunctionalLayout layout = functionalLayout(detailsX, panelX + panelWidth - 10 - detailsX, panelY + 64);
        if (layout.authored().selector().contains(mouseX, mouseY)) return FunctionalPanel.AUTHORED;
        if (layout.checked().selector().contains(mouseX, mouseY)) return FunctionalPanel.CHECKED;
        if (layout.graph().selector().contains(mouseX, mouseY)) return FunctionalPanel.GRAPH;
        return null;
    }

    private FunctionalLayout functionalLayout(int x, int width, int top) {
        int selectorX = x + 4;
        int selectorWidth = Math.max(40, width - 8);
        if (isCompactFunctionalLayout(width)) {
            int row = top + 6;
            Rect authoredSelector = new Rect(selectorX, row, selectorWidth, 20);
            row += 22;
            Rect checkedSelector = new Rect(selectorX, row, selectorWidth, 20);
            row += 22;
            Rect graphSelector = new Rect(selectorX, row, selectorWidth, 20);
            int contentTop = row + 24;
            return new FunctionalLayout(
                    Section.rows(authoredSelector, contentTop, headingHeight("screen.mathmod.rune_inspector.authored_source", selectorWidth), functionalProjection.authoredRows(), 1),
                    Section.rows(checkedSelector, contentTop, headingHeight("screen.mathmod.rune_inspector.checked_binding", selectorWidth), functionalProjection.checkedRows(), 1),
                    Section.graph(graphSelector, contentTop, headingHeight("screen.mathmod.rune_inspector.graph_authority", selectorWidth), graphValueHeight(selectorWidth))
            );
        }
        int row = top + 6;
        Section authored = Section.rows(new Rect(selectorX, row, selectorWidth, 20), headingHeight("screen.mathmod.rune_inspector.authored_source", selectorWidth), functionalProjection.authoredRows(), visibleRowLimit(selectorWidth));
        row = authored.bottom() + 4;
        Section checked = Section.rows(new Rect(selectorX, row, selectorWidth, 20), headingHeight("screen.mathmod.rune_inspector.checked_binding", selectorWidth), functionalProjection.checkedRows(), visibleRowLimit(selectorWidth));
        row = checked.bottom() + 4;
        return new FunctionalLayout(authored, checked, Section.graph(new Rect(selectorX, row, selectorWidth, 20), headingHeight("screen.mathmod.rune_inspector.graph_authority", selectorWidth)));
    }

    private int headingHeight(String key, int width) { return font.split(Component.translatable(key), Math.max(30, width - 16)).size() * 11; }
    private static int visibleRowLimit(int width) { return width < 250 ? 1 : 3; }
    private static boolean isCompactFunctionalLayout(int width) { return width < 250; }
    private int graphValueHeight(int width) { return font.split(Component.translatable("screen.mathmod.rune_inspector.functional_graph", graphStateLabel(functionalProjection.graphState()), relationLabel(functionalProjection.graphRelation())), Math.max(30, width - 16)).size() * 11; }

    /** Runtime preview oracle: every wrapped semantic line and section stays inside the details panel. */
    private boolean functionalLayoutContained() {
        int detailsX = panelX + functionalCanvasWidth() + 8;
        int detailsWidth = panelX + panelWidth - 10 - detailsX;
        FunctionalLayout layout = functionalLayout(detailsX, detailsWidth, panelY + 64);
        // The production Font splitter is the width authority used by render and by headingHeight.
        return !font.split(Component.translatable("screen.mathmod.rune_inspector.authored_source"), Math.max(30, detailsWidth - 16)).isEmpty()
                && !font.split(Component.translatable("screen.mathmod.rune_inspector.checked_binding"), Math.max(30, detailsWidth - 16)).isEmpty()
                && !font.split(Component.translatable("screen.mathmod.rune_inspector.graph_authority"), Math.max(30, detailsWidth - 16)).isEmpty()
                && layout.authored().bottom() <= panelY + panelHeight - 8
                && layout.checked().bottom() <= panelY + panelHeight - 8
                && layout.graph().bottom() <= panelY + panelHeight - 8;
    }

    private String functionalLayoutDiagnostic() {
        int detailsX = panelX + functionalCanvasWidth() + 8;
        int detailsWidth = panelX + panelWidth - 10 - detailsX;
        FunctionalLayout layout = functionalLayout(detailsX, detailsWidth, panelY + 64);
        return "detailsWidth=" + detailsWidth
                + ", authoredLines=" + font.split(Component.translatable("screen.mathmod.rune_inspector.authored_source"), Math.max(30, detailsWidth - 16)).size()
                + ", checkedLines=" + font.split(Component.translatable("screen.mathmod.rune_inspector.checked_binding"), Math.max(30, detailsWidth - 16)).size()
                + ", graphLines=" + font.split(Component.translatable("screen.mathmod.rune_inspector.graph_authority"), Math.max(30, detailsWidth - 16)).size()
                + ", graphBottom=" + layout.graph().bottom()
                + ", panelBottom=" + (panelY + panelHeight - 8);
    }

    private void selectFunctionalPanel(FunctionalPanel panel) { functionalPanel = panel; functionalRow = 0; functionalScroll = 0; }
    private MathButton functionalPanelButton(Rect bounds, FunctionalPanel panel) {
        return MathButton.action(bounds.x(), bounds.y(), bounds.width(), panelLabel(panel), button -> focusFunctionalPanel(panel), MathButton.Tone.NEUTRAL);
    }
    private void focusFunctionalPanel(FunctionalPanel panel) {
        selectFunctionalPanel(panel);
        setFocused(panelButton(panel));
    }
    private MathButton panelButton(FunctionalPanel panel) {
        return switch (panel) { case AUTHORED -> authoredButton; case CHECKED -> checkedButton; case GRAPH -> graphButton; };
    }
    private void cycleFunctionalFocus(boolean backwards) {
        if (getFocused() == closeButton) { focusFunctionalPanel(backwards ? FunctionalPanel.GRAPH : FunctionalPanel.AUTHORED); return; }
        if (getFocused() == authoredButton) { if (backwards) setFocused(closeButton); else focusFunctionalPanel(FunctionalPanel.CHECKED); return; }
        if (getFocused() == checkedButton) { focusFunctionalPanel(backwards ? FunctionalPanel.AUTHORED : FunctionalPanel.GRAPH); return; }
        if (getFocused() == graphButton) { if (backwards) focusFunctionalPanel(FunctionalPanel.CHECKED); else setFocused(closeButton); return; }
        focusFunctionalPanel(backwards ? FunctionalPanel.GRAPH : FunctionalPanel.AUTHORED);
    }
    private void moveFunctionalRow(int distance) {
        List<ScopedFunctionalProjection.Row> rows = functionalPanel == FunctionalPanel.AUTHORED ? functionalProjection.authoredRows() : functionalProjection.checkedRows();
        if (rows.isEmpty()) return;
        functionalRow = Math.max(0, Math.min(rows.size() - 1, functionalRow + distance));
        functionalScroll = Math.max(0, Math.min(functionalRow, Math.max(0, rows.size() - visibleRowLimit(panelButton(functionalPanel).getWidth()))));
    }

    private ProgramInspectorPresentation.Node nodeAt(double mouseX, double mouseY) {
        if (!contentRect().contains(mouseX, mouseY)) {
            return null;
        }
        Rect content = contentRect();
        return model.nodes().stream().filter(node -> {
            return nodeRect(node, content.x(), content.y()).contains(mouseX, mouseY);
        }).findFirst().orElse(null);
    }

    private void moveSelection(int distance) {
        List<ProgramInspectorPresentation.Node> nodes = model.orderedNodes();
        if (nodes.isEmpty()) {
            return;
        }
        int current = Math.max(0, nodes.indexOf(model.node(selectedNodeId)));
        selectedNodeId = nodes.get(Math.floorMod(current + distance, nodes.size())).id();
        ProgramInspectorPresentation.Node selected = model.node(selectedNodeId);
        viewport = viewport.reveal(selected.layer() * LAYER_GAP, selected.row() * ROW_GAP, NODE_WIDTH, NODE_HEIGHT,
                canvasWidth(), canvasHeight(), contentWidth(), contentHeight());
    }

    @Override
    public Component getNarrationMessage() {
        ProgramInspectorPresentation.Narration narration = ProgramInspectorPresentation.narration(model, selectedNodeId,
                viewport.zoom(), viewport.panX(), viewport.panY());
        if (narration == null) {
            return functionalNarration();
        }
        return functionalNarration().append(Component.literal(". ")).append(narrationMessage(narration));
    }

    private net.minecraft.network.chat.MutableComponent functionalNarration() {
        List<ScopedFunctionalProjection.Row> rows = functionalPanel == FunctionalPanel.AUTHORED ? functionalProjection.authoredRows() : functionalProjection.checkedRows();
        Component selected = rows.isEmpty() || functionalPanel == FunctionalPanel.GRAPH
                ? Component.empty()
                : Component.translatable("screen.mathmod.rune_inspector.functional_selected_row", panelLabel(functionalPanel), rows.get(functionalRow).structuralPath(), rowKindLabel(rows.get(functionalRow).kind()), rows.get(functionalRow).primaryToken());
        net.minecraft.network.chat.MutableComponent diagnostics = Component.empty();
        for (ScopedFunctionalProjection.Diagnostic diagnostic : functionalProjection.diagnostics()) {
            diagnostics.append(Component.literal(". "))
                    .append(diagnosticLabel(diagnostic.code()))
                    .append(Component.literal(" "))
                    .append(Component.literal(diagnostic.structuralPath()));
        }
        return Component.translatable("screen.mathmod.rune_inspector.functional_narration", sourceLabel(functionalProjection.sourceState()), attemptLabel(functionalProjection.attemptState()), graphStateLabel(functionalProjection.graphState()), relationLabel(functionalProjection.graphRelation()), functionalProjection.diagnostics().size())
                .append(Component.literal(". "))
                .append(panelLabel(functionalPanel))
                .append(selected)
                .append(diagnostics);
    }

    private static Component narrationMessage(ProgramInspectorPresentation.Narration narration) {
        return Component.translatable("screen.mathmod.rune_inspector.read_only")
                .append(Component.literal(". "))
                .append(Component.translatable("screen.mathmod.rune_inspector.rune", narration.nodeId()))
                .append(Component.literal(". "))
                .append(Component.translatable("narrator.position", narration.position(), narration.total()))
                .append(Component.literal(". "))
                .append(Component.translatable("screen.mathmod.rune_inspector.input_sockets", narration.socketBindings()))
                .append(Component.literal(". "))
                .append(Component.translatable("screen.mathmod.rune_inspector.output_socket", narration.outputSocket()))
                .append(Component.literal(". "))
                .append(Component.translatable("screen.mathmod.rune_inspector.viewport",
                        Math.round(narration.zoom() * 100), Math.round(narration.panX()), Math.round(narration.panY())));
    }

    private Rect nodeRect(ProgramInspectorPresentation.Node node, int canvasX, int canvasY) {
        return viewport.screenRect(canvasX, canvasY, node.layer() * LAYER_GAP, node.row() * ROW_GAP, NODE_WIDTH, NODE_HEIGHT);
    }

    private int inputSocketY(ProgramInspectorPresentation.Node node, String inputName, Rect rect) {
        int index = Math.max(0, node.inputNames().indexOf(inputName));
        return inputSocketY(rect, index, node.inputNames().size());
    }

    private int canvasWidth() { return contentRect().width(); }
    private int functionalCanvasWidth() { return Math.max(130, panelWidth * (panelWidth < 700 ? 1 : 3) / (panelWidth < 700 ? 2 : 5) - 16); }
    private int canvasHeight() { return contentRect().height(); }
    private int contentWidth() { return Math.max(canvasWidth(), (model.nodes().stream().mapToInt(ProgramInspectorPresentation.Node::layer).max().orElse(0) + 1) * LAYER_GAP + NODE_WIDTH); }
    private int contentHeight() { return Math.max(canvasHeight(), (model.nodes().stream().mapToInt(ProgramInspectorPresentation.Node::row).max().orElse(0) + 1) * ROW_GAP + NODE_HEIGHT); }
    private Rect outerCanvasRect() {
        return new Rect(panelX + 10, panelY + 64,
                functionalCanvasWidth(), panelHeight - 62);
    }

    private Rect contentRect() {
        Rect outer = outerCanvasRect();
        return new Rect(outer.x() + 8, outer.y() + 10,
                outer.width() - 16, outer.height() - 20);
    }

    private void drawNodeText(GuiGraphics graphics, ProgramInspectorPresentation.Node node, Rect rect) {
        graphics.pose().pushPose();
        graphics.pose().translate(rect.x(), rect.y(), 0);
        graphics.pose().scale((float) viewport.zoom(), (float) viewport.zoom(), 1.0F);
        graphics.drawString(font, trim(node.id(), NODE_WIDTH - 18), 10, 5, MathGuiTheme.IVORY, false);
        graphics.drawString(font, trim(node.outputType(), NODE_WIDTH - 18), 10, 18, node.normalized() ? MathGuiTheme.GREEN : MathGuiTheme.MUTED, false);
        graphics.drawString(font, "out", NODE_WIDTH - 20, NODE_HEIGHT / 2 - 4, MathGuiTheme.MUTED, false);
        for (int index = 0; index < node.inputNames().size(); index++) {
            int labelY = NODE_HEIGHT * (index + 1) / (node.inputNames().size() + 1) - 4;
            graphics.drawString(font, trim(node.inputNames().get(index), NODE_WIDTH - 18), 7, labelY, MathGuiTheme.MUTED, false);
        }
        graphics.pose().popPose();
    }

    private void drawScaledText(GuiGraphics graphics, String text, int x, int y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale((float) viewport.zoom(), (float) viewport.zoom(), 1.0F);
        graphics.drawString(font, text, 0, 0, color, false);
        graphics.pose().popPose();
    }

    private static int purityColor(com.mathmod.runes.RunePurity purity) {
        return switch (purity) {
            case PURE -> MathGuiTheme.TEAL;
            case OBSERVATION -> MathGuiTheme.GOLD;
            case EFFECT -> MathGuiTheme.CORAL;
        };
    }

    private static String trim(String value, int width) {
        return value.length() > 16 ? value.substring(0, 15) + "." : value;
    }

    private static List<Component> tooltip(ProgramInspectorPresentation.Node node) {
        return List.of(
                Component.literal(node.id()),
                Component.literal(node.purity().name() + " | " + node.formula()),
                Component.literal(node.normalized() ? "normalized: " + node.normalizedValue() : "dynamic")
        );
    }

    /** Screen-only geometry; it deliberately does not alter graph presentation semantics. */
    private record Viewport(double panX, double panY, double zoom) {
        private static final double MIN_ZOOM = 0.50D;
        private static final double MAX_ZOOM = 2.00D;
        private Viewport { zoom = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, zoom)); }
        static Viewport initial() { return new Viewport(0, 0, 1); }
        Viewport pan(double deltaX, double deltaY, int canvasWidth, int canvasHeight, int contentWidth, int contentHeight) {
            double maximumX = Math.max(0, contentWidth - canvasWidth / zoom);
            double maximumY = Math.max(0, contentHeight - canvasHeight / zoom);
            return new Viewport(clamp(panX + deltaX / zoom, -maximumX, 0), clamp(panY + deltaY / zoom, -maximumY, 0), zoom);
        }
        Viewport zoomBy(double factor, int canvasWidth, int canvasHeight, int contentWidth, int contentHeight) {
            return new Viewport(panX, panY, zoom * factor).pan(0, 0, canvasWidth, canvasHeight, contentWidth, contentHeight);
        }
        Viewport reveal(int logicalX, int logicalY, int logicalWidth, int logicalHeight, int canvasWidth, int canvasHeight, int contentWidth, int contentHeight) {
            double nextX = panX;
            double nextY = panY;
            if (logicalX + nextX < 0) nextX = -logicalX;
            if (logicalX + logicalWidth + nextX > canvasWidth / zoom) nextX = canvasWidth / zoom - logicalWidth - logicalX;
            if (logicalY + nextY < 0) nextY = -logicalY;
            if (logicalY + logicalHeight + nextY > canvasHeight / zoom) nextY = canvasHeight / zoom - logicalHeight - logicalY;
            return new Viewport(nextX, nextY, zoom).pan(0, 0, canvasWidth, canvasHeight, contentWidth, contentHeight);
        }
        int screenX(int canvasX, int logicalX) { return canvasX + (int) Math.round((logicalX + panX) * zoom); }
        int screenY(int canvasY, int logicalY) { return canvasY + (int) Math.round((logicalY + panY) * zoom); }
        int screenLength(int logicalLength) { return Math.max(1, (int) Math.round(logicalLength * zoom)); }
        Rect screenRect(int canvasX, int canvasY, int logicalX, int logicalY, int logicalWidth, int logicalHeight) {
            return new Rect(screenX(canvasX, logicalX), screenY(canvasY, logicalY), screenLength(logicalWidth), screenLength(logicalHeight));
        }
        private static double clamp(double value, double lower, double upper) { return Math.max(lower, Math.min(upper, value)); }
    }

    private record Rect(int x, int y, int width, int height) {
        boolean contains(double pointX, double pointY) { return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height; }
    }

    private static int inputSocketY(Rect node, int socketIndex, int socketCount) {
        return socketCount <= 0 ? node.y() + node.height() / 2
                : node.y() + node.height() * (Math.max(0, socketIndex) + 1) / (socketCount + 1);
    }

    private static Rect boundedLabelRect(Rect content, int preferredX, int preferredY, int width, int height) {
        int boundedWidth = Math.min(width, content.width());
        int boundedHeight = Math.min(height, content.height());
        return new Rect(Math.max(content.x(), Math.min(preferredX, content.x() + content.width() - boundedWidth)),
                Math.max(content.y(), Math.min(preferredY, content.y() + content.height() - boundedHeight)), boundedWidth, boundedHeight);
    }

    private record FunctionalLayout(Section authored, Section checked, Section graph) { }

    /** One shared geometry model for selector hit targets and rendered functional content. */
    private record Section(Rect selector, int headingY, int bottom) {
        static Section rows(Rect selector, int headingHeight, List<ScopedFunctionalProjection.Row> rows, int visibleRows) {
            int headingY = selector.y() + selector.height() + 4;
            return rows(selector, headingY, headingHeight, rows, visibleRows);
        }
        static Section rows(Rect selector, int headingY, int headingHeight, List<ScopedFunctionalProjection.Row> rows, int visibleRows) {
            return new Section(selector, headingY, headingY + headingHeight + Math.max(1, Math.min(visibleRows, rows.size())) * 11);
        }
        static Section graph(Rect selector, int headingHeight) {
            int headingY = selector.y() + selector.height() + 4;
            return graph(selector, headingY, headingHeight, 44);
        }
        static Section graph(Rect selector, int headingY, int headingHeight, int valueHeight) {
            return new Section(selector, headingY, headingY + headingHeight + valueHeight);
        }
        boolean contains(double x, double y) {
            return x >= selector.x() && x < selector.x() + selector.width()
                    && y >= selector.y() && y < bottom;
        }
    }

    private enum FunctionalPanel { AUTHORED, CHECKED, GRAPH }

    private static Component sourceLabel(ScopedFunctionalProjection.SourceState value) { return label("source", value); }
    private static Component attemptLabel(ScopedFunctionalProjection.AttemptState value) { return label("attempt", value); }
    private static Component graphStateLabel(ScopedFunctionalProjection.GraphState value) { return label("graph", value); }
    private static Component relationLabel(ScopedFunctionalProjection.GraphRelation value) { return label("relation", value); }
    private static Component diagnosticLabel(ScopedFunctionalProjection.Code value) { return label("diagnostic", value); }
    private static Component rowKindLabel(ScopedFunctionalProjection.RowKind value) { return label("row", value); }
    private static Component panelLabel(FunctionalPanel value) { return label("panel", value); }
    private static Component label(String group, Enum<?> value) {
        String key = "screen.mathmod.rune_inspector.functional." + group + "." + value.name().toLowerCase(java.util.Locale.ROOT);
        return net.minecraft.client.resources.language.I18n.exists(key)
                ? Component.translatable(key)
                : Component.translatable("screen.mathmod.rune_inspector.functional.unavailable");
    }
}
