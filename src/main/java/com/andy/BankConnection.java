package com.andy;

/**
 * Inspiration from https://www.javaguides.net/2019/08/java-h2-database-tutorial.html
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankConnection {

    private static final String URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";
    
}
