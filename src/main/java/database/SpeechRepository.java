package database;

import entities.Speech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import static utility.Functions.println;

public class SpeechRepository {

    public static long TOTAL_SPEECHES = 0;
    protected static final String INSERT_INTO_SPEECH = "INSERT INTO SPEECH(ID, CONTENT, MEMBER_ID, SITTING_ID, TOTAL_WORDS) VALUES (?, ?, ?, ?, ?)";

    public SpeechRepository() {
        super();
    }

    protected void executeBatch(Connection connection, Collection<Speech> speechesBatch) {
        println("Flushing Speeches...");
        try (PreparedStatement insertIntoSpeech = connection.prepareStatement(INSERT_INTO_SPEECH)) {
            for (Speech speech : speechesBatch) {
                int sittingId = speech.getSittingId();

                if (sittingId != -1) {
                    TOTAL_SPEECHES++;

                    insertIntoSpeech.setInt(1, speech.getId());
                    insertIntoSpeech.setString(2, speech.getText());
                    insertIntoSpeech.setInt(3, speech.getMemberId());
                    insertIntoSpeech.setInt(4, sittingId);
                    insertIntoSpeech.setInt(5, speech.getSize());

                    insertIntoSpeech.addBatch();
                } else {
                    println("No sitting was found for this speech!");
                }
            }

            speechesBatch.clear();
            insertIntoSpeech.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
