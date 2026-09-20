package com.stationdecor.compat.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.stationdecor.block.signal.SectionState;
import com.stationdecor.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class CreateCompat {

    private CreateCompat() {
    }

    public static void register() {
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_MAIN_SIGNAL.get(), CreateDisplayRegistry.KS_MAIN_SIGNAL_TARGET.get());
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_DISTANT_SIGNAL.get(), CreateDisplayRegistry.KS_DISTANT_SIGNAL_TARGET.get());
        DisplayTarget.BY_BLOCK.register(ModBlocks.KS_MULTI_SECTION_SIGNAL.get(), CreateDisplayRegistry.KS_MULTI_SECTION_SIGNAL_TARGET.get());

        DisplaySource.BY_BLOCK.add(ModBlocks.KS_MAIN_SIGNAL.get(), CreateDisplayRegistry.KS_MAIN_SIGNAL_SOURCE.get());
        DisplaySource.BY_BLOCK.add(ModBlocks.KS_MULTI_SECTION_SIGNAL.get(), CreateDisplayRegistry.KS_MULTI_SECTION_SIGNAL_SOURCE.get());
    }

    @Nullable
    public static SectionState readTrackSignalBelow(Level level, BlockPos origin, int maxDistance) {
        for (int distance = 1; distance <= maxDistance; distance++) {
            BlockEntity blockEntity = level.getBlockEntity(origin.below(distance));
            if (blockEntity instanceof SignalBlockEntity signal) {
                return switch (signal.getState()) {
                    case RED -> SectionState.HALT;
                    case YELLOW -> SectionState.HALT_ERWARTEN;
                    case GREEN, INVALID -> SectionState.FAHRT;
                };
            }
        }
        return null;
    }
}
