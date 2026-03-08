package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:sqlite:src/Database/truema.db";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

