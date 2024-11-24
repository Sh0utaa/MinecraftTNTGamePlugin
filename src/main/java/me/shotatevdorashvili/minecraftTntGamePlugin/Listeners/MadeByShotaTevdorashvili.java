package me.shotatevdorashvili.minecraftTntGamePlugin.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MadeByShotaTevdorashvili implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(ChatColor.WHITE + "This plugin was made by " + ChatColor.GOLD + "Shota Tevdorashvili https://github.com/Sh0utaa/MinecraftTntGamePlugin");
    }
}
