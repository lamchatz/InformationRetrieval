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

    private final Set<Member> batchMembers;

    public MemberRepository() {
        this.batchMembers = new HashSet<>(Config.EXECUTE_BATCH_AFTER);
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
                PreparedStatement insertIntoMember = connection.prepareStatement(INSERT_INTO_MEMBER)) {
            for (Member member : batchMembers) {
                insertIntoMember.setInt(1, member.getId());
                insertIntoMember.setString(2, member.getName());
                insertIntoMember.setString(3, member.getPoliticalParty());
                insertIntoMember.setString(4, member.getRegion());
                insertIntoMember.setString(5, member.getRole());
                insertIntoMember.setString(6, member.getGender());
                insertIntoMember.addBatch();
            }

            insertIntoMember.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
