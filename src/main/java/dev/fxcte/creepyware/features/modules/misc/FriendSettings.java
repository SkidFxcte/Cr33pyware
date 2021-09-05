package dev.fxcte.creepyware.features.modules.misc;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;

public class FriendSettings extends Module {
    private static FriendSettings INSTANCE;

    public Setting<Boolean> notify = this.register(new Setting("Speed", "Notify", 0.0, 0.0, false, 0));


    public FriendSettings(){
        super("FriendSettings", "Change aspects of friends", Category.MISC, true, false, false);
        INSTANCE = this;
    }

    public static FriendSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FriendSettings();
        }
        return INSTANCE;
    }

}