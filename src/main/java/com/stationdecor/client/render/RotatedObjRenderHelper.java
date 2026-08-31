package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

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

    public static void render(ModelResourceLocation modelLocation, float rotationDegrees, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, 0f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    /**
     * Wie {@link #render(ModelResourceLocation, float, PoseStack, MultiBufferSource, int, int)},
     * verschiebt das Modell zusätzlich um {@code forwardOffset} Blöcke entlang seiner eigenen
     * (bereits gedrehten) Vorwärtsachse - genutzt von Block 3 für den Nah/Mitte/Fern-Versatz.
     */
    public static void render(ModelResourceLocation modelLocation, float rotationDegrees, float forwardOffset,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees));
        poseStack.translate(0, 0, forwardOffset);
        poseStack.translate(-0.5, 0, -0.5);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), buffer, null, model, 1f, 1f, 1f, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
