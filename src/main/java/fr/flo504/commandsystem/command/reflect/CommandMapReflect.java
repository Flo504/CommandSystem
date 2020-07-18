package fr.flo504.commandsystem.command.reflect;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SuppressWarnings({"unsafe", "unchecked"})
public class CommandMapReflect {

    private CommandMapReflect() throws IllegalAccessException {
        throw new IllegalAccessException("Static utility class");
    }

    private final static CommandMap commandMap;
    private final static Constructor<PluginCommand> plCmdConstructor;
    private final static Map<String, Command> knowsCommands;
    private final static Field commandNameField;

    static{
        CommandMap cmdMap = null;
        try {
            final Field cmdMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            cmdMapField.setAccessible(true);
            cmdMap = (CommandMap) cmdMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        commandMap = cmdMap;

        Constructor<PluginCommand> constructor = null;
        try {
            constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        } catch (NoSuchMethodException ignored) {}

        plCmdConstructor = constructor;
        assert plCmdConstructor != null;
        plCmdConstructor.setAccessible(true);

        Map<String, Command> commands = null;

        try{
            final Field knowsCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knowsCommandsField.setAccessible(true);
            commands = (Map<String, Command>) knowsCommandsField.get(commandMap);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {}

        knowsCommands = commands;

        Field commandName = null;
        try{
            commandName = Command.class.getDeclaredField("name");
        } catch (NoSuchFieldException ignored) {}

        commandNameField = commandName;
        assert commandNameField != null;
        commandNameField.setAccessible(true);
    }

    public static CommandMap getCommandMap() {
        return commandMap;
    }

    public static Constructor<PluginCommand> getPlCmdConstructor() {
        return plCmdConstructor;
    }

    public static Map<String, Command> getKnowsCommands() {
        return knowsCommands;
    }

    public static Field getCommandNameField() {
        return commandNameField;
    }

    public static void setCommandName(PluginCommand command, String name){
        try{
            commandNameField.set(command, name);
        }catch (IllegalAccessException ignored) {}
    }

    public static PluginCommand createPluginCommand(String name, Plugin plugin){
        try{
            return plCmdConstructor.newInstance(name, plugin);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ignored) {}
        return null;
    }

}
