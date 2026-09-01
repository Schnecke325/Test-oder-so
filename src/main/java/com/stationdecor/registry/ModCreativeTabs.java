package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
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
                    .icon(() -> new ItemStack(ModItems.OBJ_DISPLAY.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.OBJ_DISPLAY.get());
                        output.accept(ModItems.SEAT.get());
                        output.accept(ModItems.FLOOR_MARKING.get());
                        output.accept(ModItems.KS_MAIN_SIGNAL.get());
                        output.accept(ModItems.KS_DISTANT_SIGNAL.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
