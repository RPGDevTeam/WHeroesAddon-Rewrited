package me.wiedzmin137.wheroesaddon;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import me.wiedzmin137.wheroesaddon.commands.CommandManager;
import me.wiedzmin137.wheroesaddon.util.Config;
import me.wiedzmin137.wheroesaddon.util.Lang;
import me.wiedzmin137.wheroesaddon.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.heroes.Heroes;

public class WHeroesAddon extends JavaPlugin {
	public final static Logger LOG = Logger.getLogger("Minecraft");
	public static Heroes heroes;
	
	private static WHeroesAddon instance;
	
	private Config config;
	private Config lang;
	
	private CommandManager commandManager;
	
	@Override
	public void onEnable() {
		instance = this;
		heroes = (Heroes) Bukkit.getServer().getPluginManager().getPlugin("Heroes");
		
		if (heroes == null) {
			LOG.warning("[WHeroesAddon] Requires Heroes to run for now, please download it");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		File file = getDataFolder();
		if (!file.exists()) {
			file.mkdir();
		}
		
		try {
			config = new Config(this, "config.yml");
			lang = new Config(this, "lang.yml");
		} catch (Exception e) {
			LOG.severe("[WHeroesAddon] Cannot load configs. Error log:");
			e.printStackTrace();
		}
		
		YamlConfiguration conf = getLangManager().getYAMLConfiguration();
		for (Lang item : Lang.values()) {
			if (conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(conf);
		try {
			conf.save(getLangManager().getFile());
		} catch (IOException e) {
			LOG.warning("[WHeroesAddon] Failed to save lang.yml.");
			LOG.warning("[WHeroesAddon] Report this stack trace to Wiedzmin137.");
			e.printStackTrace();
		}
		
		commandManager = new CommandManager(this);
		
		LOG.info("[WHeroesAddon] vA0.2 has been enabled!");
	}

	
	@Override
	public void onDisable() {
		commandManager = null;
		
		config = null;
		lang = null;
		
		instance = null;
	}
	
	public Config getConfigManager() { return config; }
	public Config getLangManager() { return lang; }
	public CommandManager getCommandManager() { return commandManager; }
	
	public static WHeroesAddon getInstance() { return instance; }
}
