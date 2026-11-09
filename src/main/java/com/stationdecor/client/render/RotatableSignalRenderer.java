package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.block.signal.KsDistantSignalBlock;
import com.stationdecor.block.signal.KsMainSignalBlock;
import com.stationdecor.block.signal.KsMultiSectionSignalBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class RotatableSignalRenderer<T extends AbstractRotatableBlockEntity> implements BlockEntityRenderer<T> {

    private final Function<BlockState, ModelResourceLocation> modelResolver;

    public RotatableSignalRenderer(Function<BlockState, ModelResourceLocation> modelResolver) {
        this.modelResolver = modelResolver;
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                        int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        float rotationDegrees = blockEntity.getRotationDegrees();

        ModelResourceLocation model = modelResolver.apply(state);
        if (model != null) {
            RotatedObjRenderHelper.renderSignal(model, rotationDegrees, litColorsOf(state),
                    poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private static Set<LampColor> litColorsOf(BlockState state) {
        if (state.getBlock() instanceof KsMainSignalBlock) {
            return switch (state.getValue(KsMainSignalBlock.ASPECT)) {
                case HP0 -> EnumSet.of(LampColor.RED);
                case HP1 -> EnumSet.of(LampColor.GREEN);
                case HP2 -> EnumSet.of(LampColor.RED, LampColor.GREEN);
            };
        }
        if (state.getBlock() instanceof KsDistantSignalBlock) {
            return switch (state.getValue(KsDistantSignalBlock.ASPECT)) {
                case VR0 -> EnumSet.of(LampColor.YELLOW);
                case VR1 -> EnumSet.of(LampColor.GREEN);
                case VR2 -> EnumSet.of(LampColor.YELLOW, LampColor.GREEN);
            };
        }
        if (state.getBlock() instanceof KsMultiSectionSignalBlock) {
            return switch (state.getValue(KsMultiSectionSignalBlock.ASPECT)) {
                case HALT -> EnumSet.of(LampColor.RED);
                case HALT_ERWARTEN -> EnumSet.of(LampColor.YELLOW);
                case FAHRT -> EnumSet.of(LampColor.GREEN);
            };
        }
        return EnumSet.noneOf(LampColor.class);
    }
}
