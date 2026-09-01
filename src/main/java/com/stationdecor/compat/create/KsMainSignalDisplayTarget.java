package com.stationdecor.compat.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.SingleLineDisplayTarget;
import com.stationdecor.StationDecorMod;
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
        String raw = text.getString();
        MainSignalAspect aspect = parseAspect(raw);
        StationDecorMod.LOGGER.info("Ks-Hauptsignal bei {} hat \"{}\" per Display Link empfangen -> {}",
                context.getTargetPos(), raw, aspect);
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

    /**
     * Bewusst großzügig: akzeptiert neben "hp0"/"hp1"/"hp2" auch Zahlen und
     * gängige Klartext-Begriffe, falls die gebundene Quelle (z.B. Hebel,
     * Redstone Link) etwas anderes als unsere eigene "hpX"-Notation liefert.
     * Was tatsächlich ankommt, steht im Log (siehe {@code acceptLine}).
     */
    private static MainSignalAspect parseAspect(String rawText) {
        String value = rawText.trim().toLowerCase();
        return switch (value) {
            case "0", "hp0", "halt", "red", "stop" -> MainSignalAspect.HP0;
            case "1", "hp1", "fahrt", "green", "go", "proceed" -> MainSignalAspect.HP1;
            case "2", "hp2", "yellow" -> MainSignalAspect.HP2;
            default -> null;
        };
    }
}
