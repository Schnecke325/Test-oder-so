package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.obj.ObjDisplayBlockEntity;
import com.stationdecor.block.obj.TicketMachineStyle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;

/**
 * Zeichnet den OBJ-Anzeigeblock (Fahrkartenautomat), frei rotierbar. Das
 * jeweilige OBJ-Modell (eins pro {@link TicketMachineStyle}, siehe
 * {@link TicketMachineModels}) wird als "additional model" registriert,
 * siehe {@link com.stationdecor.client.ClientSetup}.
 */
public class ObjDisplayBlockEntityRenderer implements BlockEntityRenderer<ObjDisplayBlockEntity> {

    /**
     * Die aktuelle Modellversion (Blockbench 5.1.6) hat ihren Boden bei Y=0
     * (Bounding Box Y: 0..3, X: -1..1, Z: -0.56..0.5) - anders als die
     * vorherige Version (Y: -1..2), die einen Versatz von +1 brauchte.
     */
    private static final float MODEL_Y_OFFSET = 0f;

    /**
     * {@link RotatedObjRenderHelper#render} setzt den Modell-Nullpunkt
     * standardmäßig auf die Block-ECKE (0,0), passend für gewöhnliche
     * Modelle mit lokalem X-Bereich 0..1. Dieses Modell ist aber 2 Blöcke
     * breit (lokal X: -1..1, Mitte also bei lokal X=0) - ohne diesen Versatz
     * landet die Blockmitte an der Block-Ecke statt an der Block-Mitte, und
     * der platzierte (anklickbare) Block sitzt sichtbar am Rand des Modells
     * statt mittig darin.
     */
    private static final float MODEL_X_OFFSET = 0.5f;

    public ObjDisplayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ObjDisplayBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelResourceLocation model = modelFor(blockEntity.getStyle());
        RotatedObjRenderHelper.render(model, blockEntity.getRotationDegrees(), MODEL_X_OFFSET, MODEL_Y_OFFSET, 0f,
                poseStack, bufferSource, packedLight, packedOverlay);
    }

    private static ModelResourceLocation modelFor(TicketMachineStyle style) {
        return switch (style) {
            case DB -> TicketMachineModels.DB;
        };
    }
}
