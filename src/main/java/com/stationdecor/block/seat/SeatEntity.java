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

/**
 * Unsichtbare, technische Entity, die als "Sitzplatz" für {@link SeatBlock}
 * dient. Sie wird beim Aufsetzen erzeugt und verschwindet automatisch
 * wieder, sobald niemand mehr auf ihr sitzt oder der zugehörige Block
 * entfernt wird - es bleiben also nie verwaiste Entities zurück.
 */
public class SeatEntity extends Entity {

    /** Höhe über der Blockunterkante, auf der der Spieler "sitzt". */
    public static final double SEAT_Y_OFFSET = 0.4;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setInvisible(true);
        this.setNoGravity(true);
    }

    /**
     * Erzeugt (aber fügt noch nicht der Welt hinzu) eine neue SeatEntity an
     * der Sitzposition über dem angegebenen Block, mit der Blickrichtung des
     * Blocks als Ausrichtung.
     */
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
        // Keine zusätzlichen synchronisierten Daten nötig.
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Rein technische Entity, es gibt nichts zu laden.
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // Rein technische Entity, es gibt nichts zu speichern.
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
        // Der Spieler sitzt exakt auf Höhe dieser Entity (siehe SEAT_Y_OFFSET beim Erzeugen),
        // ohne zusätzlichen Versatz.
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
