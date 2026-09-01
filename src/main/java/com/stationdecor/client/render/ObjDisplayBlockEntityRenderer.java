package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.StationDecorMod;
import com.stationdecor.block.obj.ObjDisplayBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Zeichnet den OBJ-Anzeigeblock (Fahrkartenautomat). Aktuell mit fester
 * Rotation (0°) - wird wieder frei drehbar, sobald das Modell-Laden
 * zuverlässig funktioniert (siehe README). Das eigentliche OBJ-Modell wird
 * als "additional model" registriert, siehe {@link com.stationdecor.client.ClientSetup}.
 */
public class ObjDisplayBlockEntityRenderer implements BlockEntityRenderer<ObjDisplayBlockEntity> {

    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, "block/obj_display_render"));

    /**
     * Das gelieferte Modell (db_fahrkartenautomat.obj) beginnt bei Y=-1 statt bei Y=0
     * (Bounding Box Y: -1..2, X: -1..1, Z: -0.56..0.5) - dieser Versatz schiebt den
     * Modellboden auf die Blockunterkante. X/Z sind bereits mittig zentriert.
     */
    private static final float MODEL_Y_OFFSET = 1f;

    public ObjDisplayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ObjDisplayBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RotatedObjRenderHelper.render(MODEL, 0f, 0f, MODEL_Y_OFFSET, 0f,
                poseStack, bufferSource, packedLight, packedOverlay);
    }
}
