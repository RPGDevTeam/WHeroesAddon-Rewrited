package me.wiedzmin137.wheroesaddon.commands;

import me.wiedzmin137.wheroesaddon.SkillTree;
import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
	private WHeroesAddon plugin;
	
	public CommandManager(WHeroesAddon plugin) {
		this.plugin = plugin;
	}
	
	private String[] help = new String[] {Lang.HELP_1.toString(), Lang.HELP_2.toString(), Lang.HELP_3.toString(), Lang.HELP_4.toString()};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skilltree")) {
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
					case "up": SkillUpCommand.skillUp((Player)sender, args); break;
					case "down": SkillDownCommand.skillDown((Player)sender, args); break;
					case "gui": SkillTree.showSkillTree(plugin.getPlayerData((Player) sender), WHeroesAddon.heroes.getCharacterManager().getHero((Player) sender).getHeroClass()); break;
					case "pd": sender.sendMessage(plugin.getPlayerData((Player) sender).getSkillsPoints().toString()); break;
					default: sender.sendMessage(help);
				}
			} else {
				sender.sendMessage(help);
				sender.sendMessage(Lang.SKILLTREE_POINTS_AMOUNT.toString().replace("%points%", String.valueOf(plugin.getPlayerData((Player) sender).getPoints())));
			}
			return true;
		}
		return false;
	}
}
