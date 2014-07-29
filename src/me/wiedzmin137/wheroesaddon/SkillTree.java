package me.wiedzmin137.wheroesaddon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.desht.scrollingmenusign.SMSException;
import me.desht.scrollingmenusign.SMSHandler;
import me.desht.scrollingmenusign.SMSMenu;
import me.desht.scrollingmenusign.SMSMenuItem;
import me.desht.scrollingmenusign.enums.SMSMenuAction;
import me.desht.scrollingmenusign.views.SMSInventoryView;
import me.wiedzmin137.wheroesaddon.util.Lang;
import me.wiedzmin137.wheroesaddon.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;

import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

/**
 * 
 * @author Wiedzmin137
 * SkillTree is Object which can tells you something about
 * skill inheritance and requirements.
 * 
 */
public class SkillTree {
	
	private HeroClass hClass;
	private WHeroesAddon plugin;
	
	private static SMSHandler smsHandler = WHeroesAddon.sms.getHandler();
	
	private HashMap<Skill, Integer> skillsWithMaxLevel = new HashMap<Skill, Integer>();
	
	//private HashMap<String, HashMap<Integer, Object>> skillLeveledSettings = new HashMap<String, HashMap<Integer, Object>>();
	
	private HashMap<Skill, HashMap<Skill, Integer>> skillStrongParents = new HashMap<Skill, HashMap<Skill, Integer>>();
	private HashMap<Skill, HashMap<Skill, Integer>> skillWeakParents = new HashMap<Skill, HashMap<Skill, Integer>>();
	
