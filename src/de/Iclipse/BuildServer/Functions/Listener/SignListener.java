package de.Iclipse.BuildServer.Functions.Listener;

import de.Iclipse.IMAPI.Database.Mode;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import static de.Iclipse.BuildServer.Data.dsp;

public class SignListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType().name().contains("SIGN")) {
            Sign sign = (Sign) e.getBlock().getState();
            if (e.getPlayer().hasPermission("im.sign.create")) {
                if (sign.getLine(0).startsWith("[") && sign.getLine(0).endsWith("]")) {
                    String mode = sign.getLine(0).replace("[", "").replace("]", "");
                    if (!de.Iclipse.IMAPI.Database.Sign.isSign(sign.getLocation())) {
                        if (Mode.getModes().contains(mode)) {
                            de.Iclipse.IMAPI.Database.Sign.createSign(mode, sign.getLocation());
                            dsp.send(e.getPlayer(), "sign.create");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().name().contains("SIGN")) {
            if (de.Iclipse.IMAPI.Database.Sign.isSign(e.getBlock().getLocation())) {
                if (e.getPlayer().hasPermission("im.sign.delete")) {
                    de.Iclipse.IMAPI.Database.Sign.deleteSign(de.Iclipse.IMAPI.Database.Sign.getId(e.getBlock().getLocation()));
                    dsp.send(e.getPlayer(), "sign.delete");
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }


}
