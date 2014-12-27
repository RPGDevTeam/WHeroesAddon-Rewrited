package me.wiedzmin137.wheroesaddon.util;

import java.util.ArrayList;
import java.util.List;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.data.PlayerData;
import me.wiedzmin137.wheroesaddon.data.Properties;
import me.wiedzmin137.wheroesaddon.data.SkillBar;
import me.wiedzmin137.wheroesaddon.util.menu.events.ItemClickEvent;
import me.wiedzmin137.wheroesaddon.util.menu.items.MenuItem;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu;
import me.wiedzmin137.wheroesaddon.util.menu.menus.MenuHolder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class SkillBook extends ItemMenu {
	
	private WHeroesAddon plugin;
	private Size size;
	private String name;
	
	public SkillBook(String name, Size size, WHeroesAddon plugin, HeroClass hClass) {
		super(name, size, plugin);
		this.plugin = plugin;
		this.size = size;
		this.name = name;
	}
	
	@Override
	protected void apply(Inventory inventory, Player player) {
		for (int i = 0; i < items.length; i++) {
			MenuItem item = items[i];
			if (item instanceof SkillBookItem) {
				if (((SkillBookItem) item).isHide() && !WHeroesAddon.getInstance().getPlayerData(player).canUnlock(((SkillBookItem) item).getSkill())) {
					continue;
				}
			}
			if (item != null) {
				inventory.setItem(i, item.getFinalIcon(player));
			}
		}
	}
	
	@Override
	public void open(Player player) {
		if (!MenuListener.getInstance().isRegistered(plugin)) {
			MenuListener.getInstance().register(plugin);
		}
		Inventory inventory = Bukkit.createInventory(new MenuHolder(this, Bukkit.createInventory(player, size.getSize())), size.getSize(), 
				name.replace("%a%", String.valueOf(WHeroesAddon.getInstance().getPlayerData(player).getPoints())));
		apply(inventory, player);
		player.openInventory(inventory);
	}

	public static class SkillBookItem extends MenuItem {
		private Skill skill;
		private boolean isHide;
		
		public SkillBookItem( String displayName, ItemStack icon, String[] lore, Skill skill, boolean isHide) {
			super(displayName, icon, lore);
			this.skill = skill;
			this.isHide = isHide;
		}
			
		@Override
		public void onItemClick(ItemClickEvent event) {
			Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(event.getPlayer());
			if ((boolean) Properties.SKILLBAR_ENABLED.getValue()) {
				switch (event.getClickType()) {
				case LEFT: hero.bind(hero.getPlayer().getItemInHand().getType(), new String[] { skill.getName() }); break;
				case RIGHT: 
					SkillBar sb = WHeroesAddon.getInstance().getPlayerData(hero.getPlayer()).getSkillBar();
					sb.assignSkill(skill, sb.getHandSlot());
				default: return;
				}
			} else {
				switch (event.getClickType()) {
				case LEFT: skill.execute(hero.getPlayer(), skill.getName(), new String[]{skill.getName()}); break; //TODO Test it!
				case RIGHT: hero.bind(hero.getPlayer().getItemInHand().getType(), new String[] { skill.getName() }); break;
				default: return;
				}
			}
		}
		@Override
		public ItemStack getFinalIcon(Player player) {
			PlayerData pd = WHeroesAddon.getInstance().getPlayerData(player);
			
			ItemStack finalIcon = super.getFinalIcon(player);
			finalIcon.setAmount(WHeroesAddon.getInstance().getPlayerData(player).getSkillLevel(skill));

			ItemMeta im = finalIcon.getItemMeta();
			List<String> newLore = new ArrayList<>();
			for (String lore : im.getLore()) {
				if (lore.contains("%description%")) {
					lore = lore.replace("%description%", "");
					for (String newL : Utils.splitIntoLine(skill.getDescription(WHeroesAddon.heroes.getCharacterManager().getHero(player)), 50)) {
						newLore.add(lore + newL);
					}
					continue;
				} else {
					lore = lore.replace("%level%", String.valueOf(pd.getSkillLevel(skill)));
					lore = lore.replace("%maxlevel%", String.valueOf(pd.getMaxLevel(skill)));
					lore = lore.replace("%lvlneed%", String.valueOf((int) SkillConfigManager.getSetting(
							WHeroesAddon.heroes.getCharacterManager().getHero(player).getHeroClass(), skill, "level", 1.0D)));
					lore = lore.replace("%skill%", skill.getName());
					lore = lore.replace("%canIUpgrade%", "");
					//TODO make langauge support here
				}
				newLore.add(lore);
			}
			
			im.setLore(newLore);
			finalIcon.setItemMeta(im);
			return finalIcon;
		}
		public Skill getSkill() { return skill; }
		public boolean isHide() { return isHide; }
	}
}
