package com.daposeidonguy.teamsmod.client.gui.toasts;

import com.daposeidonguy.teamsmod.client.KeyBindings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.text.LanguageMap;

import java.awt.*;

public class ToastInvite implements IToast {

    public boolean accepted = false;
    String teamName;
    boolean firstDraw = true;
    long firstDrawTime;

    public ToastInvite(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public Visibility draw(ToastGui toastGui, long delta) {
        if (firstDraw) {
            firstDrawTime = delta;
            firstDraw = false;
        }
        if (accepted) {
            return IToast.Visibility.HIDE;
        }
        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        toastGui.blit(0, 0, 0, 64, 160, 32);
        toastGui.getMinecraft().fontRenderer.drawString("Invited to storage: " + this.teamName, 22, 7, Color.WHITE.getRGB());
        String keyName = "\"" + LanguageMap.getInstance().translateKey(KeyBindings.accept.getTranslationKey()) + "\"";
        toastGui.getMinecraft().fontRenderer.drawString("Press " + keyName + " to accept", 22, 18, -16777216);

        return delta - this.firstDrawTime < 15000L && this.teamName != null ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }
}
