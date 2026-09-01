package com.stationdecor.block.obj;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.menu.ObjDisplayMenu;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity des Fahrkartenautomaten. Frei rotierbar wie Sitzblock/
 * Bodenmarkierung/Signale (siehe {@link AbstractRotatableBlockEntity}),
 * jetzt wo das Laden des Modells zuverlässig funktioniert. Trägt außerdem
 * die optische Variante ({@link TicketMachineStyle}), die beim Platzieren
 * vom Item übernommen wird (siehe {@link ObjDisplayBlockItem}).
 */
public class ObjDisplayBlockEntity extends AbstractRotatableBlockEntity implements MenuProvider {

    private TicketMachineStyle style = TicketMachineStyle.DB;

    public ObjDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OBJ_DISPLAY.get(), pos, state);
    }

    public TicketMachineStyle getStyle() {
        return style;
    }

    public void setStyle(TicketMachineStyle style) {
        this.style = style;
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Style", style.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        style = TicketMachineStyle.CODEC.byName(tag.getString("Style"), TicketMachineStyle.DB);
    }
}
