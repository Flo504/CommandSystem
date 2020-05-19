package fr.flo504.commandsystem.command;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CommandBuilder {

    private String name;
    private List<String> aliases;
    private String description;
    private String usageMessage;
    private String permission;
    private String permissionMessage;

    private List<CustomCommand> subCommands;
    private Map<String, Object> tags;
    private CommandHandler commandHandler;

    private boolean register;
    private Plugin plugin;

    public CommandBuilder() {
        this.name = "";
        this.aliases = new ArrayList<>();
        this.description = "";
        this.usageMessage = "";
        this.permission = "";
        this.permissionMessage = "";
        this.subCommands = new ArrayList<>();
        this.tags = new HashMap<>();
        this.commandHandler = new BasicCommandHandler();
        this.register = false;
        this.plugin = null;
    }

    public CommandBuilder setName(String name){
        Objects.requireNonNull(name, "The name can not be null");
        this.name = name;
        return this;
    }

    public CommandBuilder setDescription(String description) {
        if(description == null){
            description = "";
        }
        this.description = description;
        return this;
    }

    public CommandBuilder setUsageMessage(String usageMessage) {
        if(usageMessage == null){
            usageMessage = "";
        }
        this.usageMessage = usageMessage;
        return this;
    }

    public CommandBuilder setPermission(String permission) {
        if(permission == null){
            permission = "";
        }
        this.permission = permission;
        return this;
    }

    public CommandBuilder setPermission(Permission permission){
        return setPermission(permission.getName());
    }

    public CommandBuilder setPermissionMessage(String permissionMessage) {
        if(permissionMessage == null){
            permissionMessage = "";
        }
        this.permissionMessage = permissionMessage;
        return this;
    }

    public CommandBuilder setRegister(boolean register, Plugin plugin) {
        Objects.requireNonNull(plugin, "Plugin which register the command can not be null");
        this.register = register;
        this.plugin = plugin;
        return this;
    }

    public CommandBuilder addAlias(String... aliases){
        if(aliases == null || aliases.length == 0){
            return this;
        }
        for(String alias : aliases){
            if(alias == null || this.aliases.contains(alias) || alias.replace(" ", "").equalsIgnoreCase("")){
                continue;
            }
            this.aliases.add(alias);
        }
        return this;
    }

    public CommandBuilder addSubCommand(CustomCommand... commands){
        if(commands == null || commands.length == 0){
            return this;
        }
        for(CustomCommand command : commands){
            if(command == null || this.subCommands.contains(command)){
                continue;
            }
            this.subCommands.add(command);
        }
        return this;
    }

    public CommandBuilder addTag(String tag, Object value){
        tags.put(tag, value);
        return this;
    }

    public CommandBuilder removeTag(String tag){
        tags.remove(tag);
        return this;
    }

    public CommandBuilder setCommandHandler(CommandHandler commandHandler) {
        Objects.requireNonNull(commandHandler, "The command handler can not be null");
        this.commandHandler = commandHandler;
        return this;
    }

    public CustomCommand create(){
        return create(true);
    }

    public CustomCommand create(boolean clear){
        if(name.replace(" ", "").equalsIgnoreCase("")){
            throw new IllegalArgumentException("The name can not be empty (or spaces)");
        }
        CustomCommand command = new CustomCommand(name, aliases, description, usageMessage, permission, permissionMessage);
        subCommands.forEach(command::addSubCommand);
        tags.forEach(command::addTag);
        command.setCommandHandler(commandHandler);
        if(register){
            command.register(plugin);
        }
        if(clear)
            clear();
        return command;
    }

    public void clear(){
        this.name = "";
        this.aliases = new ArrayList<>();
        this.description = "";
        this.usageMessage = "";
        this.permission = "";
        this.permissionMessage = "";
        this.subCommands = new ArrayList<>();
        this.commandHandler = new BasicCommandHandler();
        this.tags = new HashMap<>();
        this.register = false;
        this.plugin = null;
    }

}
