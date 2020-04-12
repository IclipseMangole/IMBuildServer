package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.BuildServer.Data;
import de.Iclipse.IMAPI.Util.Command.IMCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class cmd_map implements Listener {
    @IMCommand(
            name = "map",
            maxArgs = 0,
            minArgs = 0,
            noConsole = true,
            permissions = "im.cmd.map"
    )
    public void map(Player p) {
        Location loc = p.getLocation();
        p.getInventory().setItem(8, getMap(p));
        renderMap(p, 0, 0, loc);
    }

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

    public static ItemStack getMap(Player p) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(getMapView(p.getWorld()));
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public static MapView getMapView(World w) {
        MapView view = Bukkit.createMap(w);
        view.setUnlimitedTracking(true);
        view.setTrackingPosition(false);
        view.setCenterX(0);
        view.setCenterZ(0);
        view.setTrackingPosition(true);
        view.setScale(MapView.Scale.FAR);
        view.setLocked(false);
        return view;
    }

}
