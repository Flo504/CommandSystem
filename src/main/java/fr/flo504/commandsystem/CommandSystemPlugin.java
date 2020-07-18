package fr.flo504.commandsystem;

import fr.flo504.commandsystem.command.CommandBuilder;
import fr.flo504.commandsystem.command.CommandHandler;
import fr.flo504.commandsystem.command.CustomCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class CommandSystemPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "CommandSystem API is correctly loaded");

        test();
    }

    private void test() {
        final AtomicReference<CustomCommand> command = new AtomicReference<>();

        final CustomCommand desactivateCommand = new CommandBuilder()
                .setName("activate")
                .addAlias("act")
                .setCommandHandler(new CommandHandler() {
                    @Override
                    public boolean handleCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber) {
                        sender.sendMessage("desactivé");
                        System.out.println(currentCommand.getRegisterNames());
                        currentCommand.unregister();
                        command.get().register(CommandSystemPlugin.this);
                        return true;
                    }

                    @Override
                    public List<String> handleTab(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber) {
                        return Collections.emptyList();
                    }
                })
                .create();

        final CustomCommand activateCommand = new CommandBuilder()
                .setName("activate")
                .addAlias("act")
                .setRegister(true, this)
                .setCommandHandler(new CommandHandler() {
                    @Override
                    public boolean handleCommand(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber) {
                        sender.sendMessage("activé");
                        currentCommand.unregister();
                        desactivateCommand.register(CommandSystemPlugin.this);
                        return true;
                    }

                    @Override
                    public List<String> handleTab(CommandSender sender, CustomCommand rootCommand, CustomCommand lastCommand, CustomCommand currentCommand, List<String> args, List<String> specificArgs, int argNumber) {
                        return Collections.emptyList();
                    }
                })
                .create();

        command.set(activateCommand);

    }

}
