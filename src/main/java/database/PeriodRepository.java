package database;

import entities.parliament.Period;
import entities.parliament.Processor;
import entities.parliament.Session;
import entities.parliament.Sitting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodRepository implements AbstractRepository<Period> {


    private static final String INSERT_INTO_PERIOD = "INSERT INTO PERIOD (NAME) VALUES (?) ON CONFLICT DO NOTHING";
    private static final String INSERT_INTO_SESSION = "INSERT INTO SESSION (ID, NAME, PERIOD_NAME) VALUES (?, ?, ?)";
    private static final String INSERT_INTO_SITTING = "INSERT INTO SITTING (ID, NAME, SESSION_ID, SITTING_DATE) VALUES (?, ?, ?, ?)";

    private static final String SELECT_PERIOD_BY_PERIOD_NAME = "SELECT PERIOD.NAME AS PERIOD_NAME, SESSION.ID AS SESSION_ID, SESSION.NAME AS SESSION_NAME," +
            " SITTING.ID AS SITTING_ID, SITTING.NAME AS SITTING_NAME, SITTING.SITTING_DATE AS SITTING_DATE" +
            " FROM PERIOD" +
            " JOIN SESSION ON (SESSION.PERIOD_NAME = PERIOD.NAME)" +
            " JOIN SITTING ON (SITTING.SESSION_ID = SESSION.ID)" +
            " WHERE PERIOD.NAME like ?";

    @Override
    public void selectAll() {

    }

    @Override
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
                    insertIntoSitting.setString(4, sitting.getDate().toString());
                    insertIntoSitting.addBatch();
                }
            }

            insertIntoSession.executeBatch();
            insertIntoSitting.executeBatch();

            connection.commit();

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Period getByName(String name) {
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PERIOD_BY_PERIOD_NAME)) {
            preparedStatement.setString(1, name);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                // loop through the result set
                Period period = new Period(name);
                while (rs.next()) {
                    String sessionName = rs.getString("SESSION_NAME");
                    String sittingName = rs.getString("SITTING_NAME");
                    String sittingDate = rs.getString("SITTING_DATE");

                    Processor.managePeriodSessionAndSitting(period, sessionName, sittingName, sittingDate);
                }

                return period;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

}
