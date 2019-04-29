package net.casino.casino.defaultgames.slots;

import net.casino.CasinoPlugin;
import net.casino.casino.defaultgames.slots.slot.SlotMachineItem;
import net.casino.casino.defaultgames.slots.slot.SlotWheelIcon;
import net.casino.casino.defaultgames.slots.slot.SlotWheelPosition;
import net.casino.casino.defaultgames.slots.slot.WinType;
import net.casino.utils.AngleUtil;
import net.casino.utils.CustomChatMessage;
import net.casino.utils.itemstack.ItemFactory;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlotMachine {

    private final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    private int uniqueId;

    private final Location baseLocation;
    private BukkitTask runnable;

    private ArmorStand baseStand;
    private ArmorStand baseHandle;
    private ArmorStand[] wheelStands;

    private ArmorStand hologramStand;

    private long[] timeLeft;
    private int timeLeftHandle;

    private Player currentlyUsing;
    private boolean inProgress;
    private Random random;

    private long lastPingPlaying = System.currentTimeMillis();

    // Individual settings
    private boolean guiEnabled;

    // Currency
    public int coinCostPerUse;
    public int vaultCostPerUse;

    // Hologram
    public boolean hologramEnabled;
    public String hologramText;

    // Particles
    public boolean particlesEnabled;
    public Effect runningEffect, finishedEffect;
    public int particleSpeed, particleAmount, particleRange;
    public int particleOffsetX, particleOffsetY, particleOffsetZ;

    // Sounds
    public Sound playingSound, lockedSound;
    public int playingSoundInterval;
    public boolean playingSoundEnabled, lockedSoundEnabled;
    public double playingSoundVolume, playingSoundPitch, lockedSoundVolume, lockedSoundPitch;

    // Speeds
    public List<String> speeds;

    private List<SlotMachineItem> winItems;

    public SlotMachine(int uniqueId, Location baseLocation) {
        this.uniqueId = uniqueId;
        this.baseLocation = new Location(baseLocation.getWorld(), baseLocation.getBlockX(), baseLocation.getBlockY(), baseLocation.getBlockZ(), getNearestYaw(baseLocation.getYaw()), baseLocation.getPitch());
        this.wheelStands = new ArmorStand[3];
        this.winItems = new ArrayList<>();
        this.random = new Random();
        this.speeds = new ArrayList<>();
        this.inProgress = false;
    }

    public Location getBaseLocation() {
        return baseLocation;
    }

    public void addWinItem(SlotMachineItem item) { winItems.add(item); }

    public List<SlotMachineItem> getWinItems() {
        return winItems;
    }

    public void setCurrentlyUsing(Player currentlyUsing) {
        this.currentlyUsing = currentlyUsing;
    }

    public Player getCurrentlyUsing() {
        return currentlyUsing;
    }

    public void spawnBase() {

        baseStand = (ArmorStand) baseLocation.getWorld().spawnEntity(baseLocation, EntityType.ARMOR_STAND);
        baseStand.setCustomNameVisible(false);
        baseStand.setRemoveWhenFarAway(false);
        baseStand.setHelmet(new ItemFactory(Material.valueOf(CasinoPlugin.getInstance().getConfig().getString("slots.items.base.item"))).setUnbreakable().setDurability((short) CasinoPlugin.getInstance().getConfig().getInt("slots.items.base.durability")).build());
        baseStand.setCollidable(false);
        baseStand.setSilent(true);
        baseStand.setGravity(false);
        baseStand.setVisible(false);

        baseStand.setMetadata("casinoSlotBase", new FixedMetadataValue(CasinoPlugin.getInstance(), this));
        baseStand.setMetadata("casinoClickable", new FixedMetadataValue(CasinoPlugin.getInstance(), this));

        if (hologramEnabled) {
            hologramStand = (ArmorStand) baseLocation.getWorld().spawnEntity(baseLocation.clone().add(0, 1, 0), EntityType.ARMOR_STAND);
            hologramStand.setCustomName(ChatColor.translateAlternateColorCodes('&', hologramText));
            hologramStand.setCustomNameVisible(hologramEnabled);
            hologramStand.setRemoveWhenFarAway(false);
            hologramStand.setCollidable(false);
            hologramStand.setSilent(true);
            hologramStand.setGravity(false);
            hologramStand.setVisible(false);

            hologramStand.setMetadata("casinoSlotBase", new FixedMetadataValue(CasinoPlugin.getInstance(), this));
            hologramStand.setMetadata("casinoClickable", new FixedMetadataValue(CasinoPlugin.getInstance(), this));
        }

        float yaw = getNearestYaw(baseLocation.clone().getYaw());

        baseHandle = (ArmorStand) baseLocation.getWorld().spawnEntity((yaw == 90 || yaw == -90 ? this.getNextOffset(this.baseLocation.clone(), -1.068, true).subtract(0, 0.20, 0) : this.getNextOffset(this.baseLocation.clone(), 1.068, true).subtract(0, 0.20, 0)), EntityType.ARMOR_STAND);
        baseHandle.setCustomName(ChatColor.translateAlternateColorCodes('&', "&7Base Handle"));
        baseHandle.setCustomNameVisible(false);
        baseHandle.setRemoveWhenFarAway(false);
        baseHandle.setHelmet(new ItemFactory(Material.valueOf(CasinoPlugin.getInstance().getConfig().getString("slots.items.handle.item"))).setUnbreakable().setDurability((short) CasinoPlugin.getInstance().getConfig().getInt("slots.items.handle.durability")).build());
        baseHandle.setCollidable(false);
        baseHandle.setSilent(true);
        baseHandle.setGravity(false);
        baseHandle.setVisible(false);
        baseHandle.setGlowing(false); // For development positioning

        baseHandle.setHeadPose(new EulerAngle(0, 0, 0));

        baseHandle.setMetadata("casinoSlotHandle", new FixedMetadataValue(CasinoPlugin.getInstance(), this));
        baseStand.setMetadata("casinoClickable", new FixedMetadataValue(CasinoPlugin.getInstance(), this));
    }

    public void spawnWheels() {

        // Spawns 3 armor stands.
        for (int i = 0; i < 3; i++) {
            Location location = new Location(baseLocation.getWorld(), baseLocation.getBlockX(), baseLocation.getBlockY(), baseLocation.getBlockZ());

            switch (i) {
                case 0: /* Left */
                    location = this.getNextOffset(this.baseLocation.clone(), -0.475, true).add(0, 0.3, 0);
                    break;
                case 1: /* Centre */
                    location = this.getNextOffset(this.baseLocation.clone(), 0, true).add(0, 0.3, 0);
                    break;
                case 2: /* Right */
                    location = this.getNextOffset(this.baseLocation.clone(), 0.475, true).add(0, 0.3, 0);
                    break;
            }

            ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&7Stand " + (i + 1)));
            stand.setCustomNameVisible(false);
            stand.setHeadPose(new EulerAngle(AngleUtil.getRadiansFromDegrees(random.nextInt(360)), 0, 0));
            stand.setHelmet(new ItemFactory(Material.valueOf(CasinoPlugin.getInstance().getConfig().getString("slots.items.wheels.item"))).setUnbreakable().setDurability((short) CasinoPlugin.getInstance().getConfig().getInt("slots.items.wheels.durability")).build());
            stand.setRemoveWhenFarAway(false);
            stand.setVisible(false);
            stand.setCollidable(false);
            stand.setSilent(true);
            stand.setGravity(false);
            stand.setMetadata("casinoSlotID", new FixedMetadataValue(CasinoPlugin.getInstance(), i));
            stand.setMetadata("casinoClickable", new FixedMetadataValue(CasinoPlugin.getInstance(), this));
            wheelStands[i] = stand;
        }
    }

    public void despawnWheels() {
        for (ArmorStand wheelStand : wheelStands) {
            wheelStand.remove();
        }
        wheelStands = new ArmorStand[3];
    }

    /**
     * Beginds rolling animation.
     */
    public void beginRolling() {

        if (inProgress) {
            return;
        }

        if (runnable != null) {
            cancelRolling(false);
        }

        inProgress = true;

        timeLeft = getArrayBasedOnDirection(baseLocation.getYaw());
        timeLeftHandle = 25;

        int playInterval = CasinoPlugin.getInstance().getConfig().getInt("slots.sounds.playing.playInterval");

        // Handles
        runnable = new BukkitRunnable() {

            @Override
            public void run() {
                // TODO rolling effect

                if (timeLeftHandle >= 0) {
                    if (timeLeftHandle > 12) {
                        baseHandle.setHeadPose(baseHandle.getHeadPose().add(AngleUtil.getRadiansFromDegrees(35 * 0.20), 0, 0));
                    } else {
                        baseHandle.setHeadPose(baseHandle.getHeadPose().add(-AngleUtil.getRadiansFromDegrees(35 * 0.20), 0, 0));
                    }

                    timeLeftHandle -= 1;
                }

                if (playingSoundEnabled && System.currentTimeMillis() >= lastPingPlaying) {
                    baseLocation.getWorld().playSound(baseLocation, playingSound, (float) playingSoundVolume, (float) playingSoundPitch);
                    lastPingPlaying = System.currentTimeMillis() + (1000 * playInterval);
                }

                if (particlesEnabled) {
                    for (ArmorStand wheelStand : wheelStands) {
                        if (particlesEnabled) {
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                player.spigot().playEffect(wheelStand.getLocation(), runningEffect,  0, 0, (float) particleOffsetX, (float) particleOffsetY, (float) particleOffsetZ, particleSpeed, particleAmount, particleRange);
                            });
                        }
                    }
                }

                if (timeLeft[0] < 0 && timeLeft[2] < 0) {

                    List<SlotWheelIcon> icons = new ArrayList<>();

                    // CALCULATE ITEMS
                    for (int index = 0; index < wheelStands.length; index++) {
                        ArmorStand wheelStand = wheelStands[index];
                        SlotWheelPosition position = SlotWheelPosition.getIcoPosition(wheelStand);
                        if (position == null) continue;
                        icons.add(position.getSlotWheelIcon());
                    }

                    SlotWheelIcon icon1 = icons.get(0), icon2 = icons.get(1), icon3 = icons.get(2);
                    WinType winType = getWinType(icon1, icon2, icon3);

                    if (getWinItems().size() > 0) {
                        SlotMachineItem wonItem = getWinItems().get(random.nextInt(getWinItems().size()));

                        switch (winType) {
                            case THREE: // $500 + random prize

                                if (CasinoPlugin.getInstance().getConfig().getBoolean("slots.threeicon.randomrewards")) {
                                    // random prize
                                    CustomChatMessage.sendMessage(currentlyUsing, CasinoPlugin.getInstance().getMessageManager().getMessage("wonItem3").replace("<itemWon>", wonItem.getDisplayName()));
                                    wonItem.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", currentlyUsing.getName())));
                                }

                                CasinoPlugin.getInstance().getConfig().getStringList("slots.threeicon.commands").forEach(command -> {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", currentlyUsing.getName()));
                                });

                                baseLocation.getWorld().playSound(baseLocation, Sound.BLOCK_GLASS_BREAK, 1F, 1F);

                                if ((CasinoPlugin.getInstance().getConfig().getBoolean("slots.threeicon.fireworkEnabled"))) {
                                    Firework firework = (Firework) baseStand.getWorld().spawnEntity(baseStand.getLocation(), EntityType.FIREWORK);
                                    FireworkMeta meta = firework.getFireworkMeta();
                                    meta.addEffect(FireworkEffect.builder()
                                            .trail(true)
                                            .flicker(true)
                                            .withColor(Color.RED)
                                            .with(FireworkEffect.Type.BALL)
                                            .build());

                                    firework.setFireworkMeta(meta);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            firework.detonate();
                                        }
                                    }.runTaskLater(CasinoPlugin.getInstance(), 10);
                                }
                                break;
                            case TWO:

                                if (CasinoPlugin.getInstance().getConfig().getBoolean("slots.twoicon.randomrewards")) {
                                    // random prize
                                    CustomChatMessage.sendMessage(currentlyUsing, CasinoPlugin.getInstance().getMessageManager().getMessage("wonItem2").replace("<itemWon>", wonItem.getDisplayName()));
                                    wonItem.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", currentlyUsing.getName())));
                                }

                                CasinoPlugin.getInstance().getConfig().getStringList("slots.twoicon.commands").forEach(command -> {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", currentlyUsing.getName()));
                                });

                                baseLocation.getWorld().playSound(baseLocation, Sound.BLOCK_GLASS_BREAK, 1F, 1F);

                                if ((CasinoPlugin.getInstance().getConfig().getBoolean("slots.twoicon.fireworkEnabled"))) {
                                    Firework firework = (Firework) baseStand.getWorld().spawnEntity(baseStand.getLocation(), EntityType.FIREWORK);
                                    FireworkMeta meta = firework.getFireworkMeta();
                                    meta.addEffect(FireworkEffect.builder()
                                            .trail(true)
                                            .flicker(true)
                                            .withColor(Color.RED)
                                            .with(FireworkEffect.Type.BALL)
                                            .build());

                                    firework.setFireworkMeta(meta);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            firework.detonate();
                                        }
                                    }.runTaskLater(CasinoPlugin.getInstance(), 10);
                                }
                                break;
                            case ONE:

                                if (CasinoPlugin.getInstance().getConfig().getBoolean("slots.oneicon.randomrewards")) {
                                    // random prize
                                    CustomChatMessage.sendMessage(currentlyUsing, CasinoPlugin.getInstance().getMessageManager().getMessage("wonItem1").replace("<itemWon>", wonItem.getDisplayName()));
                                    wonItem.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", currentlyUsing.getName())));
                                }

                                CasinoPlugin.getInstance().getConfig().getStringList("slots.oneicon.commands").forEach(command -> {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", currentlyUsing.getName()));
                                });


                                baseLocation.getWorld().playSound(baseLocation, Sound.BLOCK_GLASS_BREAK, 1F, 1F);

                                if ((CasinoPlugin.getInstance().getConfig().getBoolean("slots.oneicon.fireworkEnabled"))) {
                                    Firework firework = (Firework) baseStand.getWorld().spawnEntity(baseStand.getLocation(), EntityType.FIREWORK);
                                    FireworkMeta meta = firework.getFireworkMeta();
                                    meta.addEffect(FireworkEffect.builder()
                                            .trail(true)
                                            .flicker(true)
                                            .withColor(Color.RED)
                                            .with(FireworkEffect.Type.BALL)
                                            .build());

                                    firework.setFireworkMeta(meta);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            firework.detonate();
                                        }
                                    }.runTaskLater(CasinoPlugin.getInstance(), 10);
                                }
                                break;
                        }
                    } else {
                        CustomChatMessage.sendMessage(currentlyUsing, "There is an error, please configure the plugin!");
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            baseHandle.setHeadPose(new EulerAngle(0, 0, 0));
//                            despawnWheels();
//                            spawnWheels();
                            currentlyUsing = null;
                            inProgress = false;
                        }
                    }.runTaskLater(CasinoPlugin.getInstance(), 4 * 20);
                    cancelRolling(false);
                    return;
                }

                double[] timesBy = new double[] {0.4, 0.4, 0.4};

                for (int i = 0; i < wheelStands.length; i++) {
                    ArmorStand stand = wheelStands[i];
                    if (timeLeft[i] > -1) {
                        if(timeLeft[i] == 1){
                            double amount = AngleUtil.getRadiansFromDegrees(Math.abs((Math.ceil(AngleUtil.getDegreesFromRadians(stand.getHeadPose().getX()) / 45) * 45) - AngleUtil.getDegreesFromRadians(stand.getHeadPose().getX())));
                            double addSum = amount / 12;

                            new BukkitRunnable(){
                                double total = 0;
                                @Override
                                public void run() {

                                    if (particlesEnabled) {
                                        Bukkit.getOnlinePlayers().forEach(player -> {
                                            player.spigot().playEffect(stand.getLocation(), finishedEffect,  0, 0, (float) particleOffsetX, (float) particleOffsetY, (float) particleOffsetZ, particleSpeed, particleAmount, particleRange);
                                        });
                                    }

                                    if (lockedSoundEnabled) {
                                        baseLocation.getWorld().playSound(baseLocation, lockedSound,(float) lockedSoundVolume, (float) lockedSoundPitch);
                                    }

                                    stand.setHeadPose(stand.getHeadPose().add(addSum, 0, 0));
                                    total += addSum;
                                    if(total >= amount){
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(CasinoPlugin.getInstance(), 0L, 1L);
                        } else {
                            stand.setHeadPose(stand.getHeadPose().add(AngleUtil.getRadiansFromDegrees((timeLeft[i] * timesBy[i])), 0, 0));
                        }
                        timeLeft[i] -= 1;
                    }
                }
            }
        }.runTaskTimer(CasinoPlugin.getInstance(), 0L, 1L);
    }

    public void cancelRolling(boolean cancelProgress) {
        if (runnable != null) {
            runnable.cancel();
            runnable = null;
        }

        if (cancelProgress) {
            inProgress = false;
        }
    }

    /**
     * Used for on shutdown.
     */
    public void despawnAllStands() {
        baseStand.remove();
        baseHandle.remove();
        hologramStand.remove();
        despawnWheels();
        baseStand = null;
        baseHandle = null;
        hologramStand = null;
    }

    public boolean inProgress() { return inProgress; }

    private Location getNextOffset(Location current, double offset, boolean b) {
        double yaw = Math.toRadians(current.getYaw()) + (Math.PI / 2);
        double x = current.getX() + offset * (b ? Math.sin(yaw) : Math.cos(yaw));
        double z = current.getZ() + offset * (b ? Math.cos(yaw) : Math.sin(yaw));
        return new Location(current.getWorld(), x, current.getY(), z, getNearestYaw(current.getYaw()), current.getPitch());
    }

    private float getNearestYaw(float yaw) {
        BlockFace face = axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();

        switch (face) {
            case SOUTH:
                return 0;
            case WEST:
                return 90;
            case NORTH:
                return -180;
            case EAST:
                return -90;
        }

        return 0;
    }

    private long[] getArrayBasedOnDirection(float yaw) {
        BlockFace face = axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();

//        long[] longValues = new long[] { 300, 200, 100 };
        long[] longValues = new long[] { Integer.parseInt(speeds.get(0)), Integer.parseInt(speeds.get(1)), Integer.parseInt(speeds.get(2)) };

        switch (face) {
            case EAST:
                return longValues;
            case WEST:
                return longValues;
            case NORTH:
                return reverse(longValues);
            case SOUTH:
                return reverse(longValues);
        }

        return longValues;
    }

    private WinType getWinType(SlotWheelIcon icon1, SlotWheelIcon icon2, SlotWheelIcon icon3) {
        if (icon1 == icon2 && icon1 == icon3) { // 3 in a row
            return WinType.THREE;
        } else if (icon1 == icon2 || icon1 == icon3 || icon2 == icon3) {
            return WinType.TWO;
        }
        return WinType.ONE;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    private long[] reverse(long[] values) {
        long[] temp = values;
        ArrayUtils.reverse(temp);
        return temp;
    }

    public void setGuiEnabled(boolean guiEnabled) {
        this.guiEnabled = guiEnabled;
    }

    public boolean isGuiEnabled() {
        return guiEnabled;
    }
}
