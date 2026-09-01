package com.stationdecor.block.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Gemeinsame Logik für den Signalbinder ({@code SignalBinderItem}): liest
 * aus, ob ein per Binder verlinktes Signal ("Signal davor") gerade Halt
 * zeigt. Genutzt sowohl von {@link KsDistantSignalBlockEntity} (Vr0/Vr1) als
 * auch von {@link KsMultiSectionSignalBlockEntity} (Vorwarnung für "Halt
 * erwarten"). Absichtlich unabhängig von Create - reine
 * Block-zu-Block-Verlinkung ohne Display Link.
 */
public final class SignalLinkUtil {

    private SignalLinkUtil() {
    }

    /**
     * @return {@code true}/{@code false} für Halt/nicht-Halt, oder
     * {@code null}, wenn an {@code pos} kein gültiges Quellsignal (mehr)
     * steht bzw. der Chunk nicht geladen ist - in dem Fall soll der Aufrufer
     * den zuletzt bekannten Zustand beibehalten statt ihn zu verwerfen.
     */
    @Nullable
    public static Boolean readHalt(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return null;
        }
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof KsMainSignalBlock) {
            return state.getValue(KsMainSignalBlock.ASPECT) == MainSignalAspect.HP0;
        }
        if (state.getBlock() instanceof KsMultiSectionSignalBlock) {
            return state.getValue(KsMultiSectionSignalBlock.ASPECT) == CombinedSignalAspect.HALT;
        }
        return null;
    }

    /** Gültige Quellen für den Signalbinder - alles mit einem Halt/Fahrt-Begriff. */
    public static boolean isValidSource(BlockState state) {
        return state.getBlock() instanceof KsMainSignalBlock || state.getBlock() instanceof KsMultiSectionSignalBlock;
    }

    /** Gültige Ziele für den Signalbinder - alles, was ein "Signal davor" braucht. */
    public static boolean isValidTarget(BlockState state) {
        return state.getBlock() instanceof KsDistantSignalBlock || state.getBlock() instanceof KsMultiSectionSignalBlock;
    }
}
