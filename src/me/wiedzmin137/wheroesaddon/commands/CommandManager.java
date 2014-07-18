package me.wiedzmin137.wheroesaddon.commands;

import me.wiedzmin137.wheroesaddon.util.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
	private String[] help = new String[] {Lang.HELP_1.toString(), Lang.HELP_2.toString(), Lang.HELP_3.toString(), Lang.HELP_4.toString()};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skilltree")) {
			if (args.length > 0) {
				switch (args[0]) {
					case "up": SkillUpCommand.skillUp((Player)sender, args); break;
					case "down": SkillDownCommand.skillDown((Player)sender, args); break;
					case "choose": ChooseCommand.showClassChoose((Player)sender); break;
					//TODO case "GUI": plugin.getSkillTree(WHereosAddon.heroes.getCharacterManager().getHero().getHeroClass()).showSkillTree(); break;
					default: sender.sendMessage(help);
				}
			} else {
				sender.sendMessage(help);
			}
			return true;
		}
		return false;
	}
}
