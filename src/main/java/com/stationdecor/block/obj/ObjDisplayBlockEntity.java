package com.stationdecor.block.obj;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.menu.ObjDisplayMenu;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity des Fahrkartenautomaten. Frei rotierbar wie Sitzblock/
 * Bodenmarkierung/Signale (siehe {@link AbstractRotatableBlockEntity}),
 * jetzt wo das Laden des Modells zuverlässig funktioniert.
 */
public class ObjDisplayBlockEntity extends AbstractRotatableBlockEntity implements MenuProvider {

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
