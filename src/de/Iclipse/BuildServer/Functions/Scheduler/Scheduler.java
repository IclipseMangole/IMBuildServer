package de.Iclipse.BuildServer.Functions.Scheduler;

import de.Iclipse.BuildServer.Data;
import de.Iclipse.BuildServer.Functions.Animations.Flag;
import de.Iclipse.BuildServer.Functions.Animations.Grave;
import de.Iclipse.BuildServer.Functions.Animations.Vent;
import de.Iclipse.BuildServer.Functions.Animations.Windmill;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Scheduler {
    private static BukkitTask task;

    public static void startScheduler() {
        task = Bukkit.getScheduler().runTaskTimer(Data.instance, () -> {
            Flag.flag();
            Grave.grave();
            Vent.vent();
            Windmill.windmill();
        }, 20, 20);
    }

    public static void stopScheduler() {
        task.cancel();
    }
}
