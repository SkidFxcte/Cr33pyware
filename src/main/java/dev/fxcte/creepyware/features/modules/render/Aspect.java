package dev.fxcte.creepyware.features.modules.render;

import dev.fxcte.creepyware.event.events.PerspectiveEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect
        extends Module {
    private Setting<Double> aspect;

    public Aspect() {
        super("Aspect", "eeeeee", Module.Category.RENDER, true, false, false);
        this.aspect = this.register(new Setting<Double>("Aspect", (double)Aspect.mc.displayWidth / (double)Aspect.mc.displayHeight, 0.0, 3.0));
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent event) {
        event.setAspect(this.aspect.getValue().floatValue());
    }
}
