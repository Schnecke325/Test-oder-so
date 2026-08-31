package com.stationdecor.block.marking;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Flache Bodenmarkierung. Rotation ebenso frei/konfigurierbar wie bei den
 * anderen Blöcken, zusätzlich mit einem Nah/Mitte/Fern-Versatz, der beim
 * Platzieren über {@link FloorMarkingBlockItem} anhand der 3 Zonen der
 * Vorschau-Outline (siehe {@code client.render.PlacementOutlineHandler})
 * bestimmt wird. Rotation und Versatz werden bewusst dort (im Item, mit
 * Zugriff auf den genauen Klickpunkt) gesetzt statt in {@code setPlacedBy}.
 */
public class FloorMarkingBlock extends BaseEntityBlock {

    public static final MapCodec<FloorMarkingBlock> CODEC = simpleCodec(FloorMarkingBlock::new);

    /** Dünne, rotationsunabhängige Kollisionsbox knapp über dem Boden. */
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

    public FloorMarkingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<FloorMarkingBlock> codec() {
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
        return new FloorMarkingBlockEntity(pos, state);
    }
}
