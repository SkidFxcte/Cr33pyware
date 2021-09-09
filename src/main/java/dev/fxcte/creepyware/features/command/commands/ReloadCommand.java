package dev.fxcte.creepyware.features.command.commands;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.features.command.Command;

public
class ReloadCommand
        extends Command {
    public
    ReloadCommand() {
        super("reload" , new String[0]);
    }

    @Override
    public
    void execute(String[] commands) {
        CreepyWare.reload();
    }
}

