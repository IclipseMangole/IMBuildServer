package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.IMAPI.Util.Command.IMCommand;
import de.Iclipse.IMAPI.Util.executor.ThreadExecutor;
import net.minecraft.server.v1_16_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.map.CraftMapView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.time.Instant;
import java.util.ArrayList;


public class cmd_field {

    @IMCommand(
            name = "field",
            maxArgs = 1,
            minArgs = 1,
            noConsole = true,
            permissions = "im.cmd.field"
    )
    public void exec(Player p, byte color) {
        if (p.getInventory().getItemInMainHand().getType().equals(Material.MAP)) {
            p.getInventory().setItemInMainHand(getMap(p, (byte) 3));
            ThreadExecutor.executeAsync(new Runnable() {
                @Override
                public void run() {
                    sendMapView(p, color, p.getInventory().getItemInMainHand());
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

    public void sendMapView(Player p, byte color, ItemStack item) {
        System.out.println(Instant.now().toString());
        int id = ((MapMeta) item.getItemMeta()).getMapView().getId();


        //File f = new File(p.getWorld().getWorldFolder().getPath() + "/maps/map_" + p.getLocation().getBlockX() + "_" + p.getLocation().getBlockZ() + "_" + scale + ".txt");
        byte[] data = new byte[128 * 128];

        for (int i = 0; i < 128 * 128; i++) {
            data[i] = color;
            //System.out.println("I: " + i + " = " + c);
            //c = (byte) (i % 127);
        }
        PacketPlayOutMap packet = new PacketPlayOutMap(id, (byte) 0, true, true, new ArrayList<>(), data, 0, 0, 128, 128);
        ((CraftPlayer) p).getHandle().playerConnection.networkManager.sendPacket(packet);
        System.out.println(Instant.now().toString());
    }
}
