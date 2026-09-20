package com.stationdecor.client;

import com.stationdecor.StationDecorMod;
import com.stationdecor.block.marking.FloorMarkingBlock;
import com.stationdecor.block.obj.ObjDisplayBlock;
import com.stationdecor.block.rotation.RotationUtil;
import com.stationdecor.block.seat.SeatBlock;
import com.stationdecor.block.signal.KsDistantSignalBlock;
import com.stationdecor.block.signal.KsMainSignalBlock;
import com.stationdecor.block.signal.KsMultiSectionSignalBlock;
import com.stationdecor.config.StationDecorConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = StationDecorMod.MOD_ID, value = Dist.CLIENT)
public final class RotationHudOverlay {

    private RotationHudOverlay() {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }

        int steps = rotationStepsFor(player.getMainHandItem());
        if (steps <= 0) {
            steps = rotationStepsFor(player.getOffhandItem());
        }
        if (steps <= 0) {
            return;
        }

        float degrees = RotationUtil.indexToDegrees(RotationUtil.snapToIndex(player.getYRot(), steps), steps);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Component text = Component.translatable("hud.station_decor.rotation", Math.round(degrees));
        guiGraphics.drawCenteredString(minecraft.font, text, guiGraphics.guiWidth() / 2, guiGraphics.guiHeight() - 59, 0xFFFFFF);
    }

    private static int rotationStepsFor(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return -1;
        }
        Block block = blockItem.getBlock();
        if (block instanceof ObjDisplayBlock) {
            return StationDecorConfig.OBJ_BLOCK_ROTATION_STEPS.get();
        }
        if (block instanceof SeatBlock) {
            return StationDecorConfig.SEAT_BLOCK_ROTATION_STEPS.get();
        }
        if (block instanceof FloorMarkingBlock) {
            return StationDecorConfig.FLOOR_MARKING_ROTATION_STEPS.get();
        }
        if (block instanceof KsMainSignalBlock || block instanceof KsDistantSignalBlock || block instanceof KsMultiSectionSignalBlock) {
            return StationDecorConfig.SIGNAL_ROTATION_STEPS.get();
        }
        return -1;
    }
}
