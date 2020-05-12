package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.NewChatGui;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;

public class GuiHandler {
    public static final Map<UUID, Integer> hungerMap = new HashMap<>();
    public static final Map<UUID, Integer> healthMap = new HashMap<>();
    public static final Map<String, Pair<String, Long>> chatMap = new HashMap<>();
    public static final List<UUID> priorityPlayers = new ArrayList<>();
    public static boolean displayTeamChat = false;
    public static NewChatGui backupChatGUI = new NewChatGui(ClientHandler.mc);
    public static final Field persistentChatGUI = ObfuscationReflectionHelper.findField(IngameGui.class, "field_73840_e");
}
