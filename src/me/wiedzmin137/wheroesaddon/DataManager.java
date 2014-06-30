package me.wiedzmin137.wheroesaddon;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	}
	
	public void savePlayer(PlayerData playerData) {
		if (sql instanceof SQLite ? !sql.open() : !sql.isOpen() && !sql.open());
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
			//hm.put("skills", playerData.getSkillsPoints());
			hm.put("player-points", playerData.getPoints());
			
			String[] aKeys = hm.keySet().toArray(new String[hm.keySet().size()]);
			List<String> keys = Arrays.asList("name");
			String code = "";
			if (sql.query("SELECT `name` FROM WADDON WHERE name='" + playerData.getPlayer().getName() + "'") == null) {
				code = "INSERT INTO WADDON ";
				String keycode = "(";
				String valuecode = " VALUES (";
				for (int count = 0; count < hm.size(); count++) {
					keycode += "`" + aKeys[count] + "`";
					valuecode += "?";
					if ((count < (hm.size() - 1))) {
						keycode += ", ";
						valuecode += ",";
					} else {
						keycode += ")";
						valuecode += ")";
					}
				}
				code += keycode;
				code += valuecode;
				WHeroesAddon.LOG.info("SQL save: 1");
			} else {
				code = "UPDATE WADDON SET ";
				for (int count = 0; count < hm.size(); count++) {
					code += "`" + aKeys[count] + "` = ?";
					if ((count < (hm.size() - 1))) {
						code += ",";
					}
				}
				code += " WHERE ";
				for (int count = 0; count < keys.size(); count++) {
					code += "`" + keys.get(count) + "` = ?";
					if ((count < (keys.size() - 1))) {
						code += " AND ";
					}
				}
				WHeroesAddon.LOG.info("SQL save: 2");
			}
			sql.query(code);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql.close();
		return;
	}
	
	public PlayerData loadPlayer(Player player) {
		if (sql instanceof SQLite ? !sql.open() : !sql.isOpen() && !sql.open());
		ResultSet rs;
		PlayerData pd;
		try {
			rs = sql.query("SELECT * FROM WADDON WHERE name='" + player.getName() + "'");
			
			pd = new PlayerData(plugin, player);
			if (Integer.valueOf(rs.getInt("player-points")) == null) {
				pd.setPoints(0);
			}

			//TODO load HashMap<String, Integer> with skills levels
			pd.countPlayerPoints();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		sql.close();
		WHeroesAddon.LOG.info("Loaded PD");
		return pd;
	}
	
	public void resetPlayerData(PlayerData player) {
		//TODO
	}
	
	public Database getDatabase() {
		return sql;
	}
}
