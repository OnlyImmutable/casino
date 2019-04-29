package net.casino.utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MenuItem {

    private final ItemStack itemStack;
    private final int index;

    /**
     * Construct a new MenuItem.
     *
     * @param index - the slot
     * @param itemStack - the itemstack
     */
    public MenuItem(int index, ItemStack itemStack) {
        this.itemStack = itemStack;
        this.index = index;
    }

    /**
     * Get the ItemStack
     * @return ItemStack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the slot
     * @return Slot
     */
    public int getIndex() {
        return index;
    }

    /**
     * Ran when you click the item.
     * @param player - the player clicking.
     * @param clickType - the click type.
     */
    public void click(Player player, ClickType clickType) {}
}
