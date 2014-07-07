package me.wiedzmin137.wheroesaddon;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
				"Database", ".sqlite");
		} else {
			sql = new MySQL(WHeroesAddon.LOG, 
				"WHeroesAddon ", 
				String.valueOf(Properties.MYSQL_HOST.getValue()), 
				(int)Properties.MYSQL_PORT.getValue(), 
				"WADDON", 
				String.valueOf(Properties.MYSQL_DATABASE.getValue()), 
				String.valueOf(Properties.MYSQL_PASSWORD.getValue()));
		}
		checkTables();
		sql.open();
	}
	
	public void checkTables() {
		if (sql instanceof SQLite ? !sql.open() : !sql.isOpen() && !sql.open());
		try {
			sql.query("CREATE TABLE IF NOT EXISTS playerdata ("
					+ "`name` VARCHAR(16) NOT NULL,"
					+ "`playerpoints` INT NOT NULL,"
					+ "PRIMARY KEY (`name`)"
					+ ")");
			
			sql.query("CREATE TABLE IF NOT EXISTS skills ("
					+ "`name` VARCHAR(16) NOT NULL,"
					+ "`skill` VARCHAR(16) NOT NULL,"
					+ "`level` INT NOT NULL,"
					+ "PRIMARY KEY (`name`)"
					+ ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void savePlayer(PlayerData playerData) {
		if (sql instanceof SQLite ? !sql.open() : !sql.isOpen() && !sql.open());
		try {
			ResultSet r;
			for (final Map.Entry<String, Integer> entry : playerData.getSkillsPoints().entrySet()) {
				String sqlS = "REPLACE INTO skills ("
						+ "name,skill,level"
						+ ") values(\""
						+ playerData.getPlayer().getName().toLowerCase() + "\",\""
						+ entry.getKey() + "\",\""
						+ entry.getValue()
						+ "\");";
				r = sql.query(sqlS);
				r.close();
			}
			
			String sqlS = "REPLACE INTO playerdata ("
					+ "name,playerpoints"
					+ ") values(\""
					+ playerData.getPlayer().getName().toLowerCase() + "\",\""
					+ playerData.getPoints() 
					+ "\");";
			r = sql.query(sqlS);
			r.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		WHeroesAddon.LOG.info("[WHeroesAddon] Saved player: " + playerData.getPlayer());
		sql.close();
		return;
	}
	
	public PlayerData loadPlayer(Player player) {
		if (sql instanceof SQLite ? !sql.open() : !sql.isOpen() && !sql.open());
		PlayerData pd = new PlayerData(plugin, player);
		try {
			ResultSet rs = sql.query("SELECT playerpoints FROM playerdata WHERE name='" + player.getName().toLowerCase() + "';");
			if (rs != null) {
				while (rs.next()) {
					pd.setPoints(rs.getInt("playerpoints"));
					pd.setPlayer(player);
				}
			}

			rs = sql.query("SELECT skill, level FROM skills WHERE name='" + player.getName().toLowerCase() + "';");
			if (rs != null) {
				while (rs.next()) {
					HashMap<String, Integer> skills = new HashMap<String, Integer>();
					skills.put(rs.getString("skill"), rs.getInt("level"));
					
					pd.setSkillPoints(skills);
				}
			}
			pd.countPlayerPoints();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		sql.close();
		WHeroesAddon.LOG.info("Loaded PD");
		savePlayer(pd);
		return pd;
	}
	
	public void resetPlayerData(PlayerData player) {
		//TODO
	}
	
	public Database getDatabase() {
		return sql;
	}
}
