package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.Sound;

public class ArenaFillListener implements Listener {
    private final int arenaBaseY; // Y-level of the arena floor
    private final Location arenaLocation; // Center location of the arena
    private final int arenaSize; // Size of the arena (e.g., 16x16)
    private final int arenaHeight; // Height of the arena

    public ArenaFillListener(int baseY, Location arenaLocation, int size, int height) {
        this.arenaBaseY = baseY;
        this.arenaLocation = arenaLocation;
        this.arenaSize = size;
        this.arenaHeight = height;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        int topY = arenaBaseY + arenaHeight - 1; // Y-level of the top layer

        // Check if the placed block is within the top layer
        if (placedBlock.getY() == topY && isWithinArena(placedBlock.getLocation())) {
            if (isTopLayerFilled()) {
                // Top layer is completely filled — trigger your action here
                Player player = event.getPlayer();

                player.playSound(player.getLocation(), Sound.BLOCK_BASALT_BREAK, 1.0f, 1.0f);
                player.sendTitle(ChatColor.GREEN + "Arena Complete!", "The top layer is filled!", 10, 70, 20);
            }
        }
    }

    private boolean isWithinArena(Location location) {
        int startX = arenaLocation.getBlockX() - (arenaSize / 2);
        int startZ = arenaLocation.getBlockZ() - (arenaSize / 2);
        int endX = startX + arenaSize;
        int endZ = startZ + arenaSize;

        return location.getBlockX() >= startX && location.getBlockX() < endX &&
                location.getBlockZ() >= startZ && location.getBlockZ() < endZ;
    }

    private boolean isTopLayerFilled() {
        int topY = arenaBaseY + arenaHeight - 1;
        int startX = arenaLocation.getBlockX() - (arenaSize / 2);
        int startZ = arenaLocation.getBlockZ() - (arenaSize / 2);

        for (int x = 0; x < arenaSize; x++) {
            for (int z = 0; z < arenaSize; z++) {
                Location blockLocation = new Location(arenaLocation.getWorld(), startX + x, topY, startZ + z);
                Block block = blockLocation.getBlock();
                if (block.getType() == Material.AIR) {
                    return false; // If any block is air, return false
                }
            }
        }
        return true; // All blocks in the top layer are filled
    }
}
