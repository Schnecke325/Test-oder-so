package com.stationdecor.client;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.signal.KsDistantSignalBlock;
import com.stationdecor.client.render.FloorMarkingBlockEntityRenderer;
import com.stationdecor.client.render.FloorMarkingModels;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = StationDecorMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(TicketMachineModels.DB);
        event.register(TicketMachineModels.BVG);
        event.register(TicketMachineModels.BEWEGT);
        event.register(TicketMachineModels.GOAHEAD);
        event.register(TicketMachineModels.RMV);
        event.register(TicketMachineModels.VVR);
        event.register(SeatBlockEntityRenderer.MODEL);
        for (var model : FloorMarkingModels.all()) {
            event.register(model);
        }

        event.register(SignalModels.KS_MAIN_SIGNAL);
        event.register(SignalModels.KS_DISTANT_SIGNAL);
        event.register(SignalModels.KS_DISTANT_SIGNAL_REPEATER);
        event.register(SignalModels.KS_MULTI_SECTION_SIGNAL);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.OBJ_DISPLAY.get(), ObjDisplayBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SEAT.get(), SeatBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLOOR_MARKING.get(), FloorMarkingBlockEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.SEAT.get(), SeatEntityRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.KS_MAIN_SIGNAL.get(), context -> new RotatableSignalRenderer<>(
                state -> SignalModels.KS_MAIN_SIGNAL));

        event.registerBlockEntityRenderer(ModBlockEntities.KS_DISTANT_SIGNAL.get(), context -> new RotatableSignalRenderer<>(
                state -> state.getBlock() instanceof KsDistantSignalBlock block && block.isRepeater()
                        ? SignalModels.KS_DISTANT_SIGNAL_REPEATER
                        : SignalModels.KS_DISTANT_SIGNAL));

        event.registerBlockEntityRenderer(ModBlockEntities.KS_MULTI_SECTION_SIGNAL.get(), context -> new RotatableSignalRenderer<>(
                state -> SignalModels.KS_MULTI_SECTION_SIGNAL));
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.OBJ_DISPLAY_MENU.get(), ObjDisplayScreen::new);
    }
}
