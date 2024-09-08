package database;

import entities.PoliticalParty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class PoliticalPartyRepository {
    private static final String INSERT_INTO_POLITICAL_PARTY = "INSERT INTO POLITICAL_PARTY (ID, NAME) VALUES(?, ?)";
    private final Set<PoliticalParty> politicalParties;

    public PoliticalPartyRepository() {
        this.politicalParties = new HashSet<>(32); //based on political parties in the big dataset
    }

    public void addToBatch(PoliticalParty politicalParty) {
        politicalParties.add(politicalParty);
    }

    public void flushBatch() {
        executeBatch();
        politicalParties.clear();
    }

    private void executeBatch() {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement insertIntoPoliticalParty = connection.prepareStatement(INSERT_INTO_POLITICAL_PARTY)) {
            connection.setAutoCommit(false);
            for (PoliticalParty politicalParty : politicalParties) {
                insertIntoPoliticalParty.setInt(1, politicalParty.getId());
                insertIntoPoliticalParty.setString(2, politicalParty.getName());

                insertIntoPoliticalParty.addBatch();
            }

            insertIntoPoliticalParty.executeBatch();

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
