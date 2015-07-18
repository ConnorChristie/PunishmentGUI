package com.tecno_wizard.plugingui.data;

import com.tecno_wizard.plugingui.core.Resources;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ethan Zeigler on 7/7/2015 for PunishmentGUI.
 */
public class PlayerFile {
    private YamlConfiguration configuration;
    private File file;
    private static final String HISTORY_FIELD = "History";
    private static final String NUM_OF_PREVIOUS_MUTES = "PreviousMutes";
    private static final String NUM_OF_PREVIOUS_BANS = "PreviousBans";

    public PlayerFile(UUID id) {
        this.configuration = Resources.getPlayerFile(id);
        this.file = Resources.getPlayerFileSaveLoc(id);
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
                return configuration.getBoolean(type.toString(), false);
            case TEMP_MUTE:
            case TEMP_BAN:
                return configuration.getLong(type.toString(), 0L)  >= System.currentTimeMillis();
            default:
                throw new IllegalArgumentException("That punishment type is not one that can be active");
        }
    }

    /**
     * Gets the number of milliseconds until the temporary punishment expires
     * @param type any temporary punish type
     * @return returns the number of milliseconds at which the given punishment ends. Types that do not end or are NA will return 0
     */
    public long getExpirationOfTemporaryPunishment(TemporaryPunishType type) {
        Long time = System.currentTimeMillis();
        if(configuration.getLong(type.toString(), 0L)  <= time) return 0L;
        return configuration.getLong(type.toString(), 0L);
    }

    public List<Infraction> getInfractionHistory() {
        return (List<Infraction>) configuration.getList(HISTORY_FIELD, new ArrayList<Infraction>());
    }

    /**
     * Sets the acive state of a punishment. Is not required for warnings.
     * @param type punishment type to change activity of
     * @param active activity state to set to
     * @return whether the activity state was successfully changed to var active. Will return true if the activity state was the same as var active.
     */
    public boolean setPunishmentActivity(PunishType type, boolean active) {
        switch(type) {
            case PERM_BAN:
            case PERM_MUTE:
                configuration.set(type.toString(), active);
                return true;
            case TEMP_MUTE:
            case TEMP_BAN:
                if(active) {
                    // activate
                    if (isPunishmentActive(type)) {
                        return true;
                    } else {
                        long seed = (long) Math.pow(getNumOfInfractions(TemporaryPunishType.valueOf(type.toString())) + 1, 2);
                        configuration.set(type.toString(), (System.currentTimeMillis() * seed));
                        incrementNumOfInfractions(TemporaryPunishType.valueOf(type.toString()));
                    }
                } else {
                    // deactivate
                    if(!isPunishmentActive(type)) {
                        return true;
                    } else {
                        configuration.set(type.toString(), 0);
                        return true;
                    }
                }
            default:
                return false;
        }
    }


    public int getNumOfInfractions(TemporaryPunishType type) {
        if(type.equals(PunishType.TEMP_BAN)) {
            configuration.getInt(NUM_OF_PREVIOUS_BANS, 0);
        } else {
            configuration.getInt(NUM_OF_PREVIOUS_MUTES, 0);
        }
        return 0;
    }

    private void incrementNumOfInfractions(TemporaryPunishType type) {
        if(type.equals(TemporaryPunishType.TEMP_BAN)) {
            configuration.set(NUM_OF_PREVIOUS_BANS, configuration.getInt(NUM_OF_PREVIOUS_BANS, 0) +1);
        } else {
            configuration.set(NUM_OF_PREVIOUS_MUTES, configuration.getInt(NUM_OF_PREVIOUS_MUTES, 0) +1);
        }
    }

    public void addInfraction(Infraction infraction) {
        List list = configuration.getList(HISTORY_FIELD, new ArrayList<Infraction>());
        list.add(infraction);
        configuration.set(HISTORY_FIELD, list);
        setPunishmentActivity(infraction.getType(), true);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
