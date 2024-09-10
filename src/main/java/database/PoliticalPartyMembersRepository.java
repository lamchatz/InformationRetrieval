package database;

import entities.PoliticalPartyMemberRelation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PoliticalPartyMembersRepository {

    private static final String INSERT_INTO_POLITICAL_PARTY_MEMBERS = "INSERT INTO POLITICAL_PARTY_MEMBERS " +
            "(POLITICAL_PARTY_ID, MEMBER_ID, START_DATE) VALUES(?, ?, ?)";

    private static final String UPDATE_POLITICAL_PARTY_MEMBER = "UPDATE POLITICAL_PARTY_MEMBERS SET END_DATE = ? " +
            "WHERE MEMBER_ID = ? AND END_DATE IS NULL";

    public PoliticalPartyMembersRepository() {
        super();
    }

    public void insert(PoliticalPartyMemberRelation politicalPartyMemberRelation) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement update = connection.prepareStatement(UPDATE_POLITICAL_PARTY_MEMBER);
             PreparedStatement insertInto = connection.prepareStatement(INSERT_INTO_POLITICAL_PARTY_MEMBERS)) {

            connection.setAutoCommit(false);

            int politicalPartyId = politicalPartyMemberRelation.getPoliticalPartyId();
            int memberId = politicalPartyMemberRelation.getMemberId();
            String from = politicalPartyMemberRelation.getFrom();

            update.setString(1, from);
            update.setInt(2, memberId);

            insertInto.setInt(1, politicalPartyId);
            insertInto.setInt(2, memberId);
            insertInto.setString(3, from);

            update.execute();
            insertInto.execute();

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
