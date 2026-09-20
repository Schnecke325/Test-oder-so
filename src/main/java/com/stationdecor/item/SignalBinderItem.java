package com.stationdecor.item;

import com.stationdecor.block.signal.KsDistantSignalBlockEntity;
import com.stationdecor.block.signal.KsMultiSectionSignalBlockEntity;
import com.stationdecor.block.signal.SignalLinkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignalBinderItem extends Item {

    private static final Map<UUID, BlockPos> PENDING_TARGET = new HashMap<>();

    public SignalBinderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return handleClick(context.getLevel(), context.getClickedPos(), context.getPlayer()).result();
    }

    public static ItemInteractionResult handleClick(Level level, BlockPos pos, @Nullable Player player) {
        if (level.isClientSide || player == null) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockPos clickedPos = pos.immutable();
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos pending = PENDING_TARGET.get(player.getUUID());

        if (pending == null) {
            if (!SignalLinkUtil.isValidTarget(clickedState)) {
                player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.invalid_target"), true);
                return ItemInteractionResult.FAIL;
            }
            PENDING_TARGET.put(player.getUUID(), clickedPos);
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.target_selected"), true);
            return ItemInteractionResult.SUCCESS;
        }

        PENDING_TARGET.remove(player.getUUID());

        if (clickedPos.equals(pending)) {
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.cancelled"), true);
            return ItemInteractionResult.FAIL;
        }
        if (!SignalLinkUtil.isValidSource(clickedState)) {
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.invalid_source"), true);
            return ItemInteractionResult.FAIL;
        }

        BlockEntity targetBlockEntity = level.getBlockEntity(pending);
        if (targetBlockEntity instanceof KsDistantSignalBlockEntity distant) {
            distant.setLinkedSignalPos(clickedPos);
        } else if (targetBlockEntity instanceof KsMultiSectionSignalBlockEntity multi) {
            multi.setLinkedSignalPos(clickedPos);
        } else {
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.cancelled"), true);
            return ItemInteractionResult.FAIL;
        }

        player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.linked"), true);
        return ItemInteractionResult.SUCCESS;
    }
}
