package database;

import entities.PoliticalParty;
import utility.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class PoliticalPartyRepository {
    private static final String INSERT_INTO_POLITICAL_PARTY = "INSERT INTO POLITICAL_PARTY (ID, NAME) VALUES(?, ?)";

    public PoliticalPartyRepository() {
        super();
    }

    protected void executeBatch(Connection connection, Collection<PoliticalParty> politicalParties) {
        Functions.println("Flushing Political Parties...");
        try (PreparedStatement insertIntoPoliticalParty = connection.prepareStatement(INSERT_INTO_POLITICAL_PARTY)) {
            for (PoliticalParty politicalParty : politicalParties) {
                insertIntoPoliticalParty.setInt(1, politicalParty.getId());
                insertIntoPoliticalParty.setString(2, politicalParty.getName());

                insertIntoPoliticalParty.addBatch();
            }

            politicalParties.clear();
            insertIntoPoliticalParty.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
