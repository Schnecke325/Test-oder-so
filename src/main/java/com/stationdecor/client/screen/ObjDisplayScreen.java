package com.stationdecor.client.screen;

import com.stationdecor.menu.ObjDisplayMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen für {@link ObjDisplayMenu}. Nutzt bewusst die vanilla
 * Werfer-Textur (3x3-Raster) als Platzhalter-Hintergrund, damit für das
 * Grundgerüst keine eigene GUI-Grafik nötig ist. Kann später gegen eine
 * eigene Textur unter assets/station_decor/textures/gui/ ausgetauscht werden.
 */
public class ObjDisplayScreen extends AbstractContainerScreen<ObjDisplayMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png");

    public ObjDisplayScreen(ObjDisplayMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
