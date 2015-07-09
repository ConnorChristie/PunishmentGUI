package com.tecno_wizard.plugingui.core;

import com.tecno_wizard.plugingui.data.TemporaryPunishType;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Ethan on 6/27/2015.
 */
public class Resources {
    private static String pluginPrefix;
    private static JavaPlugin plugin;

    /**
     * Allows use of the plugin
     */
    public static final String PUNISH_USE_PERM = "punish.use";
    /**
     * Allows use of temp mute
     */
    public static final String TEMP_MUTE_PERM = "punish.temp_mute";
    /**
     * Allows use of temp ban
     */
    public static final String TEMP_BAN_PERM = "punish.temp_ban";
    /**
     * Allows use of perm ban
     */
    public static final String PERM_BAN_PERM = "punish.perm_ban";
    /**
     * Allows use of perm mute
     */
    public static final String PERM_MUTE_PERM = "punish.perm_mute";
    /**
     * Denies the player from being punished.
     * CURRENTLY UNUSED
     */
    public static final String PROTECTED_PERM = "punish.protected";

    /**
     * Seed time of a temp mute in minutes
     */
    private static long SEED_TEMP_MUTE_TIME;
    /**
     * Seed time of a temp ban in minutes
     */
    private static long SEED_TEMP_BAN_TIME;

    public Resources(JavaPlugin plugin) {
        Resources.plugin = plugin;
        setDataFields();
        directoryCheck(plugin);
    }

    protected void setDataFields() {
        pluginPrefix = plugin.getConfig().getString("PluginPrefix");
        SEED_TEMP_BAN_TIME = plugin.getConfig().getLong("SeedTempBanTime");
        SEED_TEMP_MUTE_TIME = plugin.getConfig().getLong("SeedTempMuteTime");
    }

    private void directoryCheck(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder().getPath() + "/Players");
        file.mkdirs();
    }

    public static long getSeedPunishTime(TemporaryPunishType type) {
        if(type.equals(TemporaryPunishType.TEMP_BAN)) return SEED_TEMP_BAN_TIME;
        else return SEED_TEMP_MUTE_TIME;
    }


    public static YamlConfiguration getPlayerFile(OfflinePlayer player) {
        return getPlayerFile(player.getUniqueId());
    }

    public static YamlConfiguration getPlayerFile(UUID id) {
        File file = new File(plugin.getDataFolder().getPath() + "/Players/" + id.toString() + ".yml");
        try {
            if (!file.exists()) file.createNewFile();
            return YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {}
        return null;
    }

    public static File getPlayerFileSaveLoc(UUID id) {
        File file = new File(plugin.getDataFolder().getPath() + "/Players/" + id.toString() + ".yml");
        try {
            if (!file.exists()) file.createNewFile();
            return file;
        } catch (IOException e) {}
        return null;
    }


    public static void sendMessage(String msg, CommandSender recipient, ChatColor startColor){
        recipient.sendMessage(String.format("%s[%s] %s", startColor, pluginPrefix, msg));
    }


    public String getPluginPrefix() {
        return pluginPrefix;
    }
}
