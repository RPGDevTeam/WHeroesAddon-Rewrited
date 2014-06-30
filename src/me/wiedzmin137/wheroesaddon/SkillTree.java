package me.wiedzmin137.wheroesaddon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.desht.scrollingmenusign.SMSException;
import me.desht.scrollingmenusign.SMSHandler;
import me.desht.scrollingmenusign.SMSMenu;
import me.desht.scrollingmenusign.SMSMenuItem;
import me.desht.scrollingmenusign.ScrollingMenuSign;
import me.desht.scrollingmenusign.enums.SMSMenuAction;
import me.desht.scrollingmenusign.variables.VariablesManager;
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
	private static SMSHandler smsHandler = ((ScrollingMenuSign) WHeroesAddon.getInstance().getServer().getPluginManager().getPlugin("ScrollingMenuSign")).getHandler();
	
	private HeroClass hClass;
	private WHeroesAddon plugin;
	
	private HashMap<Skill, Integer> skillsWithMaxLevel = new HashMap<Skill, Integer>();
	
	//private HashMap<String, HashMap<Integer, Object>> skillLeveledSettings = new HashMap<String, HashMap<Integer, Object>>();
	
	private HashMap<Skill, List<Skill>> skillStrongParents = new HashMap<Skill, List<Skill>>();
	private HashMap<Skill, List<Skill>> skillWeakParents = new HashMap<Skill, List<Skill>>();
	
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
				for (String string : getParentSkills(skill, "strong")) {
					List<Skill> skillParentStrong = new ArrayList<Skill>();
					skillParentStrong.add(WHeroesAddon.heroes.getSkillManager().getSkill(string));
					skillStrongParents.put(skill, skillParentStrong);
				}
			} catch (NullPointerException e) {}
			
			try {
				for (String string : getParentSkills(skill, "weak")) {
					List<Skill> skillParentWeak = new ArrayList<Skill>();
					skillParentWeak.add(WHeroesAddon.heroes.getSkillManager().getSkill(string));
					skillWeakParents.put(skill, skillParentWeak);
				}
			} catch (NullPointerException e) {}
		}
	}
	
	public void createSkillTreeMenu() {
		SMSMenu menu = null;
		
		if (smsHandler == null) { return; }
		try {
			menu = smsHandler.getMenu(hClass + " SkillTree");
		} catch (SMSException e) {
			menu = smsHandler.createMenu(hClass + " SkillTree", Lang.TITLE_ITEM_GUI.toString().replace("%class%", hClass.getName()), plugin);
		}
		menu.removeAllItems();
		
		menu.setAutosave(false);
		menu.setAutosort(false);
		
		for (String skillNames : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillNames);
			if (skill instanceof ActiveSkill) {
				if (skill.getIdentifiers().length == 0) {
					WHeroesAddon.LOG.severe(Lang.GUI_INVAILD_SKILLS.toString().replace("%skill%", skillNames));
				} else {
					String indicator = (String)SkillConfigManager.getSetting(hClass, skill, "indicator");
					boolean glow = (boolean)SkillConfigManager.getSetting(hClass, skill, "glow");
					
					//VERY unclear method to get all skill properties
					Map<String, Object> values = Properties.getHeroesProperties(hClass).getConfigurationSection("permitted-skills." + skill.getName()).getValues(false);
					String valuesInString = "";
					for (String key : values.keySet()) {
						if (key != "parents") {
							valuesInString = valuesInString + key + ": <$*." + hClass.toString() + "." + key +">|";
							
							VariablesManager vmgr = smsHandler.getVariablesManager();
							vmgr.set(null, "*." + hClass.toString() + "." + key, String.valueOf(values.get(key)));
						}
					}
					
					//Not best method to set lore
					List<String> newLore = new ArrayList<String>();
					for (String string : (String[])Properties.ST_ITEM.getValue()) {
						String newString = string.replaceAll("{description}", skill.getDescription())
												 .replaceAll("{heroclass}", hClass.getName());
						newLore.add(newString);
						if (newString.contains("{parents}")) {
							newString.replace("{parents}", "");
							for (Skill str : getWeakParentSkills(skill)) {
								newLore.add(str.getName());
							}
							for (Skill str : getWeakParentSkills(skill)) {
								newLore.add(str.getName());
							}
						}
						
						if (newString.contains("{values}")) {
							newString.replace("{values}", "");
							String[] splitedValues = valuesInString.split("|");
							for (String str : splitedValues) {
								newLore.add(str);
							}
						}
					}
					
					SMSMenuItem skillClass = new SMSMenuItem.Builder(menu,
						Lang.GUI_TITLE_SKILL.toString().replace("%skill%", skill.getName()))
						.withCommand("/st down " + skill.getName() + " 1")
						.withAltCommand("/st up " + skill.getName() + " 1")
						.withIcon(indicator)
						.withGlow(glow)
						.withLore(newLore)
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
			menu = smsHandler.getMenu(hc + " SkillTree");
		} catch (SMSException e) {
			WHeroesAddon.getInstance().getSkillTree(hClass).createSkillTreeMenu();
			menu = smsHandler.getMenu(hc + " SkillTree");
			//menu = smsHandler.createMenu(hc + " SkillTree", Lang.TITLE_ITEM_GUI.toString().replace("%class%", hc), pd.getPlayer());
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
	
	public List<Skill> getStrongParentSkills(Skill skill) {
		return skillStrongParents.get(skill);
	}

	public List<Skill> getWeakParentSkills(Skill skill) {
		return skillWeakParents.get(skill);
	}
	
	public List<Skill> getSkills() {
		return new ArrayList<Skill>(skillsWithMaxLevel.keySet());
	}
	
	public HeroClass getHeroClass() {
		return hClass;
	}
	
	protected int getMaxLevel(Skill skill) {
		return skillsWithMaxLevel.get(skill);
	}
	
	private List<String> getParentSkills(Skill skill, String weakOrStrong) {
		FileConfiguration hCConfig = Properties.getHeroesProperties(hClass);
		return (hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents") == null) ? null :
				hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents")
					.getStringList(weakOrStrong);
	}
}
