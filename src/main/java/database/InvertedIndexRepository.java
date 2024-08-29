package database;

import entities.InvertedIndex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class InvertedIndexRepository implements AbstractRepository<InvertedIndex> {
    private static final String INSERT_INTO_INVERTED_INDEX = "INSERT INTO INVERTED_INDEX (WORD, SPEECH_ID, COUNTER) VALUES (?, ?, ?) ON CONFLICT(WORD, SPEECH_ID) DO UPDATE SET COUNTER = COUNTER + excluded.COUNTER";
    private static final String SELECT_SPEECH_BY_WORD = "SELECT SPEECH.ID AS SPEECH_ID, SPEECH.CONTENT AS SPEECH_CONTENT, INVERTED_INDEX.COUNTER AS COUNTER " +
            "FROM INVERTED_INDEX " +
            "JOIN SPEECH ON (INVERTED_INDEX.SPEECH_ID = SPEECH.ID) " +
            "WHERE WORD LIKE ? " +
            "ORDER BY INVERTED_INDEX.COUNTER DESC " +
            "LIMIT 10";

    private static final String LOAD_MOST_COMMON_ENTRIES = "SELECT * FROM INVERTED_INDEX ORDERY BY COUNT DESC LIMIT 20000";

    public InvertedIndexRepository() {
        super();
    }

    public void selectIndex() {
        try (Connection connection = DatabaseManager.connect();
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

    @Override
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
            System.out.println(e.getMessage());
        }
    }

    @Override
    public InvertedIndex getByName(String name) {
        return null;
    }

//    public void ss(String word) {
//        try (Connection connection = DatabaseManager.connect();
//             PreparedStatement selectSpeechesByWord = connection.prepareStatement(SELECT_SPEECH_BY_WORD)) {
//
//            selectSpeechesByWord.setString(1, word);
//
//            try (ResultSet resultSet = selectSpeechesByWord.executeQuery()) {
//                while (resultSet.next()) {
//                    Speech speech = new Speech(resultSet.getInt("SPEECH_ID"));
//                    speech.setText(resultSet.getString("SPEECH_CONTENT"));
//
//                    System.out.println("Counter of " + word + " for speech" + speech.getId() + ": " + resultSet.getInt("COUNTER"));
//                }
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }


    @Override
    public void selectAll() {

    }
}
