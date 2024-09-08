package database;

import config.Config;
import dto.InfoToShow;
import dto.Member;
import dto.Period;
import entities.Speech;
import utility.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class SpeechRepository {

    public static long TOTAL_SPEECHES = 0;
    private static final String MEMBER_NAME = "MEMBER_NAME";
    private static final String POLITICAL_PARTY = "POLITICAL_PARTY";
    private static final String REGION = "REGION";
    private static final String ROLE = "ROLE";
    private static final String SITTING_NAME = "SITTING_NAME";
    private static final String SESSION_NAME = "SESSION_NAME";
    private static final String PERIOD_NAME = "PERIOD_NAME";
    private static final String SELECT_ALL_INFO_TO_SHOW = "SELECT CONTENT, " +
            "MEMBER.NAME AS MEMBER_NAME, POLITICAL_PARTY, REGION, " +
            "ROLE, GENDER, SITTING.NAME AS SITTING_NAME, DATE, " +
            "SESSION.NAME AS SESSION_NAME, PERIOD_NAME " +
            "FROM SPEECH " +
            "JOIN MEMBER ON (SPEECH.MEMBER_ID = MEMBER.ID) " +
            "JOIN SITTING ON (SPEECH.SITTING_ID = SITTING.ID) " +
            "JOIN SESSION ON (SITTING.SESSION_ID = SESSION.ID) " +
            "JOIN PERIOD ON (SESSION.PERIOD_NAME = PERIOD.NAME) " +
            "WHERE SPEECH.ID IN ";
    protected static final String INSERT_INTO_SPEECH = "INSERT INTO SPEECH(ID, CONTENT, MEMBER_ID, SITTING_ID, TOTAL_WORDS) VALUES (?, ?, ?, ?, ?)";
    private static final String CONTENT = "CONTENT";
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
        Functions.println("Saving speeches...");
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
                    Functions.println("No sitting was found for this speech!");
                }
            }

            insertIntoSpeech.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Collection<InfoToShow> getAllInfoFor(Set<Integer> speechIds) {
        Collection<InfoToShow> infoToShowCollection = new ArrayList<>(speechIds.size());

        if (!speechIds.isEmpty()) {
            try (Connection connection = DatabaseManager.connect();
                 ResultSet resultSet = connection.prepareStatement(SELECT_ALL_INFO_TO_SHOW + Functions.generateInClauseFor(speechIds)).executeQuery()) {

                while (resultSet.next()) {
                    Member member = new Member(resultSet.getString(MEMBER_NAME),
                            resultSet.getString(POLITICAL_PARTY),
                            resultSet.getString(REGION),
                            resultSet.getString(ROLE)
                    );
                    Period period = new Period(resultSet.getString(PERIOD_NAME),
                            resultSet.getString(SESSION_NAME),
                            resultSet.getString(SITTING_NAME)
                    );

                    infoToShowCollection.add(new InfoToShow(resultSet.getString(CONTENT), member, period));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return infoToShowCollection;
    }
}
