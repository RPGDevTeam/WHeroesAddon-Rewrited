package me.wiedzmin137.wheroesaddon.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.menu.events.ItemClickEvent;
import me.wiedzmin137.wheroesaddon.util.menu.items.MenuItem;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu.Size;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.OutsourcedSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

/**
 * 
 * @author Wiedzmin137
 * SkillTree is Object which can tells you something about
 * skill inheritance and requirements.
 * 
 */
public class SkillTree {
	
	private HeroClass hClass;
	private WHeroesAddon plugin;
	
//	private static SMSHandler smsHandler = WHeroesAddon.sms.getHandler();
	private SkillTreeMenu stm;
	private YamlConfiguration config;
	
	private HashMap<Skill, Integer> skillsWithMaxLevel = new HashMap<Skill, Integer>();
	
	//private HashMap<String, HashMap<Integer, Object>> skillLeveledSettings = new HashMap<String, HashMap<Integer, Object>>();
	
	private HashMap<Skill, HashMap<Skill, Integer>> skillStrongParents = new HashMap<Skill, HashMap<Skill, Integer>>();
	private HashMap<Skill, HashMap<Skill, Integer>> skillWeakParents = new HashMap<Skill, HashMap<Skill, Integer>>();
	
	public SkillTree(WHeroesAddon plugin, HeroClass hClass) {
		this.plugin = plugin;
		this.hClass = hClass;
		
		for (String skillNames : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillNames);
			
			//Getting max-level for each HeroClass skill
			int level = SkillConfigManager.getSetting(hClass, skill, "max-level", -1) == -1 ? -1 :
				SkillConfigManager.getSetting(hClass, skill, "max-level", -1);
			skillsWithMaxLevel.put(skill, level);

			//Getting all parents for each skill
			try {
				for (Map.Entry<String, Integer> string : getParentSkills(skill, "strong").entrySet()) {
					HashMap<Skill, Integer> skillParentStrong = new HashMap<Skill, Integer>();
					skillParentStrong.put(WHeroesAddon.heroes.getSkillManager().getSkill(string.getKey()), string.getValue());
					skillStrongParents.put(skill, skillParentStrong);
				}
			} catch (NullPointerException e) {}
			
			try {
				for (Map.Entry<String, Integer> string : getParentSkills(skill, "weak").entrySet()) {
					HashMap<Skill, Integer> skillParentWeak = new HashMap<Skill, Integer>();
					skillParentWeak.put(WHeroesAddon.heroes.getSkillManager().getSkill(string.getKey()), string.getValue());
					skillWeakParents.put(skill, skillParentWeak);
				}
			} catch (NullPointerException e) {}
		}
		stm = setupMenu();
	}
	
	@SuppressWarnings("unchecked")
	private SkillTreeMenu setupMenu() {
		try {
			config = new Config(plugin, "classes" + File.separator + hClass.getName()).getYAML();
			generateConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SkillTreeMenu menu = new SkillTreeMenu(u(config.getString("DisplayName")), Size.fit(hClass.getSkillNames().size()), plugin, hClass);
		for (Map<?, ?> map : config.getMapList("Items")) {
			MenuItem item;
			if (map.get("IsSkill").equals(true)) {
				ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
				Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill((String) map.get("Name"));
				String[] lore = (u((List<String>) map.get("Lore"))).toArray(new String[0]);
				item = new SkillTreeItem(u((String) map.get("DisplayName")), stack, lore, skill, (boolean) map.get("isHide"));
			} else {
				ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
				String[] lore = (u((List<String>) map.get("Lore"))).toArray(new String[0]);
				item = new MenuItem(u((String) map.get("DisplayName")), stack, lore);
			}
			menu.setItem((int) map.get("Number"), item);
		}
		
		return menu;
	}
	
	private void generateConfig() {
		List<HashMap<String, Object>> hashMap = new ArrayList<>();
		int number = 0;
		for (String skillName : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillName);
			if (!(skill instanceof OutsourcedSkill)) {
				HashMap<String, Object> f = new HashMap<>();
				f.put("Name", skillName);
				f.put("DisplayName", Lang.GUI_TITLE_SKILL.toString().replace("%skill%", skillName));
				f.put("Number", number); number++;
				f.put("IsHide", false);
				f.put("IsSkill", true);
				f.put("Indicator", "STONE");
				f.put("Lore", Arrays.asList("Level: &#167;o{level}/{maxlevel}", "", "&#167;o{description}"));
				hashMap.add(f);
			}
		}
		config.set("DisplayName", "&#167;a&#167;l[ &#167;2" + hClass.getName() + "&#167;a&#167;o]");
		config.set("DefaultMaterial", "null");
		config.set("Items", hashMap);
	}
	
