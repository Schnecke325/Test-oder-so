package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.obj.TicketMachineStyle;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StationDecorMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STATION_DECOR_TAB = TABS.register(
            "station_decor_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + StationDecorMod.MOD_ID))
                    .icon(() -> new ItemStack(ModItems.OBJ_DISPLAY.get(TicketMachineStyle.DB).get()))
                    .displayItems((params, output) -> {
                        for (TicketMachineStyle style : TicketMachineStyle.values()) {
                            output.accept(ModItems.OBJ_DISPLAY.get(style).get());
                        }
                        output.accept(ModItems.SEAT.get());
                        for (DyeColor color : DyeColor.values()) {
                            output.accept(ModItems.FLOOR_MARKING.get(color).get());
                        }
                        // Signale + Signalbinder vorübergehend aus dem Creative-Tab entfernt,
                        // bis echte Modelle dafür da sind - Block/Item bleiben registriert,
                        // hier einfach die 4 output.accept(...)-Zeilen wieder einfügen:
                        // output.accept(ModItems.KS_MAIN_SIGNAL.get());
                        // output.accept(ModItems.KS_DISTANT_SIGNAL.get());
                        // output.accept(ModItems.KS_MULTI_SECTION_SIGNAL.get());
                        // output.accept(ModItems.SIGNAL_BINDER.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
