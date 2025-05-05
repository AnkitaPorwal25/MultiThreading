package A2MultiThreadedLoggingSystem;

import java.util.ArrayList;
import java.util.List;

public class BankSystem {
    private static List<BankAccount> accounts = new ArrayList<>();

    public static void main(String[] args) {
        // Adding some bank accounts to the list
        accounts.add(new BankAccount(101, "Alice", 1000));
        accounts.add(new BankAccount(102, "Bob", 500));
        accounts.add(new BankAccount(103, "Charlie", 1500));

        // Create and start deposit and withdraw threads for each account
        Thread[] depositThreads = new Thread[3];
        Thread[] withdrawThreads = new Thread[3];

        for (int i = 0; i < accounts.size(); i++) {
            BankAccount account = accounts.get(i);

            // Start deposit thread for each account
            depositThreads[i] = new Thread(() -> {
                account.deposit(200);
            });

            // Start withdrawal thread for each account
            withdrawThreads[i] = new Thread(() -> {
                account.withdraw(100);
            });

            depositThreads[i].start();
            withdrawThreads[i].start();
        }

        // Wait for all threads to complete
        try {
            for (Thread t : depositThreads) {
                t.join();
            }
            for (Thread t : withdrawThreads) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final balance for each account
        for (BankAccount account : accounts) {
            System.out.println("Final balance for Account " + account.getAccountNumber() + " (" + account.getUsername() + "): " + account.getBalance());
        }
    }
}