//	public void createSkillTreeMenu() {
//		SMSMenu menu = null;
//		
//		if (smsHandler == null) { return; }
//		try {
//			menu = smsHandler.getMenu(hClass + "-SkillTree");
//		} catch (SMSException e) {
//			menu = smsHandler.createMenu(hClass + "-SkillTree", Lang.TITLE_ITEM_GUI.toString().replace("%class%", hClass.getName()), plugin);
//		}
//		menu.removeAllItems();
//		
//		menu.setAutosave(false);
//		menu.setAutosort(false);
//		
//		for (String skillNames : hClass.getSkillNames()) {
//			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillNames);
//			if (skill instanceof ActiveSkill) {
//				if (skill.getIdentifiers().length == 0) {
//					WHeroesAddon.LOG.severe(Lang.GUI_INVALID_SKILLS.toString().replace("%skill%", skillNames));
//				} else {
//					String indicator = (String)SkillConfigManager.getSetting(hClass, skill, "indicator");
//					boolean glow = SkillConfigManager.getSetting(hClass, skill, "glow", false);
					
					//VERY unclear method to get all skill properties
//					Map<String, Object> values = Properties.getHeroesProperties(hClass).getConfigurationSection("permitted-skills." + skill.getName()).getValues(false);
//					String valuesInString = "";
//					for (String key : values.keySet()) {
//						if (key != "parents") {
//							valuesInString = valuesInString + key + ": <$*." + hClass.toString() + "." + key +">|";
//							
//							VariablesManager vmgr = smsHandler.getVariablesManager();
//							vmgr.set(null, "*." + hClass.toString() + "." + key, String.valueOf(values.get(key)));
//						}
//					}
					
					//Not best method to set lore
//					List<String> newLore = new ArrayList<String>();
//					for (String string : (String[])Properties.ST_ITEM.getValue()) {
//						String newString = string.replaceAll("{description}", skill.getDescription())
//												 .replaceAll("{heroclass}", hClass.getName());
//						newLore.add(newString);
//						if (newString.contains("{parents}")) {
//							newString.replace("{parents}", "");
//							for (Skill str : getWeakParentSkills(skill)) {
//								newLore.add(str.getName());
//							}
//							for (Skill str : getWeakParentSkills(skill)) {
//								newLore.add(str.getName());
//							}
//						}
//						
//						if (newString.contains("{values}")) {
//							newString.replace("{values}", "");
//							String[] splitedValues = valuesInString.split("|");
//							for (String str : splitedValues) {
//								newLore.add(str);
//							}
//						}
//					}
					
//					SMSMenuItem skillClass = new SMSMenuItem.Builder(menu,
//						Lang.GUI_TITLE_SKILL.toString().replace("%skill%", skill.getName()))
//						.withCommand("/st down " + skill.getName() + " 1")
//						.withAltCommand("/st up " + skill.getName() + " 1")
//						.withIcon(indicator)
////						.withGlow(glow)
//						.withLore("<$" + skill.getName() + ">/<$max" + skill.getName() + ">")
//						.build();
//					menu.addItem(skillClass);
//				}
//			}
//		}
//		menu.setAutosave(true);
//		menu.setAutosort(true);
//	}
	
