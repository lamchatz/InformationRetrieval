package database;

import config.Config;
import entities.Speech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static utility.Functions.println;

public class SpeechRepository {

    public static long TOTAL_SPEECHES = 0;
    protected static final String INSERT_INTO_SPEECH = "INSERT INTO SPEECH(ID, CONTENT, MEMBER_ID, SITTING_ID, TOTAL_WORDS) VALUES (?, ?, ?, ?, ?)";
    private final Collection<Speech> batchSpeeches;

    public SpeechRepository() {
        this.batchSpeeches = new ArrayList<>(Config.EXECUTE_BATCH_AFTER);
    }

    public void clear() {
        batchSpeeches.clear();
    }

    public void addToBatch(Speech speech) {
        batchSpeeches.add(speech);

        if (batchSpeeches.size() == Config.EXECUTE_BATCH_AFTER) {
            flushBatch();
        }
    }

    public void flushBatch() {
        executeBatch();
        batchSpeeches.clear();
    }

    public void executeBatch() {
        println("Saving speeches...");
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement insertIntoSpeech = connection.prepareStatement(INSERT_INTO_SPEECH)) {
            connection.createStatement().execute("PRAGMA SYNCHRONOUS = OFF;");
            connection.setAutoCommit(false);
            for (Speech speech : batchSpeeches) {
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

            insertIntoSpeech.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
