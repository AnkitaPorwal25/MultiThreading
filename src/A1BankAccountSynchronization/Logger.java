package A1BankAccountSynchronization;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Logger {
    public enum LogLevel { INFO, WARN, ERROR }

    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Thread logFlusherThread;
    private final boolean writeToFile;
    private final PrintWriter writer;

    public Logger(boolean writeToFile) throws IOException {
        this.writeToFile = writeToFile;
        if (writeToFile) {
            writer = new PrintWriter(new FileWriter("logs.txt", true), true);
        } else {
            writer = null;
        }

        logFlusherThread = new Thread(this::flushPeriodically);
        logFlusherThread.start();
    }

    public void log(String message) {
        log(LogLevel.INFO, message);
    }

    public void log(LogLevel level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String fullMessage = String.format("[%s] [%s] %s", timestamp, level, message);
        logQueue.offer(fullMessage);
    }

    private void flushPeriodically() {
        while (running.get() || !logQueue.isEmpty()) {
            try {
                Thread.sleep(5000);
                flushLogs();
            } catch (InterruptedException ignored) {}
        }
        flushLogs(); // Final flush before exit
        if (writer != null) {
            writer.close();
        }
    }

    private void flushLogs() {
        String msg;
        while ((msg = logQueue.poll()) != null) {
            if (writeToFile) {
                writer.println(msg);
            } else {
                System.out.println(msg);
            }
        }
    }

    public void shutdown() {
        running.set(false);
        try {
            logFlusherThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // For stress simulation
    public static void main(String[] args) throws IOException {
        Logger logger = new Logger(false); // Change to true to log to file

        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    logger.log(LogLevel.INFO, "Thread-" + threadId + " Message-" + j);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.shutdown();
    }
}


//
//Assignment 1: Multi-threaded Logging System
//Background:
//You're building a high-throughput logging system for a large-scale application. Logs are generated from various parts of the system and should be stored in the order received, without blocking the producers.
//Requirements:
//Part 1: Basic Logging System
//Implement a class Logger with the following features:
//Method: void log(String message)
//Accepts log messages from multiple threads concurrently.
//Stores the messages in the order they were received.
//Uses a separate thread to flush logs every 5 seconds to the console (simulate disk).
//Part 2: Graceful Shutdown
//Provide a method void shutdown() that:
//Ensures all pending logs are flushed.
//Terminates the logging thread cleanly.
//        Part 3: Stress Simulation
//Simulate 100 threads each logging 100 messages.
//Ensure that the messages do not get lost or printed out of order.
//Bonus Requirements:
//Support log levels (INFO, WARN, ERROR)
//Add timestamps to log entries
//Provide optional file-based flushing instead of console (via configuration)