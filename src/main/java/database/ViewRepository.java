package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ViewRepository {
    private static final Map<Character, Character> VOWEL_AND_ITS_ACCENT = Map.of(
            'α', 'ά',
            'ε', 'έ',
            'η', 'ή',
            'ι', 'ί',
            'ο', 'ό',
            'υ', 'ύ',
            'ω', 'ώ');

    private static final Set<Character> VOWELS = Set.of('α', 'ε', 'η', 'ι', 'ο', 'υ', 'ω');

    private static final String SELECT_IDF_VALUE_OF_WORD = "SELECT ROUND(IDF, 8) AS IDF FROM IDF WHERE WORD = ?";

    private static final String SELECT_IDF_VALUE_OF_WORD_IGNORING_ACCENT = "SELECT ROUND(IDF, 8) AS IDF, WORD FROM IDF WHERE WORD IN ";

    private static final String SELECT_TF_VALUE_OF_WORD = "SELECT ROUND((COUNTER * 1.0) / TOTAL_WORDS, 4) AS TF, INVERTED_INDEX.SPEECH_ID " +
            "FROM INVERTED_INDEX " +
            "JOIN TOTAL_SPEECH_WORDS ON (INVERTED_INDEX.SPEECH_ID = TOTAL_SPEECH_WORDS.SPEECH_ID)" +
            "WHERE WORD = ?";

    private static final String SELECT_TF_VALUE_OF_WORD_WITH_MEMBER = "SELECT ROUND((COUNTER * 1.0) / TOTAL_WORDS, 4) AS TF, INVERTED_INDEX.SPEECH_ID " +
            "FROM INVERTED_INDEX " +
            "JOIN TOTAL_SPEECH_WORDS ON (INVERTED_INDEX.SPEECH_ID = TOTAL_SPEECH_WORDS.SPEECH_ID) " +
            "JOIN SPEECH ON (TOTAL_SPEECH_WORDS.SPEECH_ID = SPEECH.ID) " +
            "JOIN MEMBER ON (SPEECH.MEMBER_ID = MEMBER.ID) " +
            "WHERE WORD = ? AND NAME LIKE ?";


    private static final String BASE_SELECT_TF_VALUE_OF_WORD = "SELECT ROUND((COUNTER * 1.0) / TOTAL_WORDS, 4) AS TF, INVERTED_INDEX.SPEECH_ID " +
            "FROM INVERTED_INDEX " +
            "JOIN TOTAL_SPEECH_WORDS ON (INVERTED_INDEX.SPEECH_ID = TOTAL_SPEECH_WORDS.SPEECH_ID) ";

    private static final String SELECT_TF_VALUE_OF_WORD_IGNORING_ACCENT = "SELECT ROUND((COUNTER * 1.0) / TOTAL_WORDS, 4) AS TF, INVERTED_INDEX.SPEECH_ID, WORD " +
            "FROM INVERTED_INDEX " +
            "JOIN TOTAL_SPEECH_WORDS ON (INVERTED_INDEX.SPEECH_ID = TOTAL_SPEECH_WORDS.SPEECH_ID)" +
            "WHERE WORD IN ";


    private static final String SELECT_SPEECH_TOTAL_WORDS = "SELECT * FROM TOTAL_SPEECH_WORDS WHERE " +
            "SPEECH_ID IN ";
    private static final String ACCENT_REGEX = "\\p{M}";
    private static final String EMPTY = "";
    private static final String TF = "TF";
    private static final String SPEECH_ID = "SPEECH_ID";
    private static final String TOTAL_WORDS = "TOTAL_WORDS";
    private static final String COMMA = ", ";
    private static final String LEFT_PARENTHESIS = "(";
    private static final String RIGHT_PARENTHESIS = ")";
    private static final String REMOVE_ACCENT = "REMOVE_ACCENT";
    private static final String WORD = "WORD";
    private static final String IDF = "IDF";
    private static final String SINGLE_QUOTE = "'";

    public ViewRepository() {
        super();
    }

    public double getIdfValueOfWord(String word) {
        System.out.println("Searching idf value of " + word);
        double idf = 0.0;

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IDF_VALUE_OF_WORD)) {

            preparedStatement.setString(1, word);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    idf = resultSet.getDouble("IDF");
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("IDF value of " + word + ": " + idf);

        return Math.log(1 + idf);
    }

    public Map<String, Double> getPossibleIdfValuesOfWordIgnoringAccent(String word) {
        System.out.println("Searching idf value of " + word + " ignoring accent");
        Map<String, Double> possibleIdfValuesOfWord = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_IDF_VALUE_OF_WORD_IGNORING_ACCENT
                             + generateInClauseFor(generateAccentVariants(word)))
                     .executeQuery()) {
            while (resultSet.next()) {
                possibleIdfValuesOfWord.put(resultSet.getString(WORD), Math.log(1 + resultSet.getDouble(IDF)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("possible IDF values of " + word + ": " + possibleIdfValuesOfWord);

        return possibleIdfValuesOfWord;
    }

    public Map<Integer, Double> getTfValueOfWordForMember(String word, String memberName) {
        System.out.println("TF FOR MEMBER");
        Map<Integer, Double> tfOfSpeeches = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TF_VALUE_OF_WORD_WITH_MEMBER)) {
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, '%' + memberName + '%');

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    double tf = resultSet.getDouble(TF);
                    int speechId = resultSet.getInt(SPEECH_ID);
                    tfOfSpeeches.put(speechId, tf);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(tfOfSpeeches);
        return tfOfSpeeches;
    }

    public Map<Integer, Double> getTFValueOfWord(String word) {
        System.out.println("searching tf value for: " + word);
        Map<Integer, Double> tfOfSpeeches = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TF_VALUE_OF_WORD)) {
            preparedStatement.setString(1, word);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    double tf = resultSet.getDouble(TF);
                    int speechId = resultSet.getInt(SPEECH_ID);
                    tfOfSpeeches.put(speechId, tf);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        System.out.println("TF for " + word + ": " + tfOfSpeeches);
        return tfOfSpeeches;
    }

    public Map<String, Map<Integer, Double>> getTFValueOfWordWithoutAccent(String searchWord) {
        Map<String, Map<Integer, Double>> tfOfSpeechesForWord = new HashMap<>();

        System.out.println("Searching for: " + searchWord + " ignoring accent");

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_TF_VALUE_OF_WORD_IGNORING_ACCENT
                             + generateInClauseFor(generateAccentVariants(searchWord)))
                     .executeQuery()) {
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

        System.out.println("TF for " + searchWord + " ignoring accent: " + tfOfSpeechesForWord);

        return tfOfSpeechesForWord;
    }

    public Map<Integer, Integer> getSpeechTotalWords(Set<Integer> speechIds) {
        Map<Integer, Integer> speechTotalWords = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_SPEECH_TOTAL_WORDS + generateInClauseFor(speechIds)).executeQuery()) {
            while (resultSet.next()) {
                speechTotalWords.put(resultSet.getInt(SPEECH_ID), resultSet.getInt(TOTAL_WORDS));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return speechTotalWords;
    }

    public void ss(String question, String memberName, String from, String to) {
        StringBuilder whereClause = new StringBuilder("WHERE WORD = '").append(question).append("' ");
        StringBuilder joins = new StringBuilder();
        boolean joinedSpeeches = false;


        if (isNotEmpty(memberName)) {
            joins.append("JOIN SPEECH ON (TOTAL_SPEECH_WORDS.SPEECH_ID = SPEECH.ID) ");
            joins.append("JOIN MEMBER ON (SPEECH.MEMBER_ID = MEMBER.ID) ");

            joinedSpeeches = true;
            whereClause.append("AND NAME LIKE '%").append(memberName).append("%' ");
        }

        if (isNotEmpty(from)) {
            if (!joinedSpeeches) {
                joins.append("JOIN SPEECH ON (TOTAL_SPEECH_WORDS.SPEECH_ID = SPEECH.ID) ");
            }
            whereClause.append("AND DATE = ").append(from);
        }

        if (isNotEmpty(to)) {
            if (!joinedSpeeches) {
                joins.append("JOIN SPEECH ON (TOTAL_SPEECH_WORDS.SPEECH_ID = SPEECH.ID) ");
            }

            whereClause.append("AND DATE = ").append(to);
        }

        String s = BASE_SELECT_TF_VALUE_OF_WORD + joins + whereClause;

        int i = 0;
    }

    private boolean isNotEmpty(String string) {
        return !(string == null || string.isBlank());
    }

    private Set<String> generateAccentVariants(String input) {
        Set<String> variants = new HashSet<>();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            // If the current character is a vowel, replace it with its accented version
            if (VOWELS.contains(currentChar)) {
                StringBuilder variant = new StringBuilder(input);
                variant.setCharAt(i, VOWEL_AND_ITS_ACCENT.get(currentChar));
                variants.add(SINGLE_QUOTE + variant + SINGLE_QUOTE);
            }
        }

        return variants;
    }

    private <T> String generateInClauseFor(Set<T> parameters) {
        return parameters.stream().map(String::valueOf).collect(Collectors.joining(COMMA, LEFT_PARENTHESIS, RIGHT_PARENTHESIS));
    }
}

