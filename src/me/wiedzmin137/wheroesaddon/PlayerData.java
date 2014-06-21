package me.wiedzmin137.wheroesaddon;

import java.util.HashMap;

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
	private HeroClass hClass;
	private Hero hero;
	
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
		
		//TODO if player have not playerPoints, count it
		if (Integer.valueOf(playerPoints) == null) {
			playerPoints = countPlayerPoints(player);
		}
		
		//TODO add saving method
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
		
		//Upgrader
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
	
	public int countPlayerPoints(Player player) {
		//TODO countPlayerPoints
		return 0;
	}
	
	public boolean hasSkillUnlocked(Skill skill) {
		return hasSkill(skill) && getSkillLevel(skill) > 0;
	}
	
	public boolean hasSkill(Skill skill) {
		return skills.containsKey(skill);
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
