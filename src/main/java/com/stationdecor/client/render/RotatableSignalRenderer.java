package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

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
            RotatedObjRenderHelper.render(model, rotationDegrees, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}
