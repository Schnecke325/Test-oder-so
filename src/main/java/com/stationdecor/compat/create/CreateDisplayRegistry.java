package com.stationdecor.compat.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateRegistries;
import com.stationdecor.StationDecorMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registriert unsere {@link DisplaySource}/{@link DisplayTarget}-Implementierungen
 * als echte Einträge in Creates eigenen Registries ({@link CreateRegistries#DISPLAY_SOURCE}/
 * {@link CreateRegistries#DISPLAY_TARGET}).
 * <p>
 * Das ist zwingend notwendig, nicht nur optional: Create identifiziert eine
 * {@link DisplaySource}/{@link DisplayTarget}-Instanz über ihre Registry-ID
 * (siehe {@code DisplaySource#getId()}/{@code #getName()}). Ohne echte
 * Registrierung liefert {@code getId()} {@code null}, wodurch
 * {@code getName()} beim Öffnen des Display-Link-Bildschirms (Aufbau der
 * Quellen-Auswahlliste) mit einer NullPointerException abstürzt bzw. unsere
 * Quelle dort gar nicht erst sauber auswählbar ist - das war der eigentliche
 * Grund, warum das Ks-Vorsignal (rein Display-Link-gesteuert) nie
 * aktualisiert wurde: die eigene Signal-Quelle ließ sich im Spiel nie
 * wirklich als aktive Quelle auswählen. Die reine Block-Zuordnung über
 * {@code DisplayTarget.BY_BLOCK}/{@code DisplaySource.BY_BLOCK} (siehe
 * {@link CreateCompat#register()}) ist davon unabhängig und bleibt zusätzlich
 * nötig.
 * <p>
 * Muss bereits im Mod-Konstruktor an den Mod-Event-Bus gehängt werden (nicht
 * erst in {@code FMLCommonSetupEvent}), da NeoForge Registry-Einträge über
 * das reguläre {@code RegisterEvent} sammelt, das vor Common Setup feuert.
 */
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
