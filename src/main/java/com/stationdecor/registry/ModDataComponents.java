package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.obj.TicketMachineStyle;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, StationDecorMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TicketMachineStyle>> TICKET_MACHINE_STYLE =
            DATA_COMPONENTS.register("ticket_machine_style", () -> DataComponentType.<TicketMachineStyle>builder()
                    .persistent(TicketMachineStyle.CODEC)
                    .networkSynchronized(TicketMachineStyle.STREAM_CODEC)
                    .build());

    private ModDataComponents() {
    }
}
