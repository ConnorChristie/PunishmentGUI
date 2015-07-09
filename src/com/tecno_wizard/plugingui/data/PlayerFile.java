package com.tecno_wizard.plugingui.data;

import com.tecno_wizard.plugingui.core.Resources;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

/**
 * Created by Ethan Zeigler on 7/7/2015 for PunismentGUI.
 */
public class PlayerFile {
    private YamlConfiguration file;
    private static final String HISTORY_FIELD = "History";
    private static final String NUM_OF_PREVIOUS_MUTES = "PreviousMutes";
    private static final String NUM_OF_PREVIOUS_BANS = "PreviousBans";

    public PlayerFile(UUID id) {
        file = Resources.getPlayerFile(id);
    }

    /**
     * Gets whether or not a punishment is active. Is only applicable to punishments that have a time constraint. (Not WARNING)
     * @param type PunishmentType to check if active
     * @return true if punishment is currently active, false if not.
     */
    public boolean isPunishmentActive(PunishType type) {
        switch(type) {
            case PERM_MUTE:
            case PERM_BAN:
                return file.getBoolean(type.toString(), false);
            case TEMP_MUTE:
            case TEMP_BAN:
                return file.getLong(type.toString(), 0L)  >= System.currentTimeMillis();
            default:
                throw new IllegalArgumentException("That punishment type is not one that can be active");
        }
    }

    public getNumOfInfractions() {

    }

    /**
     * Gets the number of milliseconds until the temporary punishment expires
     * @param type any temporary punish type
     * @return returns the number of milliseconds until the given punishment ends. Types that do not end or are NA will return 0
     */
    public long getExpirationOfTemporaryPunishment(TemporaryPunishType type) {
        if(file.getLong(type.toString(), 0L)  <= System.currentTimeMillis()) return 0L;
        return file.getLong(type.toString(), 0L) - System.currentTimeMillis();
    }

    public Set<Infraction> getInfractionHistory() {
        List<Infraction> infractions = new HashSet<Infraction>((List<Infraction>) file.getList(HISTORY_FIELD, new ArrayList<Infraction>()));
    }

    /**
     *
     * @param type
     * @param active
     * @return
     */
    public boolean setPunishmentActivity(PunishType type, boolean active) {
        switch(type) {
            case PERM_BAN:
            case PERM_MUTE:
                file.set(type.toString(), active);
                return true;
            case TEMP_BAN:
                if(active) {
                    if (isPunishmentActive(type)) {
                        return false;
                    } else {
                        file.set();
                    }
                }
        }
    }


    private int getNumOfInfractions(TemporaryPunishType type) {
        if(type.equals(PunishType.TEMP_BAN)) {
            file.getInt(NUM_OF_PREVIOUS_BANS, 0);
        } else {
            file.getInt(NUM_OF_PREVIOUS_MUTES, 0);
        }
        return 0;
    }

    private void incrementNumOfInfractions(TemporaryPunishType type) {
        if(type.equals(TemporaryPunishType.TEMP_BAN)) {
            file.set(NUM_OF_PREVIOUS_BANS, file.getInt(NUM_OF_PREVIOUS_BANS, 0) +1);
        }
    }
}
