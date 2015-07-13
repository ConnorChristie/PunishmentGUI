package com.tecno_wizard.plugingui.invgui;


import com.tecno_wizard.plugingui.core.Resources;
import com.tecno_wizard.plugingui.data.Infraction;
import com.tecno_wizard.plugingui.data.PlayerFile;
import com.tecno_wizard.plugingui.data.PunishType;
import org.bukkit.OfflinePlayer;

/**
 * Created by Ethan Zeigler on 7/13/2015 for PunismentGUI.
 */
 //TODO edit player metadata if online
public class PunishDealer {
    public static void warn(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.WARN, reason, System.currentTimeMillis(), punisherName));
        file.save();
    }

    public static void tempMute(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.TEMP_MUTE, reason, System.currentTimeMillis(), punisherName));
        file.save();
    }

    public static void permMute(OfflinePlayer player, String punisherName, String reason) {

        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.PERM_MUTE, reason, System.currentTimeMillis(), punisherName));
        file.save();
    }

    public static void tempBan(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.TEMP_BAN, reason, System.currentTimeMillis(), punisherName));
    }

    public static void permBan(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.PERM_BAN, reason, System.currentTimeMillis(), punisherName));
    }
}
