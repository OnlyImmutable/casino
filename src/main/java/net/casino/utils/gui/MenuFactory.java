package net.casino.utils.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuFactory implements InventoryHolder {

    private final Inventory inventory;

    private final String title;
    private final int rows;

    private List<MenuItem> items;

    /**
     * Construct the MenuManager.
     * @param title - the title.
     * @param rows - the rows.
     */
    public MenuFactory(String title, int rows)  {
        this.title = title;
        this.rows = rows;

        items = new ArrayList<>();
        inventory = Bukkit.createInventory(this, (rows * 9), ChatColor.translateAlternateColorCodes('&', title));
    }

    /**
     * Add a new item to the inventory.
     * @param item - the item.
     */
    public void addItem(MenuItem item) {
        items.add(item);
    }

    /**
     * Open inventory.
     * @param player - the player who you open it for.
     */
    public void openInventory(Player player) {

        if (inventory == null) return;

        inventory.clear();

        for (MenuItem item : items)
            inventory.setItem(item.getIndex(), item.getItemStack());

        player.openInventory(inventory);
    }

    /**
     * Get the inventory instance.
     * @return Inventory
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the inventory title.
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the number of rows.
     * @return Number of rows (1 - 6).
     */
    public int getRows() {
        return rows;
    }

    /**
     * Get items in the inventory.
     * @return MenuItem - list
     */
    public List<MenuItem> getItems() {
        return items;
    }
}

