package me.wiedzmin137.wheroesaddon.data;

import java.util.HashMap;
import java.util.Map;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.SkillPointChangeEvent;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
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
	public HashMap<Skill, Boolean> lockedTable; 
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
		
//		for (String skill : hClass.getSkillNames()) {
//			actualizeMenuVariables(WHeroesAddon.heroes.getSkillManager().getSkill(skill));
//		}
		
		recountLock();
		
		p.setPlayerData(player, this);
	}
	
	public boolean upgradeSkill(Skill skill, int amount) {
		if (!hero.canUseSkill(skill)) 
			return false;

		playerPoints -= amount;
		try {
			skills.put(skill.getName(), skills.get(skill.getName()) + amount);
		} catch (NullPointerException e) {
			skills.put(skill.getName(), amount);
		}
		p.getServer().getPluginManager().callEvent(
				new SkillPointChangeEvent(player, hClass, amount));
		
//		actualizeMenuVariables(skill);
		return true;
	}
	
	public boolean downgradeSkill(Skill skill, int amount) {
		if (skills.get(skill.getName()) > 0) return false;
		
		playerPoints += amount;
		skills.put(skill.getName(), skills.get(skill.getName()) - amount);
		
		p.getServer().getPluginManager().callEvent(
				new SkillPointChangeEvent(player, hClass, amount));
		
//		actualizeMenuVariables(skill);
		return true;
	}
	
	public int countPlayerPoints() {
		int amount = (Integer) Properties.SKILLTREE_POINTS_ON_START.getValue()
				+ hero.getLevel() * (Integer) Properties.SKILLTREE_POINTS_PER_LEVEL.getValue()
				- getUsedPoints();
		return amount;
	}
	
	public boolean hasSkillUnlocked(Skill skill) {
		return hasSkill(skill) && (getSkillLevel(skill) > 0);
	}
	
	public boolean hasSkill(Skill skill) {
		return (skills == null) ? false : skills.containsKey(skill);
	}
	
	public boolean isLocked(Skill skill) {
		return (lockedTable == null) ? true : !lockedTable.get(skill);
	}
	
	public boolean isMastered(Skill skill) {
		return hero.hasAccessToSkill(skill) ? (getSkillLevel(skill) >= getMaxLevel(skill)) : false;
	}
	
	public boolean canUnlock(Skill skill) {
		if (!hero.hasAccessToSkill(skill) || !hero.canUseSkill(skill) || !isLocked(skill)) {
			return false;
		} else {
			return true;
		}
	}
	
	public void reset() {
		skills = null;
		playerPoints = 0;
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
	
	public void setSkillLevel(Skill skill, int amount) {
		if (!hero.canUseSkill(skill) || !isLocked(skill)) return;
		if (amount < 0) amount = 0;
		skills.put(skill.getName(), amount);
//		actualizeMenuVariables(skill);
	}
	
	public int getUsedPoints() {
		int points = 0;
		for (int skillPoints : skills.values()) {
			points += skillPoints;
		}
		return points;
	}
	
	public int getSkillLevel(Skill skill) {
		if (!hasSkill(skill)) {
			return 0;
		}
		return skills.get(skill.getName());
	}
	
	public int getMaxLevel(Skill skill) {
		return p.getSkillTree(hClass).getMaxLevel(skill);
	}
	
	public void recountLock() {
		lockedTable = new HashMap<Skill, Boolean>();
		for (String string : hClass.getSkillNames()) {
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(string);
			setLocked(skill, checkLocked(skill));
		}
	}
	
	public void setLocked(Skill skill, boolean var) {
		lockedTable.put(skill, var);
	}
	
	private boolean checkLocked(Skill skill) {
		if (skill instanceof PassiveSkill) return true;
		ConfigurationSection sec = Properties.getHeroesProperties(hClass).getConfigurationSection("permitted-skills." + skill.getName());
		if (sec == null) {
			WHeroesAddon.LOG.info("[ConfigurationSection Error] SkillName: " + skill.getName());
			return true;
		}
		if (sec.getInt("max-level") != 0) {
			if (getSkillLevel(skill) > 0) {
				return true;
			} else {
				if (sec.get("need-unlock") != null && sec.getBoolean("need-unlock") == true) {
					return false;
				}
			}
			
			HashMap<Skill, Integer> strongParents = p.getSkillTree(hero.getHeroClass()).getStrongParentSkills(skill);
			HashMap<Skill, Integer> weakParents = p.getSkillTree(hero.getHeroClass()).getWeakParentSkills(skill);
				
			boolean hasStrongParents = true;
			boolean hasWeakParents = true;
			
			searchW:
				if (weakParents != null && !weakParents.isEmpty()) {
					for (Map.Entry<Skill, Integer> wParents : weakParents.entrySet()) {
						switch (wParents.getValue()) {
						case 0:
							if (hasSkill(wParents.getKey())) {
								hasWeakParents = false;
							}
							break;
						case -1:
							if (!isMastered(wParents.getKey())) {
								hasWeakParents = false;
							}
							break;
						default:
							if (wParents.getValue() < 0) {
								WHeroesAddon.LOG.info("Some weak parents of " + wParents.getKey().getName() + " are not configured properly");
								break searchW;
							}
							if (!(getSkillLevel(wParents.getKey()) == wParents.getValue())) {
								hasWeakParents = false;
							}
						}
					}
				}

			searchS:
				if (strongParents != null && !strongParents.isEmpty()) {
					for (Map.Entry<Skill, Integer> sParents : strongParents.entrySet()) {
						switch (sParents.getValue()) {
						case 0:
							if (hasSkill(sParents.getKey())) {
								hasStrongParents = false;
							}
							break;
						case -1:
							if (!isMastered(sParents.getKey())) {
								hasStrongParents = false;
							}
							break;
						default:
							if (sParents.getValue() < 0) {
								WHeroesAddon.LOG.info("Some strong parents of " + sParents.getKey().getName() + " are not configured properly");
								break searchS;
							}
							if (!(getSkillLevel(sParents.getKey()) == sParents.getValue())) {
								hasStrongParents = false;
							}
						}
					}
				}
			return hasStrongParents || hasWeakParents;
		} else {
			return true;
		}
	}
	
	public Player getPlayer() { return player; }
	public int getPoints() { return playerPoints; }
	public HashMap<String, Integer> getSkillsPoints() { return skills; }
	protected void setPlayer(Player player) { this.player = player; }
	protected void setSkillPoints(HashMap<String, Integer> skills) { this.skills = skills; }
}
