package de.Iclipse.BuildServer.Functions.Listener;

import de.Iclipse.BuildServer.IMBuildServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;


public class BackupListener implements Listener {
    private IMBuildServer imBuildServer;

    public BackupListener(IMBuildServer imBuildServer) {
        this.imBuildServer = imBuildServer;
    }

    @EventHandler
    public void onSave(WorldSaveEvent e) {
        File from = e.getWorld().getWorldFolder();
        File backupFolder = new File(imBuildServer.getDataFolder().getPath() + "/backups");
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }
        saveFileTo(from, new File(backupFolder + "/" + e.getWorld().getName() + "_" + Instant.now().toString()));

        int backups = 0;
        File oldest = null;
        for (File file : backupFolder.listFiles()) {
            if (file.getName().startsWith(e.getWorld().getName() + "_")) {
                backups++;
                if (oldest == null) {
                    oldest = file;
                } else {
                    Instant dateFile = Instant.parse(file.getName().replace(e.getWorld().getName() + "_", ""));
                    Instant dateOldest = Instant.parse(oldest.getName().replace(e.getWorld().getName() + "_", ""));
                    if (dateFile.isBefore(dateOldest)) {
                        oldest = file;
                    }
                }
            }
        }
        if (backups > 3) {
            oldest.delete();
        }
    }

    public static void saveFileTo(File fromWorld, File toWorld) {
        try {
            FileUtil.copy(new File(fromWorld.getPath() + "/region"), new File(toWorld.getPath() + "/region"));
            Files.copy(new File(fromWorld.getPath() + "/level.dat").toPath(), new File(toWorld.getPath() + "/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileUtil.copy(new File(fromWorld.getPath() + "/maps"), new File(toWorld.getPath() + "/maps"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
