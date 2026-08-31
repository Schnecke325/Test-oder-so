package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.StationDecorMod;
import com.stationdecor.block.seat.SeatBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Zeichnet den Sitz-Block mit seiner frei konfigurierbaren Rotation.
 */
public class SeatBlockEntityRenderer implements BlockEntityRenderer<SeatBlockEntity> {

    public static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, "block/seat_render");

    public SeatBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SeatBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RotatedObjRenderHelper.render(MODEL, blockEntity.getRotationDegrees(), poseStack, bufferSource, packedLight, packedOverlay);
    }
}
