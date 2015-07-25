package me.chiller.punishmentgui.handler;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;
import me.chiller.punishmentgui.resources.Message;
import me.chiller.punishmentgui.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/16/2015 for PunismentGUI.
 */
public class PunishmentChecker implements Listener
{
	public PunishmentChecker(JavaPlugin plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		
		if (file.hasInfraction(PunishType.TEMP_BAN, PunishType.PERM_BAN))
		{
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			
			if (file.hasInfraction(PunishType.PERM_BAN))
			{
				Infraction infraction = file.getLatestInfraction(PunishType.PERM_BAN);
				
				if (infraction != null)
				{
					event.setKickMessage(Message.LOGIN_PERM_BAN.replace("%reason%", infraction.getReason()).replace("%punisher%", infraction.getGivenBy()));
				} else
				{
					event.setKickMessage(Message.PERM_BAN_UNKNOWN.toString());
				}
			} else
			{
				Infraction infraction = file.getLatestInfraction(PunishType.TEMP_BAN);
				
				if (infraction != null)
				{
					event.setKickMessage(Message.LOGIN_TEMP_BAN.replace("%date%", file.getExpiration(infraction.getType())).replace("%reason%", infraction.getReason()).replace("%punisher%", infraction.getGivenBy()));
				} else
				{
					event.setKickMessage(Message.TEMP_BAN_UNKNOWN.toString());
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(e.getPlayer().getUniqueId());
		
		if (file.hasInfraction(PunishType.PERM_MUTE, PunishType.TEMP_MUTE))
		{
			e.setCancelled(true);
			
			if (file.isPunishmentActive(PunishType.TEMP_MUTE))
			{
				Infraction infraction = file.getLatestInfraction(PunishType.TEMP_MUTE);
				
				if (infraction != null)
				{
					Util.sendMessage(Message.TEMP_MUTED.replace("%date%", file.getExpiration(PunishType.TEMP_MUTE)).replace("%reason%", infraction.getReason()).replace("%punisher%", infraction.getGivenBy()), e.getPlayer());
					Util.sendSuffix(e.getPlayer());
				}
			} else if (file.isPunishmentActive(PunishType.PERM_MUTE))
			{
				Infraction infraction = file.getLatestInfraction(PunishType.PERM_MUTE);
				
				if (infraction != null)
				{
					Util.sendMessage(Message.PERM_MUTED.toString().replace("%reason%", infraction.getReason()).replace("%punisher%", infraction.getGivenBy()), e.getPlayer());
					Util.sendSuffix(e.getPlayer());
				}
			}
		}
	}
}
