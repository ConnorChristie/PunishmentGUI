package com.tecno_wizard.plugingui.invgui;

import com.tecno_wizard.plugingui.core.Resources;
import com.tecno_wizard.plugingui.data.Infraction;
import com.tecno_wizard.plugingui.data.PlayerFile;
import com.tecno_wizard.plugingui.data.PunishType;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Ethan Zeigler on 7/6/2015 for PunismentGUI.
 */
public class GUIConstructor implements CommandExecutor{
    private JavaPlugin plugin;

    private ItemStack warnSeed;
    private ItemStack tempMuteSeed;
    private ItemStack tempBanSeed;
    private ItemStack permMuteSeed;
    private ItemStack permBanSeed;
    private ItemStack historicalEntryButtonSeed;
    private ItemStack historicalEntrySeed;

    public static final String WARN_DISPLAY_NAME = GOLD + "" +  BOLD + "Warn";
    public static final String TEMP_MUTE_DISPLAY_NAME = GOLD + "" + BOLD + "Temporary Mute";
    public static final String TEMP_BAN_DISPLAY_NAME = GOLD + "" + BOLD +  "Temporary Ban";
    public static final String PERM_MUTE_DISPLAY_NAME = GOLD + "" + BOLD +  "Permanent Mute";
    public static final String PERM_BAN_DISPLAY_NAME = GOLD + "" + BOLD + "Permanent Ban";
    public static final String HISTORICAL_ENTRY_BUTTON_DISPLAY_NAME = GOLD + "" + BOLD + "Previous Report";
    public static final String PLAYR_HEAD_DISPLAY_NAME = GOLD + "" + BOLD + "Punish: ";
    public static final String ACTIVE_TAG = MAGIC + "|" + RED + "Currently Active" + MAGIC + "|";


    public GUIConstructor(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("punish").setExecutor(this);

        warnSeed = new ItemStack(Material.PAPER, 1);
        editMetadata(warnSeed, WARN_DISPLAY_NAME,
                RED + "Warn the player");
        tempMuteSeed = new ItemStack(Material.INK_SACK, 1, (short)12);
        editMetadata(tempMuteSeed, TEMP_MUTE_DISPLAY_NAME,
                RED + "Temporarily mute the player");
        tempBanSeed = new ItemStack(Material.INK_SACK, 1, (short)1);
        editMetadata(tempBanSeed, TEMP_BAN_DISPLAY_NAME,
                RED + "Temporarily ban the player");
        permMuteSeed = new ItemStack(Material.BOOK, 1);
        editMetadata(permMuteSeed, PERM_MUTE_DISPLAY_NAME,
                RED + "Permanently mute the player");
        permBanSeed = new ItemStack(Material.REDSTONE_BLOCK, 1);
        editMetadata(permBanSeed, PERM_BAN_DISPLAY_NAME,
                RED + "Permanently ban the player");
        historicalEntryButtonSeed = new ItemStack(Material.BOOKSHELF);
        editMetadata(historicalEntryButtonSeed, HISTORICAL_ENTRY_BUTTON_DISPLAY_NAME,
                RED + "View previous punishments against this player");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            System.out.println(args.length);
            if (args.length > 1) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if (player.hasPlayedBefore() || player.isOnline()) {
                    StringBuilder builder = new StringBuilder();
                    // combine reason
                    for (int i = 1; i < args.length; i++)
                        builder.append(args[i]);
                    // send menu
                    openPlayerPunishMenu(player, (Player) sender, builder.toString());
                } else Resources.sendMessage("Error: that player does not exist", sender, RED);
                return true;
            } else return false;
        }
        Resources.sendMessage("You can only do this as a player!", sender, RED);
        return true;
    }


    //<editor-fold desc="Primary Menu">
    public void openPlayerPunishMenu(final OfflinePlayer toBePunished, final Player punisher, final String reason) {
        final PlayerFile file = new PlayerFile(toBePunished.getUniqueId());

        final Inventory menu = Bukkit.createInventory(null, 27, RED + "Punish " + toBePunished.getName());
        new BukkitRunnable() {

            @Override
            public void run() {
                if(punisher.hasPermission(Resources.PERM_BAN_PERM)) {
                    addPermBans(menu, file);
                } if(punisher.hasPermission(Resources.PERM_MUTE_PERM)) {
                    addPermMute(menu, file);
                } if(punisher.hasPermission(Resources.TEMP_BAN_PERM)) {
                    addTempBan(menu, file);
                } if(punisher.hasPermission(Resources.TEMP_MUTE_PERM)) {
                    addTempMute(menu, file);
                }
                addWarn(menu);
                addHistoryButton(menu);
                addPlayerHead(menu, toBePunished, reason);
                new BukkitRunnable(){

                    @Override
                    public void run() {
                        punisher.openInventory(menu);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    private void addPermBans(Inventory inv, PlayerFile file) {
        ItemStack permBan = permBanSeed.clone();
        if(file.isPunishmentActive(PunishType.PERM_BAN)) {
            ItemMeta meta = permBan.getItemMeta();
            List<String> lore = permBan.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            permBan.setItemMeta(meta);
        }
        inv.setItem(15, permBan);
    }

    private void addPermMute(Inventory inv, PlayerFile file) {
        ItemStack permMute = permMuteSeed.clone();
        if(file.isPunishmentActive(PunishType.PERM_MUTE)) {
            ItemMeta meta = permMute.getItemMeta();
            List<String> lore = permMute.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            permMute.setItemMeta(meta);
        }
        inv.setItem(14, permMute);
    }

    private void addTempBan(Inventory inv, PlayerFile file) {
        ItemStack tempBan = tempBanSeed.clone();
        if(file.isPunishmentActive(PunishType.TEMP_BAN)) {
            ItemMeta meta = tempBan.getItemMeta();
            List<String> lore = tempBan.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            tempBan.setItemMeta(meta);
        }
        inv.setItem(13, tempBan);
    }

    private void addTempMute(Inventory inv, PlayerFile file) {
        ItemStack tempMute = tempMuteSeed.clone();
        if(file.isPunishmentActive(PunishType.TEMP_MUTE)) {
            ItemMeta meta = tempMute.getItemMeta();
            List<String> lore = tempMute.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            tempMute.setItemMeta(meta);
        }
        inv.setItem(12, tempMute);
    }

    private void addWarn(Inventory inv) {
        ItemStack warn = warnSeed.clone();
        inv.setItem(11, warn);
    }

    private void addHistoryButton(Inventory inv) {
        inv.setItem(0, historicalEntryButtonSeed);
    }

    private void addPlayerHead(Inventory inv, OfflinePlayer player, String punishReason) {
        inv.setItem(8, getPlayerHead(player, PLAYR_HEAD_DISPLAY_NAME + player.getName(), RED + player.getUniqueId().toString(), RED + punishReason));
    }

    //</editor-fold>



    public static void editMetadata(ItemStack stack, String displayName, String... lore) {
        List<String> convertedLore = Arrays.asList(lore);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(convertedLore);
        stack.setItemMeta(meta);
    }

    private ItemStack getPlayerHead(OfflinePlayer player, String displayName, String... lore) {
        List<String> convertedLore = Arrays.asList(lore);
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(displayName);
        meta.setLore(convertedLore);
        skull.setItemMeta(meta);
        return skull;
    }
}
