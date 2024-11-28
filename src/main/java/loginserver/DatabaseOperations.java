package loginserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class DatabaseOperations {
    private static final String CONNECTION_URL = "jdbc:sqlserver://8912finallab.database.windows.net:1433;database=xiao8915;user=xiao@8912finallab;password=Zz300312!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQL Server JDBC driver", e);
        }
    }

    public static void insertUser(String firstName, String lastName, String username, String phoneNumber, String email, String password, Logger logger) throws Exception {
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
            String insertSql = "INSERT INTO dbo.Users (firstName, lastName, userName, phoneNumber, email, passwordHash) VALUES (?, ?, ?, ?, ?, ?)";
            logger.info(insertSql);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, username);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setString(5, email);
                preparedStatement.setString(6, password);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            logger.severe("Database insertion error: " + e.getMessage());
            throw e;
        }
    }

    public static boolean validateUser(String username, String password, Logger logger) throws Exception {
    try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
        String selectSql = "SELECT passwordHash FROM dbo.Users WHERE userName = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPasswordHash = resultSet.getString("passwordHash");
                    return storedPasswordHash.equals(password);
                } else {
                    return false;
                }
            }
        }
    } catch (Exception e) {
        logger.severe("Database query error: " + e.getMessage());
        throw e;
    }
}
}