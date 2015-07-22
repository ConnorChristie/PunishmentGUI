package com.tecno_wizard.plugingui.misc;

import com.tecno_wizard.plugingui.data.PlayerFile;
import com.tecno_wizard.plugingui.data.PunishType;
import com.tecno_wizard.plugingui.data.TemporaryPunishType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;

/**
 * Created by Ethan Zeigler on 7/15/2015 for PunismentGUI.
 */
public class MetadataHandler {
    public static final String MUTE_IS_ACTIVE_KEY = "PUNISHMENTGUIMUTED";
    public static final String MUTE_IS_PERMANENT_KEY = "PUNISHMENTGUIMUTEPERMANENT";
    public static final String MUTE_EXPIRATION_KEY = "PUNISHMENTGUIMUTEEXPIRATION";
    private static MetadataHandler ourInstance;
    private JavaPlugin plugin;

    public static MetadataHandler getInstance() {
        return ourInstance;
    }

    public static void prepare(JavaPlugin plugin) {
        if(ourInstance == null) {
            ourInstance = new MetadataHandler(plugin);
        }
    }

    private MetadataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyNeccesaryData(final Player player) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        player.removeMetadata(MUTE_IS_PERMANENT_KEY, plugin);
        player.removeMetadata(MUTE_IS_ACTIVE_KEY, plugin);
        player.removeMetadata(MUTE_EXPIRATION_KEY, plugin);
        if(file.isPunishmentActive(PunishType.TEMP_MUTE)) {
            //DEBUG
            System.out.println("TEMP MUTE APPLIED");
            player.setMetadata(MUTE_IS_ACTIVE_KEY, new FixedMetadataValue(plugin, true));
            player.setMetadata(MUTE_EXPIRATION_KEY, new FixedMetadataValue(plugin, file.getExpirationOfTemporaryPunishment(TemporaryPunishType.TEMP_MUTE)));
            player.setMetadata(MUTE_IS_PERMANENT_KEY, new FixedMetadataValue(plugin, false));
        } else if(file.isPunishmentActive(PunishType.PERM_MUTE)) {
            System.out.println("PERM MUTE APPLIED");
            player.setMetadata(MUTE_IS_ACTIVE_KEY, new FixedMetadataValue(plugin, true));
            player.setMetadata(MUTE_IS_PERMANENT_KEY, new FixedMetadataValue(plugin, true));
        }
    }

    public boolean isPlayerMuted(Player player) {
        try {
            return player.getMetadata(MUTE_IS_ACTIVE_KEY).get(0).asBoolean();
        } catch (IndexOutOfBoundsException e) { return false; }
    }

    public boolean isMutePermanent(Player player) {
        return player.getMetadata(MUTE_IS_PERMANENT_KEY).get(0).asBoolean();
    }

    public String getMuteExpirationAsString(Player player) {
        try {
            Long expiration = player.getMetadata(MUTE_EXPIRATION_KEY).get(0).asLong();
            if (expiration == null) return "";
            return new Timestamp(expiration).toString();
        } catch(IndexOutOfBoundsException exception) {return "";}
    }

    public Long getMuteExpiration(Player player) {
        try {
            Long expiration = player.getMetadata(MUTE_EXPIRATION_KEY).get(0).asLong();
            return expiration;
        } catch (IndexOutOfBoundsException exception) {return Long.MAX_VALUE;}
    }
}
