package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StationDecorMod.MOD_ID);

    public static final Supplier<BlockItem> OBJ_DISPLAY = ITEMS.registerSimpleBlockItem(
            "obj_display", ModBlocks.OBJ_DISPLAY, new Item.Properties());

    public static final Supplier<BlockItem> SEAT = ITEMS.registerSimpleBlockItem(
            "seat", ModBlocks.SEAT, new Item.Properties());

    private ModItems() {
    }
}
