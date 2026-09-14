package com.stationdecor.block.obj;

/**
 * Optische Variante des Fahrkartenautomaten. Jede Variante ist ein eigener,
 * separat registrierter Block (siehe {@code ModBlocks.OBJ_DISPLAY}). Weitere
 * Varianten einfach hier als weiteren Konstanten ergänzen, dazu eine
 * passende {@code models/block/obj_display_render_<name>.json}
 * (Textur-Zuordnung) und {@code textures/block/ticket_machine_<name>.png}
 * anlegen, siehe {@link com.stationdecor.client.render.ObjDisplayBlockEntityRenderer}
 * und {@code ModBlocks}/{@code ModItems}.
 */
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
