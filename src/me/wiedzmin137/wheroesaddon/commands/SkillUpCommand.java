package me.wiedzmin137.wheroesaddon.commands;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.data.Lang;
import me.wiedzmin137.wheroesaddon.data.PlayerData;

import org.bukkit.entity.Player;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillUpCommand {
	public static void skillUp(Player player, String[] args) {
		if(!player.hasPermission("skilltree.up")) {
			player.sendMessage(Lang.ERROR_PERMISSIONS.toString());
		} else if(args.length < 2) {
			player.sendMessage(Lang.ERROR_NOT_ENOUGH_ARGUMENTS.toString().replace("%argument%", "/skill up (skill) [amount]"));
		} else if(!(player instanceof Player)) {
			player.sendMessage(Lang.ERROR_NOT_IN_GAME.toString());
		} else {
			Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
			Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(args[1]);
			PlayerData pd = WHeroesAddon.getInstance().getPlayerData(player);
			if(skill != null && hero.hasAccessToSkill(skill.getName())) {
				if (pd.getMaxLevel(skill) == -1) {
					player.sendMessage(Lang.SKILLTREE_UP_NOT_TO_INCREASE.toString());
				} else {
					int pointsToIncrease;
					try {
						pointsToIncrease = (args.length > 2) ? Integer.parseInt(args[2]) : 1;
					} catch (NumberFormatException e) {
						player.sendMessage(Lang.SKILLTREE_ERROR_NOT_NUMBER.toString());
						return;
					}
					
					if (pd.getPoints() < pointsToIncrease && !player.hasPermission("skilltree.override.usepoints")) {
						player.sendMessage(Lang.SKILLTREE_UP_NOT_ENOUGH_SKILLPOINTS.toString());
					} else if (pd.getMaxLevel(skill) < pd.getSkillLevel(skill) + pointsToIncrease) {
						player.sendMessage(Lang.SKILLTREE_UP_ALREADY_MASTERED.toString());
					} else if (pd.isLocked(skill) && !pd.canUnlock(skill)) {
						player.sendMessage(Lang.SKILLTREE_UP_UNLOCK_CANNOT.toString());
					} else {
						if(!player.hasPermission("skilltree.override.usepoints")) {
							pd.removePoints(pointsToIncrease);
						}
						pd.upgradeSkill(skill, pointsToIncrease);
						WHeroesAddon.getInstance().getDatabaseManager().savePlayer(pd);
						hero.addEffect(new Effect(skill, skill.getName()));
						if (pd.isLocked(skill)) {
							player.sendMessage(Lang.SKILLTREE_UP_UNLOCK_SUCCESS.toString()
								.replace("%skill%", skill.getName())
								.replace("%level%", String.valueOf(pd.getSkillLevel(skill))));
						} else if (pd.isMastered(skill)) {
							player.sendMessage(Lang.SKILLTREE_UP_MASTERED.toString()	
								.replace("%skill%", skill.getName())
								.replace("%level%", String.valueOf(pd.getSkillLevel(skill))));
						} else {
							player.sendMessage(Lang.SKILLTREE_UP_LEVELED.toString()
								.replace("%skill%", skill.getName())
								.replace("%slevel%", String.valueOf(pd.getSkillLevel(skill)))
								.replace("%slevelmax%", String.valueOf(pd.getMaxLevel(skill))));
						}
					}
				}
			} else {
				player.sendMessage(Lang.SKILLTREE_ERROR_NO_ACCESS.toString());
			}
		}
	}
}
