package ErrorLogging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

/**
 * This class features writing error messages from exceptions in an error log file.
 * Uses a semaphore to ensure that only one thread at a time can write in the error log file.
 */
public class ErrorLogging {

    private static final String ERROR_LOG_FILE = "error.log";
    private static final Semaphore semaphore = new Semaphore(1);

    /**
     * Writes an error message in the error log file.
     *
     * @param message   Error message to be written.
     * @param OwnFormat If true, message will be written in the own format.
     *                  Else, message will be written in a given format featuring a timestamp.
     */
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