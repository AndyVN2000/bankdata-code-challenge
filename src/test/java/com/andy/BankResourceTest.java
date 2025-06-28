package com.andy;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
/**
 * I will follow the principles of Test-Driven Development by writing tests first
 * for the account creation endpoint. This test will ensure that the endpoint behaves
 * as expected.
 * One big issue in my attempt to perform TDD is the fact that test cases are
 * not independent from each other. The database is not reset between tests.
 * For now, I have to accept this, but I truly wish I knew a way to reset the database
 * for each test.
 * If I knew how to access the database directly, I could drop the table
 * and recreate it before each test. But I do not know how to do that.
 */

@QuarkusTest
public class BankResourceTest {

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    @BeforeEach
    public void resetDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM EntityAccount");
            stmt.executeUpdate("INSERT INTO EntityAccount " +
                "(accountNumber, balance, firstName, lastName)" +
                " VALUES (82, 2000, 'Jane', 'Doe')");
        }
    }

    /**
     * So this test will be testing on the POST method. I know that this method is not idempotent,
     * meaning that I will not get the same outcome if I call it multiple times.
     * But given my time constraints, I just want to explore how REST and H2 works for now.
     */
    @Test
    public void testCreateAccount() {
        String newAccount = """
                {
                    "accountNumber": 41,
                    "balance": 1000.0,
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(newAccount)
            .when().post("/bank")
            .then()
                .statusCode(201) // Assuming 201 Created is the expected response for a successful POST
                .body("firstName", is("John"))
                .body("lastName", is("Doe"))
                .body("balance", is(1000.0f)); // Using float to match the JSON representation
    }

    @Test
    public void testDepositMoney() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"accountNumber\": 82, \"amount\": 500.0 }") // Assuming a deposit endpoint accepts account number and amount
            .when().patch("/bank")
            .then()
                .statusCode(200) // Assuming 200 OK is the expected response for a successful deposit
                .body("balance", is(2500.0f)); // Check if the balance is updated correctly after deposit
    }
    
    /**
     * My initial thoughts before writing test and implementation:
     * This looks like a good opportunity to reuse the depositMoney method.
     * I wonder if it is possible to define a transferMoney method that reuses
     * the depositMoney method.
     * First, define a withdrawMoney method. It could just call the depositMoney method
     * with a negative amount.
     * Then transferMoney could call withdrawMoney and depositMoney.
     * TODO: I must assign different paths to the depositMoney, withdrawMoney methods and transferMoney.
     * TODO: Another problem is how I correctly write my test case for this. I have observed
     *  that the database is not reset between tests, so I can reuse the John Doe account.
     */
    @Disabled
    @Test
    public void testTransferMoney() {
        String newAccount = """
                {
                    "accountNumber": 82,
                    "balance": 200.0,
                    "firstName": "Mary",
                    "lastName": "Sue"
                }
                """;
        given()
            .contentType(ContentType.JSON)
            .body(newAccount)
            .when().post("/bank")
            .then()
                .statusCode(201)
                .body("firstName", is("Mary"))
                .body("lastName", is("Sue"))
                .body("balance", is(200.0f));
        
        given()
            .contentType(ContentType.JSON)
            .body("{ \"fromAccount\": 41, \"toAccount\": 82, \"amount\": 300.0 }")
            .when().patch("/bank/transfer")
            .then()
                .statusCode(200) // Assuming 200 OK is the expected response for a successful transfer
                .body("fromAccount.balance", is(1200.0f)) // Check if the balance of the sender account is updated correctly after transfer
                .body("toAccount.balance", is(500.0f)); // Check if the balance of the receiver account is updated correctly after transfer
    }
}
