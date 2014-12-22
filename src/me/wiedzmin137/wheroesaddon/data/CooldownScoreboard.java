package me.wiedzmin137.wheroesaddon.data;

import java.util.Map;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.ImmutableList;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.Skill;

public class CooldownScoreboard implements Listener {
	
	@SuppressWarnings("unused")
	private WHeroesAddon plugin;
	private Scoreboard emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	
	public CooldownScoreboard(final WHeroesAddon plugin) {
		this.plugin = plugin;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : ImmutableList.copyOf(plugin.getServer().getOnlinePlayers())) {
					Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(player);
					
					Map<String, Long> tempMap = hero.getCooldowns();
					if (tempMap == null) {
						player.setScoreboard(emptyScoreboard);
						return;
					}
					
					final long time = System.currentTimeMillis();
					for (Map.Entry<String, Long> entry : tempMap.entrySet()) {
						if (entry.getKey().equals("global")) {
							continue;
						}
						final Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(entry.getKey());
						if (skill == null) {
								continue;
						} else {
							final long timeLeft = entry.getValue() - time;
							if (timeLeft <= 0L) {
								continue;
							}
							updateScoreboard(player, skill.getName(), (int) (timeLeft / 1000L));
						}
					}
				}
			}
		}, 0L, 20L);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		setScoreboard(event.getPlayer());
	}

	public static void setScoreboard(Player player) {
		Scoreboard scoreboard = player.getServer().getScoreboardManager().getNewScoreboard();
		Objective scoreboardObj = scoreboard.registerNewObjective("test", "dummy");
		scoreboardObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		scoreboardObj.setDisplayName(ChatColor.RED + "Cooldowns"); //TODO add Lang field
		player.setScoreboard(scoreboard);
	}
	 
	public static Scoreboard getScoreboard(Player player) {
		if (player.getScoreboard() == null) setScoreboard(player);
		return player.getScoreboard();
	}
	 
	@SuppressWarnings("deprecation")
	public static void updateScoreboard(Player player, String property, int value) {
		Scoreboard pScoreboard = getScoreboard(player);
		pScoreboard.getObjective("test").getScore(Bukkit.getOfflinePlayer(property)).setScore(value);
		player.setScoreboard(pScoreboard);
	}
	
}
