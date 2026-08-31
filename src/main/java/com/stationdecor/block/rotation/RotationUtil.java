package com.stationdecor.block.rotation;

/**
 * Rechnet zwischen einer Blickrichtung (Yaw in Grad) und einem diskreten
 * Rotationsindex um, abhängig von einer konfigurierbaren Schrittzahl.
 * Index 0 entspricht Süden (0°, wie {@code Entity#getYRot()}), die Schritte
 * laufen im Uhrzeigersinn.
 */
public final class RotationUtil {

    private RotationUtil() {
    }

    /**
     * Rundet einen Yaw-Winkel auf den nächstgelegenen Rotationsindex.
     *
     * @param yawDegrees Blickrichtung in Grad (beliebiger Wert, auch negativ oder > 360)
     * @param steps      Anzahl der Rotationsschritte über 360° (muss > 0 sein)
     * @return Index im Bereich [0, steps)
     */
    public static int snapToIndex(float yawDegrees, int steps) {
        if (steps <= 0) {
            return 0;
        }
        float normalized = ((yawDegrees % 360f) + 360f) % 360f;
        float stepSize = 360f / steps;
        int index = Math.round(normalized / stepSize) % steps;
        return index < 0 ? index + steps : index;
    }

    /**
     * Liefert den Rotationswinkel in Grad für einen gegebenen Index/Schrittzahl.
     */
    public static float indexToDegrees(int index, int steps) {
        if (steps <= 0) {
            return 0f;
        }
        return index * (360f / steps);
    }

    /**
     * Stellt sicher, dass ein Index in den gültigen Bereich [0, steps) fällt,
     * z.B. nachdem sich die Config-Schrittzahl seit dem letzten Speichern geändert hat.
     */
    public static int clampIndex(int index, int steps) {
        if (steps <= 0) {
            return 0;
        }
        int result = index % steps;
        return result < 0 ? result + steps : result;
    }
}
