package database;

import clusters.MinMax;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ClustersRepository {

    private static final String SELECT_SPEECH_VECTORS = "SELECT SPEECH_ID, WORD, SCORE FROM IDF_TF";
    private static final String SELECT_DISTINCT_WORDS_WITH_MIN_MAX_VALUES = "SELECT WORD, MIN(SCORE) AS MIN, MAX(SCORE) AS MAX " +
            "FROM IDF_TF " +
            "GROUP BY WORD " +
            "ORDER BY WORD";
    private static final String WORD = "WORD";
    private static final String MIN = "MIN";
    private static final String MAX = "MAX";

    //private final List<String> vocabulary;
    public ClustersRepository() {
        super();
    }

    public Map<String, MinMax> getVocabulary() {
        Map<String, MinMax> vocabulary = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
        ResultSet resultSet = connection.prepareStatement(SELECT_DISTINCT_WORDS_WITH_MIN_MAX_VALUES).executeQuery()) {
            while (resultSet.next()) {
                vocabulary.put(resultSet.getString(WORD), new MinMax(resultSet.getDouble(MIN), resultSet.getDouble(MAX)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vocabulary;
    }

    public Map<Integer, Map<String, Double>> getSpeechVectors() {
        Map<Integer, Map<String, Double>> speechVectors = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_SPEECH_VECTORS).executeQuery()) {
            while (resultSet.next()) {

                //50_709_333
                //55576352
                //62_590_726
                Integer speechId = resultSet.getInt("SPEECH_ID");
                String word = resultSet.getString("WORD");
                Double score = resultSet.getDouble("SCORE");

                speechVectors.computeIfAbsent(speechId, k -> new HashMap<>()).put(word, score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return speechVectors;
    }
}
