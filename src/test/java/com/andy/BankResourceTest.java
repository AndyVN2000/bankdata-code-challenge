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
    
}
