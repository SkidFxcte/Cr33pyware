package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.DiscordPresence;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.*;

public class RPC
extends Module {
    public static RPC INSTANCE;
    public Setting<Boolean> showIP = this.register(new Setting<Boolean>("IP", false));
    public Setting<Boolean> users = this.register(new Setting<Boolean>("Users", false));

    public RPC() {
        super("RPC", "Discord rich presence", Module.Category.CLIENT, false, false, false);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        DiscordPresence.start();
    }

    @Override
    public void onDisable() {
        DiscordPresence.stop();
    }
}
