package com.andy;

/**
 * https://fullstackdeveloper.guru/2024/02/06/how-to-connect-to-database-from-a-rest-api/
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

@Entity
public class EntityAccount {

    @Id
    @GeneratedValue
    private int accountNumber;
    private Double balance;
    private String firstName;
    private String lastName;

    public EntityAccount() {
    }

    public EntityAccount(int accountNumber, Double balance, String firstName, String lastName) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
}
