package com.stationdecor.block.signal;

import com.stationdecor.compat.create.CreateCompat;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

/**
 * BlockEntity des Mehrabschnittssignals. Scannt periodisch bis zu 10 Blöcke
 * unter sich nach einem Create-Gleissignal und kombiniert dessen Zustand mit
 * dem zuletzt per Display Link empfangenen "Signal davor"-Zustand zu einem
 * {@link CombinedSignalAspect}, der als BlockState-Property gesetzt wird.
 * <p>
 * Referenziert absichtlich NIE direkt eine {@code com.simibubi.create}-Klasse -
 * das übernimmt ausschließlich {@link CreateCompat}, aufgerufen hinter einem
 * {@link ModList}-Check, damit diese Klasse auch ohne installiertes Create
 * anstandslos lädt.
 */
public class KsMultiSectionSignalBlockEntity extends BlockEntity {

    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int MAX_SCAN_DISTANCE = 10;

    private boolean upstreamHalt = false;
    private int scanCooldown = 0;

    public KsMultiSectionSignalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KS_MULTI_SECTION_SIGNAL.get(), pos, state);
    }

    /**
     * Wird vom Create-Display-Link-Ziel aufgerufen, wenn ein neuer Zustand
     * vom Signal "davor" eintrifft.
     */
    public void setUpstreamHalt(boolean halt) {
        if (this.upstreamHalt != halt) {
            this.upstreamHalt = halt;
            setChanged();
            recomputeAspect();
        }
    }

    public boolean isUpstreamHalt() {
        return upstreamHalt;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KsMultiSectionSignalBlockEntity blockEntity) {
        if (blockEntity.scanCooldown-- > 0) {
            return;
        }
        blockEntity.scanCooldown = SCAN_INTERVAL_TICKS;
        blockEntity.recomputeAspect();
    }

    private void recomputeAspect() {
        if (level == null || level.isClientSide) {
            return;
        }

        SectionState localSection = null;
        if (ModList.get().isLoaded("create")) {
            localSection = CreateCompat.readTrackSignalBelow(level, worldPosition, MAX_SCAN_DISTANCE);
        }

        CombinedSignalAspect newAspect;
        if (localSection == SectionState.HALT) {
            newAspect = CombinedSignalAspect.HALT;
        } else if (localSection == SectionState.HALT_ERWARTEN) {
            newAspect = CombinedSignalAspect.HALT_ERWARTEN;
        } else if (upstreamHalt) {
            newAspect = CombinedSignalAspect.HALT_ERWARTEN;
        } else {
            newAspect = CombinedSignalAspect.FAHRT;
        }

        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(KsMultiSectionSignalBlock.ASPECT) && state.getValue(KsMultiSectionSignalBlock.ASPECT) != newAspect) {
            level.setBlock(worldPosition, state.setValue(KsMultiSectionSignalBlock.ASPECT, newAspect), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("UpstreamHalt", upstreamHalt);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        upstreamHalt = tag.getBoolean("UpstreamHalt");
    }
}
