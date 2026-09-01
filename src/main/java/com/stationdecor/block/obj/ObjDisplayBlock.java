package com.stationdecor.block.obj;

import com.mojang.serialization.MapCodec;
import com.stationdecor.block.rotation.RotationUtil;
import com.stationdecor.config.StationDecorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Dekorativer Block, der über eine {@link BlockEntityRenderer} als OBJ-Modell
 * dargestellt wird - frei rotierbar wie Sitzblock/Bodenmarkierung/Signale
 * (siehe {@code AbstractRotatableBlockEntity}). Ein Rechtsklick öffnet ein
 * einfaches Platzhalter-GUI.
 */
public class ObjDisplayBlock extends BaseEntityBlock {

    public static final MapCodec<ObjDisplayBlock> CODEC = simpleCodec(ObjDisplayBlock::new);

    /**
     * Feste, rotationsunabhängige Kollisionsbox, nahezu blockfüllend. Das
     * gelieferte Fahrkartenautomat-Modell ist real 2 Blöcke breit / 3 Blöcke
     * hoch (siehe {@code ObjDisplayBlockEntityRenderer}) - die Kollision
     * deckt bewusst nur den Platzierungsblock ab, der optisch überstehende
     * Teil ist rein visuell begehbar. Eine exakt der gewählten Rotation
     * folgende Box wäre bei 22,5°-Schritten zudem nicht mehr achsenparallel
     * und ist bewusst nicht implementiert.
     */
    private static final VoxelShape SHAPE = box(-4, 0, -4, 20, 48, 20);

    public ObjDisplayBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ObjDisplayBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Das eigentliche Modell wird per BlockEntityRenderer mit freier
        // Rotation gezeichnet, nicht über das statische Blockmodell.
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ObjDisplayBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof ObjDisplayBlockEntity blockEntity)) {
            return;
        }
        int steps = StationDecorConfig.OBJ_BLOCK_ROTATION_STEPS.get();
        float yaw = placer != null ? placer.getYRot() : 0f;
        blockEntity.setRotation(RotationUtil.snapToIndex(yaw, steps), steps);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            MenuProvider menuProvider = state.getMenuProvider(level, pos);
            if (menuProvider != null) {
                serverPlayer.openMenu(menuProvider);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof MenuProvider menuProvider) {
            return menuProvider;
        }
        return null;
    }
}
