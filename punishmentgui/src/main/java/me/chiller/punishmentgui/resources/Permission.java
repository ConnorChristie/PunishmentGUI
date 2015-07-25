package me.chiller.punishmentgui.resources;

public enum Permission
{
	PUNISH_USE("punish.use"),
	TEMP_MUTE("punish.temp_mute"),
	TEMP_BAN("punish.temp_ban"),
	PERM_MUTE("punish.perm_mute"),
	PERM_BAN("punish.perm_ban"),
	PROTECTED("punish.protected");
	
	private String perm;
	
	private Permission(String perm)
	{
		this.perm = perm;
	}
	
	public String toString()
	{
		return perm;
	}
}