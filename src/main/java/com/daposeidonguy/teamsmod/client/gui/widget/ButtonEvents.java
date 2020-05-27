package com.daposeidonguy.teamsmod.client.gui.widget;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.team.ScreenMain;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, value = Side.CLIENT)
public class ButtonEvents {

    /* Show Teams GUI button in inventory screens */
    @SubscribeEvent
    public static void addGuiButton(final GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof InventoryEffectRenderer) {
            InventoryEffectRenderer guiInventory = (InventoryEffectRenderer) event.getGui();
            boolean isCreative = event.getGui() instanceof GuiContainerCreative;
            int renderX = (TeamConfig.client.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiLeft() + 152;
            renderX += TeamConfig.client.smallIcon ? 5 : 0;
            int renderY = (TeamConfig.client.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiTop() + 4;
            int renderWidth = TeamConfig.client.smallIcon ? 15 : 20;
            int renderHeight = TeamConfig.client.smallIcon ? 13 : 18;
            ResourceLocation renderLoc = TeamConfig.client.smallIcon ? new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png") : new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png");
            GuiButtonImage buttonTeamScreen = new GuiButtonImage(GuiHandler.BUTTON_GUI, renderX - (TeamConfig.client.smallIcon ? 2 : 0), renderY, renderWidth, renderHeight, 0, 0, renderHeight + (TeamConfig.client.smallIcon ? 1 : 0), renderLoc);
            event.getButtonList().add(buttonTeamScreen);
        }
    }

    @SubscribeEvent
    public static void buttonPress(final GuiScreenEvent.ActionPerformedEvent event) {
        if (event.getGui() instanceof InventoryEffectRenderer) {
            if (event.getButton().id == GuiHandler.BUTTON_GUI) {
                ClientHelper.mc.displayGuiScreen(new ScreenMain());
            }
        }
    }

}
