package com.stationdecor.client.render;

import com.stationdecor.StationDecorMod;
import net.minecraft.resources.ResourceLocation;

public enum LampColor {
    RED("signal_glow"),
    YELLOW("signal_glow_2"),
    GREEN("signal_glow_3");

    private final ResourceLocation sprite;

    LampColor(String textureName) {
        this.sprite = ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, "block/" + textureName);
    }

    public ResourceLocation sprite() {
        return sprite;
    }
}
