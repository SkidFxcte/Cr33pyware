package dev.fxcte.creepyware.features.modules.render;

import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public
class HandSwing
        extends Module {
    private final Setting< Mode > mode = this.register ( new Setting <> ( "OldHandSwing" , Mode.OneDotEight ) );
    private final Setting < Swing > swing = this.register ( new Setting <> ( "Swing" , Swing.Mainhand ) );
    private final Setting < Boolean > slow = this.register ( new Setting <> ( "Slow" , false ) );

    public
    HandSwing ( ) {
        super ( "HandSwing" , "Change HandSwing." , Module.Category.RENDER , true , false , false );
    }

    @Override
    public
    void onUpdate ( ) {
        if ( HandSwing.nullCheck ( ) ) {
            return;
        }
        if ( this.swing.getValue ( ) == Swing.Offhand ) {
            HandSwing.mc.player.swingingHand = EnumHand.OFF_HAND;
        }
        if ( this.mode.getValue ( ) == Mode.OneDotEight && (double) HandSwing.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9 ) {
            HandSwing.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            HandSwing.mc.entityRenderer.itemRenderer.itemStackMainHand = HandSwing.mc.player.getHeldItemMainhand ( );
        }
    }

    @Override
    public
    void onEnable ( ) {
        if ( this.slow.getValue ( ) ) {
            HandSwing.mc.player.addPotionEffect ( new PotionEffect ( MobEffects.MINING_FATIGUE , 255000 ) );
        }
    }

    @Override
    public
    void onDisable ( ) {
        if ( this.slow.getValue ( ) ) {
            HandSwing.mc.player.removePotionEffect ( MobEffects.MINING_FATIGUE );
        }
    }

    @SubscribeEvent
    public
    void onPacketSend ( PacketEvent.Send send ) {
        Object t = send.getPacket ( );
        if ( t instanceof CPacketAnimation ) {
            if ( this.swing.getValue ( ) == Swing.Disable ) {
                send.setCanceled ( true );
            }
        }
    }

    private
    enum Mode {
        Normal,
        OneDotEight

    }

    private
    enum Swing {
        Mainhand,
        Offhand,
        Disable

    }
}
