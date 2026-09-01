package com.stationdecor.block.signal;

import net.minecraft.util.StringRepresentable;

/**
 * Anzeigebegriffe des Mehrabschnittssignals: kombiniert Haupt- und
 * Vorsignalfunktion in einem Signal (Fahrt / Halt / Halt erwarten).
 */
public enum CombinedSignalAspect implements StringRepresentable {
    HALT("halt"),
    HALT_ERWARTEN("halt_erwarten"),
    FAHRT("fahrt");

    private final String serializedName;

    CombinedSignalAspect(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
