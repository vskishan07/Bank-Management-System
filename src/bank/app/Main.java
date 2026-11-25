package bank.app;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import bank.model.Account;
import bank.model.Transaction;
import bank.service.BankService;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final BankService bankService = new BankService();

    private static class EndOfInputException extends Exception {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            try {
                int choice = readInt(scanner, "Enter your choice: ");
                switch (choice) {
                    case 1 -> handleCreateAccount(scanner);
                    case 2 -> handleDeposit(scanner);
                    case 3 -> handleWithdraw(scanner);
                    case 4 -> handleTransfer(scanner);
                    case 5 -> handleBalanceAndStatement(scanner);
                    case 6 -> handleListAccounts();
                    case 7 -> {
                        bankService.applyAnnualInterestToAll();
                        System.out.println("Annual interest applied to eligible accounts.");
                    }
                    case 8 -> {
                        running = false;
                        System.out.println("Exiting Bank Management System. Goodbye!");
                    }
                    default -> System.out.println("Invalid choice. Please select between 1 and 8.");
                }
            } catch (EndOfInputException ex) {
                System.out.println("\nInput stream ended. Exiting Bank Management System.");
                running = false;
            } catch (InvalidAmountException | InsufficientFundsException
                     | AccountNotFoundException | IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== BANK MANAGEMENT SYSTEM ===");
        System.out.println("1. Create Account (Savings / Current)");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Check Balance & Mini Statement");
        System.out.println("6. View All Accounts");
        System.out.println("7. Apply Annual Interest");
        System.out.println("8. Exit");
    }

    private static void handleCreateAccount(Scanner scanner)
            throws InvalidAmountException, EndOfInputException {
        System.out.println("Select Account Type: 1 = Savings, 2 = Current");
        int type = readInt(scanner, "Type: ");
        String accNo = readLineOrThrow(scanner, "Enter Account Number: ");
        String name = readLineOrThrow(scanner, "Enter Account Holder Name: ");
        double deposit = readDouble(scanner, "Enter Initial Deposit (₹): ");

        Account created = switch (type) {
            case 1 -> bankService.createSavingsAccount(accNo, name, deposit);
            case 2 -> bankService.createCurrentAccount(accNo, name, deposit);
            default -> throw new IllegalArgumentException("Invalid account type selected.");
        };
        System.out.println("Account created successfully: " + created);
    }

    private static void handleDeposit(Scanner scanner)
            throws AccountNotFoundException, InvalidAmountException, EndOfInputException {
        String accNo = readLineOrThrow(scanner, "Enter Account Number: ");
        double amount = readDouble(scanner, "Enter Deposit Amount (₹): ");
        bankService.deposit(accNo, amount);
        System.out.println("Deposit successful.");
    }

    private static void handleWithdraw(Scanner scanner)
            throws AccountNotFoundException, InvalidAmountException,
                   InsufficientFundsException, EndOfInputException {
        String accNo = readLineOrThrow(scanner, "Enter Account Number: ");
        double amount = readDouble(scanner, "Enter Withdrawal Amount (₹): ");
        bankService.withdraw(accNo, amount);
        System.out.println("Withdrawal successful.");
    }

    private static void handleTransfer(Scanner scanner)
            throws AccountNotFoundException, InvalidAmountException,
                   InsufficientFundsException, EndOfInputException {
        String fromAcc = readLineOrThrow(scanner, "Enter Source Account Number: ");
        String toAcc = readLineOrThrow(scanner, "Enter Destination Account Number: ");
        double amount = readDouble(scanner, "Enter Transfer Amount (₹): ");
        bankService.transfer(fromAcc, toAcc, amount);
        System.out.println("Transfer successful.");
    }

    private static void handleBalanceAndStatement(Scanner scanner)
            throws AccountNotFoundException, EndOfInputException {
        String accNo = readLineOrThrow(scanner, "Enter Account Number: ");
        Account account = bankService.getAccount(accNo);
        System.out.println(account);
        List<Transaction> txns = bankService.getStatement(accNo);
        if (txns.isEmpty()) {
            System.out.println("No transactions recorded yet.");
        } else {
            System.out.println("--- Transactions ---");
            for (Transaction txn : txns) {
                System.out.println(txn);
            }
        }
    }

    private static void handleListAccounts() {
        List<Account> all = bankService.getAllAccounts();
        if (all.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("--- All Accounts ---");
        for (Account acc : all) {
            System.out.println(acc);
        }
    }

    private static String readLineOrThrow(Scanner scanner, String prompt) throws EndOfInputException {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new EndOfInputException();
        }
        return scanner.nextLine().trim();
    }

    private static int readInt(Scanner scanner, String prompt) throws EndOfInputException {
        while (true) {
            String input = readLineOrThrow(scanner, prompt);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt) throws EndOfInputException {
        while (true) {
            String input = readLineOrThrow(scanner, prompt);
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid numeric amount.");
            }
        }
    }
}