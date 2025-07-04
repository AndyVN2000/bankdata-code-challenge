package com.andy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class AccountService {

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    public EntityAccount createAccount(EntityAccount account) throws SQLException {
        String insertSQL = "INSERT INTO EntityAccount (accountNumber, balance, firstName, lastName) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setInt(1, account.getAccountNumber());
            statement.setDouble(2, account.getBalance());
            statement.setString(3, account.getFirstName());
            statement.setString(4, account.getLastName());
            statement.executeUpdate();
            System.out.println("Account created: " + account.getFirstName() + " " + account.getLastName() + ", Balance: " + account.getBalance());
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ExceptionAccount();
        }
    }
}
