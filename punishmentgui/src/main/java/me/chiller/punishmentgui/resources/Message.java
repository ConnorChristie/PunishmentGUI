package me.chiller.punishmentgui.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import me.chiller.punishmentgui.core.Main;

@SuppressWarnings("unchecked")
public enum Message
{
	MESSAGE_PREFIX("{message_prefix}", "&c&lPUNISH&r",                                   Type.PREFIX_SUFFIX),
	MESSAGE_SUFFIX("{message_suffix}", "&aUnfairly punished? Contact us on the forums!", Type.PREFIX_SUFFIX),
	
	WARN(     "", "{message_prefix} &4You have been warned &6({reason}) &4by &6{punisher}",                                  Type.MESSAGE),
	PERM_BAN( "", "{message_prefix} &4You have been permanently banned &6({reason}) &4by &6{punisher}#n{message_suffix}",    Type.MESSAGE),
	PERM_MUTE("", "{message_prefix} &4You have been permanently muted &6({reason}) &4by &6{punisher}",                       Type.MESSAGE),
	TEMP_BAN( "", "{message_prefix} &4You have been banned until &b{expiration} &6({reason}) &4by &6{punisher}#n{message_suffix}", Type.MESSAGE),
	TEMP_MUTE("", "{message_prefix} &4You have been temporarily muted &6({reason}) &4by &6{punisher}",                       Type.MESSAGE),
	
	UNMUTE(    "", "{message_prefix} &aYou are no longer muted.",                                        Type.MESSAGE),
	PERM_MUTED("", "{message_prefix} &4You are permanently muted for &6{reason} &4by &3{punisher}",      Type.MESSAGE),
	TEMP_MUTED("", "{message_prefix} &4You are muted until &b{expiration} &4for &6{reason} &4by &3{punisher}", Type.MESSAGE),
	
	LOGIN_PERM_BAN("", "{message_prefix} &4You have been permanently banned &6({reason}) &4by &6{punisher}#n{message_suffix}",    Type.MESSAGE),
	LOGIN_TEMP_BAN("", "{message_prefix} &4You have been banned until &b{expiration} &6({reason}) &4by &6{punisher}#n{message_suffix}", Type.MESSAGE),
	
	NOT_PUNISHABLE(  "", "{message_prefix} &4You are not allowed to punish &6{punished}",                                      Type.MESSAGE),
	PERM_BAN_UNKNOWN("", "{message_prefix} &4You have been banned for an unknown reason#n{message_suffix}",                    Type.MESSAGE),
	TEMP_BAN_UNKNOWN("", "{message_prefix} &4You have been banned for an unknown reason at an unknown time#n{message_suffix}", Type.MESSAGE),
	
	MOTD_PERM_BAN( "", "&4You are permanently banned by &3{punisher} &4for &6{reason}",      Type.MESSAGE),
	MOTD_PERM_MUTE("", "&4You are permanently muted by &3{punisher} &4for &6{reason}",       Type.MESSAGE),
	MOTD_TEMP_BAN( "", "&4You are banned until &b{expiration} &4by &3{punisher} &4for &6{reason}", Type.MESSAGE),
	MOTD_TEMP_MUTE("", "&4You are muted until &b{expiration} &4by &3{punisher} &4for &6{reason}",  Type.MESSAGE),
	
	MOTD_BAN_UNKNOWN( "", "&4You have been banned for an unknown reason", Type.MESSAGE),
	MOTD_MUTE_UNKNOWN("", "&4You have been muted for an unknown reason",  Type.MESSAGE),
	
	HISTORY(           "", new String[] { "&6Reason: &c{reason}", "&6Given by: &c{punisher}", "&6Date: &c{date}", "&6Expiration: &c{expiration}" }, Type.LORE),
	HISTORY_REMOVED_BY("", new String[] { "", "&6Removed by: &c{remover}", "&6Removed reason: &c{remove_reason}" },                                 Type.LORE);
	
	static
	{
		//Fix lore, multi line
		
		FileConfiguration config = Main.getInstance().getConfig();
		
		for (Message message : values())
		{
			if (!config.contains(message.getKey()))
			{
				if (message.type == Type.LORE)
					config.set(message.getKey(), message.defList);
				else
					config.set(message.getKey(), message.def);
			}
			
			if (message.type == Type.LORE)
			{
				Object configMessage = config.get(message.getKey());
				
				if (configMessage instanceof String)
				{
					//Replace with list
					
					config.set(message.getKey(), Arrays.asList(((String) configMessage).split("#n")));
					message.list = message.defList;
				} else
				{
					message.list = (List<String>) configMessage;
				}
			} else
			{
				String value = config.getString(message.getKey());
				String updatedValue = value.contains("%") ? value.replaceAll("%(.*?)%", "{$1}") : value;
				
				message.value = ChatColor.translateAlternateColorCodes('&', updatedValue);
				if (message.type != Type.LORE) message.value = message.value.replace("#n", "\n");
				
				if (!value.equals(updatedValue))
				{
					config.set(message.getKey(), updatedValue);
				}
			}
		}
		
		for (Message message : values())
		{
			for (Message replacement : values())
			{
				if (replacement.getReplacement().isEmpty()) continue;
				
				if (message.list != null)
				{
					List<String> replaced = new ArrayList<String>();
					
					for (String msg : message.list)
					{
						replaced.add(ChatColor.translateAlternateColorCodes('&', msg.replace(replacement.getReplacement(), replacement.value)));
					}
					
					message.list = replaced;
				} else
				{
					message.value = message.value.replace(replacement.getReplacement(), replacement.value);
				}
			}
		}
		
		Main.getInstance().saveConfig();
	}
	
	protected String replacement;
	protected String def;
	protected String value;
	
	protected List<String> defList;
	protected List<String> list;
	
	protected Type type;
	
	private Message(String replacement, String def, Type type)
	{
		this.replacement = replacement;
		this.def = def;
		this.type = type;
	}
	
	private Message(String replacement, String[] defList, Type type)
	{
		this.replacement = replacement;
		this.defList = Arrays.asList(defList);
		this.type = type;
	}
	
	public String getKey()
	{
		return type.getKey() + name().toLowerCase();
	}
	
	public String getReplacement()
	{
		return replacement;
	}
	
	public List<String> getList()
	{
		return list;
	}
	
	public String toString()
	{
		return value;
	}
	
	public String replace(String replace, String replacement)
	{
		return toString().replace(replace, replacement);
	}
	
	private enum Type
	{
		PREFIX_SUFFIX(""),
		MESSAGE(      "messages."),
		LORE(         "lore.");
		
		private String key;
		
		private Type(String key)
		{
			this.key = key;
		}
		
		public String getKey()
		{
			return key;
		}
	}
}