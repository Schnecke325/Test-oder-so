package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.marking.FloorMarkingBlockItem;
import com.stationdecor.block.obj.TicketMachineStyle;
import com.stationdecor.item.SignalBinderItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StationDecorMod.MOD_ID);

    /** Ein separat registriertes Item pro Fahrkartenautomat-Variante, siehe {@link TicketMachineStyle}. */
    public static final Map<TicketMachineStyle, Supplier<BlockItem>> OBJ_DISPLAY = registerObjDisplayItems();

    private static Map<TicketMachineStyle, Supplier<BlockItem>> registerObjDisplayItems() {
        Map<TicketMachineStyle, Supplier<BlockItem>> items = new EnumMap<>(TicketMachineStyle.class);
        for (TicketMachineStyle style : TicketMachineStyle.values()) {
            items.put(style, ITEMS.registerSimpleBlockItem(
                    "obj_display_" + style.getSerializedName(), ModBlocks.OBJ_DISPLAY.get(style), new Item.Properties()));
        }
        return items;
    }

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
