package fr.flo504.commandsystem.command;

import fr.flo504.commandsystem.command.reflect.CommandMapReflect;
import fr.flo504.commandsystem.command.reflect.VersionReflect;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class CustomCommand {

    private String name;
    private List<String> aliases;
    private String description;
    private String usageMessage;
    private String permission;
    private String permissionMessage;

    private final List<CustomCommand> parent;
    private final List<CustomCommand> subCommands;
    private final Map<String, Object> tags;
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
        return Collections.unmodifiableSet(register ? getKeyOfValue(CommandMapReflect.getKnowsCommands(), plCommand) : Collections.emptySet());
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

        final PluginCommand cmd = CommandMapReflect.createPluginCommand(name, plugin);

        assert cmd != null;
        CommandMapReflect.setCommandName(cmd, name);
        cmd.setLabel(name);
        cmd.setDescription(description);
        cmd.setAliases(aliases);
        cmd.setUsage(usageMessage);
        cmd.setPermission(permission);
        cmd.setPermissionMessage(permissionMessage);

        cmd.setExecutor(executor);
        cmd.setTabCompleter(completer);
        plCommand = cmd;
        CommandMapReflect.getCommandMap().register(name, plugin.getDescription().getName(), plCommand);

        if(VersionReflect.NEW_TAB){
            final Set<String> registerNames = getKeyOfValue(CommandMapReflect.getKnowsCommands(), plCommand);
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

        final Set<String> registeredCommandNames = getKeyOfValue(CommandMapReflect.getKnowsCommands(), plCommand);
        registeredCommandNames.forEach(CommandMapReflect.getKnowsCommands()::remove);

        if(VersionReflect.NEW_TAB){
            NewTabCompleter.removeCommands(registeredCommandNames);
            NewTabCompleter.synchronize();
        }

        plCommand.unregister(CommandMapReflect.getCommandMap());
        plCommand = null;

        register = false;
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
