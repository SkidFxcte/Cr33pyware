package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;

public class EntityControl
        extends Module {
    public static EntityControl INSTANCE;

    public EntityControl() {
        super("EntityControl", "Control entities with the force or some shit", Module.Category.MOVEMENT, false, false, false);
        INSTANCE = this;
    }
}

