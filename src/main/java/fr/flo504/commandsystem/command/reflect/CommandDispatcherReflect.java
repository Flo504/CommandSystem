package fr.flo504.commandsystem.command.reflect;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class CommandDispatcherReflect {

    private CommandDispatcherReflect() throws IllegalAccessException{
        throw new IllegalAccessException("Static utility class");
    }

    private final static Method getHandleMethod;
    private final static Class<?> NMSCommandDispatcherClass;
    private final static Method actualizeMethod;
    private final static Field childrenMapField;
    private final static Field literalsMapField;
    private final static Field argumentsMapField;
    private final static Field commandDispatcherField;
    private final static Constructor<?> bukkitCommandWrapperConstructor;
    private final static Method registerMethod;
    private final static CmdDispatcherGetter cmdDispatcherGetter;

    static{
        Method getHandle = null;
        try{
            final Class<?> craftPlayerClass = Class.forName(VersionReflect.CRAFTBUKKIT+"entity.CraftPlayer");
            getHandle = craftPlayerClass.getDeclaredMethod("getHandle");
        }catch (ReflectiveOperationException ignored){}

        assert getHandle != null;
        getHandleMethod = getHandle;
        getHandleMethod.setAccessible(true);

        Class<?> NMSCommandDispatcher = null;
        try{
            NMSCommandDispatcher = Class.forName(VersionReflect.MINECRAFT+ "CommandDispatcher");
        }catch (ReflectiveOperationException ignored){}

        assert NMSCommandDispatcher != null;
        NMSCommandDispatcherClass = NMSCommandDispatcher;

        Method actualize = null;
        try{
            final Class<?> nmsPlayer = Class.forName(VersionReflect.MINECRAFT+"EntityPlayer");
            actualize = NMSCommandDispatcherClass.getDeclaredMethod("a", nmsPlayer);
        }catch (ReflectiveOperationException ignored){}

        assert actualize != null;
        actualizeMethod = actualize;
        actualizeMethod.setAccessible(true);

        Field childrenMap = null;
        Field literalsMap = null;
        Field argumentsMap = null;
        try {
            childrenMap = CommandNode.class.getDeclaredField("children");
            literalsMap = CommandNode.class.getDeclaredField("literals");
            argumentsMap = CommandNode.class.getDeclaredField("arguments");
        }catch (ReflectiveOperationException ignored){}

        assert childrenMap != null;
        assert literalsMap != null;
        assert argumentsMap != null;

        childrenMapField = childrenMap;
        literalsMapField = literalsMap;
        argumentsMapField = argumentsMap;

        childrenMapField.setAccessible(true);
        literalsMapField.setAccessible(true);
        argumentsMapField.setAccessible(true);

        Field commandDispatcher = null;
        try{
            commandDispatcher = NMSCommandDispatcherClass.getDeclaredField("b");
        }catch (ReflectiveOperationException ignored){}

        assert commandDispatcher != null;
        commandDispatcherField = commandDispatcher;
        commandDispatcherField.setAccessible(true);

        Constructor<?> bukkitCommandWrapper = null;
        Method register = null;
        try{
            final Class<?> bukkitCommandWrapperClass = Class.forName(VersionReflect.CRAFTBUKKIT+"command.BukkitCommandWrapper");
            final Class<?> craftServerClass = Class.forName(VersionReflect.CRAFTBUKKIT+"CraftServer");
            bukkitCommandWrapper = bukkitCommandWrapperClass.getDeclaredConstructor(craftServerClass, Command.class);
            register = bukkitCommandWrapperClass.getDeclaredMethod("register", CommandDispatcher.class, String.class);
        }catch (ReflectiveOperationException ignored){}

        bukkitCommandWrapperConstructor = bukkitCommandWrapper;
        registerMethod = register;

        assert bukkitCommandWrapperConstructor != null;
        assert registerMethod != null;

        bukkitCommandWrapperConstructor.setAccessible(true);
        registerMethod.setAccessible(true);

        CmdDispatcherGetter cmdDispatcherGetterTemp = null;
        try {
            cmdDispatcherGetterTemp = VersionReflect.NEW_DISPATCHER ? new DatapackResourcesLocated() : new MinecraftServerLocated();
        }catch (ReflectiveOperationException ignored){}

        cmdDispatcherGetter = cmdDispatcherGetterTemp;
    }

    public static Object getNMSPlayer(Player player){
        try{
            return getHandleMethod.invoke(player);
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    public static void actualize(Object commandDispatcher, Object nmsPlayer){
        try{
            actualizeMethod.invoke(commandDispatcher, nmsPlayer);
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "unsafe"})
    public static void removeCommandFromRoot(RootCommandNode<?> rootCommandNode, Set<String> commands){
        try {
            final Map<String, CommandNode<?>> children = (Map<String, CommandNode<?>>) childrenMapField.get(rootCommandNode);
            final Map<String, LiteralCommandNode<?>> literals = (Map<String, LiteralCommandNode<?>>) literalsMapField.get(rootCommandNode);
            final Map<String, ArgumentCommandNode<?, ?>> arguments = (Map<String, ArgumentCommandNode<?, ?>>) argumentsMapField.get(rootCommandNode);

            for (String command : commands) {
                children.remove(command);
                literals.remove(command);
                arguments.remove(command);
            }
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    public static CommandDispatcher<?> getCommandDispatcher(Object nmsCommandDispatcher){
        try{
            return (CommandDispatcher<?>) commandDispatcherField.get(nmsCommandDispatcher);
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    public static void createCmdWrapperAndRegister(Server server, Command command, CommandDispatcher<?> commandDispatcher, String label){
        try {
            final Object bukkitCommandWrapper = bukkitCommandWrapperConstructor.newInstance(server, command);
            registerMethod.invoke(bukkitCommandWrapper, commandDispatcher, label);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getNmsCommandDispatcher(Server server){
        try{
            return cmdDispatcherGetter.get(server);
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    private interface CmdDispatcherGetter {

        Object get(Server server) throws ReflectiveOperationException;

    }

    private static class MinecraftServerLocated implements CmdDispatcherGetter{

        private final Field consoleField;
        private final Field commandDispatcherField;

        public MinecraftServerLocated() throws ReflectiveOperationException{
            final Class<?> craftServerClass = Class.forName(VersionReflect.CRAFTBUKKIT+"CraftServer");
            final Class<?> minecraftServerClass = Class.forName(VersionReflect.MINECRAFT+"MinecraftServer");
            consoleField = craftServerClass.getDeclaredField("console");
            commandDispatcherField = minecraftServerClass.getDeclaredField("commandDispatcher");
            consoleField.setAccessible(true);
            commandDispatcherField.setAccessible(true);
        }

        @Override
        public Object get(Server server) throws ReflectiveOperationException{
            return commandDispatcherField.get(consoleField.get(server));
        }
    }

    private static class DatapackResourcesLocated implements CmdDispatcherGetter{

        private final Field playerListField;
        private final Field serverField;
        private final Field dataPackResourcesField;
        private final Field commandDispatcherField;

        public DatapackResourcesLocated() throws ReflectiveOperationException{
            final Class<?> craftServerClass = Class.forName(VersionReflect.CRAFTBUKKIT+"CraftServer");
            final Class<?> playerListClass = Class.forName(VersionReflect.MINECRAFT+"PlayerList");
            final Class<?> minecraftServerClass = Class.forName(VersionReflect.MINECRAFT+"MinecraftServer");
            final Class<?> datapackResourcesClass = Class.forName(VersionReflect.MINECRAFT+"DataPackResources");

            playerListField = craftServerClass.getDeclaredField("playerList");
            serverField = playerListClass.getDeclaredField("server");
            dataPackResourcesField = minecraftServerClass.getDeclaredField("dataPackResources");
            commandDispatcherField = datapackResourcesClass.getDeclaredField("commandDispatcher");

            playerListField.setAccessible(true);
            serverField.setAccessible(true);
            dataPackResourcesField.setAccessible(true);
            commandDispatcherField.setAccessible(true);
        }

        @Override
        public Object get(Server server) throws ReflectiveOperationException{
            return commandDispatcherField.get(
                    dataPackResourcesField.get(
                            serverField.get(
                                    playerListField.get(
                                            server
                                    )
                            )
                    )
            );
        }
    }


}
