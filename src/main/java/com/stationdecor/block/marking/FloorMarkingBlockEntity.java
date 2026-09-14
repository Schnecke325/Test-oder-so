package com.stationdecor.block.marking;

import com.stationdecor.block.rotation.AbstractRotatableBlockEntity;
import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity der Bodenmarkierung. Neben der geerbten freien Rotation wird
 * zusätzlich ein Nah/Mitte/Fern-Versatz gespeichert, der beim Platzieren über
 * die 3 Zonen der Vorschau-Outline gewählt wird (siehe {@link FloorMarkingBlockItem}),
 * sowie die aktuelle Farbe (per Rechtsklick mit Farbstoff änderbar, siehe
 * {@link FloorMarkingBlock#useItemOn}).
 */
public class FloorMarkingBlockEntity extends AbstractRotatableBlockEntity {

    /** Versatz in Dritteln eines Blocks entlang der eigenen (gedrehten) Vorwärtsachse. */
    public static final float OFFSET_STEP = 1f / 3f;

    private int offsetIndex = 0;
    private DyeColor color = DyeColor.YELLOW;

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

    public DyeColor getColor() {
        return color;
    }

    /** Setzt die Farbe, speichert die Änderung und synchronisiert sie zu allen Clients in der Nähe. */
    public void setColor(DyeColor color) {
        this.color = color;
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("OffsetIndex", offsetIndex);
        tag.putString("Color", color.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.offsetIndex = Math.max(-1, Math.min(1, tag.getInt("OffsetIndex")));
        this.color = DyeColor.byName(tag.getString("Color"), DyeColor.YELLOW);
    }
}
