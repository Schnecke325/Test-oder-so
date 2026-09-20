package com.stationdecor.block.rotation;

import net.minecraft.world.phys.Vec3;

public final class RotationUtil {

    private RotationUtil() {
    }

    public static int snapToIndex(float yawDegrees, int steps) {
        if (steps <= 0) {
            return 0;
        }
        float normalized = ((yawDegrees % 360f) + 360f) % 360f;
        float stepSize = 360f / steps;
        int index = Math.round(normalized / stepSize) % steps;
        return index < 0 ? index + steps : index;
    }

    public static float indexToDegrees(int index, int steps) {
        if (steps <= 0) {
            return 0f;
        }
        return index * (360f / steps);
    }

    public static int clampIndex(int index, int steps) {
        if (steps <= 0) {
            return 0;
        }
        int result = index % steps;
        return result < 0 ? result + steps : result;
    }

    public static Vec3 forwardVector(float degrees) {
        double rad = Math.toRadians(degrees);
        return new Vec3(-Math.sin(rad), 0.0, Math.cos(rad));
    }

    public static float diagonalStretch(float degrees) {
        float mod90 = ((degrees % 90f) + 90f) % 90f;
        float distanceToNearestAxis = Math.min(mod90, 90f - mod90);
        return (float) (1.0 / Math.cos(Math.toRadians(distanceToNearestAxis)));
    }
}
