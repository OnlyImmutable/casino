package net.casino.casino.defaultgames.slots;

import net.casino.CasinoPlugin;
import net.casino.casino.CasinoGame;
import net.casino.casino.defaultgames.slots.commands.DebugCommand;
import net.casino.casino.defaultgames.slots.configuration.SlotMachineConfiguration;
import net.casino.casino.defaultgames.slots.configuration.SlotMachineRewardConfiguration;
import net.casino.casino.defaultgames.slots.listener.CasinoSlotListener;
import net.casino.casino.defaultgames.slots.slot.SlotMachineItem;
import net.casino.utils.LocationUtil;
import net.casino.utils.ParserUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SlotMachineGame extends CasinoGame {

    private SlotMachineConfiguration configuration;
    private SlotMachineRewardConfiguration rewardConfiguration;

    private List<SlotMachine> loadedSlotMachines;

    @Override
    public void onGameEnable() {
        loadedSlotMachines = new ArrayList<>();
        configuration = new SlotMachineConfiguration();
        rewardConfiguration = new SlotMachineRewardConfiguration();

        for (String tempLocation : configuration.getTemporaryMachineCacheConfig().getStringList("tempslotmachines")) {
            Location location = LocationUtil.getLocationFromString(tempLocation);

            if (location.getWorld() != null) {
                location.getWorld().getNearbyEntities(location, 3, 3, 3).stream().filter(entity -> entity instanceof ArmorStand).forEach(Entity::remove);
            }
        }

        try {
            configuration.getTemporaryMachineCacheConfig().set("tempslotmachines", null);
            configuration.getTemporaryMachineCacheConfig().save(configuration.getTemporaryMachineCache());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (configuration.getConfiguration().getConfigurationSection("slotmachines") != null) {
            List<String> tempLocations = new ArrayList<>();
            for (String uniqueId : configuration.getConfiguration().getConfigurationSection("slotmachines").getKeys(false)) {
                int id = ParserUtil.parseInt(uniqueId).isPresent() ? ParserUtil.parseInt(uniqueId).get() : -1;
                Location location = LocationUtil.getLocationFromString(configuration.getConfiguration().getString("slotmachines." + uniqueId + ".location"));
                SlotMachine slotMachine = new SlotMachine(id, location);

                // Prizes
                for (String prizeId : configuration.getConfiguration().getConfigurationSection("slotmachines." + uniqueId + ".prizes").getKeys(false)) {
                    String prizeDisplayName = configuration.getConfiguration().getString("slotmachines." + uniqueId + ".prizes." + prizeId + ".displayName");
                    List<String> commands = configuration.getConfiguration().getStringList("slotmachines." + uniqueId + ".prizes." + prizeId + ".commands");
                    System.out.println("Adding new prize: " + prizeId + " - " + prizeDisplayName + " there are a total of " + commands.size() + " commands for this prize.");
                    slotMachine.addWinItem(new SlotMachineItem(prizeDisplayName, commands));
                }

                // Gui
                slotMachine.setGuiEnabled(configuration.getConfiguration().getBoolean("slotmachines." + slotMachine.getUniqueId() + ".guiEnabled"));

                // Currency
                slotMachine.coinCostPerUse = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".coins.costPerUse");
                slotMachine.vaultCostPerUse = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".vault.costPerUse");

                // Hologram
                slotMachine.hologramEnabled = configuration.getConfiguration().getBoolean("slotmachines." + slotMachine.getUniqueId() + ".hologramEnabled");
                slotMachine.hologramText = configuration.getConfiguration().getString("slotmachines." + slotMachine.getUniqueId() + ".hologramText");

                // Speeds
                slotMachine.speeds = configuration.getConfiguration().getStringList("slotmachines." + slotMachine.getUniqueId() + ".speeds");

                // Particles
                slotMachine.particlesEnabled = configuration.getConfiguration().getBoolean("slotmachines." + slotMachine.getUniqueId() + ".particles.particlesEnabled");
                slotMachine.runningEffect = Effect.valueOf(configuration.getConfiguration().getString("slotmachines." + slotMachine.getUniqueId() + ".particles.runningEffect"));
                slotMachine.finishedEffect = Effect.valueOf(configuration.getConfiguration().getString("slotmachines." + slotMachine.getUniqueId() + ".particles.finishedEffect"));
                slotMachine.particleSpeed = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".particles.speed");
                slotMachine.particleAmount = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".particles.amount");
                slotMachine.particleRange = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".particles.range");
                slotMachine.particleOffsetX = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".particles.offsetX");
                slotMachine.particleOffsetY = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".particles.offsetY");
                slotMachine.particleOffsetZ = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".particles.offsetZ");

                // Sounds
                slotMachine.playingSoundInterval = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".sounds.playing.playInterval");
                slotMachine.playingSound = Sound.valueOf(configuration.getConfiguration().getString("slotmachines." + slotMachine.getUniqueId() + ".sounds.playing.soundEffect"));
                slotMachine.lockedSound = Sound.valueOf(configuration.getConfiguration().getString("slotmachines." + slotMachine.getUniqueId() + ".sounds.iconSelected.soundEffect"));
                slotMachine.playingSoundEnabled = configuration.getConfiguration().getBoolean("slotmachines." + slotMachine.getUniqueId() + ".sounds.playing.soundEffectEnabled");
                slotMachine.lockedSoundEnabled = configuration.getConfiguration().getBoolean("slotmachines." + slotMachine.getUniqueId() + ".sounds.iconSelected.soundEffect");
                slotMachine.playingSoundVolume = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".sounds.playing.soundEffectVolume");
                slotMachine.lockedSoundVolume = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".sounds.iconSelected.soundEffectVolume");
                slotMachine.playingSoundPitch = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".sounds.playing.soundEffectPitch");
                slotMachine.lockedSoundPitch = configuration.getConfiguration().getInt("slotmachines." + slotMachine.getUniqueId() + ".sounds.iconSelected.soundEffectPitch");

                loadedSlotMachines.add(slotMachine);
                tempLocations.add(LocationUtil.getStringFromLocation(slotMachine.getBaseLocation()));
            }

            try {
                configuration.getTemporaryMachineCacheConfig().set("tempslotmachines", tempLocations);
                configuration.getTemporaryMachineCacheConfig().save(configuration.getTemporaryMachineCache());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        loadedSlotMachines.forEach(slotMachine -> {
            slotMachine.spawnBase();
            slotMachine.spawnWheels();
        });

        CasinoPlugin.getInstance().getCommandManager().registerCommand(new DebugCommand());
        Bukkit.getPluginManager().registerEvents(new CasinoSlotListener(), CasinoPlugin.getInstance());

        System.out.println("Casino Slot Machines enabled!");
    }

    @Override
    public void onGameDisable() {

        Bukkit.getWorlds().forEach(world -> {
            world.getEntitiesByClass(ArmorStand.class).forEach(stand -> {
                if (stand.hasMetadata("casinoSlotBase") || stand.hasMetadata("casinoSlotID") || stand.hasMetadata("casinoSlotHandle") || stand.hasMetadata("special")) {
                    stand.remove();
                }
            });
        });

        // Wipe all stands then wipe slot machines to stop memory leaks.
        loadedSlotMachines.clear();
        System.out.println("Casino Slot Machines disabled!");
    }

    @Override
    public String getGameName() {
        return "Slot Machine";
    }

    @Override
    public double getGameVersion() {
        return 1.0;
    }

    @Override
    public String getGameAuthor() {
        return "ThatAbstractWolf";
    }

    public SlotMachine getSlotMachineFromEntity(Entity entity) {
        if (entity.hasMetadata("casinoClickable")) return ((SlotMachine) entity.getMetadata("casinoClickable").get(0).value());
        return (entity.hasMetadata("casinoSlotBase") ?  (SlotMachine) entity.getMetadata("casinoSlotBase").get(0).value() : null);
    }

    public SlotMachineConfiguration getConfiguration() {
        return configuration;
    }

    public SlotMachineRewardConfiguration getRewardConfiguration() {
        return rewardConfiguration;
    }

    public List<SlotMachine> getLoadedSlotMachines() {
        return loadedSlotMachines;
    }
}
