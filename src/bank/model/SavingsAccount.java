package bank.model;

import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;

public class SavingsAccount extends Account {
    public static final double MIN_BALANCE = 1000.0;
    public static final double ANNUAL_INTEREST_RATE = 0.04; // 4% p.a.

    public SavingsAccount(String accountNumber, String holderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, holderName, initialDeposit);
        if (initialDeposit < MIN_BALANCE) {
            throw new InvalidAmountException(
                    "Savings account requires an initial deposit of at least ₹" + MIN_BALANCE + "."
            );
        }
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        if (balance - amount < MIN_BALANCE) {
            throw new InsufficientFundsException(
                    "Withdrawal denied. Minimum balance of ₹" + MIN_BALANCE + " must be maintained."
            );
        }
        balance -= amount;
        recordTransaction(Transaction.Type.WITHDRAWAL, amount, "Savings withdrawal");
    }

    @Override
    public double calculateAnnualInterest() {
        return balance * ANNUAL_INTEREST_RATE;
    }
}