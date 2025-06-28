package com.andy;

/**
 * https://fullstackdeveloper.guru/2024/02/06/how-to-connect-to-database-from-a-rest-api/
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EntityAccount {

    @Id
    private int accountNumber;
    private Double balance;
    private String firstName;
    private String lastName;

    public EntityAccount() {
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
