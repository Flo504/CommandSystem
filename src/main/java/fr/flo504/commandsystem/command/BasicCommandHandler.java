package fr.flo504.commandsystem.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public class BasicCommandHandler extends AbstractCommandHandler {

    @Override
    public boolean executeCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber, boolean hasPermission) {
        if(!hasPermission && !currentCommand.getPermissionMessage().equals(""))
            sender.sendMessage(currentCommand.getPermissionMessage());
        return true;
    }
}
