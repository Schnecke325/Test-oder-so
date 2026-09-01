package com.stationdecor.block.signal;

import net.minecraft.util.StringRepresentable;

/**
 * Signalbegriffe des Ks-Hauptsignals: Hp0 (Halt), Hp1 (Fahrt),
 * Hp2 (Fahrt mit Geschwindigkeitsbeschränkung).
 */
public enum MainSignalAspect implements StringRepresentable {
    HP0("hp0"),
    HP1("hp1"),
    HP2("hp2");

    private final String serializedName;

    MainSignalAspect(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public MainSignalAspect next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
