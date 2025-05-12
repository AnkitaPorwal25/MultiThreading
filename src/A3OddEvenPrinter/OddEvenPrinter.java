package A3OddEvenPrinter;
public class OddEvenPrinter {
    private static final Object lock = new Object();  // Shared lock for synchronization
    private static int number = 1;  // Starting number for odd/even printing

    // Function for printing odd numbers
    public static void printOdd() {
        while (number <= 100) {
            synchronized (lock) {
                if (number % 2 != 0) {
                    System.out.print(number + " ");
                    number++;
                    lock.notify();  // Notify the even thread to print the next number
                }
                try {
                    if (number <= 100) {  // Wait if the number is even
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Function for printing even numbers
    public static void printEven() {
        while (number <= 100) {
            synchronized (lock) {
                if (number % 2 == 0) {
                    System.out.print(number + " ");
                    number++;
                    lock.notify();  // Notify the odd thread to print the next number
                }
                try {
                    if (number <= 100) {  // Wait if the number is odd
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Create and start threads
        Thread oddThread = new Thread(OddEvenPrinter::printOdd);
        Thread evenThread = new Thread(OddEvenPrinter::printEven);

        oddThread.start();
        evenThread.start();

        // Wait for both threads to finish
        try {
            oddThread.join();
            evenThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//
//
//Assignment 3: Odd-Even Printer
//Background:
//Create a coordinated printing system where two threads print odd and even numbers up to 100 in sequence.
//Requirements:
//Thread A prints odd numbers (1, 3, 5...)
//Thread B prints even numbers (2, 4, 6...)
//Output must be in order: 1 2 3 4 5 ... 100
//Use wait() and notify() for coordination