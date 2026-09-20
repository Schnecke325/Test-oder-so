package com.stationdecor.block.obj;

public enum TicketMachineStyle {
    DB("db"),
    BVG("bvg"),
    BEWEGT("bewegt"),
    GOAHEAD("goahead"),
    RMV("rmv"),
    VVR("vvr");

    private final String serializedName;

    TicketMachineStyle(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return serializedName;
    }
}
