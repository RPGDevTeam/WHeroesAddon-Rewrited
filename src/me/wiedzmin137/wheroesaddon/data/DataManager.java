package me.wiedzmin137.wheroesaddon.data;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
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
	public static final Table PLAYER_SKILLBAR = new Table("PLAYER_SKILLBAR", "NAME VARCHAR(16)"
			+ ",zero VARCHAR(16),one VARCHAR(16),two VARCHAR(16),three VARCHAR(16),four VARCHAR(16),five VARCHAR(16),six VARCHAR(16),seven VARCHAR(16),eight VARCHAR(16)");

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
		database.registerTable(PLAYER_SKILLBAR);
	}

	public PlayerData loadPlayer(Player player) {
		checkConnection();
		PlayerData pd = new PlayerData(plugin, player);
		
		pd.setSkillPoints(database.get(PLAYER_SKILLS, "skill", "level", "NAME", player.getName().toLowerCase()));
		pd.setPoints(pd.countPlayerPoints());
		pd.recountLock();
		
		if ((boolean) Properties.SKILLBAR_ENABLED.getValue()) {
			SkillBar skillBar = new SkillBar(plugin, player);
			HashMap<Integer, String> slots = new HashMap<>();
			for (int i = 0; i < 9; i++) {
				slots.put(i, (String) database.get(PLAYER_SKILLBAR, "NAME", Numbers.valueOf("A" + i).toNumber(), player.getName().toLowerCase()));
			}
			for (Map.Entry<Integer, String> entry : slots.entrySet()) {
				if (entry.getValue() == null || entry.getValue().equalsIgnoreCase("null"))
					entry.setValue(null);
			}
			skillBar.setData(slots);
			pd.setSkillBar(skillBar);
		}

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
				break;
			}
		}
		
		if ((boolean) Properties.SKILLBAR_ENABLED.getValue()) {
			HashMap<Integer, String> map = playerData.getSkillBar().getData();
			
			//If you know prettier way, tell me it
			String zero = map.get(0);
			String one = map.get(1);
			String two = map.get(2);
			String three = map.get(3);
			String four = map.get(4);
			String five = map.get(5);
			String six = map.get(6);
			String seven = map.get(7);
			String eight = map.get(8);
			
			for (final Map.Entry<Integer, String> entry : playerData.getSkillBar().getData().entrySet()) {
				if (database.contains(PLAYER_SKILLBAR, "NAME", playerData.getPlayer().getName().toLowerCase())) {
					database.update(PLAYER_SKILLBAR, "NAME", Numbers.valueOf("A" + entry.getKey()).toNumber(), playerData.getPlayer().getName().toLowerCase(),
							(entry.getValue() == null) ? "null" : entry.getValue());
				} else {
					database.set(PLAYER_SKILLBAR, playerData.getPlayer().getName().toLowerCase(), zero, one, two, three, four, five, six, seven, eight);
					break;
				}
			}
			playerData.getSkillBar().update();
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
	
	//I know it's stupid, maybe I will change it later
	private enum Numbers {
		A0("zero"),
		A1("one"),
		A2("two"),
		A3("three"),
		A4("four"),
		A5("five"),
		A6("six"),
		A7("seven"),
		A8("eight");
		
		private String number;
		
		private Numbers(String number) {
			this.number = number;
		}
		
		public String toNumber() { return number; }
	}
	
	public static Database getDatabase(DatabaseConfigBuilder builder) {
		return new Database(WHeroesAddon.getInstance(), builder);
	}
	
	public Database getDatabase() { return database; }
	protected void disconnect() { database.close(); }
}
