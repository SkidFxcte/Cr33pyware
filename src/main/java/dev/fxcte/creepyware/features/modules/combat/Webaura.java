package dev.fxcte.creepyware.features.modules.combat;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.event.events.UpdateWalkingPlayerEvent;
import dev.fxcte.creepyware.features.command.Command;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.modules.client.ClickGui;
import dev.fxcte.creepyware.features.modules.client.ServerModule;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.*;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Webaura
        extends Module {
    public static boolean isPlacing = false;
    private final Setting<Boolean> server = this.register(new Setting <> ("Speed" , "Server" , 0.0 , 0.0 , false , 0));
    private final Setting<Integer> delay = this.register(new Setting <> ("Delay/Place" , 50 , 0 , 250));
    private final Setting<Integer> blocksPerPlace = this.register(new Setting <> ("Block/Place" , 8 , 1 , 30));
    private final Setting<Double> targetRange = this.register(new Setting <> ("TargetRange" , 10.0 , 0.0 , 20.0));
    private final Setting<Double> range = this.register(new Setting <> ("PlaceRange" , 6.0 , 0.0 , 10.0));
    private final Setting<TargetMode> targetMode = this.register(new Setting <> ("Speed" , "Target" , 0.0 , 0.0 , TargetMode.CLOSEST , 0));
    private final Setting<InventoryUtil.Switch> switchMode = this.register(new Setting <> ("Speed" , "Switch" , 0.0 , 0.0 , InventoryUtil.Switch.NORMAL , 0));
    private final Setting<Boolean> rotate = this.register(new Setting <> ("Speed" , "Rotate" , 0.0 , 0.0 , true , 0));
    private final Setting<Boolean> raytrace = this.register(new Setting <> ("Speed" , "Raytrace" , 0.0 , 0.0 , false , 0));
    private final Setting<Double> speed = this.register(new Setting <> ("Speed" , 30.0 , 0.0 , 30.0));
    private final Setting<Boolean> upperBody = this.register(new Setting <> ("Speed" , "Upper" , 0.0 , 0.0 , false , 0));
    private final Setting<Boolean> lowerbody = this.register(new Setting <> ("Speed" , "Lower" , 0.0 , 0.0 , true , 0));
    private final Setting<Boolean> ylower = this.register(new Setting <> ("Speed" , "Y-1" , 0.0 , 0.0 , false , 0));
    private final Setting<Boolean> antiSelf = this.register(new Setting <> ("Speed" , "AntiSelf" , 0.0 , 0.0 , false , 0));
    private final Setting<Integer> eventMode = this.register(new Setting <> ("Updates" , 3 , 1 , 3));
    private final Setting<Boolean> freecam = this.register(new Setting <> ("Speed" , "Freecam" , 0.0 , 0.0 , false , 0));
    private final Setting<Boolean> info = this.register(new Setting <> ("Speed" , "Info" , 0.0 , 0.0 , false , 0));
    private final Setting<Boolean> disable = this.register(new Setting <> ("Speed" , "TSelfMove" , 0.0 , 0.0 , false , 0));
    private final Setting<Boolean> packet = this.register(new Setting <> ("Speed" , "Packet" , 0.0 , 0.0 , false , 0));
    private final Timer timer = new Timer();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private boolean smartRotate = false;
    private BlockPos startPos = null;

    public Webaura() {
        super("Webaura", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
    }

    private boolean shouldServer() {
        return ServerModule.getInstance().isConnected() && this.server.getValue() != false;
    }

    @Override
    public void onEnable() {
        if (Webaura.fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedBlockPos(Webaura.mc.player);
        this.lastHotbarSlot = Webaura.mc.player.inventory.currentItem;
        if (this.shouldServer()) {
            Webaura.mc.player.connection.sendPacket(new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
            Webaura.mc.player.connection.sendPacket(new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "module Webaura set Enabled true"));
        }
    }

    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.smartRotate = false;
            this.doTrap();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.smartRotate = this.rotate.getValue() != false && this.blocksPerPlace.getValue() == 1;
            this.doTrap();
        }
    }

    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.smartRotate = false;
            this.doTrap();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.info.getValue () && this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    @Override
    public void onDisable() {
        if (this.shouldServer()) {
            Webaura.mc.player.connection.sendPacket(new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
            Webaura.mc.player.connection.sendPacket(new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "module Webaura set Enabled false"));
            return;
        }
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }

    private void doTrap() {
        if (this.shouldServer() || this.check()) {
            return;
        }
        this.doWebTrap();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    private void doWebTrap() {
        List<Vec3d> placeTargets = this.getPlacements();
        this.placeList(placeTargets);
    }

    private List<Vec3d> getPlacements() {
        ArrayList<Vec3d> list = new ArrayList <> ();
        Vec3d baseVec = this.target.getPositionVector();
        if (this.ylower.getValue ()) {
            list.add(baseVec.add(0.0, -1.0, 0.0));
        }
        if (this.lowerbody.getValue ()) {
            list.add(baseVec);
        }
        if (this.upperBody.getValue ()) {
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        return list;
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(Webaura.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), Webaura.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (Vec3d vec3d3 : list) {
            BlockPos position = new BlockPos(vec3d3);
            int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
            if (placeability != 3 && placeability != 1 || this.antiSelf.getValue () && MathUtil.areVec3dsAligned(Webaura.mc.player.getPositionVector(), vec3d3))
                continue;
            this.placeBlock(position);
        }
    }

    private boolean check() {
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (this.isOff()) {
            return true;
        }
        if (this.disable.getValue () && !this.startPos.equals(EntityUtil.getRoundedBlockPos(Webaura.mc.player))) {
            this.disable();
            return true;
        }
        if (obbySlot == -1) {
            if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
                if (this.info.getValue ()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> " + "\u00a7c" + "You are out of Webs.");
                }
                this.disable();
            }
            return true;
        }
        if (Webaura.mc.player.inventory.currentItem != this.lastHotbarSlot && Webaura.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = Webaura.mc.player.inventory.currentItem;
        }
        this.switchItem(true);
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(this.targetRange.getValue(), this.targetMode.getValue() == TargetMode.UNTRAPPED);
        return this.target == null || CreepyWare.moduleManager.isModuleEnabled("Freecam") && this.freecam.getValue() == false || !this.timer.passedMs(this.delay.getValue ()) || this.switchMode.getValue() == InventoryUtil.Switch.NONE && Webaura.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockWeb.class);
    }

    private EntityPlayer getTarget(double range, boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : Webaura.mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range) || trapped && player.isInWeb || EntityUtil.getRoundedBlockPos(Webaura.mc.player).equals(EntityUtil.getRoundedBlockPos(player)) && this.antiSelf.getValue () || CreepyWare.speedManager.getPlayerSpeed(player) > this.speed.getValue())
                continue;
            if (target == null) {
                target = player;
                distance = Webaura.mc.player.getDistanceSq(player);
                continue;
            }
            if (!(Webaura.mc.player.getDistanceSq(player) < distance)) continue;
            target = player;
            distance = Webaura.mc.player.getDistanceSq(player);
        }
        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue() && Webaura.mc.player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue()) && this.switchItem(false)) {
            isPlacing = true;
            this.isSneaking = this.smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
            this.didPlace = true;
            ++this.placements;
        }
    }

    private boolean switchItem(boolean back) {
        boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), BlockWeb.class);
        this.switchedItem = value[0];
        return value[1];
    }

    public enum TargetMode {
        CLOSEST,
        UNTRAPPED

    }
}

