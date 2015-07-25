package me.chiller.punishmentgui.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.chiller.punishmentgui.core.Main;
import me.chiller.punishmentgui.resources.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ethan, edited by Chiller on 6/27/2015.
 */
public class Util
{
	private static String DATE_FORMAT = "MM/dd/yyyy h:mm:ss a";
	
	static
	{
		DATE_FORMAT = Main.getInstance().getConfig().getString("date_format", DATE_FORMAT);
	}
	
	public static String getFormattedDate(Long date)
	{
		return new SimpleDateFormat(DATE_FORMAT).format(new Date(date));
	}
	
	public static void sendMessage(String msg, CommandSender recipient, ChatColor startColor)
	{
		if (recipient != null) recipient.sendMessage(String.format("%s %s%s", Message.MESSAGE_PREFIX, startColor, msg));
	}
	
	public static void sendMessage(String msg, CommandSender recipient)
	{
		if (recipient != null) recipient.sendMessage(msg);
	}
	
	public static void sendSuffix(CommandSender recipient)
	{
		if (recipient != null) recipient.sendMessage(Message.MESSAGE_PREFIX.toString() + " " + Message.MESSAGE_SUFFIX.toString());
	}
}
