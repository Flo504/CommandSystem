package fr.flo504.commandsystem.command.reflect;

import org.bukkit.Bukkit;

public class VersionReflect {

    private static final String versionName = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    private static final int version = Integer.parseInt(versionName.substring(1).split("_")[1]);
    private static final String npack = "net.minecraft.server." + versionName + ".";
    private static final String cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
    public static final String VERSION_NAME = versionName;
    public static final int VERSION = version;
    public static final String MINECRAFT = npack;
    public static final String CRAFTBUKKIT = cpack;

    public static final boolean NEW_TAB = version >= 13;
    public static final boolean NEW_DISPATCHER = version >= 16;

}
