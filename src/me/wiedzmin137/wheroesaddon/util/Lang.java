package me.wiedzmin137.wheroesaddon.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
* An enum for requesting strings from the language file.
* @author Gomeow
* @author Wiedzmin137
*/
public enum Lang {
	TITLE("Title.MainTitle", "&1[&bSkillTree&1]&r"),
	TITLE_ITEM_GUI("Title.ItemGUI", "&1[&9 %class% &1]&r"),
	GUI_INVAILD_SKILLS("GUI.InvaildSkills", "Skill %skill% has no valid identifiers and can not be used on the menu! Please contact the author to fix the skill."),
	GUI_LORE("GUI.Lore", "&eClick for use!"),
	GUI_LORE_LEVEL("GUI.LoreLevel", "&f&oSkillLevel: %level%/%maxLevel%"),
	GUI_LORE_MANA("GUI.LoreMana", "&f&oMana: %manaCost%"),
	GUI_TITLE_SKILL("GUI.TitleSkill", "&2&l[&r&a%skill%&2&l]"),
	GUI_TITLE_CHOOSE("GUI.TitleChoose", "&1== &bChoose your class! &1=="),
	SKILLTREE_GAIN("SkillTree.Gain", "You have gained %amount% skill points!");
	
	private String path;
	private String def;
	private static YamlConfiguration LANG;
	
	/**
	 * Lang enum constructor.
	 * @param path The string path.
	 * @param start The default string.
	 */
	Lang(String path, String start) {
		this.path = path;
		this.def = start;
	}
	
	/**
	 * Set the {@code YamlConfiguration} to use.
	 * @param config The config to set.
	 */
	public static void setFile(YamlConfiguration config) {
		LANG = config;
	}
	
	@Override
	public String toString() {
		if (this == TITLE)
			return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " ";
		return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
	}
	
	/**
	 * Get the default value of the path.
	 * @return The default value of the path.
	 */
	public String getDefault() {
		return this.def;
	}
	
	/**
	 * Get the path to the string.
	 * @return The path to the string.
	 */
	public String getPath() {
		return this.path;
	}
}
