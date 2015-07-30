package me.chiller.punishmentgui.core;

import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.external.Metrics;
import me.chiller.punishmentgui.external.Updater;
import me.chiller.punishmentgui.external.Updater.UpdateType;
import me.chiller.punishmentgui.handler.PunishmentChecker;
import me.chiller.punishmentgui.invgui.GUIClickListener;
import me.chiller.punishmentgui.invgui.GUIConstructor;
import me.chiller.punishmentgui.resources.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ethan Zeigler, edited by Chiller on 6/27/2015 for ${PROJECT_NAME}.
 */
public class Main extends JavaPlugin
{
	private static Main instance;
	
	private File playersDir;
	
	private Map<UUID, PlayerFile> playerFiles = new HashMap<UUID, PlayerFile>();
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		setUpConfig();
		
		runUpdater();
		runPluginMetrics();
		
		loadPlayerFiles();
		
		registerCommands();
		registerListeners();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	private void runUpdater()
	{
		new Updater(this, 93800, getFile(), UpdateType.DEFAULT, true);
	}
	
	private void runPluginMetrics()
	{
		try
		{
			Metrics pm = new Metrics(this);
			
			boolean didMetricsLoad = pm.start();
			
			if (!didMetricsLoad)
			{
				getLogger().info("Plugin metrics is disabled. This will not affect the performance of PunishmentGUI.");
			}
		} catch (IOException e) { }
	}
	
	public PlayerFile getPlayerFile(UUID uuid)
	{
		if (playerFiles.containsKey(uuid))
		{
			return playerFiles.get(uuid);
		}
		
		File file = new File(playersDir, uuid.toString() + ".yml");
		try
		{
			file.createNewFile();
		} catch (IOException e) { }
		
		PlayerFile playerFile = new PlayerFile(uuid, file);
		playerFiles.put(uuid, playerFile);
		
		return playerFile;
	}
	
	public PlayerFile getPlayerFile(String ip)
	{
		for (PlayerFile file : playerFiles.values())
		{
			if (file.getIp().equals(ip))
			{
				return file;
			}
		}
		
		return null;
	}
	
	private void registerCommands()
	{
		new GUIConstructor(this);
	}
	
	private void registerListeners()
	{
		new GUIClickListener(this);
		new PunishmentChecker();
	}
	
	private void loadPlayerFiles()
	{
		playersDir = new File(getDataFolder(), "Players");
		playersDir.mkdirs();
		
		for (String fileName : playersDir.list())
		{
			File file = new File(playersDir, fileName);
			UUID playerUUID = UUID.fromString(file.getName().replace(".yml", ""));
			
			PlayerFile playerFile = new PlayerFile(playerUUID, file);
			
			playerFiles.put(playerUUID, playerFile);
		}
		
		getLogger().info("Loaded " + playerFiles.size() + " player" + (playerFiles.size() == 1 ? "" : "s") + "!");
	}
	
	private void setUpConfig()
	{
		getConfig().options().header("Time increments are in seconds");
		getConfig().options().copyDefaults(true);
		
		updateConfig();
		saveConfig();
		
		ConfigurationSerialization.registerClass(Infraction.class);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void updateConfig()
	{
		if (!getConfig().contains("motd_infraction_status"))
		{
			getConfig().set("motd_infraction_status", true);
		}
		
		Set<String> keys = getConfig().getKeys(true);
		boolean containsExpiration = false;
		
		for (String key : keys)
		{
			Object obj = getConfig().get(key);
			
			if (obj instanceof String)
			{
				if (((String) obj).contains("{expiration}"))
				{
					containsExpiration = true;
					
					break;
				}
			} else if (obj instanceof List)
			{
				for (String str : (List<String>) obj)
				{
					if (str.contains("{expiration}"))
					{
						containsExpiration = true;
						
						break;
					}
				}
			}
		}
		
		if (!containsExpiration)
		{
			for (String key : keys)
			{
				Object obj = getConfig().get(key);
				
				if (obj instanceof String)
				{
					if (((String) obj).contains("{date}"))
					{
						getConfig().set(key, ((String) obj).replace("{date}", "{expiration}"));
					}
				} else if (obj instanceof List)
				{
					List<String> replacement = new ArrayList<String>();
					
					for (String str : (List<String>) obj)
					{
						if (str.contains("{date}"))
						{
							str.replace("{date}", "{expiration}");
						}
						
						replacement.add(str);
					}
					
					getConfig().set(key, replacement);
				}
			}
		}
		
		if (getConfig().get("lore.history") instanceof String)
		{
			getConfig().set("lore.history", Arrays.asList(new String[] { "&6Reason: &c{reason}", "&6Given by: &c{punisher}", "&6Date: &c{date}", "&6Expiration: &c{expiration}" }));
		}
		
		Message msg = Message.WARN; //Instantiate static
		
		//All other messages that are not already in the config update in the Message class
	}
	
	public static Main getInstance()
	{
		return instance;
	}
}