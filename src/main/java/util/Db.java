package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Db {
    private static final String URL = "jdbc:mysql://localhost:3306/ocean_view_resort";
    private static final String USER = "root";
    private static final String PASS = "1234";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found on runtime classpath: " + DRIVER, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void verifyConnection() throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next() || rs.getInt(1) != 1) {
                throw new SQLException("Database ping query failed");
            }
        }
    }
}
