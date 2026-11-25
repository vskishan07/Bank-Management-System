package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import bank.model.Account;
import bank.model.CurrentAccount;
import bank.model.SavingsAccount;
import bank.model.Transaction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BankService {
    private static final Path STORAGE_PATH = Paths.get("data", "bank_data.ser");
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public BankService() {
        loadFromDisk();
    }

    public Account createSavingsAccount(String accNo, String holderName, double initialDeposit)
            throws InvalidAmountException {
        validateUniqueAccountNumber(accNo);
        Account account = new SavingsAccount(accNo, holderName, initialDeposit);
        accounts.put(account.getAccountNumber(), account);
        saveToDisk();
        return account;
    }

    public Account createCurrentAccount(String accNo, String holderName, double initialDeposit)
            throws InvalidAmountException {
        validateUniqueAccountNumber(accNo);
        Account account = new CurrentAccount(accNo, holderName, initialDeposit);
        accounts.put(account.getAccountNumber(), account);
        saveToDisk();
        return account;
    }
    public Account deleteAccount(String accNo) throws AccountNotFoundException {
        Account removed = getAccount(accNo);
        accounts.remove(removed.getAccountNumber());
        saveToDisk();
        return removed;
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
        saveToDisk();
    }

    public void withdraw(String accNo, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        Account account = getAccount(accNo);
        account.withdraw(amount);
        saveToDisk();
    }

    public void transfer(String fromAccNo, String toAccNo, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        if (fromAccNo != null && toAccNo != null && fromAccNo.trim().equalsIgnoreCase(toAccNo.trim())) {
            throw new InvalidAmountException("Source and destination accounts must be different.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero.");
        }

        Account sender = getAccount(fromAccNo);
        Account receiver = getAccount(toAccNo);

        sender.withdraw(amount);
        receiver.deposit(amount);
        saveToDisk();
    }

    public void applyAnnualInterestToAll() {
        for (Account account : accounts.values()) {
            account.applyInterest();
        }
        saveToDisk();
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Transaction> getStatement(String accNo) throws AccountNotFoundException {
        return getAccount(accNo).getTransactions();
    }

    private void saveToDisk() {
        try {
            if (STORAGE_PATH.getParent() != null) {
                Files.createDirectories(STORAGE_PATH.getParent());
            }
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(STORAGE_PATH))) {
                out.writeObject(accounts);
            }
        } catch (IOException ex) {
            System.out.println("Warning: Could not save bank data: " + ex.getMessage());
        }
    }

    
    private void loadFromDisk() {
        if (!Files.exists(STORAGE_PATH)) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(STORAGE_PATH))) {
            Object loaded = in.readObject();
            if (loaded instanceof Map<?, ?> map) {
                accounts.clear();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof Account acc) {
                        accounts.put(key, acc);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Warning: Could not load saved bank data: " + ex.getMessage());
        }
    }
}