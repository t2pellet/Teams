package com.daposeidonguy.teamsmod;

import com.daposeidonguy.teamsmod.proxy.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = TeamsMod.MODID, name = TeamsMod.NAME, version = TeamsMod.VERSION)
public class TeamsMod {


    public static final String MODID = "teamsmod";
    public static final String NAME = "Teams Mod";
    public static final String VERSION = "1.1";

    @SidedProxy(clientSide = "com.daposeidonguy.teamsmod.proxy.ClientProxy", serverSide = "com.daposeidonguy.teamsmod.proxy.ServerProxy")
    public static ServerProxy proxy;


    @Instance(MODID)
    public static TeamsMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        proxy.serverLoad(event);
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        proxy.serverStop(event);
    }
}
