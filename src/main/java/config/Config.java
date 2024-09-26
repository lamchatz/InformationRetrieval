package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PROPERTIES = "src/config.properties";
    public static final boolean DROP_AND_RECREATE_TABLES;
    public static final int EXECUTE_BATCH_AFTER;
    public static final boolean SEARCH;
    public static final int SEARCH_TOP_K;
    public static final boolean EXTRACT_KEY_WORDS;
    public static final boolean EXTRACT_MEMBER_KEY_WORDS;
    public static final boolean EXTRACT_POLITICAL_PARTY_KEY_WORDS;
    public static final boolean EXTRACT_SPEECH_KEY_WORDS;
    public static final int NUMBER_OF_KEY_WORDS;
    public static final boolean FIND_SIMILARITIES;
    public static final boolean FIND_SIMILARITIES_IN_BATCHES;
    public static final int SIMILARITY_BATCH;
    public static final int TOP_K_SIMILARITIES;
    public static final String CSV_TO_READ;
    public static final String DB_URL;

    static {
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            DROP_AND_RECREATE_TABLES = Boolean.parseBoolean(properties.getProperty(Property.DROP_AND_RECREATE_TABLES.name()));
            EXECUTE_BATCH_AFTER = Integer.parseInt(properties.getProperty(Property.EXECUTE_BATCH_AFTER.name()));
            SEARCH = Boolean.parseBoolean(properties.getProperty(Property.SEARCH.name()));
            SEARCH_TOP_K = Integer.parseInt(properties.getProperty(Property.SEARCH_TOP_K.name()));
            EXTRACT_KEY_WORDS = Boolean.parseBoolean(properties.getProperty(Property.EXTRACT_KEY_WORDS.name()));
            EXTRACT_MEMBER_KEY_WORDS = Boolean.parseBoolean(properties.getProperty(Property.EXTRACT_MEMBER_KEY_WORDS.name()));
            EXTRACT_POLITICAL_PARTY_KEY_WORDS = Boolean.parseBoolean(properties.getProperty(Property.EXTRACT_POLITICAL_PARTY_KEY_WORDS.name()));
            EXTRACT_SPEECH_KEY_WORDS = Boolean.parseBoolean(properties.getProperty(Property.EXTRACT_SPEECH_KEY_WORDS.name()));
            NUMBER_OF_KEY_WORDS = Integer.parseInt(properties.getProperty(Property.NUMBER_OF_KEY_WORDS.name()));
            FIND_SIMILARITIES = Boolean.parseBoolean(properties.getProperty(Property.FIND_SIMILARITIES.name()));
            FIND_SIMILARITIES_IN_BATCHES = Boolean.parseBoolean(properties.getProperty(Property.FIND_SIMILARITIES_IN_BATCHES.name()));
            SIMILARITY_BATCH = Integer.parseInt(properties.getProperty(Property.SIMILARITY_BATCH.name()));
            TOP_K_SIMILARITIES = Integer.parseInt(properties.getProperty(Property.TOP_K_SIMILARITIES.name()));
            CSV_TO_READ = determineCsvToRead(properties, properties.getProperty(Property.CSV_TO_READ.name()));
            DB_URL = properties.getProperty(Property.DB_URL.name());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String determineCsvToRead(Properties properties, String property) {
        if ("big".equalsIgnoreCase(property)) {
            return properties.getProperty(Property.BIG_CSV_PATH.name());

        } else {
            return properties.getProperty(Property.NORMAL_CSV_PATH.name());
        }
    }
}
