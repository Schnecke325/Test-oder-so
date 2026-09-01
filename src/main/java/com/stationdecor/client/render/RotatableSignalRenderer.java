package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

/**
 * Generischer BlockEntityRenderer für alle drei Ks-Signalblöcke. Zeichnet
 * zwei Dinge, beide um {@link AbstractRotatableBlockEntity#getRotationDegrees()}
 * gedreht:
 * <ol>
 *   <li>Das Mast-/Signalkopf-Modell des aktuellen Begriffs (per
 *       {@link RotatedObjRenderHelper}, das trotz des Namens ein beliebiges
 *       Baked Model dreht, nicht nur OBJ-Modelle) - ersetzt das (jetzt
 *       {@code RenderShape.INVISIBLE}) statische Blockmodell.</li>
 *   <li>Die zusätzliche, immer volle helle Lampenfläche (siehe
 *       {@link SignalLampRenderHelper}).</li>
 * </ol>
 */
public class RotatableSignalRenderer<T extends AbstractRotatableBlockEntity> implements BlockEntityRenderer<T> {

    private final Function<BlockState, ModelResourceLocation> modelResolver;
    private final Function<BlockState, ResourceLocation> lampTextureResolver;

    public RotatableSignalRenderer(Function<BlockState, ModelResourceLocation> modelResolver,
                                    Function<BlockState, ResourceLocation> lampTextureResolver) {
        this.modelResolver = modelResolver;
        this.lampTextureResolver = lampTextureResolver;
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                        int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        float rotationDegrees = blockEntity.getRotationDegrees();

        ModelResourceLocation model = modelResolver.apply(state);
        if (model != null) {
            RotatedObjRenderHelper.render(model, rotationDegrees, poseStack, bufferSource, packedLight, packedOverlay);
        }

        ResourceLocation lampTexture = lampTextureResolver.apply(state);
        if (lampTexture != null) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees));
            poseStack.translate(-0.5, 0, -0.5);
            SignalLampRenderHelper.renderLamp(poseStack, bufferSource, lampTexture);
            poseStack.popPose();
        }
    }
}
