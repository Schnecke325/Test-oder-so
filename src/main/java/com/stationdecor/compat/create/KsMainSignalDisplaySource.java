package com.stationdecor.compat.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.stationdecor.block.signal.KsMainSignalBlock;
import com.stationdecor.block.signal.MainSignalAspect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Lässt ein Create Display Link den aktuellen Signalbegriff eines
 * {@link KsMainSignalBlock} auslesen (z.B. um ihn per Display Link an ein
 * {@code station_decor:ks_multi_section_signal} als "Signal davor" zu binden).
 */
public class KsMainSignalDisplaySource extends DisplaySource {

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        Level level = context.level();
        BlockState state = level.getBlockState(context.getSourcePos());
        if (!(state.getBlock() instanceof KsMainSignalBlock)) {
            return DisplaySource.EMPTY;
        }
        MainSignalAspect aspect = state.getValue(KsMainSignalBlock.ASPECT);
        return List.of(Component.literal(aspect.getSerializedName()));
    }
}
