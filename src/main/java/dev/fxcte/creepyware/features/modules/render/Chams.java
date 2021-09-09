package dev.fxcte.creepyware.features.modules.render;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;

public class Chams
        extends Module {
    private static Chams INSTANCE = new Chams();
    public Setting<Boolean> colorSync = this.register(new Setting <> ("Speed" , "Sync" , 0.0 , 0.0 , false , 0));
    public Setting<Boolean> colored = this.register(new Setting <> ("Speed" , "Colored" , 0.0 , 0.0 , false , 0));
    public Setting<Boolean> textured = this.register(new Setting <> ("Speed" , "Textured" , 0.0 , 0.0 , false , 0));
    public Setting<Boolean> rainbow = this.register(new Setting<Object>("Rainbow", false , v -> this.colored.getValue()));
    public Setting<Integer> saturation = this.register(new Setting<Object>("Saturation", 50 , 0 , 100 , v -> this.colored.getValue() != false && this.rainbow.getValue() != false));
    public Setting<Integer> brightness = this.register(new Setting<Object>("Brightness", 100 , 0 , 100 , v -> this.colored.getValue() != false && this.rainbow.getValue() != false));
    public Setting<Integer> speed = this.register(new Setting<Object>("Speed", 40 , 1 , 100 , v -> this.colored.getValue() != false && this.rainbow.getValue() != false));
    public Setting<Boolean> xqz = this.register(new Setting<Object>("XQZ", false , v -> this.colored.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> red = this.register(new Setting<Object>("Red", 0 , 0 , 255 , v -> this.colored.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> green = this.register(new Setting<Object>("Green", 255 , 0 , 255 , v -> this.colored.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> blue = this.register(new Setting<Object>("Blue", 0 , 0 , 255 , v -> this.colored.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", 255 , 0 , 255 , v -> this.colored.getValue()));
    public Setting<Integer> hiddenRed = this.register(new Setting<Object>("Hidden Red", 255 , 0 , 255 , v -> this.colored.getValue() != false && this.xqz.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> hiddenGreen = this.register(new Setting<Object>("Hidden Green", 0 , 0 , 255 , v -> this.colored.getValue() != false && this.xqz.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> hiddenBlue = this.register(new Setting<Object>("Hidden Blue", 255 , 0 , 255 , v -> this.colored.getValue() != false && this.xqz.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> hiddenAlpha = this.register(new Setting<Object>("Hidden Alpha", 255 , 0 , 255 , v -> this.colored.getValue() != false && this.xqz.getValue() != false && this.rainbow.getValue() == false));

    public Chams() {
        super("Chams", "Renders players through walls.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static Chams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Chams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

