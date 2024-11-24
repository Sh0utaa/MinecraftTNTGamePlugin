package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MadeByShotaTevdorashvili implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        e.setJoinMessage(ChatColor.WHITE + "This Plugis was made by " + ChatColor.DARK_RED + "Shota Tevdorashvili " + ChatColor.DARK_PURPLE + " https://github.com/Sh0utaa/MinecraftTntGamePlugin");
    }
}
