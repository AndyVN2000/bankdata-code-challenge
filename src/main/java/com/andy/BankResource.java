package com.andy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/bank")
public class BankResource {

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    /**
     * Based on https://www.javaguides.net/2019/08/java-h2-database-tutorial.html that shows how to insert record.
     * @param account
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(EntityAccount account) {
        String insertSQL = "INSERT INTO EntityAccount (accountNumber, balance, firstName, lastName) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setInt(1, account.getAccountNumber());
            statement.setDouble(2, account.getBalance());
            statement.setString(3, account.getFirstName());
            statement.setString(4, account.getLastName());
            statement.executeUpdate();
            System.out.println("Account created: " + account.getFirstName() + " " + account.getLastName() + ", Balance: " + account.getBalance());
            return Response.status(Response.Status.CREATED).entity(account).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{accountNumber}")
    public Response getAccount(int accountNumber) {
        String selectSQL = "SELECT * FROM EntityAccount WHERE accountNumber = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setInt(1, accountNumber);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                EntityAccount account = new EntityAccount();
                account.setAccountNumber(resultSet.getInt("accountNumber"));
                account.setBalance(resultSet.getDouble("balance"));
                account.setFirstName(resultSet.getString("firstName"));
                account.setLastName(resultSet.getString("lastName"));
                return Response.ok(account).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response depositMoney(EntityAccount account) {
        String updateSQL = "UPDATE EntityAccount SET balance = balance + ? WHERE accountNumber = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            statement.setDouble(1, account.getBalance());
            statement.setInt(2, account.getAccountNumber());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Deposit successful for account number: " + account.getAccountNumber());
                return Response.ok(account).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}