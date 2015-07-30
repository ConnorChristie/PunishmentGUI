package me.chiller.punishmentgui.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import me.chiller.punishmentgui.util.Util;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/7/2015 for PunismentGUI.
 */
public class Infraction implements ConfigurationSerializable, Comparable<Infraction>
{
	private PunishType type;
	private String reason;
	
	private Long date;
	private boolean active;
	
	private String givenBy;
	private String removedBy;
	private String removeReason;
	private String expiration;
	
	public Infraction(PunishType type, String reason, long date, String givenBy)
	{
		this.type = type;
		this.reason = reason;
		
		this.date = date;
		this.active = type != PunishType.WARN;
		
		this.givenBy = givenBy;
		this.removedBy = "";
		this.removeReason = "";
	}
	
	public Infraction(PunishType type, String reason, long date, long expiration, String givenBy)
	{
		this.type = type;
		this.reason = reason;
		
		this.date = date;
		this.expiration = Util.getFormattedDate(expiration);
		this.active = type != PunishType.WARN;
		
		this.givenBy = givenBy;
		this.removedBy = "";
		this.removeReason = "";
	}
	
	public Infraction(Map<String, Object> map)
	{
		type = PunishType.valueOf((String) map.get("type"));
		reason = (String) map.get("reason");
		
		active = (Boolean) map.get("active");
		expiration = (String) map.get("expiration");
		
		givenBy = (String) map.get("given_by");
		removedBy = (String) map.get("removed_by");
		removeReason = (String) map.get("remove_reason");
	}
	
	public Long getDate()
	{
		return date;
	}
	
	public String getDateString()
	{
		return Util.getFormattedDate(getDate());
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
	
	public String getRemovedBy()
	{
		return removedBy;
	}
	
	public void setRemovedBy(String player)
	{
		this.removedBy = player;
	}
	
	public String getRemoveReason()
	{
		return removeReason;
	}
	
	public void setRemoveReason(String removeReason)
	{
		this.removeReason = removeReason;
	}
	
	public void setDate(Long date)
	{
		this.date = date;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public void setExpiration(String expiration)
	{
		this.expiration = expiration;
	}
	
	public String getExpiration()
	{
		return expiration == null ? "Never" : expiration;
	}

	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("type", type.name());
		map.put("reason", reason);
		
		map.put("active", active);
		map.put("expiration", expiration);
		
		map.put("given_by", givenBy);
		map.put("removed_by", removedBy);
		map.put("remove_reason", removeReason);
		
		return map;
	}

	public int compareTo(Infraction other)
	{
		return getDate().compareTo(other.getDate());
	}
}
