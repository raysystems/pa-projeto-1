package Logging;

import ErrorLogging.ErrorLogging;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * This class is responsible for the behaviour of consumer threads.
 * Consumer threads write log messages in a server log file from a buffer.
 */
public class LogConsumer extends Thread {

    private final BlockingQueue<String> logQueue;
    protected static final String LOG_FILE = "server.log";

    /**
     * Initializes the log message buffer.
     *
     * @param logQueue Buffer containing log messages to be written.
     */
    public LogConsumer(BlockingQueue<String> logQueue) {
        this.logQueue = logQueue;
    }

    /**
     * Consumes log messages from the buffer and writes them in a server log file.
     */
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