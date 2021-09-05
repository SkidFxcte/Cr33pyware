package dev.fxcte.creepyware.features.modules.misc;

import dev.fxcte.creepyware.event.events.EventNetworkPacketEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;

import java.util.LinkedList;

public class Blink extends Module {
    public final Setting<Boolean> Visualize = new Setting("Visualize", true);
    public final Setting<Boolean> EntityBlink = new Setting<Boolean>("EntityBlink", false);

    public Blink() {
        super("Blink", "BLINKL", Category.MISC, false, false, false);
    }

    private EntityOtherPlayerMP Original;
    private EntityDonkey RidingEntity;
    private LinkedList<Packet> Packets = new LinkedList<Packet>();

    @Override
    public void onEnable() {
        super.onEnable();

        Packets.clear();
        Original = null;
        RidingEntity = null;

        if (Visualize.getValue()) {
            Original = new EntityOtherPlayerMP(mc.world, mc.session.getProfile());
            Original.copyLocationAndAnglesFrom(mc.player);
            Original.rotationYaw = mc.player.rotationYaw;
            Original.rotationYawHead = mc.player.rotationYawHead;
            Original.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(-0xFFFFF, Original);

            if (mc.player.isRiding() && mc.player.getRidingEntity() instanceof EntityDonkey) {
                EntityDonkey l_Original = (EntityDonkey) mc.player.getRidingEntity();

                RidingEntity = new EntityDonkey(mc.world);
                RidingEntity.copyLocationAndAnglesFrom(l_Original);
                RidingEntity.setChested(l_Original.hasChest());
                mc.world.addEntityToWorld(-0xFFFFF + 1, RidingEntity);

                Original.startRiding(RidingEntity, true);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (!Packets.isEmpty() && mc.world != null) {
            while (!Packets.isEmpty()) {
                mc.getConnection().sendPacket(Packets.getFirst()); ///< front
                Packets.removeFirst(); ///< pop
            }
        }

        if (Original != null) {
            if (Original.isRiding())
                Original.dismountRidingEntity();

            mc.world.removeEntity(Original);
        }

        if (RidingEntity != null)
            mc.world.removeEntity(RidingEntity);
    }
}