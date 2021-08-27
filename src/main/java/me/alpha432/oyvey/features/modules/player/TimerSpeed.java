//
// Decompiled by Procyon v0.5.36
//

package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.util.Timer;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.Module;

public class TimerSpeed extends Module
{
    public Setting<Boolean> autoOff;
    public Setting<Integer> timeLimit;
    public Setting<TimerMode> mode;
    public Setting<Float> timerSpeed;
    public Setting<Float> fastSpeed;
    public Setting<Integer> fastTime;
    public Setting<Integer> slowTime;
    public Setting<Boolean> startFast;
    public float speed;
    private Timer timer;
    private Timer turnOffTimer;
    private boolean fast;

    public TimerSpeed() {
        super("Timer", "Will speed up the game.", Category.PLAYER, false, false, false);
        this.autoOff = (Setting<Boolean>)this.register(new Setting("AutoOff", false));
        this.timeLimit = (Setting<Integer>)this.register(new Setting("Limit", 250, 1, 2500, v -> this.autoOff.getValue()));
        this.mode = (Setting<TimerMode>)this.register(new Setting("Mode", TimerMode.NORMAL));
        this.timerSpeed = (Setting<Float>)this.register(new Setting("Speed", 4.0f, 0.1f, 20.0f));
        this.fastSpeed = (Setting<Float>)this.register(new Setting("Fast", 10.0f, 0.1f, 100.0f, v -> this.mode.getValue() == TimerMode.SWITCH, "Fast Speed for switch."));
        this.fastTime = (Setting<Integer>)this.register(new Setting("FastTime", 20, 1, 500, v -> this.mode.getValue() == TimerMode.SWITCH, "How long you want to go fast.(ms * 10)"));
        this.slowTime = (Setting<Integer>)this.register(new Setting("SlowTime", 20, 1, 500, v -> this.mode.getValue() == TimerMode.SWITCH, "Recover from too fast.(ms * 10)"));
        this.startFast = (Setting<Boolean>)this.register(new Setting("StartFast", false, v -> this.mode.getValue() == TimerMode.SWITCH));
        this.speed = 1.0f;
        this.timer = new Timer();
        this.turnOffTimer = new Timer();
        this.fast = false;
    }

    @Override
    public void onEnable() {
        this.turnOffTimer.reset();
        this.speed = this.timerSpeed.getValue();
        if (!this.startFast.getValue()) {
            this.timer.reset();
        }
    }

    @Override
    public void onUpdate() {
        if (this.autoOff.getValue() && this.turnOffTimer.passedMs(this.timeLimit.getValue())) {
            this.disable();
            return;
        }
        if (this.mode.getValue() == TimerMode.NORMAL) {
            this.speed = this.timerSpeed.getValue();
            return;
        }
        if (!this.fast && this.timer.passedDms(this.slowTime.getValue())) {
            this.fast = true;
            this.speed = this.fastSpeed.getValue();
            this.timer.reset();
        }
        if (this.fast && this.timer.passedDms(this.fastTime.getValue())) {
            this.fast = false;
            this.speed = this.timerSpeed.getValue();
            this.timer.reset();
        }
    }

    @Override
    public void onDisable() {
        this.speed = 1.0f;
        OyVey.timerManager.reset();
        this.fast = false;
    }

    @Override
    public String getDisplayInfo() {
        return this.timerSpeed.getValueAsString();
    }

    public enum TimerMode
    {
        NORMAL,
        SWITCH;
    }
}
