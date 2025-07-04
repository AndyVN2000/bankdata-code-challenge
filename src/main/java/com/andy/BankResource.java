package com.andy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/bank")
public class BankResource {

    private AccountService as;

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    public BankResource(AccountService as){
        as = as;
    }

    /**
     * Based on https://www.javaguides.net/2019/08/java-h2-database-tutorial.html that shows how to insert record.
     * @param account
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(EntityAccount account) {
        try {
            EntityAccount ea = as.createAccount(account);
        }
        catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong during account creation").build();
        } 
    }

    @GET
    @Path("/{accountNumber}")
    public Response getAccount(@PathParam("accountNumber") int accountNumber) {
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

    @GET
    @Path("/{accountNumber}/balance")
    public Response getAccountBalance(@PathParam("accountNumber") int accountNumber) {
        Object account = getAccount(accountNumber).getEntity();
        if (account instanceof EntityAccount entityAccount) {
            Map<String, Object> json = new HashMap<>();
            json.put("balance", entityAccount.getBalance());
            return Response.ok(json).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * There were other possible REST methods such as PUT or POST, but I 
     * think that PATCH was more semantically fitting, given that we partially
     * update the account balance.
     * @param request
     * @return
     */
    @PATCH
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response depositMoney(BalanceChangeRequest request) {
        String updateSQL = "UPDATE EntityAccount SET balance = balance + ? WHERE accountNumber = ?";
        String selectSQL = "SELECT * FROM EntityAccount WHERE accountNumber = ?";
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                PreparedStatement updateStmt = connection.prepareStatement(updateSQL);
                PreparedStatement selectStmt = connection.prepareStatement(selectSQL)) {

            updateStmt.setDouble(1, request.amount());
            updateStmt.setInt(2, request.accountNumber());
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                // Fetch updated account
                selectStmt.setInt(1, request.accountNumber());
                var resultSet = selectStmt.executeQuery();
                if (resultSet.next()) {
                    EntityAccount account = new EntityAccount();
                    account.setAccountNumber(resultSet.getInt("accountNumber"));
                    account.setBalance(resultSet.getDouble("balance"));
                    account.setFirstName(resultSet.getString("firstName"));
                    account.setLastName(resultSet.getString("lastName"));
                    System.out.println("Updated account: " + account.getFirstName() + " " + account.getLastName() + ", New Balance: " + account.getBalance());
                    return Response.ok(account).build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PATCH
    @Path("/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdrawMoney(BalanceChangeRequest request) {
        // Reuse depositMoney method with negative amount
        BalanceChangeRequest newRequest = new BalanceChangeRequest(request.accountNumber(), -request.amount());
        return depositMoney(newRequest);
    }

    @PATCH
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferRequest request) {
        int fromAccount = request.getFromAccount();
        int toAccount = request.getToAccount();
        double amount = request.getAmount();
        // Withdraw from source account
        BalanceChangeRequest withdrawRequest = new BalanceChangeRequest(fromAccount, amount);
        Response withdrawResponse = withdrawMoney(withdrawRequest);
        if (withdrawResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return withdrawResponse; // Return error if withdrawal failed
        }

        // Deposit into destination account
        BalanceChangeRequest depositRequest = new BalanceChangeRequest(toAccount, request.getAmount());
        return depositMoney(depositRequest);
    }
}