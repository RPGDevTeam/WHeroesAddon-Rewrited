package me.wiedzmin137.wheroesaddon;

import java.util.HashMap;
import java.util.List;

import me.wiedzmin137.wheroesaddon.util.Properties;
import me.wiedzmin137.wheroesaddon.util.SkillPointChangeEvent;

import org.bukkit.entity.Player;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;


/**
 * 
 * @author Wiedzmin
 * Simple class storing data for each Player
 *
 */
public class PlayerData {
	private WHeroesAddon plugin;
	
	private Player player;
	private Hero hero;
	private HeroClass hClass;
	
	//Player statistics
	private HashMap<String, Integer> skills = new HashMap<String, Integer>();
	private int playerPoints;
	
	//Creating instace of PlayerData for proper Player count PlayerPoints
	public PlayerData(WHeroesAddon plugin, Player player) {
		this.plugin = plugin;
		this.hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
		this.hClass = hero.getHeroClass();
		
		for (String s : hClass.getSkillNames()) {
			if (skills.get(s) == null) {
				skills.put(s.toLowerCase(), 1);
			}
		}
		
		if (Integer.valueOf(playerPoints) == null) {
			playerPoints = countPlayerPoints();
		}
	}
	
	public boolean upgradeSkill(Skill skill, int amount) {
		Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
		if (!hero.canUseSkill(skill)) 
			return false;
		
		if (amount > playerPoints)
			return false;
		
		int level = getSkillLevel(skill);
		
		if (level >= getMaxLevel(skill))
			return false;
		
		//TODO add some checkers like skilllevel == 0, permissions, Vault
		
		playerPoints -= amount;
		skills.put(skill.getName().toLowerCase(), skills.get(skill.getName().toLowerCase()) + amount);
		
		plugin.getServer().getPluginManager().callEvent(
				new SkillPointChangeEvent(player, hClass, amount));
		return true;
	}
	
	public boolean downgradeSkill(Skill skill, int amount) {
		if (skills.get(skill.getName().toLowerCase()) > 0) return false;
		
		//TODO add some checkers
		
		playerPoints += amount;
		skills.put(skill.getName().toLowerCase(), skills.get(skill.getName().toLowerCase()) - amount);
		
		plugin.getServer().getPluginManager().callEvent(
				new SkillPointChangeEvent(player, hClass, amount));
		return true;
	}
	
	public int getUsedPoints() {
		int points = 0;
		for (int skillPoints : skills.values()) {
			points += skillPoints;
		}
		return points;
	}
	
	public int countPlayerPoints() {
		return (Integer)Properties.SKILLTREE_POINTS_ON_START.getValue()
				+ hero.getLevel() * (Integer)Properties.SKILLTREE_POINTS_PER_LEVEL.getValue()
				- getUsedPoints();
	}
	
	public boolean hasSkillUnlocked(Skill skill) {
		return hasSkill(skill) && (getSkillLevel(skill) > 0);
	}
	
	public boolean hasSkill(Skill skill) {
		return skills.containsKey(skill);
	}
	
	public boolean isLocked(Skill skill) {
		if (skill != null && hero.canUseSkill(skill)) {
			List<String> strongParents = WHeroesAddon.getInstance().getSkillTree(hero.getHeroClass()).getStrongParentSkills(skill);
			List<String> weakParents = WHeroesAddon.getInstance().getSkillTree(hero.getHeroClass()).getWeakParentSkills(skill);
			boolean skillLevel = WHeroesAddon.getInstance().getPlayerData(hero.getPlayer()).getSkillLevel(skill) < 1;
			boolean hasStrongParents = strongParents != null && !strongParents.isEmpty();
			boolean hasWeakParents = weakParents != null && !weakParents.isEmpty();
			return skillLevel && hasStrongParents || hasWeakParents;
		} else {
			return true;
		}
	}
	
	public int getSkillLevel(Skill skill) {
		if (!skills.containsKey(skill)) {
			WHeroesAddon.LOG.info("[WHeroesAddon] This player does not have " + skill.getName() + " skill!");
			return 0;
		}
		return skills.get(skill.getName());
	}
	
	public int getMaxLevel(Skill skill) {
		return plugin.getSkillTree(hClass).getMaxLevel(skill);
	}
	
	public void givePoints(int amount) {
		playerPoints += amount;
		if (playerPoints < 0) {
			playerPoints = 0;
		}
		new SkillPointChangeEvent(player, hClass, amount);
	}
	
	public void removePoints(int amount) {
		playerPoints -= amount;
		if (playerPoints < 0) {
			playerPoints = 0;
		}
		new SkillPointChangeEvent(player, hClass, amount);
	}

	public void setPoints(int amount) {
		playerPoints = amount;
		if (playerPoints < 0) {
			playerPoints = 0;
		}
	}
	
	public Player getPlayer() { return player; }
	public int getPlayerPoints() { return playerPoints; }
	public HashMap<String, Integer> getSkillPoints() { return skills; }
}
