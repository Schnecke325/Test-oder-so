package com.stationdecor.block.seat;

import com.mojang.serialization.MapCodec;
import com.stationdecor.block.rotation.RotationUtil;
import com.stationdecor.config.StationDecorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Sitz-Block: frei rotierbar (wie {@link com.stationdecor.block.obj.ObjDisplayBlock}),
 * richtet sich beim Platzieren aber automatisch an einem direkt angrenzenden
 * bereits vorhandenen Sitz-Block aus (statt an der Blickrichtung des Spielers),
 * sofern die Config das erlaubt - Schleichen (Shift) beim Platzieren
 * überschreibt das und erzwingt die eigene Blickrichtung, auch neben einem
 * bestehenden Sitz-Block. Ein Rechtsklick setzt den Spieler auf den Block,
 * ein weiterer Rechtsklick lässt ihn wieder aufstehen.
 */
public class SeatBlock extends BaseEntityBlock {

    public static final MapCodec<SeatBlock> CODEC = simpleCodec(SeatBlock::new);

    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 10, 14);

    public SeatBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<SeatBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SeatBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof SeatBlockEntity blockEntity)) {
            return;
        }

        int steps = StationDecorConfig.SEAT_BLOCK_ROTATION_STEPS.get();
        boolean sneaking = placer != null && placer.isShiftKeyDown();
        Integer alignedIndex = StationDecorConfig.SEAT_BLOCK_AUTO_ALIGN.get() && !sneaking
                ? findNeighborRotationIndex(level, pos, steps)
                : null;

        if (alignedIndex != null) {
            blockEntity.setRotation(alignedIndex, steps);
        } else {
            float yaw = placer != null ? placer.getYRot() : 0f;
            blockEntity.setRotation(RotationUtil.snapToIndex(yaw, steps), steps);
        }
    }

    /**
     * Sucht in den vier horizontalen Nachbarblöcken nach einem bereits
     * vorhandenen Sitz-Block und übernimmt dessen Rotation (umgerechnet auf
     * die aktuell konfigurierte Schrittzahl).
     */
    @Nullable
    private static Integer findNeighborRotationIndex(Level level, BlockPos pos, int steps) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            if (level.getBlockEntity(neighborPos) instanceof SeatBlockEntity neighbor) {
                return RotationUtil.snapToIndex(neighbor.getRotationDegrees(), steps);
            }
        }
        return null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            AABB searchBox = new AABB(pos).inflate(0.1);
            for (SeatEntity seat : level.getEntitiesOfClass(SeatEntity.class, searchBox)) {
                seat.ejectPassengers();
                seat.discard();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.getVehicle() instanceof SeatEntity riddenSeat && riddenSeat.blockPosition().equals(pos)) {
            player.stopRiding();
            return InteractionResult.SUCCESS;
        }
        if (player.isPassenger()) {
            // Spieler sitzt bereits woanders.
            return InteractionResult.PASS;
        }

        AABB searchBox = new AABB(pos).inflate(0.1);
        List<SeatEntity> existing = level.getEntitiesOfClass(SeatEntity.class, searchBox);
        if (!existing.isEmpty() && !existing.get(0).getPassengers().isEmpty()) {
            // Sitzplatz bereits belegt.
            return InteractionResult.PASS;
        }

        if (!(level.getBlockEntity(pos) instanceof SeatBlockEntity blockEntity)) {
            return InteractionResult.PASS;
        }

        // +180°: die Modellrotation zeigt die Ausrichtung der Rückenlehne, der
        // Spieler soll aber von der Lehne weg blicken, nicht auf sie drauf.
        float yaw = blockEntity.getRotationDegrees() + 180f;
        SeatEntity seat;
        if (existing.isEmpty()) {
            seat = SeatEntity.create(level, pos, yaw);
            if (seat == null) {
                return InteractionResult.FAIL;
            }
            level.addFreshEntity(seat);
        } else {
            seat = existing.get(0);
        }

        player.setYRot(yaw);
        player.setYHeadRot(yaw);
        player.startRiding(seat, true);
        return InteractionResult.SUCCESS;
    }
}
