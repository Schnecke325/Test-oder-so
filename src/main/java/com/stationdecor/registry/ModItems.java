package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.marking.FloorMarkingBlockItem;
import com.stationdecor.item.SignalBinderItem;
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

    public static final Supplier<FloorMarkingBlockItem> FLOOR_MARKING = ITEMS.register("floor_marking",
            () -> new FloorMarkingBlockItem(ModBlocks.FLOOR_MARKING.get(), new Item.Properties()));

    public static final Supplier<BlockItem> KS_MAIN_SIGNAL = ITEMS.registerSimpleBlockItem(
            "ks_main_signal", ModBlocks.KS_MAIN_SIGNAL, new Item.Properties());

    public static final Supplier<BlockItem> KS_DISTANT_SIGNAL = ITEMS.registerSimpleBlockItem(
            "ks_distant_signal", ModBlocks.KS_DISTANT_SIGNAL, new Item.Properties());

    public static final Supplier<BlockItem> KS_MULTI_SECTION_SIGNAL = ITEMS.registerSimpleBlockItem(
            "ks_multi_section_signal", ModBlocks.KS_MULTI_SECTION_SIGNAL, new Item.Properties());

    public static final Supplier<SignalBinderItem> SIGNAL_BINDER = ITEMS.register("signal_binder",
            () -> new SignalBinderItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }
}
