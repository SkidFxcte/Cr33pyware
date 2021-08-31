package dev.fxcte.creepyware.features.modules.movement;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraft.init.Blocks;

public class IceSpeed
        extends Module {
    private Setting<Float> speed = this.register(new Setting<Float>("Speed", Float.valueOf(0.4f), Float.valueOf(0.2f), Float.valueOf(1.5f)));
    private static IceSpeed INSTANCE = new IceSpeed();

    public IceSpeed() {
        super("IceSpeed", "Speeds you up on ice.", Module.Category.MOVEMENT, false, false, false);
        INSTANCE = this;
    }

    public static IceSpeed getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new IceSpeed();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        Blocks.ICE.slipperiness = this.speed.getValue().floatValue();
        Blocks.PACKED_ICE.slipperiness = this.speed.getValue().floatValue();
        Blocks.FROSTED_ICE.slipperiness = this.speed.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }
}

