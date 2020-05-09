package com.daposeidonguy.teamsmod.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;


public class Keybind {
    public static KeyBinding hud;
    public static KeyBinding accept;

    public static void register() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            hud = new KeyBinding("Display Teams HUD", Keyboard.KEY_V, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(hud);
            accept = new KeyBinding("Accept Team Invite", Keyboard.KEY_RBRACKET, "key.categories.multiplayer");
            ClientRegistry.registerKeyBinding(accept);
        }
    }
}