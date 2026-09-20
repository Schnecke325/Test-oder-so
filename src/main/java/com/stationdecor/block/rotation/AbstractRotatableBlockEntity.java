package com.stationdecor.block.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRotatableBlockEntity extends BlockEntity {

    private int rotationIndex = 0;
    private int rotationSteps = 8;

    protected AbstractRotatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setRotation(int index, int steps) {
        this.rotationSteps = Math.max(1, steps);
        this.rotationIndex = RotationUtil.clampIndex(index, this.rotationSteps);
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public int getRotationIndex() {
        return rotationIndex;
    }

    public int getRotationSteps() {
        return rotationSteps;
    }

    public float getRotationDegrees() {
        return RotationUtil.indexToDegrees(rotationIndex, rotationSteps);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("RotationIndex", rotationIndex);
        tag.putInt("RotationSteps", rotationSteps);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.rotationSteps = Math.max(1, tag.getInt("RotationSteps"));
        this.rotationIndex = RotationUtil.clampIndex(tag.getInt("RotationIndex"), this.rotationSteps);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
