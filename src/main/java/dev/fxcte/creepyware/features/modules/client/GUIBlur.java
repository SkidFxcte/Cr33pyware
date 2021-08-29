package dev.fxcte.creepyware.features.modules.client;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.util.Util;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiModList;

public
class GUIBlur extends Module implements Util {

    public
    GUIBlur ( ) {
        super ( "GUIBlur" , "Sussy" , Category.CLIENT , true , false , false );
    }

    public
    void onDisable ( ) {
        if ( mc.world != null ) {
            mc.entityRenderer.getShaderGroup ( ).deleteShaderGroup ( );
        }
    }

    public
    void onUpdate ( ) {
        if ( mc.world != null ) {
            if ( ClickGui.getInstance ( ).isEnabled ( ) || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiConfirmOpenLink || this.mc.currentScreen instanceof GuiEditSign || this.mc.currentScreen instanceof GuiGameOver || this.mc.currentScreen instanceof GuiOptions || this.mc.currentScreen instanceof GuiIngameMenu || this.mc.currentScreen instanceof GuiVideoSettings || this.mc.currentScreen instanceof GuiScreenOptionsSounds || this.mc.currentScreen instanceof GuiControls || this.mc.currentScreen instanceof GuiCustomizeSkin || this.mc.currentScreen instanceof GuiModList ) {
                if ( OpenGlHelper.shadersSupported && mc.getRenderViewEntity ( ) instanceof EntityPlayer ) {
                    if ( mc.entityRenderer.getShaderGroup ( ) != null ) {
                        mc.entityRenderer.getShaderGroup ( ).deleteShaderGroup ( );
                    }
                    try {
                        mc.entityRenderer.loadShader ( new ResourceLocation ( "shaders/post/blur.json" ) );
                    } catch ( Exception e ) {
                        e.printStackTrace ( );
                    }
                } else if ( mc.entityRenderer.getShaderGroup ( ) != null && mc.currentScreen == null ) {
                    mc.entityRenderer.getShaderGroup ( ).deleteShaderGroup ( );
                }
            } else if ( mc.entityRenderer.getShaderGroup ( ) != null ) {
                mc.entityRenderer.getShaderGroup ( ).deleteShaderGroup ( );
            }
        }
    }
}