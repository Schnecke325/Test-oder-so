package com.stationdecor.block.rotation;

import net.minecraft.world.phys.Vec3;

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

    /**
     * Horizontaler "Vorwärts"-Vektor (Länge 1, Y=0) für einen Rotationswinkel in Grad,
     * in der gleichen Konvention wie {@code Entity#getYRot()} (0° = Süden/+Z, 90° = Westen/-X).
     * Wird genutzt, um bei Block 3 (Bodenmarkierung) die Nah/Mitte/Fern-Achse an die
     * tatsächliche (eingerastete) Platzierungsrotation zu koppeln, statt an die rohe Blickrichtung.
     */
    public static Vec3 forwardVector(float degrees) {
        double rad = Math.toRadians(degrees);
        return new Vec3(-Math.sin(rad), 0.0, Math.cos(rad));
    }

    /**
     * Streckfaktor für ein achsenparalleles Modell, das exakt eine Blockbreite lang ist und
     * trotzdem bei jeder Rotation bis zum gegenüberliegenden Blockrand reichen soll (z.B. die
     * Bodenmarkierung). Bei einer Diagonalen (z.B. 45°) ist die Strecke von Kante zu Kante um
     * den Faktor 1/cos(Winkel zur nächsten 90°-Achse) länger als bei achsenparalleler Rotation -
     * bei genau 45° also {@code 1/cos(45°) = √2 ≈ 1,41}. Periodisch alle 90°.
     */
    public static float diagonalStretch(float degrees) {
        float mod90 = ((degrees % 90f) + 90f) % 90f;
        float distanceToNearestAxis = Math.min(mod90, 90f - mod90);
        return (float) (1.0 / Math.cos(Math.toRadians(distanceToNearestAxis)));
    }
}
