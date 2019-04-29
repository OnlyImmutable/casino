package net.casino.casino.defaultgames.slots.gui;

import net.casino.casino.defaultgames.slots.gui.settings.SlotMachineSelectorGui;
import net.casino.utils.CustomChatMessage;
import net.casino.utils.gui.MenuFactory;
import net.casino.utils.gui.MenuItem;
import net.casino.utils.itemstack.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SlotMachineMainGui extends MenuFactory {

    public SlotMachineMainGui(Player player) {

        super("Slot Machine", 3);

        addItem(new MenuItem(11, new ItemFactory(Material.STICK)
            .setDisplayName("&aCreate a new Slot Machine")
            .setLore("", "&7Click to build a new Slot Machine.")
            .build()) {

            @Override
            public void click(Player player, ClickType clickType) {

                player.getInventory().addItem(new ItemFactory(Material.STICK)
                        .setDisplayName("&aSlot Machine Creator")
                        .setUnbreakable()
                        .build());
                CustomChatMessage.sendMessage(player,  "&aRight click the spot you want to place your Slot Machine!");
            }
        });

        addItem(new MenuItem(13, new ItemFactory(Material.BOOK)
                .setDisplayName("&e&lEdit Slot Machine Settings")
                .setLore("", "&7Click to edit a Slot Machines settings.")
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
//                new SlotMachineSelectorGui(player);
                CustomChatMessage.sendMessage(player, "This feature is coming in the future!");
            }
        });

        addItem(new MenuItem(15, new ItemFactory(Material.STICK)
                .setDisplayName("&aRemove a Slot Machine")
                .setLore("", "&7Click to remove a current Slot Machine.")
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {

                player.getInventory().addItem(new ItemFactory(Material.STICK)
                        .setDisplayName("&cSlot Machine Remover")
                        .setUnbreakable()
                        .build());

                CustomChatMessage.sendMessage(player, "&cRight click the Slot Machine you want to remove.");
            }
        });

        addItem(new MenuItem(getInventory().getSize() - 1, new ItemFactory(Material.BARRIER)
                .setDisplayName("&c&lClear Armourstands")
                .setLore("&7Clear armourstands within a 4 block radius.")
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                player.getNearbyEntities(4, 4, 4).stream().filter(entity -> entity instanceof ArmorStand).forEach(Entity::remove);
                CustomChatMessage.sendMessage(player, "Removed nearby armourstands.");
            }
        });

        openInventory(player);
    }
}
