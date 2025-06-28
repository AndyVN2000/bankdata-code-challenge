package com.andy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;


@Path("/bank")
public class BankResource {

    private static final String JDBC_URL = "jdbc:h2:file:./data/mydb";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    /**
     * Based on https://www.javaguides.net/2019/08/java-h2-database-tutorial.html that shows how to insert record.
     * @param account
     */
    @POST
    public void createAccount(EntityAccount account) {
        // Here you would typically save the account to the database
        String insertSQL = "INSERT INTO bank_accounts (balance, first_name, last_name) VALUES (" +
                           account.getBalance() + ", '" +
                           account.getFirstName() + "', '" +
                           account.getLastName() + "')";
        
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.executeUpdate(insertSQL);
            System.out.println("Account created: " + account.getFirstName() + " " + account.getLastName() + ", Balance: " + account.getBalance());
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception, e.g., return an error response
        }
    }
    
}
