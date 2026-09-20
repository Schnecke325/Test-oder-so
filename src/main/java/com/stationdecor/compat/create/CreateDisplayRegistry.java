package com.stationdecor.compat.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateRegistries;
import com.stationdecor.StationDecorMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreateDisplayRegistry {

    private static final DeferredRegister<DisplaySource> DISPLAY_SOURCES =
            DeferredRegister.create(CreateRegistries.DISPLAY_SOURCE, StationDecorMod.MOD_ID);
    private static final DeferredRegister<DisplayTarget> DISPLAY_TARGETS =
            DeferredRegister.create(CreateRegistries.DISPLAY_TARGET, StationDecorMod.MOD_ID);

    public static final DeferredHolder<DisplaySource, KsMainSignalDisplaySource> KS_MAIN_SIGNAL_SOURCE =
            DISPLAY_SOURCES.register("ks_main_signal_aspect", KsMainSignalDisplaySource::new);
    public static final DeferredHolder<DisplaySource, KsMultiSectionSignalDisplaySource> KS_MULTI_SECTION_SIGNAL_SOURCE =
            DISPLAY_SOURCES.register("ks_multi_section_signal_aspect", KsMultiSectionSignalDisplaySource::new);

    public static final DeferredHolder<DisplayTarget, KsMainSignalDisplayTarget> KS_MAIN_SIGNAL_TARGET =
            DISPLAY_TARGETS.register("ks_main_signal", KsMainSignalDisplayTarget::new);
    public static final DeferredHolder<DisplayTarget, KsDistantSignalDisplayTarget> KS_DISTANT_SIGNAL_TARGET =
            DISPLAY_TARGETS.register("ks_distant_signal", KsDistantSignalDisplayTarget::new);
    public static final DeferredHolder<DisplayTarget, KsMultiSectionSignalDisplayTarget> KS_MULTI_SECTION_SIGNAL_TARGET =
            DISPLAY_TARGETS.register("ks_multi_section_signal", KsMultiSectionSignalDisplayTarget::new);

    private CreateDisplayRegistry() {
    }

    public static void register(IEventBus modEventBus) {
        DISPLAY_SOURCES.register(modEventBus);
        DISPLAY_TARGETS.register(modEventBus);
    }
}
