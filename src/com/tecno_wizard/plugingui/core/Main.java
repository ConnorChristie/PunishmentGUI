package com.tecno_wizard.plugingui.core;

import com.tecno_wizard.plugingui.invgui.GUIConstructor;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hidendra.mcstats.Metrics;

import java.io.IOException;

/**
 * Created by Ethan Zeigler on 6/27/2015 for ${PROJECT_NAME}.
 */
public class Main extends JavaPlugin {
    private Resources resources;
    private static Metrics pm;
    private static Updater updater;
    private static UpdateScheduler updateScheduler;
    private GUIConstructor constructor;


    @Override
    public void onEnable() {
        setUpConfig();
        startPluginMetrics();
        registerCommands();
        registerListeners();
        updateScheduler = new UpdateScheduler(this);
        resources = new Resources(this);
        runUpdaterService();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        constructor = new GUIConstructor(this);
    }

    private void registerListeners() {

    }

    private void setUpConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }


    //<editor-fold desc="External Resources">


    private void startPluginMetrics() {
        try {
            pm = new Metrics(this);
        } catch (IOException e) {}
        boolean didMetricsLoad = pm.start();
        if (!didMetricsLoad) {
            Bukkit.getLogger().info(String.format("[%s] Plugin metrics is disabled. This will not affect the performance of PunishmentGUI.",
                    getConfig().getString("PluginPrefix")));
        }
    }

    protected void runUpdaterService() {
        Updater.UpdateType type = Updater.UpdateType.NO_DOWNLOAD;
        if (getConfig().getBoolean("Updater.UpdateNotificationsOn")) {
            if (getConfig().getBoolean("Updater.AutomaticallyGetLatestVersion")) {
                type = Updater.UpdateType.DEFAULT;
            }
            // TODO customize for your server
            updater = new Updater(this, 0, this.getFile(), type, updateScheduler);
        }
    }

    public Updater getUpdater() {
        return updater;
    }
    //</editor-fold>
}