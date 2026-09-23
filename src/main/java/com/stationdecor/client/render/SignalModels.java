package com.stationdecor.client.render;

import com.stationdecor.StationDecorMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public final class SignalModels {

    public static final ModelResourceLocation KS_MAIN_SIGNAL = standalone("block/ks_main_signal_render");
    public static final ModelResourceLocation KS_DISTANT_SIGNAL = standalone("block/ks_distant_signal_render");
    public static final ModelResourceLocation KS_DISTANT_SIGNAL_REPEATER = standalone("block/ks_distant_signal_repeater_render");
    public static final ModelResourceLocation KS_MULTI_SECTION_SIGNAL = standalone("block/ks_multi_section_signal_render");

    private SignalModels() {
    }

    private static ModelResourceLocation standalone(String path) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, path));
    }
}
