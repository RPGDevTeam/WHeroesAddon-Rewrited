package me.wiedzmin137.wheroesaddon.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;

public class CommandManager implements CommandExecutor {
	private WHeroesAddon plugin;
	
	public CommandManager(WHeroesAddon plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("skilltree")) {
				if (args.length > 0) {
					switch (args[0]) {
						case "Test": sender.sendMessage("It should work!"); break;
						case "Choose": ChooseCommand.showClassChoose((Player)sender); break;
						default: sender.sendMessage("[WHeroesAddon] Command not found. Use:");
								 showInfo(sender);
					}
				}
			} else {
				showInfo(sender);
			}
		} else {
			WHeroesAddon.LOG.info("[WHeroesAddon] Console senders are not support for now!");
		}
		return false;
	}
	
	private void showInfo(CommandSender sender) {
		sender.sendMessage("Wait for Lang.HELP");
	}
}
