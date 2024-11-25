package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashSet;
import java.util.Set;

public class ArenaProtectionListener implements Listener {
    private final Set<Block> protectedBlocks = new HashSet<>();

    // Register protected blocks
    public void addProtectedBlock(Block block) {
        protectedBlocks.add(block);
    }

    // Block break event handler
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if the block is protected
        if (protectedBlocks.contains(block)) {
            if (!player.isOp()) {  // Allow only operators to break
                event.setCancelled(true);
            }
        }
    }

    // Prevent explosion damage
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(protectedBlocks::contains);
    }
}
