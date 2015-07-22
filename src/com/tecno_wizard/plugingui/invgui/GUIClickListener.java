package com.tecno_wizard.plugingui.invgui;

import com.tecno_wizard.plugingui.data.Infraction;
import com.tecno_wizard.plugingui.data.PlayerFile;
import com.tecno_wizard.plugingui.data.PunishType;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;

/**
 * Created by Ethan Zeigler on 7/6/2015 for PunismentGUI.
 */
public class GUIClickListener implements Listener {
    JavaPlugin plugin;

    public GUIClickListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (e.getInventory().getName().contains("Punish ")) {
            e.setResult(Event.Result.DENY);
            ItemMeta meta;
            String punishedUUID;
            String reason;
            boolean punishmentIsActive;
            OfflinePlayer player;

            try {
                meta = e.getCurrentItem().getItemMeta();
                punishedUUID = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(0));
                reason = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(1));
                punishmentIsActive = meta.getLore().get(0).contains("Active");
                player = Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID));
                if(punishmentIsActive)
                    e.getWhoClicked().sendMessage(ChatColor.GOLD + "Punishment is disabled");
                else
                    e.getWhoClicked().sendMessage(ChatColor.GOLD + "Punishment is enabled");
            } catch (NullPointerException exception) {return;}
            if(meta == null) return;
            String displayName = meta.getDisplayName();
            if(displayName == null) {
                return;
            }
            System.out.println(displayName);
            // cannot use switch, needs constant
            if (displayName.equals(GUIConstructor.WARN_DISPLAY_NAME)) {
                closeInventoryRunnable((Player)e.getWhoClicked());
                PunishDealer.warn(player, e.getWhoClicked().getName(), reason);
            } else if(displayName.equals(GUIConstructor.TEMP_BAN_DISPLAY_NAME)) {
                closeInventoryRunnable((Player)e.getWhoClicked());
                if(punishmentIsActive)
                    PunishDealer.revertPunishment(player, PunishType.TEMP_BAN);
                else
                    PunishDealer.tempBan((Bukkit.getOfflinePlayer(UUID.fromString(punishedUUID))), e.getWhoClicked().getName(), reason);
            } else if (displayName.equals(GUIConstructor.TEMP_MUTE_DISPLAY_NAME)) {
                closeInventoryRunnable((Player)e.getWhoClicked());
                if(punishmentIsActive)
                    PunishDealer.revertPunishment(player, PunishType.TEMP_MUTE);
                else
                    PunishDealer.tempMute((player), e.getWhoClicked().getName(), reason);
            } else if (displayName.equals(GUIConstructor.PERM_BAN_DISPLAY_NAME)) {
                closeInventoryRunnable((Player)e.getWhoClicked());
                if(punishmentIsActive)
                    PunishDealer.revertPunishment(player, PunishType.PERM_BAN);
                else
                    PunishDealer.permBan((player), e.getWhoClicked().getName(), reason);
            } else if (displayName.equals(GUIConstructor.PERM_MUTE_DISPLAY_NAME)) {
                closeInventoryRunnable((Player)e.getWhoClicked());
                if(punishmentIsActive)
                    PunishDealer.revertPunishment(player, PunishType.PERM_MUTE);
                else
                    PunishDealer.permMute((player), e.getWhoClicked().getName(), reason);
            } else if(displayName.equals(GUIConstructor.HISTORICAL_ENTRY_BUTTON_DISPLAY_NAME)) {
                closeInventoryRunnable((Player)e.getWhoClicked());
                openPunishHistoryMenu(player, (Player) e.getWhoClicked());
            }
        } else if(inv.getName().contains(RED + "History of ")) {
            e.setResult(Event.Result.DENY);
        }
    }
    public void openPunishHistoryMenu(final OfflinePlayer toBePunished, final Player punisher) {
        final Inventory menu = Bukkit.createInventory(null, 27, RED + "History of " + toBePunished.getName());
        final PlayerFile file = new PlayerFile(toBePunished.getUniqueId());
        new BukkitRunnable(){
            @Override
            public void run() {
                ItemStack[] stacks = convertHistoryIntoItemStackArray(file);
                menu.addItem(stacks);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        punisher.openInventory(menu);
                    }
                }.runTaskLater(plugin, 3);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void closeInventoryRunnable(final Player toClose) {
        new BukkitRunnable() {
            @Override
            public void run() {
                toClose.closeInventory();
            }
        }.runTaskLater(plugin, 1);
    }


    private ItemStack[] convertHistoryIntoItemStackArray(PlayerFile file) {
        List<Infraction> infractions = file.getInfractionHistory();
        ItemStack[] stacks = new ItemStack[infractions.size()];
        for (int i = 0; i < infractions.size(); i++) {
            Infraction currentInfraction = infractions.get(i);
            ItemStack itemStack = new ItemStack(Material.BOOK, 1);
            GUIConstructor.editMetadata(itemStack, new Timestamp(currentInfraction.getDate()).toString(),
                    GOLD + "Punishment: " + currentInfraction.getType().toString().toLowerCase().replace("_", " "),
                    GOLD + "Reason: " + currentInfraction.getReason(),
                    GOLD + "Given by: "  + currentInfraction.getGivenBy() + " (At time of punishment)");
            stacks[i] = itemStack;
        }
        return stacks;
    }
}
