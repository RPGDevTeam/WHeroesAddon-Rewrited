package me.wiedzmin137.wheroesaddon.util.database;

import java.io.File;

import me.wiedzmin137.wheroesaddon.data.Properties;

public class DatabaseConfigBuilder {
	private String driver;
	private String url;
	private String database;
	private String user;
	private String password;
	private String file;
	
	/**
	 * Construct a database based on a config section with sqlite backup, drivers auto-generated.
	 * 
	 * @param section Configuration section.
	 * @param backup SQLIte file backup.
	 */
	public DatabaseConfigBuilder(File backup) {
		if ((Boolean) Properties.MYSQL_ENABLED.getValue()) {
			String url = String.format("%s:%d", Properties.MYSQL_HOST.getValue(), Properties.MYSQL_PORT.getValue());
			driver("com.mysql.jdbc.Driver").url(url).database((String) Properties.MYSQL_DATABASE.getValue())
				.user((String) Properties.MYSQL_USER.getValue()).password((String) Properties.MYSQL_PASSWORD.getValue());
		} else {
			driver("org.sqlite.JDBC").sqlite(backup);
		}
	}
	
	public DatabaseConfigBuilder driver(String driver) {
		this.driver = driver;
		return this;
	}
	
	public DatabaseConfigBuilder url(String url) {
		this.url = url;
		return this;
	}
	
	public DatabaseConfigBuilder database(String database) {
		this.database = database;
		return this;
	}
	
	public DatabaseConfigBuilder user(String user) {
		this.user = user;
		return this;
	}
	
	public DatabaseConfigBuilder password(String password) {
		this.password = password;
		return this;
	}
	
	public DatabaseConfigBuilder sqlite(File file) {
		this.file = file.getPath();
		return this;
	}

	public String getFile() {
		return file;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getDatabase() {
		return database;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
