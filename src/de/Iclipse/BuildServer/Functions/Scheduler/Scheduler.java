package de.Iclipse.BuildServer.Functions.Scheduler;

import de.Iclipse.BuildServer.Data;
import de.Iclipse.BuildServer.Functions.Animations.Animation;
import de.Iclipse.BuildServer.IMBuildServer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Scheduler {
    private BukkitTask task;

    public Scheduler(IMBuildServer imBuildServer){
        task = Bukkit.getScheduler().runTaskTimer(imBuildServer, () -> {
            if (!imBuildServer.getData().isKilllag()) {
                for (Animation animation : imBuildServer.getData().getAnimations()) {
                    animation.update();
                }
            }
        }, 20, 20);
    }

    public void stop() {
        task.cancel();
    }
}
