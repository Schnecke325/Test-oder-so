package com.stationdecor.compat.create;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.stationdecor.registry.ModBlocks;

/**
 * Einstiegspunkt für die Create-Integration. Diese Klasse referenziert
 * Create-Klassen direkt und darf daher NUR aufgerufen werden, nachdem geprüft
 * wurde, dass Create tatsächlich geladen ist (siehe {@code StationDecorMod}) -
 * andernfalls würde das Laden dieser Klasse mit einem {@link NoClassDefFoundError}
 * abstürzen. Create selbst ist nur eine {@code compileOnly}-Abhängigkeit
 * (siehe build.gradle) und wird zur Laufzeit nicht vorausgesetzt.
 */
public final class CreateCompat {

    private CreateCompat() {
    }

    public static void register() {
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_MAIN_SIGNAL.get(), new KsMainSignalDisplayTarget());
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_DISTANT_SIGNAL.get(), new KsDistantSignalDisplayTarget());
    }
}
