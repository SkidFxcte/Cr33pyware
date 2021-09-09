package dev.fxcte.creepyware.mixin.mixins;

import dev.fxcte.creepyware.features.modules.client.Colors;
import dev.fxcte.creepyware.features.modules.render.HandChams;
import dev.fxcte.creepyware.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin (value = {RenderPlayer.class})
public
class MixinRenderPlayer {

    @Inject (method = {"renderRightArm"}, at = {@At (value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F", opcode = 181)}, cancellable = true)
    public
    void renderRightArmBegin(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            if (HandChams.INSTANCE.rainbow.getValue()) {
                Color rainbowColor = HandChams.INSTANCE.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow(HandChams.INSTANCE.speed.getValue() * 100, 0, (float) HandChams.INSTANCE.saturation.getValue() / 100.0f, (float) HandChams.INSTANCE.brightness.getValue() / 100.0f));
                GL11.glColor4f((float) rainbowColor.getRed() / 255.0f, (float) rainbowColor.getGreen() / 255.0f, (float) rainbowColor.getBlue() / 255.0f, (float) HandChams.INSTANCE.alpha.getValue() / 255.0f);
            } else {
                Color color = HandChams.INSTANCE.colorSync.getValue() ? new Color(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getBlue(), Colors.INSTANCE.getCurrentColor().getGreen(), HandChams.INSTANCE.alpha.getValue()) : new Color(HandChams.INSTANCE.red.getValue(), HandChams.INSTANCE.green.getValue(), HandChams.INSTANCE.blue.getValue(), HandChams.INSTANCE.alpha.getValue());
                GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
            }
        }
    }

    @Inject (method = {"renderRightArm"}, at = {@At (value = "RETURN")}, cancellable = true)
    public
    void renderRightArmReturn(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        }
    }

    @Inject (method = {"renderLeftArm"}, at = {@At (value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F", opcode = 181)}, cancellable = true)
    public
    void renderLeftArmBegin(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            if (HandChams.INSTANCE.rainbow.getValue()) {
                Color rainbowColor = HandChams.INSTANCE.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow(HandChams.INSTANCE.speed.getValue() * 100, 0, (float) HandChams.INSTANCE.saturation.getValue() / 100.0f, (float) HandChams.INSTANCE.brightness.getValue() / 100.0f));
                GL11.glColor4f((float) rainbowColor.getRed() / 255.0f, (float) rainbowColor.getGreen() / 255.0f, (float) rainbowColor.getBlue() / 255.0f, (float) HandChams.INSTANCE.alpha.getValue() / 255.0f);
            } else {
                Color color = HandChams.INSTANCE.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow(HandChams.INSTANCE.speed.getValue() * 100, 0, (float) HandChams.INSTANCE.saturation.getValue() / 100.0f, (float) HandChams.INSTANCE.brightness.getValue() / 100.0f));
                GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) HandChams.INSTANCE.alpha.getValue() / 255.0f);
            }
        }
    }

    @Inject (method = {"renderLeftArm"}, at = {@At (value = "RETURN")}, cancellable = true)
    public
    void renderLeftArmReturn(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        }
    }
}