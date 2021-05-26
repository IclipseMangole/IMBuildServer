package de.Iclipse.BuildServer;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                         
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              
//  ╚══════════════════════════════════════╝

import de.Iclipse.BuildServer.Functions.Animations.Animation;
import de.Iclipse.IMAPI.IMAPI;
import de.Iclipse.IMAPI.Util.Dispatching.Dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by Iclipse on 24.05.2021
 */
public class Data {
    private final IMBuildServer imBuildServer;
    private final IMAPI imapi;

    private Dispatcher dispatcher;
    private ResourceBundle langDE;
    private ResourceBundle langEN;

    private boolean dispatching = true;
    private boolean killlag = true;

    private ArrayList<Animation> animations = new ArrayList<>();


    public Data(IMBuildServer imBuildServer){
        this.imBuildServer = imBuildServer;
        this.imapi = IMAPI.getInstance();
        loadResourceBundles();
    }

    public void loadResourceBundles() {
        HashMap<String, ResourceBundle> langs = new HashMap<>();
        try {
            langDE = ResourceBundle.getBundle("i18n.langDE");
            langEN = ResourceBundle.getBundle("i18n.langEN");
        } catch (MissingResourceException e) {
            dispatching = false;
        } catch (Exception e) {
            System.out.println("Reload oder Bundle not found!");
            dispatching = false;
        }
        langs.put("DE", langDE);
        langs.put("EN", langEN);
        dispatcher = new Dispatcher(imapi, langs);
    }

    public IMAPI getIMAPI() {
        return imapi;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public boolean isDispatching() {
        return dispatching;
    }

    public boolean isKilllag() {
        return killlag;
    }

    public ArrayList<Animation> getAnimations() {
        return animations;
    }
}
