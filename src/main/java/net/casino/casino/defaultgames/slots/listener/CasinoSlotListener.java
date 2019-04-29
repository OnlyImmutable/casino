package net.casino.casino.defaultgames.slots.listener;

import net.casino.CasinoPlugin;
import net.casino.casino.CasinoManager;
import net.casino.casino.defaultgames.slots.SlotMachine;
import net.casino.casino.defaultgames.slots.SlotMachineGame;
import net.casino.casino.defaultgames.slots.gui.SlotMachineStartGui;
import net.casino.casino.defaultgames.slots.gui.creation.SlotMachineDirectionGui;
import net.casino.casino.defaultgames.slots.slot.SlotWheelPosition;
import net.casino.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.List;

public class CasinoSlotListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();

            if (item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null && item.getItemMeta().isUnbreakable()) {
                if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Slot Machine Creator")) {
                    new SlotMachineDirectionGui(player, event.getClickedBlock().getLocation().add(0, 1, 0));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof ArmorStand) {
            CasinoManager manager = CasinoPlugin.getInstance().getCasinoManager();
            SlotMachineGame slotMachineGame = ((SlotMachineGame) manager.getCasinoGame(SlotMachineGame.class));
            SlotMachine slotMachine = slotMachineGame.getSlotMachineFromEntity(entity);

            ItemStack item = player.getInventory().getItemInMainHand();

            if (item != null && !CoinUtil.isCoin(item)) {
                if (slotMachine != null && item.getItemMeta() != null && item.getItemMeta().isUnbreakable()) {
                    if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Slot Machine Remover")) {
                        for (int i = 0; i < slotMachineGame.getLoadedSlotMachines().size(); i++) {
                            SlotMachine machine = slotMachineGame.getLoadedSlotMachines().get(i);

                            if (machine.getBaseLocation() == slotMachine.getBaseLocation()) {
                                slotMachineGame.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()), null);
                                slotMachineGame.getConfiguration().save();
                                slotMachineGame.getLoadedSlotMachines().get(i).despawnAllStands();
                                slotMachineGame.getLoadedSlotMachines().remove(i);
                                break;
                            }
                        }
                        CustomChatMessage.sendMessage(player, "&cRemoved Slot Machine " + slotMachine.getUniqueId());
                    }
                    return;
                }
            }

            if (slotMachine != null) {
                if (slotMachine.isGuiEnabled()) {
                    new SlotMachineStartGui(player, slotMachine);
                } else {

                    FileConfiguration configuration = CasinoPlugin.getInstance().getConfig();
                    int vaultCostPerUse = ParserUtil.parseInt(configuration.getString("slots.vault.costPerUse")).isPresent() ? ParserUtil.parseInt(configuration.getString("slots.vault.costPerUse")).get() : 100;
                    int coinCostPerUse = ParserUtil.parseInt(configuration.getString("slots.coins.costPerUse")).isPresent() ? ParserUtil.parseInt(configuration.getString("slots.coins.costPerUse")).get() : 1;
                    boolean isVaultEnabled = configuration.getBoolean("casino.vault.enabled");
                    boolean isCoinEnabled = configuration.getBoolean("casino.coins.enabled");

                    if (slotMachine.inProgress()) {
                        CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("inUse"));
                        player.closeInventory();
                        return;
                    }

                    if (!player.isSneaking() && isVaultEnabled) {
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
                        slotMachine.setCurrentlyUsing(player);
                        slotMachine.beginRolling();
                        player.closeInventory();
                    } else if (player.isSneaking() && isCoinEnabled) {
                        if (CoinUtil.hasEnoughCoin(player, coinCostPerUse)) {
                            CoinUtil.removeCoinByAmount(player, coinCostPerUse);
                            CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("takenFromAmountChips").replace("<amount>", String.valueOf(coinCostPerUse)));
                            slotMachine.setCurrentlyUsing(player);
                            slotMachine.beginRolling();
                            player.closeInventory();
                        } else {
                            CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("notEnoughChips").replace("<amount>", String.valueOf(coinCostPerUse)));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.getRightClicked().hasMetadata("casinoSlotBase") || event.getRightClicked().hasMetadata("casinoSlotID") || event.getRightClicked().hasMetadata("casinoSlotHandle") || event.getRightClicked().hasMetadata("special")) {
            event.setCancelled(true);
        }
    }
}
