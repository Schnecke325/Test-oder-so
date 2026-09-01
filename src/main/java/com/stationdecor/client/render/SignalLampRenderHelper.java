package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Zeichnet die Signallampen-Fläche (Nord-/Südseite des "head"-Elements aus
 * den Ks-Signal-Blockmodellen, siehe {@code models/block/ks_*_signal_*.json})
 * zusätzlich mit fester voller Helligkeit, unabhängig vom Umgebungslicht.
 * <p>
 * Grund: {@code lightLevel} auf Blockebene lässt den GESAMTEN Block
 * (inklusive Mast) gleich hell erscheinen, weil Vanilla-Block-Licht nicht auf
 * einzelne Flächen/Elemente beschränkt werden kann. Damit nur die Lampe
 * "leuchtet" und der Mast weiterhin normal vom Umgebungslicht abhängt, wird
 * hier stattdessen ein zusätzliches, immer voll ausgeleuchtetes Quad exakt
 * über die vorhandene Lampenfläche gezeichnet (siehe {@code head}-Element:
 * x 3..13, y 12..20, z 7..9 von 16). Ein winziger Versatz nach außen
 * verhindert Z-Fighting mit der darunterliegenden, normal beleuchteten
 * Modellfläche.
 */
public final class SignalLampRenderHelper {

    private static final int FULL_BRIGHT = LightTexture.pack(15, 15);
    private static final float EPSILON = 0.002f;

    private static final float X0 = 3f / 16f;
    private static final float X1 = 13f / 16f;
    private static final float Y0 = 12f / 16f;
    private static final float Y1 = 20f / 16f;
    private static final float Z_NORTH = 7f / 16f - EPSILON;
    private static final float Z_SOUTH = 9f / 16f + EPSILON;

    private SignalLampRenderHelper() {
    }

    public static void renderLamp(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(texture));
        PoseStack.Pose pose = poseStack.last();

        // Nordseite (Blickrichtung -Z), Normale zeigt nach Norden.
        quad(buffer, pose,
                X0, Y1, Z_NORTH, X1, Y1, Z_NORTH, X1, Y0, Z_NORTH, X0, Y0, Z_NORTH,
                0, 0, -1);
        // Südseite (Blickrichtung +Z), Normale zeigt nach Süden.
        quad(buffer, pose,
                X1, Y1, Z_SOUTH, X0, Y1, Z_SOUTH, X0, Y0, Z_SOUTH, X1, Y0, Z_SOUTH,
                0, 0, 1);
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose pose,
                              float x0, float y0, float z0,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float nx, float ny, float nz) {
        vertex(buffer, pose, x0, y0, z0, 0, 0, nx, ny, nz);
        vertex(buffer, pose, x1, y1, z1, 1, 0, nx, ny, nz);
        vertex(buffer, pose, x2, y2, z2, 1, 1, nx, ny, nz);
        vertex(buffer, pose, x3, y3, z3, 0, 1, nx, ny, nz);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z,
                                float u, float v, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal(pose, nx, ny, nz);
    }
}
