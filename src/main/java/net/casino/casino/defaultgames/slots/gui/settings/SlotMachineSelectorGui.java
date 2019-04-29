package net.casino.casino.defaultgames.slots.gui.settings;

import net.casino.CasinoPlugin;
import net.casino.casino.CasinoGame;
import net.casino.casino.defaultgames.slots.SlotMachine;
import net.casino.casino.defaultgames.slots.SlotMachineGame;
import net.casino.utils.gui.MenuFactory;
import net.casino.utils.gui.MenuItem;
import net.casino.utils.itemstack.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SlotMachineSelectorGui extends MenuFactory {

    public SlotMachineSelectorGui(Player player) {
        super("Nearby Slot Machines", 5);

        // Nearby slot machines
        SlotMachineGame game = ((SlotMachineGame) CasinoPlugin.getInstance().getCasinoManager().getCasinoGame(SlotMachineGame.class));

        int position = 0;
        for (Entity entity : player.getNearbyEntities(30, 30, 30)) {
            if (entity instanceof ArmorStand && game.getSlotMachineFromEntity(entity) != null) {
                SlotMachine machine = game.getSlotMachineFromEntity(entity);
                NumberFormat formatter = new DecimalFormat("#0.00");

                addItem(new MenuItem(position, new ItemFactory(Material.IRON_HOE).setUnbreakable().setDurability((short) 4).addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .setDisplayName("&bSlot Machine " + machine.getUniqueId() + " " + (position == 0 ? "&a&lCLOSEST SLOT MACHINE" : ""))
                        .setLore("", "&7Distance: &a" + (formatter.format((entity.getLocation().distance(player.getLocation())))) + " blocks away from you.", "", "&aClick to edit settings.")
                        .build()) {

                    @Override
                    public void click(Player player, ClickType clickType) {
                        new SlotMachineSettingsGui(player, machine);
                    }
                });

                position += 1;
            }
        }

        openInventory(player);
    }
}