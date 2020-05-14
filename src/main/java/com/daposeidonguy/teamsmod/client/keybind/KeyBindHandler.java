package com.daposeidonguy.teamsmod.client.keybind;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.lwjgl.glfw.GLFW;

public class KeyBindHandler {

    public static boolean doDisplayStatus = true;
    public static boolean doDisplayCompass = true;
    public static KeyBinding showStatus;
    public static KeyBinding showCompass;
    public static KeyBinding acceptInvite;
    public static KeyBinding switchChat;

    public static void register() {
        if (EffectiveSide.get().isClient()) {
            TeamsMod.logger.info("Registering keybindings...");
            showStatus = new KeyBinding("teamsmod.keyhud.desc", GLFW.GLFW_KEY_V, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(showStatus);
            showCompass = new KeyBinding("teamsmod.keycompass.desc", GLFW.GLFW_KEY_M, "key.categories.multilpayer");
            ClientRegistry.registerKeyBinding(showCompass);
            acceptInvite = new KeyBinding("teamsmod.keyaccept.desc", GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(acceptInvite);
            switchChat = new KeyBinding("teamsmod.keyswitch.desc", GLFW.GLFW_KEY_R, "key.categories.multiplayer");
        }
    }


}
