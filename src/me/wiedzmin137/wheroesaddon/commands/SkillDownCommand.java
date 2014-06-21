package me.wiedzmin137.wheroesaddon.commands;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.SkillPointChangeEvent;

import org.bukkit.entity.Player;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillDownCommand {
	public static void skillDown(Player player, Skill skill) {
		Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
		//TODO check is command
			//TODO create specific constructor for console only for skilldown Player
		
		//TODO create checker for Player skill
		
		//SkillDown!
		new SkillPointChangeEvent(player, hero.getHeroClass(), 0);
	}
}
