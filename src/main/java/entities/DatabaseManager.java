package entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class DatabaseManager {
    private static final String INSERT_INTO_SPEECH = "INSERT INTO SPEECH(content) VALUES (?)";
    private static final int BATCH_SIZE = Config.EXECUTE_BATCH_AFTER;
    public static final String INSERT_INTO_INVERTED_INDEX = "INSERT INTO inverted_index (word, speech_id, counter) VALUES (?, ?, ?) ON CONFLICT(word, speech_id) DO UPDATE SET counter = counter + excluded.counter";
    public static final String DB_URL = Config.DB_URL;
    private Collection<Speech> batchSpeeches;

    public static void main(String[] args) {

        //create();
        ss();
    }

    public DatabaseManager() {
        this.batchSpeeches = new ArrayList<>();
        create();
        //selectSpeeches();
    }

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Config.DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void create() {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            if (Config.DROP_TABLES_IN_STARTUP) {
                statement.execute("DROP TABLE IF EXISTS SPEECH");
                statement.execute("DROP TABLE IF EXISTS INVERTED_INDEX");
            }
            // Create tables
            statement.execute("CREATE TABLE IF NOT EXISTS SPEECH (id INTEGER PRIMARY KEY, content TEXT)");
            statement.execute("CREATE TABLE IF NOT EXISTS Member (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
            statement.execute("CREATE TABLE IF NOT EXISTS INVERTED_INDEX (" +
                    "word TEXT NOT NULL, " +
                    "speech_id INTEGER NOT NULL, " +
                    "counter INTEGER NOT NULL, " +
                    "PRIMARY KEY (word, speech_id))");

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSpeechToBatch(Speech speech) {
        batchSpeeches.add(speech);

        if (batchSpeeches.size() == BATCH_SIZE) {
            flushSpeechesBatch();
        }
    }

    public void flushSpeechesBatch() {
        executeSpeechesBatch();
        batchSpeeches.clear();
    }

    private void executeSpeechesBatch() {
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_SPEECH)) {
            System.out.println("saving");

            connection.setAutoCommit(false);

            for (Speech speech : batchSpeeches) {
                preparedStatement.setString(1, speech.getText());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);
        } catch (SQLException e) {

        }
    }


    public void selectSpeeches() {
        try (Connection connection = connect();
             ResultSet rs = connection.createStatement().executeQuery("Select * from SPEECH")) {

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("content"));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void selectIndex() {
        try (Connection connection = connect();
             ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM INVERTED_INDEX")) {

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("word") + "\t" +
                        rs.getInt("speech_id") + "\t" +
                        rs.getInt("counter"));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void saveInvertedIndex(Map<String, Map<Long, Long>> index) {
        try (Connection connection = connect(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_INVERTED_INDEX)) {
            connection.setAutoCommit(false);

            for (Map.Entry<String, Map<Long, Long>> entry : index.entrySet()) {
                String word = entry.getKey();
                for (Map.Entry<Long, Long> subEntry : entry.getValue().entrySet()) {
                    preparedStatement.setString(1, word);
                    preparedStatement.setLong(2, subEntry.getKey());
                    preparedStatement.setLong(3, subEntry.getValue());
                    preparedStatement.addBatch();
                }
            }

            preparedStatement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            index.clear();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void ss() {
        try (Connection connection = connect(); ResultSet rs = connection.createStatement().executeQuery("SELECT word, SUM(counter) AS total_count " +
                "FROM inverted_index " +
                "GROUP BY word " +
                "HAVING total_count > 5 " +
                "ORDER BY total_count DESC")) {
            while (rs.next()) {

                String word = rs.getString("word");
                long totalCount = rs.getLong("total_count");
                System.out.println("Word: " + word + ", Count: " + totalCount);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}