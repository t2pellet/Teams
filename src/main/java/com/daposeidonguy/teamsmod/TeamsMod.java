package com.daposeidonguy.teamsmod;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.keybind.KeyBindHandler;
import com.daposeidonguy.teamsmod.common.command.CommandTeam;
import com.daposeidonguy.teamsmod.common.config.ConfigHelper;
import com.daposeidonguy.teamsmod.common.network.NetworkHelper;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import com.daposeidonguy.teamsmod.common.storage.StorageEvents;
import com.daposeidonguy.teamsmod.common.storage.TeamDataManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TeamsMod.MODID, name = TeamsMod.MODNAME, version = TeamsMod.VERSION, acceptedMinecraftVersions = TeamsMod.ACCEPTED_VERSIONS)
public class TeamsMod {

    public static final String MODID = "teamsmod";
    public static final String MODNAME = "Teams";
    public static final String VERSION = "1.12.2-Rfinal";
    public static final String ACCEPTED_VERSIONS = "[1.12.2]";
    public static final Logger logger = LogManager.getLogger(MODID);
    public static boolean doneSetup = false;


    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        logger.info("Teams: Pre-initializing");
        if (FMLCommonHandler.instance().getSide().isClient()) {
            PacketHandler.register(Side.CLIENT);
        }
        PacketHandler.register(Side.SERVER);
        logger.info("Teams: Registered packets");
        MinecraftForge.EVENT_BUS.register(new ConfigHelper());
        MinecraftForge.EVENT_BUS.register(this);
        logger.info("Teams: Registered event listeners");
    }

    @Mod.EventHandler
    private void init(FMLInitializationEvent event) {
        logger.info("Teams: Initializing");
        if (event.getSide().isClient()) {
            KeyBindHandler.register();
            GuiHandler.persistentChatGUI.setAccessible(true);
            logger.info("Teams: patched chat");
        }
        doneSetup = true;
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        logger.info("Teams: Server Starting");
        event.registerServerCommand(new CommandTeam());
        logger.info("Teams: Registered server commands");
        StorageEvents.data = TeamDataManager.get(event.getServer().getEntityWorld());
        if (event.getServer().isSinglePlayer()) {
            NetworkHelper.sendToAll(new MessageSaveData());
        }
        logger.info("Teams: Loading save data");
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        StorageEvents.data.markDirty();
    }


}
