package me.chiller.punishmentgui.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.resources.Message;
import me.chiller.punishmentgui.resources.Time;
import me.chiller.punishmentgui.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/7/2015 for PunishmentGUI.
 */
public class PlayerFile
{
	private UUID id;
	private String ip;
	
	private File file;
	private YamlConfiguration config;
	
	private static final String HISTORY_FIELD = "history";
	private static final String NUM_OF_PREVIOUS_MUTES = "offenses.mutes";
	private static final String NUM_OF_PREVIOUS_BANS = "offenses.bans";
	
	private Map<Long, Infraction> infractionHistory;
	
	private int infractions;
	private long banExpiration, muteExpiration;
	
	private BukkitRunnable banRunnable, muteRunnable;
	
	public PlayerFile(UUID id, File file)
	{
		this.id = id;
		this.config = YamlConfiguration.loadConfiguration(file);
		this.file = file;
		
		ip = config.getString("ip_address", "");
		infractions = config.getInt("current_infractions", 0);
		banExpiration = config.getLong("expiration.ban", 0L);
		muteExpiration = config.getLong("expiration.mute", 0L);
		
		if (System.currentTimeMillis() < banExpiration)
		{
			createRunnable(PunishType.TEMP_BAN);
		} else if (banExpiration != 0L)
		{
			config.set("expiration.ban", banExpiration = 0L);
		}
		
		if (System.currentTimeMillis() < muteExpiration)
		{
			createRunnable(PunishType.TEMP_MUTE);
		} else if (muteExpiration != 0L)
		{
			config.set("expiration.mute", muteExpiration = 0L);
		}
		
		infractionHistory = obtainInfractionHistory();
	}
	
	private void createRunnable(PunishType type)
	{
		if (type == PunishType.TEMP_BAN)
		{
			banRunnable = new BukkitRunnable()
			{
				public void run()
				{
					setPunishmentActivity(PunishType.TEMP_BAN, false);
				}
			};
			
			banRunnable.runTaskLater(Main.getInstance(), (banExpiration - System.currentTimeMillis()) / 1000 * 20);
		} else
		{
			muteRunnable = new BukkitRunnable()
			{
				public void run()
				{
					setPunishmentActivity(PunishType.TEMP_MUTE, false);
					
					OfflinePlayer player = Bukkit.getOfflinePlayer(id);
					
					if (player.isOnline())
					{
						Util.sendMessage(Message.UNMUTE.toString(), (Player) player);
					}
				}
			};
			
			muteRunnable.runTaskLater(Main.getInstance(), (muteExpiration - System.currentTimeMillis()) / 1000 * 20);
		}
	}
	
