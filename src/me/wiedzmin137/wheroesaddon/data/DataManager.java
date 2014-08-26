package me.wiedzmin137.wheroesaddon.data;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.database.Database;
import me.wiedzmin137.wheroesaddon.util.database.DatabaseConfigBuilder;
import me.wiedzmin137.wheroesaddon.util.database.Table;

import org.bukkit.entity.Player;

public class DataManager {
	private WHeroesAddon plugin;

    public static final Table PLAYER_POINTS = new Table("PLAYER_POINTS", "NAME VARCHAR(16),player_points INT");
    public static final Table PLAYER_SKILLS = new Table("PLAYER_SKILLS", "NAME VARCHAR(16),skill VARCHAR(16),level INT");

	private Database database;
	
	public DataManager(WHeroesAddon wHeroesAddon) {
		this.plugin = wHeroesAddon;
		
		File sqliteFile = new File(wHeroesAddon.getDataFolder(), "database.db");
		DatabaseConfigBuilder config = new DatabaseConfigBuilder(sqliteFile);
		database = DataManager.getDatabase(config);
		
		try {
			database.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		database.registerTable(PLAYER_POINTS);
		database.registerTable(PLAYER_SKILLS);
	}

	public PlayerData loadPlayer(Player player) {
		checkConnection();
		PlayerData pd = new PlayerData(plugin, player);
		
		pd.setSkillPoints(database.get(PLAYER_SKILLS, "skill", "level", "NAME", player.getName().toLowerCase()));
		pd.setPoints(pd.countPlayerPoints());
		pd.setupLock();

		savePlayer(pd);
		return pd;
	}
	
	public void savePlayer(PlayerData playerData) {
		checkConnection();
		if (database.contains(PLAYER_POINTS, "NAME", playerData.getPlayer().getName().toLowerCase())) {
			database.update(PLAYER_POINTS, "NAME", "player_points", playerData.getPlayer().getName().toLowerCase(), playerData.getPoints());
		} else {
			database.set(PLAYER_POINTS, playerData.getPlayer().getName().toLowerCase(), playerData.getPoints());
		}
		
		for (final Map.Entry<String, Integer> entry : playerData.getSkillsPoints().entrySet()) {
			if (database.contains(PLAYER_SKILLS, "NAME", "skill", playerData.getPlayer().getName().toLowerCase(), entry.getKey())) {
				database.update(PLAYER_SKILLS, "skill", "level", "NAME", entry.getKey(), entry.getValue(), playerData.getPlayer().getName().toLowerCase());
			} else {
				database.set(PLAYER_SKILLS, playerData.getPlayer().getName().toLowerCase(), entry.getKey(), entry.getValue());
			}
		}
	}
	
	private void checkConnection() {
		if (!database.isConnected()) {
			try {
				database.connect();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Database getDatabase(DatabaseConfigBuilder builder) {
		return new Database(WHeroesAddon.getInstance(), builder);
	}
	
	public Database getDatabase() { return database; }
	protected void disconnect() { database.close(); }
}
