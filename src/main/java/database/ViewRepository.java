package database;

import utility.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ViewRepository {

    private static final String SELECT_IDF_VALUE_OF_WORD = "SELECT ROUND(IDF, 8) AS IDF FROM IDF WHERE WORD = ?";

    private static final String SELECT_IDF_VALUE_OF_WORD_WITHOUT_ACCENT = "SELECT ROUND(IDF, 8) AS IDF, WORD FROM IDF WHERE WORD IN ";

    private static final String BASE_SELECT_TF_VALUE_OF_WORD = "SELECT ROUND((COUNTER * 1.0) / TOTAL_WORDS, 4) AS TF, INVERTED_INDEX.SPEECH_ID " +
            "FROM INVERTED_INDEX " +
            "JOIN TOTAL_SPEECH_WORDS ON (INVERTED_INDEX.SPEECH_ID = TOTAL_SPEECH_WORDS.SPEECH_ID) ";

    private static final String BASE_SELECT_TF_VALUE_OF_WORD_WITHOUT_ACCENT = "SELECT ROUND((COUNTER * 1.0) / TOTAL_WORDS, 4) AS TF, INVERTED_INDEX.SPEECH_ID, WORD " +
            "FROM INVERTED_INDEX " +
            "JOIN TOTAL_SPEECH_WORDS ON (INVERTED_INDEX.SPEECH_ID = TOTAL_SPEECH_WORDS.SPEECH_ID) ";
    private static final String SELECT_SPEECH_TOTAL_WORDS = "SELECT * FROM TOTAL_SPEECH_WORDS WHERE " +
            "SPEECH_ID IN ";
    private static final String TF = "TF";
    private static final String SPEECH_ID = "SPEECH_ID";
    private static final String TOTAL_WORDS = "TOTAL_WORDS";
    private static final String WORD = "WORD";
    private static final String IDF = "IDF";
    private static final String SINGLE_QUOTE = "'";
    private static final String WHERE_WORD_IN_ = "WHERE WORD IN ";
    private static final String SPACE = " ";
    private static final String JOIN_SPEECH_ON_TOTAL_SPEECH_WORDS = "JOIN SPEECH ON (TOTAL_SPEECH_WORDS.SPEECH_ID = SPEECH.ID) ";
    private static final String JOIN_MEMBER_ON_SPEECH = "JOIN MEMBER ON (SPEECH.MEMBER_ID = MEMBER.ID) ";
    private static final String JOIN_SITTING_ON_SPEECH = "JOIN SITTING ON (SPEECH.SITTING_ID = SITTING.ID) ";
    private static final String AND_SITTING_DATE_LESS_THAN = "AND DATE <= '";
    private static final String AND_SITTING_DATE_GREATER_THAN = "AND DATE >= '";
    private static final String SINGLE_QUOTE_WITH_SPACE = "' ";
    private static final String AND_MEMBER_NAME_LIKE = "AND MEMBER.NAME LIKE '%";
    private static final String PERCENTAGE_SINGLE_QUOTE_WITH_SPACE = "%' ";
    private static final String WHERE_WORD = "WHERE WORD = '";

    public ViewRepository() {
        super();
    }

    public double getIdfValueOfWord(String word) {
        Functions.println("Searching idf value of " + word);
        double idf = 0.0;

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IDF_VALUE_OF_WORD)) {

            preparedStatement.setString(1, word);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    idf = resultSet.getDouble(IDF);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.println("IDF value of " + word + ": " + idf);

        return Math.log(1 + idf);
    }

    public Map<String, Double> getPossibleIdfValuesOfWordWithoutAccent(String word) {
        Functions.println("Searching idf value of " + word + " without accent");
        Map<String, Double> possibleIdfValuesOfWord = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_IDF_VALUE_OF_WORD_WITHOUT_ACCENT
                             + Functions.generateInClauseFor(Functions.generateAccentVariants(word)))
                     .executeQuery()) {
            while (resultSet.next()) {
                possibleIdfValuesOfWord.put(resultSet.getString(WORD), Math.log(1 + resultSet.getDouble(IDF)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Functions.println("possible IDF values of " + word + ": " + possibleIdfValuesOfWord);

        return possibleIdfValuesOfWord;
    }

    public Map<Integer, Integer> getSpeechTotalWords(Set<Integer> speechIds) {
        Map<Integer, Integer> speechTotalWords = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_SPEECH_TOTAL_WORDS + Functions.generateInClauseFor(speechIds)).executeQuery()) {
            while (resultSet.next()) {
                speechTotalWords.put(resultSet.getInt(SPEECH_ID), resultSet.getInt(TOTAL_WORDS));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return speechTotalWords;
    }

    public Map<Integer, Double> selectTFValueOfWord(String searchWord, String... args) {
        Map<Integer, Double> tfOfSpeeches = new HashMap<>();

        StringBuilder whereClause = new StringBuilder(WHERE_WORD).append(searchWord).append(SINGLE_QUOTE_WITH_SPACE);

        String s = BASE_SELECT_TF_VALUE_OF_WORD + createTFValueOfWordQueryFor(whereClause, args);

        Functions.println(s);
        Functions.println("searching tf value for: " + searchWord);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(s).executeQuery()) {
            while (resultSet.next()) {
                double tf = resultSet.getDouble(TF);
                int speechId = resultSet.getInt(SPEECH_ID);
                tfOfSpeeches.put(speechId, tf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Functions.println("TF for " + searchWord + ": " + tfOfSpeeches);
        return tfOfSpeeches;
    }

    public Map<String, Map<Integer, Double>> selectTFValueOfWordWithoutAccent(String searchWord, String... args) {
        Map<String, Map<Integer, Double>> tfOfSpeechesForWord = new HashMap<>();

        StringBuilder whereClause = new StringBuilder(WHERE_WORD_IN_).append(Functions.generateInClauseFor(Functions.generateAccentVariants(searchWord))).append(SPACE);
        String s = BASE_SELECT_TF_VALUE_OF_WORD_WITHOUT_ACCENT + createTFValueOfWordQueryFor(whereClause, args);

        Functions.println("Searching for: " + searchWord + " without accent");

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(s).executeQuery()) {
            while (resultSet.next()) {
                double tf = resultSet.getDouble(TF);
                int speechId = resultSet.getInt(SPEECH_ID);
                String word = resultSet.getString(WORD);

                if (!tfOfSpeechesForWord.containsKey(word)) {
                    tfOfSpeechesForWord.put(word, new HashMap<>());
                }

                Map<Integer, Double> tfOfSpeeches = tfOfSpeechesForWord.get(word);
                tfOfSpeeches.put(speechId, tf);

                tfOfSpeechesForWord.put(word, tfOfSpeeches);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Functions.println(s);

        Functions.println("TF for " + searchWord + " without accent: " + tfOfSpeechesForWord);

        return tfOfSpeechesForWord;
    }

    public String createTFValueOfWordQueryFor(StringBuilder whereClause, String... args) {
        StringBuilder joins = new StringBuilder();
        boolean joinedSpeech = false;
        boolean joinedSitting = false;

        if (args.length > 1) {
            String memberName = args[1];
            if (Functions.isNotEmpty(memberName)) {
                joins.append(JOIN_SPEECH_ON_TOTAL_SPEECH_WORDS);
                joins.append(JOIN_MEMBER_ON_SPEECH);

                joinedSpeech = true;
                whereClause.append(AND_MEMBER_NAME_LIKE).append(memberName).append(PERCENTAGE_SINGLE_QUOTE_WITH_SPACE);
            }
        }

        if (args.length > 2) {
            String from = args[2];
            if (Functions.isNotEmpty(from)) {
                if (!joinedSpeech) {
                    joins.append(JOIN_SPEECH_ON_TOTAL_SPEECH_WORDS);
                    joinedSpeech = true;
                }
                joins.append(JOIN_SITTING_ON_SPEECH);
                joinedSitting = true;

                whereClause.append(AND_SITTING_DATE_GREATER_THAN).append(from).append(SINGLE_QUOTE_WITH_SPACE);
            }
        }

        if (args.length > 3) {
            String to = args[3];
            if (Functions.isNotEmpty(to)) {
                if (!joinedSpeech) {
                    joins.append(JOIN_SPEECH_ON_TOTAL_SPEECH_WORDS);
                }
                if (!joinedSitting) {
                    joins.append(JOIN_SITTING_ON_SPEECH);
                }

                whereClause.append(AND_SITTING_DATE_LESS_THAN).append(to).append(SINGLE_QUOTE);
            }
        }

        return joins.append(whereClause).toString();
    }
}

