package com.daposeidonguy.teamsmod.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.lwjgl.glfw.GLFW;


public class KeyBindings {
    public static KeyBinding hud;
    public static KeyBinding accept;

    public static void register() {
        if (EffectiveSide.get().isClient()) {
            hud = new KeyBinding("Display Teams HUD", GLFW.GLFW_KEY_V, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(hud);
            accept = new KeyBinding("Accept Team Invite", GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(accept);
        }
    }
}
