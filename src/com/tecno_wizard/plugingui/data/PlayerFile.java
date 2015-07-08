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

    /**
     * Gets the number of milliseconds until the temporary punishment expires
     * @param type any temporary punish type
     * @return returns the number of milliseconds until the given punishment ends. Types that do not end or are NA will return 0
     */
    public long getExpirationOfPunishment(PunishType type) {
        if(file.getLong(type.toString(), 0L)  <= System.currentTimeMillis()) return 0L;
        return file.getLong(type.toString(), 0L) - System.currentTimeMillis();
    }

    public Set<Infraction> getInfractionHistory() {
        List<Infraction> infractions = new HashSet<Infraction>((List<Infraction>) file.getList(HISTORY_FIELD, new ArrayList<Infraction>()));
    }
}
