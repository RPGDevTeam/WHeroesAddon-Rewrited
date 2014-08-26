package me.wiedzmin137.wheroesaddon.commands;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.data.Lang;
import me.wiedzmin137.wheroesaddon.data.PlayerData;

import org.bukkit.entity.Player;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.Skill;

public class SkillDownCommand {
	public static void skillDown(Player player, String[] args) {
		
		if(!player.hasPermission("skilltree.down")) {
			player.sendMessage(Lang.ERROR_PERMISSIONS.toString());
		} else if (args.length < 2) {
			player.sendMessage(Lang.ERROR_NOT_ENOUGH_ARGUMENTS.toString().replace("%argument%", "/st down (skill) [amount]"));
		} else if (!(player instanceof Player)) {
			player.sendMessage(Lang.ERROR_NOT_IN_GAME.toString());
		} else {
			Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
			if (!hero.hasAccessToSkill(args[2])) {
				player.sendMessage(Lang.SKILLTREE_ERROR_NO_ACCESS.toString());
			} else {
				PlayerData pd = WHeroesAddon.getInstance().getPlayerData(player);
				Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(args[2]);
				int pointsDecrease;
				try {
					pointsDecrease = (args.length > 3) ? Integer.parseInt(args[3]) : 1;
				} catch (NumberFormatException var7) {
					player.sendMessage(Lang.SKILLTREE_ERROR_NOT_NUMBER.toString());
					return;
				}
				
				if (pd.getSkillLevel(skill) < pointsDecrease) {
					player.sendMessage(Lang.SKILLTREE_ERROR_TOO_LOW_LEVEL.toString());
				} else {
					if (pd.getSkillLevel(skill) - pointsDecrease < 2) {
						if(!player.hasPermission("skilltree.lock")) {
							player.sendMessage(Lang.ERROR_PERMISSIONS.toString());
							return;
						}
						
						if (!player.hasPermission("skilltree.override.usepoints")) {
							pd.setPoints(pd.getPoints() + pointsDecrease);
						}
						
						pd.downgradeSkill(skill, pointsDecrease);
						hero.removeEffect(hero.getEffect(skill.getName()));
						WHeroesAddon.getInstance().getDatabaseManager().savePlayer(pd);
						player.sendMessage(Lang.SKILLTREE_DOWN_LOCKED.toString().replace("%skill%", skill.getName()));
					} else {
						if(!player.hasPermission("skilltree.override.usepoints")) {
							pd.setPoints(pd.getPoints() + pointsDecrease);
						}
						
						pd.downgradeSkill(skill, pointsDecrease);
						WHeroesAddon.getInstance().getDatabaseManager().savePlayer(pd);
						player.sendMessage(Lang.SKILLTREE_DOWN_NORMAL.toString()
								.replace("%skill%", skill.getName())
								.replace("%slevel%", String.valueOf(pd.getSkillLevel(skill)))
								.replace("%slevelmax%", String.valueOf(pd.getMaxLevel(skill))));
					}
				}
			}
		}
	}
}
