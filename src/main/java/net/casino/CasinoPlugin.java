package net.casino;

import net.casino.casino.CasinoManager;
import net.casino.casino.defaultgames.slots.SlotMachineGame;
import net.casino.commands.CommandManager;
import net.casino.listener.ConnectionListener;
import net.casino.messages.MessageManager;
import net.casino.utils.gui.MenuManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CasinoPlugin extends JavaPlugin {

    private static CasinoPlugin instance;

    public String RESOURCEPACK_DOWNLOAD = "https://www.dropbox.com/s/nkdnh7qga5ppse8/Casino%20Resourcepack.zip?dl=1";

    private CasinoManager casinoManager;
    private CommandManager commandManager;
    private MessageManager messageManager;

    private Economy economy;

//    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public void onEnable() {

        instance = this;

//        Date today = new Date();
//
//        try {
//            if (today.after(sdf.parse("11-06-2018 00:01:00"))) {
//                System.out.println("Your trial date has passed. Please purchase the full plugin at: https://www.spigotmc.org/resources/casino.56727/");
//                Bukkit.shutdown();
//                return;
//            }
//        } catch (ParseException e) {
//            System.out.println("Your trial date has passed. Please purchase the full plugin at: https://www.spigotmc.org/resources/casino.56727/");
//            Bukkit.shutdown();
//            return;
//        }

        saveResource("config.yml", false);
        saveResource("slotmachines.yml", false);
        saveResource("messages.yml", false);
        saveResource("rewards.yml", false);

        getConfig().options().copyDefaults(true);
        saveConfig();

        boolean isVaultEnabled = getConfig().getBoolean("casino.vault.enabled");

        if (isVaultEnabled) {
            setupVaultEconomy();
            System.out.println("[Casino] Vault is now required!");

            if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
                System.out.println("[Casino] You require Vault! To fix this error install Vault or disable it in the config.yml.");
                Bukkit.shutdown();
                return;
            }
        }

        messageManager = new MessageManager();

        commandManager = new CommandManager();
        commandManager.registerCommands();

        // Enabling casino games will be done via the API other than slots!
        casinoManager = new CasinoManager();

        new BukkitRunnable() {

            @Override
            public void run() {
                casinoManager.enableCasinoGame(new SlotMachineGame());
            }
        }.runTaskLater(this, 20L);

        Bukkit.getPluginManager().registerEvents(new MenuManager(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);

        if (getConfig().getString("casino.resourcepack.customlink").trim().length() > 5) {
            RESOURCEPACK_DOWNLOAD = getConfig().getString("casino.resourcepack.customlink").trim();
            System.out.println("Loading your custom resourcepack for players now.. " + RESOURCEPACK_DOWNLOAD);
        }

        System.out.println("Casino has started up successfully.");
    }

    @Override
    public void onDisable() {
        casinoManager.getCasinoGame(SlotMachineGame.class).onGameDisable();
        System.out.println("Casino has shutdown successfully.");
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CasinoManager getCasinoManager() {
        return casinoManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    // Handle Vault API setup
    private boolean setupVaultEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public static CasinoPlugin getInstance() {
        return instance;
    }
}
