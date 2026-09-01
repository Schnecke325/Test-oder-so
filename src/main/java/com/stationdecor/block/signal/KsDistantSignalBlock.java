package com.stationdecor.block.signal;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.stationdecor.registry.ModBlockEntities;

/**
 * Ks-Vorsignal. Analog zu {@link KsMainSignalBlock}, zeigt aber
 * {@link DistantSignalAspect} (Vr0/Vr1/Vr2) statt Hp0/Hp1/Hp2. Die
 * BlockEntity scannt periodisch das per Signalbinder verlinkte "Signal
 * davor" (siehe {@link KsDistantSignalBlockEntity}) und zeichnet zusätzlich
 * die Lampenfläche unabhängig vom Umgebungslicht.
 */
public class KsDistantSignalBlock extends BaseEntityBlock {

    public static final MapCodec<KsDistantSignalBlock> CODEC = simpleCodec(KsDistantSignalBlock::new);
    public static final EnumProperty<DistantSignalAspect> ASPECT = EnumProperty.create("aspect", DistantSignalAspect.class);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KsDistantSignalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.KS_DISTANT_SIGNAL.get(),
                KsDistantSignalBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ASPECT, state.getValue(ASPECT).next()), 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
