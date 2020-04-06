package de.Iclipse.BuildServer;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Data.instance = this;
        loadWorlds();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void loadWorlds(){
        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if(file.getName().contains("_world")){
                getServer().createWorld(new WorldCreator(file.getName()));
            }
        }
    }

}
