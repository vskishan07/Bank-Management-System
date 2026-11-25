package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, INTEREST
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String transactionId;
    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String timestamp;
    private final String note;

    public Transaction(String transactionId, Type type, double amount, double balanceAfter, String note) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
        this.note = note;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] %-12s | Amt: ₹%.2f | Bal: ₹%.2f | %s (%s)",
                timestamp, type, amount, balanceAfter, note, transactionId
        );
    }
}