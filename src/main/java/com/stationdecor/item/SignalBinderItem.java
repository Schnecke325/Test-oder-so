package com.stationdecor.item;

import com.stationdecor.block.signal.KsDistantSignalBlockEntity;
import com.stationdecor.block.signal.KsMultiSectionSignalBlockEntity;
import com.stationdecor.block.signal.SignalLinkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Verlinkt zwei Signale ohne Create Display Link: erster Rechtsklick auf das
 * Ziel (Ks-Vorsignal oder Ks-Mehrabschnittssignal), zweiter Rechtsklick auf
 * das Quellsignal ("Signal davor", Ks-Hauptsignal oder Ks-Mehrabschnittssignal).
 * Der "ausstehende Klick"-Zustand wird bewusst nur im Arbeitsspeicher pro
 * Spieler gehalten (nicht auf dem Item persistiert) - ein einfacher,
 * sitzungsbezogener Zwei-Klick-Vorgang.
 */
public class SignalBinderItem extends Item {

    private static final Map<UUID, BlockPos> PENDING_TARGET = new HashMap<>();

    public SignalBinderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos().immutable();
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos pending = PENDING_TARGET.get(player.getUUID());

        if (pending == null) {
            if (!SignalLinkUtil.isValidTarget(clickedState)) {
                player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.invalid_target"), true);
                return InteractionResult.FAIL;
            }
            PENDING_TARGET.put(player.getUUID(), clickedPos);
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.target_selected"), true);
            return InteractionResult.SUCCESS;
        }

        PENDING_TARGET.remove(player.getUUID());

        if (clickedPos.equals(pending)) {
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.cancelled"), true);
            return InteractionResult.FAIL;
        }
        if (!SignalLinkUtil.isValidSource(clickedState)) {
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.invalid_source"), true);
            return InteractionResult.FAIL;
        }

        BlockEntity targetBlockEntity = level.getBlockEntity(pending);
        if (targetBlockEntity instanceof KsDistantSignalBlockEntity distant) {
            distant.setLinkedSignalPos(clickedPos);
        } else if (targetBlockEntity instanceof KsMultiSectionSignalBlockEntity multi) {
            multi.setLinkedSignalPos(clickedPos);
        } else {
            player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.cancelled"), true);
            return InteractionResult.FAIL;
        }

        player.displayClientMessage(Component.translatable("item.station_decor.signal_binder.linked"), true);
        return InteractionResult.SUCCESS;
    }
}
