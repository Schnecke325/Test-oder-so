package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.stationdecor.StationDecorMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
    private static final ResourceLocation FLARE_SPRITE =
            ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, "block/signal_flare");
    private static final float FLARE_HALF_SIZE = 0.22f;
    private static final float LAMP_CLUSTER_DISTANCE = 0.3f;
    private static final float REFERENCE_LAMP_EXTENT = 0.18f;
    private static final float MIN_FLARE_SCALE = 0.35f;

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

        List<LampCluster> clusters = new ArrayList<>();

        for (BakedQuad quad : collectAllQuads(model)) {
            LampColor lampColor = lampColorOf(quad.getSprite());
            if (lampColor == null) {
                buffer.putBulkData(pose, quad, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
                continue;
            }
            boolean on = litColors.contains(lampColor);
            putLampQuad(buffer, pose, quad, on ? LAMP_ON_LIGHT : packedLight, packedOverlay, on ? 0f : LAMP_OFF_U_SHIFT);
            if (on) {
                addQuadToClusters(clusters, lampColor, quad);
            }
        }

        drawFlares(clusters, rotationDegrees, poseStack, bufferSource, packedOverlay);
        poseStack.popPose();
    }

    /**
     * Mehrere Lampen können dieselbe Glow-Textur (also dieselbe {@link LampColor}) teilen, obwohl sie
     * physisch getrennte Lampen sind (z.B. Haupt- und Zs-Lampe beim Vorsignalwiederholer). Statt pro
     * Farbe einen einzigen Flare zu mitteln, werden Quads anhand ihrer lokalen Position geclustert:
     * nahe beieinanderliegende Quads (eine Lampen-Cube) verschmelzen zu einem Cluster, weit entfernte
     * Quads der gleichen Farbe bilden einen eigenen Cluster mit eigenem Flare.
     */
    private static void addQuadToClusters(List<LampCluster> clusters, LampColor lampColor, BakedQuad quad) {
        int[] data = quad.getVertices();
        int vertexCount = data.length / 8;
        Vector3f quadSum = new Vector3f();
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < vertexCount; i++) {
            float x = Float.intBitsToFloat(data[i * 8]);
            float y = Float.intBitsToFloat(data[i * 8 + 1]);
            float z = Float.intBitsToFloat(data[i * 8 + 2]);
            quadSum.add(x, y, z);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);
        }
        Vector3f quadCenter = new Vector3f(quadSum).div(vertexCount);

        for (LampCluster cluster : clusters) {
            if (cluster.color == lampColor && cluster.centroid().distance(quadCenter) < LAMP_CLUSTER_DISTANCE) {
                cluster.merge(quadSum, vertexCount, minX, maxX, minY, maxY, minZ, maxZ);
                return;
            }
        }
        LampCluster cluster = new LampCluster(lampColor);
        cluster.merge(quadSum, vertexCount, minX, maxX, minY, maxY, minZ, maxZ);
        clusters.add(cluster);
    }

    /**
     * Zeichnet die Flares innerhalb derselben Pose, in der auch die Lampen-Quads liegen (vor dem
     * äußeren popPose) - sonst würde die Block-Position doppelt verrechnet, weil sie dort schon in der
     * Pose steckt. Die Rotation des Signals (-rotationDegrees) wird direkt danach durch +rotationDegrees
     * wieder aufgehoben, damit nur noch die reine Position übrig bleibt, auf die die Kamera-Rotation
     * für das Billboard angewendet wird.
     */
    private static void drawFlares(List<LampCluster> clusters, float rotationDegrees,
                                    PoseStack poseStack, MultiBufferSource bufferSource, int packedOverlay) {
        if (clusters.isEmpty()) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        TextureAtlasSprite flareSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(FLARE_SPRITE);
        VertexConsumer flareBuffer = bufferSource.getBuffer(RenderType.translucent());

        for (LampCluster cluster : clusters) {
            Vector3f localCenter = cluster.centroid();
            float scale = Math.max(MIN_FLARE_SCALE, Math.min(1f, cluster.extent() / REFERENCE_LAMP_EXTENT));

            poseStack.pushPose();
            poseStack.translate(localCenter.x(), localCenter.y(), localCenter.z());
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees));
            poseStack.mulPose(camera.rotation());
            drawFlareQuad(flareBuffer, poseStack.last(), cluster.color, flareSprite, packedOverlay, FLARE_HALF_SIZE * scale);
            poseStack.popPose();
        }
    }

    private static final class LampCluster {
        final LampColor color;
        final Vector3f centroidSum = new Vector3f();
        int vertexCount;
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

        LampCluster(LampColor color) {
            this.color = color;
        }

        void merge(Vector3f sum, int count, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
            centroidSum.add(sum);
            vertexCount += count;
            this.minX = Math.min(this.minX, minX);
            this.maxX = Math.max(this.maxX, maxX);
            this.minY = Math.min(this.minY, minY);
            this.maxY = Math.max(this.maxY, maxY);
            this.minZ = Math.min(this.minZ, minZ);
            this.maxZ = Math.max(this.maxZ, maxZ);
        }

        Vector3f centroid() {
            return new Vector3f(centroidSum).div(vertexCount);
        }

        float extent() {
            return Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
        }
    }

    private static void drawFlareQuad(VertexConsumer buffer, PoseStack.Pose pose, LampColor lampColor,
                                        TextureAtlasSprite sprite, int packedOverlay, float halfSize) {
        int color = FastColor.ARGB32.color(255, lampColor.flareRed(), lampColor.flareGreen(), lampColor.flareBlue());
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float s = halfSize;

        float[][] corners = {
                {-s, -s, u0, v1},
                {s, -s, u1, v1},
                {s, s, u1, v0},
                {-s, s, u0, v0}
        };
        for (int winding = 0; winding < 2; winding++) {
            for (int i = 0; i < 4; i++) {
                float[] corner = corners[winding == 0 ? i : 3 - i];
                buffer.addVertex(pose, corner[0], corner[1], 0f)
                        .setColor(color)
                        .setUv(corner[2], corner[3])
                        .setOverlay(packedOverlay)
                        .setLight(LAMP_ON_LIGHT)
                        .setNormal(pose, 0f, 0f, 1f);
            }
        }
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
