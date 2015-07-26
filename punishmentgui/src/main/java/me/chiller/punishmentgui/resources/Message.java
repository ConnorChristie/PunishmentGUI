package me.chiller.punishmentgui.resources;

import org.bukkit.ChatColor;

import me.chiller.punishmentgui.core.Main;

public enum Message
{
	MESSAGE_PREFIX("%message_prefix%", "&c&lPUNISH&r",                                   Type.PREFIX_SUFFIX),
	MESSAGE_SUFFIX("%message_suffix%", "&aUnfairly punished? Contact us on the forums!", Type.PREFIX_SUFFIX),
	
	WARN(     "", "%message_prefix% &4You have been warned &6(%reason%) &4by &6%punisher%",                                  Type.MESSAGE),
	PERM_BAN( "", "%message_prefix% &4You have been permanantly banned &6(%reason%) &4by &6%punisher%#n%message_suffix%",    Type.MESSAGE),
	PERM_MUTE("", "%message_prefix% &4You have been permanantly muted &6(%reason%) &4by &6%punisher%",                       Type.MESSAGE),
	TEMP_BAN( "", "%message_prefix% &4You have been banned until &b%date% &6(%reason%) &4by &6%punisher%#n%message_suffix%", Type.MESSAGE),
	TEMP_MUTE("", "%message_prefix% &4You have been temporarily muted &6(%reason%) &4by &6%punisher%",                       Type.MESSAGE),
	
	UNMUTE(    "", "%message_prefix% &aYou are no longer muted.",                                        Type.MESSAGE),
	PERM_MUTED("", "%message_prefix% &4You are permanently muted for &6%reason% &4by &3%punisher%",      Type.MESSAGE),
	TEMP_MUTED("", "%message_prefix% &4You are muted until &b%date% &4for &6%reason% &4by &3%punisher%", Type.MESSAGE),
	
	LOGIN_PERM_BAN("", "%message_prefix% &4You have been permanantly banned &6(%reason%) &4by &6%punisher%#n%message_suffix%",    Type.MESSAGE),
	LOGIN_TEMP_BAN("", "%message_prefix% &4You have been banned until &b%date% &6(%reason%) &4by &6%punisher%#n%message_suffix%", Type.MESSAGE),
	
	NOT_PUNISHABLE(  "", "%message_prefix% &4You are not allowed to punish &6%punished%",                                      Type.MESSAGE),
	PERM_BAN_UNKNOWN("", "%message_prefix% &4You have been banned for an unknown reason#n%message_suffix%",                    Type.MESSAGE),
	TEMP_BAN_UNKNOWN("", "%message_prefix% &4You have been banned for an unknown reason at an unknown time#n%message_suffix%", Type.MESSAGE),
	
	HISTORY(           "", "&6Reason: &c%reason%#n&6Given by: &c%punisher%#n&6Date: &c%date%", Type.LORE),
	HISTORY_REMOVED_BY("", "#n&6Removed by: &c%remover%#n&6Removed reason: &c%remove_reason%", Type.LORE);
	
	static
	{
		for (Message message : values())
		{
			message.value = ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString(message.type.getKey() + message.getKey(), message.def));
			
			if (message.type != Type.LORE) message.value = message.value.replace("#n", "\n");
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
	
	protected Type type;
	
	private Message(String replacement, String def, Type type)
	{
		this.replacement = replacement;
		this.def = def;
		this.type = type;
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