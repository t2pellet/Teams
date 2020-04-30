package com.daposeidonguy.teamsmod.common.inventory;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID)
public class ContainerTypes {
    public static ContainerType<ContainerTransfer> containerTypeTransfer;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<ContainerType<?>> event) {
        containerTypeTransfer = IForgeContainerType.create(ContainerTransfer::new);
    }

}
