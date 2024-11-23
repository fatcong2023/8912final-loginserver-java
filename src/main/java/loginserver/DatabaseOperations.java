package loginserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

public class DatabaseOperations {
    private static final String CONNECTION_URL = "jdbc:sqlserver://8912finallab.database.windows.net:1433;database=xiao8915;user=xiao@8912finallab;password=Zz300312!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    public static void insertUser(String firstName, String lastName, String username, String phoneNumber, String email, Logger logger) throws Exception {
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
            String insertSql = "INSERT INTO dbo.Users (firstName, lastName, userName, phoneNumber, email) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, username);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setString(5, email);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            logger.severe("Database insertion error: " + e.getMessage());
            throw e;
        }
    }
}