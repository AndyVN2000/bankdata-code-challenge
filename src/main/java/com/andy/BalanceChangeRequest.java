package com.andy;

/**
 * A class for encapsulating the info change balance of an acocunt.
 * @param accountNumber the account number to which the balance change applies
 * @param amount the amount of money to change by. Positive for deposit, negative for withdrawal.
 */
public record BalanceChangeRequest(int accountNumber, double amount) {
}