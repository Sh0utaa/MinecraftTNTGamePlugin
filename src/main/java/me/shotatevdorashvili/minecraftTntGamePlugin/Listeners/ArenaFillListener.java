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
                startCountdown(player, 15, 20, plugin);

            }
        }
    }

    // Your function that gets triggered when the countdown hits 0
    public void onCountdownComplete(Player player) {
        // Your logic when the countdown is over
        player.sendMessage(ChatColor.GREEN + "Countdown Complete! Triggering function...");
        // You can add any function or event here, for example, starting a game, etc.
    }

    public void startCountdown(Player player, int countdown, int delay, JavaPlugin plugin) {
        // Display the starting countdown message
        player.sendTitle(ChatColor.GREEN + "Starting countdown", "", 0, 20, 0);
        player.playSound(player.getLocation(), Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.0f, 1.0f); // Start sound
        final int[] countdownArr = {countdown};

        // Start the countdown task
        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdownArr[0] >= 0) {
                    if(countdownArr[0] <= 3) {
                        this.cancel(); // Cancel the outer countdown task

                        // Start a new task that counts down from 3 to 0
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (countdownArr[0] > 0) {
                                    // Display the countdown number in red
                                    player.sendTitle(ChatColor.RED + "" + countdownArr[0] + " sec", "", 0, 100, 100);
                                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.0f); // Countdown sound

                                    if (countdownArr[0] == 0) {
                                        onCountdownComplete(player); // Call the function you want to trigger
                                        this.cancel(); // Stop the countdown task
                                    }

                                    countdownArr[0]--;
                                }
                            }
                        }.runTaskTimer(plugin, 100L, 100L); // Delay of 100 ticks for the inner countdown task
                    }

                    // Display the countdown number in red
                    player.sendTitle(ChatColor.RED + "" + countdownArr[0] + " sec", "", 0, 20, 0);
                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.0f); // Countdown sound

                    countdownArr[0]--; // Only decrement here in the outer task
                }
            }
        }.runTaskTimer(plugin, delay, delay); // Delay of 1 second (20 ticks)
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
