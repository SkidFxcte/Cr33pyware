package dev.fxcte.creepyware.features.modules.misc;

import dev.fxcte.creepyware.DiscordPresence;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;

public class RPC extends Module {
    public static RPC INSTANCE;
    public Setting<Boolean> showIP = this.register(new Setting<Boolean>("Speed", false));
    public Setting<Boolean> users = this.register(new Setting<Boolean>("Speed", false));
    public Setting<String> largeImageText = (Setting<String>) this.register(new Setting("LargeImageText", "CreepyWare"));
    public Setting<String> smallImageText = (Setting<String>) this.register(new Setting("SmallImageText", "UwU"));


    public RPC() {
        super("RPC", "Discord rich presence", Category.MISC, false, false, false);

        RPC.INSTANCE = this;
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
