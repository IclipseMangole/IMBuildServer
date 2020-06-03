package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.BuildServer.Data;
import de.Iclipse.IMAPI.Util.Command.IMCommand;
import de.Iclipse.IMAPI.Util.executor.Callback;
import de.Iclipse.IMAPI.Util.executor.ThreadExecutor;
import net.minecraft.server.v1_15_R1.Block;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.MaterialMapColor;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.map.CraftMapView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class cmd_map {
    Callback task;

    @IMCommand(
            name = "map",
            maxArgs = 1,
            minArgs = 1,
            noConsole = true,
            permissions = "im.cmd.map"
    )
    public void map(Player p, byte scale) {
        if (p.getInventory().getItemInMainHand().getType().equals(Material.MAP)) {
            p.getInventory().setItemInMainHand(getMap(p, scale));
            task = ThreadExecutor.executeAsync(new Runnable() {
                @Override
                public void run() {
                    sendMapView(p, scale, p.getInventory().getItemInMainHand());
                }
            });
        }
        if (p.getInventory().getItemInOffHand().getType().equals(Material.MAP)) {
            p.getInventory().setItemInOffHand(getMap(p, scale));
            task = ThreadExecutor.executeAsync(new Runnable() {
                @Override
                public void run() {
                    sendMapView(p, scale, p.getInventory().getItemInOffHand());
                }
            });
        }
    }

    public ItemStack getMap(Player p, byte scale) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(getMapView(p, scale));
        //meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public MapView getMapView(Player p, byte scale) {
        CraftMapView view = (CraftMapView) Bukkit.createMap(p.getWorld());
        view.setUnlimitedTracking(true);
        view.setTrackingPosition(false);
        view.setCenterX(p.getLocation().getBlockX());
        view.setCenterZ(p.getLocation().getBlockY());
        view.setTrackingPosition(true);
        view.setScale(MapView.Scale.valueOf(scale));
        view.setLocked(true);
        return view;
    }

    public void sendMapView(Player p, byte scale, ItemStack item) {
        System.out.println(Instant.now().toString());
        int id = ((MapMeta) item.getItemMeta()).getMapView().getId();


        File f = new File(p.getWorld().getWorldFolder().getPath() + "/maps/map_" + p.getLocation().getBlockX() + "_" + p.getLocation().getBlockZ() + "_" + scale + ".txt");
        byte[] data;
        if (f.exists()) {
            data = loadData(f);
        } else {

            data = renderMap(new Location(p.getWorld(), 0, 100, 0), (byte) Math.pow(2, scale));
            saveData(data, f);
        }
        PacketPlayOutMap packet = new PacketPlayOutMap(id, scale, true, true, new ArrayList<>(), data, 0, 0, 128, 128);
        ((CraftPlayer) p).getHandle().playerConnection.networkManager.sendPacket(packet);
        System.out.println(Instant.now().toString());
    }

    public byte[] renderMap(Location middle, byte scaleBlocks) {
        byte[] data = new byte[128 * 128];
        Location upperLeftCorner = middle.subtract(128 * scaleBlocks / 2, 0.0, 128 * scaleBlocks / 2);
        System.out.println("BlockX: " + upperLeftCorner.getBlockX() + ", BlockZ: " + upperLeftCorner.getBlockZ());
        for (int mapZ = 0; mapZ < 128; mapZ++) {
            for (int mapX = 0; mapX < 128; mapX++) {


                HashMap<Byte, Integer> colors = getColor(upperLeftCorner, (byte) mapX, (byte) mapZ, scaleBlocks);
                final Byte[] mostOften = {null};
                final int[] biggestAmount = {0};
                colors.forEach((color, amount) -> {
                    if (mostOften[0] == null) {
                        mostOften[0] = color;
                        biggestAmount[0] = amount;
                    } else {
                        if (amount > biggestAmount[0]) {
                            mostOften[0] = color;
                        }
                    }
                });
                if (mostOften[0] <= 0) {
                    System.out.println("Most often: " + mostOften[0]);
                    colors.forEach((color, amount) -> {
                        System.out.println("Color: " + color + ", Amount: " + amount);
                    });
                }
                data[mapX + mapZ * 128] = (mostOften[0]);
                //System.out.println("Field:" + (mapZ));
                //System.out.println("Color: " + mostOften[0]);
            }
            //System.out.println("Row: " + (mapZ + 1));
        }
        return data;
    }


    public HashMap<Byte, Integer> getColor(Location corner, byte mapX, byte mapZ, byte size) {
        //Chunk chunk = new Location(corner.getWorld(), corner.getBlockX() + mapX * size, 100, corner.getBlockZ() + mapZ * size).getChunk();
        //chunk.setForceLoaded(true);
        //System.out.println("Chunk: [" + chunk.getX() + "|" + chunk.getZ() + "]");
        HashMap<Byte, Integer> blocks = new HashMap<>();
        for (int fieldX = 0; fieldX < size; fieldX++) {
            for (int fieldZ = 0; fieldZ < (int) size; fieldZ++) {
                Location loc = new Location(corner.getWorld(), corner.getBlockX() + (mapX * (int) size) + fieldX, 100, corner.getBlockZ() + (mapZ * size) + fieldZ);
                Block b = null;

                if (!loc.getChunk().isLoaded()) {
                    System.out.println("Not loaded: [" + loc.getChunk().getX() + "|" + loc.getChunk().getZ() + "]");
                    corner.getWorld().getChunkAt(loc).setForceLoaded(true);
                    Bukkit.getScheduler().runTaskLater(Data.instance, () -> loc.getChunk().setForceLoaded(false), 100);
                    while (!loc.getChunk().isLoaded()) {
                        System.out.println(b == null);
                    }
                }

                b = ((CraftBlock) getHighestBlock(loc)).getNMS().getBlock();

                //System.out.println(new Location(corner.getWorld(), corner.getBlockX() + (mapX * (int) size) + fieldX, 0.0, corner.getBlockZ() + (mapZ * size) + fieldZ));
                //MaterialMapColor materialMapColor = b.getBlockData().getMaterial().i();
                MaterialMapColor materialMapColor = b.e(b.getBlockData(), null, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                byte color;
                if (materialMapColor != null && materialMapColor.ac > 0) {
                    //System.out.println(materialMapColor.ac);
                    //System.out.println(materialMapColor.ac > 0);
                    if (materialMapColor.ac == 12) {
                        switch (loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                            case WARM_OCEAN:
                            case DEEP_WARM_OCEAN:
                            case BEACH:
                                color = (byte) (32 * 4 + 1);
                                break;
                            case LUKEWARM_OCEAN:
                            case DEEP_LUKEWARM_OCEAN:
                                color = (byte) (32 * 4 + 2);
                                break;
                            case OCEAN:
                            case DEEP_OCEAN:
                                color = (byte) 12 * 4 + 1;
                                break;
                            case COLD_OCEAN:
                            case DEEP_COLD_OCEAN:
                                color = (byte) 12 * 4 + 2;
                                break;
                            case FROZEN_OCEAN:
                            case DEEP_FROZEN_OCEAN:
                                color = (byte) 12 * 4 + 3;
                                break;
                            default:
                                color = 12 * 4;
                        }
                    } else {
                        color = (byte) ((materialMapColor.ac * 4) + (loc.getBlockY() % 2) * 1);
                    }
                } else {
                    if (loc.getBlock().getType().equals(Material.WATER) || loc.getBlock().getType().equals(Material.KELP_PLANT)) {
                        switch (loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                            case WARM_OCEAN:
                            case DEEP_WARM_OCEAN:
                            case BEACH:
                                color = (byte) (31 * 4 + 2);
                                break;
                            case LUKEWARM_OCEAN:
                            case DEEP_LUKEWARM_OCEAN:
                                color = (byte) (31 * 4 + 3);
                                break;
                            case OCEAN:
                            case DEEP_OCEAN:
                                color = (byte) 12 * 4 + 1;
                                break;
                            case COLD_OCEAN:
                            case DEEP_COLD_OCEAN:
                                color = (byte) 12 * 4 + 2;
                                break;
                            case FROZEN_OCEAN:
                            case DEEP_FROZEN_OCEAN:
                                color = (byte) 12 * 4 + 3;
                                break;
                            default:
                                color = 12 * 4;
                        }
                    } else if (b.getBlockData().getMaterial().equals(net.minecraft.server.v1_15_R1.Material.LAVA)) {
                        color = (byte) (net.minecraft.server.v1_15_R1.Material.LAVA.i().ac * 4);
                    } else {
                        color = 0;
                    }
                }
                //System.out.println("getColor: " + color);
                if (blocks.containsKey(color)) {
                    blocks.replace(color, (blocks.get(color) + 1));
                } else {
                    blocks.put(color, 1);
                }

            }
        }
        //chunk.setForceLoaded(false);
        return blocks;
    }

    public byte[] loadData(File f) {
        BufferedReader reader = null;
        byte[] data;
        try {
            reader = new BufferedReader(new FileReader(f));
            String[] line = reader.readLine().split(",");
            data = new byte[line.length];
            for (int i = 0; i < line.length; i++) {
                data[i] = Byte.parseByte(line[i]);
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveData(byte[] data, File f) {
        BufferedWriter out = null;
        try {
            if (!f.exists()) {
                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                f.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(f));
            String s = data[0] + "";
            for (int i = 1; i < data.length; i++) {
                s = s + "," + data[i];
            }
            out.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public org.bukkit.block.Block getHighestBlock(Location loc) {
        org.bukkit.block.Block highest = loc.getWorld().getHighestBlockAt(loc);
        Location overHighest = new Location(highest.getWorld(), highest.getX(), highest.getY() + 1, highest.getZ());
        while (overHighest.getBlock().getType() != Material.AIR) {
            highest = overHighest.getBlock();
            overHighest.add(0, 1, 0);
        }
        return highest;
    }


}
