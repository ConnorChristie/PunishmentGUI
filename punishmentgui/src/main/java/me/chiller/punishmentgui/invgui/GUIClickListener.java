package me.chiller.punishmentgui.invgui;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.bukkit.ChatColor.*;

/**
 * Created by Ethan Zeigler on 7/6/2015 for PunismentGUI.
 */
public class GUIClickListener implements Listener
{
	private Main main;
	
	public GUIClickListener(Main main)
	{
		this.main = main;
		
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e)
	{
		Inventory inv = e.getInventory();
		
		if (e.getInventory().getName().contains("Punish "))
		{
			e.setResult(Event.Result.DENY);
			
			ItemMeta meta;
			
			String punishedUUID;
			String reason;
			
			OfflinePlayer player;
			boolean isPunishmentActive;
			
			try
			{
				meta = e.getCurrentItem().getItemMeta();
				
				punishedUUID = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(0));
				reason = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(1));
				isPunishmentActive = meta.getLore().get(0).contains("Active");
				
				player = Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID));
				
				if (isPunishmentActive)
					e.getWhoClicked().sendMessage("§c§lPUNISH §6Punishment is disabled");
				else
					e.getWhoClicked().sendMessage("§c§lPUNISH §6Punishment is enabled");
			} catch (NullPointerException exception)
			{
				return;
			}
			
			String displayName = meta.getDisplayName();
			PunishType name = PunishType.value(displayName);
			
			if (displayName == null || name == null) return;
			
			switch (name)
			{
				case WARN:
					closeInventoryRunnable((Player) e.getWhoClicked());
					PunishDealer.warn(player, e.getWhoClicked().getName(), reason);
					
					break;
				case TEMP_BAN:
					closeInventoryRunnable((Player) e.getWhoClicked());
					
					if (isPunishmentActive)
						PunishDealer.revertPunishment(player, PunishType.TEMP_BAN);
					else
						PunishDealer.tempBan((Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID))), e.getWhoClicked().getName(), reason);
					
					break;
				case TEMP_MUTE:
					closeInventoryRunnable((Player) e.getWhoClicked());
					
					if (isPunishmentActive)
						PunishDealer.revertPunishment(player, PunishType.TEMP_MUTE);
					else
						PunishDealer.tempMute((player), e.getWhoClicked().getName(), reason);
					
					break;
				case PERM_BAN:
					closeInventoryRunnable((Player) e.getWhoClicked());
					
					if (isPunishmentActive)
						PunishDealer.revertPunishment(player, PunishType.PERM_BAN);
					else
						PunishDealer.permBan((player), e.getWhoClicked().getName(), reason);
					
					break;
				case PERM_MUTE:
					closeInventoryRunnable((Player) e.getWhoClicked());
					
					if (isPunishmentActive)
						PunishDealer.revertPunishment(player, PunishType.PERM_MUTE);
					else
						PunishDealer.permMute((player), e.getWhoClicked().getName(), reason);
					
					break;
				case HISTORICAL_ENTRY:
					closeInventoryRunnable((Player) e.getWhoClicked());
					openPunishHistoryMenu(player, (Player) e.getWhoClicked());
					
					break;
				default: break;
			}
		} else if (inv.getName().contains(RED + "History of "))
		{
			e.setResult(Event.Result.DENY);
		}
	}
	
	public void openPunishHistoryMenu(final OfflinePlayer toBePunished, final Player punisher)
	{
		final Inventory menu = Bukkit.createInventory(null, 54, StringUtils.abbreviate(DARK_RED + "History of " + toBePunished.getName(), 32));
		final PlayerFile file = main.getPlayerFile(toBePunished.getUniqueId());
		
		new BukkitRunnable()
		{
			public void run()
			{
				ItemStack[] stacks = convertHistoryIntoItemStackArray(file, menu);
				menu.addItem(stacks);
				
				new BukkitRunnable()
				{
					public void run()
					{
						punisher.openInventory(menu);
					}
				}.runTaskLater(main, 3);
			}
		}.runTaskAsynchronously(main);
	}
	
	public void closeInventoryRunnable(final Player toClose)
	{
		new BukkitRunnable()
		{
			public void run()
			{
				toClose.closeInventory();
			}
		}.runTaskLater(main, 1);
	}
	
	private ItemStack[] convertHistoryIntoItemStackArray(PlayerFile file, Inventory inv)
	{
		List<Infraction> infractions = file.getInfractionHistory();
		List<ItemStack> infractionList = new ArrayList<ItemStack>();
		
		//Save room for back button and next page
		for (int i = 0; i < inv.getSize() - 2; i++)
		{
			if (i < infractions.size())
			{
				Infraction currentInfraction = infractions.get(i);
				ItemStack itemStack = currentInfraction.getType().getItem().clone();
				
				GUIConstructor.editMetadata(itemStack, ChatColor.AQUA + ChatColor.stripColor(currentInfraction.getType().getPlural()),
						GOLD + "Reason: " + RED + currentInfraction.getReason(),
						GOLD + "Given by: " + RED + currentInfraction.getGivenBy(), GOLD + "Date: " + RED + currentInfraction.getDateString());
						
				infractionList.add(itemStack);
			}
		}
		
		//Greater than
		if (infractions.size() > inv.getSize() - 2)
		{
			//Add next page button
			
			ItemStack nextPage = new ItemStack(Material.PAPER, 1);
			GUIConstructor.editMetadata(nextPage, ChatColor.GREEN + "Next Page");
			
			inv.setItem(inv.getSize() - 2, nextPage);
		}
		
		Collections.reverse(infractionList);
		
		return infractionList.toArray(new ItemStack[infractionList.size()]);
	}
}
