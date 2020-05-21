package com.daposeidonguy.teamsmod;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.inventory.ScreenTransfer;
import com.daposeidonguy.teamsmod.client.keybind.KeyBindHandler;
import com.daposeidonguy.teamsmod.common.command.CommandTeam;
import com.daposeidonguy.teamsmod.common.compat.StageEvents;
import com.daposeidonguy.teamsmod.common.config.ConfigHolder;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTypes;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TeamsMod.MODID)
public class TeamsMod {

    public static final String MODID = "teamsmod";
    public static final Logger logger = LogManager.getLogger(MODID);
    public static boolean doneSetup = false;
    private static TeamsMod instance;


    public TeamsMod() {
        TeamsMod.logger.info("Teams: Initializing");
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(instance::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(instance::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        if (ModList.get().isLoaded("gamestages")) {
            MinecraftForge.EVENT_BUS.register(new StageEvents());
        }
        TeamsMod.logger.info("Registering configs...");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        TeamsMod.logger.info("Teams: Common setup");
        PacketHandler.register();
        doneSetup = true;
    }

    private void clientSetup(FMLClientSetupEvent event) {
        TeamsMod.logger.info("Teams: Client setup");
        KeyBindHandler.register();
        TeamsMod.logger.info("Registering container screens...");
        ScreenManager.registerFactory(ContainerTypes.containerTypeTransfer, ScreenTransfer::new);
        GuiHandler.persistentChatGUI.setAccessible(true);
    }

    @SubscribeEvent
    public void serverStart(FMLServerStartingEvent event) {
        TeamsMod.logger.info("Teams: Server starting");
        CommandTeam.register(event.getCommandDispatcher());
    }


}
