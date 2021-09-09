package dev.fxcte.creepyware.features.modules.combat;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.util.EntityUtil;
import dev.fxcte.creepyware.util.MathUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.Vec3d;

public
class BowAim extends Module {
    public
    BowAim() {
        super("BowAim", "Automatically aim ur bow at ppl because lazy.", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public
    void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            EntityPlayer player = null;
            float tickDis = 100f;
            for (EntityPlayer p : mc.world.playerEntities) {
                if (p instanceof EntityPlayerSP || CreepyWare.friendManager.isFriend(p.getName())) continue;
                float dis = p.getDistance(mc.player);
                if (dis < tickDis) {
                    tickDis = dis;
                    player = p;
                }
            }
            if (player != null) {
                Vec3d pos = EntityUtil.getInterpolatedPos(player, mc.getRenderPartialTicks());
                float[] angels = MathUtil.calcAngle(EntityUtil.getInterpolatedPos(mc.player, mc.getRenderPartialTicks()), pos);
                mc.player.rotationYaw = angels[0];
                mc.player.rotationPitch = angels[1];
            }
        }
    }
}