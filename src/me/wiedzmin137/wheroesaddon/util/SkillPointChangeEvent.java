package me.wiedzmin137.wheroesaddon.util;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.heroes.characters.classes.HeroClass;

public class SkillPointChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	private HeroClass hClass;
	private int newValue;
	
	public SkillPointChangeEvent(Player player, HeroClass hClass, int newValue) {
		this.player = player;
		this.hClass = hClass;
		this.newValue = newValue;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
 
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}
	
	public HeroClass getHeroClass() {
		return hClass;
	}
	
	public int getSkillPoints() {
		return newValue;
	}
	
	public void setSkillPoints(int amount) {
		newValue = amount;
	}
}
