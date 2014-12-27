package me.wiedzmin137.wheroesaddon.util;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu;
import me.wiedzmin137.wheroesaddon.util.menu.menus.MenuHolder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ClassChoose extends ItemMenu {
	
	private WHeroesAddon plugin;
	private String name;
	private Size size;

	public ClassChoose(WHeroesAddon plugin, String name, Size size) {
		super(name, size, plugin);
		
		this.plugin = plugin;
		this.name = name;
		this.size = size;
	}
	
	@Override
	public void open(Player player) {
		if (!MenuListener.getInstance().isRegistered(plugin)) {
			MenuListener.getInstance().register(plugin);
		}
		Inventory inventory = Bukkit.createInventory(new MenuHolder(this, Bukkit.createInventory(player, size.getSize())), size.getSize(), name);
		apply(inventory, player);
		player.openInventory(inventory);
	}
}
