package net.casino.utils;

import net.casino.CasinoPlugin;
import net.casino.utils.itemstack.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class CoinUtil {

    public static void addCoin(Player player, int amount) {
        player.getInventory().addItem(new ItemFactory(Material.valueOf(CasinoPlugin.getInstance().getConfig().getString("slots.items.coins.item")), amount)
                .setDisplayName(CasinoPlugin.getInstance().getConfig().getString("slots.items.coins.displayName"))
                .setDurability((byte) CasinoPlugin.getInstance().getConfig().getInt("slots.items.coins.durability"))
                .setUnbreakable()
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .build());
    }

    public static void removeCoinByAmount(Player player, int quantity) {

        int quantityLeft = quantity;

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack checkItem = player.getInventory().getItem(slot);

            if (quantityLeft <= 0) {
                break;
            }

            if (checkItem == null)
                continue;

            if (!isCoin(checkItem)) continue;

            int amount = checkItem.getAmount() - quantityLeft;

            if (amount > 0) {
                checkItem.setAmount(amount);
            } else {
                player.getInventory().setItem(slot, null);
            }

            quantityLeft -= (amount + quantityLeft);
            player.updateInventory();
        }
    }

    public static boolean hasEnoughCoin(Player player, int quantity) {
        int amount = 0;
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (amount >= quantity) break;

            if (item != null && isCoin(item)) {
                amount += item.getAmount();
            }
        }

        if (amount >= quantity) {
            return true;
        }
        return false;
    }

    public static int getTotalCoins(Player player) {
        int amount = 0;
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (isCoin(item)) {
                amount += item.getAmount();
            }
        }

        return amount;
    }

    public static boolean isCoin(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.valueOf(CasinoPlugin.getInstance().getConfig().getString("slots.items.coins.item"))) return false;
        if (item.getItemMeta() == null) return false;
        if (item.getDurability() != (short) CasinoPlugin.getInstance().getConfig().getInt("slots.items.coins.durability")) return false;
        if (!item.getItemMeta().isUnbreakable()) return false;
        return true;
    }
}
