package dev.fxcte.creepyware.features.modules.client;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.Util;

public class Media
        extends Module {
    private static Media instance;
    public final Setting<Boolean> changeOwn = this.register(new Setting<Boolean>("Speed", "MyName", 0.0, 0.0, true, 0));
    public final Setting<String> ownName = this.register(new Setting<Object>("Name", "Name here...", v -> this.changeOwn.getValue()));

    public Media() {
        super("Media", "Helps with creating Media", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    public static Media getInstance() {
        if (instance == null) {
            instance = new Media();
        }
        return instance;
    }

    public static String getPlayerName() {
        if (Media.fullNullCheck() || !ServerModule.getInstance().isConnected()) {
            return Util.mc.getSession().getUsername();
        }
        String name = ServerModule.getInstance().getPlayerName();
        if (name == null || name.isEmpty()) {
            return Util.mc.getSession().getUsername();
        }
        return name;
    }
}

