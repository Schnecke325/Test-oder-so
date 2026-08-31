package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.marking.FloorMarkingBlockEntity;
import com.stationdecor.block.obj.ObjDisplayBlockEntity;
import com.stationdecor.block.seat.SeatBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, StationDecorMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ObjDisplayBlockEntity>> OBJ_DISPLAY =
            BLOCK_ENTITIES.register("obj_display", () -> BlockEntityType.Builder.of(
                    ObjDisplayBlockEntity::new, ModBlocks.OBJ_DISPLAY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeatBlockEntity>> SEAT =
            BLOCK_ENTITIES.register("seat", () -> BlockEntityType.Builder.of(
                    SeatBlockEntity::new, ModBlocks.SEAT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FloorMarkingBlockEntity>> FLOOR_MARKING =
            BLOCK_ENTITIES.register("floor_marking", () -> BlockEntityType.Builder.of(
                    FloorMarkingBlockEntity::new, ModBlocks.FLOOR_MARKING.get()).build(null));

    private ModBlockEntities() {
    }
}
