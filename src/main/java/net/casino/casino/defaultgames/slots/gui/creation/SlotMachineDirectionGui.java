package net.casino.casino.defaultgames.slots.gui.creation;

import net.casino.CasinoPlugin;
import net.casino.casino.defaultgames.slots.SlotMachine;
import net.casino.casino.defaultgames.slots.SlotMachineGame;
import net.casino.casino.defaultgames.slots.slot.SlotMachineItem;
import net.casino.utils.CustomChatMessage;
import net.casino.utils.LocationUtil;
import net.casino.utils.gui.MenuFactory;
import net.casino.utils.gui.MenuItem;
import net.casino.utils.itemstack.ItemFactory;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotMachineDirectionGui extends MenuFactory {

    public SlotMachineDirectionGui(Player player, Location location) {
        super("Direction Selector", 3);

        int position = 10;

        for (int i = 0; i < 4; i++) {

            String displayName = "";

            float yaw = 0;

            switch (i) {
                case 0:
                    displayName = "North";
                    yaw = 180;
                    break;
                case 1:
                    displayName = "East";
                    yaw = -90;
                    break;
                case 2:
                    displayName = "South";
                    yaw = 0;
                    break;
                case 3:
                    displayName = "West";
                    yaw = 90;
                    break;
            }

            final String finalDisplayName = displayName;
            final float finalYaw = yaw;

            addItem(new MenuItem(position, new ItemFactory(Material.BOOK)
                    .setDisplayName("&b&l" + displayName)
                    .setLore("", "&7Slot Machine will face " + displayName, "", "&aClick to confirm positioning.")
                .build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    location.setYaw(finalYaw);
                    SlotMachineGame game = ((SlotMachineGame) CasinoPlugin.getInstance().getCasinoManager().getCasinoGame(SlotMachineGame.class));
                    SlotMachine machine = new SlotMachine(game.getLoadedSlotMachines().size() + 1, location);

                    setupDefaultStand(game, machine);

                    machine.spawnBase();
                    machine.spawnWheels();

                    game.getLoadedSlotMachines().add(machine);
                    CustomChatMessage.sendMessage(player, "&aSlot Machine spawned! This is Slot Machine " + game.getLoadedSlotMachines().size());
                    CustomChatMessage.sendMessage(player, "&aYour Slot Machine is facing &b" + finalDisplayName);

                    List<String> tempLocations = (game.getConfiguration().getTemporaryMachineCacheConfig().getStringList("tempslotmachines") == null ? new ArrayList<>() : game.getConfiguration().getTemporaryMachineCacheConfig().getStringList("tempslotmachines"));
                    tempLocations.add(LocationUtil.getStringFromLocation(machine.getBaseLocation()));

                    try {
                        game.getConfiguration().getTemporaryMachineCacheConfig().set("tempslotmachines", tempLocations);
                        game.getConfiguration().getTemporaryMachineCacheConfig().save(game.getConfiguration().getTemporaryMachineCache());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            position += 2;
        }

        openInventory(player);
    }

    private void setupDefaultStand(SlotMachineGame game, SlotMachine machine) {

        // Location
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".location", LocationUtil.getStringFromLocation(machine.getBaseLocation()));

        // Rewards
        for (String rewardId : game.getRewardConfiguration().getConfiguration().getConfigurationSection("rewards").getKeys(false)) {
            String prizeDisplayName = game.getRewardConfiguration().getConfiguration().getString("rewards." + rewardId + ".displayName");
            List<String> prizeCommands = game.getRewardConfiguration().getConfiguration().getStringList("rewards." + rewardId + ".commands");

            game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".prizes." + rewardId + ".displayName", prizeDisplayName);
            game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".prizes." + rewardId + ".commands", prizeCommands);
            machine.addWinItem(new SlotMachineItem(prizeDisplayName, prizeCommands));
        }

        // Currency
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".coins.costPerUse", 1);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".vault.costPerUse", 100);
        machine.coinCostPerUse = 1;
        machine.vaultCostPerUse = 100;

        // Hologram
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".hologramEnabled", true);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".hologramText", "&b&lCasino Slot Machine");
        machine.hologramEnabled = true;
        machine.hologramText = "&b&lCasino Slot Machine";

        // Gui
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".guiEnabled", true);
        machine.setGuiEnabled(true);

        // Speeds
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".speeds", Arrays.asList("300", "200", "100"));
        machine.speeds = Arrays.asList("300", "200", "100");

        // Particles
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.particlesEnabled", true);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.runningEffect", "FLAME");
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.finishedEffect", "CLOUD");
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.speed", 20);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.amount", 35);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.range", 150);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.offsetX", 0);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.offsetY", 0);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".particles.offsetZ", 0);
        machine.particlesEnabled = true;
        machine.runningEffect = Effect.FLAME;
        machine.finishedEffect = Effect.CLOUD;
        machine.particleSpeed = 20;
        machine.particleAmount = 35;
        machine.particleRange = 150;
        machine.particleOffsetX = 0;
        machine.particleOffsetY = 0;
        machine.particleOffsetZ = 0;

        // Sounds (playing)
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.playing.playInterval", 2);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.playing.soundEffectEnabled", true);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.playing.soundEffect", "BLOCK_NOTE_PLING");
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.playing.soundEffectVolume", 0.5);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.playing.soundEffectPitch", 1);
        machine.playingSoundInterval = 2;
        machine.playingSoundEnabled = true;
        machine.playingSound = Sound.BLOCK_NOTE_PLING;
        machine.playingSoundVolume = 0.5;
        machine.playingSoundPitch = 1;

        // Sounds (icon selected)
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.iconSelected.soundEffectEnabled", true);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.iconSelected.soundEffect", "BLOCK_NOTE_PLING");
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.iconSelected.soundEffectVolume", 0.5);
        game.getConfiguration().getConfiguration().set("slotmachines." + (machine.getUniqueId()) + ".sounds.iconSelected.soundEffectPitch", 1);
        machine.lockedSoundEnabled = true;
        machine.lockedSound = Sound.BLOCK_NOTE_PLING;
        machine.lockedSoundVolume = 0.5;
        machine.lockedSoundPitch = 1;

        game.getConfiguration().save();
    }
}
