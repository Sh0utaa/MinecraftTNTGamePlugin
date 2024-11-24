package me.shotatevdorashvili.minecraftTntGamePlugin;

import me.shotatevdorashvili.minecraftTntGamePlugin.CommandExecutors.TntArenaCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftTntGamePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("tntarena").setExecutor(new TntArenaCommand());
    }
}
