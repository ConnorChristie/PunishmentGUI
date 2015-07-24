package me.chiller.punishmentgui.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import me.chiller.punishmentgui.core.Resources;

/**
 * Created by Ethan Zeigler on 7/7/2015 for PunismentGUI.
 */
public class Infraction implements ConfigurationSerializable
{
	private PunishType type;
	private String reason;
	private Long date;
	private String givenBy;
	
	public Infraction(PunishType type, String reason, long date, String givenBy)
	{
		this.type = type;
		this.reason = reason;
		this.date = date;
		this.givenBy = givenBy;
	}
	
	public Infraction(Map<String, Object> map)
	{
		type = PunishType.valueOf((String) map.get("type"));
		reason = (String) map.get("reason");
		givenBy = (String) map.get("given_by");
	}
	
	public long getDate()
	{
		return date;
	}
	
	public String getDateString()
	{
		return Resources.getFormattedDate(getDate());
	}
	
	public String getReason()
	{
		return reason;
	}
	
	public PunishType getType()
	{
		return type;
	}
	
	public String getGivenBy()
	{
		return givenBy;
	}
	
	public void setDate(Long date)
	{
		this.date = date;
	}

	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("type", type.name());
		map.put("reason", reason);
		map.put("given_by", givenBy);
		
		return map;
	}
	
}
