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
            stmt.executeUpdate("INSERT INTO EntityAccount " +
                "(accountNumber, balance, firstName, lastName)" +
                " VALUES (777, 700, 'Mary', 'Sue')");
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
                .statusCode(201)
                .body("firstName", is("John"))
                .body("lastName", is("Doe"))
                .body("balance", is(1000.0f));
    }

    @Test
    public void testDepositMoney() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"accountNumber\": 82, \"amount\": 500.0 }")
            .when().patch("/bank/deposit")
            .then()
                .statusCode(200)
                .body("balance", is(2500.0f));
    }
    
    /**
     * My initial thoughts before writing test and implementation:
     * This looks like a good opportunity to reuse the depositMoney method.
     * I wonder if it is possible to define a transferMoney method that reuses
     * the depositMoney method.
     * First, define a withdrawMoney method. It could just call the depositMoney method
     * with a negative amount.
     * Then transferMoney could call withdrawMoney and depositMoney.
     */

    public void testWithdrawMoney() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"accountNumber\": 82, \"amount\": 300.0 }")
            .when().patch("/bank/withdraw")
            .then()
                .statusCode(200)
                .body("balance", is(1700.0f));
    }

    @Test
    public void testTransferMoney() {        
        given()
            .contentType(ContentType.JSON)
            .body("{ \"fromAccount\": 82, \"toAccount\": 777, \"amount\": 300.0 }")
            .when().patch("/bank/transfer")
            .then()
                .statusCode(200);
        
        given()
            .contentType(ContentType.JSON)
            .body("{ \"accountNumber\": 82 }")
            .when().get("/bank/82")
            .then()
                .body("balance", is(1700.0f));
        
        given()
            .contentType(ContentType.JSON)
            .body("{ \"accountNumber\": 777 }")
            .when().get("/bank/777")
            .then()
                .body("balance", is(1000.0f));
    }

    /**
     * I have a new dilemma, now that my next step is to implement the operation
     * of getting the balance of an account.
     * I have two options: 
     * - Reuse my existing GET method that retrieves the account,
     *   which was created as a side-effect of me wanting to implement the operation
     *   of depositing money.
     * - Create a new endpoint for getting the balance of an account.
     * My personal preference is to reuse the existing GET method.
     * This is because I was taught the principle of reusability.
     * So my idea is to create a new endpoint that calls getAccount and then extracts
     * the balance from the returned account object, which will be returned.
     */
}
