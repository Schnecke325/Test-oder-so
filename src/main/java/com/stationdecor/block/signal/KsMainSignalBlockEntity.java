package com.stationdecor.block.signal;

import com.stationdecor.compat.create.CreateCompat;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

/**
 * BlockEntity des Ks-Hauptsignals. Scannt periodisch bis zu 10 Blöcke unter
 * sich nach einem Create-Gleissignal und übernimmt dessen Zustand direkt als
 * Signalbegriff. Wird dabei kein Gleissignal gefunden (z.B. weil kein Create
 * installiert ist, oder einfach keins in Reichweite steht), bleibt der zuletzt
 * gesetzte Begriff (manuell per Rechtsklick oder per Display Link) erhalten.
 * <p>
 * Referenziert absichtlich NIE direkt eine {@code com.simibubi.create}-Klasse -
 * das übernimmt ausschließlich {@link CreateCompat}, aufgerufen hinter einem
 * {@link ModList}-Check, damit diese Klasse auch ohne installiertes Create
 * anstandslos lädt.
 */
public class KsMainSignalBlockEntity extends BlockEntity {

    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int MAX_SCAN_DISTANCE = 10;

    private int scanCooldown = 0;

    public KsMainSignalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KS_MAIN_SIGNAL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KsMainSignalBlockEntity blockEntity) {
        if (blockEntity.scanCooldown-- > 0) {
            return;
        }
        blockEntity.scanCooldown = SCAN_INTERVAL_TICKS;
        blockEntity.recomputeFromTrack();
    }

    private void recomputeFromTrack() {
        if (level == null || level.isClientSide || !ModList.get().isLoaded("create")) {
            return;
        }

        SectionState localSection = CreateCompat.readTrackSignalBelow(level, worldPosition, MAX_SCAN_DISTANCE);
        if (localSection == null) {
            return;
        }

        MainSignalAspect newAspect = switch (localSection) {
            case HALT -> MainSignalAspect.HP0;
            case FAHRT -> MainSignalAspect.HP1;
            case HALT_ERWARTEN -> MainSignalAspect.HP2;
        };

        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(KsMainSignalBlock.ASPECT) && state.getValue(KsMainSignalBlock.ASPECT) != newAspect) {
            level.setBlock(worldPosition, state.setValue(KsMainSignalBlock.ASPECT, newAspect), 3);
        }
    }
}
