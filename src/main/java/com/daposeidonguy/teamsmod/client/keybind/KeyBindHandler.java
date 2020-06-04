package com.daposeidonguy.teamsmod.client.keybind;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindHandler {

    public static boolean doDisplayStatus = true;
    public static boolean doDisplayCompass = true;
    public static KeyBinding showStatus;
    public static KeyBinding showCompass;
    public static KeyBinding acceptInvite;
    public static KeyBinding switchChat;

    public static void register() {
        showStatus = new KeyBinding("teamsmod.keyhud.desc", Keyboard.KEY_V, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(showStatus);
        showCompass = new KeyBinding("teamsmod.keycompass.desc", Keyboard.KEY_M, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(showCompass);
        acceptInvite = new KeyBinding("teamsmod.keyaccept.desc", Keyboard.KEY_RBRACKET, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(acceptInvite);
        switchChat = new KeyBinding("teamsmod.keyswitch.desc", Keyboard.KEY_R, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(switchChat);
        TeamsMod.logger.info("Teams: Registered keybindings");
    }


}
