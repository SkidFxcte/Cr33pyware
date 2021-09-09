package dev.fxcte.creepyware.features.modules.misc;

import dev.fxcte.creepyware.features.modules.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import java.util.Random;

public
class FxcteSexDupe
        extends Module {

    private final Random random = new Random();

    public
    FxcteSexDupe() {
        super("FxcteSexDupe", "Hacker shit", Module.Category.MISC, true, false, false);
    }

    public
    void onEnable() {
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;

        if (player == null || mc.world == null) return;

        ItemStack itemStack = player.getHeldItemMainhand();

        if (itemStack.isEmpty()) {
            setDisabledMessage("You need an object in your hand to dupe");
            disable();
            return;
        }

        int count = random.nextInt(31) + 1;

        for (int i = 0; i <= count; i++) {
            EntityItem entityItem = player.dropItem(itemStack.copy(), false, true);
            if (entityItem != null) world.addEntityToWorld(entityItem.entityId, entityItem);
        }

        int total = count * itemStack.getCount();
        player.sendChatMessage("I'm cum inside Fxcte and i got " + total + " " + itemStack.getDisplayName() + " thanks to creepyware");
        disable();
    }

    private
    void setDisabledMessage(String s) {
    }
}