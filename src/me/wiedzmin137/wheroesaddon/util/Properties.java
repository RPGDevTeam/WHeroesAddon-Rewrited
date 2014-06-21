package me.wiedzmin137.wheroesaddon.util;

import org.bukkit.configuration.file.YamlConfiguration;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;

public class Properties {
	//TODO [Bad conding detected] - I'll change it all
	
	private WHeroesAddon plugin;
	
	public Properties(WHeroesAddon wHeroesAddon) {
		YamlConfiguration yaml = wHeroesAddon.getConfigManager().getYAML();
		this.plugin = wHeroesAddon;
		
		STEnabled = yaml.getBoolean("SkillTree.Enabled", true);
		STPointsPerLevel = yaml.getInt("SkillTree.Points_Per_Level", 1);
		STCostUnlock = yaml.getInt("SkillTree.Cost_Unlock", 0);
		STCostLevelUP = yaml.getInt("SkillTree.Cost_Level_Up", 0);
		STCostLevelDown = yaml.getInt("SkillTree.Cost_Level_Down", 0);
		STCostReset = yaml.getInt("SkillTree.Cost_Reset", 0);
	}
	public boolean STEnabled = true;
	public int STPointsPerLevel = 1;
	public int STCostUnlock = 0;;
	public int STCostLevelUP = 0;
	public int STCostLevelDown = 0;
	public int STCostReset = 0;
}
