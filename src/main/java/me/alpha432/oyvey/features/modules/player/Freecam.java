package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.events.MoveEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam
        extends Module {
    private static Freecam INSTANCE = new Freecam();
    public Setting<Double> speed = this.register(new Setting<Double>("Speed", 0.5, 0.1, 5.0));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Cancel Packets", true));
    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;

    public Freecam() {
        super("Freecam", "Look around freely.", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    public static Freecam getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Freecam();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (Freecam.mc.player != null) {
            boolean bl = this.isRidingEntity = Freecam.mc.player.getRidingEntity() != null;
            if (Freecam.mc.player.getRidingEntity() == null) {
                this.posX = Freecam.mc.player.posX;
                this.posY = Freecam.mc.player.posY;
                this.posZ = Freecam.mc.player.posZ;
            } else {
                this.ridingEntity = Freecam.mc.player.getRidingEntity();
                Freecam.mc.player.dismountRidingEntity();
            }
            this.pitch = Freecam.mc.player.rotationPitch;
            this.yaw = Freecam.mc.player.rotationYaw;
            this.clonedPlayer = new EntityOtherPlayerMP((World)Freecam.mc.world, mc.getSession().getProfile());
            this.clonedPlayer.copyLocationAndAnglesFrom((Entity)Freecam.mc.player);
            this.clonedPlayer.rotationYawHead = Freecam.mc.player.rotationYawHead;
            Freecam.mc.world.addEntityToWorld(-100, (Entity)this.clonedPlayer);
            Freecam.mc.player.capabilities.isFlying = true;
            Freecam.mc.player.capabilities.setFlySpeed((float)(this.speed.getValue() / 100.0));
            Freecam.mc.player.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        EntityPlayerSP localPlayer = Freecam.mc.player;
        if (localPlayer != null) {
            Freecam.mc.player.setPositionAndRotation(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            Freecam.mc.world.removeEntityFromWorld(-100);
            this.clonedPlayer = null;
            this.posZ = 0.0;
            this.posY = 0.0;
            this.posX = 0.0;
            this.yaw = 0.0f;
            this.pitch = 0.0f;
            Freecam.mc.player.capabilities.isFlying = false;
            Freecam.mc.player.capabilities.setFlySpeed(0.05f);
            Freecam.mc.player.noClip = false;
            Freecam.mc.player.motionZ = 0.0;
            Freecam.mc.player.motionY = 0.0;
            Freecam.mc.player.motionX = 0.0;
            if (this.isRidingEntity) {
                Freecam.mc.player.startRiding(this.ridingEntity, true);
            }
        }
    }

    @Override
    public void onUpdate() {
        Freecam.mc.player.capabilities.isFlying = true;
        Freecam.mc.player.capabilities.setFlySpeed((float)(this.speed.getValue() / 100.0));
        Freecam.mc.player.noClip = true;
        Freecam.mc.player.onGround = false;
        Freecam.mc.player.fallDistance = 0.0f;
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        Freecam.mc.player.noClip = true;
    }

    @SubscribeEvent
    public void onPlayerPushOutOfBlock(PlayerSPPushOutOfBlocksEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent event) {
        if ((event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) && this.packet.getValue().booleanValue()) {
            event.setCanceled(true);
        }
    }
}

