package com.stationdecor.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common-Config der Mod. Steuert u.a. wie viele Rotationsschritte die
 * einzelnen Blöcke beim Platzieren anbieten (z.B. 4 = 90°, 8 = 45°, 16 = 22,5°).
 */
public final class StationDecorConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue OBJ_BLOCK_ROTATION_STEPS;
    public static final ModConfigSpec.IntValue SEAT_BLOCK_ROTATION_STEPS;
    public static final ModConfigSpec.BooleanValue SEAT_BLOCK_AUTO_ALIGN;
    public static final ModConfigSpec.IntValue FLOOR_MARKING_ROTATION_STEPS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("rotation");

        OBJ_BLOCK_ROTATION_STEPS = builder
                .comment(
                        "Anzahl der Rotationsschritte für den OBJ-Anzeigeblock, gleichmäßig über 360° verteilt.",
                        "Beispiele: 4 = 90°-Schritte, 8 = 45°-Schritte, 16 = 22,5°-Schritte.",
                        "Der zuletzt gültige Wert wird beim Start des Spiels aus der Config gelesen; ein Serverneustart",
                        "ist nötig, damit neu platzierte Blöcke die geänderte Schrittzahl verwenden."
                )
                .defineInRange("objBlockRotationSteps", 8, 2, 64);

        SEAT_BLOCK_ROTATION_STEPS = builder
                .comment(
                        "Anzahl der Rotationsschritte für den Sitz-Block, gleichmäßig über 360° verteilt.",
                        "Beispiele: 4 = 90°-Schritte, 8 = 45°-Schritte, 16 = 22,5°-Schritte."
                )
                .defineInRange("seatBlockRotationSteps", 8, 2, 64);

        SEAT_BLOCK_AUTO_ALIGN = builder
                .comment(
                        "Wenn aktiviert, übernimmt ein neu platzierter Sitz-Block automatisch die Rotation eines",
                        "direkt angrenzenden (Nord/Süd/Ost/West) bereits vorhandenen Sitz-Blocks, statt die Blickrichtung",
                        "des Spielers zu verwenden."
                )
                .define("seatBlockAutoAlign", true);

        FLOOR_MARKING_ROTATION_STEPS = builder
                .comment(
                        "Anzahl der Rotationsschritte für die Bodenmarkierung, gleichmäßig über 360° verteilt.",
                        "Diese Schrittzahl bestimmt auch die Ausrichtung der Vorschau-Balken beim Platzieren",
                        "(z.B. diagonal bei einem 45°-Schritt)."
                )
                .defineInRange("floorMarkingRotationSteps", 8, 2, 64);

        builder.pop();

        SPEC = builder.build();
    }

    private StationDecorConfig() {
    }
}
