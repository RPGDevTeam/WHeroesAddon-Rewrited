package me.wiedzmin137.wheroesaddon.listeners;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.herocraftonline.heroes.api.events.ClassChangeEvent;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;

public class PlayerListener implements Listener {
	private WHeroesAddon p;
	
	public PlayerListener(WHeroesAddon plugin /*, SkillTreeManager or somewhat*/) {
		this.p = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		//TODO load player data
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		//TODO save player data
	}
	
	@EventHandler
	public void onLevelChange(HeroChangeLevelEvent event) {
		//TODO change players SkillPoints
	}
	
	@EventHandler
	public void onClassChangeEvent(ClassChangeEvent event) {
		//TODO change players SkillTree
	}
	
	@EventHandler
	public void onSkillUse(SkillUseEvent event) {
		//TODO change Skill usage properties by SkillPoints in SkillTree
	}
	
	@EventHandler
	public void onSkillDamage(SkillDamageEvent event) {
		//TODO change skill damages by SkillPoints in SkillTree
	}
}
