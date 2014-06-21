package me.wiedzmin137.wheroesaddon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.desht.scrollingmenusign.SMSException;
import me.desht.scrollingmenusign.SMSHandler;
import me.desht.scrollingmenusign.SMSMenu;
import me.desht.scrollingmenusign.SMSMenuItem;
import me.desht.scrollingmenusign.ScrollingMenuSign;
import me.desht.scrollingmenusign.enums.SMSMenuAction;
import me.desht.scrollingmenusign.views.SMSInventoryView;
import me.wiedzmin137.wheroesaddon.util.Lang;
import me.wiedzmin137.wheroesaddon.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
	private ScrollingMenuSign sms;
	
	private static SMSHandler smsHandler;
	
	private List<Skill> skills;
	private HashMap<Skill, Integer> skillLevel;
	
	public List<Skill> SkillStrongParents = new ArrayList<Skill>();
	public List<Skill> SkillWeakParents = new ArrayList<Skill>();
	
	public SkillTree(WHeroesAddon plugin, HeroClass hClass, ScrollingMenuSign menuPlugin) {
		this.plugin = plugin;
		this.hClass = hClass;
		this.sms = menuPlugin;
		smsHandler = sms.getHandler();
		
		for (String skillNames : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillNames);
			skills.add(skill);
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
					SMSMenuItem skillClass = new SMSMenuItem.Builder(menu,
						Lang.GUI_TITLE_SKILL.toString().replace("%skill%", skill.getName()))
						.withCommand("/st down " + skill.getName() + " 1")
						.withAltCommand("/st up " + skill.getName() + " 1")
						.withIcon(indicator)
						.withLore(Lang.GUI_LORE.toString(), "", skill.getDescription())
						.build();
					menu.addItem(skillClass);
				}
			}
		}
		menu.setAutosave(true);
		menu.setAutosort(true);
	}
	
	public static void showSkillTree(Player player, HeroClass hClass) {
		//TODO create something to change lore of items by subtitutions.
		
		//TODO add level of skills - by quantity of items
		//TODO get statistics from .getSettings() and take them to the lore
		//TODO add full language support
		//TODO add e.g. .replace("{Level}", getSkillLevel(skill))
		//int skillLevel = plugin.getPlayerData(player).getSkillLevel(commandSendingHero, skill);
		//int skillMaxLevel = WAddonCore.getInstance().getSkillTree().getSkillMaxLevel(playerHero, skill);
		
		String hc = hClass.getName();
		SMSMenu menu = null;
		try {
			menu = smsHandler.getMenu(hc + " SkillTree");
		} catch (SMSException e) {
			menu = smsHandler.createMenu(hc + " SkillTree", Lang.TITLE_ITEM_GUI.toString().replace("%class%", hc), player);
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

		view.toggleGUI(player);
	}
	
	public List<String> getStrongParentSkills(Skill skill) {
		return getParentSkills(skill, "strong");
	}

	public List<String> getWeakParentSkills(Skill skill) {
		return getParentSkills(skill, "weak");
	}
	
	public List<String> getParentSkills(Skill skill, String weakOrStrong) {
		FileConfiguration hCConfig = Properties.getHeroesProperties(hClass);
		return (hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents") == null) ? null
				: hCConfig.getConfigurationSection("permitted-skills." + skill.getName() + ".parents")
				.getStringList(weakOrStrong);
	}
	
	protected int getMaxLevel(Skill skill) {
		return skillLevel.get(skill);
	}
	
	public List<Skill> getSkills() {
		return skills;
	}
	
	public HeroClass getHeroClass() {
		return hClass;
	}
}
