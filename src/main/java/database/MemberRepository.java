package database;

import config.Config;
import entities.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MemberRepository implements AbstractBatchRepository<Member> {

    protected static final String INSERT_INTO_MEMBER = "INSERT INTO MEMBER (ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, POLITICAL_PARTY, REGION, ROLE, GENDER) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_MEMBER_IDS_BY_NAME = "SELECT ID FROM MEMBER WHERE NAME like ?";
    private Set<Member> batchMembers;

    public MemberRepository() {
        this.batchMembers = new HashSet<>();
    }

    @Override
    public void flushBatch() {
        executeBatch();
        batchMembers.clear();
    }

    @Override
    public void addToBatch(Member member) {
        batchMembers.add(member);

        if (batchMembers.size() == Config.EXECUTE_BATCH_AFTER) {
            System.out.println("Saving members, size: " + batchMembers.size());
            flushBatch();
        }
    }

    @Override
    public void executeBatch() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_MEMBER)) {

            connection.setAutoCommit(false);

            for (Member member : batchMembers) {
                preparedStatement.setInt(1, member.getId());
                preparedStatement.setString(2, member.getName());
                preparedStatement.setString(3, member.getPoliticalParty());
                preparedStatement.setString(4, member.getRegion());
                preparedStatement.setString(5, member.getRole());
                preparedStatement.setInt(6, member.getGenderAsInt());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Member getByName(String name) {
        return null;
    }

    @Override
    public void selectAll() {
        try (Connection connection = DatabaseManager.connect();
             ResultSet rs = connection.createStatement().executeQuery("Select * from MEMBER")) {

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("NAME") + "\t" +
                        rs.getString("POLITICAL_PARTY") + "\t" +
                        rs.getString("REGION") + "\t" +
                        rs.getString("ROLE") + "\t" +
                        rs.getInt("GENDER"));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Collection<Integer> selectMemberIdsByName(String name) {
        Collection<Integer> memberIds = new ArrayList<>();

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement selectMemberIdsByName = connection.prepareStatement(SELECT_MEMBER_IDS_BY_NAME)) {
            selectMemberIdsByName.setString(1, '%' + name + '%');

            try (ResultSet resultSet = selectMemberIdsByName.executeQuery()) {
                while (resultSet.next()) {
                    memberIds.add(resultSet.getInt("ID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberIds;
    }


    @Override
    public void save(Member element) {

    }
}
