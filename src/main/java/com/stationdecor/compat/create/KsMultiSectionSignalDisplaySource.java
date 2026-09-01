package com.stationdecor.compat.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.stationdecor.block.signal.CombinedSignalAspect;
import com.stationdecor.block.signal.KsMultiSectionSignalBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Lässt ein Create Display Link den aktuellen Begriff eines
 * {@link KsMultiSectionSignalBlock} auslesen - damit lassen sich mehrere
 * Mehrabschnittssignale hintereinander verketten (jedes bindet sich per
 * Display Link an das vorherige).
 */
public class KsMultiSectionSignalDisplaySource extends DisplaySource {

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        Level level = context.level();
        BlockState state = level.getBlockState(context.getSourcePos());
        if (!(state.getBlock() instanceof KsMultiSectionSignalBlock)) {
            return DisplaySource.EMPTY;
        }
        CombinedSignalAspect aspect = state.getValue(KsMultiSectionSignalBlock.ASPECT);
        return List.of(Component.literal(aspect.getSerializedName()));
    }
}
