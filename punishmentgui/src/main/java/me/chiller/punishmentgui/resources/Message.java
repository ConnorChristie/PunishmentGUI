package me.chiller.punishmentgui.resources;

import org.bukkit.ChatColor;

import me.chiller.punishmentgui.core.Main;

public enum Message
{
	MESSAGE_PREFIX("%message_prefix%", "&c&lPUNISH&r"),
	MESSAGE_SUFFIX("%message_suffix%", "&aUnfairly punished? Contact us on the forums!"),
	
	WARN(     "", "%message_prefix% &4You have been warned &6(%reason%) &4by &6%punisher%"),
	PERM_BAN( "", "%message_prefix% &4You have been permanantly banned &6(%reason%) &4by &6%punisher%#n%message_suffix%"),
	PERM_MUTE("", "%message_prefix% &4You have been permanantly muted &6(%reason%) &4by &6%punisher%"),
	TEMP_BAN( "", "%message_prefix% &4You have been banned until &b%date% &6(%reason%) &4by &6%punisher%#n%message_suffix%"),
	TEMP_MUTE("", "%message_prefix% &4You have been temporarily muted &6(%reason%) &4by &6%punisher%"),
	
	UNMUTE(    "", "%message_prefix% &aYou are no longer muted."),
	PERM_MUTED("", "%message_prefix% &4You are permanently muted for &6%reason% &4by &3%punisher%"),
	TEMP_MUTED("", "%message_prefix% &4You are muted until &b%date% &4for &6%reason% &4by &3%punisher%"),
	
	LOGIN_PERM_BAN("", "%message_prefix% &4You have been permanantly banned &6(%reason%) &4by &6%punisher%#n%message_suffix%"),
	LOGIN_TEMP_BAN("", "%message_prefix% &4You have been banned until &b%date% &6(%reason%) &4by &6%punisher%#n%message_suffix%");
	
	static
	{
		for (Message message : values())
		{
			message.value = ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString((message.name().contains("MESSAGE") ? "" : "messages.") + message.getKey(), message.def)).replace("#n", "\n");
		}
		
		for (Message message : values())
		{
			for (Message replacement : values())
			{
				if (replacement.getReplacement().isEmpty()) continue;
				
				message.value = message.value.replace(replacement.getReplacement(), replacement.value);
			}
		}
	}
	
	protected String replacement;
	protected String def;
	protected String value;
	
	private Message(String replacement, String def)
	{
		this.replacement = replacement;
		this.def = def;
	}
	
	public String getKey()
	{
		return name().toLowerCase();
	}
	
	public String getReplacement()
	{
		return replacement;
	}
	
	public String toString()
	{
		return value;
	}
	
	public String replace(String replace, String replacement)
	{
		return toString().replace(replace, replacement);
	}
}