package entities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public static final String CONFIG = "src/config.properties";
    public static final String DROP_TABLES_IN_STARTUP = "DROP_TABLES_IN_STARTUP";
    public static final String SAVE_INDEX_TO_FILE = "SAVE_INDEX_TO_FILE";
    public static final String SAVE_AFTER = "SAVE_AFTER";
    private boolean dropTablesInStartup;
    private boolean saveIndexToFile;
    private int saveAfter;

    public Config() {
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            this.dropTablesInStartup = Boolean.parseBoolean(properties.getProperty(DROP_TABLES_IN_STARTUP));
            this.saveIndexToFile = Boolean.parseBoolean(properties.getProperty(SAVE_INDEX_TO_FILE));
            this.saveAfter = Integer.parseInt(properties.getProperty(SAVE_AFTER));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public boolean shouldDropTablesInStartup() {
        return dropTablesInStartup;
    }

    public boolean shouldSaveIndexToFile() {
        return saveIndexToFile;
    }

    public int getSaveAfter() {
        return saveAfter;
    }
}
