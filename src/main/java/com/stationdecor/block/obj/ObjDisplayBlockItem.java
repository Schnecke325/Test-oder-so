package com.stationdecor.block.obj;

import com.stationdecor.registry.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Eigenes {@link BlockItem} für den Fahrkartenautomaten: Rechtsklick in die
 * Luft (kein Block in Reichweite, sonst würde der Block selbst reagieren)
 * schaltet die optische Variante ({@link TicketMachineStyle}) weiter, auf
 * dem Item als Data Component gespeichert. {@link ObjDisplayBlock#setPlacedBy}
 * überträgt die gewählte Variante beim Platzieren auf die BlockEntity.
 */
public class ObjDisplayBlockItem extends BlockItem {

    public ObjDisplayBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        TicketMachineStyle current = stack.getOrDefault(ModDataComponents.TICKET_MACHINE_STYLE.get(), TicketMachineStyle.DB);
        TicketMachineStyle next = current.next();
        stack.set(ModDataComponents.TICKET_MACHINE_STYLE.get(), next);

        player.displayClientMessage(Component.translatable("item.station_decor.obj_display.style_changed",
                Component.translatable("station_decor.ticket_machine_style." + next.getSerializedName())), true);
        return InteractionResultHolder.success(stack);
    }
}
