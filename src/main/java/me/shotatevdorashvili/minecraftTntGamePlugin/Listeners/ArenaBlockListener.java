package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ArenaBlockListener implements Listener {
    private final int arenaFloorY; // Floor Y-level
    private final int arenaHeight; // Arena height

    public ArenaBlockListener(int arenaFloorY, int arenaHeight) {
        this.arenaFloorY = arenaFloorY;
        this.arenaHeight = arenaHeight;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        int blockHeight = block.getY() - arenaFloorY; // Calculate height relative to the arena floor

        // Transform block only within the arena height range
        if (blockHeight >= 1 && blockHeight <= arenaHeight) {
            if (blockHeight <= 4) { // First 4 levels (Iron blocks)
                block.setType(Material.IRON_BLOCK);
            } else if (blockHeight == 8) { // Fifth level (Gold block)
                block.setType(Material.GOLD_BLOCK);
            } else if (blockHeight == 12) { // Sixth level (Diamond block)
                block.setType(Material.DIAMOND_BLOCK);
            } else if (blockHeight == 16) { // Seventh level (Emerald block)
                block.setType(Material.EMERALD_BLOCK);
            }
        }
    }
}
