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
	private WHeroesAddon p;
	
	private Player player;
	private Hero hero;
	private HeroClass hClass;
	
	//Player statistics
	private HashMap<String, Integer> skills = new HashMap<String, Integer>();
	private int playerPoints;
	
	//Creating instace of PlayerData for proper Player count PlayerPoints
	public PlayerData(WHeroesAddon plugin, Player player) {
		this.p = plugin;
		this.player = player;
		this.hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
		this.hClass = hero.getHeroClass();
		
		if (Integer.valueOf(playerPoints) == null) {
			playerPoints = countPlayerPoints();
		}
		
		p.setPlayerData(player, this);
	}
	
	public boolean upgradeSkill(Skill skill, int amount) {
		if (!hero.canUseSkill(skill)) 
			return false;

		playerPoints -= amount;
		skills.put(skill.getName().toLowerCase(), skills.get(skill.getName().toLowerCase()) + amount);
		
		p.getServer().getPluginManager().callEvent(
				new SkillPointChangeEvent(player, hClass, amount));
		return true;
	}
	
	public boolean downgradeSkill(Skill skill, int amount) {
		if (skills.get(skill.getName().toLowerCase()) > 0) return false;
		
		playerPoints += amount;
		skills.put(skill.getName(), skills.get(skill.getName()) - amount);
		
		p.getServer().getPluginManager().callEvent(
				new SkillPointChangeEvent(player, hClass, amount));
		return true;
	}
	
	public void setSkillLevel(Skill skill, int amount) {
		if (!hero.canUseSkill(skill) || !isLocked(skill)) return;
		if (amount < 0) amount = 0;
		skills.put(skill.getName(), amount);
	}
	
	public int getUsedPoints() {
		int points = 0;
		for (int skillPoints : skills.values()) {
			points += skillPoints;
		}
		return points;
	}
	
	public int countPlayerPoints() {
		int amount = (Integer)Properties.SKILLTREE_POINTS_ON_START.getValue()
				+ hero.getLevel() * (Integer)Properties.SKILLTREE_POINTS_PER_LEVEL.getValue()
				- getUsedPoints();
		return amount;
	}
	
	public boolean hasSkillUnlocked(Skill skill) {
		return hasSkill(skill) && (getSkillLevel(skill) > 0);
	}
	
	public boolean hasSkill(Skill skill) {
		return skills.containsKey(skill);
	}
	
	public boolean isLocked(Skill skill) {
		if (skill != null && hero.canUseSkill(skill)) {
			List<Skill> strongParents = p.getSkillTree(hero.getHeroClass()).getStrongParentSkills(skill);
			List<Skill> weakParents = p.getSkillTree(hero.getHeroClass()).getWeakParentSkills(skill);
			boolean skillLevel = p.getPlayerData(hero.getPlayer()).getSkillLevel(skill) < 1;
			boolean hasStrongParents = strongParents != null && !strongParents.isEmpty();
			boolean hasWeakParents = weakParents != null && !weakParents.isEmpty();
			return skillLevel && hasStrongParents || hasWeakParents;
		} else {
			return true;
		}
	}
	
	public boolean isMastered(Skill skill) {
		return hero.hasAccessToSkill(skill) ? (getSkillLevel(skill) >= getMaxLevel(skill)) : false;
	}
	
	public boolean canUnlock(Skill skill) {
		if (!hero.hasAccessToSkill(skill) || !hero.canUseSkill(skill)) {
			return false;
		}
		SkillTree st = p.getSkillTree(hClass);
		
		boolean hasStrongParents = (st.getStrongParentSkills(skill) != null) && (!st.getStrongParentSkills(skill).isEmpty());
		boolean hasWeakParents = (st.getWeakParentSkills(skill) != null) && (!st.getWeakParentSkills(skill).isEmpty());
		if (!hasStrongParents && !hasWeakParents) {
			return true;
		}
		if (hasStrongParents) {
			for (Skill name : st.getStrongParentSkills(skill)) {
				if (!isMastered(name)) {
					return false;
				}
			}
		}
		if (hasWeakParents) {
			for (Skill name : st.getWeakParentSkills(skill)) {
				if (isMastered(name)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	public int getSkillLevel(Skill skill) {
		if (!skills.containsKey(skill)) {
			WHeroesAddon.LOG.info("[WHeroesAddon] This player does not have " + skill.getName() + " skill!");
			return 0;
		}
		return skills.get(skill.getName());
	}
	
	public int getMaxLevel(Skill skill) {
		return p.getSkillTree(hClass).getMaxLevel(skill);
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
	
	public void reset() {
		skills = null;
		playerPoints = 0;
	}
	
	public Player getPlayer() { return player; }
	public int getPoints() { return playerPoints; }
	public HashMap<String, Integer> getSkillsPoints() { return skills; }
	protected void setPlayer(Player player) { this.player = player; }
	protected void setSkillPoints(HashMap<String, Integer> skills) { this.skills = skills; }
}
