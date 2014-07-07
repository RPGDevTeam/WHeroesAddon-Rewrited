package me.wiedzmin137.wheroesaddon;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import me.desht.scrollingmenusign.ScrollingMenuSign;
import me.wiedzmin137.wheroesaddon.commands.CommandManager;
import me.wiedzmin137.wheroesaddon.util.Config;
import me.wiedzmin137.wheroesaddon.util.Lang;
import me.wiedzmin137.wheroesaddon.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.classes.HeroClass;

public class WHeroesAddon extends JavaPlugin {
	public final static Logger LOG = Logger.getLogger("Minecraft");
	public static Heroes heroes;
	private ScrollingMenuSign sms;
	
	private static WHeroesAddon instance;
	
	private Config config;
	private Config lang;
	
	private CommandManager commandManager;
	private Properties properties;
	private DataManager dataManager;
	
	private HashMap<HeroClass, SkillTree> skillTrees = new HashMap<HeroClass, SkillTree>();
	private HashMap<Player, PlayerData> pData = new HashMap<Player, PlayerData>();
	
	@Override
	public void onEnable() {
		instance = this;
		heroes = (Heroes) Bukkit.getServer().getPluginManager().getPlugin("Heroes");
		sms = (ScrollingMenuSign) getServer().getPluginManager().getPlugin("ScrollingMenuSign");
		
		//Check is Heroes exists
		if (heroes == null) {
			LOG.warning("[WHeroesAddon] Requires Heroes to run for now, please download it");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (sms == null) {
			LOG.warning("[WHeroesAddon] Requires ScrollingMenuSign to run for now, please download it");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		//Load and optionally create configs if not exist
		try {
			config = new Config(this, "config.yml");
			lang = new Config(this, "lang.yml");
		} catch (Exception e) {
			LOG.severe("[WHeroesAddon] Cannot load configs. Error log:");
			e.printStackTrace();
		}
		
		loadProperties();
		
		//Initialize CommandManager for handling commands
		commandManager = new CommandManager(this);
		
		//Create SkillTrees for all HeroClass
		for (HeroClass hClass : heroes.getClassManager().getClasses()) {
			SkillTree st = new SkillTree(this, hClass);
			skillTrees.put(hClass, st);
		}
		
		//Set "skilltree" command Executor in CommandManager
		getCommand("skilltree").setExecutor(new CommandManager(this));
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		dataManager = new DataManager(this);
		dataManager.setDatabase((boolean)Properties.MYSQL_ENABLED.getValue());
		
		//Create data for all exists players (if you reload plugin by PlugMan)
		//FIXME Bugged
//		for (Player player : getServer().getOnlinePlayers()) {
//			try {
//				getPlayerData(player);
//			} catch (NullPointerException e) {
//				dataManager.loadPlayer(player);
//				dataManager.savePlayer(pData.get(player));
//				LOG.info("Loaded by main class");
//			}
//		}

		LOG.info("[WHeroesAddon] vA0.2 has been enabled!");
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
	
	public Config getConfigManager() { return config; }
	public Config getLangManager() { return lang; }
	public CommandManager getCommandManager() { return commandManager; }
	public Properties getProperties() { return properties; }
	public SkillTree getSkillTree(HeroClass hc) { return skillTrees.get(hc); }
	public PlayerData getPlayerData(Player player) { return pData.get(player); }
	public DataManager getDatabaseManager() { return dataManager; }
	protected void setPlayerData(Player player, PlayerData pd) { pData.put(player, pd); return; }
	
	public static WHeroesAddon getInstance() { return instance; }
	
}
