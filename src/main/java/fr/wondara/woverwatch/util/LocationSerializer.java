package fr.wondara.woverwatch.util;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {

    public static String serializeLocation(Location location) {
        StringBuilder string = new StringBuilder();
        if (location == null)
            return string.append("none").toString();
        string.append(location.getX()).append(";");
        string.append(location.getY()).append(";");
        string.append(location.getZ()).append(";");
        string.append(location.getYaw()).append(";");
        string.append(location.getPitch()).append(";");
        return string.toString();
    }

    public static Location deserializeLocation(String string, World world) {
        if (string.equals("none"))
            return null;
        String[] split = string.split(";");
        return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                Float.parseFloat(split[3]), Float.parseFloat(split[4]));
    }
}
