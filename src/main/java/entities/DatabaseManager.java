package entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:sqlite:information_retrieval.db";

    public static void main(String[] args) {
        select();
    }

    private static Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void create() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {

            // Create tables
            statement.execute("CREATE TABLE IF NOT EXISTS Speech (id INTEGER PRIMARY KEY)");
            statement.execute("CREATE TABLE IF NOT EXISTS Words (id INTEGER PRIMARY KEY AUTOINCREMENT, speech_id INTEGER, word TEXT, FOREIGN KEY(speech_id) REFERENCES Speech(id))");
            //statement.execute("INSERT INTO Speech (id) VALUES (2)");
            //statement.execute("");

            // Truncate tables (if needed)
//            statement.execute("DELETE FROM Words");
//            statement.execute("DELETE FROM Speech");

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void add() {
        try{
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO words(word, speech_id) VALUES(?,?)");
            pstmt.setString(1, "nass");
            pstmt.setInt(2, 1);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void select() {
        try {
            Connection conn = connect();

            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery("Select * from words");

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("word") + "\t" +
                        rs.getDouble("speech_id"));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}