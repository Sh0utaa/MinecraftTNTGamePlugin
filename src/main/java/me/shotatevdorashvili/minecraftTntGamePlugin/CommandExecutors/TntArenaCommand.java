package me.shotatevdorashvili.minecraftTntGamePlugin.CommandExecutors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TntArenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only Players can use this command!");
            return true;
        }

        Player player = (Player) commandSender;
        World world = player.getWorld();
        Location center = player.getLocation();

        int arenaSize = 17;
        int height = 16;
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

                    if (dy == 0) { // Floor layer
                        if (x == 0 || z == 0 || x == arenaSize - 1 || z == arenaSize - 1) {
                            blockLocation.getBlock().setType(Material.SEA_LANTERN); // Floor border
                        } else {
                            blockLocation.getBlock().setType(Material.SMOOTH_STONE); // Inner floor
                        }
                    } else { // Wall layers
                        if (x == 0 || z == 0 || x == arenaSize - 1 || z == arenaSize - 1) {
                            if (dy == height - 1) {
                                blockLocation.getBlock().setType(Material.SEA_LANTERN); // Top edge of walls
                            } else {
                                blockLocation.getBlock().setType(Material.WHITE_STAINED_GLASS); // Walls
                            }
                        }
                    }
                }
            }
        }

        player.sendMessage("§aTNT Arena created!");
        return true;
    }
}
