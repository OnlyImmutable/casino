package net.casino.commands.defaults;

import net.casino.CasinoPlugin;
import net.casino.commands.attributes.CommandPermission;
import net.casino.utils.CoinUtil;
import net.casino.utils.CustomChatMessage;
import net.casino.utils.ParserUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermission(permission = "casino.coins")
public class CoinCommand extends Command {

    public CoinCommand() {
        super("coins");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;
        boolean requiresPermission = false;

        if (this.getClass().isAnnotationPresent(CommandPermission.class)) {
            requiresPermission = true;
        }

        if (requiresPermission && !player.hasPermission(getClass().getAnnotation(CommandPermission.class).permission())) {
            CustomChatMessage.sendMessage(player, CasinoPlugin.getInstance().getMessageManager().getMessage("noPermissions"));
            return false;
        }

        // coins give <name> <amount>, coins take <name> <amount>, coins balance <name>
        if (args.length < 1) {
            CustomChatMessage.sendMessage(player, "Casino help");
            CustomChatMessage.sendMessage(player, "coins give <name> <amount>");
            CustomChatMessage.sendMessage(player, "coins take <name> <amount>");
            CustomChatMessage.sendMessage(player, "coins balance <name>");
        } else {
            if (args.length == 3) {
                // give and take
                if (args[0].equalsIgnoreCase("give")) {

                    String username = args[1];
                    int amount = ParserUtil.parseInt(args[2]).isPresent() ? ParserUtil.parseInt(args[2]).get() : 0;

                    Player foundPlayer = Bukkit.getPlayer(username);

                    if (foundPlayer == null) {
                        // Offline
                        CustomChatMessage.sendMessage(player, username + " is offline!");
                        return false;
                    }

                    if (amount < 1) {
                        CustomChatMessage.sendMessage(player, "Please enter a valid amount!");
                        return false;
                    }

                    CoinUtil.addCoin(foundPlayer, amount);
                    CustomChatMessage.sendMessage(player, "You added " + amount + " coins to " + username);
                } else if (args[0].equalsIgnoreCase("take")) {

                    String username = args[1];
                    int amount = ParserUtil.parseInt(args[2]).isPresent() ? ParserUtil.parseInt(args[2]).get() : 0;

                    Player foundPlayer = Bukkit.getPlayer(username);

                    if (foundPlayer == null) {
                        // Offline
                        CustomChatMessage.sendMessage(player, username + " is offline!");
                        return false;
                    }

                    if (amount < 1) {
                        CustomChatMessage.sendMessage(player, "Please enter a valid amount!");
                        return false;
                    }

                    CoinUtil.removeCoinByAmount(foundPlayer, amount);
                    CustomChatMessage.sendMessage(player, "You removed " + amount + " coins from " + username);
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("balance")) {

                    String username = args[1];
                    Player foundPlayer = Bukkit.getPlayer(username);

                    if (foundPlayer == null) {
                        // Offline
                        CustomChatMessage.sendMessage(player, username + " is offline!");
                        return false;
                    }

                    CustomChatMessage.sendMessage(player, username + " has " + CoinUtil.getTotalCoins(foundPlayer) + " coins..");
                }
            }
        }
        return false;
    }
}
