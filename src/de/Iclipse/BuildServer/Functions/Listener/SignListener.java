package de.Iclipse.BuildServer.Functions.Listener;

import de.Iclipse.BuildServer.IMBuildServer;
import de.Iclipse.IMAPI.Util.Dispatching.Dispatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {
    private IMBuildServer imBuildServer;
    private Dispatcher dsp;

    public SignListener(IMBuildServer imBuildServer) {
        this.imBuildServer = imBuildServer;
        this.dsp = imBuildServer.getData().getDispatcher();
    }

    @EventHandler
    public void onBlockPlace(SignChangeEvent e) {
        if (e.getPlayer().hasPermission("im.sign.create")) {
            if (e.getLine(0).contains("[") && e.getLine(0).contains("]")) {
                String mode = e.getLine(0).replace("[", "").replace("]", "");
                if (!imBuildServer.getData().getIMAPI().getData().getSignTable().isSign(e.getBlock().getLocation())) {
                    if (imBuildServer.getData().getIMAPI().getData().getModeTable().getModes().contains(mode)) {
                        imBuildServer.getData().getIMAPI().getData().getSignTable().createSign(mode, e.getBlock().getLocation());
                        dsp.send(e.getPlayer(), "sign.create");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().name().contains("SIGN")) {
            if (imBuildServer.getData().getIMAPI().getData().getSignTable().isSign(e.getBlock().getLocation())) {
                if (e.getPlayer().hasPermission("im.sign.delete")) {
                    imBuildServer.getData().getIMAPI().getData().getSignTable().deleteSign(imBuildServer.getData().getIMAPI().getData().getSignTable().getId(e.getBlock().getLocation()));
                    dsp.send(e.getPlayer(), "sign.delete");
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }


}
