package me.chiller.punishmentgui.invgui;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.core.Resources;
import me.chiller.punishmentgui.core.Resources.Permission;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;

import org.bukkit.Bukkit;

import static org.bukkit.ChatColor.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Ethan Zeigler on 7/6/2015 for PunismentGUI.
 */
public class GUIConstructor implements CommandExecutor
{
	private Main main;
	
	private ItemStack warnSeed;
	private ItemStack tempMuteSeed;
	private ItemStack tempBanSeed;
	private ItemStack permMuteSeed;
	private ItemStack permBanSeed;
	private ItemStack historicalEntryButtonSeed;
	private ItemStack historicalEntrySeed;
	
	public GUIConstructor(Main main)
	{
		this.main = main;
		main.getCommand("punish").setExecutor(this);
		
		warnSeed = PunishType.WARN.getItem();
		editMetadata(warnSeed, PunishType.WARN, RED + "Warn the player");
		
		tempMuteSeed = PunishType.TEMP_MUTE.getItem();
		editMetadata(tempMuteSeed, PunishType.TEMP_MUTE, RED + "Temporarily mute the player");
		
		tempBanSeed = PunishType.TEMP_BAN.getItem();
		editMetadata(tempBanSeed, PunishType.TEMP_BAN, RED + "Temporarily ban the player");
		
		permMuteSeed = PunishType.PERM_MUTE.getItem();
		editMetadata(permMuteSeed, PunishType.PERM_MUTE, RED + "Permanently mute the player");
		
		permBanSeed = PunishType.PERM_BAN.getItem();
		editMetadata(permBanSeed, PunishType.PERM_BAN, RED + "Permanently ban the player");
		
		historicalEntryButtonSeed = PunishType.HISTORICAL_ENTRY.getItem();
		editMetadata(historicalEntryButtonSeed, PunishType.HISTORICAL_ENTRY, RED + "View previous punishments against this player");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			if (args.length > 1)
			{
				OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
				
				if (player.hasPlayedBefore() || player.isOnline())
				{
					StringBuilder builder = new StringBuilder();
					
					// Combine reason
					for (int i = 1; i < args.length; i++)
						builder.append((i == 1 ? "" : " ") + args[i]);
						
					// Send menu
					openPlayerPunishMenu(player, (Player) sender, builder.toString());
				} else
				{
					Resources.sendMessage("Error: that player does not exist", sender, RED);
				}
				
				return true;
			} else
			{
				return false;
			}
		}
		
		Resources.sendMessage("You can only do this as a player!", sender, RED);
		
