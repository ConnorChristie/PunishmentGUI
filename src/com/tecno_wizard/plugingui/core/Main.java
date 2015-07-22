package com.tecno_wizard.plugingui.core;

import com.tecno_wizard.plugingui.data.Infraction;
import com.tecno_wizard.plugingui.invgui.GUIClickListener;
import com.tecno_wizard.plugingui.invgui.GUIConstructor;
import com.tecno_wizard.plugingui.misc.MetadataHandler;
import com.tecno_wizard.plugingui.misc.PunishmentChecker;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            runUpdaterService();
        }
    };


    @Override
    public void onEnable() {
        setUpConfig();
        startPluginMetrics();
        registerCommands();
        registerListeners();
        registerSingletons();
        startUpdaterServices();
        updateScheduler = new UpdateScheduler();
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
        new GUIClickListener(this);
        new PunishmentChecker(this);
    }

    private void registerSingletons() {
        MetadataHandler.prepare(this);
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


    @SuppressWarnings("deprecation")
    private void startUpdaterServices() {
        //TODO enable for devbukkit versions
       // Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, updateRunnable, 0L, 72000L);
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