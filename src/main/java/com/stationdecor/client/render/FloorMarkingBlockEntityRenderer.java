package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.StationDecorMod;
import com.stationdecor.block.marking.FloorMarkingBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Zeichnet die Bodenmarkierung mit ihrer frei konfigurierbaren Rotation und
 * dem beim Platzieren gewählten Nah/Mitte/Fern-Versatz entlang der eigenen
 * (gedrehten) Vorwärtsachse.
 */
public class FloorMarkingBlockEntityRenderer implements BlockEntityRenderer<FloorMarkingBlockEntity> {

    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, "block/floor_marking_render"));

    public FloorMarkingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FloorMarkingBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RotatedObjRenderHelper.render(MODEL, blockEntity.getRotationDegrees(), blockEntity.getOffsetDistance(),
                poseStack, bufferSource, packedLight, packedOverlay);
    }
}
