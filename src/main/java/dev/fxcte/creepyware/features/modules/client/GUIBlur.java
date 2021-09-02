package dev.fxcte.creepyware.features.modules.client;


import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.util.Wrapper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.Minecraft;

public class GUIBlur extends Module
{
    final Minecraft mc;

    public GUIBlur() {
        super("GUIBlur", "Blur" ,Module.Category.CLIENT, true, false, false);
        this.mc = Minecraft.getMinecraft();
    }

    public void onDisable() {
        if (this.mc.world != null) {
            Wrapper.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    @Override
    public void onUpdate() {
        if (this.mc.world != null) {
            if (ClickGui.getInstance().isEnabled() || this.mc.currentScreen instanceof GuiContainer || this.mc.currentScreen instanceof GuiChat || this.mc.currentScreen instanceof GuiConfirmOpenLink || this.mc.currentScreen instanceof GuiEditSign || this.mc.currentScreen instanceof GuiGameOver || this.mc.currentScreen instanceof GuiOptions || this.mc.currentScreen instanceof GuiIngameMenu || this.mc.currentScreen instanceof GuiVideoSettings || this.mc.currentScreen instanceof GuiScreenOptionsSounds || this.mc.currentScreen instanceof GuiControls || this.mc.currentScreen instanceof GuiCustomizeSkin || this.mc.currentScreen instanceof GuiModList) {
                if (OpenGlHelper.shadersSupported && Wrapper.mc.getRenderViewEntity() instanceof EntityPlayer) {
                    if (Wrapper.mc.entityRenderer.getShaderGroup() != null) {
                        Wrapper.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                    }
                    try {
                        Wrapper.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (Wrapper.mc.entityRenderer.getShaderGroup() != null && Wrapper.mc.currentScreen == null) {
                    Wrapper.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }
            }
            else if (Wrapper.mc.entityRenderer.getShaderGroup() != null) {
                Wrapper.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
        }
    }
}