package fr.flo504.commandsystem.permission;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PermissionBuilder {

    private final PluginManager manager;

    private boolean initialized;

    private String name;
    private PermissionDefault permissionDefault;
    private Map<String, Boolean> children;
    private String description;

    public PermissionBuilder() {
        manager = Bukkit.getPluginManager();
        children = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public PermissionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PermissionDefault getDefault() {
        return permissionDefault;
    }

    public PermissionBuilder setDefault(PermissionDefault permissionDefault) {
        this.permissionDefault = permissionDefault;
        return this;
    }

    public Map<String, Boolean> getChildren(){
        return Collections.unmodifiableMap(children);
    }

    public PermissionBuilder addChildren(PermissionChildren... children){
        for(final PermissionChildren child : children) {
            if(child == null)
                continue;
            this.children.put(child.getPermission(), child.isChild());
        }
        return this;
    }

    public PermissionBuilder addChildren(String... children){
        for(final String child : children){
            if(child == null)
                continue;
            this.children.put(child, true);
        }
        return this;
    }

    public PermissionBuilder addChildren(Permission... children){
        for(final Permission child : children){
            if(child == null)
                continue;
            this.children.put(child.getName(), true);
        }
        return this;
    }

    public PermissionBuilder addChildrenWithPrefix(String prefix, PermissionChildren... children){
        for(final PermissionChildren child : children) {
            if(child == null)
                continue;
            this.children.put(prefix+child.getPermission(), child.isChild());
        }
        return this;
    }

    public PermissionBuilder addChildrenWithPrefix(String prefix, String... children){
        for(final String child : children){
            if(child == null)
                continue;
            this.children.put(prefix+child, true);
        }
        return this;
    }

    public PermissionBuilder addChildrenWithPrefix(String prefix, Permission... children){
        for(final Permission child : children){
            if(child == null)
                continue;
            this.children.put(prefix+child.getName(), true);
        }
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PermissionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public void clear(){
        name = null;
        permissionDefault = null;
        children = new HashMap<>();
        description = null;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Permission create(){
        return create(true);
    }

    public Permission create(boolean clear){
        final Permission permission = new Permission(name, description, permissionDefault, children);
        manager.addPermission(permission);
        initialized = true;
        if(clear)
            clear();
        return permission;
    }
}
