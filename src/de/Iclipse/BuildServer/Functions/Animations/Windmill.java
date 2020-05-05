package de.Iclipse.BuildServer.Functions.Animations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Random;

public class Windmill {
    public static Location animation0 = new Location(Bukkit.getWorld("IMLobby_world"), -34, 2, -3);
    public static Location animation1 = new Location(Bukkit.getWorld("IMLobby_world"), -29, 2, -3);
    public static Location animation2 = new Location(Bukkit.getWorld("IMLobby_world"), -24, 2, -3);
    public static Location animation3 = new Location(Bukkit.getWorld("IMLobby_world"), -19, 2, -3);
    public static int height = 15;
    public static int width = 15;
    public static int length = 2;
    public static HashMap<Location, Integer> windmills;

    public static void windmill() {
        windmills.forEach((loc, animation) -> {
            Location animationLoc;
            switch (animation) {
                case 0:
                    animationLoc = animation0;
                    break;
                case 1:
                    animationLoc = animation1;
                    break;
                case 2:
                    animationLoc = animation2;
                    break;
                default:
                    animationLoc = animation3;
            }
            for (int x = 0; x < length; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < width; z++) {
                        Block change;
                        change = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                        Block animationBlock = animationLoc.getWorld().getBlockAt(animationLoc.getBlockX() + x, animationLoc.getBlockY() + y, animationLoc.getBlockZ() + z);
                        if (change.getType() != animationBlock.getType()) {
                            change.setType(animationBlock.getType());
                        }
                    }
                }
            }
            windmills.replace(loc, (animation + 1) % 4);
        });
    }

    public static void createWindmills() {
        Random random = new Random();
        windmills = new HashMap<>();
        windmills.put(new Location(Bukkit.getWorld("IMLobby_world"), -7, 57, -71), random.nextInt(4));

    }
}
