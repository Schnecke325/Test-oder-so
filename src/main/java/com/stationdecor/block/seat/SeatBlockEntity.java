package com.stationdecor.block.seat;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity des Sitz-Blocks. Enthält keine eigenen Daten über die
 * geerbte Rotation hinaus - die eigentliche Sitzlogik lebt in {@link SeatBlock}
 * und {@link SeatEntity}.
 */
public class SeatBlockEntity extends AbstractRotatableBlockEntity {

    public SeatBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEAT.get(), pos, state);
    }
}
