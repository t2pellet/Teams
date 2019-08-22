package com.daposeidonguy.teamsmod;

import com.daposeidonguy.teamsmod.client.GuiHandler;
import com.daposeidonguy.teamsmod.client.Keybind;
import com.daposeidonguy.teamsmod.commands.CommandTeam;
import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.network.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TeamsMod.MODID, name = TeamsMod.NAME, version = TeamsMod.VERSION)
public class TeamsMod
{


    public static final SoundEvent SONG = new SoundEvent(new ResourceLocation("teamsmod","time_to_mine"));

    public static final String MODID = "teamsmod";
    public static final String NAME = "Teams Mod";
    public static final String VERSION = "1.0";

    @Instance(MODID)
    public static TeamsMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.INSTANCE.registerMessage(MessageSaveData.MessageHandler.class,MessageSaveData.class,0, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageHunger.MessageHandler.class, MessageHunger.class,1,Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageClear.MessageHandler.class,MessageClear.class,5,Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageSong.MessageHandler.class,MessageSong.class,3,Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageGui.MessageHandler.class, MessageGui.class,4,Side.SERVER);
        if(FMLCommonHandler.instance().getSide()==Side.CLIENT) {
            Keybind.register();
            MinecraftForge.EVENT_BUS.register(GuiHandler.instance());
            MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
            PacketHandler.INSTANCE.registerMessage(MessageSaveData.MessageHandler.class,MessageSaveData.class,0,Side.CLIENT);
            PacketHandler.INSTANCE.registerMessage(MessageHunger.MessageHandler.class, MessageHunger.class,1,Side.CLIENT);
            PacketHandler.INSTANCE.registerMessage(MessageClear.MessageHandler.class,MessageClear.class,5,Side.CLIENT);
            PacketHandler.INSTANCE.registerMessage(MessageSong.MessageHandler.class,MessageSong.class,3,Side.CLIENT);
            PacketHandler.INSTANCE.registerMessage(MessageGui.MessageHandler.class, MessageGui.class,4,Side.CLIENT);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTeam());
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        PacketHandler.INSTANCE.sendToAll(new MessageClear());
    }
}
