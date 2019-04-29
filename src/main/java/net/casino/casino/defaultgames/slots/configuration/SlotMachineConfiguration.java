package net.casino.casino.defaultgames.slots.configuration;

import net.casino.CasinoPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SlotMachineConfiguration {

    private File configurationFile;
    private FileConfiguration configuration;

    private File temporaryMachineCache;
    private FileConfiguration temporaryMachineCacheConfig;

    public SlotMachineConfiguration() {
        this.configurationFile = new File(CasinoPlugin.getInstance().getDataFolder(), "slotmachines.yml");
        this.temporaryMachineCache = new File(CasinoPlugin.getInstance().getDataFolder(), "temporarycache.yml");
        this.configuration = YamlConfiguration.loadConfiguration(configurationFile);
        this.temporaryMachineCacheConfig = YamlConfiguration.loadConfiguration(temporaryMachineCache);
        System.out.println("Loaded configuration for Slot Machines!");
    }

    public void save() {
        try {
            configuration.save(configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving shops.yml");
        }
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public File getTemporaryMachineCache() {
        return temporaryMachineCache;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public FileConfiguration getTemporaryMachineCacheConfig() {
        return temporaryMachineCacheConfig;
    }
}
