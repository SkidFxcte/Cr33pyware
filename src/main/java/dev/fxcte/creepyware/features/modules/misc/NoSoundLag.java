package dev.fxcte.creepyware.features.modules.misc;

import com.google.common.collect.Sets;
import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.modules.combat.AutoCrystal;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Set;

public
class NoSoundLag
        extends Module {
    private static final Set <SoundEvent> BLACKLIST;
    private static NoSoundLag instance;

    static {
        BLACKLIST = Sets.newHashSet(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER);
    }

    public Setting <Boolean> crystals = this.register(new Setting <>("Speed", "Crystals", 0.0, 0.0, true, 0));
    public Setting <Boolean> armor = this.register(new Setting <>("Speed", "Armor", 0.0, 0.0, true, 0));
    public Setting <Float> soundRange = this.register(new Setting <>("SoundRange", 12.0f, 0.0f, 12.0f));

    public
    NoSoundLag() {
        super("NoSoundLag", "Prevents Lag through sound spam.", Module.Category.MISC, true, false, false);
        instance = this;
    }

    public static
    NoSoundLag getInstance() {
        if (instance == null) {
            instance = new NoSoundLag();
        }
        return instance;
    }

    public static
    void removeEntities(SPacketSoundEffect packet, float range) {
        BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        ArrayList <Entity> toRemove = new ArrayList <>();
        if (NoSoundLag.fullNullCheck()) return;
        for (Entity entity : NoSoundLag.mc.world.loadedEntityList) {
            if (! (entity instanceof EntityEnderCrystal) || ! (entity.getDistanceSq(pos) <= MathUtil.square(range)))
                continue;
            toRemove.add(entity);
        }
        for (Entity entity : toRemove) {
            entity.setDead();
        }
    }

    @SubscribeEvent
    public
    void onPacketReceived(PacketEvent.Receive event) {
        if (event != null && event.getPacket() != null && NoSoundLag.mc.player != null && NoSoundLag.mc.world != null && event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = event.getPacket();
            if (this.crystals.getValue() && packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && (AutoCrystal.getInstance().isOff() || ! AutoCrystal.getInstance().sound.getValue() && AutoCrystal.getInstance().threadMode.getValue() != AutoCrystal.ThreadMode.SOUND)) {
                NoSoundLag.removeEntities(packet, this.soundRange.getValue());
            }
            if (BLACKLIST.contains(packet.getSound()) && this.armor.getValue()) {
                event.setCanceled(true);
            }
        }
    }
}
