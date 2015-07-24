package me.chiller.punishmentgui.core;

import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.invgui.GUIClickListener;
import me.chiller.punishmentgui.invgui.GUIConstructor;
import me.chiller.punishmentgui.misc.PunishmentChecker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ethan Zeigler on 6/27/2015 for ${PROJECT_NAME}.
 */
public class Main extends JavaPlugin
{
	private static Main instance;
	
	private File playersDir;
	private GUIConstructor constructor;
	
	private Map<UUID, PlayerFile> playerFiles;
	
	@Override
	public void onEnable()
	{
		instance = this;
		playerFiles = new HashMap<UUID, PlayerFile>();
		
		setUpConfig();
		loadPlayerFiles();
		
		registerCommands();
		registerListeners();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	public PlayerFile getPlayerFile(UUID uuid)
	{
		if (playerFiles.containsKey(uuid))
		{
			return playerFiles.get(uuid);
		}
		
		File file = new File(playersDir, uuid.toString() + ".yml");
		try { file.createNewFile(); } catch (IOException e) { }
		
		PlayerFile playerFile = new PlayerFile(uuid, file);
		playerFiles.put(uuid, playerFile);
		
		return playerFile;
	}
	
	private void registerCommands()
	{
		constructor = new GUIConstructor(this);
	}
	
	private void registerListeners()
	{
		new GUIClickListener(this);
		new PunishmentChecker(this);
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
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		ConfigurationSerialization.registerClass(Infraction.class);
	}
	
	public static Main getInstance()
	{
		return instance;
	}
}