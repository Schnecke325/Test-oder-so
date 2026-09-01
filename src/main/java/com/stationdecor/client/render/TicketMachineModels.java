package com.stationdecor.client.render;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.obj.TicketMachineStyle;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Standalone-Modellreferenzen (siehe {@code ModelEvent.RegisterAdditional} in
 * {@link com.stationdecor.client.ClientSetup}) für die Fahrkartenautomat-
 * Varianten, siehe {@link TicketMachineStyle}. Weitere Varianten: hier einen
 * Eintrag ergänzen und in {@link ObjDisplayBlockEntityRenderer#modelFor}
 * verdrahten.
 */
public final class TicketMachineModels {

    public static final ModelResourceLocation DB = standalone("block/obj_display_render_db");

    private TicketMachineModels() {
    }

    private static ModelResourceLocation standalone(String path) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, path));
    }
}
