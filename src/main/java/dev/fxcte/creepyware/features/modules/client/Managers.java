package dev.fxcte.creepyware.features.modules.client;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.event.events.ClientEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.TextUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Managers
        extends Module {
    private static Managers INSTANCE = new Managers();
    public Setting<Boolean> betterFrames = this.register(new Setting <> ("Speed" , "BetterMaxFPS" , 0.0 , 0.0 , false , 0));
    public Setting<String> commandBracket = this.register(new Setting <> ("Speed" , "Bracket" , 0.0 , 0.0 , "<" , 0));
    public Setting<String> commandBracket2 = this.register(new Setting <> ("Speed" , "Bracket2" , 0.0 , 0.0 , ">" , 0));
    public Setting<String> command = this.register(new Setting <> ("Speed" , "Command" , 0.0 , 0.0 , "CreepyWare" , 0));
    public Setting<Boolean> rainbowPrefix = this.register(new Setting <> ("Speed" , "RainbowPrefix" , 0.0 , 0.0 , false , 0));
    public Setting<TextUtil.Color> bracketColor = this.register(new Setting <> ("Speed" , "BColor" , 0.0 , 0.0 , TextUtil.Color.BLUE , 0));
    public Setting<TextUtil.Color> commandColor = this.register(new Setting <> ("Speed" , "CColor" , 0.0 , 0.0 , TextUtil.Color.BLUE , 0));
    public Setting<Integer> betterFPS = this.register(new Setting<Object>("MaxFPS", 300 , 30 , 1000 , v -> this.betterFrames.getValue()));
    public Setting<Boolean> potions = this.register(new Setting <> ("Speed" , "Potions" , 0.0 , 0.0 , true , 0));
    public Setting<Integer> textRadarUpdates = this.register(new Setting <> ("TRUpdates" , 500 , 0 , 1000));
    public Setting<Integer> respondTime = this.register(new Setting <> ("SeverTime" , 500 , 0 , 1000));
    public Setting<Integer> moduleListUpdates = this.register(new Setting <> ("ALUpdates" , 1000 , 0 , 1000));
    public Setting<Float> holeRange = this.register(new Setting <> ("HoleRange" , 6.0f , 1.0f , 256.0f));
    public Setting<Integer> holeUpdates = this.register(new Setting <> ("HoleUpdates" , 100 , 0 , 1000));
    public Setting<Integer> holeSync = this.register(new Setting <> ("HoleSync" , 10000 , 1 , 10000));
    public Setting<Boolean> safety = this.register(new Setting <> ("Speed" , "SafetyPlayer" , 0.0 , 0.0 , false , 0));
    public Setting<Integer> safetyCheck = this.register(new Setting <> ("SafetyCheck" , 50 , 1 , 150));
    public Setting<Integer> safetySync = this.register(new Setting <> ("SafetySync" , 250 , 1 , 10000));
    public Setting<ThreadMode> holeThread = this.register(new Setting <> ("Speed" , "HoleThread" , 0.0 , 0.0 , ThreadMode.WHILE , 0));
    public Setting<Boolean> speed = this.register(new Setting <> ("Speed" , "Speed" , 0.0 , 0.0 , true , 0));
    public Setting<Boolean> oneDot15 = this.register(new Setting <> ("Speed" , "1.15" , 0.0 , 0.0 , false , 0));
    public Setting<Boolean> tRadarInv = this.register(new Setting <> ("Speed" , "TRadarInv" , 0.0 , 0.0 , true , 0));
    public Setting<Boolean> unfocusedCpu = this.register(new Setting <> ("Speed" , "UnfocusedCPU" , 0.0 , 0.0 , false , 0));
    public Setting<Integer> cpuFPS = this.register(new Setting<Object>("UnfocusedFPS", 60 , 1 , 60 , v -> this.unfocusedCpu.getValue()));
    public Setting<Integer> baritoneTimeOut = this.register(new Setting <> ("Baritone" , 5 , 1 , 20));
    public Setting<Boolean> oneChunk = this.register(new Setting <> ("Speed" , "OneChunk" , 0.0 , 0.0 , false , 0));

    public Managers() {
        super("Management", "ClientManagement", Module.Category.CLIENT, false, false, true);
        this.setInstance();
    }

    public static Managers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Managers();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        CreepyWare.commandManager.setClientMessage(this.getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2) {
            if (this.oneChunk.getPlannedValue ()) {
                Managers.mc.gameSettings.renderDistanceChunks = 1;
            }
            if (event.getSetting() != null && this.equals(event.getSetting().getFeature())) {
                if (event.getSetting().equals(this.holeThread)) {
                    CreepyWare.holeManager.settingChanged();
                }
                CreepyWare.commandManager.setClientMessage(this.getCommandMessage());
            }
        }
    }

    public String getCommandMessage() {
        if (this.rainbowPrefix.getPlannedValue ()) {
            StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
            stringBuilder.insert(0, "\u00a7+");
            stringBuilder.append("\u00a7r");
            return stringBuilder.toString();
        }
        return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue()) + TextUtil.coloredString(this.command.getPlannedValue(), this.commandColor.getPlannedValue()) + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue());
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, "\u00a7+");
        stringBuilder.append("\u00a7r");
        return stringBuilder.toString();
    }

    public String getRawCommandMessage() {
        return this.commandBracket.getValue() + this.command.getValue() + this.commandBracket2.getValue();
    }

    public enum ThreadMode {
        POOL,
        WHILE,
        NONE

    }
}

