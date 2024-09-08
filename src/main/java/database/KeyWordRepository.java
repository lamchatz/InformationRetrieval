package database;

import keyword.Entry;
import utility.Functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class KeyWordRepository {

    private static final String SELECT_UNIQUE_DATES = "SELECT DISTINCT (SUBSTR(DATE, -4)) AS DATE FROM SITTING ORDER BY DATE ASC";
    private static final String SELECT_POLITICAL_PARTY_KEYWORD = "SELECT MAX(TOTAL_SCORE) AS SCORE, KEYWORD, POLITICAL_PARTY_NAME " +
            "FROM (" +
                "SELECT " +
                "POLITICAL_PARTY.NAME AS POLITICAL_PARTY_NAME, " +
                "IDF_TF.WORD AS KEYWORD, " +
                "SUM(IDF_TF.SCORE) AS TOTAL_SCORE " +
                "FROM IDF_TF " +
                "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
                "JOIN MEMBER ON SPEECH.MEMBER_ID = MEMBER.ID " +
                "JOIN SITTING ON SPEECH.SITTING_ID = SITTING.ID " +
                "JOIN POLITICAL_PARTY ON MEMBER.POLITICAL_PARTY_ID " +
                "WHERE SITTING.DATE LIKE '%%%s'" +
                "GROUP BY POLITICAL_PARTY.NAME, IDF_TF.WORD" +
            ") " +
            "GROUP BY POLITICAL_PARTY_NAME";

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
    private static final String KEYWORD = "KEYWORD";
    private static final String SCORE = "SCORE";
    private static final String MEMBER_NAME = "MEMBER_NAME";
    private static final String DATE = "DATE";

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

    public Collection<Entry> getMembersKeyWordForEachYear(String date) {
        Collection<Entry> results = new ArrayList<>();
        String query = String.format(SELECT_MEMBER_KEYWORD, date);

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


    public Collection<Entry> getKeyWordForPoliticalParties(String date) {
        Collection<Entry> results = new ArrayList<>();

        String query = String.format(SELECT_POLITICAL_PARTY_KEYWORD, date);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(query).executeQuery()) {
            while (resultSet.next()) {
                results.add(new Entry(date, resultSet.getString("POLITICAL_PARTY_NAME"),
                        resultSet.getString(KEYWORD),
                        resultSet.getDouble(SCORE))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Functions.println(results);
        return results;
    }


}
