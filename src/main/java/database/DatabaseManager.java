package database;

import config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static utility.Functions.println;

public class DatabaseManager {

    private static final String CREATE_SPEECH_TABLE = "CREATE TABLE IF NOT EXISTS SPEECH (ID INTEGER PRIMARY KEY, " +
            "CONTENT TEXT, " +
            "MEMBER_ID INTEGER, " +
            "SITTING_ID INTEGER, " +
            "TOTAL_WORDS INTEGER, " +
            "FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(ID), " +
            "FOREIGN KEY (SITTING_ID) REFERENCES SITTING(ID))";
    private static final String CREATE_MEMBER_TABLE = "CREATE TABLE IF NOT EXISTS MEMBER (" +
            "ID INTEGER, " +
            "NAME TEXT, " +
            "POLITICAL_PARTY_ID INTEGER," +
            "REGION TEXT," +
            "ROLE TEXT," +
            "GENDER TEXT," +
            "PRIMARY KEY (ID)," +
            "FOREIGN KEY(POLITICAL_PARTY_ID) REFERENCES POLITICAL_PARTY(ID))";

    private static final String CREATE_POLITICAL_PARTY_TABLE = "CREATE TABLE IF NOT EXISTS POLITICAL_PARTY " +
            "(ID INTEGER, NAME TEXT, PRIMARY KEY(ID))";

    private static final String CREATE_POLITICAL_PARTY_MEMBERS_TABLE = "CREATE TABLE IF NOT EXISTS POLITICAL_PARTY_MEMBERS (" +
            "POLITICAL_PARTY_ID INTEGER, " +
            "MEMBER_ID INTEGER, " +
            "START_DATE STRING," +
            "END_DATE STRING," +
            "FOREIGN KEY (POLITICAL_PARTY_ID) REFERENCES POLITICAL_PARTY(ID), " +
            "FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(ID)" +
            ")";
    private static final String CREATE_PERIOD_TABLE = "CREATE TABLE IF NOT EXISTS PERIOD (NAME TEXT, PRIMARY KEY(NAME) )";
    private static final String CREATE_SESSION_TABLE = "CREATE TABLE IF NOT EXISTS SESSION (ID INTEGER, " +
            "NAME TEXT, " +
            "PERIOD_NAME TEXT, " +
            "PRIMARY KEY(ID), " +
            "FOREIGN KEY(PERIOD_NAME) REFERENCES PERIOD(NAME))";
    private static final String CREATE_SITTING_TABLE = "CREATE TABLE IF NOT EXISTS SITTING (ID INTEGER, " +
            "NAME TEXT, " +
            "DATE TEXT, " +
            "SESSION_ID INTEGER, " +
            "PRIMARY KEY (ID), " +
            "FOREIGN KEY(SESSION_ID) REFERENCES SESSION(ID))";

    private static final String CREATE_WORD_FREQUENCY_TABLE = "CREATE TABLE IF NOT EXISTS WORD_FREQUENCY (" +
            "WORD TEXT, " +
            "FREQUENCY INTEGER, " +
            "PRIMARY KEY (WORD))";
    private static final String CREATE_TF_TABLE = "CREATE TABLE IF NOT EXISTS TF (TF REAL, WORD TEXT, SPEECH_ID INTEGER, " +
            "FOREIGN KEY(SPEECH_ID) REFERENCES SPEECH(ID))";
    private static final String CREATE_IDF_TF_TABLE = "CREATE TABLE IF NOT EXISTS IDF_TF " +
            "AS SELECT (ROUND((%d / (FREQUENCY * 1.0)), 8))* TF AS SCORE, TF.WORD, SPEECH_ID " +
            "FROM TF " +
            "JOIN WORD_FREQUENCY ON TF.WORD = WORD_FREQUENCY.WORD";
    private static final String DROP_SPEECH_TABLE = "DROP TABLE IF EXISTS SPEECH";
    private static final String DROP_MEMBER_TABLE = "DROP TABLE IF EXISTS MEMBER";
    private static final String DROP_POLITICAL_PARTY_TABLE = "DROP TABLE IF EXISTS POLITICAL_PARTY";
    private static final String DROP_PERIOD_TABLE = "DROP TABLE IF EXISTS PERIOD";
    private static final String DROP_SESSION_TABLE = "DROP TABLE IF EXISTS SESSION";
    private static final String DROP_SITTING_TABLE = "DROP TABLE IF EXISTS SITTING";
    private static final String DROP_TF_TABLE = "DROP TABLE IF EXISTS TF";
    private static final String DROP_NUMBER_OF_SPEECHES_WITH_WORD_TABLE = "DROP TABLE IF EXISTS WORD_FREQUENCY";
    private static final String DROP_POLITICAL_PARTY_MEMBERS_TABLE = "DROP TABLE IF EXISTS POLITICAL_PARTY_MEMBERS";

    //INDEXES
    private static final String CREATE_TF_WORD_INDEX = "CREATE INDEX IF NOT EXISTS TF_WORD_INDEX ON TF(WORD)";
    private static final String CREATE_IDF_TF_WORD_INDEX = "CREATE INDEX IF NOT EXISTS IDF_TF_WORD_INDEX ON IDF_TF(WORD)";
    private static final String CREATE_IDF_TF_SPEECH_ID_INDEX = "CREATE INDEX IF NOT EXISTS IDF_TF_SPEECH_ID_INDEX ON IDF_TF(SPEECH_ID)";
    private static final String DROP_IDF_TF_TABLE = "DROP TABLE IF EXISTS IDF_TF";

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Config.DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void dropTables(Statement statement) {
        try {
            statement.execute(DROP_POLITICAL_PARTY_MEMBERS_TABLE);
            statement.execute(DROP_PERIOD_TABLE);
            statement.execute(DROP_SESSION_TABLE);
            statement.execute(DROP_SITTING_TABLE);
            statement.execute(DROP_MEMBER_TABLE);
            statement.execute(DROP_POLITICAL_PARTY_TABLE);
            statement.execute(DROP_SPEECH_TABLE);
            statement.execute(DROP_TF_TABLE);
            statement.execute(DROP_NUMBER_OF_SPEECHES_WITH_WORD_TABLE);
            statement.execute(DROP_IDF_TF_TABLE);


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
            statement.execute(CREATE_POLITICAL_PARTY_TABLE);
            statement.execute(CREATE_MEMBER_TABLE);
            statement.execute(CREATE_POLITICAL_PARTY_MEMBERS_TABLE);
            statement.execute(CREATE_SPEECH_TABLE);
            statement.execute(CREATE_TF_TABLE);
            statement.execute(CREATE_WORD_FREQUENCY_TABLE);

            println("Created tables successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTFIndex() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            println("Creating TF word index");
            statement.execute(CREATE_TF_WORD_INDEX);

            println("Created table indexes successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createIdfTfTable() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA SYNCHRONOUS = OFF;");
            connection.setAutoCommit(false);

            println("Calculating IDF*TF table");
            statement.execute(String.format(CREATE_IDF_TF_TABLE, SpeechRepository.TOTAL_SPEECHES));

            println("Creating IDF_TF word index.");
            statement.execute(CREATE_IDF_TF_WORD_INDEX);

            println("Creating IDF_TF speech_id index.");
            statement.execute(CREATE_IDF_TF_SPEECH_ID_INDEX);

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}