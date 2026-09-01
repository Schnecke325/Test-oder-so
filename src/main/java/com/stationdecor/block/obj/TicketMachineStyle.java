package com.stationdecor.block.obj;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * Optische Variante des Fahrkartenautomaten (aktuell nur "db", der gelieferte
 * DB-Automat). Weitere Varianten (z.B. "rmv") einfach hier als weiteren
 * Konstanten ergänzen, dazu eine passende
 * {@code models/block/obj_display_render_<name>.json} (Textur-Zuordnung) und
 * {@code textures/block/ticket_machine_<name>.png} anlegen, siehe
 * {@link com.stationdecor.client.render.ObjDisplayBlockEntityRenderer}.
 */
public enum TicketMachineStyle implements StringRepresentable {
    DB("db");

    public static final StringRepresentable.EnumCodec<TicketMachineStyle> CODEC = StringRepresentable.fromEnum(TicketMachineStyle::values);
    public static final StreamCodec<ByteBuf, TicketMachineStyle> STREAM_CODEC =
            ByteBufCodecs.idMapper(id -> values()[id], TicketMachineStyle::ordinal);

    private final String serializedName;

    TicketMachineStyle(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public TicketMachineStyle next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
