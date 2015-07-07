package com.tecno_wizard.plugingui.invgui;

import com.mysql.jdbc.StringUtils;
import com.tecno_wizard.plugingui.core.Resources;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ethan Zeigler on 7/6/2015 for PunismentGUI.
 */
public class GUIConstructor implements CommandExecutor{
    private ItemStack warn;
    private ItemStack tempMute;
    private ItemStack tempBan;
    private ItemStack permMute;
    private ItemStack permBan;

    private static final String WARN_DISPLAY_NAME = ChatColor.GOLD + "Warn";
    private static final String TEMP_MUTE_DISPLAY_NAME = ChatColor.GOLD + "Temporary Mute";
    private static final String TEMP_BAN_DISPLAY_NAME = ChatColor.GOLD + "Temporary Ban";
    private static final String PERM_MUTE_DISPLAY_NAME = ChatColor.GOLD + "Permanent Mute";
    private static final String PERM_BAN_DISPLAY_NAME = ChatColor.GOLD + "Permanent Ban";

    private static final String ACTIVE_TAG = ChatColor.MAGIC + "|" + ChatColor.RED + "Currently Active" + ChatColor.MAGIC + "|";


    public GUIConstructor(JavaPlugin plugin) {
        plugin.getCommand("punish").setExecutor(this);
        // TODO- add in lore whether punishment is currently on, remove history

        // note to @OfficerDeveloper, none of this is even close to done. Most of the methods here will disappear. Organizing.
        warn = new ItemStack(Material.PAPER, 1);
        editMetadata(warn, WARN_DISPLAY_NAME,
                ChatColor.LIGHT_PURPLE + "Warns the player of improper behavior");
        tempMute = new ItemStack(Material.INK_SACK, 1, (short)12);
        editMetadata(tempMute, TEMP_MUTE_DISPLAY_NAME,
                ChatColor.LIGHT_PURPLE + "Temporarily mutes the player");
        tempBan = new ItemStack(Material.INK_SACK, 1, (short)1);
        editMetadata(tempBan, TEMP_BAN_DISPLAY_NAME,
                ChatColor.LIGHT_PURPLE + "Temporarily bans the player");
        permMute = new ItemStack(Material.BOOK, 1);
        editMetadata(permMute, PERM_MUTE_DISPLAY_NAME,
                ChatColor.LIGHT_PURPLE + "Permanently mutes the player");
        permBan = new ItemStack(Material.REDSTONE_BLOCK, 1);
        editMetadata(permBan, PERM_BAN_DISPLAY_NAME,
                ChatColor.LIGHT_PURPLE + "Permanently bans the player");
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
                } else Resources.sendMessage("Error: that player does not exist", sender, ChatColor.RED);
                return true;
            } else return false;
        } Resources.sendMessage("You can only do this as a player!", sender, ChatColor.RED);
    }

    public boolean openPlayerPunishMenu(OfflinePlayer toBePunished, Player punisher, String reason) {
        Inventory menu = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Punish " + toBePunished.getName());
        if(punisher.hasPermission(Resources.PERM_BAN_PERM)) {
            addPermBans(menu);
        } if(punisher.hasPermission(Resources.PERM_MUTE_PERM)) {
            addPermMute(menu);
        } if(punisher.hasPermission(Resources.TEMP_BAN_PERM)) {
            addTempBan(menu);
        } if(punisher.hasPermission(Resources.TEMP_MUTE_PERM)) {
            addTempMute(menu);
        }
    }

    private void addPermBans(Inventory inv) {

    }

    private void addPermMute(Inventory inv) {

    }

    private void addTempBan(Inventory inv) {

    }

    private void addTempMute(Inventory inv) {

    }


    private void editMetadata(ItemStack stack, String displayName, String... lore) {
        ArrayList<String> convertedLore = (ArrayList) Arrays.asList(lore);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(convertedLore);
        stack.setItemMeta(meta);
    }
}
