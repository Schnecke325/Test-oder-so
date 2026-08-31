package com.stationdecor.block.obj;

import com.stationdecor.menu.ObjDisplayMenu;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity des Fahrkartenautomaten. Bewusst stark vereinfacht (keine
 * Rotation, kein Inventar) während das Laden des gelieferten OBJ-Modells
 * debuggt wird - siehe README ("Bekannte Einschränkungen"). Rotation wird
 * wieder ergänzt (analog {@code AbstractRotatableBlockEntity}), sobald das
 * Modell zuverlässig rendert.
 */
public class ObjDisplayBlockEntity extends BlockEntity implements MenuProvider {

    public ObjDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OBJ_DISPLAY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.station_decor.obj_display");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ObjDisplayMenu(containerId, playerInventory);
    }
}
