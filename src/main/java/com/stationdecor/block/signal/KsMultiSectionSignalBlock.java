package com.stationdecor.block.signal;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.stationdecor.registry.ModBlockEntities;

/**
 * Ks-Mehrabschnittssignal: kombiniert Haupt- und Vorsignalfunktion.
 * Anders als {@link KsMainSignalBlock}/{@link KsDistantSignalBlock} braucht
 * dieser Block eine BlockEntity, weil er aktiv zwei Quellen kombiniert:
 * <ol>
 *   <li>Ein Create-Gleissignal bis zu 10 Blöcke unter sich (periodisch
 *       gescannt, siehe {@link KsMultiSectionSignalBlockEntity}).</li>
 *   <li>Der Signalbegriff des Signals "davor" (per Create Display Link
 *       gebunden, siehe {@code com.stationdecor.compat.create}).</li>
 * </ol>
 * Der angezeigte Begriff ({@link CombinedSignalAspect}) bleibt wie bei den
 * anderen Signalen eine BlockState-Property, damit Vanilla die Anzeige
 * automatisch zum Client synchronisiert.
 */
public class KsMultiSectionSignalBlock extends BaseEntityBlock {

    public static final MapCodec<KsMultiSectionSignalBlock> CODEC = simpleCodec(KsMultiSectionSignalBlock::new);
    public static final EnumProperty<CombinedSignalAspect> ASPECT = EnumProperty.create("aspect", CombinedSignalAspect.class);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);

    public KsMultiSectionSignalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ASPECT, CombinedSignalAspect.HALT));
    }

    @Override
    protected MapCodec<KsMultiSectionSignalBlock> codec() {
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
        return new KsMultiSectionSignalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.KS_MULTI_SECTION_SIGNAL.get(),
                KsMultiSectionSignalBlockEntity::serverTick);
    }
}
