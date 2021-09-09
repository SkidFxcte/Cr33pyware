package dev.fxcte.creepyware.features.command.commands;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.features.command.Command;

public
class UnloadCommand
        extends Command {
    public
    UnloadCommand() {
        super("unload" , new String[0]);
    }

    @Override
    public
    void execute(String[] commands) {
        CreepyWare.unload(true);
    }
}

