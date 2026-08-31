package com.stationdecor.menu;

import com.stationdecor.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Stark vereinfachtes, slot-loses Menü des Fahrkartenautomaten - dient
 * aktuell nur dazu, den "Soon™"-Platzhalter-Screen zu öffnen. Wird wieder
 * um den Münzslot/Ziel-Knöpfe erweitert, sobald das eigentliche
 * OBJ-Modell-Laden zuverlässig funktioniert.
 */
public class ObjDisplayMenu extends AbstractContainerMenu {

    /** Client-seitige Rekonstruktion aus dem Netzwerk. */
    public ObjDisplayMenu(int containerId, Inventory playerInventory) {
        super(ModMenus.OBJ_DISPLAY_MENU.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
