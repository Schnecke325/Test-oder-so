package com.stationdecor.block.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class SignalLinkUtil {

    private SignalLinkUtil() {
    }

    @Nullable
    public static Boolean readHalt(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return null;
        }
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof KsMainSignalBlock) {
            return state.getValue(KsMainSignalBlock.ASPECT) == MainSignalAspect.HP0;
        }
        if (state.getBlock() instanceof KsMultiSectionSignalBlock) {
            return state.getValue(KsMultiSectionSignalBlock.ASPECT) == CombinedSignalAspect.HALT;
        }
        return null;
    }

    public static boolean isValidSource(BlockState state) {
        return state.getBlock() instanceof KsMainSignalBlock || state.getBlock() instanceof KsMultiSectionSignalBlock;
    }

    public static boolean isValidTarget(BlockState state) {
        return state.getBlock() instanceof KsDistantSignalBlock || state.getBlock() instanceof KsMultiSectionSignalBlock;
    }
}
