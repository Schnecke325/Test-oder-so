package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class RotatedObjRenderHelper {

    private static final float LAMP_OFF_U_SHIFT = -7f / 16f;
    private static final int LAMP_ON_LIGHT = LightTexture.pack(15, 15);

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

    /**
     * Wie {@link #render(ModelResourceLocation, float, RenderType, PoseStack, MultiBufferSource, int, int)},
     * rendert aber die Signallampen-Quads (erkannt an ihrem Sprite: signal_glow/_2/_3) separat: Lampen in
     * {@code litColors} werden mit fixem Full-Bright-Licht gezeichnet, alle anderen Lampen werden per
     * UV-Verschiebung auf ihren "aus"-Ausschnitt in derselben Textur umgeschaltet. Nicht-Lampen-Quads
     * (Metall, Signalschild) werden unverändert wie gewohnt gerendert.
     */
    public static void renderSignal(ModelResourceLocation modelLocation, float rotationDegrees, Set<LampColor> litColors,
                                     PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationDegrees));
        poseStack.translate(-0.5, 0, -0.5);

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        for (BakedQuad quad : collectAllQuads(model)) {
            LampColor lampColor = lampColorOf(quad.getSprite());
            if (lampColor == null) {
                buffer.putBulkData(pose, quad, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
                continue;
            }
            boolean on = litColors.contains(lampColor);
            putLampQuad(buffer, pose, quad, on ? LAMP_ON_LIGHT : packedLight, packedOverlay, on ? 0f : LAMP_OFF_U_SHIFT);
        }

        poseStack.popPose();
    }

    private static List<BakedQuad> collectAllQuads(BakedModel model) {
        RandomSource random = RandomSource.create(42L);
        List<BakedQuad> quads = new ArrayList<>(model.getQuads(null, null, random));
        for (Direction direction : Direction.values()) {
            quads.addAll(model.getQuads(null, direction, random));
        }
        return quads;
    }

    private static LampColor lampColorOf(TextureAtlasSprite sprite) {
        ResourceLocation name = sprite.contents().name();
        for (LampColor color : LampColor.values()) {
            if (color.sprite().equals(name)) {
                return color;
            }
        }
        return null;
    }

    private static void putLampQuad(VertexConsumer buffer, PoseStack.Pose pose, BakedQuad quad,
                                     int packedLight, int packedOverlay, float uShift) {
        int[] data = quad.getVertices();
        Vec3i normal = quad.getDirection().getNormal();
        Vector3f transformedNormal = pose.transformNormal((float) normal.getX(), (float) normal.getY(), (float) normal.getZ(), new Vector3f());
        Matrix4f matrix = pose.pose();
        int vertexCount = data.length / 8;

        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = memoryStack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            for (int i = 0; i < vertexCount; i++) {
                intBuffer.clear();
                intBuffer.put(data, i * 8, 8);
                float x = byteBuffer.getFloat(0);
                float y = byteBuffer.getFloat(4);
                float z = byteBuffer.getFloat(8);
                int red = byteBuffer.get(12) & 255;
                int green = byteBuffer.get(13) & 255;
                int blue = byteBuffer.get(14) & 255;
                int alpha = byteBuffer.get(15) & 255;
                float u = byteBuffer.getFloat(16) + uShift;
                float v = byteBuffer.getFloat(20);

                Vector3f transformedPos = matrix.transformPosition(x, y, z, new Vector3f());
                buffer.addVertex(transformedPos.x(), transformedPos.y(), transformedPos.z(),
                        FastColor.ARGB32.color(alpha, red, green, blue), u, v, packedOverlay, packedLight,
                        transformedNormal.x(), transformedNormal.y(), transformedNormal.z());
            }
        }
    }
}
