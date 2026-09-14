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
        render(modelLocation, rotationDegrees, 0f, 0f, forwardOffset, 0f, 1f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    /**
     * Wie oben, aber mit einem vollen lokalen Versatz (x/y/z, in Blöcken), der nach der Rotation
     * angewendet wird. Genutzt z.B. für Modelle, deren eigener Ursprung nicht am Blockboden liegt
     * (z.B. der Fahrkartenautomat, dessen Modell-Y bei -1 statt 0 beginnt).
     */
    public static void render(ModelResourceLocation modelLocation, float rotationDegrees,
                               float offsetX, float offsetY, float offsetZ,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        render(modelLocation, rotationDegrees, offsetX, offsetY, offsetZ, 0f, 1f, poseStack, bufferSource, packedLight, packedOverlay);
    }

    /**
     * Wie oben, mit 2 zusätzlichen, rein lokalen Anpassungen des Modells selbst - unabhängig von
     * {@code rotationDegrees}/{@code offset} (die weiterhin exakt der Blickrichtung entsprechen,
     * z.B. für den Nah/Mitte/Fern-Versatz und die Vorschau-Outline der Bodenmarkierung):
     * <ul>
     *   <li>{@code localTwistDegrees}: dreht NUR das Modell selbst um seine eigene Mitte
     *   (0.5, *, 0.5) - genutzt von der Bodenmarkierung, deren geliefertes Modell/Textur lokal
     *   entlang Z ausgerichtet ist (Länge in Blickrichtung), was sich beim Platzieren unpraktisch
     *   anfühlte. Wichtig: Textur UND Geometrie der Bodenmarkierung gehören fest zusammen (die
     *   Textur ist nur in einem schmalen Band entlang X undurchsichtig, passend zur schmalen
     *   X-Spanne des Modells) - eine Drehung direkt im Modell (X/Z vertauschen) hätte das
     *   Zusammenspiel kaputt gemacht, siehe die 16 {@code platform_border_narrow_<farbe>.json}.
     *   Daher rein visuell hier im Renderer gelöst.</li>
     *   <li>{@code lengthScale}: streckt das Modell entlang seiner eigenen (lokalen, unrotierten)
     *   Z-Achse (der Längsachse vor jeder Drehung), symmetrisch um seine Mitte. Bei einer
     *   diagonalen Rotation (z.B. 45°) ist die Diagonale eines Blocks um den Faktor
     *   1/cos(Winkel zur nächsten Achse) länger als dessen Kante (bei 45° z.B. √2 ≈ 1,41), sonst
     *   würde die Markierung nicht mehr bis zum gegenüberliegenden Blockrand reichen.</li>
     * </ul>
     * Reihenfolge: erst strecken (im ursprünglichen Modell-Koordinatensystem, in dem Z die
     * Längsachse ist), dann drehen - damit {@code lengthScale} unabhängig von
     * {@code localTwistDegrees} immer entlang der tatsächlichen Modell-Länge wirkt.
     */
    public static void render(ModelResourceLocation modelLocation, float rotationDegrees,
                               float offsetX, float offsetY, float offsetZ,
                               float localTwistDegrees, float lengthScale,
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
        if (localTwistDegrees != 0f) {
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(localTwistDegrees));
            poseStack.translate(-0.5, 0, -0.5);
        }
        if (lengthScale != 1f) {
            // Um die lokale Mitte (Z=0.5) strecken statt um den Modellursprung (Z=0),
            // sonst würde die Markierung nur nach einer Seite wachsen statt symmetrisch.
            poseStack.translate(0, 0, 0.5);
            poseStack.scale(1f, 1f, lengthScale);
            poseStack.translate(0, 0, -0.5);
        }

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), buffer, null, model, 1f, 1f, 1f, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
