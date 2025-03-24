package Logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class LogProducer {

    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        // Start the log consumer thread
        LogConsumer logConsumer = new LogConsumer(logQueue);
        logConsumer.start();
    }

    public static void logRequest(String method, String route, String origin, int statusCode) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format(
                "{ \"timestamp\": \"%s\", \"method\": \"%s\", \"route\": \"%s\", \"origin\": \"%s\", \"HTTP response status\": %d }",
                timestamp, method, route, origin, statusCode
        );
        System.out.println(logEntry); // Print to console for immediate feedback
        try {
            logQueue.put(logEntry); // Block if the queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("[ERROR] LogProducer interrupted while putting log entry in queue.");
        }
    }
}
