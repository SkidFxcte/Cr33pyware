package dev.fxcte.creepyware.features.modules.player;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;

public class NoEntityTrace
        extends Module {
    private static NoEntityTrace INSTANCE = new NoEntityTrace();
    public Setting<Boolean> pickaxe = this.register(new Setting <> ("Speed" , "Pickaxe" , 0.0 , 0.0 , true , 0));
    public Setting<Boolean> crystal = this.register(new Setting <> ("Speed" , "Crystal" , 0.0 , 0.0 , true , 0));
    public Setting<Boolean> gapple = this.register(new Setting <> ("Speed" , "Gapple" , 0.0 , 0.0 , true , 0));

    public NoEntityTrace() {
        super("NoEntityTrace", "NoEntityTrace.", Module.Category.MISC, false, false, false);
        this.setInstance();
    }

    public static NoEntityTrace getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new NoEntityTrace();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}