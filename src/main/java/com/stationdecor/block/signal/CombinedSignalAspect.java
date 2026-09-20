package com.stationdecor.block.signal;

import net.minecraft.util.StringRepresentable;

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
