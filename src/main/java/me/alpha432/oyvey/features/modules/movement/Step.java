package me.alpha432.oyvey.features.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import me.alpha432.oyvey.util.Wrapper;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.Minecraft;
import me.alpha432.oyvey.features.modules.Module;

public class Step extends Module
{
    Minecraft mc;
    private Setting<Mode> mode;
    private Setting<Float> height;
    private float oldStepHeight;

    public Step() {
        super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
        this.mc = Minecraft.getMinecraft();
        this.mode = (Setting<Mode>)this.register("Mode", Mode.VANILLA);
        this.height = (Setting<Float>)this.register("Height", 2.0f, 0.1f, 3.0f);
        this.oldStepHeight = -1.0f;
    }

    private Object register(String height, float v, float v1, float v2){
        return null;
    }

    private Object register(String mode, Mode vanilla) {
        return null;
    }

    public void onEnable() {
        if (Wrapper.getPlayer() != null && this.mode.getValue().equals(Mode.VANILLA)) {
            this.oldStepHeight = Wrapper.getPlayer().stepHeight;
            Wrapper.getPlayer().stepHeight = this.height.getValue();
        }
    }

    public void onDisable() {
        if (this.mode.getValue().equals(Mode.VANILLA) && Wrapper.getPlayer() != null && this.oldStepHeight != -1.0f) {
            Wrapper.getPlayer().stepHeight = this.oldStepHeight;
        }
        this.oldStepHeight = -1.0f;
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue().equals(Mode.BETTER) && Wrapper.getPlayer() != null) {
            if (!this.mc.player.collidedHorizontally) {
                return;
            }
            if (!this.mc.player.onGround || this.mc.player.isOnLadder() || this.mc.player.isInWater() || this.mc.player.isInLava() || this.mc.player.movementInput.jump || this.mc.player.noClip) {
                return;
            }
            if (this.mc.player.moveForward == 0.0f && this.mc.player.moveStrafing == 0.0f) {
                return;
            }
            final double n = this.get_n_normal();
            if (n < 0.0 || n > 2.0) {
                return;
            }
            if (n == 2.0) {
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.42, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.78, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.63, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.51, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.9, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.21, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.45, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.43, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.setPosition(this.mc.player.posX, this.mc.player.posY + 2.0, this.mc.player.posZ);
            }
            if (n == 1.5) {
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.41999998688698, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.7531999805212, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.00133597911214, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.16610926093821, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.24918707874468, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.1707870772188, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.setPosition(this.mc.player.posX, this.mc.player.posY + 1.0, this.mc.player.posZ);
            }
            if (n == 1.0) {
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.41999998688698, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.7531999805212, this.mc.player.posZ, this.mc.player.onGround));
                this.mc.player.setPosition(this.mc.player.posX, this.mc.player.posY + 1.0, this.mc.player.posZ);
            }
        }
    }

    public double get_n_normal() {
        this.mc.player.stepHeight = 0.5f;
        double max_y = -1.0;
        final AxisAlignedBB grow = this.mc.player.getEntityBoundingBox().offset(0.0, 0.05, 0.0).grow(0.05);
        if (!this.mc.world.getCollisionBoxes((Entity)this.mc.player, grow.offset(0.0, 2.0, 0.0)).isEmpty()) {
            return 100.0;
        }
        for (final AxisAlignedBB aabb : this.mc.world.getCollisionBoxes((Entity)this.mc.player, grow)) {
            if (aabb.maxY > max_y) {
                max_y = aabb.maxY;
            }
        }
        return max_y - this.mc.player.posY;
    }

    public enum Mode
    {
        VANILLA,
        BETTER;
    }
}
