package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.marking.FloorMarkingBlockEntity;
import com.stationdecor.block.rotation.RotationUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FloorMarkingBlockEntityRenderer implements BlockEntityRenderer<FloorMarkingBlockEntity> {

    private static final float PRACTICAL_TWIST_DEGREES = 90f;

    public FloorMarkingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FloorMarkingBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float rotationDegrees = blockEntity.getRotationDegrees();
        RotatedObjRenderHelper.render(FloorMarkingModels.forColor(blockEntity.getColor()), rotationDegrees,
                0f, 0f, blockEntity.getOffsetDistance(),
                PRACTICAL_TWIST_DEGREES, RotationUtil.diagonalStretch(rotationDegrees),
                poseStack, bufferSource, packedLight, packedOverlay);
    }
}
