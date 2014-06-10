package me.wiedzmin137.wheroesaddon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import me.wiedzmin137.wheroesaddon.WHeroesAddon;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	private String fileName;
	private File dataFolder;
	private File file;
	private YamlConfiguration yamlConf;
	
	public Config(WHeroesAddon plugin, String fileName) throws Exception {
		this.fileName = fileName;
		this.dataFolder = plugin.getDataFolder();
		this.file = new File(dataFolder, fileName);
		WHeroesAddon.LOG.info(file.toString());
		
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		
		checkFile(file);
		
		yamlConf = YamlConfiguration.loadConfiguration(getFile());
	}
	
	public static void checkFile(File out) throws Exception {
		if (!out.exists()) {
			InputStream fis = Config.class.getResourceAsStream("/resources/" + out.getName());
			FileOutputStream fos = new FileOutputStream(out);
			try {
				byte[] buf = new byte[1024];
				int i = 0;
				while ((i = fis.read(buf)) != -1) {
					fos.write(buf, 0, i);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
		}
	}
	
	public YamlConfiguration getYAML() { return yamlConf; }
	public String getName() { return fileName; }
	public File getFile() { return file; }
}
