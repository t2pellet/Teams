package com.daposeidonguy.teamsmod.client.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.lwjgl.glfw.GLFW;

public class KeyBindHandler {

    public static boolean doDisplayHud = true;
    public static KeyBinding showHud;
    public static KeyBinding acceptInvite;

    public static void register() {
        if (EffectiveSide.get().isClient()) {
            showHud = new KeyBinding("Display Teams HUD", GLFW.GLFW_KEY_V, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(showHud);
            acceptInvite = new KeyBinding("Accept Team Invite", GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(acceptInvite);
        }
    }
}
