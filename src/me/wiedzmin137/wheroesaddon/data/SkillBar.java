package me.wiedzmin137.wheroesaddon.data;

import java.util.Arrays;
import java.util.HashMap;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillBar {
	
	private final WHeroesAddon plugin;

	//TODO config values for lore etc.

	private HashMap<Integer, String> slots = new HashMap<Integer, String>();
	
	private final Player player;
	private final Hero hero;
	
	private int handSlot = 0;

	public SkillBar(WHeroesAddon plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
		this.hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
	}
	
	public boolean isEnabled() {
		Player p = player.getPlayer();
		return p != null && p.getGameMode() != GameMode.CREATIVE;
	}
	
	public ItemStack getSkillItem(Material material, Skill skill) {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList("", "Skill: " + skill.getName())); //TODO download lore layout from the config
		im.setDisplayName(skill.getName()); //TODO get name layout from the config
		is.setItemMeta(im);
		//TODO add description
		return is;
	}
	
	public ItemStack getBlockItem(Material material) {
		ItemStack is = new ItemStack(material);
		//TODO add lore
		//TODO add description
		return is;
	}
	
	public void unassignSlot(int slot) {
		if (getAssignType(slot) != AssignType.NONE) {
			player.getInventory().setItem(slot, null);
		}
		slots.put(slot, null);
	}
	
	public void assignBlockedSlot(int slot) {
		if (isEnabled()) {
			player.getInventory().setItem(slot, getBlockItem(Material.RECORD_11));
			slots.put(slot, "BLOCK");
		}
	}
	
	
	public void assignSkill(Skill skill, int slot) {
		if (isEnabled()) {
			if (!plugin.getPlayerData(player).isLocked(skill)) {
				if (player.getItemOnCursor() == null && player.getItemOnCursor().getType() == Material.AIR) {
					slots.put(slot, skill.getName());
					PlayerInventory inv = player.getInventory();
					if (inv.getItem(slot) != null) {
						Utils.moveItem(player, slot);
					}
					inv.setItem(slot, getSkillItem(Material.GOLD_RECORD, skill)); //TODO get better material somehow
				}
			}
		}
	}
	
	public boolean isAssignedSkill(int slot) {
		return (slots.get(slot) != null);
	}
	
	public Skill getAssignSkill(int slot) {
		ItemStack is = player.getInventory().getItem(slot);
		String skill = "";
		
		for (String lore : is.getItemMeta().getLore()) {
			lore = ChatColor.stripColor(lore);
			boolean isSkill = false;
			
			for (String subLore : lore.split("[: ]")) {
				if (isSkill == false) {
					if (subLore.equalsIgnoreCase("Skill")) {
						isSkill = true;
					}
				} else {
					skill = subLore;
					isSkill = false;
				}
			}
		}
		
		return WHeroesAddon.heroes.getSkillManager().getSkill(skill);
	}
	
	public AssignType getAssignType(ItemStack is) {
		if (is == null || is.getType() == Material.AIR || is.getItemMeta().getLore() == null)
			return AssignType.NONE;
		
		String skill = "null";
		boolean isBlock = false;
		for (String lore : is.getItemMeta().getLore()) {
			lore = ChatColor.stripColor(lore);
			boolean isSkill = false;
			
			if (lore.equalsIgnoreCase("BlockItem")) {
				isBlock = true;
			} else {
				for (String subLore : lore.split("[: ]")) {
					if (isSkill == false) {
						if (subLore.equalsIgnoreCase("Skill")) {
							isSkill = true;
						}
					} else {
						skill = subLore;
						isSkill = false;
					}
				}
			}
		}
		if (isBlock && !skill.equalsIgnoreCase("null"))
			return AssignType.NONE;
		if (isBlock)
			return AssignType.BLOCK;
		if (!skill.equalsIgnoreCase("null")) {
			return AssignType.SKILL;
		}
		return AssignType.NONE;
	}
	
	public AssignType getAssignType(int slot) {
		ItemStack is = player.getInventory().getItem(slot);
		return getAssignType(is);
	}
	
	public void use(int slot) {
		if (!isEnabled()) return;
		Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(slots.get(slot));
		if (skill == null) return;
		skill.execute(player, skill.getName(), new String[]{skill.getName()}); //TODO Test it!
	}

	public void clear() {
		if (!isEnabled()) return;
		for (int i = 0; i < 9; i++) {
			if (getAssignType(i) != AssignType.NONE) {
				player.getInventory().setItem(i, null);
			}
		}
	}

	public void update() {
		for (int i = 1; i <= 9; i++) {
			int index = i - 1;
			String string = slots.get(i);
			if (string == null)
				return;
			if (!string.equalsIgnoreCase("BLOCK")) {
				if (getAssignType(index) == AssignType.NONE) {
					//TODO check is there is something in this slot. If yes, move it or drop
				}
				if (isEnabled()) {
					assignBlockedSlot(index);
				} else {
					unassignSlot(index);
				}
			} else {
				Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(slots.get(i));
				if (skill == null || !hero.hasAccessToSkill(skill) || !isEnabled()) {
					unassignSlot(index);
				} else {
					assignSkill(skill, index);
				}
			}
		}
	}
	
	public int getHandSlot() { return handSlot; }
	public HashMap<Integer, String> getData() {  return slots; }
	public void setData(HashMap<Integer, String> slots) { this.slots = slots; }
	
	public static enum AssignType {
		SKILL,
		BLOCK,
		NONE;
	}
	
	public static class SkillBarListener implements Listener {
		
		private WHeroesAddon plugin;
		
		public SkillBarListener(WHeroesAddon plugin) {
			this.plugin = plugin;
		}
		
		@EventHandler
		public void onHeldChange(PlayerItemHeldEvent event) {
			Player player = event.getPlayer();
			SkillBar sb = plugin.getPlayerData(player).getSkillBar();
			int slot = event.getNewSlot();
			
			switch (sb.getAssignType(event.getNewSlot())) {
			case BLOCK: event.setCancelled(true);
			case SKILL: sb.use(slot); break;
			case NONE: sb.handSlot = event.getNewSlot(); return;
			}	
		}
		
		@EventHandler
		public void onItemDrop(PlayerDropItemEvent event) {
			SkillBar sb = plugin.getPlayerData(event.getPlayer()).getSkillBar();
			ItemStack item = event.getItemDrop().getItemStack();
			
			if (sb.getAssignType(item) != AssignType.NONE) {
				sb.slots.put(sb.getHandSlot(), null);
				sb.unassignSlot(sb.getHandSlot());
				event.setCancelled(true);
				return;
			}
		}
		
		@EventHandler
		public void onItemTransfter(InventoryMoveItemEvent event) {
			if (event.getSource().getHolder() instanceof Player) {
				SkillBar sb = plugin.getPlayerData((Player) event.getSource().getHolder()).getSkillBar();
				if (sb.getAssignType(event.getItem()) != AssignType.NONE) {
					event.setCancelled(true);
				}
			}
		}
		
		@EventHandler
		public void onItemInteract(PlayerInteractEvent event) {
			SkillBar sb = plugin.getPlayerData(event.getPlayer()).getSkillBar();
			if (sb.getAssignType(event.getItem()) != AssignType.NONE) {
				event.setCancelled(true);
			}
		}
		
		@EventHandler
		public void clear(PlayerDeathEvent event) {
			if (event.getEntity().getGameMode() == GameMode.CREATIVE) return;
			SkillBar sb = plugin.getPlayerData(event.getEntity()).getSkillBar();
			for (int i = 0; i < 9; i++) {
				if (sb.getAssignType(i) != AssignType.NONE) {
					event.getDrops().remove(event.getEntity().getInventory().getItem(i));
					event.getEntity().getInventory().setItem(i, null);
					sb.slots.put(i, null);
				}
			}
		}
	}
}
