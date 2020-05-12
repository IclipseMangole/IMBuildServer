package de.Iclipse.BuildServer.Functions.Listener;

import de.Iclipse.IMAPI.Database.Mode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import static de.Iclipse.BuildServer.Data.dsp;

public class SignListener implements Listener {
    @EventHandler
    public void onBlockPlace(SignChangeEvent e) {
        if (e.getPlayer().hasPermission("im.sign.create")) {
            if (e.getLine(0).contains("[") && e.getLine(0).contains("]")) {
                String mode = e.getLine(0).replace("[", "").replace("]", "");
                if (!de.Iclipse.IMAPI.Database.Sign.isSign(e.getBlock().getLocation())) {
                    if (Mode.getModes().contains(mode)) {
                        de.Iclipse.IMAPI.Database.Sign.createSign(mode, e.getBlock().getLocation());
                        dsp.send(e.getPlayer(), "sign.create");
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