	public boolean hasInfraction(PunishType... types)
	{
		for (PunishType type : types)
		{
			if ((infractions & type.getOrdinal()) == type.getOrdinal())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Infraction getLatestInfraction(PunishType type)
	{
		for (Infraction infract : getInfractionHistory())
		{
			if (infract.getType() == type)
			{
				return infract;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets whether or not a punishment is active. Is only applicable to
	 * punishments that have a time constraint. (Not WARNING)
	 * 
	 * @param type
	 *            PunishmentType to check if active
	 * @return true if punishment is currently active, false if not.
	 */
	public boolean isPunishmentActive(PunishType type)
	{
		switch (type)
		{
			case TEMP_BAN:
				return banExpiration > System.currentTimeMillis();
			case TEMP_MUTE:
				return muteExpiration > System.currentTimeMillis();
			case PERM_BAN:
			case PERM_MUTE:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Gets the number of milliseconds until the temporary punishment expires
	 * 
	 * @param type
	 *            any temporary punish type
	 * @return returns the number of milliseconds at which the given punishment
	 *         ends. Types that do not end or are NA will return 0
	 */
	public String getExpiration(PunishType type)
	{
		if (!isPunishmentActive(type))
			return "";
			
		long expiration = type == PunishType.TEMP_BAN ? banExpiration : muteExpiration;
		
		return Util.getFormattedDate(expiration);
	}
	
	private Map<Long, Infraction> obtainInfractionHistory()
	{
		Map<Long, Infraction> infractions = new HashMap<Long, Infraction>();
		ConfigurationSection history = config.getConfigurationSection(HISTORY_FIELD);
		
		if (history != null)
		{
			for (String key : history.getKeys(false))
			{
				Infraction infraction = (Infraction) history.get(key);
				infraction.setDate(Long.parseLong(key));
				
				infractions.put(infraction.getDate(), infraction);
			}
		}
		
		return infractions;
	}
	
	public List<Infraction> getInfractionHistory()
	{
		List<Infraction> infracts = new ArrayList<Infraction>(infractionHistory.values());
		
		Collections.sort(infracts);
		Collections.reverse(infracts);
		
		return infracts;
	}
	
	/**
	 * Sets the acive state of a punishment. Is not required for warnings.
	 * 
	 * @param type
	 *            punishment type to change activity of
	 * @param active
	 *            activity state to set to
	 * @return whether the activity state was successfully changed to var
	 *         active. Will return true if the activity state was the same as
	 *         var active.
	 */
	public void setPunishmentActivity(PunishType type, boolean active)
	{
		setPunishmentActivity(type, active, null, null);
	}
	
	public void setPunishmentActivity(PunishType type, boolean active, Player remover, String reason)
	{
		if (type.isTemp())
		{
			if (active)
			{
				// activate
				if (!hasInfraction(type))
				{
					setExpiration(type, type == PunishType.TEMP_BAN ? Time.BAN.getTime(getNumOfInfractions(type)) : Time.MUTE.getTime(getNumOfInfractions(type)));
					incrementNumOfInfractions(type);
					
					// Create runnable
					createRunnable(type);
				}
			} else
			{
				// deactivate
				if (hasInfraction(type))
				{
					setExpiration(type, 0);
					
					if (type == PunishType.TEMP_BAN)
					{
						banRunnable.cancel();
					} else if (type == PunishType.TEMP_MUTE)
					{
						muteRunnable.cancel();
					}
				}
			}
		}
		
		if (active)
		{
			infractions |= type.getOrdinal();
		} else
		{
			infractions &= ~type.getOrdinal();
			
			unActivateInfraction(type, remover, reason);
		}
		
		saveAll();
	}
	
	private void setExpiration(PunishType type, long expiration)
	{
		if (type == PunishType.TEMP_BAN)
			banExpiration = expiration;
		else
			muteExpiration = expiration;
	}
	
	public int getNumOfInfractions(PunishType type)
	{
		if (type == PunishType.TEMP_BAN)
			return config.getInt(NUM_OF_PREVIOUS_BANS, 0);
		else
			return config.getInt(NUM_OF_PREVIOUS_MUTES, 0);
	}
	
	private void incrementNumOfInfractions(PunishType type)
	{
		if (type == PunishType.TEMP_BAN)
			config.set(NUM_OF_PREVIOUS_BANS, config.getInt(NUM_OF_PREVIOUS_BANS, 0) + 1);
		else
			config.set(NUM_OF_PREVIOUS_MUTES, config.getInt(NUM_OF_PREVIOUS_MUTES, 0) + 1);
	}
	
	public void saveInfraction(Infraction infraction)
	{
		config.set(HISTORY_FIELD + "." + infraction.getDate(), infraction);
		infractionHistory.put(infraction.getDate(), infraction);
		
		setPunishmentActivity(infraction.getType(), true);
		saveAll();
	}
	
	public void unActivateInfraction(PunishType type, Player remover, String reason)
	{
		for (Infraction inf : infractionHistory.values())
		{
			if (inf.getType() == type && inf.isActive())
			{
				inf.setActive(false);
				
				if (remover != null)
				{
					inf.setRemovedBy(remover.getName());
					inf.setRemoveReason(reason);
				}
			}
		}
		
		saveAll();
	}
	
	public void saveAll()
	{
		config.set("history", infractionHistory);
		
		config.set("current_infractions", infractions);
		config.set("expiration.ban", banExpiration);
		config.set("expiration.mute", muteExpiration);
		
		save();
	}
	
	public void save()
	{
		try
		{
			config.save(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public OfflinePlayer getPlayer()
	{
		return Bukkit.getOfflinePlayer(id);
	}
	
	public UUID getUUID()
	{
		return id;
	}
	
	public void setIp(String ip)
	{
		config.set("ip_address", this.ip = ip);
		
		save();
	}
	
	public String getIp()
	{
		return ip;
	}
	
	public boolean hasAnyInfraction()
	{
		return infractions != 0;
	}
	
	public void clearCurrentInfractions()
	{
		infractions = 0;
		
		Iterator<Map.Entry<Long, Infraction>> iter = infractionHistory.entrySet().iterator();
		
		while (iter.hasNext())
		{
			Map.Entry<Long, Infraction> entry = iter.next();
			
			if (entry.getValue().isActive())
			{
				iter.remove();
			}
		}
		
		saveAll();
	}
}
