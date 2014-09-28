package me.wiedzmin137.wheroesaddon.data;

import java.io.File;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.herocraftonline.heroes.characters.classes.HeroClass;


public enum Properties {
	SKILLTREE_ENABLED("SkillTree.Enabled", true),
	SKILLTREE_SKILLS_DEFAULT_LOCKED("SkillTree.SkillsDefaultLocked", false),
	SKILLTREE_POINTS_PER_LEVEL("SkillTree.PointsPerLevel", 1),
	SKILLTREE_POINTS_ON_START("SkillTree.PointsOnStart", 3),
	SKILLTREE_COST_UNLOCK("SkillTree.CostToUnlock", 0),
	SKILLTREE_COST_LEVEL_UP("SkillTree.CostToLevelUp", 0),
	SKILLTREE_COST_LEVEL_DOWN("SkillTree.CostToLevelDown", 0),
	SKILLTREE_COST_RESET("SkillTree.CostToReset", 0),
	MYSQL_ENABLED("MySQL.Enabled", false),
	MYSQL_HOST("MySQL.Host", "localhost"),
	MYSQL_PORT("MySQL.Port", 3306),
	MYSQL_DATABASE("MySQL.Database", "minecraft"),
	MYSQL_PASSWORD("MySQL.Password", "passwd"),
	MYSQL_USER("MySQL.User", "user"),
	ST_ITEM("ST_ITEM", new String[] {"{description}", "", "{requirements}", "{heroclass}", "{values}"});
	
	private String path;
	private Object def;
	private static YamlConfiguration PROPERTIES;
	
	Properties(String path, Object start) {
		this.path = path;
		this.def = start;
	}
	
	public Object getDefault() {
		return this.def;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public Object getValue() {
		return PROPERTIES.get(this.path, def);
	}
	
	public static void setFile(YamlConfiguration config) {
		PROPERTIES = config;
	}
	
	public static FileConfiguration getHeroesProperties(HeroClass hClass) {
		File classFolder = new File(WHeroesAddon.heroes.getDataFolder(), "classes");
		for (File f : classFolder.listFiles()) {
			FileConfiguration config = new YamlConfiguration();
			try {
				config.load(f);
				String currentClassName = config.getString("name");
				if (currentClassName.equalsIgnoreCase(hClass.getName())) {
					return config;
				}
			}
			catch (Exception localException) {
				WHeroesAddon.LOG.warning("[WHeroesAddon] Failed try to load HeroClasses from Heroes plugin.");
			}
		}
		return null;
	}
}
