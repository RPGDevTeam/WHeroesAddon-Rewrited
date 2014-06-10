package me.wiedzmin137.wheroesaddon;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;

public class PlayerSkills {
	private WHeroesAddon plugin;
	private Player player;
	private HeroClass hClass;
	private int playerPoints;
	private HashMap<String, Integer> skills = new HashMap<String, Integer>();
	
	PlayerSkills(WHeroesAddon plugin, Player player) {
		this.plugin = plugin;
		this.hClass = WHeroesAddon.heroes.getCharacterManager().getHero(player).getHeroClass();
		
		for (String s : hClass.getSkillNames()) {
			if (skills.get(s) == null) {
				skills.put(s.toLowerCase(), 1);
			}
		}
		
		//TODO if player have not playerPoints, count it
	}
	
	public boolean upgradeSkill(Skill skill, int amount) {
		Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
		if (!hero.canUseSkill(skill)) {
			return false;
		}
		//TODO add some checkers like level == 0, permissions, Vault
		
		//Upgrader
		playerPoints -= amount;
        skills.put(skill.getName().toLowerCase(), skills.get(skill.getName().toLowerCase()) + 1);
		return true;
	}
	
	public int countPlayerPoints(Player player) {
		//TODO countPlayerPoints
		return 0;
	}
	
	public Player getPlayer() { return player; }
	public int getPlayerPoints() { return playerPoints; }
	public HashMap<String, Integer> getSkillPoints() { return skills; }
}
