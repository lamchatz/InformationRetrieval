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

    private static final String DROP_SPEECH_TABLE = "DROP TABLE IF EXISTS SPEECH";
    private static final String DROP_INVERTED_INDEX_TABLE = "DROP TABLE IF EXISTS INVERTED_INDEX";
    private static final String DROP_MEMBER_TABLE = "DROP TABLE IF EXISTS MEMBER";
    private static final String DROP_PERIOD_TABLE = "DROP TABLE IF EXISTS PERIOD";
    private static final String DROP_SESSION_TABLE = "DROP TABLE IF EXISTS SESSION";
    private static final String DROP_SITTING_TABLE = "DROP TABLE IF EXISTS SITTING";


    public static void main(String[] args) {
        new DatabaseManager();
    }

    public DatabaseManager() {
        init();
    }

    public static void init() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            if (Config.DROP_AND_RECREATE_TABLES) {
                dropTables(statement);
                createTables(statement);
            }
            // createTables(statement);
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

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}