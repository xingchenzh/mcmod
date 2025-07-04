package com.example.autoaim;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = AutoAimMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    private static final Minecraft mc = Minecraft.getInstance();
    private static boolean autoAimEnabled = false;
    
    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_R && event.getAction() == GLFW.GLFW_PRESS) {
            autoAimEnabled = !autoAimEnabled;
        }
    }

    public static boolean isAutoAimEnabled() {
        return autoAimEnabled && mc.player != null;
    }
}