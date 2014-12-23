package me.wiedzmin137.wheroesaddon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Utils {
	public static String u(String str) {
		return StringEscapeUtils.unescapeHtml(ChatColor.translateAlternateColorCodes('&', str));
	}
	
	public static List<String> u(List<String> str) {
		List<String> list = new ArrayList<>();
		for (String string : str) {
			list.add(u(string));
		}
		return list;
	}
	
	public static String[] splitIntoLine(String input, int maxCharInLine){
		StringTokenizer tok = new StringTokenizer(input, " ");
		StringBuilder output = new StringBuilder(input.length());
		int lineLen = 0;
		while (tok.hasMoreTokens()) {
			String word = tok.nextToken();

			while(word.length() > maxCharInLine){
				output.append(word.substring(0, maxCharInLine-lineLen) + "\n");
				word = word.substring(maxCharInLine-lineLen);
				lineLen = 0;
			}

			if (lineLen + word.length() > maxCharInLine) {
				output.append("\n");
				lineLen = 0;
			}
			output.append(word + " ");
			
			lineLen += word.length() + 1;
		}
		return output.toString().split("\n");
	}
	
	public static boolean moveItem(final Player player, final int slot) {
		final PlayerInventory inv = player.getInventory();
		final ItemStack item = inv.getItem(slot);
		final int empty = firstEmpty(inv.getContents());
		if (empty == -1) {
			player.getWorld().dropItemNaturally(player.getLocation(), item);
			if (slot != -1) {
				inv.clear(slot);
			}
			return false;
		}
		inv.setItem(empty, item);
		if (slot != -1) {
			inv.clear(slot);
		}
		return true;
	}
	
	public static int firstEmpty(final ItemStack[] inventory) {
		for (int i = 9; i < inventory.length; ++i) {
			if (inventory[i] == null) {
				return i;
			}
		}
		return -1;
	}
}
