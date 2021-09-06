package dev.fxcte.creepyware.features.modules.combat;

import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.event.events.ProcessRightClickBlockEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.EntityUtil;
import dev.fxcte.creepyware.util.InventoryUtil;
import dev.fxcte.creepyware.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public
class AutoTotem
        extends Module {
    private static AutoTotem instance;
    private final Queue < InventoryUtil.Task > taskList = new ConcurrentLinkedQueue <> ( );
    private final Timer timer = new Timer ( );
    private final Timer secondTimer = new Timer ( );
    public Setting < Boolean > crystal = this.register ( new Setting <> ("Speed", true) );
    public Setting < Float > crystalHealth = this.register ( new Setting <> ( "CrystalHP" , 13.0f , 0.1f , 36.0f ) );
    public Setting < Float > crystalHoleHealth = this.register ( new Setting <> ( "CrystalHoleHP" , 3.5f , 0.1f , 36.0f ) );
    public Setting < Boolean > gapple = this.register ( new Setting <> ("Speed", true) );
    public Setting < Boolean > armorCheck = this.register ( new Setting <> ("Speed", true) );
    public Setting < Integer > actions = this.register ( new Setting <> ( "Actions" , 4 , 1 , 4 ) );
    public Mode2 currentMode = Mode2.TOTEMS;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot = - 1;
    public int lastGappleSlot = - 1;
    public int lastCrystalSlot = - 1;
    public int lastObbySlot = - 1;
    public int lastWebSlot = - 1;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    private boolean second;
    private boolean switchedForHealthReason;

    public AutoTotem( ) {
        super ( "AutoTotem" , "Allows you to switch up your Offhand." , Module.Category.COMBAT , true , false , false );
        instance = this;
    }

    public static AutoTotem getInstance ( ) {
        if ( instance == null ) {
            instance = new AutoTotem( );
        }
        return instance;
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( ProcessRightClickBlockEvent event ) {
        if ( event.hand == EnumHand.MAIN_HAND && event.stack.getItem ( ) == Items.END_CRYSTAL && AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && AutoTotem.mc.objectMouseOver != null && event.pos == AutoTotem.mc.objectMouseOver.getBlockPos ( ) ) {
            event.setCanceled ( true );
            AutoTotem.mc.player.setActiveHand ( EnumHand.OFF_HAND );
            AutoTotem.mc.playerController.processRightClick ( AutoTotem.mc.player , AutoTotem.mc.world , EnumHand.OFF_HAND );
        }
    }

    @Override
    public
    void onUpdate ( ) {
        if ( this.timer.passedMs ( 50L ) ) {
            if ( AutoTotem.mc.player != null && AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && AutoTotem.mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.END_CRYSTAL && Mouse.isButtonDown ( 1 ) ) {
                AutoTotem.mc.player.setActiveHand ( EnumHand.OFF_HAND );
                AutoTotem.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown ( 1 );
            }
        } else if ( AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && AutoTotem.mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.END_CRYSTAL ) {
            AutoTotem.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if ( AutoTotem.nullCheck ( ) ) {
            return;
        }
        this.doOffhand ( );
        if ( this.secondTimer.passedMs ( 50L ) && this.second ) {
            this.second = false;
            this.timer.reset ( );
        }
    }

    @SubscribeEvent
    public
    void onPacketSend ( PacketEvent.Send event ) {
        if ( ! AutoTotem.fullNullCheck ( ) && AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && AutoTotem.mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.END_CRYSTAL && AutoTotem.mc.gameSettings.keyBindUseItem.isKeyDown ( ) ) {
            if ( event.getPacket ( ) instanceof CPacketPlayerTryUseItemOnBlock ) {
                CPacketPlayerTryUseItemOnBlock packet2 = event.getPacket ( );
                if ( packet2.getHand ( ) == EnumHand.MAIN_HAND ) {
                    if ( this.timer.passedMs ( 50L ) ) {
                        AutoTotem.mc.player.setActiveHand ( EnumHand.OFF_HAND );
                        AutoTotem.mc.player.connection.sendPacket ( new CPacketPlayerTryUseItem ( EnumHand.OFF_HAND ) );
                    }
                    event.setCanceled ( true );
                }
            } else if ( event.getPacket ( ) instanceof CPacketPlayerTryUseItem && ( (CPacketPlayerTryUseItem) event.getPacket ( ) ).getHand ( ) == EnumHand.OFF_HAND && ! this.timer.passedMs ( 50L ) ) {
                event.setCanceled ( true );
            }
        }
    }

    @Override
    public
    String getDisplayInfo ( ) {
        if ( AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.END_CRYSTAL ) {
            return "Crystals";
        }
        if ( AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.TOTEM_OF_UNDYING ) {
            return "Totems";
        }
        if ( AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE ) {
            return "Gapples";
        }
        return null;
    }

    public
    void doOffhand ( ) {
        this.didSwitchThisTick = false;
        this.holdingCrystal = AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.END_CRYSTAL;
        this.holdingTotem = AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.TOTEM_OF_UNDYING;
        this.holdingGapple = AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE;
        this.totems = AutoTotem.mc.player.inventory.mainInventory.stream ( ).filter (itemStack -> itemStack.getItem ( ) == Items.TOTEM_OF_UNDYING ).mapToInt ( ItemStack::getCount ).sum ( );
        if ( this.holdingTotem ) {
            this.totems += AutoTotem.mc.player.inventory.offHandInventory.stream ( ).filter (itemStack -> itemStack.getItem ( ) == Items.TOTEM_OF_UNDYING ).mapToInt ( ItemStack::getCount ).sum ( );
        }
        this.crystals = AutoTotem.mc.player.inventory.mainInventory.stream ( ).filter (itemStack -> itemStack.getItem ( ) == Items.END_CRYSTAL ).mapToInt ( ItemStack::getCount ).sum ( );
        if ( this.holdingCrystal ) {
            this.crystals += AutoTotem.mc.player.inventory.offHandInventory.stream ( ).filter (itemStack -> itemStack.getItem ( ) == Items.END_CRYSTAL ).mapToInt ( ItemStack::getCount ).sum ( );
        }
        this.gapples = AutoTotem.mc.player.inventory.mainInventory.stream ( ).filter (itemStack -> itemStack.getItem ( ) == Items.GOLDEN_APPLE ).mapToInt ( ItemStack::getCount ).sum ( );
        if ( this.holdingGapple ) {
            this.gapples += AutoTotem.mc.player.inventory.offHandInventory.stream ( ).filter (itemStack -> itemStack.getItem ( ) == Items.GOLDEN_APPLE ).mapToInt ( ItemStack::getCount ).sum ( );
        }
        this.doSwitch ( );
    }

    public
    void doSwitch ( ) {
        this.currentMode = Mode2.TOTEMS;
        if ( this.gapple.getValue ( ) && AutoTotem.mc.player.getHeldItemMainhand ( ).getItem ( ) instanceof ItemSword && AutoTotem.mc.gameSettings.keyBindUseItem.isKeyDown ( ) ) {
            this.currentMode = Mode2.GAPPLES;
        } else if ( this.currentMode != Mode2.CRYSTALS && this.crystal.getValue ( ) && ( EntityUtil.isSafe ( AutoTotem.mc.player ) && EntityUtil.getHealth ( AutoTotem.mc.player , true ) > this.crystalHoleHealth.getValue ( ) || EntityUtil.getHealth ( AutoTotem.mc.player , true ) > this.crystalHealth.getValue ( ) ) ) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if ( this.currentMode == Mode2.CRYSTALS && this.crystals == 0 ) {
            this.setMode ( Mode2.TOTEMS );
        }
        if ( this.currentMode == Mode2.CRYSTALS && ( ! EntityUtil.isSafe ( AutoTotem.mc.player ) && EntityUtil.getHealth ( AutoTotem.mc.player , true ) <= this.crystalHealth.getValue ( ) || EntityUtil.getHealth ( AutoTotem.mc.player , true ) <= this.crystalHoleHealth.getValue ( ) ) ) {
            if ( this.currentMode == Mode2.CRYSTALS ) {
                this.switchedForHealthReason = true;
            }
            this.setMode ( Mode2.TOTEMS );
        }
        if ( this.switchedForHealthReason && ( EntityUtil.isSafe ( AutoTotem.mc.player ) && EntityUtil.getHealth ( AutoTotem.mc.player , true ) > this.crystalHoleHealth.getValue ( ) || EntityUtil.getHealth ( AutoTotem.mc.player , true ) > this.crystalHealth.getValue ( ) ) ) {
            this.setMode ( Mode2.CRYSTALS );
            this.switchedForHealthReason = false;
        }
        if ( this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue ( ) && ( AutoTotem.mc.player.getItemStackFromSlot ( EntityEquipmentSlot.CHEST ).getItem ( ) == Items.AIR || AutoTotem.mc.player.getItemStackFromSlot ( EntityEquipmentSlot.HEAD ).getItem ( ) == Items.AIR || AutoTotem.mc.player.getItemStackFromSlot ( EntityEquipmentSlot.LEGS ).getItem ( ) == Items.AIR || AutoTotem.mc.player.getItemStackFromSlot ( EntityEquipmentSlot.FEET ).getItem ( ) == Items.AIR ) ) {
            this.setMode ( Mode2.TOTEMS );
        }
        if ( AutoTotem.mc.currentScreen instanceof GuiContainer && ! ( AutoTotem.mc.currentScreen instanceof GuiInventory ) ) {
            return;
        }
        Item currentOffhandTolonEditionItem = AutoTotem.mc.player.getHeldItemOffhand ( ).getItem ( );
        switch (this.currentMode) {
            case TOTEMS: {
                if ( this.totems <= 0 || this.holdingTotem ) break;
                this.lastTotemSlot = InventoryUtil.findItemInventorySlot ( Items.TOTEM_OF_UNDYING , false );
                int lastSlot = this.getLastSlot ( currentOffhandTolonEditionItem , this.lastTotemSlot );
                this.putItemInOffhandTolonEdition ( this.lastTotemSlot , lastSlot );
                break;
            }
            case GAPPLES: {
                if ( this.gapples <= 0 || this.holdingGapple ) break;
                this.lastGappleSlot = InventoryUtil.findItemInventorySlot ( Items.GOLDEN_APPLE , false );
                int lastSlot = this.getLastSlot ( currentOffhandTolonEditionItem , this.lastGappleSlot );
                this.putItemInOffhandTolonEdition ( this.lastGappleSlot , lastSlot );
                break;
            }
            default: {
                if ( this.crystals <= 0 || this.holdingCrystal ) break;
                this.lastCrystalSlot = InventoryUtil.findItemInventorySlot ( Items.END_CRYSTAL , false );
                int lastSlot = this.getLastSlot ( currentOffhandTolonEditionItem , this.lastCrystalSlot );
                this.putItemInOffhandTolonEdition ( this.lastCrystalSlot , lastSlot );
            }
        }
        for (int i = 0; i < this.actions.getValue ( ); ++ i) {
            InventoryUtil.Task task = this.taskList.poll ( );
            if ( task == null ) continue;
            task.run ( );
            if ( ! task.isSwitching ( ) ) continue;
            this.didSwitchThisTick = true;
        }
    }

    private
    int getLastSlot ( Item item , int slotIn ) {
        if ( item == Items.END_CRYSTAL ) {
            return this.lastCrystalSlot;
        }
        if ( item == Items.GOLDEN_APPLE ) {
            return this.lastGappleSlot;
        }
        if ( item == Items.TOTEM_OF_UNDYING ) {
            return this.lastTotemSlot;
        }
        if ( InventoryUtil.isBlock ( item , BlockObsidian.class ) ) {
            return this.lastObbySlot;
        }
        if ( InventoryUtil.isBlock ( item , BlockWeb.class ) ) {
            return this.lastWebSlot;
        }
        if ( item == Items.AIR ) {
            return - 1;
        }
        return slotIn;
    }

    private
    void putItemInOffhandTolonEdition ( int slotIn , int slotOut ) {
        if ( slotIn != - 1 && this.taskList.isEmpty ( ) ) {
            this.taskList.add ( new InventoryUtil.Task ( slotIn ) );
            this.taskList.add ( new InventoryUtil.Task ( 45 ) );
            this.taskList.add ( new InventoryUtil.Task ( slotOut ) );
            this.taskList.add ( new InventoryUtil.Task ( ) );
        }
    }

    public
    void setMode ( Mode2 mode ) {
        this.currentMode = this.currentMode == mode ? Mode2.TOTEMS : mode;
    }

    public
    enum Mode2 {
        TOTEMS,
        GAPPLES,
        CRYSTALS

    }
}