package com.tecno_wizard.plugingui.invgui;


import com.tecno_wizard.plugingui.data.Infraction;
import com.tecno_wizard.plugingui.data.PlayerFile;
import com.tecno_wizard.plugingui.data.PunishType;
import com.tecno_wizard.plugingui.misc.MetadataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Ethan Zeigler on 7/13/2015 for PunismentGUI.
 */
 //TODO edit player metadata if online
public class PunishDealer {
    public static void warn(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.WARN, reason, System.currentTimeMillis(), punisherName));
        file.save();
        if(player.isOnline()) {
            ((Player)player).sendMessage(String.format("%sYou received a warning for: %s. Continued action may result in a punishment", ChatColor.RED, reason));
        }
    }

    public static void tempMute(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.TEMP_MUTE, reason, System.currentTimeMillis(), punisherName));
        file.save();
        if(player.isOnline()) {
            //DEBUG
            System.out.println("online");
            ((Player)player).sendMessage(ChatColor.RED + "You have been temporarily muted for: " + reason + ".");
            MetadataHandler.getInstance().applyNeccesaryData((Player)player);
        }
    }

    public static void permMute(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.PERM_MUTE, reason, System.currentTimeMillis(), punisherName));
        if(player.isOnline()) {
            ((Player)player).sendMessage(ChatColor.RED + "You have been permanently muted for: \"" + reason + "\" by " + punisherName);
        }
        file.save();
    }

    public static void tempBan(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.TEMP_BAN, reason, System.currentTimeMillis(), punisherName));
        file.save();
        if(player.isOnline()) {
            ((Player)player).kickPlayer("You have been temporarily banned for: " + reason + ".");
        }
    }

    public static void permBan(OfflinePlayer player, String punisherName, String reason) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.addInfraction(new Infraction(PunishType.PERM_BAN, reason, System.currentTimeMillis(), punisherName));
        file.save();
        if(player.isOnline()) {
            ((Player)player).kickPlayer("You have been permanently banned for: " + reason + ".");
        }
    }

    public static void revertPunishment(OfflinePlayer player, PunishType type) {
        PlayerFile file = new PlayerFile(player.getUniqueId());
        file.setPunishmentActivity(type, false);
        if(player.isOnline()) {
            MetadataHandler.getInstance().applyNeccesaryData((Player) player);
            switch(type) {
                case PERM_MUTE: ((Player) player).sendMessage(ChatColor.GREEN + "You are no longer permanently muted.");
                    break;
                case TEMP_MUTE: ((Player) player).sendMessage(ChatColor.GREEN + "You are no longer temp muted.");
            }
        }

    }
}
