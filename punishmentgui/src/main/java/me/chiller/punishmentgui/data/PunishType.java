package me.chiller.punishmentgui.data;

import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.chiller.punishmentgui.resources.Message;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/7/2015 for PunismentGUI.
 */
public enum PunishType
{
	//Enum    //Message                //Plural form         //Material of the item                           //Temporary
	WARN(     AQUA + "Warn",           "Warned",             new ItemStack(Material.PAPER, 1),                false),
	TEMP_MUTE(AQUA + "Temporary Mute", "Temporarily Muted",  new ItemStack(Material.INK_SACK, 1, (short) 12), true),
	TEMP_BAN( AQUA + "Temporary Ban",  "Temporarily Banned", new ItemStack(Material.INK_SACK, 1, (short) 1),  true),
	PERM_MUTE(AQUA + "Permanent Mute", "Permanently Muted",  new ItemStack(Material.LAPIS_BLOCK, 1),          false),
	PERM_BAN( AQUA + "Permanent Ban",  "Permanently Banned", new ItemStack(Material.REDSTONE_BLOCK, 1),       false),
	
	//In here because they are all in one place, easy to access
	HISTORICAL_ENTRY(AQUA +  "Previous Reports", "", new ItemStack(Material.BOOK, 1), false),
	PLAYR_HEAD(      AQUA +  "Punish ",          "", null,                            false),
	ACTIVE_TAG(      GREEN + "Currently Active", "", null,                            false);
	
	private String displayName;
	private String plural;
	
	private ItemStack item;
	private boolean isTemp;
	
	private PunishType(String displayName, String plural, ItemStack item, boolean isTemp)
	{
		this.displayName = displayName;
		this.plural = plural;
		this.item = item;
		this.isTemp = isTemp;
	}
	
	public String toString()
	{
		return displayName;
	}
	
	public String getPlural()
	{
		return plural;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
	
	public boolean isTemp()
	{
		return isTemp;
	}
	
	public int getOrdinal()
	{
		return 1 << ordinal();
	}
	
	public Message getMessage()
	{
		return Message.valueOf(name());
	}
	
	public static PunishType value(String name)
	{
		for (PunishType names : PunishType.values())
		{
			if (ChatColor.stripColor(names.toString()).equals(ChatColor.stripColor(name))) return names;
		}
		
		return null;
	}
}