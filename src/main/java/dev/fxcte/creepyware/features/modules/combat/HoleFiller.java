package dev.fxcte.creepyware.features.modules.combat;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.event.events.UpdateWalkingPlayerEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.modules.client.ClickGui;
import dev.fxcte.creepyware.features.modules.client.ServerModule;
import dev.fxcte.creepyware.features.modules.player.Freecam;
import dev.fxcte.creepyware.features.setting.Bind;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.*;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HoleFiller
        extends Module {
    private static HoleFiller INSTANCE = new HoleFiller();
    private final Setting<Boolean> server = this.register(new Setting<Boolean>("Speed", "Server", 0.0, 0.0, false, 0));
    private final Setting<Double> range = this.register(new Setting<Double>("PlaceRange", 6.0, 0.0, 10.0));
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay/Place", 50, 0, 250));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Block/Place", 8, 1, 20));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Speed", "Rotate", 0.0, 0.0, true, 0));
    private final Setting<Boolean> raytrace = this.register(new Setting<Boolean>("Speed", "Raytrace", 0.0, 0.0, false, 0));
    private final Setting<Boolean> disable = this.register(new Setting<Boolean>("Speed", "Disable", 0.0, 0.0, true, 0));
    private final Setting<Integer> disableTime = this.register(new Setting<Integer>("Ms/Disable", 200, 1, 250));
    private final Setting<Boolean> offhand = this.register(new Setting<Boolean>("Speed", "OffHand", 0.0, 0.0, true, 0));
    private final Setting<InventoryUtil.Switch> switchMode = this.register(new Setting<InventoryUtil.Switch>("Speed", "Switch", 0.0, 0.0, InventoryUtil.Switch.NORMAL, 0));
    private final Setting<Boolean> onlySafe = this.register(new Setting<Object>("OnlySafe", Boolean.valueOf(true), v -> this.offhand.getValue()));
    private final Setting<Boolean> webSelf = this.register(new Setting<Boolean>("Speed", "SelfWeb", 0.0, 0.0, false, 0));
    private final Setting<Boolean> highWeb = this.register(new Setting<Boolean>("Speed", "HighWeb", 0.0, 0.0, false, 0));
    private final Setting<Boolean> freecam = this.register(new Setting<Boolean>("Speed", "Freecam", 0.0, 0.0, false, 0));
    private final Setting<Boolean> midSafeHoles = this.register(new Setting<Boolean>("Speed", "MidSafe", 0.0, 0.0, false, 0));
    private final Setting<Boolean> packet = this.register(new Setting<Boolean>("Speed", "Packet", 0.0, 0.0, false, 0));
    private final Setting<Boolean> onGroundCheck = this.register(new Setting<Boolean>("Speed", "OnGroundCheck", 0.0, 0.0, false, 0));
    private final Timer offTimer = new Timer();
    private final Timer timer = new Timer();
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private final Timer retryTimer = new Timer();
    public Setting<Mode> mode = this.register(new Setting<Mode>("Speed", "Mode", 0.0, 0.0, Mode.OBSIDIAN, 0));
    public Setting<PlaceMode> placeMode = this.register(new Setting<PlaceMode>("Speed", "PlaceMode", 0.0, 0.0, PlaceMode.ALL, 0));
    private final Setting<Double> smartRange = this.register(new Setting<Object>("SmartRange", Double.valueOf(6.0), Double.valueOf(0.0), Double.valueOf(10.0), v -> this.placeMode.getValue() == PlaceMode.SMART));
    public Setting<Bind> obbyBind = this.register(new Setting<Bind>("Speed", "Obsidian", 0.0, 0.0, new Bind(-1), 0));
    public Setting<Bind> webBind = this.register(new Setting<Bind>("Speed", "Webs", 0.0, 0.0, new Bind(-1), 0));
    public Mode currentMode = Mode.OBSIDIAN;
    private boolean accessedViaBind = false;
    private int targetSlot = -1;
    private int blocksThisTick = 0;
    private Offhand.Mode offhandMode = Offhand.Mode.CRYSTALS;
    private Offhand.Mode2 offhandMode2 = Offhand.Mode2.CRYSTALS;
    private boolean isSneaking;
    private boolean hasOffhand = false;
    private boolean placeHighWeb = false;
    private int lastHotbarSlot = -1;
    private boolean switchedItem = false;

    public HoleFiller() {
        super("HoleFiller", "Fills holes around you.", Module.Category.COMBAT, true, false, true);
        this.setInstance();
    }

    public static HoleFiller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleFiller();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private boolean shouldServer() {
        return ServerModule.getInstance().isConnected() && this.server.getValue() != false;
    }

    @Override
    public void onEnable() {
        if (HoleFiller.fullNullCheck()) {
            this.disable();
        }
        if (!HoleFiller.mc.player.onGround && this.onGroundCheck.getValue().booleanValue()) {
            return;
        }
        if (this.shouldServer()) {
            HoleFiller.mc.player.connection.sendPacket(new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
            HoleFiller.mc.player.connection.sendPacket(new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "module HoleFiller set Enabled true"));
            return;
        }
        this.lastHotbarSlot = HoleFiller.mc.player.inventory.currentItem;
        if (!this.accessedViaBind) {
            this.currentMode = this.mode.getValue();
        }
        Offhand module = CreepyWare.moduleManager.getModuleByClass(Offhand.class);
        this.offhandMode = module.mode;
        this.offhandMode2 = module.currentMode;
        if (this.offhand.getValue().booleanValue() && (EntityUtil.isSafe(HoleFiller.mc.player) || !this.onlySafe.getValue().booleanValue())) {
            if (module.type.getValue() == Offhand.Type.NEW) {
                if (this.currentMode == Mode.WEBS) {
                    module.setSwapToTotem(false);
                    module.setMode(Offhand.Mode.WEBS);
                } else {
                    module.setSwapToTotem(false);
                    module.setMode(Offhand.Mode.OBSIDIAN);
                }
            } else {
                if (this.currentMode == Mode.WEBS) {
                    module.setMode(Offhand.Mode2.WEBS);
                } else {
                    module.setMode(Offhand.Mode2.OBSIDIAN);
                }
                if (!module.didSwitchThisTick) {
                    module.doOffhand();
                }
            }
        }
        CreepyWare.holeManager.update();
        this.offTimer.reset();
    }

    @Override
    public void onTick() {
        if (this.isOn() && (this.blocksPerTick.getValue() != 1 || !this.rotate.getValue().booleanValue())) {
            this.doHoleFill();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.isOn() && event.getStage() == 0 && this.blocksPerTick.getValue() == 1 && this.rotate.getValue().booleanValue()) {
            this.doHoleFill();
        }
    }

    @Override
    public void onDisable() {
        if (this.offhand.getValue().booleanValue()) {
            CreepyWare.moduleManager.getModuleByClass(Offhand.class).setMode(this.offhandMode);
            CreepyWare.moduleManager.getModuleByClass(Offhand.class).setMode(this.offhandMode2);
        }
        this.switchItem(true);
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.retries.clear();
        this.accessedViaBind = false;
        this.hasOffhand = false;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            if (this.obbyBind.getValue().getKey() == Keyboard.getEventKey()) {
                this.accessedViaBind = true;
                this.currentMode = Mode.OBSIDIAN;
                this.toggle();
            }
            if (this.webBind.getValue().getKey() == Keyboard.getEventKey()) {
                this.accessedViaBind = true;
                this.currentMode = Mode.WEBS;
                this.toggle();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doHoleFill() {
        ArrayList<BlockPos> targets;
        Object object;
        if (this.check()) {
            return;
        }
        if (this.placeHighWeb) {
            BlockPos pos = new BlockPos(HoleFiller.mc.player.posX, HoleFiller.mc.player.posY + 1.0, HoleFiller.mc.player.posZ);
            this.placeBlock(pos);
            this.placeHighWeb = false;
        }
        if (this.midSafeHoles.getValue().booleanValue()) {
            object = CreepyWare.holeManager.getMidSafety();
            synchronized (object) {
                targets = new ArrayList<BlockPos>(CreepyWare.holeManager.getMidSafety());
            }
        }
        object = CreepyWare.holeManager.getHoles();
        synchronized (object) {
            targets = new ArrayList<BlockPos>(CreepyWare.holeManager.getHoles());
        }
        for (BlockPos position : targets) {
            int placeability;
            if (HoleFiller.mc.player.getDistanceSq(position) > MathUtil.square(this.range.getValue()) || this.placeMode.getValue() == PlaceMode.SMART && !this.isPlayerInRange(position))
                continue;
            if (position.equals(new BlockPos(HoleFiller.mc.player.getPositionVector()))) {
                if (this.currentMode != Mode.WEBS || !this.webSelf.getValue().booleanValue()) continue;
                if (this.highWeb.getValue().booleanValue()) {
                    this.placeHighWeb = true;
                }
            }
            if ((placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue())) == 1 && (this.currentMode == Mode.WEBS || this.switchMode.getValue() == InventoryUtil.Switch.SILENT && (this.currentMode == Mode.WEBS || this.retries.get(position) == null || this.retries.get(position) < 4))) {
                this.placeBlock(position);
                if (this.currentMode == Mode.WEBS) continue;
                this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
                continue;
            }
            if (placeability != 3) continue;
            this.placeBlock(position);
        }
    }

    private void placeBlock(BlockPos pos) {
        if (this.blocksThisTick < this.blocksPerTick.getValue() && this.switchItem(false)) {
            boolean smartRotate;
            boolean bl = smartRotate = this.blocksPerTick.getValue() == 1 && this.rotate.getValue() != false;
            this.isSneaking = smartRotate ? BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking) : BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
            this.timer.reset();
            ++this.blocksThisTick;
        }
    }

    private boolean isPlayerInRange(BlockPos pos) {
        for (EntityPlayer player : HoleFiller.mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, this.smartRange.getValue())) continue;
            return true;
        }
        return false;
    }

    private boolean check() {
        if (HoleFiller.fullNullCheck() || this.disable.getValue().booleanValue() && this.offTimer.passedMs(this.disableTime.getValue().intValue())) {
            this.disable();
            return true;
        }
        if (HoleFiller.mc.player.inventory.currentItem != this.lastHotbarSlot && HoleFiller.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(this.currentMode == Mode.WEBS ? BlockWeb.class : BlockObsidian.class)) {
            this.lastHotbarSlot = HoleFiller.mc.player.inventory.currentItem;
        }
        this.switchItem(true);
        if (!this.freecam.getValue().booleanValue() && CreepyWare.moduleManager.isModuleEnabled(Freecam.class)) {
            return true;
        }
        this.blocksThisTick = 0;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        switch (this.currentMode) {
            case WEBS: {
                this.hasOffhand = InventoryUtil.isBlock(HoleFiller.mc.player.getHeldItemOffhand().getItem(), BlockWeb.class);
                this.targetSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
                break;
            }
            case OBSIDIAN: {
                this.hasOffhand = InventoryUtil.isBlock(HoleFiller.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
                this.targetSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                break;
            }
        }
        if (this.onlySafe.getValue().booleanValue() && !EntityUtil.isSafe(HoleFiller.mc.player)) {
            this.disable();
            return true;
        }
        if (!this.hasOffhand && this.targetSlot == -1 && (!this.offhand.getValue().booleanValue() || !EntityUtil.isSafe(HoleFiller.mc.player) && this.onlySafe.getValue().booleanValue())) {
            return true;
        }
        if (this.offhand.getValue().booleanValue() && !this.hasOffhand) {
            return true;
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }

    private boolean switchItem(boolean back) {
        if (this.offhand.getValue().booleanValue()) {
            return true;
        }
        boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), this.currentMode == Mode.WEBS ? BlockWeb.class : BlockObsidian.class);
        this.switchedItem = value[0];
        return value[1];
    }

    public enum PlaceMode {
        SMART,
        ALL

    }

    public enum Mode {
        WEBS,
        OBSIDIAN

    }
}

