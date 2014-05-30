package me.wiedzmin137.wheroesaddon.util;

public enum Properties {
	SKILLTREE_ENABLED("SkillTree.Enabled", "true"),
	SKILLTREE_POINTS_PER_LEVEL("SkillTree.PointsPerLevel", "1"),
	SKILLTREE_COST_UNLOCK("SkillTree.CostToUnlock", "0"),
	SKILLTREE_COST_LEVEL_UP("SkillTree.CostToLevelUp", "0"),
	SKILLTREE_COST_LEVEL_DOWN("SkillTree.CostToLevelDown", "0"),
	SKILLTREE_COST_RESET("SkillTree.CostToReset", "0");

	private String path;
	private Object def;
	
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
}
