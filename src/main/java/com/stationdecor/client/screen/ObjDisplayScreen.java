package com.stationdecor.client.screen;

import com.stationdecor.menu.ObjDisplayMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Stark vereinfachter Platzhalter-Screen: nur ein grauer Kasten mit "Soon™".
 * Bewusst ohne Knöpfe/Slots, um beim Debuggen des OBJ-Modell-Ladens
 * (Fahrkartenautomat) möglichst wenig bewegliche Teile zu haben.
 */
public class ObjDisplayScreen extends AbstractContainerScreen<ObjDisplayMenu> {

    private static final int PANEL_COLOR = 0xF0202225;
    private static final int PANEL_BORDER_COLOR = 0xFF3A3D42;
    private static final Component SOON_TEXT = Component.literal("Soon™");

    public ObjDisplayScreen(ObjDisplayMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 140;
        this.imageHeight = 80;
        this.inventoryLabelY = -1000; // "Inventar"-Label ausblenden, es gibt keine Inventar-Slots
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, PANEL_BORDER_COLOR);
        guiGraphics.drawCenteredString(font, SOON_TEXT, x + imageWidth / 2, y + imageHeight / 2 - 4, 0xFFFFFF);
    }
}
