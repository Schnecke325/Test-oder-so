package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

public final class RotatedObjRenderHelper {

    private RotatedObjRenderHelper() {
    }

    public static void render(ModelResourceLocation modelLocation, float rotationDegrees, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, 0f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    /**
     * Wie oben, aber mit {@link RenderType#translucent()} statt {@link RenderType#cutout()} -
     * cutout kann Alpha nur binär (verwerfen oder voll deckend), echte Halbtransparenz (z.B. eine
     * angeschaltete Signallampe, die man leicht durchscheinen sehen soll) braucht translucent.
     */
    public static void render(ModelResourceLocation modelLocation, float rotationDegrees, RenderType renderType,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, 0f, 0f, 0f, 0f, 1f, renderType,
                poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static void render(ModelResourceLocation modelLocation, float rotationDegrees, float forwardOffset,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, 0f, 0f, forwardOffset, 0f, 1f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static void render(ModelResourceLocation modelLocation, float rotationDegrees,
                               float offsetX, float offsetY, float offsetZ,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, offsetX, offsetY, offsetZ, 0f, 1f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static void render(ModelResourceLocation modelLocation, float rotationDegrees,
                               float offsetX, float offsetY, float offsetZ,
                               float localTwistDegrees, float lengthScale,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, offsetX, offsetY, offsetZ, localTwistDegrees, lengthScale,
                RenderType.cutout(), poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static void render(ModelResourceLocation modelLocation, float rotationDegrees,
                               float offsetX, float offsetY, float offsetZ,
                               float localTwistDegrees, float lengthScale, RenderType renderType,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationDegrees));
        poseStack.translate(offsetX, offsetY, offsetZ);
        poseStack.translate(-0.5, 0, -0.5);
        if (localTwistDegrees != 0f) {
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(localTwistDegrees));
            poseStack.translate(-0.5, 0, -0.5);
        }
        if (lengthScale != 1f) {
            poseStack.translate(0, 0, 0.5);
            poseStack.scale(1f, 1f, lengthScale);
            poseStack.translate(0, 0, -0.5);
        }

        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), buffer, null, model, 1f, 1f, 1f, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
