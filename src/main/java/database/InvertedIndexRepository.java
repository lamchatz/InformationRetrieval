package database;

import entities.Entry;
import entities.InvertedIndex;
import entities.TF;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class InvertedIndexRepository {

    private static final String INSERT_INTO_TF = "INSERT INTO TF (TF, WORD, SPEECH_ID) VALUES (ROUND(?, 4), ?, ?);";

    private static final String INSERT_INTO_WORD_FREQUENCY = "INSERT INTO WORD_FREQUENCY (WORD, FREQUENCY) " +
            "VALUES (?, 1) " +
            "ON CONFLICT(WORD) " +
            "DO UPDATE SET FREQUENCY = FREQUENCY + 1;";

    public InvertedIndexRepository() {
        super();
    }

    public void save(InvertedIndex invertedIndex) {
        try (Connection connection = DatabaseManager.connect();
                PreparedStatement insertIntoWordFrequency = connection.prepareStatement(INSERT_INTO_WORD_FREQUENCY);
             PreparedStatement insertIntoTF = connection.prepareStatement(INSERT_INTO_TF)) {

            connection.setAutoCommit(false);

            Collection<TF> tfScores = invertedIndex.getTfScores();
            for (TF tf : tfScores) {
                int speechId = tf.getSpeechId();

                Collection<Entry<String, Double>> scores = tf.getScore();

                for (Entry<String, Double> entry : scores) {
                    String word = entry.getKey();

                    insertIntoWordFrequency.setString(1, word);
                    insertIntoWordFrequency.addBatch();

                    insertIntoTF.setDouble(1, entry.getValue());
                    insertIntoTF.setString(2, word);
                    insertIntoTF.setInt(3, speechId);

                    insertIntoTF.addBatch();
                }
                scores.clear();
            }

            insertIntoWordFrequency.executeBatch();
            insertIntoTF.executeBatch();

            connection.commit();

            tfScores.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
