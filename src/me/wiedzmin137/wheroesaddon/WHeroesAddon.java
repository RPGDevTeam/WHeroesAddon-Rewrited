package me.wiedzmin137.wheroesaddon;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.wiedzmin137.wheroesaddon.commands.CommandManager;
import me.wiedzmin137.wheroesaddon.data.CooldownScoreboard;
import me.wiedzmin137.wheroesaddon.data.DataManager;
import me.wiedzmin137.wheroesaddon.data.Lang;
import me.wiedzmin137.wheroesaddon.data.PlayerData;
import me.wiedzmin137.wheroesaddon.data.Properties;
import me.wiedzmin137.wheroesaddon.data.SkillBar;
import me.wiedzmin137.wheroesaddon.data.SkillTree;
import me.wiedzmin137.wheroesaddon.util.Config;
import me.wiedzmin137.wheroesaddon.util.FirstClassChooseEvent;
import me.wiedzmin137.wheroesaddon.util.MenuListener;
import me.wiedzmin137.wheroesaddon.util.SkillBook;
import me.wiedzmin137.wheroesaddon.util.Utils;
import me.wiedzmin137.wheroesaddon.util.menu.events.ItemClickEvent;
import me.wiedzmin137.wheroesaddon.util.menu.items.BackItem;
import me.wiedzmin137.wheroesaddon.util.menu.items.MenuItem;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu;
import me.wiedzmin137.wheroesaddon.util.menu.menus.ItemMenu.Size;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ImmutableList;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;

public class WHeroesAddon extends JavaPlugin {
	public final static Logger LOG = Logger.getLogger("Minecraft");
	public static Heroes heroes;
	
	private static WHeroesAddon instance;
	
	private Config config;
	private Config lang;
	private Config classChoose;
	
	private CommandManager commandManager;
	private Properties properties;
	private DataManager dataManager;
	
	private ItemMenu classChooseMenu;
	private ItemMenu confirmMenu;
	
	private Map<String, String> classChooseMap = new HashMap<>();
	private Map<HeroClass, SkillBook> skillBooks = new HashMap<>();
	private Map<HeroClass, SkillTree> skillTrees = new HashMap<HeroClass, SkillTree>();
	public Map<Player, PlayerData> pData = new HashMap<Player, PlayerData>(); //TODO change public
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		instance = this;
		heroes = (Heroes) Bukkit.getServer().getPluginManager().getPlugin("Heroes");
		
		if (heroes == null) {
			LOG.warning("[WHeroesAddon] Requires Heroes to run for now, please download it");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		try {
			config = new Config(this, "config.yml");
			config.checkFile(config.getFile());
			lang = new Config(this, "lang.yml");
			lang.checkFile(config.getFile());
			loadProperties();
			
			classChoose = new Config(this, "classChoose.yml"); //TODO check Properties
			classChoose.checkFile(config.getFile());
		} catch (Exception e) {
			LOG.severe("[WHeroesAddon] Cannot load configs. Error log:");
			e.printStackTrace();
		}
	
		
		//TODO initialise only if the SkillTree feature is enabled
		for (HeroClass hClass : heroes.getClassManager().getClasses()) {
			SkillTree st = new SkillTree(this, hClass);
			skillTrees.put(hClass, st);
			
			YamlConfiguration co = Properties.classConfig.get(hClass);
			SkillBook menu = new SkillBook(Utils.u(co.getString("DisplayName")), Size.fit(hClass.getSkillNames().size()), this, hClass);
			int amount = 1;
				for (Map<?, ?> map : co.getMapList("Items")) {
					MenuItem item;
					if ((boolean) map.get("IsSkill") == true) {
						ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
						Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill((String) map.get("Name"));
						String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
						item = new SkillBook.SkillBookItem(Utils.u((String) map.get("DisplayName")), stack, lore, skill, (boolean) map.get("IsHide"));
					} else if ((boolean) map.get("IsBlockItem")) {
						ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
						String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
						item = new SkillTree.BarBlockItem(Utils.u((String) map.get("DisplayName")), stack, lore);
					} else {
						ItemStack stack = new ItemStack(Material.getMaterial((String) map.get("Indicator")));
						String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
						item = new MenuItem(Utils.u((String) map.get("DisplayName")), stack, lore);
					}
					amount++;
					if (amount <= 54) {
						menu.setItem((int) map.get("Number"), item);
					} else {
						break;
					}
			}
			skillBooks.put(hClass, menu);
		}
		
		//Initialize CommandManager for handling commands
		commandManager = new CommandManager(this);
		getCommand("skilltree").setExecutor(commandManager);
		getCommand("classchoose").setExecutor(commandManager); //TODO add config variable in Properties
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		//getServer().getPluginManager().registerEvents(commandManager, this);
		
		if ((boolean) Properties.SCOREBOARD_COOLDOWN_ENABLED.getValue())
			getServer().getPluginManager().registerEvents(new CooldownScoreboard(this), this);
		
		if ((boolean) Properties.SKILLBAR_ENABLED.getValue())
			getServer().getPluginManager().registerEvents(new SkillBar.SkillBarListener(this), this);
		
		initializeMenus();
		
		dataManager = new DataManager(this);
		
		for (Player player : ImmutableList.copyOf(getServer().getOnlinePlayers())) {
			dataManager.loadPlayer(player);
		}
		
		MenuListener.getInstance().register(this);
		
		LOG.info("[WHeroesAddon] vB0.3 has been enabled!");
	}