		return true;
	}
	
	public void openPlayerPunishMenu(final OfflinePlayer toBePunished, final Player punisher, final String reason)
	{
		final PlayerFile file = main.getPlayerFile(toBePunished.getUniqueId());
		final Inventory menu = Bukkit.createInventory(null, 18, DARK_RED + "Punish " + toBePunished.getName());
		
		new BukkitRunnable()
		{
			public void run()
			{
				if (punisher.hasPermission(Permission.PERM_BAN.toString()))
				{
					addPermBans(menu, file);
				}
				
				if (punisher.hasPermission(Permission.PERM_MUTE.toString()))
				{
					addPermMute(menu, file);
				}
				
				if (punisher.hasPermission(Permission.TEMP_BAN.toString()))
				{
					addTempBan(menu, file);
				}
				
				if (punisher.hasPermission(Permission.TEMP_MUTE.toString()))
				{
					addTempMute(menu, file);
				}
				
				addWarn(menu);
				addHistoryButton(menu);
				addPlayerHead(menu, toBePunished, reason);
				
				new BukkitRunnable()
				{
					
					public void run()
					{
						punisher.openInventory(menu);
					}
				}.runTask(main);
			}
		}.runTaskAsynchronously(main);
	}
	
	private void addPermBans(Inventory inv, PlayerFile file)
	{
		ItemStack permBan = permBanSeed.clone();
		
		if (file.hasInfraction(PunishType.PERM_BAN))
		{
			ItemMeta meta = permBan.getItemMeta();
			List<String> lore = permBan.getItemMeta().getLore();
			
			lore.add(0, PunishType.ACTIVE_TAG.toString());
			meta.setLore(lore);
			
			permBan.setItemMeta(meta);
			permBan = addGlow(permBan);
		}
		
		inv.setItem(15, permBan);
	}
	
	private void addPermMute(Inventory inv, PlayerFile file)
	{
		ItemStack permMute = permMuteSeed.clone();
		
		if (file.hasInfraction(PunishType.PERM_MUTE))
		{
			ItemMeta meta = permMute.getItemMeta();
			List<String> lore = permMute.getItemMeta().getLore();
			
			lore.add(0, PunishType.ACTIVE_TAG.toString());
			meta.setLore(lore);
			
			permMute.setItemMeta(meta);
			permMute = addGlow(permMute);
		}
		
		inv.setItem(14, permMute);
	}
	
	private void addTempBan(Inventory inv, PlayerFile file)
	{
		ItemStack tempBan = tempBanSeed.clone();
		
		if (file.hasInfraction(PunishType.TEMP_BAN))
		{
			ItemMeta meta = tempBan.getItemMeta();
			List<String> lore = tempBan.getItemMeta().getLore();
			
			lore.add(0, PunishType.ACTIVE_TAG.toString());
			meta.setLore(lore);
			
			tempBan.setItemMeta(meta);
			tempBan = addGlow(tempBan);
		}
		
		inv.setItem(13, tempBan);
	}
	
	private void addTempMute(Inventory inv, PlayerFile file)
	{
		ItemStack tempMute = tempMuteSeed.clone();
		
		if (file.hasInfraction(PunishType.TEMP_MUTE))
		{
			ItemMeta meta = tempMute.getItemMeta();
			List<String> lore = tempMute.getItemMeta().getLore();
			
			lore.add(0, PunishType.ACTIVE_TAG.toString());
			meta.setLore(lore);
			
			tempMute.setItemMeta(meta);
			tempMute = addGlow(tempMute);
		}
		
		inv.setItem(12, tempMute);
	}
	
	private void addWarn(Inventory inv)
	{
		ItemStack warn = warnSeed.clone();
		inv.setItem(11, warn);
	}
	
	private void addHistoryButton(Inventory inv)
	{
		inv.setItem(0, historicalEntryButtonSeed);
	}
	
	private void addPlayerHead(Inventory inv, OfflinePlayer player, String punishReason)
	{
		inv.setItem(8, getPlayerHead(player, PunishType.PLAYR_HEAD + player.getName(), RED + player.getUniqueId().toString(), RED + punishReason));
	}
	
	private void editMetadata(ItemStack stack, PunishType displayName, String... lore)
	{
		editMetadata(stack, displayName.toString(), lore);
	}
	
	public static void editMetadata(ItemStack stack, String displayName, String... lore)
	{
		List<String> convertedLore = Arrays.asList(lore);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(displayName);
		meta.setLore(convertedLore);
		
		stack.setItemMeta(meta);
	}
	
	private ItemStack getPlayerHead(OfflinePlayer player, String displayName, String... lore)
	{
		List<String> convertedLore = Arrays.asList(lore);
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		
		meta.setOwner(player.getName());
		meta.setDisplayName(displayName);
		meta.setLore(convertedLore);
		
		skull.setItemMeta(meta);
		
		return skull;
	}
	
	public static ItemStack addGlow(ItemStack item)
	{
		try
		{
			String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			
			Class<?> CraftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
			Class<?> ItemStack = Class.forName("net.minecraft.server." + version + ".ItemStack");
			Class<?> NBTTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
			Class<?> NBTTagList = Class.forName("net.minecraft.server." + version + ".NBTTagList");
			Class<?> NBTBase = Class.forName("net.minecraft.server." + version + ".NBTBase");
			
			Object nmsStack = CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object tag = null;
			
			if (!((Boolean) nmsStack.getClass().getMethod("hasTag").invoke(nmsStack)))
			{
				tag = NBTTagCompound.newInstance();
				
				nmsStack.getClass().getMethod("setTag", NBTTagCompound).invoke(nmsStack, tag);
			}
			
			if (tag == null)
			{
				tag = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
			}
			
			Object ench = NBTTagList.newInstance();
			
			tag.getClass().getMethod("set", String.class, NBTBase).invoke(tag, "ench", ench);
			nmsStack.getClass().getMethod("setTag", NBTTagCompound).invoke(nmsStack, tag);
			
			return (ItemStack) CraftItemStack.getMethod("asCraftMirror", ItemStack).invoke(null, nmsStack);
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
