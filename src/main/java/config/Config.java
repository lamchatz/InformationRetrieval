package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PROPERTIES = "src/config.properties";
    public static String BIG;
    public static String NORMAL;
    public static String TEST;
    public static String DB_URL;
    public static boolean DROP_AND_RECREATE_TABLES;
    public static int EXECUTE_BATCH_AFTER;

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
            System.out.println(e);
        }
    }
}
