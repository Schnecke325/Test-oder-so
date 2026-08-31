package com.stationdecor.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stationdecor.block.seat.SeatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * {@link SeatEntity} ist rein technisch und unsichtbar - dieser Renderer
 * zeichnet absichtlich nichts. Ein Renderer muss dennoch registriert werden,
 * da NeoForge sonst beim ersten Auftauchen der Entity abstürzt.
 */
public class SeatEntityRenderer extends EntityRenderer<SeatEntity> {

    private static final ResourceLocation NO_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/none.png");

    public SeatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SeatEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight) {
        // Absichtlich leer - die SeatEntity ist unsichtbar.
    }

    @Override
    public ResourceLocation getTextureLocation(SeatEntity entity) {
        return NO_TEXTURE;
    }
}
