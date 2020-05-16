package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;

public class GuiHandler {
    public static final Map<UUID, Integer> hungerMap = new HashMap<>();
    public static final Map<UUID, Integer> healthMap = new HashMap<>();
    public static final Map<String, Pair<String, Long>> chatMap = new HashMap<>();
    public static final List<UUID> priorityPlayers = new ArrayList<>();
    public static final Field persistentChatGUI = ObfuscationReflectionHelper.findField(GuiIngame.class, "field_73840_e");
    public static final int BUTTON_GUI = Integer.MIN_VALUE;
    public static final int BUTTON_CHAT = Integer.MIN_VALUE + 1;
    public static final int BUTTON_BACK = Integer.MIN_VALUE + 2;
    public static final int BUTTON_MANAGE = Integer.MIN_VALUE + 3;
    public static final int BUTTON_TEAMLIST = Integer.MIN_VALUE + 4;
    public static final int BUTTON_TRANSFERLIST = Integer.MIN_VALUE + 5;
    public static final int BUTTON_HUD = Integer.MIN_VALUE + 6;
    public static final int BUTTON_PREVPAGE = Integer.MIN_VALUE + 7;
    public static final int BUTTON_NEXTPAGE = Integer.MIN_VALUE + 8;
    public static final int TEXT_FIELD = Integer.MIN_VALUE + 9;
    public static final int BUTTON_PRIORITY = Integer.MIN_VALUE + 10;
    public static final int BUTTON_SYNC = Integer.MIN_VALUE + 11;
    public static final int BUTTON_FF = Integer.MIN_VALUE + 12;
    public static final int BUTTON_CREATE = Integer.MIN_VALUE + 13;
    public static final int BUTTON_INVITE = Integer.MIN_VALUE + 14;
    public static final int BUTTON_PLAYERNAME = Integer.MIN_VALUE + 15;
    public static final int BUTTON_KICK = Integer.MIN_VALUE + 16;
    public static final int BUTTON_TEAMPLAYERS = Integer.MIN_VALUE + 17;
    public static final int BUTTON_TRANSFER = Integer.MIN_VALUE + 18;
    public static final int BUTTON_INVITEPLAYERS = Integer.MIN_VALUE + 19;
    public static final int BUTTON_KICKPLAYERS = Integer.MIN_VALUE + 20;
    public static final int BUTTON_CONFIG = Integer.MIN_VALUE + 21;
    public static final int BUTTON_LEAVE = Integer.MIN_VALUE + 22;
    public static boolean displayTeamChat = false;
    public static GuiNewChat backupChatGUI = new GuiNewChat(ClientHandler.mc);
}
