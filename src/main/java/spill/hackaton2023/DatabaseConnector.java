package spill.hackaton2023;

import java.sql.*;

public class DatabaseConnector {

    private Connection connection;

    public void connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/spill";
        String username = "root";
        String password = "Tobesmart7.";
        connection = DriverManager.getConnection(url, username, password);
        System.out.println("Connected to the database");
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("Disconnected from the database");
        }
    }

    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    /*

    public int executeUpdate(String query, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeUpdate();
    }
     */

}
