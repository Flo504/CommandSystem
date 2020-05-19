package fr.flo504.commandsystem.command;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class AbstractCommandHandler implements CommandHandler{

    private boolean usePermissionCommand;
    private boolean usePermissionTab;

    public AbstractCommandHandler(boolean usePermissionCommand, boolean usePermissionTab) {
        this.usePermissionCommand = usePermissionCommand;
        this.usePermissionTab = usePermissionTab;
    }

    public AbstractCommandHandler(){
        this(true, true);
    }

    public boolean isUsePermissionCommand() {
        return usePermissionCommand;
    }

    public void setUsePermissionCommand(boolean usePermissionCommand) {
        this.usePermissionCommand = usePermissionCommand;
    }

    public boolean isUsePermissionTab() {
        return usePermissionTab;
    }

    public void setUsePermissionTab(boolean usePermissionTab) {
        this.usePermissionTab = usePermissionTab;
    }

    public static List<CustomCommand> findSubCommands(CommandSender sender, CustomCommand rootCommand, String arg, boolean useAlias, boolean ignoreCase, boolean usePermission){
        return rootCommand.getSubCommands().stream()
                .filter(
                        (subCommand)->
                                (ignoreCase ? subCommand.getName().equalsIgnoreCase(arg) : subCommand.getName().equals(arg))
                                || (useAlias &&
                                        (ignoreCase ?
                                                subCommand.getAliases().stream().anyMatch((alias) -> alias.equalsIgnoreCase(arg)) :
                                                subCommand.getAliases().contains(arg)
                                        )
                                )
                )
                .filter((subCommand)-> !usePermission || subCommand.getPermission().equals("") || sender.hasPermission(subCommand.getPermission()))
                .collect(Collectors.toList());
    }

    public static List<CustomCommand> findSubCommandsStartWith(CommandSender sender, CustomCommand rootCommand, String arg, boolean useAlias, boolean ignoreCase, boolean usePermission){
        return rootCommand.getSubCommands().stream()
                .filter((subCommand)->
                        (ignoreCase ?
                                subCommand.getName().toLowerCase(Locale.ROOT).startsWith(arg.toLowerCase(Locale.ROOT)) :
                                subCommand.getName().startsWith(arg)
                        )
                        || (useAlias &&
                            (ignoreCase ?
                                    subCommand.getAliases().stream().anyMatch((alias) -> alias.toLowerCase(Locale.ROOT).startsWith(arg.toLowerCase(Locale.ROOT))) :
                                    subCommand.getAliases().stream().anyMatch((alias) -> alias.startsWith(arg))
                            )
                        )
                )
                .filter((subCommand)-> !usePermission || subCommand.getPermission().equals("") || sender.hasPermission(subCommand.getPermission()))
                .collect(Collectors.toList());
    }

    public static List<String> getSuggestion(CommandSender sender, CustomCommand rootCommand, String arg, boolean useAlias, boolean ignoreCase, boolean usePermission){
        return findSubCommandsStartWith(sender, rootCommand, arg, useAlias, ignoreCase, usePermission).stream().map(CustomCommand::getName).collect(Collectors.toList());
    }

    public static CustomCommand getSubCommands(CommandSender sender, CustomCommand rootCommand, String arg, boolean useAlias, boolean ignoreCase, int index, boolean usePermission){
        final List<CustomCommand> found = findSubCommands(sender, rootCommand, arg, useAlias, ignoreCase, usePermission);
        if(found.size() > index){
            return found.get(index);
        }
        return null;
    }

    public static boolean hasSubCommand(CommandSender sender, CustomCommand rootCommand, String arg, boolean useAlias, boolean ignoreCase, int index, boolean usePermission){
        return getSubCommands(sender, rootCommand, arg, useAlias, ignoreCase, index, usePermission) != null;
    }

    public static boolean runCommandSubCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand currentCommand, CustomCommand nextCommand, List<String> args, List<String> specificArgs, int argNumber){
        if(!specificArgs.isEmpty())
            specificArgs.remove(0);
        return nextCommand.getCommandHandler().handleCommand(sender, rootCommand, currentCommand, nextCommand, args, specificArgs, argNumber+1);
    }

    public static List<String> runTabSubCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand currentCommand, CustomCommand nextCommand, List<String> args, List<String> specificArgs, int argNumber){
        if(!specificArgs.isEmpty())
            specificArgs.remove(0);
        return nextCommand.getCommandHandler().handleTab(sender, rootCommand, currentCommand, nextCommand, args, specificArgs, argNumber+1);
    }

    public abstract boolean executeCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber, boolean hasPermission);

    @Override
    public boolean handleCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber) {
        final CustomCommand command = AbstractCommandHandler.getSubCommands(sender, currentCommand, specificArgs.isEmpty() ? "" : specificArgs.get(0), true, true,0, false);
        if(command != null){
            return AbstractCommandHandler.runCommandSubCommand(sender, rootCommand, currentCommand, command, args, specificArgs, argNumber);
        }
        return executeCommand(sender, rootCommand, lastCommand, currentCommand, args, specificArgs, argNumber, !usePermissionCommand || currentCommand.getPermission().equals("") || sender.hasPermission(currentCommand.getPermission()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber) {
        final CustomCommand command = AbstractCommandHandler.getSubCommands(sender, currentCommand, specificArgs.isEmpty() ? "" : specificArgs.get(0), true, true,0, usePermissionTab);
        if(command != null){
            return AbstractCommandHandler.runTabSubCommand(sender, rootCommand, currentCommand, command, args, specificArgs, argNumber);
        }
        return AbstractCommandHandler.getSuggestion(sender, currentCommand, specificArgs.isEmpty() ? "" : specificArgs.get(0), false, true, usePermissionTab);
    }

}
