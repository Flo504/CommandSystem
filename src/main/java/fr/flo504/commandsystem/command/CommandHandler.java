package fr.flo504.commandsystem.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandHandler {

    boolean handleCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber);

    List<String> handleTab(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber);

}
