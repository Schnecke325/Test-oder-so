package com.stationdecor.client.screen;

import com.stationdecor.menu.ObjDisplayMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen des Fahrkartenautomaten. Enthält aktuell nur Platzhalter-Zielknöpfe
 * (kaufen ist noch nicht implementiert) sowie einen Münzslot, der später für
 * eine Create: Numismatics-Bezahlung genutzt werden soll. Komplett
 * selbstgezeichnet (kein externes GUI-Texturbild nötig).
 */
public class ObjDisplayScreen extends AbstractContainerScreen<ObjDisplayMenu> {

    private static final int PANEL_COLOR = 0xF0202225;
    private static final int PANEL_BORDER_COLOR = 0xFF3A3D42;
    private static final int SLOT_BG_COLOR = 0xFF373A3F;
    private static final int SLOT_BORDER_COLOR = 0xFF17181A;

    private static final int BUTTON_WIDTH = 56;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 6;
    private static final int BUTTON_ROW_0_Y = 22;
    private static final int BUTTON_ROW_1_Y = 46;
    private static final int DESTINATION_COUNT = 6;

    public ObjDisplayScreen(ObjDisplayMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 196;
        this.imageHeight = 196;
        this.titleLabelY = 6;
        // titleLabelY/inventoryLabelY sind relativ zum Bildursprung (leftPos/topPos), nicht absolut.
        this.inventoryLabelY = ObjDisplayMenu.COIN_SLOT_Y + 20;
    }

    @Override
    protected void init() {
        super.init();
        int startX = leftPos + 8;

        for (int i = 0; i < DESTINATION_COUNT; i++) {
            int col = i % 3;
            int row = i / 3;
            int x = startX + col * (BUTTON_WIDTH + BUTTON_GAP);
            int y = topPos + (row == 0 ? BUTTON_ROW_0_Y : BUTTON_ROW_1_Y);
            int destinationIndex = i + 1;

            this.addRenderableWidget(Button.builder(
                            Component.translatable("gui.station_decor.obj_display.destination", destinationIndex),
                            button -> onDestinationPressed())
                    .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build());
        }
    }

    private void onDestinationPressed() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.displayClientMessage(
                    Component.translatable("gui.station_decor.obj_display.not_implemented"), true);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;

        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, PANEL_BORDER_COLOR);

        drawSlotBackground(guiGraphics, x + ObjDisplayMenu.COIN_SLOT_X, y + ObjDisplayMenu.COIN_SLOT_Y);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBackground(guiGraphics, x + 8 + col * 18, y + 112 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlotBackground(guiGraphics, x + 8 + col * 18, y + 170);
        }
    }

    private static void drawSlotBackground(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 18, y + 18, SLOT_BORDER_COLOR);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_BG_COLOR);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
