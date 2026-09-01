package com.stationdecor.block.signal;

import com.mojang.serialization.MapCodec;
import com.stationdecor.block.rotation.RotationUtil;
import com.stationdecor.config.StationDecorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.stationdecor.item.SignalBinderItem;
import com.stationdecor.registry.ModBlockEntities;

/**
 * Ks-Mehrabschnittssignal: kombiniert Haupt- und Vorsignalfunktion.
 * Braucht eine BlockEntity, weil er aktiv zwei Quellen kombiniert:
 * <ol>
 *   <li>Ein Create-Gleissignal bis zu 10 Blöcke unter sich (periodisch
 *       gescannt, siehe {@link KsMultiSectionSignalBlockEntity}).</li>
 *   <li>Der Signalbegriff des per Signalbinder verlinkten "Signal davor".</li>
 * </ol>
 * Dieselbe BlockEntity trägt auch die freie Rotation (siehe
 * {@code AbstractRotatableBlockEntity}). Der angezeigte Begriff
 * ({@link CombinedSignalAspect}) bleibt wie bei den anderen Signalen eine
 * BlockState-Property, damit Vanilla die Anzeige automatisch zum Client
 * synchronisiert.
 */
public class KsMultiSectionSignalBlock extends BaseEntityBlock {

    public static final MapCodec<KsMultiSectionSignalBlock> CODEC = simpleCodec(KsMultiSectionSignalBlock::new);
    public static final EnumProperty<CombinedSignalAspect> ASPECT = EnumProperty.create("aspect", CombinedSignalAspect.class);

    private static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);

    public KsMultiSectionSignalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ASPECT, CombinedSignalAspect.HALT));
    }

    @Override
    protected MapCodec<KsMultiSectionSignalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ASPECT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Wird per BlockEntityRenderer mit freier Rotation gezeichnet, nicht über das statische Blockmodell.
        return RenderShape.INVISIBLE;
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

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof KsMultiSectionSignalBlockEntity blockEntity)) {
            return;
        }
        int steps = StationDecorConfig.SIGNAL_ROTATION_STEPS.get();
        float yaw = placer != null ? placer.getYRot() : 0f;
        blockEntity.setRotation(RotationUtil.snapToIndex(yaw, steps), steps);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof SignalBinderItem) {
            return SignalBinderItem.handleClick(level, pos, player);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
