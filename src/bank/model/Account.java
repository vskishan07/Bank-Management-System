package bank.model;

import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class Account {
    private final String accountNumber;
    private final String holderName;
    protected double balance;
    protected final List<Transaction> transactions = new ArrayList<>();

    protected Account(String accountNumber, String holderName, double initialDeposit)
            throws InvalidAmountException {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be blank.");
        }
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Account holder name cannot be blank.");
        }
        if (!Double.isFinite(initialDeposit) || initialDeposit < 0) {
            throw new InvalidAmountException("Initial deposit must be a finite, non-negative amount.");
        }
        this.accountNumber = accountNumber.trim();
        this.holderName = holderName.trim();
        this.balance = initialDeposit;
        if (initialDeposit > 0) {
            recordTransaction(Transaction.Type.DEPOSIT, initialDeposit, "Opening balance");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    protected void recordTransaction(Transaction.Type type, double amount, String note) {
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        transactions.add(new Transaction(txnId, type, amount, balance, note));
    }

    public void deposit(double amount) throws InvalidAmountException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be a finite amount greater than zero.");
        }
        if (!Double.isFinite(balance + amount)) {
            throw new InvalidAmountException("Deposit would exceed the supported balance range.");
        }
        balance += amount;
        recordTransaction(Transaction.Type.DEPOSIT, amount, "Cash/UPI deposit");
    }

    public void transferOut(double amount)
            throws InvalidAmountException, InsufficientFundsException {
        withdraw(amount);
        transactions.remove(transactions.size() - 1);
        recordTransaction(Transaction.Type.TRANSFER_OUT, amount, "Transfer to another account");
    }

    public void transferIn(double amount) throws InvalidAmountException {
        deposit(amount);
        transactions.remove(transactions.size() - 1);
        recordTransaction(Transaction.Type.TRANSFER_IN, amount, "Transfer from another account");
    }

    public void applyInterest() {
        double interest = calculateAnnualInterest();
        if (!Double.isFinite(interest) || !Double.isFinite(balance + interest)) {
            throw new IllegalArgumentException("Interest calculation produced an invalid balance.");
        }
        if (interest > 0) {
            balance += interest;
            recordTransaction(Transaction.Type.INTEREST, interest, "Annual interest credit");
        }
    }

    public abstract String getAccountType();

    public abstract void withdraw(double amount)
            throws InvalidAmountException, InsufficientFundsException;

    public abstract double calculateAnnualInterest();

    @Override
    public String toString() {
        return String.format(
                "%-10s | %-10s | Holder: %-18s | Balance: ₹%.2f",
                accountNumber, getAccountType(), holderName, balance
        );
    }
}