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
    public static final ModelResourceLocation BVG = standalone("block/obj_display_render_bvg");
    public static final ModelResourceLocation BEWEGT = standalone("block/obj_display_render_bewegt");
    public static final ModelResourceLocation GOAHEAD = standalone("block/obj_display_render_goahead");
    public static final ModelResourceLocation RMV = standalone("block/obj_display_render_rmv");
    public static final ModelResourceLocation VVR = standalone("block/obj_display_render_vvr");

    private TicketMachineModels() {
    }

    private static ModelResourceLocation standalone(String path) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, path));
    }
}
