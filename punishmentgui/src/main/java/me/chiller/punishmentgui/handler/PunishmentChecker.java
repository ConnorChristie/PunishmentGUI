package me.chiller.punishmentgui.handler;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;
import me.chiller.punishmentgui.resources.Message;
import me.chiller.punishmentgui.resources.Permission;
import me.chiller.punishmentgui.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/16/2015 for PunismentGUI.
 */
public class PunishmentChecker implements Listener
{
	private List<UUID> kickedUUIDs = new ArrayList<UUID>();
	
	public PunishmentChecker()
	{
		Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event)
	{
		if (Main.getInstance().getConfig().getBoolean("motd_infraction_status", true))
		{
			PlayerFile file = Main.getInstance().getPlayerFile(event.getAddress().getHostAddress());
			
			if (file != null)
			{
				if (file.hasInfraction(PunishType.PERM_BAN, PunishType.TEMP_BAN))
				{
					if (file.hasInfraction(PunishType.PERM_BAN))
					{
						Infraction infraction = file.getLatestInfraction(PunishType.PERM_BAN);
						
						if (infraction != null)
							event.setMotd(Message.MOTD_PERM_BAN.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()));
						else
							event.setMotd(Message.MOTD_BAN_UNKNOWN.toString());
					} else if (file.isPunishmentActive(PunishType.TEMP_BAN))
					{
						Infraction infraction = file.getLatestInfraction(PunishType.TEMP_BAN);
						
						if (infraction != null)
							event.setMotd(Message.MOTD_TEMP_BAN.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()).replace("{date}", infraction.getDateString()).replace("{expiration}", file.getExpiration(infraction.getType())));
						else
							event.setMotd(Message.MOTD_BAN_UNKNOWN.toString());
					}
				} else if (file.hasInfraction(PunishType.PERM_MUTE, PunishType.TEMP_MUTE))
				{
					if (file.hasInfraction(PunishType.PERM_MUTE))
					{
						Infraction infraction = file.getLatestInfraction(PunishType.PERM_MUTE);
						
						if (infraction != null)
							event.setMotd(Message.MOTD_PERM_MUTE.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()));
						else
							event.setMotd(Message.MOTD_MUTE_UNKNOWN.toString());
					} else if (file.isPunishmentActive(PunishType.TEMP_MUTE))
					{
						Infraction infraction = file.getLatestInfraction(PunishType.TEMP_MUTE);
						
						if (infraction != null)
							event.setMotd(Message.MOTD_TEMP_MUTE.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()).replace("{date}", infraction.getDateString()).replace("{expiration}", file.getExpiration(infraction.getType())));
						else
							event.setMotd(Message.MOTD_MUTE_UNKNOWN.toString());
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		
		file.setIp(player.getAddress().getHostString());
		
		if (file.hasAnyInfraction() && player.hasPermission(Permission.PUNISH_PROTECTED.toString()))
		{
			file.clearCurrentInfractions();
			
			return;
		}
		
		if (file.hasInfraction(PunishType.TEMP_BAN, PunishType.PERM_BAN))
		{
			if (file.hasInfraction(PunishType.PERM_BAN))
			{
				Infraction infraction = file.getLatestInfraction(PunishType.PERM_BAN);
				
				if (infraction != null)
				{
					kickPlayer(event, Message.LOGIN_PERM_BAN.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()), PunishType.PERM_BAN);
				} else
				{
					kickPlayer(event, Message.PERM_BAN_UNKNOWN.toString(), PunishType.PERM_BAN);
				}
			} else
			{
				Infraction infraction = file.getLatestInfraction(PunishType.TEMP_BAN);
				
				if (infraction != null)
				{
					kickPlayer(event, Message.LOGIN_TEMP_BAN.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()).replace("{date}", infraction.getDateString()).replace("{expiration}", file.getExpiration(infraction.getType())), PunishType.TEMP_BAN);
				} else
				{
					kickPlayer(event, Message.TEMP_BAN_UNKNOWN.toString(), PunishType.TEMP_BAN);
				}
			}
		}
	}
	
	private void kickPlayer(final PlayerJoinEvent event, final String message, final PunishType type)
	{
		kickedUUIDs.add(event.getPlayer().getUniqueId());
		
		event.setJoinMessage(null);
		event.getPlayer().kickPlayer(message);
		
		new BukkitRunnable()
		{
			public void run()
			{
				Main.getInstance().getLogger().info(event.getPlayer().getName() + " has been kicked: " + type.getPlural());
			}
		}.runTaskLater(Main.getInstance(), 5);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		UUID uuid = event.getPlayer().getUniqueId();
		
		if (kickedUUIDs.contains(uuid))
		{
			event.setQuitMessage(null);
			
			kickedUUIDs.remove(uuid);
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		//event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "Banned fool");
		
		/*
		Player player = event.getPlayer();
		PlayerFile file = Main.getInstance().getPlayerFile(player.getUniqueId());
		
		if (file.hasAnyInfraction() && player.hasPermission(Permission.PUNISH_PROTECTED.toString()))
		{
			file.clearCurrentInfractions();
			
			return;
		}
		
		if (file.hasInfraction(PunishType.TEMP_BAN, PunishType.PERM_BAN))
		{
			//event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			
			if (file.hasInfraction(PunishType.PERM_BAN))
			{
				Infraction infraction = file.getLatestInfraction(PunishType.PERM_BAN);
				
				if (infraction != null)
				{
					event.setKickMessage(Message.LOGIN_PERM_BAN.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()));
				} else
				{
					event.setKickMessage(Message.PERM_BAN_UNKNOWN.toString());
				}
			} else
			{
				Infraction infraction = file.getLatestInfraction(PunishType.TEMP_BAN);
				
				if (infraction != null)
				{
					event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Message.LOGIN_TEMP_BAN.replace("{date}", file.getExpiration(infraction.getType())).replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()));
					
					//event.setKickMessage(Message.LOGIN_TEMP_BAN.replace("{date}", file.getExpiration(infraction.getType())).replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()));
				} else
				{
					event.setKickMessage(Message.TEMP_BAN_UNKNOWN.toString());
				}
			}
		}
		*/
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e)
	{
		// Check if !e.isCancelled()
		
		PlayerFile file = Main.getInstance().getPlayerFile(e.getPlayer().getUniqueId());
		
		if (file.hasInfraction(PunishType.PERM_MUTE, PunishType.TEMP_MUTE))
		{
			e.setCancelled(true);
			
			if (file.isPunishmentActive(PunishType.TEMP_MUTE))
			{
				Infraction infraction = file.getLatestInfraction(PunishType.TEMP_MUTE);
				
				if (infraction != null)
				{
					Util.sendMessage(Message.TEMP_MUTED.replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()).replace("{date}", infraction.getDateString()).replace("{expiration}", file.getExpiration(PunishType.TEMP_MUTE)), e.getPlayer());
					Util.sendSuffix(e.getPlayer());
				}
			} else if (file.isPunishmentActive(PunishType.PERM_MUTE))
			{
				Infraction infraction = file.getLatestInfraction(PunishType.PERM_MUTE);
				
				if (infraction != null)
				{
					Util.sendMessage(Message.PERM_MUTED.toString().replace("{reason}", infraction.getReason()).replace("{punisher}", infraction.getGivenBy()), e.getPlayer());
					Util.sendSuffix(e.getPlayer());
				}
			}
		}
	}
}
