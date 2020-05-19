package fr.flo504.commandsystem.command;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unsafe", "unchecked"})
public class CustomCommand {

    private final static CommandMap commandMap;
    private final static Constructor<PluginCommand> plCmdConstructor;
    private final static Map<String, Command> knowsCommands;
    private final static Field commandNameField;

    private final static boolean newTabCompleter = false;//NewTabCompleter.getVersion() >= 13;

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

    private String name;
    private List<String> aliases;
    private String description;
    private String usageMessage;
    private String permission;
    private String permissionMessage;

    private List<CustomCommand> parent;
    private List<CustomCommand> subCommands;
    private Map<String, Object> tags;
    private CommandHandler commandHandler;

    private PluginCommand plCommand;
    private boolean register;

    public CustomCommand(String name, List<String> aliases, String description, String usageMessage, String permission, String permissionMessage) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.usageMessage = usageMessage;
        this.permission = permission;
        this.permissionMessage = permissionMessage;

        this.parent = new ArrayList<>();
        this.subCommands = new ArrayList<>();
        this.tags = new HashMap<>();
        this.commandHandler = new BasicCommandHandler();
        this.plCommand = null;
        this.register = false;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getUsageMessage() {
        return usageMessage;
    }

    public String getPermission() {
        return permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public List<CustomCommand> getParent() {
        return Collections.unmodifiableList(parent);
    }

    public List<CustomCommand> getSubCommands() {
        return Collections.unmodifiableList(subCommands);
    }

    public PluginCommand getPlCommand() {
        return plCommand;
    }

    public Set<String> getRegisterNames(){
        return Collections.unmodifiableSet(register ? getKeyOfValue(knowsCommands, plCommand) : Collections.emptySet());
    }

    public boolean isRegister() {
        return register;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsageMessage(String usageMessage) {
        this.usageMessage = usageMessage;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    public boolean addSubCommand(CustomCommand command){
        if(command == this){
            return false;
        }

        if(subCommands.contains(command)){
            return false;
        }
        command.parent.add(this);
        return subCommands.add(command);
    }

    public boolean removeSubCommand(CustomCommand command){
        if(!subCommands.contains(command)){
            return false;
        }
        command.parent.remove(this);
        return subCommands.remove(command);
    }

    public void addTag(String tag, Object value){
        tags.put(tag, value);
    }

    public void removeTag(String tag){
        tags.remove(tag);
    }

    public CommandTag getTag(String tag){
        return new CommandTag(tags.get(tag));
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        Objects.requireNonNull(commandHandler, "CommandHandler can not be null");
        this.commandHandler = commandHandler;
    }

    public synchronized void register(Plugin plugin){
        register(plugin,
                (commandSender, command, label, args)->
                        this.commandHandler.handleCommand(
                                commandSender,
                                this,
                                null,
                                this,
                                new ArrayList<>(Arrays.asList(args)),
                                new ArrayList<>(Arrays.asList(args)),
                                0),
                (commandSender, command, label, args)->
                        this.commandHandler.handleTab(
                            commandSender,
                            this,
                            null,
                            this,
                            new ArrayList<>(Arrays.asList(args)),
                            new ArrayList<>(Arrays.asList(args)),
                            0)
        );
    }

    public synchronized void register(Plugin plugin, CommandExecutor executor, TabCompleter completer){
        if(register){
            return;
        }
        if(executor == null)
            executor = (commandSender, command, label, args)->false;
        if(completer == null)
            completer = (commandSender, command, label, args)->Collections.emptyList();
        PluginCommand cmd = null;
        try{
            cmd = plCmdConstructor.newInstance(this.name, plugin);
        }catch(ReflectiveOperationException ignored){}

        assert cmd != null;
        try{
            commandNameField.set(cmd, name);
        } catch (IllegalAccessException ignored) {}
        cmd.setLabel(name);
        cmd.setDescription(description);
        cmd.setAliases(aliases);
        cmd.setUsage(usageMessage);
        cmd.setPermission(permission);
        cmd.setPermissionMessage(permissionMessage);

        cmd.setExecutor(executor);
        cmd.setTabCompleter(completer);
        plCommand = cmd;
        commandMap.register(name, plugin.getDescription().getName(), plCommand);

        if(newTabCompleter){
            final Set<String> registerNames = getKeyOfValue(knowsCommands, plCommand);
            NewTabCompleter.removeCommands(registerNames);
            NewTabCompleter.addCommand(registerNames, plCommand);
            NewTabCompleter.synchronize();
        }

        register = true;
    }

    public synchronized void unregister(){
        if(!register){
            return;
        }

        final Set<String> registeredCommandNames = getKeyOfValue(knowsCommands, plCommand);
        registeredCommandNames.forEach(knowsCommands::remove);

        if(newTabCompleter){
            NewTabCompleter.removeCommands(registeredCommandNames);
            NewTabCompleter.synchronize();
        }

        plCommand.unregister(commandMap);
        plCommand = null;
    }

    private static <K, V> Set<K> getKeyOfValue(Map<K, V> map, V value){
        return map
                .entrySet()
                .stream()
                .filter((entry)-> entry.getValue().equals(value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
