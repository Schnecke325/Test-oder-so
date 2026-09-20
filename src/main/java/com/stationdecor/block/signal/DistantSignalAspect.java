package com.stationdecor.block.signal;

import net.minecraft.util.StringRepresentable;

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
