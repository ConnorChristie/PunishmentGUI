package me.chiller.punishmentgui.handler;

import me.chiller.punishmentgui.core.Config;
import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;
import me.chiller.punishmentgui.resources.Message;
import me.chiller.punishmentgui.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/13/2015 for PunismentGUI.
 */
public class PunishDealer
{
	private Config historyConfig;
	
	public PunishDealer()
	{
		historyConfig = new Config("history");
	}
	
	public void punish(PunishType type, OfflinePlayer player, String punisherName, String reason)
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
			
			addHistoryEntry(player.getName() + " (" + player.getUniqueId().toString() + ") was " + type.getPlural() + ", at " + infraction.getDateString() + ", until " + expiration + ", by " + punisherName + ", for " + reason);
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
			
			addHistoryEntry(player.getName() + " (" + player.getUniqueId().toString() + ") was " + type.getPlural() + ", at " + infraction.getDateString() + ", by " + punisherName + ", for " + reason);
		}
	}
	
	public void revertPunishment(PunishType type, UUID punishedUUID, Player remover, String reason)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(punishedUUID);
		file.setPunishmentActivity(type, false, remover, reason);
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(punishedUUID);
		
		if (player.isOnline())
		{
			Util.sendMessage(Message.UNMUTE.toString(), (Player) player);
		}
		
		addHistoryEntry(remover.getName() + " revoked " + type.toString() + ", for " + player.getName() + " (" + player.getUniqueId().toString() + "), at " + Util.getFormattedDate(System.currentTimeMillis()) + ", because " + reason);
	}
	
	@SuppressWarnings("unchecked")
	private void addHistoryEntry(String str)
	{
		List<String> history = (List<String>) historyConfig.get("history", new ArrayList<String>());
		
		history.add(str);
		
		historyConfig.set("history", history);
		historyConfig.saveConfig();
	}
}
