package net.casino.utils;

import net.casino.CasinoPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CustomChatMessage {

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CasinoPlugin.getInstance().getMessageManager().getMessage("prefix") + message));
    }
}
