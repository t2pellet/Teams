package com.daposeidonguy.teamsmod;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.keybind.KeyBindHandler;
import com.daposeidonguy.teamsmod.common.command.CommandTeam;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import com.daposeidonguy.teamsmod.common.storage.StorageEvents;
import com.daposeidonguy.teamsmod.common.storage.TeamDataManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TeamsMod.MODID, name = TeamsMod.MODNAME, version = TeamsMod.VERSION, acceptedMinecraftVersions = TeamsMod.ACCEPTED_VERSIONS)
public class TeamsMod {

    public static final String MODID = "teamsmod";
    public static final String MODNAME = "Teams";
    public static final String VERSION = "1.12.2-final";
    public static final String ACCEPTED_VERSIONS = "[1.12.2]";
    public static final Logger logger = LogManager.getLogger(MODID);
    public static boolean doneSetup = false;


    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        preInitClient();
        PacketHandler.register(Side.SERVER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    private void preInitClient() {
        PacketHandler.register(Side.CLIENT);
    }

    @Mod.EventHandler
    private void init(FMLInitializationEvent event) {
        if (event.getSide().isClient()) {
            KeyBindHandler.register();
            GuiHandler.persistentChatGUI.setAccessible(true);
        }
        doneSetup = true;
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTeam());
        StorageEvents.data = TeamDataManager.get(event.getServer().getEntityWorld());
        if (event.getServer().isSinglePlayer()) {
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
        }
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        StorageEvents.data.markDirty();
    }


}
