package database;

import utility.Functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MemberSimilarityRepository {
    private static final String SELECT_MEMBERS_WORD = "SELECT " +
            "MEMBER_ID," +
            "IDF_TF.WORD AS KEYWORD," +
            "AVG(IDF_TF.SCORE) AS SCORE " +
            "FROM IDF_TF " +
            "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
            "GROUP BY MEMBER_ID, WORD";
    private static final String SELECT_WORDS_FOR_ID = "SELECT " +
            "MEMBER_ID," +
            "IDF_TF.WORD AS KEYWORD," +
            "AVG(IDF_TF.SCORE) AS SCORE " +
            "FROM IDF_TF " +
            "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
            "WHERE MEMBER_ID = %d " +
            "GROUP BY MEMBER_ID, WORD";
    private static final String SELECT_MEMBER_NAMES_FOR_IDS = "SELECT ID, NAME FROM MEMBER WHERE ID IN ";
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String SELECT_WORDS_FOR_MEMBER_IDS = "SELECT " +
            "MEMBER_ID," +
            "IDF_TF.WORD AS KEYWORD," +
            "AVG(IDF_TF.SCORE) AS SCORE " +
            "FROM IDF_TF " +
            "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
            "WHERE MEMBER_ID IN %s GROUP BY MEMBER_ID, WORD";
    private static final String KEYWORD = "KEYWORD";
    private static final String SCORE = "SCORE";
    private static final String MEMBER_ID = "MEMBER_ID";
    private static final String SELECT_MEMBER_IDS = "SELECT ID FROM MEMBER";

    private final List<Integer> memberIds;

    public MemberSimilarityRepository() {
        this.memberIds = selectMemberIds();
    }

    private List<Integer> selectMemberIds() {
        List<Integer> memberIds = new ArrayList<>(1524);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_MEMBER_IDS).executeQuery()) {
            while (resultSet.next()) {
                memberIds.add(resultSet.getInt(ID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberIds;
    }

    public Map<Integer, Map<String, Double>> getWordsForIds(List<Integer> memberIds) {
        Map<Integer, Map<String, Double>> memberWords = new HashMap<>(memberIds.size());

        if (!memberIds.isEmpty()) {
            try (Connection connection = DatabaseManager.connect();
                 ResultSet resultSet = connection.prepareStatement(String.format(SELECT_WORDS_FOR_MEMBER_IDS, Functions.generateInClauseFor(memberIds))).executeQuery()) {
                while (resultSet.next()) {
                    memberWords.computeIfAbsent(resultSet.getInt(MEMBER_ID), k -> new HashMap<>()).put(resultSet.getString(KEYWORD), resultSet.getDouble(SCORE));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return memberWords;
    }

    public Map<String, Double> getWordsForId(Integer id) {
        Map<String, Double> memberWords = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(String.format(SELECT_WORDS_FOR_ID, id)).executeQuery()) {
            while (resultSet.next()) {
                memberWords.put(resultSet.getString(KEYWORD), resultSet.getDouble(SCORE));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberWords;
    }

    public Map<Integer, Map<String, Double>> getAllMemberWords() {
        Map<Integer, Map<String, Double>> memberWords = new HashMap<>(1524);
        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_MEMBERS_WORD).executeQuery()) {

            while (resultSet.next()) {
                memberWords.computeIfAbsent(resultSet.getInt(MEMBER_ID), k -> new HashMap<>()).put(resultSet.getString(KEYWORD), resultSet.getDouble(SCORE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberWords;
    }

    public List<Integer> getMemberIds() {
        return memberIds;
    }

    public Map<Integer, String> getMemberNames(Set<Integer> ids) {
        Map<Integer, String> names = new HashMap<>(ids.size());

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_MEMBER_NAMES_FOR_IDS + Functions.generateInClauseFor(ids)).executeQuery()) {
            while (resultSet.next()) {
                names.put(resultSet.getInt(ID), resultSet.getString(NAME));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }
}
