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
 * Zeichnet den OBJ-Anzeigeblock mit seiner frei konfigurierbaren Rotation.
 * Das eigentliche OBJ-Modell wird als "additional model" registriert, siehe
 * {@link com.stationdecor.client.ClientSetup}.
 */
public class ObjDisplayBlockEntityRenderer implements BlockEntityRenderer<ObjDisplayBlockEntity> {

    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, "block/obj_display_render"));

    public ObjDisplayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ObjDisplayBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RotatedObjRenderHelper.render(MODEL, blockEntity.getRotationDegrees(), poseStack, bufferSource, packedLight, packedOverlay);
    }
}
