package me.shotatevdorashvili.minecraftTntGamePlugin.CommandExecutors;

import me.shotatevdorashvili.minecraftTntGamePlugin.Listeners.ArenaBlockListener;
import me.shotatevdorashvili.minecraftTntGamePlugin.Listeners.ArenaFillListener;
import me.shotatevdorashvili.minecraftTntGamePlugin.Listeners.ArenaProtectionListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TntArenaCommand implements CommandExecutor {
    private final ArenaProtectionListener protectionListener;
    private final Plugin plugin;
    private final JavaPlugin javaPlugin;

    public TntArenaCommand(ArenaProtectionListener protectionListener, Plugin plugin, JavaPlugin javaPlugin) {
        this.protectionListener = protectionListener;
        this.plugin = plugin;
        this.javaPlugin = javaPlugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only Players can use this command!");
            return true;
        }

        Player player = (Player) commandSender;
        World world = player.getWorld();
        Location center = player.getLocation();

        int arenaSize = 16;
        int height = 17;
        int borderThickness = 1;

        // Calculate the starting corner
        int startX = center.getBlockX() - (arenaSize / 2);
        int startZ = center.getBlockZ() - (arenaSize / 2);
        int y = center.getBlockY() - 1; // Floor below the player

        // Build the arena
        for (int x = 0; x < arenaSize; x++) {
            for (int z = 0; z < arenaSize; z++) {
                for (int dy = 0; dy < height; dy++) {
                    Location blockLocation = new Location(world, startX + x, y + dy, startZ + z);
                    Block block = blockLocation.getBlock();

                    if (dy == 0) { // Floor layer
                        if (x == 0 || z == 0 || x == arenaSize - 1 || z == arenaSize - 1) {
                            blockLocation.getBlock().setType(Material.SEA_LANTERN); // Floor border
                            protectionListener.addProtectedBlock(block); // Add to protected list
                        } else {
                            blockLocation.getBlock().setType(Material.SMOOTH_STONE); // Inner floor
                            protectionListener.addProtectedBlock(block); // Add to protected list
                        }
                    } else { // Wall layers
                        if (x == 0 || z == 0 || x == arenaSize - 1 || z == arenaSize - 1) {
                            if (dy == height - 1) {
                                blockLocation.getBlock().setType(Material.SEA_LANTERN); // Top edge of walls
                            } else {
                                blockLocation.getBlock().setType(Material.WHITE_STAINED_GLASS); // Walls
                            }
                            protectionListener.addProtectedBlock(block); // Add to protected list
                        }
                    }
                }
            }
        }

        ArenaBlockListener arenaBlockListener = new ArenaBlockListener(y, center, arenaSize - 2, height);
        ArenaFillListener arenaFillListener = new ArenaFillListener(y, center, arenaSize - 2, height, javaPlugin);
        plugin.getServer().getPluginManager().registerEvents(arenaBlockListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(arenaFillListener, plugin);

        player.sendMessage("§aTNT Arena created!");
        return true;
    }
}
