package de.Iclipse.BuildServer.Functions.Animations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Random;

public class Vent {
    public static Location animation0 = new Location(Bukkit.getWorld("world"), -5, 2, 14);
    public static Location animation1 = new Location(Bukkit.getWorld("world"), 4, 2, 14);
    public static Location animation2 = new Location(Bukkit.getWorld("world"), 11, 2, 14);
    public static int height = 6;
    public static int width = 8;
    public static int length = 6;
    public static HashMap<Location, Integer> vents;

    /*
    public static Location animation0 = new Location(Bukkit.getWorld("world"), -34, 2, 14);
    public static Location animation1 = new Location(Bukkit.getWorld("world"), -29, 2, 14);
    public static Location animation2 = new Location(Bukkit.getWorld("world"), -24, 2, 14);
    public static int height = 3;
    public static int width = 5;
    public static int length = 4;
    public static HashMap<Location, Integer> vents;
     */

    public static void vent() {
        vents.forEach((loc, animation) -> {
            Location animationLoc;
            switch (animation) {
                case 0:
                    animationLoc = animation0;
                    break;
                case 1:
                    animationLoc = animation1;
                    break;
                default:
                    animationLoc = animation2;
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
            vents.replace(loc, (animation + 1) % 3);
        });
    }

    public static void createVents() {
        Random random = new Random();
        vents = new HashMap<>();
        vents.put(new Location(Bukkit.getWorld("world"), 21, 61, -74), random.nextInt(3));
        vents.put(new Location(Bukkit.getWorld("world"), 9, 66, -86), random.nextInt(3));
        vents.put(new Location(Bukkit.getWorld("world"), 8, 61, -65), random.nextInt(3));
        vents.put(new Location(Bukkit.getWorld("world"), -40, 62, -153), random.nextInt(3));
    }


}
