package com.stationdecor.client.render;

import com.stationdecor.StationDecorMod;
import net.minecraft.resources.ResourceLocation;

public enum LampColor {
    RED("signal_glow", 220, 25, 10),
    YELLOW("signal_glow_2", 220, 195, 10),
    GREEN("signal_glow_3", 190, 190, 190);

    private final ResourceLocation sprite;
    private final int flareRed;
    private final int flareGreen;
    private final int flareBlue;

    LampColor(String textureName, int flareRed, int flareGreen, int flareBlue) {
        this.sprite = ResourceLocation.fromNamespaceAndPath(StationDecorMod.MOD_ID, "block/" + textureName);
        this.flareRed = flareRed;
        this.flareGreen = flareGreen;
        this.flareBlue = flareBlue;
    }

    public ResourceLocation sprite() {
        return sprite;
    }

    public int flareRed() {
        return flareRed;
    }

    public int flareGreen() {
        return flareGreen;
    }

    public int flareBlue() {
        return flareBlue;
    }
}
