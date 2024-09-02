package database;

import config.Config;
import entities.Member;
import utility.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MemberRepository{

    protected static final String INSERT_INTO_MEMBER = "INSERT INTO MEMBER (ID, NAME, POLITICAL_PARTY, REGION, ROLE, GENDER) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private Set<Member> batchMembers;

    public MemberRepository() {
        this.batchMembers = new HashSet<>();
    }

    public void flushBatch() {
        executeBatch();
        batchMembers.clear();
    }

    public void addToBatch(Member member) {
        batchMembers.add(member);

        if (batchMembers.size() == Config.EXECUTE_BATCH_AFTER) {
            Functions.println("Saving members, size: " + batchMembers.size());
            flushBatch();
        }
    }

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
                preparedStatement.setString(6, member.getGender());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
