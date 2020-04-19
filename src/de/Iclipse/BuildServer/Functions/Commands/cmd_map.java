package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.IMAPI.Util.Command.IMCommand;
import de.Iclipse.IMAPI.Util.executor.Callback;
import de.Iclipse.IMAPI.Util.executor.ThreadExecutor;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.map.CraftMapView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
            p.getInventory().setItemInMainHand(getMap(p));
            task = ThreadExecutor.executeAsync(new Runnable() {
                @Override
                public void run() {
                    sendMapView(p, scale, p.getInventory().getItemInMainHand());
                }
            });
        }
        if (p.getInventory().getItemInOffHand().getType().equals(Material.MAP)) {
            p.getInventory().setItemInOffHand(getMap(p));
            task = ThreadExecutor.executeAsync(new Runnable() {
                @Override
                public void run() {
                    sendMapView(p, scale, p.getInventory().getItemInOffHand());
                }
            });
        }

        /*
        MapRender render = new MapRender(p.getLocation(), scale);
        render.run();
        while(!render.isFinished()){
        }
         */
    }

    /*
    public void renderMap(Player p, int x, int z, Location loc) {
        p.getInventory().setHeldItemSlot(8);
        int[] arrayX = {x};
        int[] arrayZ = {z};
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000, 100, true, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1000, 100, true, false));
        int loaddistance = 8;
        int tpdistance = 16 * loaddistance;
        int worldsize = 16 * 64;
        Bukkit.getScheduler().runTaskLater(Data.instance, () -> {
            if (arrayX[0] < worldsize / tpdistance) {
                if (arrayZ[0] < worldsize / tpdistance) {
                    p.teleport(new Location(p.getWorld(), arrayX[0] * tpdistance - (0.5 * worldsize - tpdistance + 50), 250, arrayZ[0] * tpdistance - (0.5 * worldsize - tpdistance + 50), loc.getYaw(), 90));
                    p.setGravity(false);
                    p.damage(0.0);
                    arrayZ[0]++;
                } else {
                    arrayZ[0] = 0;
                    arrayX[0]++;
                }
                renderMap(p, arrayX[0], arrayZ[0], loc);
            } else {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.removePotionEffect(PotionEffectType.CONFUSION);
                p.teleport(loc);
                p.setGravity(true);
            }
        }, 15);
    }
    */
    public static ItemStack getMap(Player p) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(getMapView(p));
        //meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public static MapView getMapView(Player p) {
        CraftMapView view = (CraftMapView) Bukkit.createMap(p.getWorld());
        view.setUnlimitedTracking(true);
        view.setTrackingPosition(false);
        view.setCenterX(p.getLocation().getBlockX());
        view.setCenterZ(p.getLocation().getBlockY());
        view.setTrackingPosition(true);
        view.setScale(MapView.Scale.FAR);
        view.setLocked(true);
        return view;
    }

    public static void sendMapView(Player p, byte scale, ItemStack item) {
        System.out.println(Instant.now().toString());
        int id = ((MapMeta) item.getItemMeta()).getMapView().getId();

        List<MapRenderer> removing = new ArrayList<>(((MapMeta) item.getItemMeta()).getMapView().getRenderers());
        removing.forEach(((MapMeta) item.getItemMeta()).getMapView()::removeRenderer);

        Collection<MapIcon> list = new ArrayList<>();
        list.add(new MapIcon(MapIcon.Type.BANNER_MAGENTA, (byte) p.getLocation().getBlockX(), (byte) p.getWorld().getSpawnLocation().getBlockZ(), (byte) 0, null));

        /*
        MapRender render = new MapRender(p.getLocation(), scale);
        render.run();
        while(!render.isFinished()){
        }
         */
        File f = new File(p.getWorld().getWorldFolder().getPath() + "/maps/map_" + p.getLocation().getBlockX() + "_" + p.getLocation().getBlockZ() + "_" + scale + ".txt");
        byte[] data;
        if (f.exists()) {
            data = loadData(f);
        } else {
            data = renderMap(p.getLocation(), scale, (byte) Math.pow(2, scale));
            saveData(data, f);
        }
        PacketPlayOutMap packet = new PacketPlayOutMap(id, scale, true, true, list, data, 0, 0, 128, 128);
        ((CraftPlayer) p).getHandle().playerConnection.networkManager.sendPacket(packet);
        System.out.println(Instant.now().toString());
    }

    public static byte[] renderMap(Location middle, byte scale, byte scaleBlocks) {
        byte[] data = new byte[128 * 128];
        Location upperLeftCorner = middle.subtract(128 * scaleBlocks / 2, 0.0, 128 * scaleBlocks / 2);
        System.out.println("BlockX: " + upperLeftCorner.getBlockX() + ", BlockZ: " + upperLeftCorner.getBlockZ());
        for (int mapZ = 0; mapZ < 128; mapZ++) {
            for (int mapX = 0; mapX < 128; mapX++) {

                final byte[] mostOften = {(byte) MaterialMapColor.b.ac};
                int biggestAmount = 0;
                getColor(upperLeftCorner, (byte) mapX, (byte) mapZ, scaleBlocks).forEach((color, amount) -> {
                    if (amount > biggestAmount) {
                        mostOften[0] = color;
                    }
                });
                data[mapX + mapZ * 128] = (mostOften[0]);
                //System.out.println("Field:" + (mapZ));
                //System.out.println("Color: " + mostOften[0]);
            }
            //System.out.println("Row: " + (mapZ + 1));
        }
        return data;
    }

    private static ArrayList<Chunk> forceLoadedChunks = new ArrayList<>();

    public static HashMap<Byte, Byte> getColor(Location corner, byte mapX, byte mapZ, byte size) {
        HashMap<Byte, Byte> blocks = new HashMap<>();
        for (int fieldX = 0; fieldX < size; fieldX++) {
            for (int fieldZ = 0; fieldZ < (int) size; fieldZ++) { //corner.add((mapX * (int) size) + fieldX, 0.0, (mapZ * size) + fieldZ)
                Block b = null;
                Location loc = corner.getWorld().getHighestBlockAt(corner.getBlockX() + (mapX * (int) size) + fieldX, corner.getBlockZ() + (mapZ * size) + fieldZ).getLocation();
                if (!corner.getWorld().getChunkAt(loc).isLoaded()) {
                    System.out.println("Not loaded!");
                    corner.getWorld().getChunkAt(loc).setForceLoaded(true);
                    forceLoadedChunks.add(corner.getWorld().getChunkAt(loc));
                    while (b == null) {
                        System.out.println(b == null);
                        b = ((CraftBlock) loc.getBlock()).getNMS().getBlock();
                    }
                } else {
                    if (!forceLoadedChunks.contains(loc.getChunk())) {
                        loc.getChunk().setForceLoaded(false);
                        forceLoadedChunks.remove(loc.getChunk());
                    }
                    b = ((CraftBlock) loc.getBlock()).getNMS().getBlock();

                }
                //System.out.println(new Location(corner.getWorld(), corner.getBlockX() + (mapX * (int) size) + fieldX, 0.0, corner.getBlockZ() + (mapZ * size) + fieldZ));
                MaterialMapColor materialMapColor = b.e(b.getBlockData(), null, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                byte color;
                if (materialMapColor != null) {
                    color = (byte) ((materialMapColor.ac * 4) + (loc.getBlockY() % 2) * 3);
                } else {
                    color = 0;
                }
                //System.out.println("getColor: " + color);
                if (blocks.containsKey(color)) {
                    blocks.replace(color, (byte) (blocks.get(color) + 1));
                } else {
                    blocks.put(color, (byte) 1);
                }
                    /*
                } else {
                    byte color = (byte) MaterialMapColor.aa.ac;
                    if (blocks.containsKey(color)) {
                        blocks.replace(color, blocks.get(color));
                    } else {
                        blocks.put(color, (byte) 1);
                    }
                }
               */
            }
        }
        return blocks;
    }

    public static byte[] loadData(File f) {
        BufferedReader reader = null;
        byte[] data;
        try {
            reader = new BufferedReader(new FileReader(f));
            String[] line = reader.readLine().split(",");
            data = new byte[line.length];
            for (int i = 0; i < line.length; i++) {
                System.out.println(line[i]);
                data[i] = Byte.parseByte(line[i]);
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveData(byte[] data, File f) {
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


}
