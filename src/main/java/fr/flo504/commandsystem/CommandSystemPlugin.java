package fr.flo504.commandsystem;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class CommandSystemPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "CommandSystem API is correctly loading");
    }

}
