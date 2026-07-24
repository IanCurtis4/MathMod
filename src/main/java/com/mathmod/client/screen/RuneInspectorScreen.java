package com.mathmod.client.screen;

import com.mathmod.program.ProgramSurface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** First read-only DAG canvas. It owns no server state and sends no packets. */
public final class RuneInspectorScreen extends Screen {
    private static final int NODE_WIDTH = 104;
    private static final int NODE_HEIGHT = 34;
    private static final int LAYER_GAP = 128;
    private static final int ROW_GAP = 48;

    private final Screen parent;
    private final ProgramInspectorPresentation.Model model;
    private String selectedNodeId;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    public RuneInspectorScreen(Screen parent, ProgramSurface surface) {
        super(Component.translatable("screen.mathmod.rune_inspector.title"));
        this.parent = parent;
        this.model = ProgramInspectorPresentation.build(surface);
        this.selectedNodeId = model.nodes().isEmpty() ? "" : model.orderedNodes().getFirst().id();
    }

    @Override
    protected void init() {
        panelWidth = Math.min(760, width - 20);
        panelHeight = Math.min(430, height - 20);
        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;
        addRenderableWidget(MathButton.action(
                panelX + panelWidth - 58,
                panelY + 10,
                48,
                Component.translatable("screen.mathmod.rune_inspector.close"),
                button -> onClose(),
                MathButton.Tone.NEUTRAL
        ));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        MathGuiTheme.fillChamfered(graphics, panelX, panelY, panelWidth, panelHeight, MathGuiTheme.INK);
        MathGuiTheme.outlineChamfered(graphics, panelX, panelY, panelWidth, panelHeight, MathGuiTheme.GOLD);
        graphics.drawString(font, title, panelX + 12, panelY + 15, MathGuiTheme.IVORY, false);
        graphics.drawString(font, Component.translatable("screen.mathmod.rune_inspector.read_only"), panelX + 12, panelY + 29, MathGuiTheme.MUTED, false);

        int canvasX = panelX + 10;
        int canvasY = panelY + 52;
        int canvasWidth = Math.max(130, panelWidth * 3 / 5 - 16);
        int canvasHeight = panelHeight - 62;
        int detailsX = canvasX + canvasWidth + 8;
        int detailsWidth = panelX + panelWidth - 10 - detailsX;
        MathGuiTheme.panel(graphics, canvasX, canvasY, canvasWidth, canvasHeight);
        MathGuiTheme.drawProofGrid(graphics, canvasX + 1, canvasY + 1, canvasWidth - 2, canvasHeight - 2);
        MathGuiTheme.panel(graphics, detailsX, canvasY, detailsWidth, canvasHeight);

        renderEdges(graphics, canvasX, canvasY, canvasWidth, canvasHeight);
        renderNodes(graphics, canvasX, canvasY, canvasWidth, canvasHeight, mouseX, mouseY);
        renderDetails(graphics, detailsX, canvasY, detailsWidth, canvasHeight);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            ProgramInspectorPresentation.Node hit = nodeAt(mouseX, mouseY);
            if (hit != null) {
                selectedNodeId = hit.id();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            onClose();
            return true;
        }
        if (keyCode == 262 || keyCode == 264) {
            moveSelection(1);
            return true;
        }
        if (keyCode == 263 || keyCode == 265) {
            moveSelection(-1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private void renderEdges(GuiGraphics graphics, int canvasX, int canvasY, int canvasWidth, int canvasHeight) {
        for (ProgramInspectorPresentation.Edge edge : model.edges()) {
            ProgramInspectorPresentation.Node from = model.node(edge.fromId());
            ProgramInspectorPresentation.Node to = model.node(edge.toId());
            if (from == null || to == null) {
                continue;
            }
            int fromX = nodeX(from, canvasX, canvasWidth) + NODE_WIDTH;
            int fromY = nodeY(from, canvasY, canvasHeight) + NODE_HEIGHT / 2;
            int toX = nodeX(to, canvasX, canvasWidth);
            int toY = nodeY(to, canvasY, canvasHeight) + NODE_HEIGHT / 2;
            graphics.hLine(fromX, toX, fromY, MathGuiTheme.BORDER_STRONG);
            graphics.vLine(toX, Math.min(fromY, toY), Math.max(fromY, toY), MathGuiTheme.BORDER_STRONG);
        }
    }

    private void renderNodes(GuiGraphics graphics, int canvasX, int canvasY, int canvasWidth, int canvasHeight, int mouseX, int mouseY) {
        for (ProgramInspectorPresentation.Node node : model.nodes()) {
            int x = nodeX(node, canvasX, canvasWidth);
            int y = nodeY(node, canvasY, canvasHeight);
            boolean selected = node.id().equals(selectedNodeId);
            int accent = purityColor(node.purity());
            MathGuiTheme.fillChamfered(graphics, x, y, NODE_WIDTH, NODE_HEIGHT, selected ? MathGuiTheme.SURFACE_SELECTED : MathGuiTheme.SURFACE_RAISED);
            MathGuiTheme.outlineChamfered(graphics, x, y, NODE_WIDTH, NODE_HEIGHT, selected ? accent : MathGuiTheme.BORDER_STRONG);
            graphics.fill(x + 4, y + 5, x + 6, y + NODE_HEIGHT - 5, accent);
            graphics.drawString(font, trim(node.id(), NODE_WIDTH - 16), x + 10, y + 5, MathGuiTheme.IVORY, false);
            graphics.drawString(font, trim(node.outputType(), NODE_WIDTH - 16), x + 10, y + 18, node.normalized() ? MathGuiTheme.GREEN : MathGuiTheme.MUTED, false);
            if (mouseX >= x && mouseX < x + NODE_WIDTH && mouseY >= y && mouseY < y + NODE_HEIGHT) {
                MathTooltipRenderer.render(graphics, font, tooltip(node), mouseX, mouseY);
            }
        }
    }

    private void renderDetails(GuiGraphics graphics, int x, int y, int width, int height) {
        ProgramInspectorPresentation.Node node = model.node(selectedNodeId);
        if (node == null) {
            graphics.drawString(font, Component.translatable("screen.mathmod.rune_inspector.empty"), x + 8, y + 9, MathGuiTheme.MUTED, false);
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
        int row = y + 9;
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

    private ProgramInspectorPresentation.Node nodeAt(double mouseX, double mouseY) {
        int canvasX = panelX + 10;
        int canvasY = panelY + 52;
        int canvasWidth = Math.max(130, panelWidth * 3 / 5 - 16);
        int canvasHeight = panelHeight - 62;
        return model.nodes().stream().filter(node -> {
            int x = nodeX(node, canvasX, canvasWidth);
            int y = nodeY(node, canvasY, canvasHeight);
            return mouseX >= x && mouseX < x + NODE_WIDTH && mouseY >= y && mouseY < y + NODE_HEIGHT;
        }).findFirst().orElse(null);
    }

    private void moveSelection(int distance) {
        List<ProgramInspectorPresentation.Node> nodes = model.orderedNodes();
        if (nodes.isEmpty()) {
            return;
        }
        int current = Math.max(0, nodes.indexOf(model.node(selectedNodeId)));
        selectedNodeId = nodes.get(Math.floorMod(current + distance, nodes.size())).id();
    }

    private static int nodeX(ProgramInspectorPresentation.Node node, int canvasX, int canvasWidth) {
        return canvasX + 8 + Math.min(node.layer() * LAYER_GAP, Math.max(0, canvasWidth - NODE_WIDTH - 16));
    }

    private static int nodeY(ProgramInspectorPresentation.Node node, int canvasY, int canvasHeight) {
        return canvasY + 10 + Math.min(node.row() * ROW_GAP, Math.max(0, canvasHeight - NODE_HEIGHT - 18));
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
}
