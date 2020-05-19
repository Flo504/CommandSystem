package fr.flo504.commandsystem.permission;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermissionTree {

    private final List<PermissionTree> branches;
    private final PermissionBuilder builder;

    public PermissionTree(PermissionBuilder builder){
        this.branches = new ArrayList<>();
        this.builder = builder;
    }

    public PermissionBuilder getBuilder() {
        return builder;
    }

    public void addBranch(PermissionTree branch){
        if(!branches.contains(branch))
            branches.add(branch);
    }

    public void addBranch(PermissionTree... branches){
        for(PermissionTree branch : branches)
            addBranch(branch);
    }

    public void removeBranch(PermissionTree branch){
        branches.remove(branch);
    }

    public void build(){
        for(PermissionTree branch : branches){
            branch.build();
            builder.addChildren(branch.getBuilder().getName());
        }

        if(!builder.isInitialized())
            builder.create(false);
    }

    public String buildTree(){
        return buildTree("");
    }

    private String buildTree(String indent){
        final StringBuilder builder = new StringBuilder();

        builder.append(this.builder.getName());

        final int length = indent.length() + builder.length() + 1;
        final StringBuilder newIndent = new StringBuilder();
        for(int index = 0; index < length; index ++)
            newIndent.append(" ");

        boolean first = true;
        for(PermissionTree branch : branches){
            if(first)
                first = false;
            else
                builder.append("\n").append(newIndent);
            builder.append(" -").append(branch.buildTree(newIndent.toString()));
        }

        return builder.toString();
    }

}
