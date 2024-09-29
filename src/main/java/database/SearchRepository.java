package database;

import dto.InfoToShow;
import dto.Member;
import dto.Period;
import search.Entry;
import utility.Functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static utility.Functions.isNotEmpty;

public class SearchRepository {

    private static final String SELECT_SPEECH_TOTAL_WORDS = "SELECT ID, TOTAL_WORDS FROM SPEECH WHERE ID IN ";
    private static final String BASE_SELECT_IDF_TF_VALUES = "SELECT SCORE, WORD, SPEECH_ID FROM IDF_TF ";
    private static final String SELECT_ALL_INFO_TO_SHOW = "SELECT DISTINCT CONTENT, " +
            "MEMBER.NAME AS MEMBER_NAME, POLITICAL_PARTY.NAME AS POLITICAL_PARTY, REGION, " +
            "ROLE, GENDER, SITTING.NAME AS SITTING_NAME, DATE, " +
            "SESSION.NAME AS SESSION_NAME, PERIOD_NAME " +
            "FROM SPEECH " +
            "JOIN MEMBER ON (SPEECH.MEMBER_ID = MEMBER.ID) " +
            "JOIN POLITICAL_PARTY_MEMBERS ON (POLITICAL_PARTY_MEMBERS.MEMBER_ID = MEMBER.ID) " +
            "JOIN POLITICAL_PARTY ON POLITICAL_PARTY_MEMBERS.POLITICAL_PARTY_ID = POLITICAL_PARTY.ID " +
            "JOIN SITTING ON (SPEECH.SITTING_ID = SITTING.ID) " +
            "JOIN SESSION ON (SITTING.SESSION_ID = SESSION.ID) " +
            "JOIN PERIOD ON (SESSION.PERIOD_NAME = PERIOD.NAME) " +
            "WHERE SITTING.DATE BETWEEN " +
            "POLITICAL_PARTY_MEMBERS.START_DATE " +
            "AND COALESCE(POLITICAL_PARTY_MEMBERS.END_DATE, '9999-12-31') " +
            "AND SPEECH.ID IN ";

    private static final String JOIN_SPEECH_ON_TF = "JOIN SPEECH ON (IDF_TF.SPEECH_ID = SPEECH.ID) ";
    private static final String JOIN_MEMBER_ON_SPEECH = "JOIN MEMBER ON (SPEECH.MEMBER_ID = MEMBER.ID) ";
    private static final String JOIN_SITTING_ON_SPEECH = "JOIN SITTING ON (SPEECH.SITTING_ID = SITTING.ID) ";
    private static final String JOIN_SESSION_ON_SITTING = "JOIN SESSION ON (SITTING.SESSION_ID = SESSION.ID) ";
    private static final String AND_MEMBER_NAME_LIKE = "AND MEMBER.NAME LIKE '%";
    private static final String AND_SITTING_DATE_LESS_THAN = "AND DATE <= '";
    private static final String AND_SITTING_DATE_GREATER_THAN = "AND DATE >= '";
    private static final String AND_SESSION_NAME = "AND (SESSION.NAME = '";
    private static final String OR_SESSION_PERIOD_NAME = "' OR SESSION.PERIOD_NAME = '";
    private static final String WHERE_WORD_IN = "WHERE WORD IN ";
    private static final String ID = "ID";
    private static final String SPEECH_ID = "SPEECH_ID";
    private static final String TOTAL_WORDS = "TOTAL_WORDS";
    private static final String WORD = "WORD";
    private static final String SINGLE_QUOTE = "'";
    private static final String SINGLE_QUOTE_PARENTHESIS = "') ";
    private static final String SPACE = " ";
    private static final String SINGLE_QUOTE_WITH_SPACE = "' ";
    private static final String PERCENTAGE_SINGLE_QUOTE_WITH_SPACE = "%' ";
    private static final String SCORE = "SCORE";
    private static final String MEMBER_NAME = "MEMBER_NAME";
    private static final String POLITICAL_PARTY = "POLITICAL_PARTY";
    private static final String REGION = "REGION";
    private static final String ROLE = "ROLE";
    private static final String SITTING_NAME = "SITTING_NAME";
    private static final String DATE = "DATE";
    private static final String CONTENT = "CONTENT";
    private static final String SESSION_NAME = "SESSION_NAME";
    private static final String PERIOD_NAME = "PERIOD_NAME";

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

    public Map<String, Map<Integer, Double>> selectIdfTFValues(Collection<String> searchWords, Entry searchEntry) {
        Map<String, Map<Integer, Double>> idfTfOfSpeechesForWord = new HashMap<>(30000);

        StringBuilder whereClause = new StringBuilder(WHERE_WORD_IN).append(Functions.generateInClauseFor(searchWords)).append(SPACE);

        String query = BASE_SELECT_IDF_TF_VALUES + createIdfTfValueOfWordQueryFor(whereClause, searchEntry);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(query).executeQuery()) {
            while (resultSet.next()) {
                double idfTf = resultSet.getDouble(SCORE);
                int speechId = resultSet.getInt(SPEECH_ID);
                String word = resultSet.getString(WORD);

                if (!idfTfOfSpeechesForWord.containsKey(word)) {
                    idfTfOfSpeechesForWord.put(word, new HashMap<>(250)); //arbitrary size to avoid possible resizings, as initial capacity is 16
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

    public String createIdfTfValueOfWordQueryFor(StringBuilder whereClause, Entry searchEntry) {
        StringBuilder joins = new StringBuilder();
        boolean joinedSpeech = false;
        boolean joinedSitting = false;

        if (isNotEmpty(searchEntry.getMemberName())) {
            joins.append(JOIN_SPEECH_ON_TF);
            joinedSpeech = true;

            String memberName = searchEntry.getMemberName();
            if (isNotEmpty(memberName)) {
                joins.append(JOIN_MEMBER_ON_SPEECH);

                whereClause.append(AND_MEMBER_NAME_LIKE).append(memberName).append(PERCENTAGE_SINGLE_QUOTE_WITH_SPACE);
            }
        }

        if (isNotEmpty(searchEntry.getFrom())) {
            String from = searchEntry.getFrom();
            if (isNotEmpty(from)) {
                if (!joinedSpeech) {
                    joins.append(JOIN_SPEECH_ON_TF);
                    joinedSpeech = true;
                }
                joins.append(JOIN_SITTING_ON_SPEECH);
                joinedSitting = true;

                whereClause.append(AND_SITTING_DATE_GREATER_THAN).append(from).append(SINGLE_QUOTE_WITH_SPACE);
            }
        }

        if (isNotEmpty(searchEntry.getTo())) {
            String to = searchEntry.getTo();
            if (isNotEmpty(to)) {
                if (!joinedSitting) {
                    if (!joinedSpeech) {
                        joins.append(JOIN_SPEECH_ON_TF);
                        joinedSpeech = true;
                    }
                    joins.append(JOIN_SITTING_ON_SPEECH);
                    joinedSitting = true;
                }

                whereClause.append(AND_SITTING_DATE_LESS_THAN).append(to).append(SINGLE_QUOTE);
            }
        }

        if (isNotEmpty(searchEntry.getPeriodOrSession())) {
            String periodOrSession = searchEntry.getPeriodOrSession();
            if (isNotEmpty(periodOrSession)) {
                if (!joinedSpeech) {
                    joins.append(JOIN_SPEECH_ON_TF);
                }
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

    public Collection<InfoToShow> getAllInfoFor(List<Integer> speechIds) {
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
                            resultSet.getString(SITTING_NAME),
                            resultSet.getString(DATE)
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

