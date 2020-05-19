package fr.flo504.commandsystem.permission;

import org.bukkit.permissions.Permission;

import java.util.Objects;

public class PermissionChildren {

    private final String permission;
    private final boolean child;

    public PermissionChildren(String permission, boolean child) {
        Objects.requireNonNull(permission);
        this.permission = permission;
        this.child = child;
    }

    public PermissionChildren(Permission permission, boolean child){
        this(permission.getName(), child);
    }

    public String getPermission() {
        return permission;
    }

    public boolean isChild() {
        return child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionChildren that = (PermissionChildren) o;
        return child == that.child &&
                permission.equals(that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, child);
    }

    @Override
    public String toString() {
        return "PermissionChildren{" +
                "permission='" + permission + '\'' +
                ", child=" + child +
                '}';
    }
}
