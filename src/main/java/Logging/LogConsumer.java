package Logging;

import ErrorLogging.ErrorLogging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class LogConsumer extends Thread {
    private final BlockingQueue<String> logQueue;
    private static final String LOG_FILE = "server.log";

    public LogConsumer(BlockingQueue<String> logQueue) {
        this.logQueue = logQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Take a log message from the queue (block if it is empty)
                String logEntry = logQueue.take();

                // Write the log message to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                    writer.write(logEntry);
                    writer.newLine(); // Add a new line after each log entry
                    writer.flush(); // Flush the buffer to ensure the message is written to the file
                } catch (IOException e) {
                    Thread thread = new Thread(() -> ErrorLogging.logError("Could not write to file: " + LOG_FILE, false));
                    thread.start();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Exit the loop if interrupted
            }
        }

    }
}