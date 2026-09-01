package com.stationdecor.client.render;

import com.stationdecor.StationDecorMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Standalone-Modellreferenzen (siehe {@code ModelEvent.RegisterAdditional} in
 * {@link com.stationdecor.client.ClientSetup}) für die Mast-/Signalkopf-Modelle
 * der drei Ks-Signalblöcke - dieselben vanilla Mehrelement-Blockmodell-JSONs,
 * die auch für die Item-Icons genutzt werden, hier aber zusätzlich standalone
 * geladen, damit {@link RotatableSignalRenderer} sie frei gedreht per
 * BlockEntityRenderer zeichnen kann (statt fest über das Blockmodell-System).
 */
public final class SignalModels {

    public static final ModelResourceLocation KS_MAIN_SIGNAL_HP0 = standalone("block/ks_main_signal_hp0");
    public static final ModelResourceLocation KS_MAIN_SIGNAL_HP1 = standalone("block/ks_main_signal_hp1");
    public static final ModelResourceLocation KS_MAIN_SIGNAL_HP2 = standalone("block/ks_main_signal_hp2");

    public static final ModelResourceLocation KS_DISTANT_SIGNAL_VR0 = standalone("block/ks_distant_signal_vr0");
    public static final ModelResourceLocation KS_DISTANT_SIGNAL_VR1 = standalone("block/ks_distant_signal_vr1");
    public static final ModelResourceLocation KS_DISTANT_SIGNAL_VR2 = standalone("block/ks_distant_signal_vr2");

    public static final ModelResourceLocation KS_MULTI_SECTION_SIGNAL_FAHRT = standalone("block/ks_multi_section_signal_fahrt");
    public static final ModelResourceLocation KS_MULTI_SECTION_SIGNAL_HALT = standalone("block/ks_multi_section_signal_halt");
    public static final ModelResourceLocation KS_MULTI_SECTION_SIGNAL_HALT_ERWARTEN = standalone("block/ks_multi_section_signal_halt_erwarten");

    private SignalModels() {
    }

    private static ModelResourceLocation standalone(String path) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, path));
    }
}
