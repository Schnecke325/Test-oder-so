package com.stationdecor.client;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.signal.KsDistantSignalBlock;
import com.stationdecor.block.signal.KsMainSignalBlock;
import com.stationdecor.block.signal.KsMultiSectionSignalBlock;
import com.stationdecor.client.render.FloorMarkingBlockEntityRenderer;
import com.stationdecor.client.render.ObjDisplayBlockEntityRenderer;
import com.stationdecor.client.render.RotatableSignalRenderer;
import com.stationdecor.client.render.SeatBlockEntityRenderer;
import com.stationdecor.client.render.SeatEntityRenderer;
import com.stationdecor.client.render.SignalModels;
import com.stationdecor.client.render.TicketMachineModels;
import com.stationdecor.client.screen.ObjDisplayScreen;
import com.stationdecor.registry.ModBlockEntities;
import com.stationdecor.registry.ModEntities;
import com.stationdecor.registry.ModMenus;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-seitige Registrierungen. Über {@code Dist.CLIENT} sorgt NeoForge
 * dafür, dass diese Klasse (und damit alle client-only Klassen wie Screens
 * und Renderer) auf dem dedizierten Server gar nicht erst geladen wird.
 */
@EventBusSubscriber(modid = StationDecorMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(TicketMachineModels.DB);
        event.register(SeatBlockEntityRenderer.MODEL);
        event.register(FloorMarkingBlockEntityRenderer.MODEL);

        event.register(SignalModels.KS_MAIN_SIGNAL_HP0);
        event.register(SignalModels.KS_MAIN_SIGNAL_HP1);
        event.register(SignalModels.KS_MAIN_SIGNAL_HP2);
        event.register(SignalModels.KS_DISTANT_SIGNAL_VR0);
        event.register(SignalModels.KS_DISTANT_SIGNAL_VR1);
        event.register(SignalModels.KS_DISTANT_SIGNAL_VR2);
        event.register(SignalModels.KS_MULTI_SECTION_SIGNAL_FAHRT);
        event.register(SignalModels.KS_MULTI_SECTION_SIGNAL_HALT);
        event.register(SignalModels.KS_MULTI_SECTION_SIGNAL_HALT_ERWARTEN);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.OBJ_DISPLAY.get(), ObjDisplayBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SEAT.get(), SeatBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLOOR_MARKING.get(), FloorMarkingBlockEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.SEAT.get(), SeatEntityRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.KS_MAIN_SIGNAL.get(), context -> new RotatableSignalRenderer<>(
                state -> switch (state.getValue(KsMainSignalBlock.ASPECT)) {
                    case HP0 -> SignalModels.KS_MAIN_SIGNAL_HP0;
                    case HP1 -> SignalModels.KS_MAIN_SIGNAL_HP1;
                    case HP2 -> SignalModels.KS_MAIN_SIGNAL_HP2;
                },
                state -> lampTexture("ks_main_signal", state.getValue(KsMainSignalBlock.ASPECT).getSerializedName())));

        event.registerBlockEntityRenderer(ModBlockEntities.KS_DISTANT_SIGNAL.get(), context -> new RotatableSignalRenderer<>(
                state -> switch (state.getValue(KsDistantSignalBlock.ASPECT)) {
                    case VR0 -> SignalModels.KS_DISTANT_SIGNAL_VR0;
                    case VR1 -> SignalModels.KS_DISTANT_SIGNAL_VR1;
                    case VR2 -> SignalModels.KS_DISTANT_SIGNAL_VR2;
                },
                state -> lampTexture("ks_distant_signal", state.getValue(KsDistantSignalBlock.ASPECT).getSerializedName())));

        event.registerBlockEntityRenderer(ModBlockEntities.KS_MULTI_SECTION_SIGNAL.get(), context -> new RotatableSignalRenderer<>(
                state -> switch (state.getValue(KsMultiSectionSignalBlock.ASPECT)) {
                    case FAHRT -> SignalModels.KS_MULTI_SECTION_SIGNAL_FAHRT;
                    case HALT -> SignalModels.KS_MULTI_SECTION_SIGNAL_HALT;
                    case HALT_ERWARTEN -> SignalModels.KS_MULTI_SECTION_SIGNAL_HALT_ERWARTEN;
                },
                state -> lampTexture("ks_multi_section_signal", state.getValue(KsMultiSectionSignalBlock.ASPECT).getSerializedName())));
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.OBJ_DISPLAY_MENU.get(), ObjDisplayScreen::new);
    }

    /**
     * {@code RenderType.entityCutout(...)} bindet die Textur direkt über den
     * TextureManager (nicht über das Block-Atlas) - anders als in
     * Blockmodell-JSONs muss der Pfad hier bereits das volle
     * "textures/..."-Präfix und die ".png"-Endung enthalten, siehe z.B.
     * {@link SeatEntityRenderer}. Ohne das liefert der TextureManager keine
     * gültige Textur und es wird das lila/schwarze "Missing Texture"-Muster
     * angezeigt (genau der Bug, der hier gefixt wurde).
     */
    private static ResourceLocation lampTexture(String blockName, String aspectName) {
        return ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID,
                "textures/block/" + blockName + "_" + aspectName + ".png");
    }
}
