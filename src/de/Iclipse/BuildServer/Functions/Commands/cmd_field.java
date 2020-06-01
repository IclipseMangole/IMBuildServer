package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.IMAPI.Util.Command.IMCommand;
import de.Iclipse.IMAPI.Util.executor.ThreadExecutor;
import net.minecraft.server.v1_15_R1.MapIcon;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.Iclipse.BuildServer.Functions.Commands.cmd_map.getMap;

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
                    ItemStack item = p.getInventory().getItemInMainHand();
                    System.out.println(Instant.now().toString());
                    int id = ((MapMeta) item.getItemMeta()).getMapView().getId();

                    List<MapRenderer> removing = new ArrayList<>(((MapMeta) item.getItemMeta()).getMapView().getRenderers());
                    removing.forEach(((MapMeta) item.getItemMeta()).getMapView()::removeRenderer);

                    Collection<MapIcon> list = new ArrayList<>();
                    //list.add(new MapIcon(MapIcon.Type.BANNER_MAGENTA, (byte) p.getLocation().getBlockX(), (byte) p.getWorld().getSpawnLocation().getBlockZ(), (byte) 0, null));

                    byte c = 0;
                    byte[] data = new byte[128 * 128];
                    for (int i = 0; i < 128 * 128; i++) {
                        data[i] = c;
                        System.out.println("I: " + i + " = " + c);
                        c = (byte) (i % 127);
                    }

                    PacketPlayOutMap packet = new PacketPlayOutMap(id, (byte) 1, false, true, list, data, 0, 0, 128, 128);
                    ((CraftPlayer) p).getHandle().playerConnection.networkManager.sendPacket(packet);
                    p.sendMessage("finished");
                }
            });
        }
        if (p.getInventory().getItemInOffHand().getType().equals(Material.MAP)) {
            p.getInventory().setItemInOffHand(getMap(p, (byte) 3));
            ThreadExecutor.executeAsync(new Runnable() {
                @Override
                public void run() {
                    ItemStack item = p.getInventory().getItemInOffHand();
                    System.out.println(Instant.now().toString());
                    int id = ((MapMeta) item.getItemMeta()).getMapView().getId();

                    List<MapRenderer> removing = new ArrayList<>(((MapMeta) item.getItemMeta()).getMapView().getRenderers());
                    removing.forEach(((MapMeta) item.getItemMeta()).getMapView()::removeRenderer);

                    Collection<MapIcon> list = new ArrayList<>();
                    list.add(new MapIcon(MapIcon.Type.BANNER_MAGENTA, (byte) p.getLocation().getBlockX(), (byte) p.getWorld().getSpawnLocation().getBlockZ(), (byte) 0, null));

                    byte[] data = new byte[128 * 128];
                    for (int i = 0; i < (128 * 128); i++) {
                        data[i] = color;
                    }
                    PacketPlayOutMap packet = new PacketPlayOutMap(id, (byte) 0, false, true, list, data, 0, 0, 128, 128);
                    ((CraftPlayer) p).getHandle().playerConnection.networkManager.sendPacket(packet);
                    p.sendMessage("finished");
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
}
