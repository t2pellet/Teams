package com.daposeidonguy.teamsmod.proxy;

import com.daposeidonguy.teamsmod.commands.CommandTeam;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

public class ServerProxy {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.registerMessagesServer();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTeam());
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
    }

}
