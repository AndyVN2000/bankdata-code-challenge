package com.andy;

/**
 * A class for encapsulating the info to transfer money between accounts.
 * @param fromAccount the account number to transfer money from
 * @param toAccount the account number to transfer money to
 * @param amount the amount of money to transfer
 */
public record TransferRequest(int fromAccount, int toAccount, double amount) {
    public int getFromAccount() {
        return fromAccount;
    }

    public int getToAccount() {
        return toAccount;
    }

    public double getAmount() {
        return amount;
    }

}
