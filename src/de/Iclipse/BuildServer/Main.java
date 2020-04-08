package de.Iclipse.BuildServer;

import de.Iclipse.BuildServer.Functions.Commands.cmd_clear;
import de.Iclipse.BuildServer.Functions.Commands.cmd_world;
import de.Iclipse.BuildServer.Functions.Listener.BuildListener;
import de.Iclipse.BuildServer.Functions.Tablist;
import de.Iclipse.IMAPI.Util.Dispatching.Dispatcher;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static de.Iclipse.IMAPI.IMAPI.register;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Data.instance = this;
        Data.tablist = new Tablist();
        loadWorlds();
        registerListener();
        registerCommands();
        loadResourceBundles();
        Bukkit.getWorlds().forEach(entry -> {
            entry.setAutoSave(true);
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new BuildListener(), this);
    }

    public void registerCommands() {
        register(new cmd_world(), this);
        register(new cmd_clear(), this);
    }

    public void loadWorlds() {
        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (file.getName().contains("_world")) {
                getServer().createWorld(new WorldCreator(file.getName()));
            }
        }
    }

    public void loadResourceBundles() {
        try {
            HashMap<String, ResourceBundle> langs = new HashMap<>();
            Data.langDE = ResourceBundle.getBundle("i18n.langDE");
            Data.langEN = ResourceBundle.getBundle("i18n.langEN");
            langs.put("DE", Data.langDE);
            langs.put("EN", Data.langEN);
            Data.dsp = new Dispatcher(this,
                    langs);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            de.Iclipse.IMAPI.Data.dispatching = false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Reload oder Bundle not found!");
            de.Iclipse.IMAPI.Data.dispatching = false;
        }
    }

}
