package me.chiller.punishmentgui.invgui;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.core.Resources;
import me.chiller.punishmentgui.core.Resources.Messages;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/13/2015 for PunismentGUI.
 */
public class PunishDealer
{
	public static void warn(OfflinePlayer player, String punisherName, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		
		file.saveInfraction(new Infraction(PunishType.WARN, reason, System.currentTimeMillis(), punisherName));
		file.save();
		
		if (player.isOnline())
		{
			Resources.sendMessage(Messages.WARN.replace("%reason%", reason).replace("%punisher%", punisherName), ((Player) player));
		}
	}
	
	public static void tempMute(OfflinePlayer player, String punisherName, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		
		file.saveInfraction(new Infraction(PunishType.TEMP_MUTE, reason, System.currentTimeMillis(), punisherName));
		file.save();
		
		if (player.isOnline())
		{
			Resources.sendMessage(Resources.Messages.TEMP_MUTE.replace("%reason%", reason).replace("%punisher%", punisherName), ((Player) player));
			Resources.sendSuffix((Player) player);
		}
	}
	
	public static void permMute(OfflinePlayer player, String punisherName, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		file.saveInfraction(new Infraction(PunishType.PERM_MUTE, reason, System.currentTimeMillis(), punisherName));
		
		if (player.isOnline())
		{
			Resources.sendMessage(Resources.Messages.PERM_MUTE.replace("%reason%", reason).replace("%punisher%", punisherName), ((Player) player));
			Resources.sendSuffix((Player) player);
		}
		
		file.save();
	}
	
	public static void tempBan(OfflinePlayer player, String punisherName, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		
		file.saveInfraction(new Infraction(PunishType.TEMP_BAN, reason, System.currentTimeMillis(), punisherName));
		file.save();
		
		if (player.isOnline())
		{
			((Player) player).kickPlayer(Resources.Messages.TEMP_BAN.replace("%reason%", reason).replace("%punisher%", punisherName));
		}
	}
	
	public static void permBan(OfflinePlayer player, String punisherName, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		
		file.saveInfraction(new Infraction(PunishType.PERM_BAN, reason, System.currentTimeMillis(), punisherName));
		file.save();
		
		if (player.isOnline())
		{
			((Player) player).kickPlayer(Resources.Messages.PERM_BAN.replace("%reason%", reason).replace("%punisher%", punisherName));
		}
	}
	
	public static void revertPunishment(UUID punishedUUID, PunishType type, Player remover, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(punishedUUID);
		file.setPunishmentActivity(type, false, remover, reason);
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(punishedUUID);
		
		if (player.isOnline())
		{
			Resources.sendMessage(Messages.UNMUTE.toString(), (Player) player);
		}
	}
}
