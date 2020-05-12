package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientHandler {

    public static final Map<UUID, String> idtoNameMap = new HashMap<>();
    public static final Map<String, UUID> nametoIdMap = new HashMap<>();
    public static final Minecraft mc = Minecraft.getInstance();
    public static final MainWindow window = ClientHandler.mc.getMainWindow();
    public static long ticks = 0;

    @SubscribeEvent
    public static void onTick(final TickEvent.ClientTickEvent event) {
        ticks += 1;
    }

    /* Returns username of player given UUID if online, null otherwise */
    public static String getOnlineUsernameFromUUID(final UUID uuid) {
        String playerName = idtoNameMap.get(uuid);
        if (playerName == null) {
            playerName = UsernameCache.getLastKnownUsername(uuid);
        }
        if (playerName == null) {
            try {
                playerName = ClientHandler.mc.getConnection().getPlayerInfo(uuid).getGameProfile().getName();
            } catch (NullPointerException ignored) {
            }
        }
        return playerName;
    }

    /* Returns username of player given UUID */
    public static String getUsernameFromUUID(final UUID uuid) {
        String playerName = getOnlineUsernameFromUUID(uuid);
        if (playerName == null) {
            String uuidString = uuid.toString().replace("-", "");
            String url = "https://api.mojang.com/user/profiles/" + uuidString + "/names";
            try {
                String nameJson = IOUtils.toString(new URL(url), "ANSI");
                JsonArray jsonArray = new JsonParser().parse(nameJson).getAsJsonArray();
                playerName = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
            } catch (Exception ex) {
                playerName = I18n.format("teamsmod.unknownplayer");
            }
        }
        return playerName;
    }
}
