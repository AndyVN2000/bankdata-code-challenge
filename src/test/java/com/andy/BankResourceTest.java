package com.andy;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
/**
 * I will follow the principles of Test-Driven Development by writing tests first
 * for the account creation endpoint. This test will ensure that the endpoint behaves
 * as expected.
 */

@QuarkusTest
public class BankResourceTest {

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
            .body("{ \"accountNumber\": 41, \"amount\": 500.0 }") // Assuming a deposit endpoint accepts account number and amount
            .when().patch("/bank")
            .then()
                .statusCode(200) // Assuming 200 OK is the expected response for a successful deposit
                .body("balance", is(1500.0f)); // Check if the balance is updated correctly after deposit
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
     */
    @Test
    public void testTransferMoney() {

    }
}
