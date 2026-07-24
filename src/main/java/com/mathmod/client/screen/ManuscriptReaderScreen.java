package com.mathmod.client.screen;

import com.mathmod.manuscript.ManuscriptReaderView;
import com.mathmod.network.OpenManuscriptManualPayload;
import com.mathmod.program.ProgramPresets;
import com.mathmod.program.ProgramSurface;
import com.mathmod.screen.ManuscriptReaderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

/** Read-only P6 surface with optional navigation into existing knowledge surfaces. */
public final class ManuscriptReaderScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<ManuscriptReaderMenu> {
    private int page;

    public ManuscriptReaderScreen(ManuscriptReaderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 300;
        imageHeight = 240;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(MathButton.action(leftPos + 12, topPos + imageHeight - 28, 52,
                Component.translatable("screen.mathmod.manuscript_reader.previous"), button -> { if (page > 0) page--; }, MathButton.Tone.NEUTRAL));
        addRenderableWidget(MathButton.action(leftPos + imageWidth - 64, topPos + imageHeight - 28, 52,
                Component.translatable("screen.mathmod.manuscript_reader.next"), button -> {
                    if (page + 1 < menu.view().pageTranslationKeys().size()) page++;
                }, MathButton.Tone.NEUTRAL));
        ManuscriptReaderView view = menu.view();
        MathButton manual = MathButton.action(leftPos + 68, topPos + imageHeight - 54, 96,
                Component.translatable("screen.mathmod.manuscript_reader.manual"),
                button -> PacketDistributor.sendToServer(OpenManuscriptManualPayload.INSTANCE), MathButton.Tone.RESOURCE);
        manual.active = ModList.get().isLoaded("patchouli") && view.patchouliEntry().isPresent();
        manual.setTooltip(net.minecraft.client.gui.components.Tooltip.create(Component.translatable(
                manual.active ? "screen.mathmod.manuscript_reader.manual_hint" : "screen.mathmod.manuscript_reader.manual_unavailable")));
        addRenderableWidget(manual);
        MathButton theorem = MathButton.action(leftPos + 170, topPos + imageHeight - 54, 96,
                Component.translatable("screen.mathmod.manuscript_reader.theorem"),
                button -> view.theoremId()
                        .flatMap(id -> ProgramPresets.presetForId(id.toString()))
                        .ifPresent(preset -> minecraft.setScreen(new RuneInspectorScreen(
                                this,
                                ProgramSurface.theorem(preset.graph())
                        ))), MathButton.Tone.NEUTRAL);
        theorem.active = view.theoremId()
                .flatMap(id -> ProgramPresets.presetForId(id.toString()))
                .isPresent();
        theorem.setTooltip(net.minecraft.client.gui.components.Tooltip.create(Component.translatable(
                theorem.active ? "screen.mathmod.manuscript_reader.theorem_hint" : "screen.mathmod.manuscript_reader.theorem_unavailable")));
        addRenderableWidget(theorem);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        MathGuiTheme.fillChamfered(graphics, leftPos, topPos, imageWidth, imageHeight, MathGuiTheme.INK);
        MathGuiTheme.outlineChamfered(graphics, leftPos, topPos, imageWidth, imageHeight, MathGuiTheme.GOLD);
        MathGuiTheme.panel(graphics, leftPos + 12, topPos + 42, imageWidth - 24, imageHeight - 78);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        ManuscriptReaderView view = menu.view();
        if (!view.available()) {
            graphics.drawString(font, Component.translatable("screen.mathmod.manuscript_reader.missing"), 20, 58, MathGuiTheme.CORAL, false);
            graphics.drawString(font, Component.literal(view.requestedId().toString()), 20, 76, MathGuiTheme.MUTED, false);
            return;
        }
        graphics.drawString(font, Component.translatable(view.titleTranslationKey()), 20, 18, MathGuiTheme.IVORY, false);
        graphics.drawString(font, Component.translatable(view.traditionNameTranslationKey()), 20, 31, MathGuiTheme.TEAL, false);
        if (view.pageTranslationKeys().isEmpty()) return;
        int y = 52;
        for (var line : font.split(Component.translatable(view.pageTranslationKeys().get(page)), imageWidth - 44)) {
            graphics.drawString(font, line, 22, y, MathGuiTheme.IVORY, false);
            y += 11;
        }
        graphics.drawString(font, Component.translatable("screen.mathmod.manuscript_reader.page", page + 1, view.pageTranslationKeys().size()), 110, imageHeight - 20, MathGuiTheme.MUTED, false);
    }
}
