package me.chiller.punishmentgui.invgui;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.data.Infraction;
import me.chiller.punishmentgui.data.PlayerFile;
import me.chiller.punishmentgui.data.PunishType;
import me.chiller.punishmentgui.handler.PunishDealer;
import me.chiller.punishmentgui.resources.Message;

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
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.ChatColor.*;

/**
 * Created by Ethan Zeigler, edited by Chiller on 7/6/2015 for PunismentGUI.
 */
public class GUIClickListener implements Listener
{
	private Main main;
	
	private Inventory mainMenu;
	
	private OfflinePlayer player;
	private PlayerFile file;
	private String reason;
	
	public GUIClickListener(Main main)
	{
		this.main = main;
		
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onGUIOpen(InventoryOpenEvent e)
	{
		if (e.getInventory().getName().contains("Punish "))
		{
			mainMenu = e.getInventory();
		}
	}
	
	@EventHandler
	public void onGUIClick(final InventoryClickEvent e)
	{
		Inventory inv = e.getInventory();
		
		if (inv.getName().contains("Punish "))
		{
			e.setResult(Event.Result.DENY);
			
			if (e == null || e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;
			
			ItemMeta meta = e.getCurrentItem().getItemMeta();
			
			reason = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(0)).replace("Reason: ", "");
			UUID punishedUUID = UUID.fromString(ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(1)).replace("UUID: ", ""));
			
			player = Bukkit.getOfflinePlayer(punishedUUID);
			file = Main.getInstance().getPlayerFile(punishedUUID);
			
			String displayName = meta.getDisplayName();
			PunishType type = PunishType.value(displayName);
			
			if (displayName == null || type == null) return;
			
			if (type == PunishType.HISTORICAL_ENTRY)
			{
				closeInventoryRunnable((Player) e.getWhoClicked());
				openPunishHistoryMenu(player, (Player) e.getWhoClicked());
			} else
			{
				if (file.hasInfraction(type))
				{
					PunishDealer.revertPunishment(type, punishedUUID, (Player) e.getWhoClicked(), reason);
				} else
				{
					if (type == PunishType.TEMP_BAN && file.hasInfraction(PunishType.PERM_BAN))
						file.setPunishmentActivity(PunishType.PERM_BAN, false, (Player) e.getWhoClicked(), "Changed to Temporary Ban");
					else if (type == PunishType.TEMP_MUTE && file.hasInfraction(PunishType.PERM_MUTE))
						file.setPunishmentActivity(PunishType.PERM_MUTE, false, (Player) e.getWhoClicked(), "Changed to Temporary Mute");
					else if (type == PunishType.PERM_BAN && file.hasInfraction(PunishType.TEMP_BAN))
						file.setPunishmentActivity(PunishType.TEMP_BAN, false, (Player) e.getWhoClicked(), "Changed to Permanent Ban");
					else if (type == PunishType.PERM_MUTE && file.hasInfraction(PunishType.TEMP_MUTE))
						file.setPunishmentActivity(PunishType.TEMP_MUTE, false, (Player) e.getWhoClicked(), "Changed to Permanent Mute");
					
					PunishDealer.punish(type, player, e.getWhoClicked().getName(), reason);
				}
			}
			
			refreshMainMenu(inv, (Player) e.getWhoClicked(), player, file, reason);
		} else if (inv.getName().contains("History of "))
		{
			e.setResult(Event.Result.DENY);
			
			if (e == null || e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;
			
			if (e.getCurrentItem().getType() == Material.ARROW)
			{
				closeInventoryRunnable((Player) e.getWhoClicked());
				
				new BukkitRunnable()
				{
					public void run()
					{
						e.getWhoClicked().openInventory(mainMenu);
					}
				}.runTaskLater(main, 3);
			} else
			{
				List<Infraction> infractions = file.getInfractionHistory();
				Infraction infraction = infractions.get(e.getSlot());
				
				if (infraction.isActive())
					PunishDealer.revertPunishment(infraction.getType(), file.getUUID(), (Player) e.getWhoClicked(), reason);
				
				refreshHistoryMenu(inv);
				refreshMainMenu(mainMenu, (Player) e.getWhoClicked(), player, file, reason);
			}
		}
	}
	
	private void refreshMainMenu(Inventory menu, Player punisher, OfflinePlayer toBePunished, PlayerFile file, String reason)
	{
		menu.clear();
		GUIConstructor.addMenuItems(menu, punisher, toBePunished, file, reason);
	}
	
	public void openPunishHistoryMenu(final OfflinePlayer toBePunished, final Player punisher)
	{
		final Inventory menu = Bukkit.createInventory(null, 54, StringUtils.abbreviate(DARK_RED + "History of " + toBePunished.getName(), 32));
		file = main.getPlayerFile(toBePunished.getUniqueId());
		
		new BukkitRunnable()
		{
			public void run()
			{
				menu.addItem(convertHistoryIntoItemStackArray(menu));
				
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
	
	private ItemStack[] convertHistoryIntoItemStackArray(Inventory inv)
	{
		List<Infraction> infractions = file.getInfractionHistory();
		List<ItemStack> infractionList = new ArrayList<ItemStack>();
		
		//Save room for back button and next page
		for (int i = 0; i < inv.getSize() - 1; i++)
		{
			if (i < infractions.size())
			{
				Infraction currentInfraction = infractions.get(i);
				ItemStack itemStack = currentInfraction.getType().getItem().clone();
				
				List<String> formattedHistoryList = new ArrayList<String>();
				List<String> historyList = Message.HISTORY.getList();
				
				for (String message : historyList)
					formattedHistoryList.add(message
							.replace("{reason}", currentInfraction.getReason())
							.replace("{punisher}", currentInfraction.getGivenBy())
							.replace("{date}", currentInfraction.getDateString())
							.replace("{expiration}", currentInfraction.getExpiration()));
				
				if (!currentInfraction.getRemovedBy().isEmpty() && !currentInfraction.getRemoveReason().isEmpty())
				{
					List<String> formattedHistoryRemovedList = new ArrayList<String>();
					List<String> historyRemovedList = Message.HISTORY_REMOVED_BY.getList();
					
					for (String message : historyRemovedList)
						formattedHistoryRemovedList.add(message
								.replace("{remover}", currentInfraction.getRemovedBy())
								.replace("{remove_reason}", currentInfraction.getRemoveReason()));
					
					formattedHistoryList.addAll(formattedHistoryRemovedList);
				}
				
				GUIConstructor.editMetadata(itemStack, ChatColor.AQUA + ChatColor.stripColor(currentInfraction.getType().getPlural()), formattedHistoryList);
				
				if (currentInfraction.isActive()) itemStack = GUIConstructor.addGlow(itemStack);
				
				infractionList.add(itemStack);
			}
		}
		
		/*
		//Greater than
		if (infractions.size() > inv.getSize() - 2)
		{
			//Add next page button 
			
			ItemStack nextPage = new ItemStack(Material.PAPER, 1);
			GUIConstructor.editMetadata(nextPage, ChatColor.GREEN + "Next Page");
			
			inv.setItem(inv.getSize() - 2, nextPage);
		}
		*/
		
		ItemStack back = new ItemStack(Material.ARROW, 1);
		GUIConstructor.editMetadata(back, ChatColor.GREEN + "Back to Main Menu", null);
		
		inv.setItem(inv.getSize() - 1, back);
		
		return infractionList.toArray(new ItemStack[infractionList.size()]);
	}
	
	private void refreshHistoryMenu(Inventory inv)
	{
		inv.clear();
		inv.addItem(convertHistoryIntoItemStackArray(inv));
	}
}
