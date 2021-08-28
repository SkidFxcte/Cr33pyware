package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.render.NoRender;
import me.alpha432.oyvey.features.modules.render.SmallShield;
import me.alpha432.oyvey.features.modules.render.ViewModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {

    @Shadow
    public abstract void renderItemInFirstPerson(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7);
    @Final
    public Minecraft mc;
    private boolean injection = true;
    @Inject(method = {"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        if (this.injection) {
            info.cancel();
            SmallShield offset = SmallShield.getINSTANCE();
            float xOffset = 0.0f;
            float yOffset = 0.0f;
            this.injection = false;
            if (hand == EnumHand.MAIN_HAND) {
                if (offset.isOn() && player.getHeldItemMainhand() != ItemStack.EMPTY) {
                    xOffset = offset.mainX.getValue();
                    yOffset = offset.mainY.getValue();
                }
            } else if (!offset.normalOffset.getValue() && offset.isOn() && player.getHeldItemOffhand() != ItemStack.EMPTY) {
                xOffset = offset.offX.getValue();
                yOffset = offset.offY.getValue();
            }
            this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
            this.injection = true;
        }
        if (ViewModel.getINSTANCE().enabled.getValue() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Feature.fullNullCheck()) {
            GlStateManager.scale(ViewModel.getINSTANCE().sizeX.getValue(), ViewModel.getINSTANCE().sizeY.getValue(), ViewModel.getINSTANCE().sizeZ.getValue());
            GlStateManager.rotate(ViewModel.getINSTANCE().rotationX.getValue() * 360.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(ViewModel.getINSTANCE().rotationY.getValue() * 360.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(ViewModel.getINSTANCE().rotationZ.getValue() * 360.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(ViewModel.getINSTANCE().positionX.getValue(), ViewModel.getINSTANCE().positionY.getValue(), ViewModel.getINSTANCE().positionZ.getValue());
        }
    }

    @Redirect(method = {"renderArmFirstPerson"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 0))
    public void translateHook(float x, float y, float z) {
        SmallShield offset = SmallShield.getINSTANCE();
        boolean shiftPos = Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getHeldItemMainhand() != ItemStack.EMPTY && offset.isOn();
        GlStateManager.translate((float) (x + (shiftPos ? offset.mainX.getValue().floatValue() : 0.0f)), (float) (y + (shiftPos ? offset.mainY.getValue().floatValue() : 0.0f)), (float) z);
    }

    @Inject(method = {"renderFireInFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fire.getValue().booleanValue()) {
            info.cancel();
        }
    }

    @Inject(method = {"renderSuffocationOverlay"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderSuffocationOverlay(CallbackInfo ci) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().blocks.getValue().booleanValue()) {
            ci.cancel();
        }
    }
}
