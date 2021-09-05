package dev.fxcte.creepyware.features.modules.combat;

import java.util.Comparator;
import dev.fxcte.creepyware.util.BlockUtil;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.features.modules.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BedAura
        extends Module {
    boolean moving = false;
    Setting<Double> range = this.register(new Setting<Double>("Range", 4.5, 0.0, 10.0));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Speed", "Rotate", 0.0, 0.0, true, 0));
    Setting<Boolean> dimensionCheck = this.register(new Setting<Boolean>("Speed", "DimensionCheck", 0.0, 0.0, true, 0));
    Setting<Boolean> refill = this.register(new Setting<Boolean>("Speed", "RefillBed", 0.0, 0.0, true, 0));

    public BedAura() {
        super("BedAura", "Fucked (Future)", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.refill.getValue().booleanValue()) {
            int slot = -1;
            for (int i = 0; i < 9; ++i) {
                if (BedAura.mc.player.inventory.getStackInSlot(i) != ItemStack.EMPTY) continue;
                slot = i;
                break;
            }
            if (this.moving && slot != -1) {
                BedAura.mc.playerController.windowClick(0, slot + 36, 0, ClickType.PICKUP, (EntityPlayer) BedAura.mc.player);
                this.moving = false;
                slot = -1;
            }
            if (slot != -1 && !(BedAura.mc.currentScreen instanceof GuiContainer) && BedAura.mc.player.inventory.getItemStack().isEmpty()) {
                int t = -1;
                for (int i = 0; i < 45; ++i) {
                    if (BedAura.mc.player.inventory.getStackInSlot(i).getItem() != Items.BED || i < 9) continue;
                    t = i;
                    break;
                }
                if (t != -1) {
                    BedAura.mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, (EntityPlayer) BedAura.mc.player);
                    this.moving = true;
                }
            }
        }
        BedAura.mc.world.loadedTileEntityList.stream().filter(e -> e instanceof TileEntityBed).filter(e -> BedAura.mc.player.getDistance((double)e.getPos().getX(), (double)e.getPos().getY(), (double)e.getPos().getZ()) <= this.range.getValue()).sorted(Comparator.comparing(e -> BedAura.mc.player.getDistance((double)e.getPos().getX(), (double)e.getPos().getY(), (double)e.getPos().getZ()))).forEach(bed -> {
            if (this.dimensionCheck.getValue().booleanValue() && BedAura.mc.player.dimension == 0) {
                return;
            }
            if (this.rotate.getValue().booleanValue()) {
                BlockUtil.faceVectorPacketInstant(new Vec3d((Vec3i)bed.getPos().add(0.5, 0.5, 0.5)));
            }
            BedAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(bed.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        });
    }
}

