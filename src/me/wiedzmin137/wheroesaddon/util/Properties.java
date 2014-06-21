package me.wiedzmin137.wheroesaddon.util;

import org.bukkit.configuration.file.YamlConfiguration;


public enum Properties {
	SKILLTREE_ENABLED("SkillTree.Enabled", true),
	SKILLTREE_POINTS_PER_LEVEL("SkillTree.PointsPerLevel", 1),
	SKILLTREE_POINTS_ON_START("SkillTree.PointsOnStart", 3),
	SKILLTREE_COST_UNLOCK("SkillTree.CostToUnlock", 0),
	SKILLTREE_COST_LEVEL_UP("SkillTree.CostToLevelUp", 0),
	SKILLTREE_COST_LEVEL_DOWN("SkillTree.CostToLevelDown", 0),
	SKILLTREE_COST_RESET("SkillTree.CostToReset", 0),
	MYSQL_ENABLED("MySQL.Enabled", "false"),
	MYSQL_HOST("MySQL.Host", "localhost"),
	MYSQL_PORT("MySQL.Port", 3306),
	MYSQL_DATABASE("MySQL.Database", "minecraft"),
	MYSQL_PASSWORD("MySQL.Password", "passwd");
	
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
}
