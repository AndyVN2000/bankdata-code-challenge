package com.andy;

/**
 * Inspiration from https://www.javaguides.net/2019/08/java-h2-database-tutorial.html
 * Regarding the choice of datatype for balance, I considered either DOUBLE or DECIMAL.
 * I chose DECIMAL since I read that it is more precise.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BankConnection {

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    public static void main(String[] args) {
        String createTableSQL = "CREATE TABLE bank_accounts (" +
                                "account_number INT AUTO_INCREMENT PRIMARY KEY, " +
                                "balance DECIMAL(10, 2), " +
                                "first_name VARCHAR(50), " +
                                "last_name VARCHAR(50))";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Table 'products' created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
