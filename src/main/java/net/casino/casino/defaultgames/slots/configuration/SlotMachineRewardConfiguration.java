package net.casino.casino.defaultgames.slots.configuration;

import net.casino.CasinoPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SlotMachineRewardConfiguration {

    private File configurationFile;
    private FileConfiguration configuration;

    public SlotMachineRewardConfiguration() {
        this.configurationFile = new File(CasinoPlugin.getInstance().getDataFolder(), "rewards.yml");
        this.configuration = YamlConfiguration.loadConfiguration(configurationFile);
        System.out.println("Loaded rewards.yml!");
    }

    public void save() {
        try {
            configuration.save(configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving rewards.yml");
        }
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }
}
