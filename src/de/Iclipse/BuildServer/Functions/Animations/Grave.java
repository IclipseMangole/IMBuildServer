package de.Iclipse.BuildServer.Functions.Animations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Random;

public class Grave {
    public static Location animation0 = new Location(Bukkit.getWorld("IMLobby_world"), -34, 2, -22);
    public static Location animation1 = new Location(Bukkit.getWorld("IMLobby_world"), -29, 2, -22);
    public static Location animation2 = new Location(Bukkit.getWorld("IMLobby_world"), -24, 2, -22);
    public static Location animation3 = new Location(Bukkit.getWorld("IMLobby_world"), -19, 2, -22);
    public static Location animation4 = new Location(Bukkit.getWorld("IMLobby_world"), -14, 2, -22);
    public static int height = 6;
    public static int width = 3;
    public static int length = 5;
    public static HashMap<Location, Integer> graves;

    public static void grave() {
        graves.forEach((loc, animation) -> {
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
                case 3:
                    animationLoc = animation3;
                    break;
                case 4:
                    animationLoc = animation4;
                    break;
                case 5:
                    animationLoc = animation3;
                    break;
                case 6:
                    animationLoc = animation2;
                    break;
                case 7:
                    animationLoc = animation1;
                    break;
                default:
                    animationLoc = animation0;

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
            graves.replace(loc, (animation + 1) % 9);
        });
    }

    public static void createGraves() {
        Random random = new Random();
        graves = new HashMap<>();
        graves.put(new Location(Bukkit.getWorld("IMLobby_world"), -36, 52, -138), random.nextInt(5));

    }
}
