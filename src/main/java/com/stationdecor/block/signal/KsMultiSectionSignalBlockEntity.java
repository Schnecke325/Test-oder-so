package com.stationdecor.block.signal;

import com.stationdecor.compat.create.CreateCompat;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity des Mehrabschnittssignals. Scannt periodisch bis zu 10 Blöcke
 * unter sich nach einem Create-Gleissignal und kombiniert dessen Zustand mit
 * dem Zustand des per Signalbinder verlinkten "Signal davor"
 * ({@link #linkedSignalPos}, siehe {@link SignalLinkUtil}) zu einem
 * {@link CombinedSignalAspect}, der als BlockState-Property gesetzt wird.
 * {@link #setUpstreamHalt(boolean)} bleibt zusätzlich für Create Display
 * Link nutzbar, wird aber bei jedem Scan-Durchlauf vom Signalbinder-Link
 * überschrieben, falls einer gesetzt ist.
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
    @Nullable
    private BlockPos linkedSignalPos;
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

    /** Wird vom Signalbinder beim zweiten Klick (auf das Quellsignal) aufgerufen. */
    public void setLinkedSignalPos(@Nullable BlockPos pos) {
        this.linkedSignalPos = pos;
        setChanged();
        if (level != null && !level.isClientSide) {
            refreshLinkedUpstreamHalt();
            recomputeAspect();
        }
    }

    @Nullable
    public BlockPos getLinkedSignalPos() {
        return linkedSignalPos;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KsMultiSectionSignalBlockEntity blockEntity) {
        if (blockEntity.scanCooldown-- > 0) {
            return;
        }
        blockEntity.scanCooldown = SCAN_INTERVAL_TICKS;
        blockEntity.refreshLinkedUpstreamHalt();
        blockEntity.recomputeAspect();
    }

    private void refreshLinkedUpstreamHalt() {
        if (linkedSignalPos == null || level == null) {
            return;
        }
        Boolean halt = SignalLinkUtil.readHalt(level, linkedSignalPos);
        if (halt != null && halt != upstreamHalt) {
            upstreamHalt = halt;
            setChanged();
        }
    }

    private void recomputeAspect() {
        if (level == null || level.isClientSide) {
            return;
        }

        SectionState localSection = null;
        if (ModList.get().isLoaded("create")) {
            localSection = CreateCompat.readTrackSignalBelow(level, worldPosition, MAX_SCAN_DISTANCE);
        }

        // Kombinationslogik (per Nutzervorgabe): das Gleissignal unter dem Block entscheidet
        // ausschließlich über Halt/nicht-Halt. "Halt erwarten" tritt NUR ein, wenn das per
        // Display Link gebundene Signal davor Halt zeigt - ein lokales "Vorsicht"/YELLOW
        // allein löst es nicht mehr aus.
        CombinedSignalAspect newAspect;
        if (localSection == SectionState.HALT) {
            newAspect = CombinedSignalAspect.HALT;
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
        if (linkedSignalPos != null) {
            tag.put("LinkedSignal", NbtUtils.writeBlockPos(linkedSignalPos));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        upstreamHalt = tag.getBoolean("UpstreamHalt");
        linkedSignalPos = tag.contains("LinkedSignal") ? NbtUtils.readBlockPos(tag, "LinkedSignal").orElse(null) : null;
    }
}
