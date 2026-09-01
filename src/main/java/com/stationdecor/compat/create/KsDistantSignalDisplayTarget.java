package com.stationdecor.compat.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.SingleLineDisplayTarget;
import com.stationdecor.StationDecorMod;
import com.stationdecor.block.signal.DistantSignalAspect;
import com.stationdecor.block.signal.KsDistantSignalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bindet {@link KsDistantSignalBlock} als Create-Display-Link-Ziel an. Ein
 * Display Link kann Text wie "vr0", "1" oder "Vr2" senden, um den gezeigten
 * Signalbegriff zu setzen.
 */
public class KsDistantSignalDisplayTarget extends SingleLineDisplayTarget {

    @Override
    protected void acceptLine(MutableComponent text, DisplayLinkContext context) {
        String raw = text.getString();
        DistantSignalAspect aspect = parseAspect(raw);
        StationDecorMod.LOGGER.info("Ks-Vorsignal bei {} hat \"{}\" per Display Link empfangen -> {}",
                context.getTargetPos(), raw, aspect);
        if (aspect == null) {
            return;
        }

        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof KsDistantSignalBlock && state.getValue(KsDistantSignalBlock.ASPECT) != aspect) {
            level.setBlock(pos, state.setValue(KsDistantSignalBlock.ASPECT, aspect), 3);
        }
    }

    @Override
    protected int getWidth(DisplayLinkContext context) {
        return 4;
    }

    /**
     * Bewusst großzügig geparst, siehe {@link KsMainSignalDisplayTarget#parseAspect}.
     * Was tatsächlich ankommt, steht im Log (siehe {@code acceptLine}).
     */
    private static DistantSignalAspect parseAspect(String rawText) {
        String value = rawText.trim().toLowerCase();
        return switch (value) {
            case "0", "vr0", "halt", "red", "stop" -> DistantSignalAspect.VR0;
            case "1", "vr1", "fahrt", "green", "go", "proceed" -> DistantSignalAspect.VR1;
            case "2", "vr2", "yellow" -> DistantSignalAspect.VR2;
            default -> null;
        };
    }
}
