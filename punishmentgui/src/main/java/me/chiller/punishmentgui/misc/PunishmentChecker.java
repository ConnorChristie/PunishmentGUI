package me.chiller.punishmentgui.misc;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.core.Resources;
import me.chiller.punishmentgui.core.Resources.Messages;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ethan Zeigler on 7/16/2015 for PunismentGUI.
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
			
			Infraction infraction = null;
			
			for (Infraction frac : file.getInfractionHistory())
			{
				if (frac.getType() == PunishType.TEMP_BAN || frac.getType() == PunishType.PERM_BAN)
				{
					infraction = frac;
					
					break;
				}
			}
			
			if (infraction == null)
			{
				event.setKickMessage("You have been banned for an unknown reason and an undeclared time.\n" + Messages.MESSAGE_SUFFIX);
				
				return;
			}
			
			if (!infraction.getType().isTemp())
			{
				event.setKickMessage(Messages.LOGIN_PERM_BAN.replace("%reason%", infraction.getReason()));
			} else
			{
				event.setKickMessage(Messages.LOGIN_TEMP_BAN.replace("%date%", file.getExpiration(infraction.getType())).replace("%reason%", infraction.getReason()));
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		PlayerFile file = Main.getInstance().getPlayerFile(e.getPlayer().getUniqueId());
		
		if (file.hasInfraction(PunishType.PERM_MUTE, PunishType.TEMP_MUTE))
		{
			e.setCancelled(true);
			
			if (file.isPunishmentActive(PunishType.TEMP_MUTE))
			{
				Resources.sendMessage(Messages.TEMP_MUTED.replace("%date%", file.getExpiration(PunishType.TEMP_MUTE)), e.getPlayer());
				Resources.sendMessage(Messages.MESSAGE_PREFIX.toString() + " " + Messages.MESSAGE_SUFFIX.toString(), e.getPlayer());
			} else if (file.isPunishmentActive(PunishType.PERM_MUTE))
			{
				Resources.sendMessage(Messages.PERM_MUTED.toString(), e.getPlayer());
				Resources.sendMessage(Messages.MESSAGE_PREFIX.toString() + " " + Messages.MESSAGE_SUFFIX.toString(), e.getPlayer());
			}
		}
	}
}
