package com.stationdecor.block.marking;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class FloorMarkingBlockEntity extends AbstractRotatableBlockEntity {

    public static final float OFFSET_STEP = 1f / 3f;

    private int offsetIndex = 0;

    public FloorMarkingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLOOR_MARKING.get(), pos, state);
    }

    public void setPlacement(int rotationIndex, int rotationSteps, int offsetIndex) {
        this.offsetIndex = Math.max(-1, Math.min(1, offsetIndex));
        setRotation(rotationIndex, rotationSteps);
    }

    public int getOffsetIndex() {
        return offsetIndex;
    }

    public float getOffsetDistance() {
        return offsetIndex * OFFSET_STEP;
    }

    public DyeColor getColor() {
        return ((FloorMarkingBlock) getBlockState().getBlock()).getColor();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("OffsetIndex", offsetIndex);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.offsetIndex = Math.max(-1, Math.min(1, tag.getInt("OffsetIndex")));
    }
}
