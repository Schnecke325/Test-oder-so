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
 * Container-Menü des Fahrkartenautomaten: ein einzelner "Münzslot" (Platz für
 * eine spätere Create: Numismatics-Integration zum Bezahlen) plus das
 * Spielerinventar. Die eigentlichen Ziel-/Fahrkarten-Knöpfe sind reine
 * Client-UI (siehe {@link com.stationdecor.client.screen.ObjDisplayScreen})
 * und brauchen daher keine Slots.
 */
public class ObjDisplayMenu extends AbstractContainerMenu {

    public static final int CONTAINER_SIZE = 1;
    public static final int COIN_SLOT_X = 90;
    public static final int COIN_SLOT_Y = 84;
    private static final int PLAYER_INV_Y = 112;
    private static final int HOTBAR_Y = 170;

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

        this.addSlot(new Slot(container, 0, COIN_SLOT_X, COIN_SLOT_Y));

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
