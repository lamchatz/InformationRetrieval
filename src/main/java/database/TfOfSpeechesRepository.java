package database;

import entities.Entry;
import entities.TfOfSpeech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import static utility.Functions.println;

public class TfOfSpeechesRepository {
    private static final String INSERT_INTO_TF = "INSERT INTO TF (TF, WORD, SPEECH_ID) VALUES (ROUND(?, 4), ?, ?);";

    private static final String INSERT_INTO_WORD_FREQUENCY = "INSERT INTO WORD_FREQUENCY (WORD, FREQUENCY) " +
            "VALUES (?, 1) " +
            "ON CONFLICT(WORD) " +
            "DO UPDATE SET FREQUENCY = FREQUENCY + 1;";

    public TfOfSpeechesRepository() {
        super();
    }

    protected void executeBatch(Connection connection, Collection<TfOfSpeech> tfOfSpeechesBatch) {
        println("Flushing TfOfSpeeches...");
        try (PreparedStatement insertIntoWordFrequency = connection.prepareStatement(INSERT_INTO_WORD_FREQUENCY);
             PreparedStatement insertIntoTF = connection.prepareStatement(INSERT_INTO_TF)) {

            for (TfOfSpeech tfOfSpeech : tfOfSpeechesBatch) {
                int speechId = tfOfSpeech.getSpeechId();

                Collection<Entry> scores = tfOfSpeech.getScore();

                for (Entry entry : scores) {
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

            tfOfSpeechesBatch.clear();

            insertIntoWordFrequency.executeBatch();
            insertIntoTF.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}