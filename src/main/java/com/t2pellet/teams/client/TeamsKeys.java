package com.t2pellet.teams.client;

import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.client.ui.toast.ToastInvited;
import com.t2pellet.teams.client.ui.toast.ToastRequested;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamJoinPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class TeamsKeys {

    public static class TeamsKey {
        @FunctionalInterface
        public interface OnPress {
            void execute(MinecraftClient client);
        }

        private TeamsKey(String keyName, int keyBind, OnPress action) {
            keyBinding = new KeyBinding(
                    keyName,
                    InputUtil.Type.KEYSYM,
                    keyBind,
                    "key.category.teams"
            );
            onPress = action;
        }

        public void register() {
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }

        public String getLocalizedName() {
            return keyBinding.getBoundKeyLocalizedText().asString();
        }

        final KeyBinding keyBinding;
        final OnPress onPress;
    }

    public static final TeamsKey ACCEPT = new TeamsKey("key.teams.accept", GLFW.GLFW_KEY_RIGHT_BRACKET, client -> {
        var toastManager = client.getToastManager();
        ToastInvited invited = toastManager.getToast(ToastInvited.class, Toast.TYPE);
        if (invited != null) {
            invited.respond();
            PacketHandler.INSTANCE.sendToServer(new TeamJoinPacket(client.player.getUuid(), invited.team));
        } else {
            ToastRequested requested = toastManager.getToast(ToastRequested.class, Toast.TYPE);
            if (requested != null) {
                requested.respond();
                PacketHandler.INSTANCE.sendToServer(new TeamJoinPacket(requested.id, ClientTeam.INSTANCE.getName()));
            }
        }
    });

    public static final TeamsKey REJECT = new TeamsKey("key.teams.reject", GLFW.GLFW_KEY_LEFT_BRACKET, client -> {
        var toastManager = client.getToastManager();
        ToastInvited toast = toastManager.getToast(ToastInvited.class, Toast.TYPE);
        if (toast != null) {
            toast.respond();
        } else {
            ToastRequested requested = toastManager.getToast(ToastRequested.class, Toast.TYPE);
            if (requested != null) {
                requested.respond();
            }
        }
    });

    public static final TeamsKey TOGGLE_HUD = new TeamsKey("key.teams.toggle_hud", GLFW.GLFW_KEY_B, client -> {
        TeamsModClient.compass.enabled = !TeamsModClient.compass.enabled;
        TeamsModClient.status.enabled = !TeamsModClient.status.enabled;
    });

    static final TeamsKey[] KEYS = {
            ACCEPT,
            REJECT,
            TOGGLE_HUD
    };

}
