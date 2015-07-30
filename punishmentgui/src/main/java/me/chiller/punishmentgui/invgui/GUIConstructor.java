package me.chiller.punishmentgui.invgui;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;
import me.chiller.punishmentgui.resources.Message;
import me.chiller.punishmentgui.resources.Permission;
import me.chiller.punishmentgui.util.NMSHelper;
import me.chiller.punishmentgui.util.Util;

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
 * Created by Ethan Zeigler, edited by Chiller on 7/6/2015 for PunismentGUI.
 */
public class GUIConstructor implements CommandExecutor
{
	private Main main;
	
	private static ItemStack warnSeed;
	private static ItemStack tempMuteSeed;
	private static ItemStack tempBanSeed;
	private static ItemStack permMuteSeed;
	private static ItemStack permBanSeed;
	private static ItemStack historicalEntrySeed;
	
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
		
		historicalEntrySeed = PunishType.HISTORICAL_ENTRY.getItem();
		editMetadata(historicalEntrySeed, PunishType.HISTORICAL_ENTRY, RED + "View previous punishments against this player");
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (label.equalsIgnoreCase("punish") || label.equalsIgnoreCase("p"))
			{
				if (player.hasPermission(Permission.PUNISH_USE.toString()))
				{
					if (args.length > 1)
					{
						OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
						
						if (pl.hasPlayedBefore() || pl.isOnline())
						{
							if ((pl.isOnline() && !pl.getPlayer().hasPermission(Permission.PUNISH_PROTECTED.toString())) || !pl.isOnline())
							{
								StringBuilder builder = new StringBuilder();
								
								// Combine reason
								for (int i = 1; i < args.length; i++)
									builder.append((i == 1 ? "" : " ") + args[i]);
									
								// Send menu
								openPlayerPunishMenu(pl, player, builder.toString());
							} else
							{
								Util.sendMessage(Message.NOT_PUNISHABLE.replace("{punished}", pl.getName()), player);
							}
						} else
						{
							Util.sendMessage("That player does not exist!", player, DARK_RED);
						}
					} else
					{
						Util.sendMessage("Usage: /" + label + " <player> <reason>", player, DARK_RED);
					}
				} else
				{
					Util.sendMessage("You do not have permission to do that!", sender, DARK_RED);
				}
			}
		} else
		{
			Util.sendMessage("You can only do this as a player!", sender, DARK_RED);
		}
		
		return true;
	}
	
	public void openPlayerPunishMenu(final OfflinePlayer toBePunished, final Player punisher, final String reason)
	{
		final PlayerFile file = main.getPlayerFile(toBePunished.getUniqueId());
		final Inventory menu = Bukkit.createInventory(null, 36, DARK_RED + "Punish " + toBePunished.getName());
		
		new BukkitRunnable()
		{
			public void run()
			{
				addMenuItems(menu, punisher, toBePunished, file, reason);
				
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
	
	public static void addMenuItems(Inventory menu, Player punisher, OfflinePlayer toBePunished, PlayerFile file, String reason)
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
	}
	
	private static void addPermBans(Inventory inv, PlayerFile file)
	{
		ItemStack permBan = permBanSeed.clone();
		
		if (file.hasInfraction(PunishType.PERM_BAN))
		{
			ItemMeta meta = permBan.getItemMeta();
			List<String> lore = permBan.getItemMeta().getLore();
			
			lore.add(1, PunishType.ACTIVE_TAG.toString());
			meta.setLore(lore);
			
			permBan.setItemMeta(meta);
			permBan = addGlow(permBan);
		}
		
		inv.setItem(24, permBan);
	}
	
	private static void addPermMute(Inventory inv, PlayerFile file)
	{
		ItemStack permMute = permMuteSeed.clone();
		
		if (file.hasInfraction(PunishType.PERM_MUTE))
		{
			ItemMeta meta = permMute.getItemMeta();
			List<String> lore = permMute.getItemMeta().getLore();
			
			lore.add(1, PunishType.ACTIVE_TAG.toString());
			meta.setLore(lore);
			
			permMute.setItemMeta(meta);
			permMute = addGlow(permMute);
		}
		
		inv.setItem(23, permMute);
	}
	
	private static void addTempBan(Inventory inv, PlayerFile file)
	{
		ItemStack tempBan = tempBanSeed.clone();
		
		if (file.hasInfraction(PunishType.TEMP_BAN))
		{
			ItemMeta meta = tempBan.getItemMeta();
			List<String> lore = tempBan.getItemMeta().getLore();
			
			lore.add(1, PunishType.ACTIVE_TAG.toString());
			lore.add(2, GOLD + "Expires: " + RED + file.getExpiration(PunishType.TEMP_BAN));
			meta.setLore(lore);
			
			tempBan.setItemMeta(meta);
			tempBan = addGlow(tempBan);
		}
		
		inv.setItem(22, tempBan);
	}
	
	private static void addTempMute(Inventory inv, PlayerFile file)
	{
		ItemStack tempMute = tempMuteSeed.clone();
		
		if (file.hasInfraction(PunishType.TEMP_MUTE))
		{
			ItemMeta meta = tempMute.getItemMeta();
			List<String> lore = tempMute.getItemMeta().getLore();
			
			lore.add(1, PunishType.ACTIVE_TAG.toString());
			lore.add(2, GOLD + "Expires: " + RED + file.getExpiration(PunishType.TEMP_MUTE));
			meta.setLore(lore);
			
			tempMute.setItemMeta(meta);
			tempMute = addGlow(tempMute);
		}
		
		inv.setItem(21, tempMute);
	}
	
	private static void addWarn(Inventory inv)
	{
		ItemStack warn = warnSeed.clone();
		inv.setItem(20, warn);
	}
	
	private static void addHistoryButton(Inventory inv)
	{
		inv.setItem(0, historicalEntrySeed);
	}
	
	private static void addPlayerHead(Inventory inv, OfflinePlayer player, String punishReason)
	{
		ItemStack item = getPlayerHead(player, PunishType.PLAYR_HEAD + player.getName(), GOLD + "Reason: " + RED + punishReason, GOLD + "UUID: " + RED + player.getUniqueId().toString());
		
		inv.setItem(8, item);
	}
	
	private static void editMetadata(ItemStack stack, PunishType displayName, String... lore)
	{
		editMetadata(stack, displayName.toString(), Arrays.asList(lore));
	}
	
	public static void editMetadata(ItemStack stack, String displayName, List<String> lore)
	{
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		
		stack.setItemMeta(meta);
	}
	
	private static ItemStack getPlayerHead(OfflinePlayer player, String displayName, String... lore)
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
			NMSHelper.importClass("net.minecraft.server._version_.ItemStack");
			
			Class<?> CraftItemStack = NMSHelper.importClass("org.bukkit.craftbukkit._version_.inventory.CraftItemStack");
			Class<?> NBTTagCompound = NMSHelper.importClass("net.minecraft.server._version_.NBTTagCompound");
			Class<?> NBTTagList = NMSHelper.importClass("net.minecraft.server._version_.NBTTagList");
			Class<?> NBTBase = NMSHelper.importClass("net.minecraft.server._version_.NBTBase");
			
			Object nmsStack = NMSHelper.buildStaticMethod(CraftItemStack).addUniversalMethod("asNMSCopy").execute(item);
			Object tag = null;
			
			if (!((Boolean) NMSHelper.buildMethod(nmsStack).addUniversalMethod("hasTag").execute()))
			{
				tag = NMSHelper.newInstance(NBTTagCompound);
				
				NMSHelper.buildMethod(nmsStack).addUniversalMethod("setTag").execute(tag);
			} else
			{
				tag = NMSHelper.buildMethod(nmsStack).addUniversalMethod("getTag").execute();
			}
			
			Object ench = NMSHelper.newInstance(NBTTagList);
			
			NMSHelper.buildMethod(tag).addUniversalMethod("set", String.class, NBTBase).execute("ench", ench);
			NMSHelper.buildMethod(nmsStack).addUniversalMethod("setTag").execute(tag);
			
			ItemStack stack = (ItemStack) NMSHelper.buildStaticMethod(CraftItemStack).addUniversalMethod("asCraftMirror").execute(nmsStack);
			
			return stack;
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
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}