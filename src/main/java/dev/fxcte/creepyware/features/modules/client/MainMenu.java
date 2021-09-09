package dev.fxcte.creepyware.features.modules.client;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;

public
class MainMenu
        extends Module {
    public static MainMenu INSTANCE;
    public Setting <Boolean> mainScreen = this.register(new Setting <>("MainScreen" , false));

    public
    MainMenu() {
        super("MainMenu" , "Controls custom screens used by the client" , Module.Category.CLIENT , true , false , false);
        INSTANCE = this;
    }

    @Override
    public
    void onTick() {
    }
}

