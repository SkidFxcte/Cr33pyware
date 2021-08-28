package me.alpha432.oyvey.features.modules.render;

import net.minecraft.util.EnumHand;
import me.alpha432.oyvey.util.Wrapper;
import me.alpha432.oyvey.features.modules.Module;

public class OffhandSwing
    extends Module {
    public OffhandSwing() {
        super("Swing your offhand", "Susy hand", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (Wrapper.mc.world == null) {
            return;
        }
        Wrapper.getPlayer().swingingHand = EnumHand.OFF_HAND;
    }
}