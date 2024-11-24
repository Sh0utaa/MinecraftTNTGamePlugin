package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ArenaFillListener implements Listener {
    private final int arenaBaseY;
    private final Location arenaLocation;
    private final int arenaSize;
    private final int arenaHeight;
    private final JavaPlugin plugin;

    private boolean countdownActive = false; // Track countdown state
    private BukkitTask countdownTask;        // Reference to cancel the running task

    public ArenaFillListener(int baseY, Location arenaLocation, int size, int height, JavaPlugin plugin) {
        this.arenaBaseY = baseY;
        this.arenaLocation = arenaLocation;
        this.arenaSize = size;
        this.arenaHeight = height;
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        int topY = arenaBaseY + arenaHeight - 1;

        // Check if the placed block is within the top layer
        if (placedBlock.getY() == topY && isWithinArena(placedBlock.getLocation())) {
            if (isTopLayerFilled() && !countdownActive) {
                Player player = event.getPlayer();
                startCountdown(player, 10);  // Start countdown if not already running
            }
        }
    }

    private void startCountdown(Player player, int countdownSeconds) {
        countdownActive = true;
        player.playSound(player.getLocation(), Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.0f, 1.0f);

        countdownTask = new BukkitRunnable() {
            int timeLeft = countdownSeconds;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    player.sendTitle(ChatColor.RED + "" + timeLeft + " sec", "", 0, 20, 0);
                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.0f);
                    timeLeft--;
                } else {
                    // Countdown reached zero, trigger action and cancel task
                    onCountdownComplete(player);
                    countdownActive = false;
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Delay is 0 ticks, repeating every 20 ticks (1 second)
    }

    private void onCountdownComplete(Player player) {
        player.sendMessage(ChatColor.GREEN + "Countdown Complete! Triggering function...");
        player.sendTitle(ChatColor.GREEN + "Countdown Complete!", "", 0, 40, 20);
        // Add your logic here for what happens when the countdown finishes
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        int topY = arenaBaseY + arenaHeight - 1;

        // Check if the broken block is part of the top layer and inside the arena
        if (countdownActive && brokenBlock.getY() == topY && isWithinArena(brokenBlock.getLocation())) {
            countdownTask.cancel();     // Cancel the countdown task
            countdownActive = false;    // Reset state
            event.getPlayer().sendTitle(ChatColor.RED + "Canceled!", "", 0, 20, 0);
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
