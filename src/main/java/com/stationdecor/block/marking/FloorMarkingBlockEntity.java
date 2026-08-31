package com.stationdecor.block.marking;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity der Bodenmarkierung. Neben der geerbten freien Rotation wird
 * zusätzlich ein Nah/Mitte/Fern-Versatz gespeichert, der beim Platzieren über
 * die 3 Zonen der Vorschau-Outline gewählt wird (siehe {@link FloorMarkingBlockItem}).
 */
public class FloorMarkingBlockEntity extends AbstractRotatableBlockEntity {

    /** Versatz in Dritteln eines Blocks entlang der eigenen (gedrehten) Vorwärtsachse. */
    public static final float OFFSET_STEP = 1f / 3f;

    private int offsetIndex = 0;

    public FloorMarkingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLOOR_MARKING.get(), pos, state);
    }

    /**
     * Setzt Rotation und Nah/Mitte/Fern-Versatz gemeinsam (ein einziger Sync statt zwei).
     */
    public void setPlacement(int rotationIndex, int rotationSteps, int offsetIndex) {
        this.offsetIndex = Math.max(-1, Math.min(1, offsetIndex));
        setRotation(rotationIndex, rotationSteps);
    }

    public int getOffsetIndex() {
        return offsetIndex;
    }

    /** Versatz entlang der eigenen Vorwärtsachse in Blöcken, z.B. -1/3, 0 oder +1/3. */
    public float getOffsetDistance() {
        return offsetIndex * OFFSET_STEP;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("OffsetIndex", offsetIndex);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.offsetIndex = Math.max(-1, Math.min(1, tag.getInt("OffsetIndex")));
    }
}
