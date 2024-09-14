package database;

import entities.Entry;
import utility.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberSimilarityRepository {
    private static final String SELECT_MEMBERS_WORD = "SELECT " +
            "MEMBER.NAME AS MEMBER_NAME," +
            "IDF_TF.WORD AS KEYWORD," +
            "AVG(IDF_TF.SCORE) AS SCORE " +
            "FROM IDF_TF " +
            "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
            "JOIN MEMBER ON SPEECH.MEMBER_ID = MEMBER.ID " +
            "GROUP BY MEMBER.NAME, WORD " +
            "ORDER BY MEMBER.NAME";

    private static final String SELECT_QUERY = "SELECT IDF_TF.WORD AS KEYWORD, AVG(IDF_TF.SCORE) AS SCORE " +
            "FROM IDF_TF JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
            "WHERE MEMBER_ID = %d " +
            "GROUP BY MEMBER_ID, WORD";

    private final List<Integer> memberIds;

    public MemberSimilarityRepository() {
        this.memberIds = selectMemberIds();
        //createMemberWordsView();
    }

    private List<Integer> selectMemberIds() {
        List<Integer> memberIds = new ArrayList<>(1524);

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement("SELECT ID FROM MEMBER").executeQuery()) {
            while (resultSet.next()) {
                memberIds.add(resultSet.getInt("ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberIds;
    }

    private void createMemberWordsView() {
        try (Connection connection = DatabaseManager.connect()) {
            connection.setAutoCommit(false);
            for (Integer id : memberIds) {
                String sql = String.format(SELECT_QUERY, id);

                try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE VIEW IF NOT EXISTS WORDS_OF_" + id + " AS " + sql)) {
                    preparedStatement.execute();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Map<String, Double>> getWordsForIds(List<Integer> memberIds) {
        Map<Integer, Map<String, Double>> memberWords = new HashMap<>(1524);

        if (!memberIds.isEmpty()) {
            String sql = "SELECT " +
                    "MEMBER_ID," +
                    "IDF_TF.WORD AS KEYWORD," +
                    "AVG(IDF_TF.SCORE) AS SCORE " +
                    "FROM IDF_TF " +
                    "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
                    "WHERE MEMBER_ID IN " + Functions.generateInClauseFor(memberIds) +
                    " GROUP BY MEMBER_ID, WORD";
            try (Connection connection = DatabaseManager.connect();
                 ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
                while (resultSet.next()) {
                    memberWords.computeIfAbsent(resultSet.getInt("MEMBER_ID"), k -> new HashMap<>()).put(resultSet.getString("KEYWORD"), resultSet.getDouble("SCORE"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return memberWords;
    }

    public Map<String, Double> getWordsForId(Integer id) {
        Map<String, Double> memberWords = new HashMap<>();

        String sql = "SELECT " +
                "MEMBER_ID," +
                "IDF_TF.WORD AS KEYWORD," +
                "AVG(IDF_TF.SCORE) AS SCORE " +
                "FROM IDF_TF " +
                "JOIN SPEECH ON IDF_TF.SPEECH_ID = SPEECH.ID " +
                "WHERE MEMBER_ID = " + id +
                " GROUP BY MEMBER_ID, WORD";
        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            while (resultSet.next()) {
                memberWords.put(resultSet.getString("KEYWORD"), resultSet.getDouble("SCORE"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberWords;
    }

    public Map<String, Map<String, Double>> getMembersWords() {
        Map<String, Map<String, Double>> memberWords = new HashMap<>(1524);
        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet = connection.prepareStatement(SELECT_MEMBERS_WORD).executeQuery()) {

            while (resultSet.next()) {
                memberWords.computeIfAbsent(resultSet.getString("MEMBER_NAME"), k -> new HashMap<>()).put(resultSet.getString("KEYWORD"), resultSet.getDouble("SCORE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberWords;
    }

    public Map<String, Double> ss(Integer id) {
        String sql1 = "SELECT * FROM WORDS_OF_" + id;
        HashMap<String, Double> membersWords = new HashMap<>();

        try (Connection connection = DatabaseManager.connect();
             ResultSet resultSet1 = connection.prepareStatement(sql1).executeQuery()) {
            HashMap<String, Double> wordScores1 = new HashMap<>();
            while (resultSet1.next()) {
                membersWords.put(resultSet1.getString("KEYWORD"), resultSet1.getDouble("SCORE"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membersWords;
    }

    public Map<Integer, Map<String, Double>> s(Integer id1, Integer id2) {
        String sql1 = "SELECT * FROM WORDS_OF_" + id1;
        String sql2 = "SELECT * FROM WORDS_OF_" + id2;

        HashMap<Integer, Map<String, Double>> membersWords = new HashMap<>(2);


        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql1);
             PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
             ResultSet resultSet1 = preparedStatement.executeQuery();
             ResultSet resultSet2 = preparedStatement2.executeQuery()) {
            HashMap<String, Double> wordScores1 = new HashMap<>();
            while (resultSet1.next()) {
                wordScores1.put(resultSet1.getString("KEYWORD"), resultSet1.getDouble("SCORE"));
            }

            HashMap<String, Double> wordScores2 = new HashMap<>();
            while (resultSet2.next()) {
                wordScores2.put(resultSet1.getString("KEYWORD"), resultSet1.getDouble("SCORE"));
            }

            membersWords.put(id1, wordScores1);
            membersWords.put(id2, wordScores2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membersWords;
    }

    public List<Integer> getMemberIds() {
        return memberIds;
    }

    public void print(Map<String, Collection<Entry>> map) {
        for (Map.Entry<String, Collection<Entry>> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

}
