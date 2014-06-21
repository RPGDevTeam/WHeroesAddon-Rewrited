package me.wiedzmin137.wheroesaddon.util;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillUnlockEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	private HeroClass hClass;
	private Skill skill;
	
	public SkillUnlockEvent(Player player, HeroClass hClass, Skill skill) {
		this.player = player;
		this.hClass = hClass;
		this.skill = skill;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}
	
	public HeroClass getHeroClass() {
		return hClass;
	}
	
	public Skill getSkill() {
		return skill;
	}
}
