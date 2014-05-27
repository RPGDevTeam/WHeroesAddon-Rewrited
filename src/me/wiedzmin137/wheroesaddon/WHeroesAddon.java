package me.wiedzmin137.wheroesaddon;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.heroes.Heroes;

public class WHeroesAddon extends JavaPlugin {
	private static WHeroesAddon instance;
	public static Heroes heroes;
    public final static Logger LOG = Logger.getLogger("Minecraft");
    
    private Config config;
    private Config lang;

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
            lang = new Config(this, "lang.yml");
        } catch (Exception e) {
        	LOG.info("[WHeroesAddon] Cannot load configs. Error log:");
        	e.printStackTrace();
        }
		
		LOG.info("[WHeroesAddon] Plugin v0.2 has been enabled!");
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
	
	public Config getConfigManager() { return config; }
	public Config getLangManager() { return lang; }
	
	public static WHeroesAddon getInstance() { return instance; }
}
