package com.stationdecor.block.marking;

import com.stationdecor.block.rotation.RotationUtil;
import com.stationdecor.config.StationDecorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Eigenes {@link BlockItem} für die Bodenmarkierung: setzt nach dem
 * Platzieren Rotation und Nah/Mitte/Fern-Versatz auf der BlockEntity, auf
 * Basis des exakten Klickpunkts. Ein normales {@code BlockItem} hat dafür
 * keinen Zugriff mehr, weil {@code setPlacedBy} den Klickpunkt nicht kennt.
 */
public class FloorMarkingBlockItem extends BlockItem {

    /**
     * Das Modell ist lokal entlang Z ausgerichtet (Länge in Blickrichtung) -
     * ohne Korrektur läge die Markierung beim Platzieren also parallel zur
     * Blickrichtung statt quer dazu, was sich unpraktisch anfühlt. Dieser
     * feste Versatz macht die Standardausrichtung quer zur Blickrichtung
     * (wie eine Bahnsteigkante, die man von vorne anläuft). Wird konsistent
     * hier, in der Vorschau-Outline ({@code client.PlacementOutlineHandler})
     * und im Rotations-HUD ({@code client.RotationHudOverlay}) angewendet.
     */
    public static final float ROTATION_OFFSET_DEGREES = 90f;

    public FloorMarkingBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean placed = super.placeBlock(context, state);
        if (!placed) {
            return false;
        }

        Level level = context.getLevel();
        if (level.isClientSide) {
            return true;
        }

        if (!(level.getBlockEntity(context.getClickedPos()) instanceof FloorMarkingBlockEntity blockEntity)) {
            return true;
        }

        int steps = StationDecorConfig.FLOOR_MARKING_ROTATION_STEPS.get();
        Player player = context.getPlayer();
        float yaw = (player != null ? player.getYRot() : 0f) + ROTATION_OFFSET_DEGREES;
        int rotationIndex = RotationUtil.snapToIndex(yaw, steps);
        float rotationDegrees = RotationUtil.indexToDegrees(rotationIndex, steps);

        int offsetIndex = computeOffsetIndex(context, rotationDegrees);
        blockEntity.setPlacement(rotationIndex, steps, offsetIndex);
        return true;
    }

    /**
     * Bestimmt anhand des exakten Klickpunkts auf der anvisierten Fläche, ob
     * näher, mittig oder weiter entfernt (entlang der gedrehten Vorwärtsachse)
     * platziert werden soll - passend zu den 3 Zonen der Vorschau-Outline aus
     * {@code client.render.PlacementOutlineHandler}.
     */
    private static int computeOffsetIndex(BlockPlaceContext context, float rotationDegrees) {
        Direction face = context.getClickedFace();
        BlockPos targetPos = context.replacingClickedOnBlock()
                ? context.getClickedPos()
                : context.getClickedPos().relative(face.getOpposite());

        Vec3 relative = context.getClickLocation().subtract(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        Vec3 centered = relative.subtract(0.5, 0.5, 0.5);
        Vec3 forward = RotationUtil.forwardVector(rotationDegrees);
        double depth = centered.dot(forward);

        if (depth > 1.0 / 6.0) {
            return 1;
        } else if (depth < -1.0 / 6.0) {
            return -1;
        }
        return 0;
    }
}
