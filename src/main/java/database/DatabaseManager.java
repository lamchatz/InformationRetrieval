package database;

import config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String CREATE_SPEECH_TABLE = "CREATE TABLE IF NOT EXISTS SPEECH (ID INTEGER PRIMARY KEY, " +
            "CONTENT TEXT, " +
            "MEMBER_ID INTEGER, " +
            "SITTING_ID INTEGER, " +
            "FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(ID), " +
            "FOREIGN KEY (SITTING_ID) REFERENCES SITTING(ID))";
    private static final String CREATE_INVERTED_INDEX_TABLE = "CREATE TABLE IF NOT EXISTS INVERTED_INDEX (" +
            "WORD TEXT NOT NULL, " +
            "SPEECH_ID INTEGER NOT NULL, " +
            "COUNTER INTEGER NOT NULL, " +
            "PRIMARY KEY (WORD, SPEECH_ID))";
    private static final String CREATE_WORD_INDEX = "CREATE INDEX IF NOT EXISTS INDEX_WORD ON INVERTED_INDEX (WORD)";
    private static final String CREATE_SPEECH_ID_INDEX = "CREATE INDEX IF NOT EXISTS INDEX_SPEECH_ID ON INVERTED_INDEX(SPEECH_iD)";
    private static final String CREATE_MEMBER_TABLE = "CREATE TABLE IF NOT EXISTS Member (" +
            "ID INTEGER, " +
            "NAME TEXT, " +
            "POLITICAL_PARTY TEXT," +
            "REGION TEXT," +
            "ROLE TEXT," +
            "GENDER INTEGER," +
            "PRIMARY KEY (ID))";
    private static final String CREATE_PERIOD_TABLE = "CREATE TABLE IF NOT EXISTS PERIOD (NAME TEXT, PRIMARY KEY(NAME) )";
    private static final String CREATE_SESSION_TABLE = "CREATE TABLE IF NOT EXISTS SESSION (ID INTEGER, " +
            "NAME TEXT, " +
            "PERIOD_NAME TEXT, " +
            "PRIMARY KEY(ID), " +
            "FOREIGN KEY(PERIOD_NAME) REFERENCES PERIOD(NAME))";
    private static final String CREATE_SITTING_TABLE = "CREATE TABLE IF NOT EXISTS SITTING (ID INTEGER, " +
            "NAME TEXT, " +
            "SITTING_DATE TEXT, " +
            "SESSION_ID INTEGER, " +
            "PRIMARY KEY (ID), " +
            "FOREIGN KEY(SESSION_ID) REFERENCES SESSION(ID))";
    //views
    private static final String CREATE_NUMBER_OF_SPEECHES_WITH_WORD_TABLE = "CREATE TABLE IF NOT EXISTS NUMBER_OF_SPEECHES_WITH_WORD AS " +
            "SELECT COUNT(WORD) AS WORD_FREQUENCY, WORD " +
            "FROM INVERTED_INDEX " +
            "GROUP BY WORD";

    private static final String CREATE_NUMBER_OF_SPEECHES_WITH_WORD_WORD_INDEX = "CREATE INDEX IF NOT EXISTS NUMBER_OF_SPEECHES_WITH_WORD_WORD_INDEX " +
            "ON NUMBER_OF_SPEECHES_WITH_WORD (WORD)";
    private static final String CREATE_IDF_VIEW = "CREATE TABLE IF NOT EXISTS IDF AS " +
            "SELECT (TOTAL_SPEECHES / (WORD_FREQUENCY * 1.0)) AS IDF, WORD " +
            "FROM NUMBER_OF_SPEECHES_WITH_WORD, (SELECT COUNT(ID) AS TOTAL_SPEECHES FROM SPEECH)";

    private static final String CREATE_IDF_WORD_INDEX = "CREATE INDEX IF NOT EXISTS IDF_WORD_INDEX " +
            "ON IDF (WORD)";
    private static final String CREATE_TOTAL_SPEECH_WORDS = "CREATE TABLE IF NOT EXISTS TOTAL_SPEECH_WORDS AS " +
            "SELECT SUM(COUNTER) AS TOTAL_WORDS, SPEECH_ID " +
            "FROM INVERTED_INDEX " +
            "GROUP BY SPEECH_ID";

    private static final String CREATE_TOTAL_SPEECH_WORDS_VIEW_SPEECH_ID_INDEX = "CREATE INDEX IF NOT EXISTS TOTAL_SPEECH_WORDS_SPEECH_VIEW_SPEECH_ID " +
            "ON TOTAL_SPEECH_WORDS (SPEECH_ID)";


    private static final String DROP_SPEECH_TABLE = "DROP TABLE IF EXISTS SPEECH";
    private static final String DROP_INVERTED_INDEX_TABLE = "DROP TABLE IF EXISTS INVERTED_INDEX";
    private static final String DROP_MEMBER_TABLE = "DROP TABLE IF EXISTS MEMBER";
    private static final String DROP_PERIOD_TABLE = "DROP TABLE IF EXISTS PERIOD";
    private static final String DROP_SESSION_TABLE = "DROP TABLE IF EXISTS SESSION";
    private static final String DROP_SITTING_TABLE = "DROP TABLE IF EXISTS SITTING";
    private static final String DROP_NUMBER_OF_SPEECHES_WITH_WORD_VIEW = "DROP TABLE IF EXISTS NUMBER_OF_SPEECHES_WITH_WORD";
    private static final String DROP_IDF_VIEW = "DROP TABLE IF EXISTS IDF";
    private static final String DROP_TOTAL_SPEECH_WORDS_VIEW = "DROP TABLE IF EXISTS TOTAL_SPEECH_WORDS";


    public static void main(String[] args) {
        new DatabaseManager();
    }

    public DatabaseManager() {
        init();
    }

    public static void init() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            dropTables(statement);
            createTables(statement);
            //createTableIndexes(statement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Config.DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void dropTables(Statement statement) {
        try {
            statement.execute(DROP_PERIOD_TABLE);
            statement.execute(DROP_SESSION_TABLE);
            statement.execute(DROP_SITTING_TABLE);
            statement.execute(DROP_MEMBER_TABLE);
            statement.execute(DROP_INVERTED_INDEX_TABLE);
            statement.execute(DROP_SPEECH_TABLE);
            statement.execute(DROP_NUMBER_OF_SPEECHES_WITH_WORD_VIEW);
            statement.execute(DROP_IDF_VIEW);
            statement.execute(DROP_TOTAL_SPEECH_WORDS_VIEW);

            println("Dropped tables successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTables(Statement statement) {
        try {
            // Create tables
            statement.execute(CREATE_PERIOD_TABLE);
            statement.execute(CREATE_SESSION_TABLE);
            statement.execute(CREATE_SITTING_TABLE);
            statement.execute(CREATE_MEMBER_TABLE);
            statement.execute(CREATE_SPEECH_TABLE);
            statement.execute(CREATE_INVERTED_INDEX_TABLE);

            println("Created tables successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableIndexes() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_WORD_INDEX);
            statement.execute(CREATE_SPEECH_ID_INDEX);

            println("Created table indexes successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createIDF_TF_RelatedTables() {
        //
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TOTAL_SPEECH_WORDS);
            statement.execute(CREATE_NUMBER_OF_SPEECHES_WITH_WORD_TABLE);
            statement.execute(CREATE_IDF_VIEW);

            println("Created IDF-TF related tables successfully.");

            createIDF_TF_RelatedIndexes(statement);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createIDF_TF_RelatedIndexes(Statement statement) {
        try {
            statement.execute(CREATE_TOTAL_SPEECH_WORDS_VIEW_SPEECH_ID_INDEX);
            statement.execute(CREATE_NUMBER_OF_SPEECHES_WITH_WORD_WORD_INDEX);
            statement.execute(CREATE_IDF_WORD_INDEX);

            println("Created IDF-TF Related indexes successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void println(String text) {
        System.out.println(text);
    }
}