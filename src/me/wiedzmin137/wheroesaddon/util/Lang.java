package me.wiedzmin137.wheroesaddon.util;

import org.apache.commons.lang.StringEscapeUtils;
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
	ERROR_PERMISSIONS("Error.Permissions", "&4You don\'t have enough permissions!"),
	ERROR_NOT_IN_GAME("Error.NotInGame", "&cYou must be in game to use this command"),
	ERROR_NOT_ENOUGH_ARGUMENTS("Error.NotEnoughArguments", "&cNot enough arguments, use: %argument%"),
	GUI_INVALID_SKILLS("GUI.InvaildSkills", "Skill %skill% has no valid identifiers and can not be used on the menu! Please contact the author to fix the skill."),
	GUI_LORE("GUI.Lore", "&eClick for use!"),
	GUI_LORE_LEVEL("GUI.LoreLevel", "&f&oSkillLevel: %level%/%maxLevel%"),
	GUI_LORE_MANA("GUI.LoreMana", "&f&oMana: %manaCost%"),
	GUI_TITLE_SKILL("GUI.TitleSkill", "&2&l[&r&a%skill%&2&l]"),
	GUI_TITLE_CHOOSE("GUI.TitleChoose", "&1== &bChoose your class! &1=="),
	SKILLTREE_GAIN("SkillTree.Gain", "You have gained %amount% skill points!"),
	SKILLTREE_ERROR_NO_ACCESS("SkillTree.Error.NoAccess", "&4You don\'t have this skill"),
	SKILLTREE_ERROR_TOO_LOW_LEVEL("SkillTree.Error.ToLowLevel", "&cThis skill is not a high enough level"),
	SKILLTREE_ERROR_NOT_NUMBER("SkillTree.Error.NotNumber", "&cPlease enter a vaild number of points to increase"),
	SKILLTREE_DOWN_LOCKED("SkillTree.Down.Locked", "You have locked %skill%!"),
	SKILLTREE_DOWN_NORMAL("SkillTree.Down.Normal", "%skill% - leveled down: %slevel%/%slevelmax%"),
	SKILLTREE_UP_NOT_TO_INCREASE("SkillTree.Up.NotToIncerase", "&cThis skill can\'t be increased"),
	SKILLTREE_UP_NOT_ENOUGH_SKILLPOINTS("SkillTree.Up.NotEnoughSkillPoints", "&cYou don\'t have enough SkillPoints."),
	SKILLTREE_UP_ALREADY_MASTERED("SkillTree.Up.AlreadyMastered", "&cThis skill has already been mastered."),
	SKILLTREE_UP_UNLOCK_CANNOT("SkillTree.Up.CannotUnlock", "You can\'t unlock this skill!"),
	SKILLTREE_UP_UNLOCK_SUCCESS("SkillTree.Up.UnlockSuccess", "You have unlocked %skill%! Level: %level%"),
	SKILLTREE_UP_MASTERED("SkillTree.Up.Mastered", "You have mastered %skill% at level %level%!"),
	SKILLTREE_UP_LEVELED("SkillTree.Up.Leveled", "%skill% leveled up: %slevel%/%slevelmax%"),
	HELP_1("Help.1", "&9&l=._______==&1[&b&oSkillTree&1]&9&l==_______.="),
	HELP_2("Help.2", "&b/&9ST Up &b<&oskill&b&b> [&oamount&b] &r- level up a skill"),
	HELP_3("Help.3", "&b/&9ST Down &b<&oskill&b> [&oamount&b] &r- de-levels a skill"),
	HELP_4("Help.4", "&b/&9ST GUI &r- show upgrading skills GUI (FUTURE)");
	
	
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
			return StringEscapeUtils.unescapeHtml(ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def))) + " ";
		return StringEscapeUtils.unescapeHtml(ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)));
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
