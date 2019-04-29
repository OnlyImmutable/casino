package net.casino.casino.defaultgames.slots.gui;

import net.casino.CasinoPlugin;
import net.casino.casino.defaultgames.slots.SlotMachine;
import net.casino.utils.CoinUtil;
import net.casino.utils.CustomChatMessage;
import net.casino.utils.ParserUtil;
import net.casino.utils.gui.MenuFactory;
import net.casino.utils.gui.MenuItem;
import net.casino.utils.itemstack.ItemFactory;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import sun.net.www.ParseUtil;
import org.bukkit.inventory.ItemStack;

public class SlotMachineStartGui extends MenuFactory {

    public SlotMachineStartGui(Player player, SlotMachine machine) {
        super("Slot Machines", 3);

        FileConfiguration configuration = CasinoPlugin.getInstance().getConfig();
        int vaultCostPerUse = ParserUtil.parseInt(configuration.getString("slots.vault.costPerUse")).isPresent() ? ParserUtil.parseInt(configuration.getString("slots.vault.costPerUse")).get() : 100;
        int coinCostPerUse = ParserUtil.parseInt(configuration.getString("slots.coins.costPerUse")).isPresent() ? ParserUtil.parseInt(configuration.getString("slots.coins.costPerUse")).get() : 1;
        boolean isVaultEnabled = configuration.getBoolean("casino.vault.enabled");
        boolean isCoinEnabled = configuration.getBoolean("casino.coins.enabled");

        for (int i = 0; i < (getRows() * 9); i++) {
            addItem(new MenuItem(i, new ItemFactory(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                    .setDisplayName("&7&lSLOTS")
                    .build()));
        }

        addItem(new MenuItem(13, new ItemFactory(Material.IRON_HOE).setUnbreakable().setDurability((short) 4)
                .setDisplayName("&b&lRun the Slot Machine!")
                .setLore("",
                        "&7Click to start the slot machine.",
                        "",
                        (isVaultEnabled ? "&bCost&f: &7$" + vaultCostPerUse + " &a(Left click)" : ""),
                        (isCoinEnabled ? "&bCost&f: &7" + coinCostPerUse + " coins &a(Shift-left click)" : "")
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {

                if (machine.inProgress()) {
                    CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("inUse"));
                    player.closeInventory();
                    return;
                }

                if (clickType == ClickType.LEFT) {
                    if (CasinoPlugin.getInstance().getEconomy() == null) {
                        CustomChatMessage.sendMessage(player, "Vault is not enabled in the configuration file, please alert a staff member!");
                        return;
                    }

                    if (CasinoPlugin.getInstance().getEconomy().getBalance(player) < vaultCostPerUse) {
                        CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("notEnoughVault").replace("<amount>", String.valueOf(vaultCostPerUse)));
                        return;
                    }

                    CasinoPlugin.getInstance().getEconomy().withdrawPlayer(player, vaultCostPerUse);
                    CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("takenFromAmountVault").replace("<amount>", String.valueOf(vaultCostPerUse)));
                    machine.setCurrentlyUsing(player);
                    machine.beginRolling();
                    player.closeInventory();
                } else if(clickType == ClickType.SHIFT_LEFT){
                    if (CoinUtil.hasEnoughCoin(player, coinCostPerUse)) {
                        CoinUtil.removeCoinByAmount(player, coinCostPerUse);
                        CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("takenFromAmountChips").replace("<amount>", String.valueOf(coinCostPerUse)));
                        machine.setCurrentlyUsing(player);
                        machine.beginRolling();
                        player.closeInventory();
                    } else {
                        CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("notEnoughChips").replace("<amount>", String.valueOf(coinCostPerUse)));
                    }
                }
            }
        });

        openInventory(player);
    }
}
