package dev.fxcte.creepyware.mixin.mixins;

import dev.fxcte.creepyware.event.events.KeyEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (value = {KeyBinding.class})
public
class MixinKeyBinding {
    @Shadow
    private boolean pressed;

    @Inject (method = {"isKeyDown"}, at = {@At (value = "RETURN")}, cancellable = true)
    private
    void isKeyDown(CallbackInfoReturnable <Boolean> info) {
        KeyEvent event = new KeyEvent(0 , info.getReturnValue() , this.pressed);
        MinecraftForge.EVENT_BUS.post(event);
        info.setReturnValue(event.info);
    }
}

