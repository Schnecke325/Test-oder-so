package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import com.stationdecor.menu.ObjDisplayMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, StationDecorMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ObjDisplayMenu>> OBJ_DISPLAY_MENU =
            MENUS.register("obj_display", () -> new MenuType<>(ObjDisplayMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {
    }
}
