package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

/**
 * Generischer BlockEntityRenderer für alle drei Ks-Signalblöcke: zeichnet
 * nur die zusätzliche, immer volle helle Lampenfläche (siehe
 * {@link SignalLampRenderHelper}) an der per {@code FACING} gedrehten
 * Position - das eigentliche Mast-/Signalkopf-Modell wird weiterhin normal
 * über das vanilla Blockmodell gezeichnet (RenderShape.MODEL bleibt aktiv).
 */
public class SignalAspectLampRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    private final Function<BlockState, ResourceLocation> textureResolver;

    public SignalAspectLampRenderer(Function<BlockState, ResourceLocation> textureResolver) {
        this.textureResolver = textureResolver;
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                        int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        ResourceLocation texture = textureResolver.apply(state);
        if (texture == null) {
            return;
        }

        Direction facing = state.hasProperty(HorizontalDirectionalBlock.FACING)
                ? state.getValue(HorizontalDirectionalBlock.FACING)
                : Direction.NORTH;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotationDegrees(facing)));
        poseStack.translate(-0.5, 0, -0.5);
        SignalLampRenderHelper.renderLamp(poseStack, bufferSource, texture);
        poseStack.popPose();
    }

    private static float yRotationDegrees(Direction facing) {
        return switch (facing) {
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f;
        };
    }
}
