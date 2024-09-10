package database;

import config.Config;
import keyword.Entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class KeyWordRepository {

    private static final String SELECT_UNIQUE_DATES = "SELECT DISTINCT (SUBSTR(DATE, -4)) AS DATE FROM SITTING ORDER BY DATE ASC";
    private static final String SELECT_POLITICAL_PARTY_KEYWORD = "SELECT TOTAL_SCORE AS SCORE, KEYWORD, POLITICAL_PARTY_NAME " +
            "FROM ( " +
                "SELECT " +
                    "POLITICAL_PARTY.NAME AS POLITICAL_PARTY_NAME," +
                    "IDF_TF.WORD AS KEYWORD," +
                    "SUM(IDF_TF.SCORE) AS TOTAL_SCORE, " +
                    "ROW_NUMBER() OVER (PARTITION BY POLITICAL_PARTY.NAME ORDER BY SUM(IDF_TF.SCORE) DESC) AS RN " +
                "FROM IDF_TF " +
                    "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
                    "JOIN MEMBER ON SPEECH.MEMBER_ID = MEMBER.ID " +
                    "JOIN POLITICAL_PARTY ON POLITICAL_PARTY.ID = MEMBER.POLITICAL_PARTY_ID " +
                    "JOIN SITTING ON SPEECH.SITTING_ID = SITTING.ID " +
                    "JOIN POLITICAL_PARTY_MEMBERS ON POLITICAL_PARTY.ID = POLITICAL_PARTY_MEMBERS.POLITICAL_PARTY_ID AND MEMBER.ID = POLITICAL_PARTY_MEMBERS.MEMBER_ID " +
                "WHERE " +
                    "SITTING.DATE LIKE ? " +
                    "AND SUBSTR(POLITICAL_PARTY_MEMBERS.START_DATE, -4) <= SUBSTR(SITTING.DATE, -4) " +
                    "AND (POLITICAL_PARTY_MEMBERS.END_DATE IS NULL OR SUBSTR(POLITICAL_PARTY_MEMBERS.END_DATE, -4) >= SUBSTR(SITTING.DATE, -4) ) " +
                "GROUP BY POLITICAL_PARTY.NAME, IDF_TF.WORD " +
            ") " +
            "WHERE RN <= ?;";

    private static final String SELECT_MEMBER_KEYWORD = "SELECT MAX(TOTAL_SCORE) AS SCORE, KEYWORD, MEMBER_NAME " +
            "FROM (" +
                "SELECT " +
                "MEMBER.NAME AS MEMBER_NAME, " +
                "IDF_TF.WORD AS KEYWORD, " +
                "SUM(IDF_TF.SCORE) AS TOTAL_SCORE " +
                "FROM IDF_TF " +
                "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
                "JOIN MEMBER ON SPEECH.MEMBER_ID = MEMBER.ID " +
                "JOIN SITTING ON SPEECH.SITTING_ID = SITTING.ID " +
                "WHERE SITTING.DATE LIKE '%%%s'" +
                "GROUP BY MEMBER.NAME, IDF_TF.WORD" +
            ") " +
            "GROUP BY MEMBER_NAME";

    private static final String SELECT_MEMBER_K_KEYWORDS = "SELECT " +
            "MEMBER_NAME," +
            "KEYWORD," +
            "SCORE " +
            "FROM " +
            "( " +
                "SELECT " +
                    "MEMBER.NAME AS MEMBER_NAME," +
                    "IDF_TF.WORD AS KEYWORD," +
                    "SUM(IDF_TF.SCORE) AS SCORE," +
                    "ROW_NUMBER() OVER (PARTITION BY MEMBER.NAME ORDER BY SUM(IDF_TF.SCORE) DESC) AS RN " +
                "FROM IDF_TF " +
                    "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
                    "JOIN MEMBER ON SPEECH.MEMBER_ID = MEMBER.ID " +
                    "JOIN SITTING ON SPEECH.SITTING_ID = SITTING.ID " +
                "WHERE SITTING.DATE LIKE '%%%s' " +
                "GROUP BY MEMBER.NAME, IDF_TF.WORD " +
            ")" +
            "WHERE RN <= %d " +
            "ORDER BY MEMBER_NAME, SCORE DESC;";

    private static final String SELECT_SPEECH_K_KEYWORDS =
            "SELECT " +
                "SPEECH_ID," +
                "GROUP_CONCAT(KEYWORD || ':' || SCORE, ', ') AS KEYWORDS_SCORES," +
                "MEMBER_NAME," +
                "CONTENT " +
            "FROM " +
            "(" +
                "SELECT " +
                    "IDF_TF.WORD AS KEYWORD," +
                    "IDF_TF.SCORE AS SCORE," +
                    "IDF_TF.SPEECH_ID," +
                    "MEMBER.NAME AS MEMBER_NAME," +
                    "SPEECH.CONTENT AS CONTENT," +
                    "ROW_NUMBER() OVER (PARTITION BY IDF_TF.SPEECH_ID ORDER BY IDF_TF.SCORE DESC) AS RN " +
                "FROM IDF_TF " +
                    "JOIN SPEECH ON SPEECH.ID = IDF_TF.SPEECH_ID " +
                    "JOIN MEMBER ON MEMBER.ID = SPEECH.MEMBER_ID " +
                    "JOIN SITTING ON SITTING.ID = SPEECH.SITTING_ID " +
                "WHERE SITTING.DATE LIKE '%%%s' " +
            ") " +
            "WHERE RN <= %d " +
            "GROUP BY SPEECH_ID, MEMBER_NAME, CONTENT " +
            "ORDER BY SPEECH_ID;";

    private static final String SELECT_SPEECH_KEYWORD = "SELECT SPEECH_ID, WORD AS KEYWORD, MAX(SCORE) AS SCORE, MEMBER.NAME AS MEMBER_NAME, SPEECH.CONTENT AS CONTENT " +
            "FROM IDF_TF " +
            "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
            "JOIN MEMBER ON SPEECH.MEMBER_ID = MEMBER.ID " +
            "JOIN SITTING ON SPEECH.SITTING_ID = SITTING.ID " +
            "WHERE SITTING.DATE LIKE '%%%S' " +
            "GROUP BY SPEECH_ID " +
            "ORDER BY SPEECH_ID";
    private static final String KEYWORD = "KEYWORD";
    private static final String SCORE = "SCORE";
    private static final String MEMBER_NAME = "MEMBER_NAME";
    private static final String DATE = "DATE";
    private static final int NUMBER_OF_KEYWORDS = Config.NUMBER_OF_KEY_WORDS;

    public KeyWordRepository() {
        super();
    }

    public Collection<String> getUniqueDates() {
        Collection<String> dates = new ArrayList<>(31); //2020 - 1989

        try (Connection connection = DatabaseManager.connect();
             ResultSet queryResult = connection.prepareStatement(SELECT_UNIQUE_DATES).executeQuery()) {
            while (queryResult.next()) {
                dates.add(queryResult.getString(DATE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dates;
    }

    public Collection<Entry> getMembersKeyWordsForEachYear(String date) {
        Collection<Entry> results = new ArrayList<>(1524);

        String query = String.format(SELECT_MEMBER_KEYWORD, date);
        if (NUMBER_OF_KEYWORDS > 1 ) {
            query = String.format(SELECT_MEMBER_K_KEYWORDS, date, NUMBER_OF_KEYWORDS);
        }

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(query).executeQuery()) {
            while (resultSet.next()) {
                results.add(new Entry(date, resultSet.getString(MEMBER_NAME),
                        resultSet.getString(KEYWORD),
                        resultSet.getDouble(SCORE))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public Collection<Entry> getKeyWordsForPoliticalPartiesForEachYear(String date) {
        Collection<Entry> results = new ArrayList<>();

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_POLITICAL_PARTY_KEYWORD)) {

            selectStatement.setString(1, '%' + date);
            selectStatement.setInt(2, NUMBER_OF_KEYWORDS);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(new Entry(date, resultSet.getString("POLITICAL_PARTY_NAME"),
                            resultSet.getString(KEYWORD),
                            resultSet.getDouble(SCORE))
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public Collection<Entry> getKeyWordForSpeech(String date) {
        if (NUMBER_OF_KEYWORDS > 1) {
            return getSpeechesTopKKeyWordsForEachYEar(date);
        }
        return getSpeechesSingleKeyWordForEachYear(date);
    }

    private Collection<Entry> getSpeechesSingleKeyWordForEachYear(String date) {
        Collection<Entry> results = new ArrayList<>(5000);

        String query = String.format(SELECT_SPEECH_KEYWORD, date);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(query).executeQuery()) {
            while(resultSet.next()) {
                results.add(new Entry(date, resultSet.getString(MEMBER_NAME),
                        resultSet.getString(KEYWORD),
                        resultSet.getDouble(SCORE),
                        resultSet.getInt("SPEECH_ID"),
                        resultSet.getString("CONTENT"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    private Collection<Entry> getSpeechesTopKKeyWordsForEachYEar(String date) {
        Collection<Entry> results = new ArrayList<>(5000);

        String query = String.format(SELECT_SPEECH_K_KEYWORDS, date, NUMBER_OF_KEYWORDS);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(query).executeQuery()) {
            while(resultSet.next()) {
                results.add(new Entry(date, resultSet.getString(MEMBER_NAME),
                        resultSet.getString("KEYWORDS_SCORES"),
                        resultSet.getInt("SPEECH_ID"),
                        resultSet.getString("CONTENT"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }


}
