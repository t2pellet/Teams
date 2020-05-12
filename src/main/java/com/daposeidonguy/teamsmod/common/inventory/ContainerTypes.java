package com.daposeidonguy.teamsmod.common.inventory;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerTypes {
    public static ContainerType<ContainerTransfer> containerTypeTransfer;

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<ContainerType<?>> event) {
        ContainerTypes.containerTypeTransfer = IForgeContainerType.create(ContainerTransfer::new);
        containerTypeTransfer.setRegistryName("transfer");
        event.getRegistry().register(containerTypeTransfer);
    }
}
