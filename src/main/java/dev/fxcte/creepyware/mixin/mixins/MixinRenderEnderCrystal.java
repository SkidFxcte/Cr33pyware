package dev.fxcte.creepyware.mixin.mixins;

import dev.fxcte.creepyware.event.events.RenderEntityModelEvent;
import dev.fxcte.creepyware.features.modules.client.ClientColors;
import dev.fxcte.creepyware.features.modules.render.CrystalChams;
import dev.fxcte.creepyware.util.EntityUtil;
import dev.fxcte.creepyware.util.RenderUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(value = {RenderEnderCrystal.class})
public
class MixinRenderEnderCrystal {
    private static final ResourceLocation glint;
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;

    static {
        glint = new ResourceLocation ( "textures/glint.png" );
    }

    @Redirect(method = {"doRender"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public
    void renderModelBaseHook ( ModelBase model , Entity entity , float limbSwing , float limbSwingAmount , float ageInTicks , float netHeadYaw , float headPitch , float scale ) {
        if ( CrystalChams.INSTANCE.isEnabled ( ) ) {
            if ( CrystalChams.INSTANCE.animateScale.getValue ( ) && CrystalChams.INSTANCE.scaleMap.containsKey ( entity ) ) {
                GlStateManager.scale ( CrystalChams.INSTANCE.scaleMap.get ( entity ) , CrystalChams.INSTANCE.scaleMap.get ( entity ) , CrystalChams.INSTANCE.scaleMap.get ( entity ) );
            } else {
                GlStateManager.scale ( CrystalChams.INSTANCE.scale.getValue ( ) , CrystalChams.INSTANCE.scale.getValue ( ) , CrystalChams.INSTANCE.scale.getValue ( ) );
            }
        }
        if ( CrystalChams.INSTANCE.isEnabled ( ) && CrystalChams.INSTANCE.wireframe.getValue ( ) ) {
            RenderEntityModelEvent event = new RenderEntityModelEvent ( 0 , model , entity , limbSwing , limbSwingAmount , ageInTicks , netHeadYaw , headPitch , scale );
            CrystalChams.INSTANCE.onRenderModel ( event );
        }
        if ( CrystalChams.INSTANCE.isEnabled ( ) && CrystalChams.INSTANCE.chams.getValue ( ) ) {
            GL11.glPushAttrib ( 1048575 );
            GL11.glDisable ( 3008 );
            GL11.glDisable ( 3553 );
            GL11.glDisable ( 2896 );
            GL11.glEnable ( 3042 );
            GL11.glBlendFunc ( 770 , 771 );
            GL11.glLineWidth ( 1.5f );
            GL11.glEnable ( 2960 );
            if ( CrystalChams.INSTANCE.rainbow.getValue ( ) ) {
                Color rainbowColor1 = CrystalChams.INSTANCE.colorSync.getValue ( ) ? ClientColors.INSTANCE.getCurrentColor ( ) : new Color ( RenderUtil.getRainbow ( CrystalChams.INSTANCE.speed.getValue ( ) * 100 , 0 , (float) CrystalChams.INSTANCE.saturation.getValue ( ) / 100.0f , (float) CrystalChams.INSTANCE.brightness.getValue ( ) / 100.0f ) );
                Color rainbowColor = EntityUtil.getColor ( entity , rainbowColor1.getRed ( ) , rainbowColor1.getGreen ( ) , rainbowColor1.getBlue ( ) , CrystalChams.INSTANCE.alpha.getValue ( ) , true );
                if ( CrystalChams.INSTANCE.throughWalls.getValue ( ) ) {
                    GL11.glDisable ( 2929 );
                    GL11.glDepthMask ( false );
                }
                GL11.glEnable ( 10754 );
                GL11.glColor4f ( (float) rainbowColor.getRed ( ) / 255.0f , (float) rainbowColor.getGreen ( ) / 255.0f , (float) rainbowColor.getBlue ( ) / 255.0f , (float) CrystalChams.INSTANCE.alpha.getValue ( ) / 255.0f );
                model.render ( entity , limbSwing , limbSwingAmount , ageInTicks , netHeadYaw , headPitch , scale );
                if ( CrystalChams.INSTANCE.throughWalls.getValue ( ) ) {
                    GL11.glEnable ( 2929 );
                    GL11.glDepthMask ( true );
                }
            } else if ( CrystalChams.INSTANCE.xqz.getValue ( ) && CrystalChams.INSTANCE.throughWalls.getValue ( ) ) {
                Color visibleColor;
                Color hiddenColor = CrystalChams.INSTANCE.colorSync.getValue ( ) ? EntityUtil.getColor ( entity , CrystalChams.INSTANCE.hiddenRed.getValue ( ) , CrystalChams.INSTANCE.hiddenGreen.getValue ( ) , CrystalChams.INSTANCE.hiddenBlue.getValue ( ) , CrystalChams.INSTANCE.hiddenAlpha.getValue ( ) , true ) : EntityUtil.getColor ( entity , CrystalChams.INSTANCE.hiddenRed.getValue ( ) , CrystalChams.INSTANCE.hiddenGreen.getValue ( ) , CrystalChams.INSTANCE.hiddenBlue.getValue ( ) , CrystalChams.INSTANCE.hiddenAlpha.getValue ( ) , true );
                Color color = visibleColor = CrystalChams.INSTANCE.colorSync.getValue ( ) ? EntityUtil.getColor ( entity , CrystalChams.INSTANCE.red.getValue ( ) , CrystalChams.INSTANCE.green.getValue ( ) , CrystalChams.INSTANCE.blue.getValue ( ) , CrystalChams.INSTANCE.alpha.getValue ( ) , true ) : EntityUtil.getColor ( entity , CrystalChams.INSTANCE.red.getValue ( ) , CrystalChams.INSTANCE.green.getValue ( ) , CrystalChams.INSTANCE.blue.getValue ( ) , CrystalChams.INSTANCE.alpha.getValue ( ) , true );
                if ( CrystalChams.INSTANCE.throughWalls.getValue ( ) ) {
                    GL11.glDisable ( 2929 );
                    GL11.glDepthMask ( false );
                }
                GL11.glEnable ( 10754 );
                GL11.glColor4f ( (float) hiddenColor.getRed ( ) / 255.0f , (float) hiddenColor.getGreen ( ) / 255.0f , (float) hiddenColor.getBlue ( ) / 255.0f , (float) CrystalChams.INSTANCE.alpha.getValue ( ) / 255.0f );
                model.render ( entity , limbSwing , limbSwingAmount , ageInTicks , netHeadYaw , headPitch , scale );
                if ( CrystalChams.INSTANCE.throughWalls.getValue ( ) ) {
                    GL11.glEnable ( 2929 );
                    GL11.glDepthMask ( true );
                }
                GL11.glColor4f ( (float) visibleColor.getRed ( ) / 255.0f , (float) visibleColor.getGreen ( ) / 255.0f , (float) visibleColor.getBlue ( ) / 255.0f , (float) CrystalChams.INSTANCE.alpha.getValue ( ) / 255.0f );
                model.render ( entity , limbSwing , limbSwingAmount , ageInTicks , netHeadYaw , headPitch , scale );
            } else {
                Color visibleColor;
                Color color = visibleColor = CrystalChams.INSTANCE.colorSync.getValue ( ) ? ClientColors.INSTANCE.getCurrentColor ( ) : EntityUtil.getColor ( entity , CrystalChams.INSTANCE.red.getValue ( ) , CrystalChams.INSTANCE.green.getValue ( ) , CrystalChams.INSTANCE.blue.getValue ( ) , CrystalChams.INSTANCE.alpha.getValue ( ) , true );
                if ( CrystalChams.INSTANCE.throughWalls.getValue ( ) ) {
                    GL11.glDisable ( 2929 );
                    GL11.glDepthMask ( false );
                }
                GL11.glEnable ( 10754 );
                GL11.glColor4f ( (float) visibleColor.getRed ( ) / 255.0f , (float) visibleColor.getGreen ( ) / 255.0f , (float) visibleColor.getBlue ( ) / 255.0f , (float) CrystalChams.INSTANCE.alpha.getValue ( ) / 255.0f );
                model.render ( entity , limbSwing , limbSwingAmount , ageInTicks , netHeadYaw , headPitch , scale );
                if ( CrystalChams.INSTANCE.throughWalls.getValue ( ) ) {
                    GL11.glEnable ( 2929 );
                    GL11.glDepthMask ( true );
                }
            }
            GL11.glEnable ( 3042 );
            GL11.glEnable ( 2896 );
            GL11.glEnable ( 3553 );
            GL11.glEnable ( 3008 );
            GL11.glPopAttrib ( );
            if ( CrystalChams.INSTANCE.glint.getValue ( ) ) {
                GL11.glDisable ( 2929 );
                GL11.glDepthMask ( false );
                GlStateManager.enableAlpha ( );
                GlStateManager.color ( 1.0f , 0.0f , 0.0f , 0.13f );
                model.render ( entity , limbSwing , limbSwingAmount , ageInTicks , netHeadYaw , headPitch , scale );
                GlStateManager.disableAlpha ( );
                GL11.glEnable ( 2929 );
                GL11.glDepthMask ( true );
            }
        } else {
            model.render ( entity , limbSwing , limbSwingAmount , ageInTicks , netHeadYaw , headPitch , scale );
        }
        if ( CrystalChams.INSTANCE.isEnabled ( ) ) {
            if ( CrystalChams.INSTANCE.animateScale.getValue ( ) && CrystalChams.INSTANCE.scaleMap.containsKey ( entity ) ) {
                GlStateManager.scale ( 1.0f / CrystalChams.INSTANCE.scaleMap.get ( entity ) , 1.0f / CrystalChams.INSTANCE.scaleMap.get ( entity ) , 1.0f / CrystalChams.INSTANCE.scaleMap.get ( entity ) );
            } else {
                GlStateManager.scale ( 1.0f / CrystalChams.INSTANCE.scale.getValue ( ) , 1.0f / CrystalChams.INSTANCE.scale.getValue ( ) , 1.0f / CrystalChams.INSTANCE.scale.getValue ( ) );
            }
        }
    }
}