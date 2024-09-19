package database;

import utility.Functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClustersRepository {

    private static final String SELECT_SPEECH_VECTORS = "SELECT SPEECH_ID, WORD, SCORE FROM IDF_TF";
    private static final String SELECT_DISTINCT_WORDS = "SELECT DISTINCT WORD FROM IDF_TF ORDER BY WORD";
    private static final String WORD = "WORD";

    //private final List<String> vocabulary;
    public ClustersRepository() {
      //  this.vocabulary = getVocabulary();
    }

    private List<String> getVocabulary() {
        List<String> vocabulary = new ArrayList<>();

        try (Connection connection = DatabaseManager.connect();
        ResultSet resultSet = connection.prepareStatement(SELECT_DISTINCT_WORDS).executeQuery()) {
            while (resultSet.next()) {
                vocabulary.add(resultSet.getString(WORD));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vocabulary;
    }

    public Map<Integer, Map<String, Double>> getSpeechVectors() {
        Map<Integer, Map<String, Double>> speechVectors = new HashMap<>();

        long c = 0;
        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_SPEECH_VECTORS).executeQuery()) {
            while (resultSet.next()) {
                c++;
                //50_709_333
                //55576352
                //62_590_726
//                Integer speechId = resultSet.getInt("SPEECH_ID");
//                String word = resultSet.getString("WORD");
//                double score = resultSet.getDouble("SCORE");
//
//                speechVectors.computeIfAbsent(speechId, k -> new HashMap<>()).put(word, score);
            }

            Functions.println(c);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return speechVectors;
    }

    public void s() {
        try (Connection connection = DatabaseManager.connect();
        ResultSet resultSet = connection.prepareStatement("SELECT WORD, IDF FROM (SELECT (TOTAL_SPEECHES / (FREQUENCY * 1.0)) AS IDF, WORD " +
                "FROM WORD_FREQUENCY, (SELECT COUNT(ID) AS TOTAL_SPEECHES FROM SPEECH )) ORDER BY IDF ASC LIMIT 2000" ).executeQuery()) {
            while (resultSet.next()) {
                Functions.println(resultSet.getString(WORD) + ": " + resultSet.getDouble("IDF") );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
