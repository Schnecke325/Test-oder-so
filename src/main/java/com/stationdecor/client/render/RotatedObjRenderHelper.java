package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.VertexConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Zeichnet ein per {@code neoforge:obj}-Loader geladenes Standalone-Modell
 * (siehe {@code assets/station_decor/models/block/*_render.json}) an der
 * BlockEntity-Position, gedreht um einen beliebigen Winkel. Wird sowohl vom
 * OBJ-Anzeigeblock als auch vom Sitz-Block genutzt, damit die freie
 * (nicht auf 90°-Schritte beschränkte) Rotation für beide gleich funktioniert.
 */
public final class RotatedObjRenderHelper {

    private RotatedObjRenderHelper() {
    }

    public static void render(ResourceLocation modelLocation, float rotationDegrees, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees));
        poseStack.translate(-0.5, 0, -0.5);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), buffer, null, model, 1f, 1f, 1f, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
