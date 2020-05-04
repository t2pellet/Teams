package com.daposeidonguy.teamsmod;

import com.daposeidonguy.teamsmod.client.KeyBindings;
import com.daposeidonguy.teamsmod.client.gui.screen.inventory.ScreenTransfer;
import com.daposeidonguy.teamsmod.common.commands.CommandTeam;
import com.daposeidonguy.teamsmod.common.config.ConfigHolder;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTypes;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TeamsMod.MODID)
public class TeamsMod {

    public static final String MODID = "teamsmod";
    public static Logger logger = LogManager.getLogger(MODID);
    private static TeamsMod instance;
    private static SaveData data;

    public TeamsMod() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(instance::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(instance::clientSetup);
        MinecraftForge.EVENT_BUS.register(instance);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        KeyBindings.register();
        ScreenManager.registerFactory(ContainerTypes.containerTypeTransfer, ScreenTransfer::new);
    }

    @SubscribeEvent
    public void serverStart(FMLServerStartingEvent event) {
        CommandTeam.register(event.getCommandDispatcher());
        data = SaveData.get(event.getServer().getWorld(DimensionType.OVERWORLD));
    }

    @SubscribeEvent
    public void serverStop(FMLServerStoppedEvent event) {
        event.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().save();
    }

}
