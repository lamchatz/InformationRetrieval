package database;

import config.Config;
import entities.Speech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SpeechRepository implements AbstractBatchRepository<Speech> {

    protected static final String INSERT_INTO_SPEECH = "INSERT INTO SPEECH(CONTENT, MEMBER_ID, SITTING_ID) VALUES (?, ?, ?)";
    private static final int MINUS_ONE = -1;

    private Collection<Speech> batchSpeeches;

    public SpeechRepository() {
        this.batchSpeeches = new ArrayList<>();
    }

    @Override
    public void addToBatch(Speech speech) {
        batchSpeeches.add(speech);

        if (batchSpeeches.size() == Config.EXECUTE_BATCH_AFTER) {
            flushBatch();
        }
    }

    @Override
    public void flushBatch() {
        executeBatch();
        batchSpeeches.clear();
    }

    @Override
    public void executeBatch() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_SPEECH)) {

            connection.setAutoCommit(false);

            for (Speech speech : batchSpeeches) {
                int sittingId = speech.getSittingId();

                if (sittingId != MINUS_ONE) {
                    preparedStatement.setString(1, speech.getText());
                    preparedStatement.setInt(2, speech.getMemberId());
                    preparedStatement.setInt(3, sittingId);

                    preparedStatement.addBatch();
                } else {
                    System.out.println("hmm");
                }
            }

            preparedStatement.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Speech getByName(String name) {
        return null;
    }

    @Override
    public void selectAll() {
        try (Connection connection = DatabaseManager.connect();
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


    @Override
    public void save(Speech element) {

    }
}
