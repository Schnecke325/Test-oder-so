package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.obj.ObjDisplayBlockEntity;
import com.stationdecor.block.obj.TicketMachineStyle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ObjDisplayBlockEntityRenderer implements BlockEntityRenderer<ObjDisplayBlockEntity> {

    private static final float MODEL_Y_OFFSET = 0f;

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
            case BVG -> TicketMachineModels.BVG;
            case BEWEGT -> TicketMachineModels.BEWEGT;
            case GOAHEAD -> TicketMachineModels.GOAHEAD;
            case RMV -> TicketMachineModels.RMV;
            case VVR -> TicketMachineModels.VVR;
        };
    }
}
