package com.stationdecor.block.signal;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KsDistantSignalBlockEntity extends AbstractRotatableBlockEntity {

    private static final int SCAN_INTERVAL_TICKS = 10;

    @Nullable
    private BlockPos linkedSignalPos;
    private int scanCooldown = 0;

    public KsDistantSignalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KS_DISTANT_SIGNAL.get(), pos, state);
    }

    public void setLinkedSignalPos(@Nullable BlockPos pos) {
        this.linkedSignalPos = pos;
        setChanged();
        if (level != null && !level.isClientSide) {
            recomputeAspect();
        }
    }

    @Nullable
    public BlockPos getLinkedSignalPos() {
        return linkedSignalPos;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KsDistantSignalBlockEntity blockEntity) {
        if (blockEntity.scanCooldown-- > 0) {
            return;
        }
        blockEntity.scanCooldown = SCAN_INTERVAL_TICKS;
        blockEntity.recomputeAspect();
    }

    private void recomputeAspect() {
        if (level == null || level.isClientSide || linkedSignalPos == null) {
            return;
        }
        Boolean halt = SignalLinkUtil.readHalt(level, linkedSignalPos);
        if (halt == null) {
            return;
        }
        DistantSignalAspect newAspect = halt ? DistantSignalAspect.VR0 : DistantSignalAspect.VR1;
        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(KsDistantSignalBlock.ASPECT) && state.getValue(KsDistantSignalBlock.ASPECT) != newAspect) {
            level.setBlock(worldPosition, state.setValue(KsDistantSignalBlock.ASPECT, newAspect), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (linkedSignalPos != null) {
            tag.put("LinkedSignal", NbtUtils.writeBlockPos(linkedSignalPos));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        linkedSignalPos = tag.contains("LinkedSignal") ? NbtUtils.readBlockPos(tag, "LinkedSignal").orElse(null) : null;
    }
}
