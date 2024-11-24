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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaFillListener implements Listener {
    private final int arenaBaseY; // Y-level of the arena floor
    private final Location arenaLocation; // Center location of the arena
    private final int arenaSize; // Size of the arena (e.g., 16x16)
    private final int arenaHeight; // Height of the arena
    private final JavaPlugin plugin;

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
        int topY = arenaBaseY + arenaHeight - 1; // Y-level of the top layer

// Check if the placed block is within the top layer
        if (placedBlock.getY() == topY && isWithinArena(placedBlock.getLocation())) {
            if (isTopLayerFilled()) {

                // Top layer is completely filled — trigger your action here
                Player player = event.getPlayer();

                // Start the countdown task
                new BukkitRunnable() {
                    int countdown = 15; // Start countdown from 15 seconds
                    int period = 20;    // Initial delay (1 second)
                    boolean topLayerFilled = true; // To track the top layer state

                    @Override
                    public void run() {
                        // Start a new task to continuously check if the top layer is filled
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!isTopLayerFilled()) {
                                    topLayerFilled = false; // Stop the countdown if top layer is no longer filled
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 20L); // Check every second (20 ticks)

                        if (countdown >= 0 && topLayerFilled) {
                            // Continue countdown only if top layer is still filled
                            player.sendTitle(ChatColor.RED + "Countdown: " + countdown, "", 0, 20, 0);
                            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.0f);

                            // If countdown reaches 3, change the delay to 5 seconds (100 ticks)
                            if (countdown == 3) {
                                period = 100; // Change period to 100 ticks (5 seconds)
                                this.cancel(); // Cancel current task to reschedule it with new period
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        // Continue countdown with new period
                                        if (!topLayerFilled) {
                                            // If the top layer is no longer filled, stop the countdown
                                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                                            player.sendTitle(ChatColor.RED + "Canceled!", "", 0, 70, 20);
                                            this.cancel(); // Cancel the task
                                            return; // Exit the method
                                        }

                                        // Display the countdown as the title
                                        player.sendTitle(ChatColor.RED + "Countdown: " + countdown, "", 0, 20, 0);

                                        // Play Sound Affect
                                        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.0f);

                                        // When countdown reaches 0, trigger the function
                                        if (countdown == 0) {
                                            this.cancel(); // Stop the countdown task
                                            onCountdownComplete(player); // Call the function you want to trigger
                                        }

                                        countdown--; // Decrease the countdown by 1 second
                                    }
                                }.runTaskTimer(plugin, period, 20L); // Reschedule with the new delay
                            }

                            // When the countdown reaches 0, trigger a function
                            if (countdown == 0) {
                                this.cancel(); // Stop the countdown task
                                onCountdownComplete(player); // Call the function you want to trigger
                            }

                            countdown--; // Decrease the countdown by 1 second
                        } else if (!topLayerFilled) {
                            // Stop countdown and inform the player when the top layer is no longer filled
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                            player.sendTitle(ChatColor.RED + "Canceled!", "", 0, 70, 20);
                            this.cancel(); // Cancel the task
                        }
                    }
                }.runTaskTimer(plugin, 20L, 20L); // Run every 20 ticks (1 second initially)
            }
        }


    }

    // Your function that gets triggered when the countdown hits 0
    public void onCountdownComplete(Player player) {
        // Your logic when the countdown is over
        player.sendMessage(ChatColor.GREEN + "Countdown Complete! Triggering function...");
        // You can add any function or event here, for example, starting a game, etc.
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
