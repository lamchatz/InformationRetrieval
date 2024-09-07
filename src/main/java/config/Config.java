package config;

import utility.Functions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PROPERTIES = "src/config.properties";
    public final static String BIG;
    public final static String NORMAL;
    public final static String TEST;
    public final static String DB_URL;
    public final static boolean DROP_AND_RECREATE_TABLES;
    public final static int EXECUTE_BATCH_AFTER;

    static {
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            DROP_AND_RECREATE_TABLES = Boolean.parseBoolean(properties.getProperty(Property.DROP_AND_RECREATE_TABLES.name()));
            EXECUTE_BATCH_AFTER = Integer.parseInt(properties.getProperty(Property.EXECUTE_BATCH_AFTER.name()));
            BIG = properties.getProperty(Property.BIG_CSV_PATH.name());
            NORMAL = properties.getProperty(Property.NORMAL_CSV_PATH.name());
            TEST = properties.getProperty(Property.TEST_CSV_PATH.name());
            DB_URL = properties.getProperty(Property.DB_URL.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
