package fr.flo504.commandsystem.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.RootCommandNode;
import fr.flo504.commandsystem.command.reflect.CommandDispatcherReflect;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.Set;

public class NewTabCompleter {

    public static void addCommand(Set<String> labels, PluginCommand command){
        final Server server = Bukkit.getServer();
        final CommandDispatcher<?> commandDispatcher = CommandDispatcherReflect.getCommandDispatcher(CommandDispatcherReflect.getNmsCommandDispatcher(server));

        for(String label : labels){
            CommandDispatcherReflect.createCmdWrapperAndRegister(server, command, commandDispatcher, label);
        }
    }

    public static void removeCommands(Set<String> commands){
        final Object NMSCommandDispatcher = CommandDispatcherReflect.getNmsCommandDispatcher(Bukkit.getServer());
        final CommandDispatcher<?> commandDispatcher = CommandDispatcherReflect.getCommandDispatcher(NMSCommandDispatcher);
        final RootCommandNode<?> rootCommandNode = commandDispatcher.getRoot();

        CommandDispatcherReflect.removeCommandFromRoot(rootCommandNode, commands);
    }

    public static void synchronize(){
        final Object commandDispatcher = CommandDispatcherReflect.getNmsCommandDispatcher(Bukkit.getServer());

        for(final Player player : Bukkit.getOnlinePlayers()){
            CommandDispatcherReflect.actualize(commandDispatcher, CommandDispatcherReflect.getNMSPlayer(player));
        }
    }
}
