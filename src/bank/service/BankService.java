package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import bank.model.Account;
import bank.model.CurrentAccount;
import bank.model.SavingsAccount;
import bank.model.Transaction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BankService {
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public Account createSavingsAccount(String accNo, String holderName, double initialDeposit)
            throws InvalidAmountException {
        validateUniqueAccountNumber(accNo);
        Account account = new SavingsAccount(accNo, holderName, initialDeposit);
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public Account createCurrentAccount(String accNo, String holderName, double initialDeposit)
            throws InvalidAmountException {
        validateUniqueAccountNumber(accNo);
        Account account = new CurrentAccount(accNo, holderName, initialDeposit);
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    private void validateUniqueAccountNumber(String accNo) {
        if (accNo != null && accounts.containsKey(accNo.trim())) {
            throw new IllegalArgumentException("Account number already exists: " + accNo.trim());
        }
    }

    public Account getAccount(String accNo) throws AccountNotFoundException {
        if (accNo == null) {
            throw new AccountNotFoundException("Account number cannot be null.");
        }
        Account account = accounts.get(accNo.trim());
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accNo.trim());
        }
        return account;
    }

    public void deposit(String accNo, double amount)
            throws AccountNotFoundException, InvalidAmountException {
        Account account = getAccount(accNo);
        account.deposit(amount);
    }

    public void withdraw(String accNo, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        Account account = getAccount(accNo);
        account.withdraw(amount);
    }

    public void transfer(String fromAccNo, String toAccNo, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be a finite amount greater than zero.");
        }
        if (fromAccNo != null && toAccNo != null && fromAccNo.trim().equalsIgnoreCase(toAccNo.trim())) {
            throw new InvalidAmountException("Source and destination accounts must be different.");
        }

        Account sender = getAccount(fromAccNo);
        Account receiver = getAccount(toAccNo);

        if (!Double.isFinite(receiver.getBalance() + amount)) {
            throw new InvalidAmountException("Transfer would exceed the destination account's supported balance range.");
        }

        sender.transferOut(amount);
        receiver.transferIn(amount);
    }

    public void applyAnnualInterestToAll() {
        for (Account account : accounts.values()) {
            account.applyInterest();
        }
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Transaction> getStatement(String accNo) throws AccountNotFoundException {
        return getAccount(accNo).getTransactions();
    }
}