package com.stationdecor.block.signal;

import com.mojang.serialization.MapCodec;
import com.stationdecor.block.rotation.RotationUtil;
import com.stationdecor.config.StationDecorConfig;
import com.stationdecor.item.SignalBinderItem;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
 * zum Client automatisch übernimmt. Die Rotation dagegen ist frei (nicht auf
 * 90°-Schritte beschränkt) und lebt daher auf der BlockEntity
 * ({@link KsMainSignalBlockEntity}, siehe {@code AbstractRotatableBlockEntity})
 * - Mast/Signalkopf werden per BlockEntityRenderer gezeichnet, siehe
 * {@code com.stationdecor.client.render.RotatableSignalRenderer}.
 * Die BlockEntity scannt außerdem periodisch bis zu 10 Blöcke unter sich nach
 * einem Create-Gleissignal und übernimmt dessen Zustand automatisch.
 * Rechtsklick schaltet weiterhin manuell weiter (überschrieben vom nächsten
 * Scan, falls dabei ein Gleissignal gefunden wird); ein Create Display Link
 * kann den Begriff ebenfalls setzen, siehe
 * {@code com.stationdecor.compat.create.KsMainSignalDisplayTarget}.
 */
public class KsMainSignalBlock extends BaseEntityBlock {

    public static final MapCodec<KsMainSignalBlock> CODEC = simpleCodec(KsMainSignalBlock::new);
    public static final EnumProperty<MainSignalAspect> ASPECT = EnumProperty.create("aspect", MainSignalAspect.class);

    private static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);

    public KsMainSignalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ASPECT, MainSignalAspect.HP0));
    }

    @Override
    protected MapCodec<KsMainSignalBlock> codec() {
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
        return new KsMainSignalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.KS_MAIN_SIGNAL.get(),
                KsMainSignalBlockEntity::serverTick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof KsMainSignalBlockEntity blockEntity)) {
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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ASPECT, state.getValue(ASPECT).next()), 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
