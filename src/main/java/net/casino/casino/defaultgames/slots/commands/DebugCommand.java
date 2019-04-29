package net.casino.casino.defaultgames.slots.commands;

import net.casino.CasinoPlugin;
import net.casino.casino.CasinoManager;
import net.casino.casino.defaultgames.slots.SlotMachine;
import net.casino.casino.defaultgames.slots.SlotMachineGame;
import net.casino.casino.defaultgames.slots.gui.SlotMachineMainGui;
import net.casino.commands.attributes.CommandPermission;
import net.casino.utils.AngleUtil;
import net.casino.utils.CustomChatMessage;
import net.casino.utils.itemstack.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import java.util.Arrays;

@CommandPermission(permission = "casino.admin")
public class DebugCommand extends Command {


    public DebugCommand() {
        super("casinoadmin");
        setAliases(Arrays.asList("ca", "casinoa", "admincasino"));
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {

        Player player = (Player) commandSender;
        boolean requiresPermission = false;

        if (this.getClass().isAnnotationPresent(CommandPermission.class)) {
            requiresPermission = true;
        }

        if (requiresPermission && !player.hasPermission(getClass().getAnnotation(CommandPermission.class).permission())) {
            CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("noPermissions"));
            return false;
        }

        new SlotMachineMainGui((Player) commandSender);
        return false;
    }

}
