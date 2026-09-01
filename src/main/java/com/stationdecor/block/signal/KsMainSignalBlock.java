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
 * Ks-Hauptsignal. Der aktuell gezeigte Signalbegriff ({@link MainSignalAspect})
 * ist direkt als BlockState-Property abgebildet (kein BlockEntity nötig) -
 * dadurch übernimmt Vanilla die komplette Synchronisation zum Client
 * automatisch. Rechtsklick schaltet manuell weiter (zum Testen ohne Create);
 * die eigentliche Steuerung erfolgt über Create: Display Links, siehe
 * {@code com.stationdecor.compat.create.KsMainSignalDisplayTarget}.
 */
public class KsMainSignalBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<KsMainSignalBlock> CODEC = simpleCodec(KsMainSignalBlock::new);
    public static final EnumProperty<MainSignalAspect> ASPECT = EnumProperty.create("aspect", MainSignalAspect.class);

    private static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);

    public KsMainSignalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ASPECT, MainSignalAspect.HP0));
    }

    @Override
    protected MapCodec<KsMainSignalBlock> codec() {
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
