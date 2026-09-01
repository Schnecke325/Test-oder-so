package com.stationdecor.block.signal;

import net.minecraft.util.StringRepresentable;

/**
 * Signalbegriffe des Ks-Vorsignals: Vr0 (Halt erwarten), Vr1 (Fahrt erwarten),
 * Vr2 (Fahrt mit Geschwindigkeitsbeschränkung erwarten).
 */
public enum DistantSignalAspect implements StringRepresentable {
    VR0("vr0"),
    VR1("vr1"),
    VR2("vr2");

    private final String serializedName;

    DistantSignalAspect(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public DistantSignalAspect next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
