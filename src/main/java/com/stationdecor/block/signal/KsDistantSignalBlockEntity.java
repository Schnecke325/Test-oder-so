package com.stationdecor.block.signal;

import com.stationdecor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rein technische BlockEntity ohne eigene Tick-Logik - wird nur benötigt,
 * damit {@code SignalAspectLampRenderer} (siehe client.render) die
 * Lampenfläche des Vorsignals unabhängig vom Umgebungslicht zeichnen kann.
 * Der Signalbegriff selbst bleibt eine BlockState-Property und wird
 * weiterhin ausschließlich per Rechtsklick oder Create Display Link gesetzt.
 */
public class KsDistantSignalBlockEntity extends BlockEntity {

    public KsDistantSignalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KS_DISTANT_SIGNAL.get(), pos, state);
    }
}
