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
        render(modelLocation, rotationDegrees, 0f, 0f, forwardOffset, 1f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    /**
     * Wie oben, aber mit einem vollen lokalen Versatz (x/y/z, in Blöcken), der nach der Rotation
     * angewendet wird. Genutzt z.B. für Modelle, deren eigener Ursprung nicht am Blockboden liegt
     * (z.B. der Fahrkartenautomat, dessen Modell-Y bei -1 statt 0 beginnt).
     */
    public static void render(ModelResourceLocation modelLocation, float rotationDegrees,
                               float offsetX, float offsetY, float offsetZ,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, offsetX, offsetY, offsetZ, 1f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    /**
     * Wie oben, streckt das Modell zusätzlich um {@code lengthScale} entlang seiner eigenen
     * (lokalen, unrotierten) X-Achse, symmetrisch um seine Mitte - das ist bei der
     * Bodenmarkierung die Längsachse des Modells (das Modell liegt lokal quer zur
     * Blickrichtung, siehe die {@code platform_border_narrow_<farbe>.json}-Modelle).
     * Bei einer diagonalen Rotation (z.B. 45°) ist die Diagonale eines Blocks um den
     * Faktor 1/cos(Winkel zur nächsten Achse) länger als dessen Kante (bei 45° z.B.
     * √2 ≈ 1,41), sonst würde die Markierung nicht mehr bis zum gegenüberliegenden
     * Blockrand reichen.
     */
    public static void render(ModelResourceLocation modelLocation, float rotationDegrees,
                               float offsetX, float offsetY, float offsetZ, float lengthScale,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        // Axis.YP.rotationDegrees dreht mathematisch positiv um +Y (Süden -> Osten mit
        // steigendem Winkel) - das ist die GEGENRICHTUNG von Minecrafts Yaw-Konvention
        // (Süden -> Westen mit steigendem Yaw, siehe RotationUtil#forwardVector). Ohne
        // Vorzeichenumkehr würde das Modell an der Nord-Süd-Achse gespiegelt platziert
        // (z.B. Blickrichtung Nordwest -> Modell zeigt Nordost).
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationDegrees));
        poseStack.translate(offsetX, offsetY, offsetZ);
        poseStack.translate(-0.5, 0, -0.5);
        if (lengthScale != 1f) {
            // Um die lokale Mitte (X=0.5) strecken statt um den Modellursprung (X=0),
            // sonst würde die Markierung nur nach einer Seite wachsen statt symmetrisch.
            poseStack.translate(0.5, 0, 0);
            poseStack.scale(lengthScale, 1f, 1f);
            poseStack.translate(-0.5, 0, 0);
        }

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), buffer, null, model, 1f, 1f, 1f, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
