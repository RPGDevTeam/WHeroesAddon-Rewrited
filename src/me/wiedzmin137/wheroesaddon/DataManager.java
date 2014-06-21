package me.wiedzmin137.wheroesaddon;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;
import me.wiedzmin137.wheroesaddon.util.Properties;

import org.bukkit.entity.Player;

public class DataManager {
	private Database sql;
	private WHeroesAddon plugin;
	
	public DataManager(WHeroesAddon wHeroesAddon) {
		this.plugin = wHeroesAddon;
	}

	public void setDatabase(boolean isMySQL) {
		if (!isMySQL) {
			sql = new SQLite(WHeroesAddon.LOG, 
				"WHeroesAddon ", 
				WHeroesAddon.getInstance().getDataFolder().getAbsolutePath(), 
				"Database");
		} else {
			sql = new MySQL(WHeroesAddon.LOG, 
				"WHeroesAddon ", 
				String.valueOf(Properties.MYSQL_HOST.getValue()), 
				(int)Properties.MYSQL_PORT.getValue(), 
				"WADDON", 
				String.valueOf(Properties.MYSQL_DATABASE.getValue()), 
				String.valueOf(Properties.MYSQL_PASSWORD.getValue()));
		}
	}
	
	public void savePlayer(PlayerData playerData) {
		if (!sql.isOpen()) sql.open();
		try {
			sql.query("CREATE TABLE IF NOT EXISTS WADDON ("
					+ " `name` VARCHAR(16) NOT NULL,"
					+ "`player-points` INT NOT NULL,"
					//+ "`skills` (Whatever) NOT NULL,"
					//TODO add string to store HashMap<String, Integer>
					+ "PRIMARY KEY (`name`)"
					+ ")");
			
			//TODO Get PlayerData and save it!
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("name", playerData.getPlayer().getName());
			hm.put("skills", playerData.getSkillPoints());
			hm.put("player-points", playerData.getPlayerPoints());
			//TODO save it (find nice method to do that)
			//sql.query("");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql.close();
		return;
	}
	
	public PlayerData loadPlayer(Player player) {
		if (!sql.isOpen()) sql.open();
		ResultSet rs;
		PlayerData pd;
		try {
			rs = sql.query("SELECT * FROM WADDON WHERE name='" + player.getName() + "'");
			
			pd = new PlayerData(plugin, player);
			pd.setPoints(rs.getInt("player-point"));
			//TODO load HashMap<String, Integer> with skills levels
			pd.countPlayerPoints();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		sql.close();
		return pd;
	}
	
	public void resetPlayerData(PlayerData player) {
		//TODO
	}
	
	public Database getDatabase() {
		return sql;
	}
}
