package me.wiedzmin137.wheroesaddon.commands;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.data.Properties;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandManager implements CommandExecutor, Listener {
	private WHeroesAddon plugin;
	
	public CommandManager(WHeroesAddon plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skilltree")) {
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
					case "up": SkillUpCommand.skillUp((Player)sender, args); break;
					case "down": SkillDownCommand.skillDown((Player)sender, args); break;
					case "pd": sender.sendMessage(plugin.getPlayerData((Player) sender)
							.lockedTable.toString()); break;
					case "playerdata": sender.sendMessage(plugin.pData.toString()); break;
					case "ps": sender.sendMessage(Properties.getHeroesProperties(WHeroesAddon.heroes.getClassManager().getClass(args[1]))
							.getConfigurationSection("permitted-skills." + args[2]).get(args[3]).toString());
				}
			} else {
				plugin.getSkillTree(WHeroesAddon.heroes.getCharacterManager().getHero((Player) sender).getHeroClass()).showMenu((Player) sender);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("classchoose")) { //TODO add argument in Properties
			plugin.getChooseMenu().open((Player) sender); 
		}
		return false;
	}
	
	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {	 
		Player sender = event.getPlayer();
		if (event.getMessage().equalsIgnoreCase("/skills")) {
			event.setCancelled(true);
			plugin.getSkillBook(WHeroesAddon.heroes.getCharacterManager().getHero(sender).getHeroClass()).open(sender);
		}
	}
}
