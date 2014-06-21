package me.wiedzmin137.wheroesaddon;

import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

public class DataManager {
	private Database sql;
	
	public void setDatabase(boolean isMySQL) {
		if (isMySQL) {
			sql = new SQLite(Logger.getLogger("Minecraft"), 
				"[WHeroesAddon] ", 
				WHeroesAddon.getInstance().getDataFolder().getAbsolutePath(), 
				"Database", 
				".sqlite");
		} else {
			//TODO Create MySQL properties
			sql = new MySQL(Logger.getLogger("Minecraft"), 
				"[MyPlugin] ", 
				"localhost", 
				3306, 
				"myplugin", 
				"minecraft", 
				"password1");
		}
	}
	
	public boolean savePlayer(PlayerData playerData) {
		if (!sql.isOpen()) {
			sql.open();
		}
		try {
			sql.query("CREATE TABLE IF NOT EXISTS WADDON ("
					+ " `name` VARCHAR(16) NOT NULL,"
					+ "`hero-class` mediumtext,"
					+ "`player-points` INT NOT NULL,"
					//TODO add string to store HashMap<String, Integer>
					+ "PRIMARY KEY (`name`)"
					+ ")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sql.close();
		return false;
	}
	
	public Database getDatabase() {
		return sql;
	}
}
