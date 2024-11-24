package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ArenaBlockListener implements Listener {

    private final int arenaBaseY; // Y-level of the arena floor
    private final Location arenaLocation; // Starting location of the arena
    private final int arenaSize; // Size of the arena (16x16)
    private final int arenaHeight; // Height of the arena

    public ArenaBlockListener(int baseY, Location arenaLocation, int size, int height) {
        this.arenaBaseY = baseY;
        this.arenaLocation = arenaLocation;
        this.arenaSize = size;
        this.arenaHeight = height;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location blockLoc = event.getBlock().getLocation();

        // Check if the block is within the arena's boundaries
        if (isInsideArena(blockLoc)) {

            int y = blockLoc.getBlockY();
            Material newMaterial = null;

            // Apply block material transformation based on Y-level
            if (y == arenaBaseY) {
                newMaterial = Material.SMOOTH_STONE; // The base layer (floor)
            } else if (y < arenaBaseY + 5) {
                newMaterial = Material.IRON_BLOCK; // First 4 blocks are Iron
            } else if (y < arenaBaseY + 9) {
                newMaterial = Material.GOLD_BLOCK; // Next 4 blocks are Gold
            } else if (y < arenaBaseY + 13) {
                newMaterial = Material.DIAMOND_BLOCK; // Next 4 blocks are Diamond
            } else if (y < arenaBaseY + arenaHeight) {
                newMaterial = Material.EMERALD_BLOCK; // Above that, Emerald
            }

            if (newMaterial != null) {
                blockLoc.getBlock().setType(newMaterial);
            }
        }
    }

    // Helper method to check if the block is inside the arena
    private boolean isInsideArena(Location blockLoc) {
        return blockLoc.getX() >= arenaLocation.getX() && blockLoc.getX() < arenaLocation.getX() + arenaSize &&
                blockLoc.getZ() >= arenaLocation.getZ() && blockLoc.getZ() < arenaLocation.getZ() + arenaSize &&
                blockLoc.getY() >= arenaBaseY && blockLoc.getY() < arenaBaseY + arenaHeight;
    }
}
