package com.stationdecor.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stationdecor.StationDecorMod;
import com.stationdecor.block.marking.FloorMarkingBlockItem;
import com.stationdecor.block.rotation.RotationUtil;
import com.stationdecor.config.StationDecorConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

/**
 * Zeichnet beim Anvisieren eines Blocks mit der Bodenmarkierung in der Hand
 * eine Vorschau-Outline, die die anvisierte Fläche in 3 Zonen entlang der
 * (eingerasteten) Blickrichtung unterteilt - passend zu {@link FloorMarkingBlockItem},
 * das anhand der gleichen 3 Zonen den Nah/Mitte/Fern-Versatz beim Platzieren setzt.
 * Bei einer 45°-Rotationsschrittzahl liegen die Trennlinien entsprechend diagonal.
 * <p>
 * Die vanilla Auswahl-Box wird dabei nicht entfernt (das zugehörige Event ist in
 * 1.21.1 nicht abbrechbar) - die beiden Linien werden zusätzlich darüber gezeichnet.
 */
@EventBusSubscriber(modid = StationDecorMod.MOD_ID, value = Dist.CLIENT)
public final class PlacementOutlineHandler {

    private static final float LINE_R = 1f;
    private static final float LINE_G = 0.85f;
    private static final float LINE_B = 0f;
    private static final float LINE_A = 1f;
    /** Grenzen der 3 gleich großen Zonen entlang der Vorwärtsachse, siehe FloorMarkingBlockItem. */
    private static final double ZONE_BOUNDARY = 1.0 / 6.0;

    private PlacementOutlineHandler() {
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !isHoldingFloorMarking(player)) {
            return;
        }

        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();
        Direction face = target.getDirection();

        int steps = StationDecorConfig.FLOOR_MARKING_ROTATION_STEPS.get();
        float rotationDegrees = RotationUtil.indexToDegrees(RotationUtil.snapToIndex(player.getYRot(), steps), steps);
        Vec3 forward = RotationUtil.forwardVector(rotationDegrees);
        // Horizontale Senkrechte zu "forward", um die Trennlinien quer über die Fläche zu ziehen.
        Vec3 sideways = new Vec3(forward.z, 0, -forward.x);

        Vec3 faceCenter = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(face.getNormal()).scale(0.5));
        Vec3 camPos = event.getCamera().getPosition();

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource bufferSource = event.getMultiBufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        drawZoneDivider(consumer, poseStack, faceCenter, forward, sideways, -ZONE_BOUNDARY);
        drawZoneDivider(consumer, poseStack, faceCenter, forward, sideways, ZONE_BOUNDARY);

        poseStack.popPose();
    }

    private static boolean isHoldingFloorMarking(Player player) {
        return player.getMainHandItem().getItem() instanceof FloorMarkingBlockItem
                || player.getOffhandItem().getItem() instanceof FloorMarkingBlockItem;
    }

    private static void drawZoneDivider(VertexConsumer consumer, PoseStack poseStack, Vec3 faceCenter,
                                         Vec3 forward, Vec3 sideways, double forwardOffset) {
        Vec3 mid = faceCenter.add(forward.scale(forwardOffset));
        Vec3 from = mid.subtract(sideways.scale(0.5));
        Vec3 to = mid.add(sideways.scale(0.5));
        drawLine(consumer, poseStack, from, to);
    }

    private static void drawLine(VertexConsumer consumer, PoseStack poseStack, Vec3 from, Vec3 to) {
        Vec3 dir = to.subtract(from).normalize();
        PoseStack.Pose pose = poseStack.last();
        consumer.addVertex(pose, (float) from.x, (float) from.y, (float) from.z)
                .setColor(LINE_R, LINE_G, LINE_B, LINE_A)
                .setNormal(pose, (float) dir.x, (float) dir.y, (float) dir.z);
        consumer.addVertex(pose, (float) to.x, (float) to.y, (float) to.z)
                .setColor(LINE_R, LINE_G, LINE_B, LINE_A)
                .setNormal(pose, (float) dir.x, (float) dir.y, (float) dir.z);
    }
}
