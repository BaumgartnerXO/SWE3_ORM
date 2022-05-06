package at.technikum.orm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionFactory {

    private String url;

    private static void testConnection(String url) throws SQLException {
        try {
            var connection = DriverManager.getConnection(url);
            log.info("Connected to Database");
            connection.close();
        } catch (SQLException e) {
            log.error("Failed to connect to database using {}", url);
            throw e;
        }
    }

    /**
     *
     * Creates a ConnectionFactory to the database
     * @param url
     * @return
     * @throws SQLException
     */
    public static ConnectionFactory with(String url) throws SQLException {
        testConnection(url);
        return new ConnectionFactory(url);
    }

    public Connection get() throws SQLException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error("Unexpected Driver Manager Exception");
            throw e;
        }
    }
}
