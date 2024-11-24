package me.shotatevdorashvili.minecraftTntGamePlugin;

import me.shotatevdorashvili.minecraftTntGamePlugin.CommandExecutors.TntArenaCommand;
import me.shotatevdorashvili.minecraftTntGamePlugin.Listeners.ArenaProtectionListener;
import me.shotatevdorashvili.minecraftTntGamePlugin.Listeners.MadeByShotaTevdorashvili;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftTntGamePlugin extends JavaPlugin {
    private final ArenaProtectionListener protectionListener = new ArenaProtectionListener();

    @Override
    public void onEnable() {
        // Register ProtectionListener to the Main Class
        getServer().getPluginManager().registerEvents(protectionListener, this);

        // Register MadeByShotaTevdorashvili
        getServer().getPluginManager().registerEvents(new MadeByShotaTevdorashvili(), this);

        // Register TntArena with the protectionListener to its arguments
        this.getCommand("tntarena").setExecutor(new TntArenaCommand(protectionListener, this, this));
    }
}
