package com.stationdecor.registry;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.marking.FloorMarkingBlock;
import com.stationdecor.block.obj.ObjDisplayBlock;
import com.stationdecor.block.seat.SeatBlock;
import com.stationdecor.block.signal.KsDistantSignalBlock;
import com.stationdecor.block.signal.KsMainSignalBlock;
import com.stationdecor.block.signal.KsMultiSectionSignalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(StationDecorMod.MOD_ID);

    public static final DeferredBlock<ObjDisplayBlock> OBJ_DISPLAY = BLOCKS.register("obj_display",
            () -> new ObjDisplayBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final DeferredBlock<SeatBlock> SEAT = BLOCKS.register("seat",
            () -> new SeatBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<FloorMarkingBlock> FLOOR_MARKING = BLOCKS.register("floor_marking",
            () -> new FloorMarkingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.5f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .noCollission()));

    public static final DeferredBlock<KsMainSignalBlock> KS_MAIN_SIGNAL = BLOCKS.register("ks_main_signal",
            () -> new KsMainSignalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(state -> 10)));

    public static final DeferredBlock<KsDistantSignalBlock> KS_DISTANT_SIGNAL = BLOCKS.register("ks_distant_signal",
            () -> new KsDistantSignalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(state -> 10)));

    public static final DeferredBlock<KsMultiSectionSignalBlock> KS_MULTI_SECTION_SIGNAL = BLOCKS.register("ks_multi_section_signal",
            () -> new KsMultiSectionSignalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(state -> 10)));

    private ModBlocks() {
    }
}
