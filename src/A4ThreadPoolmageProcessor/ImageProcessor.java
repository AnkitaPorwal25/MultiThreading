package A4ThreadPoolmageProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ImageProcessor {

    public static void main(String[] args) {
        int numImages = 50;
        int threadPoolSize = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        List<Future<String>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();  // Start timing

        // Submit 50 image processing tasks
        for (int i = 1; i <= numImages; i++) {
            final int imageId = i;
            Callable<String> task = () -> {
                try {
                    Thread.sleep(100); // Simulate image processing delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                }
                return "Image " + imageId + " processed";
            };
            futures.add(executor.submit(task));
        }

        // Retrieve and print results in order
        for (Future<String> future : futures) {
            try {
                System.out.println(future.get());  // Blocks until the result is available
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error retrieving result: " + e.getMessage());
            }
        }

        executor.shutdown();  // Gracefully shut down the executor

        long endTime = System.currentTimeMillis();  // End timing
        System.out.println("Total execution time: " + (endTime - startTime) + " ms");
    }
}