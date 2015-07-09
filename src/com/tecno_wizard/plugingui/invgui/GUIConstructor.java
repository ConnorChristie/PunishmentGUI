package com.tecno_wizard.plugingui.invgui;

import com.tecno_wizard.plugingui.core.Resources;
import com.tecno_wizard.plugingui.data.Infraction;
import com.tecno_wizard.plugingui.data.PlayerFile;
import com.tecno_wizard.plugingui.data.PunishType;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Ethan Zeigler on 7/6/2015 for PunismentGUI.
 */
public class GUIConstructor implements CommandExecutor{
    private ItemStack warnSeed;
    private ItemStack tempMuteSeed;
    private ItemStack tempBanSeed;
    private ItemStack permMuteSeed;
    private ItemStack permBanSeed;
    private ItemStack historicalEntrySeed;

    private static final String WARN_DISPLAY_NAME = GOLD + "" +  BOLD + "Warn";
    private static final String TEMP_MUTE_DISPLAY_NAME = GOLD + "" + BOLD + "Temporary Mute";
    private static final String TEMP_BAN_DISPLAY_NAME = GOLD + "" + BOLD +  "Temporary Ban";
    private static final String PERM_MUTE_DISPLAY_NAME = GOLD + "" + BOLD +  "Permanent Mute";
    private static final String PERM_BAN_DISPLAY_NAME = GOLD + "" + BOLD + "Permanent Ban";
    private static final String HISTORICAN_ENTRY_DISPLAY_NAME = GOLD + "" + BOLD + "Previous Report";

    private static final String ACTIVE_TAG = MAGIC + "|" + RED + "Currently Active" + MAGIC + "|";


    public GUIConstructor(JavaPlugin plugin) {
        plugin.getCommand("punish").setExecutor(this);
        // TODO- add in lore whether punishment is currently on, remove history

        // note to @OfficerDeveloper, none of this is even close to done. Most of the methods here will disappear. Organizing.
        warnSeed = new ItemStack(Material.PAPER, 1);
        editMetadata(warnSeed, WARN_DISPLAY_NAME,
                GOLD + "Warn the player");
        tempMuteSeed = new ItemStack(Material.INK_SACK, 1, (short)12);
        editMetadata(tempMuteSeed, TEMP_MUTE_DISPLAY_NAME,
                GOLD + "Temporarily mute the player");
        tempBanSeed = new ItemStack(Material.INK_SACK, 1, (short)1);
        editMetadata(tempBanSeed, TEMP_BAN_DISPLAY_NAME,
                GOLD + "Temporarily ban the player");
        permMuteSeed = new ItemStack(Material.BOOK, 1);
        editMetadata(permMuteSeed, PERM_MUTE_DISPLAY_NAME,
                GOLD + "Permanently mute the player");
        permBanSeed = new ItemStack(Material.REDSTONE_BLOCK, 1);
        editMetadata(permBanSeed, PERM_BAN_DISPLAY_NAME,
                GOLD + "Permanently ban the player");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if (args.length > 1) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if(player.hasPlayedBefore()) {
                    StringBuilder builder = new StringBuilder();
                    // combine reason
                    for (int i = 0; i < args.length; i++)
                        builder.append(args[i]);
                    // send menu
                    openPlayerPunishMenu(player, (Player)sender, builder.toString());
                } else Resources.sendMessage("Error: that player does not exist", sender, RED);
                return true;
            } else return false;
        } Resources.sendMessage("You can only do this as a player!", sender, RED);
        return true;
    }

    public boolean openPlayerPunishMenu(OfflinePlayer toBePunished, Player punisher, String reason) {
        PlayerFile file = new PlayerFile(toBePunished.getUniqueId());
        Inventory menu = Bukkit.createInventory(null, 27, GOLD + "Punish " + toBePunished.getName());
        if(punisher.hasPermission(Resources.PERM_BAN_PERM)) {
            addPermBans(menu, file);
        } if(punisher.hasPermission(Resources.PERM_MUTE_PERM)) {
            addPermMute(menu, file);
        } if(punisher.hasPermission(Resources.TEMP_BAN_PERM)) {
            addTempBan(menu, file);
        } if(punisher.hasPermission(Resources.TEMP_MUTE_PERM)) {
            addTempMute(menu, file);
        }
    }

    private void addPermBans(Inventory inv, PlayerFile file) {
        if(file.isPunishmentActive(PunishType.PERM_BAN)) {
            ItemStack permBan = permBanSeed.clone();
            ItemMeta meta = permBan.getItemMeta();
            List<String> lore = permBan.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            permBan.setItemMeta(meta);
            // TODO add as position in inventory
        }
    }

    private void addPermMute(Inventory inv, PlayerFile file) {
        if(file.isPunishmentActive(PunishType.PERM_MUTE)) {
            ItemStack permMute = permMuteSeed.clone();
            ItemMeta meta = permMute.getItemMeta();
            List<String> lore = permMute.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            permMute.setItemMeta(meta);
            // TODO add as position in inventory
        }
    }

    private void addTempBan(Inventory inv, PlayerFile file) {
        if(file.isPunishmentActive(PunishType.TEMP_BAN)) {
            ItemStack tempBan = tempBanSeed.clone();
            ItemMeta meta = tempBan.getItemMeta();
            List<String> lore = tempBan.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            tempBan.setItemMeta(meta);
            //TODO add as position in inventory
        }
    }

    private void addTempMute(Inventory inv, PlayerFile file) {
        if(file.isPunishmentActive(PunishType.TEMP_MUTE)) {
            ItemStack tempMute = tempMuteSeed.clone();
            ItemMeta meta = tempMute.getItemMeta();
            List<String> lore = tempMute.getItemMeta().getLore();
            lore.add(0, ACTIVE_TAG);
            meta.setLore(lore);
            tempMute.setItemMeta(meta);
            //TODO add as position in inventory
        }
    }

    private ItemStack[] convertHistoryIntoItemStackArray(PlayerFile file) {
        Set<Infraction> infractions = file.getInfractionHistory();
        ItemStack[] stacks = new ItemStack[infractions.size()];
        for (int i = 0; i < infractions.size(); i++) {

        }
    }

    private void editMetadata(ItemStack stack, String displayName, String... lore) {
        ArrayList<String> convertedLore = (ArrayList) Arrays.asList(lore);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(convertedLore);
        stack.setItemMeta(meta);
    }
}
