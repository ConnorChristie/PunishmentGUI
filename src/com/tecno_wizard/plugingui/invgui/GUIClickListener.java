package com.tecno_wizard.plugingui.invgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Created by Ethan Zeigler on 7/6/2015 for PunismentGUI.
 */
public class GUIClickListener implements Listener {
    @EventHandler
    public void onGUIClick(InventoryClickEvent e) {
        if (e.getInventory().getName().contains("Punish ")) {
            ItemMeta meta = e.getCurrentItem().getItemMeta();
            Inventory inv = e.getInventory();
            String punishedUUID = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(0));
            String reason = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(1));
            String displayName = meta.getDisplayName();
            e.setResult(Event.Result.DENY);
            if(displayName != null) {
                boolean closeWindow
            }
                // cannot use switch, needs constant
                if (displayName.equals(GUIConstructor.WARN_DISPLAY_NAME)) {
                    PunishDealer.warn((Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID))), e.getWhoClicked().getName(), reason);
                } else if(displayName.equals(GUIConstructor.TEMP_BAN_DISPLAY_NAME)) {
                    PunishDealer.tempBan((Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID))), e.getWhoClicked().getName(), reason);
                } else if (displayName.equals(GUIConstructor.TEMP_MUTE_DISPLAY_NAME)) {
                    PunishDealer.tempMute((Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID))), e.getWhoClicked().getName(), reason);
                } else if (displayName.equals(GUIConstructor.PERM_BAN_DISPLAY_NAME)) {
                    PunishDealer.permBan((Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID))), e.getWhoClicked().getName(), reason);
                } else if (displayName.equals(GUIConstructor.PERM_MUTE_DISPLAY_NAME)) {
                    PunishDealer.permMute((Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID))), e.getWhoClicked().getName(), reason);
                } else if(displayName.equals(GUIConstructor.HISTORICAL_ENTRY_BUTTON_DISPLAY_NAME)) {

                }
            }
        }


    }
