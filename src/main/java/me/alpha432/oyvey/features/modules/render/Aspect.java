package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.PerspectiveEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public
class Aspect
        extends Module {
    public Setting< Float > aspect = this.register ( new Setting <> ( "Alpha" , 1.0f , 0.1f , 5.0f ) );

    public
    Aspect ( ) {
        super ( "Aspect" , "Cool." , Module.Category.RENDER , true , false , false );
    }

    @SubscribeEvent
    public
    void onPerspectiveEvent ( PerspectiveEvent perspectiveEvent ) {
        perspectiveEvent.setAspect ( this.aspect.getValue ( ) );
    }
}
