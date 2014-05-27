package me.wiedzmin137.wheroesaddon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Config {
	private String fileName;
	private File dataFolder;
	private File file;
	
	public Config(WHeroesAddon plugin, String fileName) throws Exception {
		this.fileName = fileName;
		this.dataFolder = WHeroesAddon.getInstance().getDataFolder();
		this.file = new File(dataFolder, fileName);
		
		checkFile(file);
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
    
    public String getName() { return fileName; }
    public File getFile() { return file; }
}
