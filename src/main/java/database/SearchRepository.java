package database;

import utility.Functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SearchRepository {

    private static final String SELECT_SPEECH_TOTAL_WORDS = "SELECT ID, TOTAL_WORDS FROM SPEECH WHERE ID IN ";
    private static final String BASE_SELECT_IDF_TF_VALUES_OF_WORD = "SELECT SCORE, SPEECH_ID FROM IDF_TF ";
    private static final String BASE_SELECT_IDF_TF_VALUES_OF_WORD_WITHOUT_ACCENT = "SELECT SCORE, WORD, SPEECH_ID FROM IDF_TF ";

    private static final String ID = "ID";
    private static final String SPEECH_ID = "SPEECH_ID";
    private static final String TOTAL_WORDS = "TOTAL_WORDS";
    private static final String WORD = "WORD";
    private static final String SINGLE_QUOTE = "'";
    private static final String SINGLE_QUOTE_PARENTHESIS = "') ";
    private static final String SPACE = " ";
    private static final String SINGLE_QUOTE_WITH_SPACE = "' ";
    private static final String PERCENTAGE_SINGLE_QUOTE_WITH_SPACE = "%' ";
    private static final String JOIN_SPEECH_ON_TF = "JOIN SPEECH ON (TF.SPEECH_ID = SPEECH.ID) ";
    private static final String JOIN_MEMBER_ON_SPEECH = "JOIN MEMBER ON (SPEECH.MEMBER_ID = MEMBER.ID) ";
    private static final String JOIN_SITTING_ON_SPEECH = "JOIN SITTING ON (SPEECH.SITTING_ID = SITTING.ID) ";
    private static final String JOIN_SESSION_ON_SITTING = "JOIN SESSION ON (SITTING.SESSION_ID = SESSION.ID) ";
    private static final String AND_MEMBER_NAME_LIKE = "AND MEMBER.NAME LIKE '%";
    private static final String AND_SITTING_DATE_LESS_THAN = "AND DATE <= '";
    private static final String AND_SITTING_DATE_GREATER_THAN = "AND DATE >= '";
    private static final String AND_SESSION_NAME = "AND (SESSION.NAME = '";
    private static final String OR_SESSION_PERIOD_NAME = "' OR SESSION.PERIOD_NAME = '";
    private static final String WHERE_WORD = "WHERE WORD = '";
    private static final String WHERE_WORD_IN_ = "WHERE WORD IN ";
    private static final String SCORE = "SCORE";

    public SearchRepository() {
        super();
    }

    public Map<Integer, Integer> getSpeechTotalWords(Set<Integer> speechIds) {
        Map<Integer, Integer> speechTotalWords = new HashMap<>(speechIds.size());

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_SPEECH_TOTAL_WORDS + Functions.generateInClauseFor(speechIds)).executeQuery()) {
            while (resultSet.next()) {
                speechTotalWords.put(resultSet.getInt(ID), resultSet.getInt(TOTAL_WORDS));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return speechTotalWords;
    }

    public Map<Integer, Double> selectIdfTFValuesOfWord(String searchWord, String... args) {
        Map<Integer, Double> tfOfSpeeches = new HashMap<>(30000);

        StringBuilder whereClause = new StringBuilder(WHERE_WORD).append(searchWord).append(SINGLE_QUOTE_WITH_SPACE);

        String s = BASE_SELECT_IDF_TF_VALUES_OF_WORD + createIdfTfValueOfWordQueryFor(whereClause, args);

        Functions.println(s);
        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(s).executeQuery()) {
            while (resultSet.next()) {
                tfOfSpeeches.put(resultSet.getInt(SPEECH_ID), resultSet.getDouble(SCORE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Functions.println(tfOfSpeeches);

        return tfOfSpeeches;
    }


    public Map<String, Map<Integer, Double>> selectIdfTfValuesForWordWithoutAccent(String searchWord, String... args) {
        Map<String, Map<Integer, Double>> idfTfOfSpeechesForWord = new HashMap<>();

        StringBuilder whereClause = new StringBuilder(WHERE_WORD_IN_).append(Functions.generateInClauseFor(Functions.generateAccentVariants(searchWord))).append(SPACE);
        String s = BASE_SELECT_IDF_TF_VALUES_OF_WORD_WITHOUT_ACCENT + createIdfTfValueOfWordQueryFor(whereClause, args);

        Functions.println(s);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(s).executeQuery()) {
            while (resultSet.next()) {
                double idfTf = resultSet.getDouble(SCORE);
                int speechId = resultSet.getInt(SPEECH_ID);
                String word = resultSet.getString(WORD);

                if (!idfTfOfSpeechesForWord.containsKey(word)) {
                    idfTfOfSpeechesForWord.put(word, new HashMap<>());
                }

                Map<Integer, Double> idfTfOfSpeech = idfTfOfSpeechesForWord.get(word);
                idfTfOfSpeech.put(speechId, idfTf);

                idfTfOfSpeechesForWord.put(word, idfTfOfSpeech);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idfTfOfSpeechesForWord;
    }
    public String createIdfTfValueOfWordQueryFor(StringBuilder whereClause, String... args) {
        StringBuilder joins = new StringBuilder();
        boolean joinedSitting = false;

        if (args.length > 1) {
            joins.append(JOIN_SPEECH_ON_TF);

            String memberName = args[1];
            if (Functions.isNotEmpty(memberName)) {
                joins.append(JOIN_MEMBER_ON_SPEECH);

                whereClause.append(AND_MEMBER_NAME_LIKE).append(memberName).append(PERCENTAGE_SINGLE_QUOTE_WITH_SPACE);
            }
        }

        if (args.length > 2) {
            String from = args[2];
            if (Functions.isNotEmpty(from)) {
                joins.append(JOIN_SITTING_ON_SPEECH);
                joinedSitting = true;

                whereClause.append(AND_SITTING_DATE_GREATER_THAN).append(from).append(SINGLE_QUOTE_WITH_SPACE);
            }
        }

        if (args.length > 3) {
            String to = args[3];
            if (Functions.isNotEmpty(to)) {
                if (!joinedSitting) {
                    joins.append(JOIN_SITTING_ON_SPEECH);
                    joinedSitting = true;
                }

                whereClause.append(AND_SITTING_DATE_LESS_THAN).append(to).append(SINGLE_QUOTE);
            }
        }

        if (args.length > 4) {
            String periodOrSession = args[4];
            if (Functions.isNotEmpty(periodOrSession)) {
                if (!joinedSitting) {
                    joins.append(JOIN_SITTING_ON_SPEECH);
                }

                joins.append(JOIN_SESSION_ON_SITTING);

                whereClause.append(AND_SESSION_NAME).append(periodOrSession)
                        .append(OR_SESSION_PERIOD_NAME).append(periodOrSession).append(SINGLE_QUOTE_PARENTHESIS);
            }
        }

        return joins.append(whereClause).toString();
    }
}

