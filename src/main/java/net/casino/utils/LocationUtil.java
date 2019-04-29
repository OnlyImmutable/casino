package net.casino.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static Location getLocationFromString(String location) {
        String[] locationParts = location.split(";");
        return new Location(Bukkit.getWorld(locationParts[0]), ParserUtil.parseInt(locationParts[1]).get(), ParserUtil.parseInt(locationParts[2]).get(), ParserUtil.parseInt(locationParts[3]).get(), ParserUtil.parseFloat(locationParts[4]).get(), ParserUtil.parseFloat(locationParts[5]).get());
    }

    public static String getStringFromLocation(Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }
}
