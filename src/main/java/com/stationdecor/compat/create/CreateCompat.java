package com.stationdecor.compat.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.stationdecor.block.signal.SectionState;
import com.stationdecor.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Einstiegspunkt für die Create-Integration. Diese Klasse referenziert
 * Create-Klassen direkt und darf daher NUR aufgerufen werden, nachdem geprüft
 * wurde, dass Create tatsächlich geladen ist (siehe {@code StationDecorMod}
 * und {@code KsMultiSectionSignalBlockEntity}) - andernfalls würde das Laden
 * dieser Klasse mit einem {@link NoClassDefFoundError} abstürzen. Create
 * selbst ist nur eine {@code compileOnly}-Abhängigkeit (siehe build.gradle)
 * und wird zur Laufzeit nicht vorausgesetzt.
 */
public final class CreateCompat {

    private CreateCompat() {
    }

    public static void register() {
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_MAIN_SIGNAL.get(), new KsMainSignalDisplayTarget());
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_DISTANT_SIGNAL.get(), new KsDistantSignalDisplayTarget());
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_MULTI_SECTION_SIGNAL.get(), new KsMultiSectionSignalDisplayTarget());

        DisplaySource.BY_BLOCK.add(ModBlocks.KS_MAIN_SIGNAL.get(), new KsMainSignalDisplaySource());
        DisplaySource.BY_BLOCK.add(ModBlocks.KS_MULTI_SECTION_SIGNAL.get(), new KsMultiSectionSignalDisplaySource());
    }

    /**
     * Sucht ab {@code origin} (exklusiv) bis zu {@code maxDistance} Blöcke
     * gerade nach unten nach einem Create-Gleissignal und übersetzt dessen
     * Zustand (RED/YELLOW/GREEN/INVALID) in unseren eigenen, Create-freien
     * {@link SectionState}. Gibt {@code null} zurück, wenn in der Reichweite
     * kein Gleissignal gefunden wurde.
     */
    @Nullable
    public static SectionState readTrackSignalBelow(Level level, BlockPos origin, int maxDistance) {
        for (int distance = 1; distance <= maxDistance; distance++) {
            BlockEntity blockEntity = level.getBlockEntity(origin.below(distance));
            if (blockEntity instanceof SignalBlockEntity signal) {
                return switch (signal.getState()) {
                    case RED -> SectionState.HALT;
                    case YELLOW -> SectionState.HALT_ERWARTEN;
                    case GREEN, INVALID -> SectionState.FAHRT;
                };
            }
        }
        return null;
    }
}
