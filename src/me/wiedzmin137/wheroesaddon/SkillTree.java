package me.wiedzmin137.wheroesaddon;

import java.util.HashMap;
import java.util.List;

import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillTree {
	private HeroClass hClass;
	private WHeroesAddon plugin;
	
	private HashMap<Integer, Skill> slots;
	
	public SkillTree(WHeroesAddon plugin, HeroClass hClass) {
		this.plugin = plugin;
		this.hClass = hClass;
	}
	
	public void arrange() {
		int count = 1;
		for (String skillNames : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(skillNames);
			slots.put(count, skill);
			count++;
		}
	}
}
