package com.stationdecor.client.render;

import com.stationdecor.StationDecorMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.Map;

public final class FloorMarkingModels {

    private static final Map<DyeColor, ModelResourceLocation> MODELS = buildModels();

    private FloorMarkingModels() {
    }

    private static Map<DyeColor, ModelResourceLocation> buildModels() {
        Map<DyeColor, ModelResourceLocation> models = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            models.put(color, ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(
                    StationDecorMod.MOD_ID, "block/platform_border_narrow_" + color.getSerializedName())));
        }
        return models;
    }

    public static ModelResourceLocation forColor(DyeColor color) {
        return MODELS.get(color);
    }

    public static Iterable<ModelResourceLocation> all() {
        return MODELS.values();
    }
}
