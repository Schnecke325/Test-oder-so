package com.stationdecor.compat.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.SingleLineDisplayTarget;
import com.stationdecor.block.signal.KsMultiSectionSignalBlockEntity;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

/**
 * Bindet {@link KsMultiSectionSignalBlockEntity} als Create-Display-Link-Ziel
 * an: der per Display Link vom "Signal davor" empfangene Begriff wird auf
 * "zeigt Halt?" reduziert und in {@link KsMultiSectionSignalBlockEntity#setUpstreamHalt}
 * gespeichert. Nur ein tatsächliches Halt-Signal propagiert weiter als
 * "Halt erwarten" - ein bereits "Halt erwarten" zeigendes Signal muss vom
 * Signal davor nicht ebenfalls vorgewarnt werden (entspricht realer
 * Signallogik: die Vorwarnung gilt immer nur für den unmittelbar nächsten
 * Halt-Begriff).
 */
public class KsMultiSectionSignalDisplayTarget extends SingleLineDisplayTarget {

    private static final Set<String> HALT_VALUES = Set.of("halt", "hp0", "vr0", "0", "red", "stop");

    @Override
    protected void acceptLine(MutableComponent text, DisplayLinkContext context) {
        boolean halt = HALT_VALUES.contains(text.getString().trim().toLowerCase());
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
