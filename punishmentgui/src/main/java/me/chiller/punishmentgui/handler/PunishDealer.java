package me.chiller.punishmentgui.handler;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;
import me.chiller.punishmentgui.resources.Message;
import me.chiller.punishmentgui.util.Util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/13/2015 for PunismentGUI.
 */
public class PunishDealer
{
	public static void punish(PunishType type, OfflinePlayer player, String punisherName, String reason)
	{
		if (type.isTemp())
		{
			PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
			Infraction infraction = new Infraction(type, reason, System.currentTimeMillis(), punisherName);
			
			file.saveInfraction(infraction);
			
			String expiration = file.getExpiration(type);
			infraction.setExpiration(expiration);
			
			if (player.isOnline())
			{
				if (type == PunishType.PERM_BAN || type == PunishType.TEMP_BAN)
				{
					((Player) player).kickPlayer(type.getMessage().replace("{reason}", reason).replace("{punisher}", punisherName).replace("{date}", infraction.getDateString()).replace("{expiration}", expiration));
				} else
				{
					Util.sendMessage(type.getMessage().replace("{reason}", reason).replace("{punisher}", punisherName).replace("{date}", infraction.getDateString()).replace("{expiration}", expiration), ((Player) player));
					Util.sendSuffix((Player) player);
				}
			}
		} else
		{
			PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
			Infraction infraction = new Infraction(type, reason, System.currentTimeMillis(), punisherName);
			
			file.saveInfraction(infraction);
			
			if (player.isOnline())
			{
				if (type == PunishType.PERM_BAN || type == PunishType.TEMP_BAN)
				{
					((Player) player).kickPlayer(type.getMessage().replace("{reason}", reason).replace("{punisher}", punisherName).replace("{date}", infraction.getDateString()));
				} else
				{
					Util.sendMessage(type.getMessage().replace("{reason}", reason).replace("{punisher}", punisherName).replace("{date}", infraction.getDateString()), ((Player) player));
					Util.sendSuffix((Player) player);
				}
			}
		}
	}
	
	public static void revertPunishment(PunishType type, UUID punishedUUID, Player remover, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(punishedUUID);
		file.setPunishmentActivity(type, false, remover, reason);
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(punishedUUID);
		
		if (player.isOnline())
		{
			Util.sendMessage(Message.UNMUTE.toString(), (Player) player);
		}
	}
}
