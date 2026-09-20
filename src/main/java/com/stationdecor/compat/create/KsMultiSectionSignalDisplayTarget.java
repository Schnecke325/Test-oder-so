package com.stationdecor.compat.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.SingleLineDisplayTarget;
import com.stationdecor.StationDecorMod;
import com.stationdecor.block.signal.KsMultiSectionSignalBlockEntity;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class KsMultiSectionSignalDisplayTarget extends SingleLineDisplayTarget {

    private static final Set<String> HALT_VALUES = Set.of("halt", "hp0", "vr0", "0", "red", "stop");

    @Override
    protected void acceptLine(MutableComponent text, DisplayLinkContext context) {
        String raw = text.getString();
        boolean halt = HALT_VALUES.contains(raw.trim().toLowerCase());
        StationDecorMod.LOGGER.info("Ks-Mehrabschnittssignal bei {} hat \"{}\" per Display Link empfangen -> halt={}",
                context.getTargetPos(), raw, halt);
        BlockEntity be = context.getTargetBlockEntity();
        if (be instanceof KsMultiSectionSignalBlockEntity signal) {
            signal.setUpstreamHalt(halt);
        }
    }

    @Override
    protected int getWidth(DisplayLinkContext context) {
        return 12;
    }
}
