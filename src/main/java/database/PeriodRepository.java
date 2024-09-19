package database;

import entities.parliament.Period;
import entities.parliament.Session;
import entities.parliament.Sitting;
import utility.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PeriodRepository {
    private static final String INSERT_INTO_PERIOD = "INSERT INTO PERIOD (NAME) VALUES (?) ON CONFLICT DO NOTHING";
    private static final String INSERT_INTO_SESSION = "INSERT INTO SESSION (ID, NAME, PERIOD_NAME) VALUES (?, ?, ?)";
    private static final String INSERT_INTO_SITTING = "INSERT INTO SITTING (ID, NAME, SESSION_ID, DATE) VALUES (?, ?, ?, ?)";

    public PeriodRepository() {
        super();
    }

    public void save(Period period) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement insertIntoPeriod = connection.prepareStatement(INSERT_INTO_PERIOD);
             PreparedStatement insertIntoSession = connection.prepareStatement(INSERT_INTO_SESSION);
             PreparedStatement insertIntoSitting = connection.prepareStatement(INSERT_INTO_SITTING)) {

            connection.setAutoCommit(false);

            insertIntoPeriod.setString(1, period.getName());
            insertIntoPeriod.execute();

            for (Session session : period.getSessions()) {
                insertIntoSession.setLong(1, session.getId());
                insertIntoSession.setString(2, session.getName());
                insertIntoSession.setString(3, period.getName());
                insertIntoSession.addBatch();
                for (Sitting sitting : session.getSittings()) {
                    insertIntoSitting.setLong(1, sitting.getId());
                    insertIntoSitting.setString(2, sitting.getName());
                    insertIntoSitting.setLong(3, session.getId());
                    insertIntoSitting.setString(4, Functions.convertDateFormat(sitting.getDate()));
                    insertIntoSitting.addBatch();
                }
            }

            insertIntoSession.executeBatch();
            insertIntoSitting.executeBatch();

            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
