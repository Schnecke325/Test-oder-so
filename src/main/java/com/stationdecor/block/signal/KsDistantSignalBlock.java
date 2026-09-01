package com.stationdecor.block.signal;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Ks-Vorsignal. Analog zu {@link KsMainSignalBlock}, zeigt aber
 * {@link DistantSignalAspect} (Vr0/Vr1/Vr2) statt Hp0/Hp1/Hp2.
 */
public class KsDistantSignalBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<KsDistantSignalBlock> CODEC = simpleCodec(KsDistantSignalBlock::new);
    public static final EnumProperty<DistantSignalAspect> ASPECT = EnumProperty.create("aspect", DistantSignalAspect.class);

    private static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);

    public KsDistantSignalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ASPECT, DistantSignalAspect.VR0));
    }

    @Override
    protected MapCodec<KsDistantSignalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ASPECT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ASPECT, state.getValue(ASPECT).next()), 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
