package me.chiller.punishmentgui.resources;

import me.chiller.punishmentgui.core.Main;

public enum Time
{
	BAN("ban_increment",   240),
	MUTE("mute_increment", 120);
	
	static
	{
		for (Time time : values())
		{
			time.time = Main.getInstance().getConfig().getInt("time." + time.key, time.time);
		}
	}
	
	protected String key;
	protected int time;
	
	private Time(String key, int time)
	{
		this.key = key;
		this.time = time;
	}
	
	// Increase the ban time exponentially (2 ^ count) / 2 * time
	public long getTime(int amount)
	{
		return (long) (System.currentTimeMillis() + (((Math.pow(2, amount) / 2) * time) * 1000));
	}
}