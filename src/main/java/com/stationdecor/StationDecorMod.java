package com.stationdecor;

import com.stationdecor.config.StationDecorConfig;
import com.stationdecor.registry.ModBlockEntities;
import com.stationdecor.registry.ModBlocks;
import com.stationdecor.registry.ModCreativeTabs;
import com.stationdecor.registry.ModEntities;
import com.stationdecor.registry.ModItems;
import com.stationdecor.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Haupteinstiegspunkt der Mod. Registriert alle Deferred Register und die Config.
 */
@Mod(StationDecorMod.MOD_ID)
public class StationDecorMod {

    public static final String MOD_ID = "station_decor";
    public static final Logger LOGGER = LoggerFactory.getLogger("StationDecor");

    public StationDecorMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, StationDecorConfig.SPEC);
    }
}