	@Override
	public void onDisable() {
		commandManager = null;
		
		dataManager = null;
		config = null;
		lang = null;
		
		instance = null;
	}
	
	private void loadProperties() {
		//Load language
		YamlConfiguration lang = getLangManager().getYAML();
		for (Lang item : Lang.values()) {
			if (lang.getString(item.getPath()) == null) {
				lang.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(lang);
		try {
			lang.save(getLangManager().getFile());
		} catch (IOException e) {
			LOG.warning("[WHeroesAddon] Failed to save lang.yml.");
			LOG.warning("[WHeroesAddon] Report this stack trace to Wiedzmin137.");
			e.printStackTrace();
		}
		
		//Load Properties
		Properties.setFile(getConfigManager().getYAML());
	}
	
	@SuppressWarnings("unchecked")
	private void initializeMenus() {
		YamlConfiguration yml = classChoose.getYAML();
		
		int latestChoose = 0;
		Map<Integer, MenuItem> itemsCh = new HashMap<>();
		for (final Map<?, ?> map : yml.getMapList("ClassChoose")) {
			int number = (int) map.get("Number");
			if (number > latestChoose) {
				latestChoose = number;
			}
			String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
			itemsCh.put(number, new MenuItem((String) map.get("DisplayName"), new ItemStack(Material.getMaterial((String) map.get("Icon"))), lore) {
				@Override
				public void onItemClick(ItemClickEvent event) {
					event.setWillClose(true);
					final String playerName = event.getPlayer().getName();
					
					final String className = (String) map.get("ClassName");
					final boolean test = className.equalsIgnoreCase("NONE");
					if (test) return;
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(WHeroesAddon.this, new Runnable() {
						@Override
						@SuppressWarnings("deprecation")
						public void run() {
							Player p = Bukkit.getPlayerExact(playerName);
							if (p != null) {
								confirmMenu.open(p);
								classChooseMap.put(playerName, className);
							}
						}
					}, 3);
				}
			});
		}
		classChooseMenu = new ItemMenu(yml.getString("ChooseName"), Size.fit(latestChoose), this);
		for (Map.Entry<Integer, MenuItem> entry : itemsCh.entrySet()) {
			classChooseMenu.setItem(entry.getKey(), entry.getValue());
		}
		
		int latestConfirm = 0;
		Map<Integer, MenuItem> itemsCo = new HashMap<>();
		for (Map<?, ?> map : yml.getMapList("ClassChoose")) {
			int number = (int) map.get("Number");
			if (number > latestConfirm) {
				latestConfirm = number;
			}
			if ((boolean) map.get("BackItem")) {
				itemsCo.put(number, new BackItem());
			} else {
				String[] lore = (Utils.u((List<String>) map.get("Lore"))).toArray(new String[0]);
				itemsCo.put(number, new MenuItem((String) map.get("DisplayName"), new ItemStack(Material.getMaterial((String) map.get("Icon"))), lore) {
					@Override
					public void onItemClick(ItemClickEvent event) {
						//TODO add null value checker (shouldn't exist anyway)
						Hero hero = heroes.getCharacterManager().getHero(event.getPlayer());
						HeroClass hClass = heroes.getClassManager().getClass(classChooseMap.get(event.getPlayer().getName()));
						hero.setHeroClass(hClass, false);
						
						FirstClassChooseEvent newEvent = new FirstClassChooseEvent(hero, hClass);
						Bukkit.getServer().getPluginManager().callEvent(newEvent);
						
						classChooseMap.remove(hero.getPlayer().getName());
					}
				});
			}
		}
		confirmMenu = new ItemMenu(yml.getString("ConfirmName"), Size.fit(latestConfirm), this);
		for (Map.Entry<Integer, MenuItem> entry : itemsCo.entrySet()) {
			confirmMenu.setItem(entry.getKey(), entry.getValue());
		}
		
		//TODO add SkillBook here
	}
	
	public Config getConfigManager() { return config; }
	public Config getLangManager() { return lang; }
	public CommandManager getCommandManager() { return commandManager; }
	public Properties getProperties() { return properties; }
	public ItemMenu getChooseMenu() { return classChooseMenu; }
	public SkillBook getSkillBook(HeroClass hc) { return skillBooks.get(hc); }
	public SkillTree getSkillTree(HeroClass hc) { return skillTrees.get(hc); }
	public PlayerData getPlayerData(Player player) { return pData.get(player); }
	public DataManager getDatabaseManager() { return dataManager; }
	public void setPlayerData(Player player, PlayerData pd) { pData.put(player, pd); return; }
	
	public static WHeroesAddon getInstance() { return instance; }
	
}
