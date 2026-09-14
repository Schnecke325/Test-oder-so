package com.stationdecor.block.marking;

import com.mojang.serialization.MapCodec;
import com.stationdecor.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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
 * <p>
 * Jede Farbe (16 Varianten, siehe {@code client.render.FloorMarkingModels})
 * ist ein eigener, separat im Creative-Tab wählbarer Block/Item - so wie bei
 * den Fahrkartenautomat-Varianten (siehe {@code block.obj.ObjDisplayBlock}).
 * Ein bereits platzierter Block lässt sich trotzdem per Rechtsklick mit
 * einem Farbstoff umfärben: dabei wird der Block an dieser Position durch
 * die entsprechende Farbvariante ersetzt, Rotation und Nah/Mitte/Fern-Versatz
 * werden dabei von der alten auf die neue BlockEntity übernommen.
 */
public class FloorMarkingBlock extends BaseEntityBlock {

    /** Dünne, rotationsunabhängige Kollisionsbox knapp über dem Boden. */
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

    private final DyeColor color;
    private final MapCodec<FloorMarkingBlock> codec;

    public FloorMarkingBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        this.codec = simpleCodec(props -> new FloorMarkingBlock(props, color));
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    protected MapCodec<FloorMarkingBlock> codec() {
        return codec;
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

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof DyeItem dyeItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        DyeColor newColor = dyeItem.getDyeColor();
        if (newColor == color) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            int rotationIndex = 0;
            int rotationSteps = 8;
            int offsetIndex = 0;
            if (level.getBlockEntity(pos) instanceof FloorMarkingBlockEntity oldEntity) {
                rotationIndex = oldEntity.getRotationIndex();
                rotationSteps = oldEntity.getRotationSteps();
                offsetIndex = oldEntity.getOffsetIndex();
            }

            Block newBlock = ModBlocks.FLOOR_MARKING.get(newColor).get();
            level.setBlock(pos, newBlock.defaultBlockState(), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof FloorMarkingBlockEntity newEntity) {
                newEntity.setPlacement(rotationIndex, rotationSteps, offsetIndex);
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
