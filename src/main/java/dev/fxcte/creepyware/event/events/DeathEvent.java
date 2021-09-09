package dev.fxcte.creepyware.event.events;

import dev.fxcte.creepyware.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public
class DeathEvent
        extends EventStage {
    public EntityPlayer player;

    public
    DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}

