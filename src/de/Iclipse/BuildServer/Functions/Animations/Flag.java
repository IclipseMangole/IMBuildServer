package de.Iclipse.BuildServer.Functions.Animations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Random;

public class Flag {
    public static Location animation0 = new Location(Bukkit.getWorld("world"), -34, 2, -17);
    public static Location animation1 = new Location(Bukkit.getWorld("world"), -29, 2, -17);
    public static Location animation2 = new Location(Bukkit.getWorld("world"), -24, 2, -17);
    public static int height = 3;
    public static int width = 11;
    public static int length = 5;
    public static HashMap<Location, Integer> flags;

    public static void flag() {
        flags.forEach((loc, animation) -> {
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
            flags.replace(loc, (animation + 1) % 3);
        });
    }

    public static void createFlags() {
        Random random = new Random();
        flags = new HashMap<>();
        flags.put(new Location(Bukkit.getWorld("world"), 9, 74, -125), random.nextInt(3));
        flags.put(new Location(Bukkit.getWorld("world"), 9, 74, -108), random.nextInt(3));
    }
}
