package me.wiedzmin137.wheroesaddon.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wiedzmin137.wheroesaddon.PlayerData;
import me.wiedzmin137.wheroesaddon.WHeroesAddon;

public class CommandManager implements CommandExecutor {
	private WHeroesAddon plugin;
	
	public CommandManager(WHeroesAddon plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skilltree")) {
			if (args.length > 0) {
				switch (args[0]) {
					case "up": SkillUpCommand.skillUp((Player)sender, args); break;
					case "down": SkillDownCommand.skillDown((Player)sender, args); break;
					case "showpd": sender.sendMessage("" + plugin.getPlayerData((Player)sender).getPoints());
								   sender.sendMessage(plugin.getPlayerData((Player)sender).getSkillsPoints().toString()); break;
					case "pp": sender.sendMessage(String.valueOf(plugin.getPlayerData((Player)sender).getPoints()));
					case "save": plugin.getDatabaseManager().savePlayer(plugin.getPlayerData((Player)sender));
					case "recreate": new PlayerData(WHeroesAddon.getInstance(), (Player)sender);
					case "choose": ChooseCommand.showClassChoose((Player)sender); break;
					default: sender.sendMessage("[WHeroesAddon] Command not found. Use:");
						showInfo(sender);
				}
			}
			return true;
		}
		return false;
	}
	
	private void showInfo(CommandSender sender) {
		sender.sendMessage("Wait for Lang.HELP");
	}
}
