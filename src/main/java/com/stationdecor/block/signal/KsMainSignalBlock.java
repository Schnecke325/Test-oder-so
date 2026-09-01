package com.stationdecor.block.signal;

import com.mojang.serialization.MapCodec;
import com.stationdecor.registry.ModBlockEntities;
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

/**
 * Ks-Hauptsignal. Der aktuell gezeigte Signalbegriff ({@link MainSignalAspect})
 * ist als BlockState-Property abgebildet, damit Vanilla die Synchronisation
 * zum Client automatisch übernimmt. Die BlockEntity scannt periodisch bis zu
 * 10 Blöcke unter sich nach einem Create-Gleissignal und übernimmt dessen
 * Zustand automatisch (siehe {@link KsMainSignalBlockEntity}). Rechtsklick
 * schaltet weiterhin manuell weiter (überschrieben vom nächsten Scan, falls
 * dabei ein Gleissignal gefunden wird); ein Create Display Link kann den
 * Begriff ebenfalls setzen, siehe
 * {@code com.stationdecor.compat.create.KsMainSignalDisplayTarget}.
 */
public class KsMainSignalBlock extends BaseEntityBlock {

    public static final MapCodec<KsMainSignalBlock> CODEC = simpleCodec(KsMainSignalBlock::new);
    public static final EnumProperty<MainSignalAspect> ASPECT = EnumProperty.create("aspect", MainSignalAspect.class);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KsMainSignalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.KS_MAIN_SIGNAL.get(),
                KsMainSignalBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ASPECT, state.getValue(ASPECT).next()), 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
