package dev.fxcte.creepyware.features.modules.render;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;

public class SmallShield
        extends Module {
    private static SmallShield INSTANCE = new SmallShield();
    public Setting<Boolean> normalOffset = this.register(new Setting <> ("Speed" , "OffNormal" , 0.0 , 0.0 , false , 0));
    public Setting<Float> offset = this.register(new Setting<Object>("Offset", 0.7f , 0.0f , 1.0f , v -> this.normalOffset.getValue()));
    public Setting<Float> offX = this.register(new Setting<Object>("OffX", 0.0f , - 1.0f , 1.0f , v -> this.normalOffset.getValue() == false));
    public Setting<Float> offY = this.register(new Setting<Object>("OffY", 0.0f , - 1.0f , 1.0f , v -> this.normalOffset.getValue() == false));
    public Setting<Float> mainX = this.register(new Setting <> ("MainX" , 0.0f , - 1.0f , 1.0f));
    public Setting<Float> mainY = this.register(new Setting <> ("MainY" , 0.0f , - 1.0f , 1.0f));

    public SmallShield() {
        super("SmallShield", "Makes you offhand lower.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static SmallShield getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new SmallShield();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.normalOffset.getValue ()) {
            SmallShield.mc.entityRenderer.itemRenderer.equippedProgressOffHand = this.offset.getValue ();
        }
    }
}

