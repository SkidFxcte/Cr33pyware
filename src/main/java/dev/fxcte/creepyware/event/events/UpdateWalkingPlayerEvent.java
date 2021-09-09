package dev.fxcte.creepyware.event.events;

import dev.fxcte.creepyware.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public
class UpdateWalkingPlayerEvent
        extends EventStage {
    public
    UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
}

