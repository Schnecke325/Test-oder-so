package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.marking.FloorMarkingBlockEntity;
import com.stationdecor.block.rotation.RotationUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * Zeichnet die Bodenmarkierung mit ihrer frei konfigurierbaren Rotation, dem
 * beim Platzieren gewählten Nah/Mitte/Fern-Versatz entlang der eigenen
 * (gedrehten) Vorwärtsachse, dem per Rechtsklick mit Farbstoff eingestellten
 * Modell/Farbton (siehe {@link FloorMarkingModels}) und einer winkelabhängigen
 * Streckung (siehe {@link RotationUtil#diagonalStretch}), damit die Markierung
 * auch diagonal (z.B. 45°) noch bis zum gegenüberliegenden Blockrand reicht.
 */
public class FloorMarkingBlockEntityRenderer implements BlockEntityRenderer<FloorMarkingBlockEntity> {

    public FloorMarkingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FloorMarkingBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float rotationDegrees = blockEntity.getRotationDegrees();
        RotatedObjRenderHelper.render(FloorMarkingModels.forColor(blockEntity.getColor()), rotationDegrees,
                0f, 0f, blockEntity.getOffsetDistance(), RotationUtil.diagonalStretch(rotationDegrees),
                poseStack, bufferSource, packedLight, packedOverlay);
    }
}