//	public static void showSkillTree(PlayerData pd, HeroClass hClass) {
//		String hc = hClass.getName();
//		SMSMenu menu = null;
//		try {
//			menu = smsHandler.getMenu(hc + "-SkillTree");
//		} catch (SMSException e) {
//			WHeroesAddon.getInstance().getSkillTree(hClass).createSkillTreeMenu();
//			menu = smsHandler.getMenu(hc + "-SkillTree");
//		}
//
//		SMSInventoryView view = null;
//		try {
//			view = (SMSInventoryView)smsHandler.getViewManager().getView(hc);
//		} catch (SMSException e) {
//			view = new SMSInventoryView(hc, menu);
//			view.update(menu, SMSMenuAction.REPAINT);
//			smsHandler.getViewManager().registerView(view);
//		}
//		view.setAutosave(true);
//
//		view.toggleGUI(pd.getPlayer());
//	}
	
	public HashMap<Skill, Integer> getStrongParentSkills(Skill skill) {
		return skillStrongParents.get(skill);
	}

	public HashMap<Skill, Integer> getWeakParentSkills(Skill skill) {
		return skillWeakParents.get(skill);
	}
	
	public List<Skill> getSkills() {
		return new ArrayList<Skill>(skillsWithMaxLevel.keySet());
	}
	
	public HeroClass getHeroClass() {
		return hClass;
	}
	
	protected int getMaxLevel(Skill skill) {
		try {
			return skillsWithMaxLevel.get(skill);
		} catch (NullPointerException e) {
			return 0;
		}

	}
	
	private HashMap<String, Integer> getParentSkills(Skill skill, String weakOrStrong) {
		FileConfiguration hCConfig = Properties.getHeroesProperties(hClass);
		HashMap<String, Integer> parents = new HashMap<String, Integer>();
		if (hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents") == null) {
			return null;
		} else {
			for (Map.Entry<String, Object> map : hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents." + weakOrStrong).getValues(false).entrySet()) {
				parents.put(map.getKey(), (Integer) map.getValue());
			}
		}
		return parents;
	}
	
	private static String u(String str) {
		return StringEscapeUtils.unescapeHtml(str);
	}
	
	private static List<String> u(List<String> str) {
		List<String> list = new ArrayList<>();
		for (String string : str) {
			list.add(u(string));
		}
		return list;
	}
	
	private class SkillTreeItem extends MenuItem {
		private Skill skill;
		private boolean isHide;
		public SkillTreeItem(String displayName, ItemStack icon, String[] lore, Skill skill, boolean isHide) {
			super(displayName, icon, lore);
			this.skill = skill;
			this.isHide = isHide;
		}
		
		@Override
		public void onItemClick(ItemClickEvent event) {
			event.getPlayer().performCommand("st up " + skill.getName());
		}
		
		@Override
		public ItemStack getFinalIcon(Player player) {
			PlayerData pd = WHeroesAddon.getInstance().getPlayerData(player);
			
			ItemStack finalIcon = super.getFinalIcon(player);
			finalIcon.setAmount(WHeroesAddon.getInstance().getPlayerData(player).getSkillLevel(skill));

			ItemMeta im = finalIcon.getItemMeta();
			List<String> newLore = new ArrayList<>();
			for (String lore : im.getLore()) {
				lore.replaceAll("{level}", String.valueOf(pd.getSkillLevel(skill)));
				lore.replaceAll("{maxlevel}", String.valueOf(pd.getMaxLevel(skill)));
				lore.replaceAll("{skill}", skill.getName());
				lore.replaceAll("{description}", skill.getDescription(WHeroesAddon.heroes.getCharacterManager().getHero(player)));
				newLore.add(lore);
			}
			im.setLore(newLore);
			finalIcon.setItemMeta(im);
			return finalIcon;
		}
		public Skill getSkill() { return skill; }
		public boolean isHide() { return isHide; }
	}
	
	private class SkillTreeMenu extends ItemMenu {
		public SkillTreeMenu(String name, Size size, JavaPlugin plugin, HeroClass hClass) {
			super(name, size, plugin);
		}
		
		@Override
		protected void apply(Inventory inventory, Player player) {
			for (int i = 0; i < items.length; i++) {
				MenuItem item = items[i];
				if (item instanceof SkillTreeItem) {
					if (((SkillTreeItem) item).isHide() && !plugin.getPlayerData(player).canUnlock(((SkillTreeItem) item).getSkill())) {
						continue;
					}
				}
				if (item != null) {
					inventory.setItem(i, item.getFinalIcon(player));
				}
			}
		}
		
		
	}
}
