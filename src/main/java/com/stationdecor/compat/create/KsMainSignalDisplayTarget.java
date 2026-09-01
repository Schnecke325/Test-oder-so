package com.stationdecor.compat.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.SingleLineDisplayTarget;
import com.stationdecor.block.signal.KsMainSignalBlock;
import com.stationdecor.block.signal.MainSignalAspect;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bindet {@link KsMainSignalBlock} als Create-Display-Link-Ziel an, analog zu
 * Create's eigenen Nixie Tubes. Ein Display Link kann Text wie "hp0", "1"
 * oder "Hp2" an dieses Ziel senden, um den gezeigten Signalbegriff zu setzen.
 */
public class KsMainSignalDisplayTarget extends SingleLineDisplayTarget {

    @Override
    protected void acceptLine(MutableComponent text, DisplayLinkContext context) {
        MainSignalAspect aspect = parseAspect(text.getString());
        if (aspect == null) {
            return;
        }

        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof KsMainSignalBlock && state.getValue(KsMainSignalBlock.ASPECT) != aspect) {
            level.setBlock(pos, state.setValue(KsMainSignalBlock.ASPECT, aspect), 3);
        }
    }

    @Override
    protected int getWidth(DisplayLinkContext context) {
        return 4;
    }

    private static MainSignalAspect parseAspect(String rawText) {
        String value = rawText.trim().toLowerCase();
        return switch (value) {
            case "0", "hp0" -> MainSignalAspect.HP0;
            case "1", "hp1" -> MainSignalAspect.HP1;
            case "2", "hp2" -> MainSignalAspect.HP2;
            default -> null;
        };
    }
}
