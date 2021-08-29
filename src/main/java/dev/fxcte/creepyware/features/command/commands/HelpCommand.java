package dev.fxcte.creepyware.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : CreepyWare.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + CreepyWare.commandManager.getPrefix() + command.getName());
        }
    }
}

