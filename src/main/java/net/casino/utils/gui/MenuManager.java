package net.casino.utils.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MenuManager implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getInventory() == null) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder == null) return;;

        if (!(holder instanceof MenuFactory)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        MenuFactory menu = (MenuFactory) holder;

        ItemStack clicked = event.getCurrentItem();
        ClickType clickType = event.getClick();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clickType == null) return;

        for (MenuItem menuItem : menu.getItems()) {

            if (!menuItem.getItemStack().equals(clicked)) continue;

            menuItem.click(player, clickType);
            break;
        }
    }
}