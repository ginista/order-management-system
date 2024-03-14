package org.hexaware.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/orderManagement?useSSL=false","root","ginista");
        } catch (SQLException e) {
            throw e;
        }
        return connection;
    }
}
