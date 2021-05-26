package de.Iclipse.BuildServer.Functions.Listener;

import de.Iclipse.BuildServer.IMBuildServer;
import de.Iclipse.IMAPI.Util.Dispatching.Dispatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;


public class BuildListener implements Listener {
    private IMBuildServer imBuildServer;
    private Dispatcher dsp;

    public BuildListener(IMBuildServer imBuildServer){
        this.imBuildServer = imBuildServer;
        this.dsp = imBuildServer.getData().getDispatcher();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        e.setJoinMessage(null);

        Bukkit.getOnlinePlayers().forEach(entry -> {
            if (!entry.equals(p)) {
                dsp.send(entry, "join.message", p.getDisplayName());
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Bukkit.getOnlinePlayers().forEach(entry -> {
            dsp.send(entry, "quit.message", e.getPlayer().getDisplayName());
        });
    }

    @EventHandler
    public void onDie(PlayerDeathEvent e) {
        Player p = e.getEntity();
        p.spigot().respawn();
        p.teleport(p.getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onDespawn(BlockPhysicsEvent e) {
        if (e.getBlock().getType().toString().contains("LEAVES")) {
            e.setCancelled(true);
            //System.out.println(e.getEventName());
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        //Rain
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }
}
