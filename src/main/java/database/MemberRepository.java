package database;

import entities.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class MemberRepository{

    protected static final String INSERT_INTO_MEMBER = "INSERT INTO MEMBER (ID, NAME, POLITICAL_PARTY_ID, REGION, ROLE, GENDER) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private final Set<Member> batchMembers;

    public MemberRepository() {
        this.batchMembers = new HashSet<>(1524);
    } //based on members in the big dataset

    public void flushBatch() {
        executeBatch();
        batchMembers.clear();
    }

    public void addToBatch(Member member) {
        batchMembers.add(member);
    }

    private void executeBatch() {
        try (Connection connection = DatabaseManager.connect();
                PreparedStatement insertIntoMember = connection.prepareStatement(INSERT_INTO_MEMBER)) {
            connection.setAutoCommit(false);
            for (Member member : batchMembers) {
                insertIntoMember.setInt(1, member.getId());
                insertIntoMember.setString(2, member.getName());
                insertIntoMember.setInt(3, member.getPoliticalPartyId());
                insertIntoMember.setString(4, member.getRegion());
                insertIntoMember.setString(5, member.getRole());
                insertIntoMember.setString(6, member.getGender());
                insertIntoMember.addBatch();
            }

            insertIntoMember.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
