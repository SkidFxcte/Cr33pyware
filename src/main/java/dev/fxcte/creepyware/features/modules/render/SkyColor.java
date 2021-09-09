package dev.fxcte.creepyware.features.modules.render;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public
class SkyColor
        extends Module {
    private final Setting <Integer> red = this.register(new Setting <>("Red", 255, 0, 255));
    private final Setting <Integer> green = this.register(new Setting <>("Green", 255, 0, 255));
    private final Setting <Integer> blue = this.register(new Setting <>("Blue", 255, 0, 255));

    public
    SkyColor() {
        super("SkyColor", "Change the sky color.", Category.RENDER, false, false, false);
    }

    @SubscribeEvent
    public
    void onUpdate(EntityViewRenderEvent.FogColors event) {
        event.setRed((float) this.red.getValue() / 255.0F);
        event.setGreen((float) this.green.getValue() / 255.0F);
        event.setBlue((float) this.blue.getValue() / 255.0F);
    }

    public
    void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public
    void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

}