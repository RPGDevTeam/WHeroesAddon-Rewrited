package me.wiedzmin137.wheroesaddon.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.Config;
import me.wiedzmin137.wheroesaddon.util.MenuListener;
import me.wiedzmin137.wheroesaddon.util.Utils;
import me.wiedzmin137.wheroesaddon.util.menu.events.ItemClickEvent;
import me.wiedzmin137.wheroesaddon.util.menu.items.MenuItem;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu.Size;
import me.wiedzmin137.wheroesaddon.util.menu.menus.MenuHolder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
	
	private static Config conf;
	static {
		try {
			conf = new Config(WHeroesAddon.getInstance(), "classes" + File.separator + "class.yml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	
	private SkillTreeMenu setupMenu() {
		try {
			File folder = new File(plugin.getDataFolder().toString() + File.separator + "classes");
			if (!folder.exists()) {
				folder.mkdir();
			}
			conf.checkFile(conf.getFile());
			File file = new File(plugin.getDataFolder() + File.separator + "classes", hClass.getName() + ".yml");
			if (file.exists()) {
				config = YamlConfiguration.loadConfiguration(file);
				return createClassMenu();
			}
			if (conf.getFile().renameTo(file)) {
				config = YamlConfiguration.loadConfiguration(file);
				generateConfig(file);
				return createClassMenu();
			} else {
				WHeroesAddon.LOG.info("Oh god, why?");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void generateConfig(File file) {
		List<HashMap<String, Object>> hashMap = new ArrayList<>();
		int number = 0;
		for (String skillName : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillName);
			if (!(skill instanceof OutsourcedSkill)) {
				HashMap<String, Object> f = new HashMap<>();
				f.put("Name", skillName);
				f.put("DisplayName", Lang.GUI_TITLE_SKILL.toString().replace("%skill%", skill.getName()));
				f.put("Number", number); number++;
				f.put("IsHide", false);
				f.put("IsSkill", true);
				f.put("IsBlockItem", true);
				f.put("Indicator", "STONE");
				f.put("Lore", Arrays.asList("&f§lLevel§f: &o%level%/%maxlevel%", "&f§oLevel needed§f: %lvlneed%" , "", "&6&o%description%", "", "&f&o%canIUpgrade%"));
				hashMap.add(f);
			}
		}
		config.set("DisplayName", "&a&l[ &2" + hClass.getName() + " - %a% SP &a&l]");
		config.set("DefaultMaterial", "null");
		config.set("Items", hashMap);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private SkillTreeMenu createClassMenu() {
		SkillTreeMenu menu = new SkillTreeMenu(Utils.u(config.getString("DisplayName")), Size.fit(hClass.getSkillNames().size()), plugin, hClass);
		for (Map<?, ?> map : config.getMapList("Items")) {
			MenuItem item;
			if ((boolean) map.get("IsSkill") == true) {
				ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
				Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill((String) map.get("Name"));
				String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
				item = new SkillTreeItem(Utils.u((String) map.get("DisplayName")), stack, lore, skill, (boolean) map.get("IsHide"));
			} else if ((boolean) map.get("IsBlockItem")) {
				ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
				String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
				item = new BarBlockItem(Utils.u((String) map.get("DisplayName")), stack, lore);
			} else {
				ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
				String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
				item = new MenuItem(Utils.u((String) map.get("DisplayName")), stack, lore);
			}
			menu.setItem((int) map.get("Number"), item);
		}
		return menu;
	}
	
	public void showMenu(Player player) {
		stm.open(player);
	}
	
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
	
	private static class SkillTreeItem extends MenuItem {
		private Skill skill;
		private boolean isHide;
		public SkillTreeItem(String displayName, ItemStack icon, String[] lore, Skill skill, boolean isHide) {
			super(displayName, icon, lore);
			this.skill = skill;
			this.isHide = isHide;
		}
		
		@Override
		public void onItemClick(ItemClickEvent event) {
			SkillBar sb = WHeroesAddon.getInstance().getPlayerData(event.getPlayer()).getSkillBar();
			if (event.getClickType() == ClickType.SHIFT_RIGHT || event.getClickType() == ClickType.SHIFT_LEFT) {
				sb.assignSkill(skill, sb.getHandSlot());
			} else {
				event.getPlayer().performCommand("st up " + skill.getName());
				//TODO add possibility to downgrade skill here (only if allowed in Properties)
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
					lore = lore.replace("%canIUpgrade%", (pd.canUnlock(skill) && pd.getPoints() > 1) ? "§2§oYou can upgrade it" : "§c§oYou cannot upgrade it yet");
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
	
	private static class BarBlockItem extends MenuItem {
		public BarBlockItem(String displayName, ItemStack icon, String[] lore) {
			super(displayName, icon, lore);
		}
		
		@Override
		public void onItemClick(ItemClickEvent event) {
			SkillBar sb = WHeroesAddon.getInstance().getPlayerData(event.getPlayer()).getSkillBar();
			if (sb.isEnabled()) {
				sb.assignBlockedSlot(sb.getHandSlot());
			}
		}
	}
	
	private static class SkillTreeMenu extends ItemMenu {
		private JavaPlugin plugin;
		private Size size;
		private String name;
		public SkillTreeMenu(String name, Size size, JavaPlugin plugin, HeroClass hClass) {
			super(name, size, plugin);
			this.plugin = plugin;
			this.size = size;
			this.name = name;
		}
		
		@Override
		protected void apply(Inventory inventory, Player player) {
			for (int i = 0; i < items.length; i++) {
				MenuItem item = items[i];
				if (item instanceof SkillTreeItem) {
					if (((SkillTreeItem) item).isHide() && !WHeroesAddon.getInstance().getPlayerData(player).canUnlock(((SkillTreeItem) item).getSkill())) {
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
	}
}
