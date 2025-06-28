package com.andy;

/**
 * A class for encapsulating the info to deposit money.
 * @param accountNumber the account number to deposit money into
 * @param amount the amount of money to deposit
 */
public record DepositRequest(int accountNumber, double amount) {
    public double getAmount() {
        return amount;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}