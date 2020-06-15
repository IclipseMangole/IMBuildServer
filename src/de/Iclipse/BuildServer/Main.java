package de.Iclipse.BuildServer;

import de.Iclipse.BuildServer.Functions.Animations.Flag;
import de.Iclipse.BuildServer.Functions.Animations.Grave;
import de.Iclipse.BuildServer.Functions.Animations.Vent;
import de.Iclipse.BuildServer.Functions.Animations.Windmill;
import de.Iclipse.BuildServer.Functions.Commands.*;
import de.Iclipse.BuildServer.Functions.Listener.BuildListener;
import de.Iclipse.BuildServer.Functions.Listener.SignListener;
import de.Iclipse.BuildServer.Functions.Listener.TestListener;
import de.Iclipse.IMAPI.Util.Dispatching.Dispatcher;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static de.Iclipse.IMAPI.Data.langDE;
import static de.Iclipse.IMAPI.Data.langEN;
import static de.Iclipse.IMAPI.IMAPI.register;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Data.instance = this;
        //loadWorlds();
        registerListener();
        registerCommands();
        loadResourceBundles();
        Bukkit.getWorlds().forEach(entry -> {
            entry.setAutoSave(true);
        });
        //createAnimations();
        //Scheduler.startScheduler();
    }

    @Override
    public void onDisable() {
        //Scheduler.stopScheduler();
    }

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new BuildListener(), this);
        Bukkit.getPluginManager().registerEvents(new TestListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        //Bukkit.getPluginManager().registerEvents(new cmd_map(), this);
    }

    public void registerCommands() {
        register(new cmd_world(), this);
        register(new cmd_clear(), this);
        register(new cmd_map(), this);
        register(new cmd_field(), this);
        register(new cmd_armorstand(), this);
        register(new cmd_glowing(), this);
    }

    public void loadWorlds() {
        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (file.getName().contains("_world")) {
                getServer().createWorld(new WorldCreator(file.getName()));
            }
        }
    }

    public void loadResourceBundles() {
        HashMap<String, ResourceBundle> langs = new HashMap<>();
        try {
            Data.langDE = ResourceBundle.getBundle("i18n.langDE");
            Data.langEN = ResourceBundle.getBundle("i18n.langEN");
        } catch (MissingResourceException e) {
            de.Iclipse.IMAPI.Data.dispatching = false;
        } catch (Exception e) {
            System.out.println("Reload oder Bundle not found!");
            de.Iclipse.IMAPI.Data.dispatching = false;
        }
        langs.put("DE", langDE);
        langs.put("EN", langEN);
        Data.dsp = new Dispatcher(this,
                langs);
    }

    public void createAnimations() {
        Flag.createFlags();
        Grave.createGraves();
        Vent.createVents();
        Windmill.createWindmills();
    }

}
