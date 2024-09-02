package database;

import entities.InvertedIndex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class InvertedIndexRepository{

    private static final String INSERT_INTO_INVERTED_INDEX = "INSERT INTO INVERTED_INDEX (WORD, SPEECH_ID, COUNTER) VALUES (?, ?, ?) ON CONFLICT(WORD, SPEECH_ID) DO UPDATE SET COUNTER = COUNTER + excluded.COUNTER";

    private static final String LOAD_MOST_COMMON_ENTRIES = "SELECT * FROM INVERTED_INDEX ORDERY BY COUNT DESC LIMIT 20000";

    public InvertedIndexRepository() {
        super();
    }

    public void save(InvertedIndex invertedIndex) {
        Map<String, Map<Integer, Long>> index = invertedIndex.getIndex();

        try (Connection connection = DatabaseManager.connect(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_INVERTED_INDEX)) {
            connection.setAutoCommit(false);

            for (Map.Entry<String, Map<Integer, Long>> entry : index.entrySet()) {
                String word = entry.getKey();
                for (Map.Entry<Integer, Long> subEntry : entry.getValue().entrySet()) {
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
            e.printStackTrace();
        }
    }
}
