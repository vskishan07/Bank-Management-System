package bank.model;

import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;

public class CurrentAccount extends Account {
    public static final double OVERDRAFT_LIMIT = 50000.0;

    public CurrentAccount(String accountNumber, String holderName, double initialDeposit)
            throws InvalidAmountException {
        super(accountNumber, holderName, initialDeposit);
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        double maxAllowedWithdrawal = balance + OVERDRAFT_LIMIT;
        if (amount > maxAllowedWithdrawal) {
            throw new InsufficientFundsException(
                    "Exceeds balance + overdraft limit (max available: ₹" + maxAllowedWithdrawal + ")."
            );
        }
        balance -= amount;
        String note = balance < 0 ? "Current withdrawal (overdraft used)" : "Current withdrawal";
        recordTransaction(Transaction.Type.WITHDRAWAL, amount, note);
    }

    @Override
    public double calculateAnnualInterest() {
        return 0.0; // Current accounts typically earn no interest
    }
}