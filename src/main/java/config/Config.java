package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PROPERTIES = "src/config.properties";
    public static final String BIG;
    public static final String NORMAL;
    public static final String TEST;
    public static final String DB_URL;
    public static final boolean DROP_AND_RECREATE_TABLES;
    public static final boolean EXTRACT_KEY_WORDS;
    public static final int NUMBER_OF_KEY_WORDS;
    public static final int EXECUTE_BATCH_AFTER;

    static {
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            DROP_AND_RECREATE_TABLES = Boolean.parseBoolean(properties.getProperty(Property.DROP_AND_RECREATE_TABLES.name()));
            EXECUTE_BATCH_AFTER = Integer.parseInt(properties.getProperty(Property.EXECUTE_BATCH_AFTER.name()));
            EXTRACT_KEY_WORDS = Boolean.parseBoolean(properties.getProperty(Property.EXTRACT_KEY_WORDS.name()));
            NUMBER_OF_KEY_WORDS = Integer.parseInt(properties.getProperty(Property.NUMBER_OF_KEY_WORDS.name()));
            BIG = properties.getProperty(Property.BIG_CSV_PATH.name());
            NORMAL = properties.getProperty(Property.NORMAL_CSV_PATH.name());
            TEST = properties.getProperty(Property.TEST_CSV_PATH.name());
            DB_URL = properties.getProperty(Property.DB_URL.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
