package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.marking.FloorMarkingBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * Zeichnet die Bodenmarkierung mit ihrer frei konfigurierbaren Rotation, dem
 * beim Platzieren gewählten Nah/Mitte/Fern-Versatz entlang der eigenen
 * (gedrehten) Vorwärtsachse, und dem per Rechtsklick mit Farbstoff
 * eingestellten Modell/Farbton (siehe {@link FloorMarkingModels}).
 */
public class FloorMarkingBlockEntityRenderer implements BlockEntityRenderer<FloorMarkingBlockEntity> {

    public FloorMarkingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FloorMarkingBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RotatedObjRenderHelper.render(FloorMarkingModels.forColor(blockEntity.getColor()), blockEntity.getRotationDegrees(),
                blockEntity.getOffsetDistance(), poseStack, bufferSource, packedLight, packedOverlay);
    }
}
