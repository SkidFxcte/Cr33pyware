/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FullBright
        extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.GAMMA));
    public Setting<Boolean> effects = this.register(new Setting<Boolean>("Effects", false));
    private float previousSetting = 1.0f;

    public FullBright() {
        super("Fullbright", "Makes your game brighter.", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onEnable() {
        this.previousSetting = FullBright.mc.gameSettings.gammaSetting;
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.GAMMA) {
            FullBright.mc.gameSettings.gammaSetting = 1000.0f;
        }
        if (this.mode.getValue() == Mode.POTION) {
            FullBright.mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210));
        }
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue() == Mode.POTION) {
            FullBright.mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
        FullBright.mc.gameSettings.gammaSetting = this.previousSetting;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && event.getPacket() instanceof SPacketEntityEffect && this.effects.getValue().booleanValue()) {
            SPacketEntityEffect packet = (SPacketEntityEffect)event.getPacket();
            if (FullBright.mc.player != null && packet.getEntityId() == FullBright.mc.player.getEntityId() && (packet.getEffectId() == 9 || packet.getEffectId() == 15)) {
                event.setCanceled(true);
            }
        }
    }

    public static enum Mode {
        GAMMA,
        POTION;

    }
}

