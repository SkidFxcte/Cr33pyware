package dev.fxcte.creepyware.features.modules.client;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;

public
class ModuleTools extends Module {

    private static ModuleTools INSTANCE;

    public Setting <Notifier> notifier = register(new Setting("Speed", "ModuleNotifier", 0.0, 0.0, Notifier.FUTURE, 0));
    public Setting <PopNotifier> popNotifier = register(new Setting("Speed", "PopNotifier", 0.0, 0.0, PopNotifier.FUTURE, 0));

    public
    ModuleTools() {
        super("ModuleTools", "Change settings", Module.Category.CLIENT, true, false, false);
        INSTANCE = this;
    }


    public static
    ModuleTools getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleTools();
        }
        return INSTANCE;
    }


    public
    enum Notifier {
        PHOBOS,
        FUTURE,
    }

    public
    enum PopNotifier {
        PHOBOS,
        FUTURE,
        NONE
    }


}
