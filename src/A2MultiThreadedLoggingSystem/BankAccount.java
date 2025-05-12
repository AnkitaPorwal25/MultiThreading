package A2MultiThreadedLoggingSystem;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BankAccount {
    private final int accountNumber;  // Final account number
    private String username;  // Username
    private double balance;  // Balance
    private final Lock lock;  // Lock to ensure thread-safety

    // Logger to track the transactions
    private static final Logger logger = Logger.getLogger(BankAccount.class.getName());

    // Static block to configure logger to write to a file
    static {
        try {
            FileHandler fileHandler = new FileHandler("src/A2MultiThreadedLoggingSystem/bank_transactions.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BankAccount(int accountNumber, String username, double initialBalance) {
        this.accountNumber = accountNumber;
        this.username = username;
        this.balance = initialBalance;
        this.lock = new ReentrantLock();
    }

    // Deposit method with transaction logging
    public void deposit(double amount) {
        lock.lock();
        try {
            balance += amount;
            logger.info("Deposited " + amount + " to account " + accountNumber + " (" + username + "), new balance: " + balance);
        } finally {
            lock.unlock();
        }
    }

    // Withdraw method with transaction logging
    public void withdraw(double amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                logger.info("Withdrew " + amount + " from account " + accountNumber + " (" + username + "), new balance: " + balance);
            } else {
                logger.warning("Attempt to withdraw " + amount + " failed from account " + accountNumber + " (" + username + "), insufficient funds.");
            }
        } finally {
            lock.unlock();
        }
    }

    public double getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getUsername() {
        return username;
    }
}