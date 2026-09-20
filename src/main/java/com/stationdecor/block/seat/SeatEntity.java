package com.stationdecor.block.seat;

import com.stationdecor.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SeatEntity extends Entity {

    public static final double SEAT_Y_OFFSET = 0.4;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setInvisible(true);
        this.setNoGravity(true);
    }

    @Nullable
    public static SeatEntity create(Level level, BlockPos pos, float yawDegrees) {
        SeatEntity seat = ModEntities.SEAT.get().create(level);
        if (seat != null) {
            seat.moveTo(pos.getX() + 0.5, pos.getY() + SEAT_Y_OFFSET, pos.getZ() + 0.5, yawDegrees, 0f);
            seat.yRotO = yawDegrees;
        }
        return seat;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
        return Vec3.ZERO;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        if (!(level().getBlockState(blockPosition()).getBlock() instanceof SeatBlock)) {
            ejectPassengers();
            discard();
            return;
        }
        if (getPassengers().isEmpty()) {
            discard();
        }
    }
}
