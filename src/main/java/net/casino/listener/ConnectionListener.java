package net.casino.listener;

import net.casino.CasinoPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Sets the players resourcepack..
        if (CasinoPlugin.getInstance().getConfig().getStringList("casino.resourcepack.enabledWorlds").contains(player.getWorld().getName().toLowerCase())) {

            new BukkitRunnable() {

                @Override
                public void run() {
                    player.setResourcePack(CasinoPlugin.getInstance().RESOURCEPACK_DOWNLOAD);
                }
            }.runTaskLater(CasinoPlugin.getInstance(), 20);
        }
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        World from = event.getFrom();
        World to = player.getWorld();

        List<String> casinoRPEnabled = CasinoPlugin.getInstance().getConfig().getStringList("casino.resourcepack.enabledWorlds");

        if (casinoRPEnabled.contains(from.getName()) && casinoRPEnabled.contains(to.getName())) {
            return;
        }

        if (casinoRPEnabled.contains(from.getName()) && !casinoRPEnabled.contains(to.getName())) {
            player.setResourcePack("https://github.com/Phoenix616/BungeeResourcepacks/blob/master/Empty.zip?raw=true");
            return;
        }

        if (!casinoRPEnabled.contains(from.getName()) && casinoRPEnabled.contains(to.getName())) {
            player.setResourcePack(CasinoPlugin.getInstance().RESOURCEPACK_DOWNLOAD);
        }
    }
}
