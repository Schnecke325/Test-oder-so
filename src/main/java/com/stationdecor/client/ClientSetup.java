package com.stationdecor.client;

import com.stationdecor.StationDecorMod;
import com.stationdecor.client.render.FloorMarkingBlockEntityRenderer;
import com.stationdecor.client.render.ObjDisplayBlockEntityRenderer;
import com.stationdecor.client.render.SeatBlockEntityRenderer;
import com.stationdecor.client.render.SeatEntityRenderer;
import com.stationdecor.client.screen.ObjDisplayScreen;
import com.stationdecor.registry.ModBlockEntities;
import com.stationdecor.registry.ModEntities;
import com.stationdecor.registry.ModMenus;
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
        event.register(ObjDisplayBlockEntityRenderer.MODEL);
        event.register(SeatBlockEntityRenderer.MODEL);
        event.register(FloorMarkingBlockEntityRenderer.MODEL);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.OBJ_DISPLAY.get(), ObjDisplayBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SEAT.get(), SeatBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLOOR_MARKING.get(), FloorMarkingBlockEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.SEAT.get(), SeatEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.OBJ_DISPLAY_MENU.get(), ObjDisplayScreen::new);
    }
}
