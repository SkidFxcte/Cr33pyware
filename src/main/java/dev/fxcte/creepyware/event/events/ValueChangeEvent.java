package dev.fxcte.creepyware.event.events;

import dev.fxcte.creepyware.event.EventStage;
import dev.fxcte.creepyware.features.setting.Setting;

public
class ValueChangeEvent
        extends EventStage {
    public Setting setting;
    public Object value;

    public
    ValueChangeEvent(Setting setting, Object value) {
        this.setting = setting;
        this.value = value;
    }
}

