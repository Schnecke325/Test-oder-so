package com.stationdecor.block.signal;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.compat.create.CreateCompat;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class KsMultiSectionSignalBlockEntity extends AbstractRotatableBlockEntity {

    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int MAX_SCAN_DISTANCE = 10;

    private boolean upstreamHalt = false;
    @Nullable
    private BlockPos linkedSignalPos;
    private int scanCooldown = 0;

    public KsMultiSectionSignalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KS_MULTI_SECTION_SIGNAL.get(), pos, state);
    }

    public void setUpstreamHalt(boolean halt) {
        if (this.upstreamHalt != halt) {
            this.upstreamHalt = halt;
            setChanged();
            recomputeAspect();
        }
    }

    public boolean isUpstreamHalt() {
        return upstreamHalt;
    }

    public void setLinkedSignalPos(@Nullable BlockPos pos) {
        this.linkedSignalPos = pos;
        setChanged();
        if (level != null && !level.isClientSide) {
            refreshLinkedUpstreamHalt();
            recomputeAspect();
        }
    }

    @Nullable
    public BlockPos getLinkedSignalPos() {
        return linkedSignalPos;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KsMultiSectionSignalBlockEntity blockEntity) {
        if (blockEntity.scanCooldown-- > 0) {
            return;
        }
        blockEntity.scanCooldown = SCAN_INTERVAL_TICKS;
        blockEntity.refreshLinkedUpstreamHalt();
        blockEntity.recomputeAspect();
    }

    private void refreshLinkedUpstreamHalt() {
        if (linkedSignalPos == null || level == null) {
            return;
        }
        Boolean halt = SignalLinkUtil.readHalt(level, linkedSignalPos);
        if (halt != null && halt != upstreamHalt) {
            upstreamHalt = halt;
            setChanged();
        }
    }

    private void recomputeAspect() {
        if (level == null || level.isClientSide) {
            return;
        }

        SectionState localSection = null;
        if (ModList.get().isLoaded("create")) {
            localSection = CreateCompat.readTrackSignalBelow(level, worldPosition, MAX_SCAN_DISTANCE);
        }

        CombinedSignalAspect newAspect;
        if (localSection == SectionState.HALT) {
            newAspect = CombinedSignalAspect.HALT;
        } else if (upstreamHalt) {
            newAspect = CombinedSignalAspect.HALT_ERWARTEN;
        } else {
            newAspect = CombinedSignalAspect.FAHRT;
        }

        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(KsMultiSectionSignalBlock.ASPECT) && state.getValue(KsMultiSectionSignalBlock.ASPECT) != newAspect) {
            level.setBlock(worldPosition, state.setValue(KsMultiSectionSignalBlock.ASPECT, newAspect), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("UpstreamHalt", upstreamHalt);
        if (linkedSignalPos != null) {
            tag.put("LinkedSignal", NbtUtils.writeBlockPos(linkedSignalPos));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        upstreamHalt = tag.getBoolean("UpstreamHalt");
        linkedSignalPos = tag.contains("LinkedSignal") ? NbtUtils.readBlockPos(tag, "LinkedSignal").orElse(null) : null;
    }
}
