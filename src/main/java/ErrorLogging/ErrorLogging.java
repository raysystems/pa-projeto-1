package ErrorLogging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogging {

    private static final String ERROR_LOG_FILE = "error.log";
    private static final Semaphore semaphore = new Semaphore(1);

    public static void logError(String message, Boolean OwnFormat) {
        try {
            semaphore.acquire();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ERROR_LOG_FILE, true))) {
                if (OwnFormat) {
                    writer.write(message);
                } else {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-dd-MM / HH:mm:ss"));
                    writer.write("[ERROR - " + timestamp +  " ] - " + message);
                }
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.err.println("[ERROR] Could not write to file: " + ERROR_LOG_FILE);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("[ERROR] ErrorLogging interrupted while writing to error log.");
        } finally {
            semaphore.release();
        }
    }
}