	public SkillTree(WHeroesAddon plugin, HeroClass hClass) {
		this.plugin = plugin;
		this.hClass = hClass;
		
		for (String skillNames : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillNames);
			
			//Getting max-level for each HeroClass skill
			int level = SkillConfigManager.getSetting(hClass, skill, "max-level", -1) == -1 ? -1 :
				SkillConfigManager.getSetting(hClass, skill, "max-level", -1);
			skillsWithMaxLevel.put(skill, level);

			//Getting all parents for each skill
			try {
				for (Map.Entry<String, Integer> string : getParentSkills(skill, "strong").entrySet()) {
					HashMap<Skill, Integer> skillParentStrong = new HashMap<Skill, Integer>();
					skillParentStrong.put(WHeroesAddon.heroes.getSkillManager().getSkill(string.getKey()), string.getValue());
					skillStrongParents.put(skill, skillParentStrong);
				}
			} catch (NullPointerException e) {}
			
			try {
				for (Map.Entry<String, Integer> string : getParentSkills(skill, "weak").entrySet()) {
					HashMap<Skill, Integer> skillParentWeak = new HashMap<Skill, Integer>();
					skillParentWeak.put(WHeroesAddon.heroes.getSkillManager().getSkill(string.getKey()), string.getValue());
					skillWeakParents.put(skill, skillParentWeak);
				}
			} catch (NullPointerException e) {}
		}
	}
	
	public void createSkillTreeMenu() {
		SMSMenu menu = null;
		
		if (smsHandler == null) { return; }
		try {
			menu = smsHandler.getMenu(hClass + "-SkillTree");
		} catch (SMSException e) {
			menu = smsHandler.createMenu(hClass + "-SkillTree", Lang.TITLE_ITEM_GUI.toString().replace("%class%", hClass.getName()), plugin);
		}
		menu.removeAllItems();
		
		menu.setAutosave(false);
		menu.setAutosort(false);
		
		for (String skillNames : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillNames);
			if (skill instanceof ActiveSkill) {
				if (skill.getIdentifiers().length == 0) {
					WHeroesAddon.LOG.severe(Lang.GUI_INVALID_SKILLS.toString().replace("%skill%", skillNames));
				} else {
					String indicator = (String)SkillConfigManager.getSetting(hClass, skill, "indicator");
//					boolean glow = SkillConfigManager.getSetting(hClass, skill, "glow", false);
					
					//VERY unclear method to get all skill properties
//					Map<String, Object> values = Properties.getHeroesProperties(hClass).getConfigurationSection("permitted-skills." + skill.getName()).getValues(false);
//					String valuesInString = "";
//					for (String key : values.keySet()) {
//						if (key != "parents") {
//							valuesInString = valuesInString + key + ": <$*." + hClass.toString() + "." + key +">|";
//							
//							VariablesManager vmgr = smsHandler.getVariablesManager();
//							vmgr.set(null, "*." + hClass.toString() + "." + key, String.valueOf(values.get(key)));
//						}
//					}
					
					//Not best method to set lore
//					List<String> newLore = new ArrayList<String>();
//					for (String string : (String[])Properties.ST_ITEM.getValue()) {
//						String newString = string.replaceAll("{description}", skill.getDescription())
//												 .replaceAll("{heroclass}", hClass.getName());
//						newLore.add(newString);
//						if (newString.contains("{parents}")) {
//							newString.replace("{parents}", "");
//							for (Skill str : getWeakParentSkills(skill)) {
//								newLore.add(str.getName());
//							}
//							for (Skill str : getWeakParentSkills(skill)) {
//								newLore.add(str.getName());
//							}
//						}
//						
//						if (newString.contains("{values}")) {
//							newString.replace("{values}", "");
//							String[] splitedValues = valuesInString.split("|");
//							for (String str : splitedValues) {
//								newLore.add(str);
//							}
//						}
//					}
					
					SMSMenuItem skillClass = new SMSMenuItem.Builder(menu,
						Lang.GUI_TITLE_SKILL.toString().replace("%skill%", skill.getName()))
						.withCommand("/st down " + skill.getName() + " 1")
						.withAltCommand("/st up " + skill.getName() + " 1")
						.withIcon(indicator)
//						.withGlow(glow)
						.withLore("<$" + skill.getName() + ">/<$max" + skill.getName() + ">")
						.build();
					menu.addItem(skillClass);
				}
			}
		}
		menu.setAutosave(true);
		menu.setAutosort(true);
	}
	
	public static void showSkillTree(PlayerData pd, HeroClass hClass) {
		String hc = hClass.getName();
		SMSMenu menu = null;
		try {
			menu = smsHandler.getMenu(hc + "-SkillTree");
		} catch (SMSException e) {
			WHeroesAddon.getInstance().getSkillTree(hClass).createSkillTreeMenu();
			menu = smsHandler.getMenu(hc + "-SkillTree");
		}

		SMSInventoryView view = null;
		try {
			view = (SMSInventoryView)smsHandler.getViewManager().getView(hc);
		} catch (SMSException e) {
			view = new SMSInventoryView(hc, menu);
			view.update(menu, SMSMenuAction.REPAINT);
			smsHandler.getViewManager().registerView(view);
		}
		view.setAutosave(true);

		view.toggleGUI(pd.getPlayer());
	}
	
	public HashMap<Skill, Integer> getStrongParentSkills(Skill skill) {
		return skillStrongParents.get(skill);
	}

	public HashMap<Skill, Integer> getWeakParentSkills(Skill skill) {
		return skillWeakParents.get(skill);
	}
	
	public List<Skill> getSkills() {
		return new ArrayList<Skill>(skillsWithMaxLevel.keySet());
	}
	
	public HeroClass getHeroClass() {
		return hClass;
	}
	
	protected int getMaxLevel(Skill skill) {
		try {
			return skillsWithMaxLevel.get(skill);
		} catch (NullPointerException e) {
			return 0;
		}

	}
	
	private HashMap<String, Integer> getParentSkills(Skill skill, String weakOrStrong) {
		FileConfiguration hCConfig = Properties.getHeroesProperties(hClass);
		HashMap<String, Integer> parents = new HashMap<String, Integer>();
		if (hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents") == null) {
			return null;
		} else {
			for (Map.Entry<String, Object> map : hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents." + weakOrStrong).getValues(false).entrySet()) {
				parents.put(map.getKey(), (Integer) map.getValue());
			}
		}
		return parents;
	}
}
