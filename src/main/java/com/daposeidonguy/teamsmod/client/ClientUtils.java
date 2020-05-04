package com.daposeidonguy.teamsmod.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.UsernameCache;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class ClientUtils {

    // returns null if player is offline
    public static String getOnlineUsernameFromUUID(UUID uuid) {
        String playerName = ClientEventHandler.idtoNameMap.get(uuid);
        if (playerName == null) {
            playerName = UsernameCache.getLastKnownUsername(uuid);
        }
        if (playerName == null) {
            try {
                playerName = Minecraft.getInstance().getConnection().getPlayerInfo(uuid).getGameProfile().getName();
            } catch (NullPointerException ex) {
            }
        }
        return playerName;
    }

    public static String getUsernameFromUUID(UUID uuid) {
        String playerName = getOnlineUsernameFromUUID(uuid);
        if (playerName == null) {
            String uuidString = uuid.toString().replace("-", "");
            String url = "https://api.mojang.com/user/profiles/" + uuidString + "/names";
            try {
                String nameJson = IOUtils.toString(new URL(url));
                JsonArray jsonArray = new JsonParser().parse(nameJson).getAsJsonArray();
                playerName = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
            } catch (IOException e) {
                playerName = "Unknown player";
            }
        }
        return playerName;
    }
}
