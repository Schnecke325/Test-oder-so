package com.stationdecor.menu;

import com.stationdecor.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container-Menü für den OBJ-Anzeigeblock: ein 3x3-Raster (wie ein Werfer)
 * plus das komplette Spielerinventar. Der Inhalt dieses GUIs ist bewusst
 * generisch gehalten und kann später leicht um blockspezifische Slots
 * (Filter, Ausgabe, etc.) erweitert werden.
 */
public class ObjDisplayMenu extends AbstractContainerMenu {

    public static final int CONTAINER_SIZE = 9;
    private static final int GRID_ORIGIN_X = 62;
    private static final int GRID_ORIGIN_Y = 17;
    private static final int PLAYER_INV_Y = 84;
    private static final int HOTBAR_Y = 142;

    private final Container container;

    /** Client-seitige Rekonstruktion aus dem Netzwerk (leerer Platzhalter, wird per Slot-Sync gefüllt). */
    public ObjDisplayMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    /** Server-seitige Konstruktion, gebunden an das echte Inventar der BlockEntity. */
    public ObjDisplayMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenus.OBJ_DISPLAY_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        this.container = container;
        container.startOpen(playerInventory.player);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container, col + row * 3, GRID_ORIGIN_X + col * 18, GRID_ORIGIN_Y + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, HOTBAR_Y));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(stackInSlot, CONTAINER_SIZE, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, CONTAINER_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
