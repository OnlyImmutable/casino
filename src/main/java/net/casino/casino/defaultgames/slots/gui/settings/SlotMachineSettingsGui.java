package net.casino.casino.defaultgames.slots.gui.settings;

import net.casino.casino.defaultgames.slots.SlotMachine;
import net.casino.utils.gui.MenuFactory;
import org.bukkit.entity.Player;

public class SlotMachineSettingsGui extends MenuFactory {

    public SlotMachineSettingsGui(Player player, SlotMachine slotMachine) {
        super("Slot Machine " + slotMachine.getUniqueId() + " settings", 5);

        openInventory(player);
    }
}
