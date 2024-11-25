package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.EntityType;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

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
                startCountdown(player, 15);  // Start countdown if not already running
            }
        }
    }

    private void startCountdown(Player player, int countdownSeconds) {
        countdownActive = true;
        player.sendTitle(ChatColor.GREEN + "starting countdown...", "", 0, 20, 0);
        player.playSound(player.getLocation(), Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.0f, 1.0f);

        countdownTask = new BukkitRunnable() {
            int timeLeft = countdownSeconds;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    // Display the main countdown (only for 4 seconds or above)
                    if (timeLeft > 2) {
                        player.sendTitle(ChatColor.RED + "" + timeLeft + " sec", "", 0, 20, 0);
                        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.0f);
                    }

                    timeLeft--;
                } else {
                    // Once the main countdown reaches zero, start the nested countdown for 3, 2, and 1 with a 5-second interval
                    startNestedCountdown(player);
                    //countdownActive = false;
                    this.cancel(); // Stop the main countdown
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Delay of 0 ticks, repeating every 20 ticks (1 second)
    }
    private void onCountdownComplete(Player player) {
        player.sendTitle(ChatColor.GOLD + "VICTORY!", "", 0, 50, 20);

        // Calculate the starting corner for the arena
        int startX = arenaLocation.getBlockX() - (arenaSize / 2);
        int startZ = arenaLocation.getBlockZ() - (arenaSize / 2);
        int endX = startX + arenaSize;
        int endZ = startZ + arenaSize;
        int topY = arenaBaseY + arenaHeight - 1; // The top layer Y-level

        // Loop through each block inside the arena and set it to AIR
        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                for (int y = arenaBaseY + 1; y <= topY; y++) {
                    Location blockLoc = new Location(arenaLocation.getWorld(), x, y, z);

                    // Check if the block is within the arena's boundaries
                    if (isWithinArena(blockLoc)) {
                        Block block = blockLoc.getBlock();
                        block.setType(Material.AIR);  // Set the block to air
                    }
                }
            }
        }

        launchFirework(new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
    }

    // Nested countdown for 3, 2, and 1 seconds with 5-second intervals
    private void startNestedCountdown(Player player) {
        countdownTask = new BukkitRunnable() {
            int timeLeft = 2;

            @Override
            public void run() {
                if (timeLeft >= 1) {
                    player.sendTitle(ChatColor.RED + "" + timeLeft + " sec", "", 0, 20, 20);
                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.0f);
                    timeLeft--;


                } else if (timeLeft == 0) {
                    // Once the countdown reaches zero, trigger onCountdownComplete and stop the countdown
                    onCountdownComplete(player);
                    countdownActive = false;
                    this.cancel(); // Cancel the nested countdown
                }
            }
        }.runTaskTimer(plugin, 0L, 60L); // Delay of 60 ticks (3 seconds) between each nested countdown
    }


    private void launchFirework(Location location) {
        // Spawn the firework entity
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);

        // Set up the firework effect with multiple colors and styles
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE) // Multiple colors
                .withFade(Color.ORANGE, Color.AQUA)  // Color fade after explosion
                .with(FireworkEffect.Type.BALL_LARGE) // Larger explosion type
                .trail(true)   // Add trail to the explosion
                .flicker(true) // Add flicker to the explosion
                .build();
        meta.addEffect(effect);
        meta.setPower(0); // Adjust explosion height
        firework.setFireworkMeta(meta);
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        int topY = arenaBaseY + arenaHeight - 1;

        // Check if the broken block is part of the top layer and inside the arena
        if (countdownActive && brokenBlock.getY() == topY && isWithinArena(brokenBlock.getLocation())) {

            countdownTask.cancel();     // Cancel the countdown task
            countdownActive = false;    // Reset state
            event.getPlayer().sendTitle(ChatColor.RED + "canceled!", "", 0, 20, 0);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1.5f, 1.5f);
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        // Prevent blocks from dropping items
        event.setYield(0);

        // Check each block affected by the explosion
        for (Block brokenBlock : event.blockList()) {
            int topY = arenaBaseY + arenaHeight - 1;

            // If any block on the top layer inside the arena is broken
            if (countdownActive && brokenBlock.getY() == topY && isWithinArena(brokenBlock.getLocation())) {
                countdownTask.cancel();     // Cancel the countdown task
                countdownActive = false;    // Reset state

                // Notify the nearest player (or you can loop through all players if needed)
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().distance(brokenBlock.getLocation()) < 50) { // Adjust range as needed
                        player.sendTitle(ChatColor.RED + "canceled!", "", 0, 20, 0);
                        player.playSound(player.getLocation(), Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1.0f, 1.0f);
                    }
                }
                break; // No need to continue checking once one block is found
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
