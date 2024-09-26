package database;

import entities.Member;
import utility.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MemberRepository{
    protected static final String INSERT_INTO_MEMBER = "INSERT INTO MEMBER (ID, NAME, POLITICAL_PARTY_ID, REGION, ROLE, GENDER) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    public MemberRepository() {
        super();
    }

    protected void executeBatch(Connection connection, Collection<Member> membersBatch) {
        Functions.println("Flushing Members...");
        try (PreparedStatement insertIntoMember = connection.prepareStatement(INSERT_INTO_MEMBER)) {
            for (Member member : membersBatch) {
                insertIntoMember.setInt(1, member.getId());
                insertIntoMember.setString(2, member.getName());
                insertIntoMember.setInt(3, member.getPoliticalPartyId());
                insertIntoMember.setString(4, member.getRegion());
                insertIntoMember.setString(5, member.getRole());
                insertIntoMember.setString(6, member.getGender());
                insertIntoMember.addBatch();
            }

            membersBatch.clear();
            insertIntoMember.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}