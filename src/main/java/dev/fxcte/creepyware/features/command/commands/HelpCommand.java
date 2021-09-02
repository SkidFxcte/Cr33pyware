package dev.fxcte.creepyware.features.command.commands;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("commands");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("You can use following commands: ");
        for (Command command : CreepyWare.commandManager.getCommands()) {
            HelpCommand.sendMessage(CreepyWare.commandManager.getPrefix() + command.getName());
        }
    }
}

