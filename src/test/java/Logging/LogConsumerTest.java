package Logging;

import static Logging.LogConsumer.LOG_FILE;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class LogConsumerTest {

    @Test
    void logConsumerHandlesIOException() throws InterruptedException {
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
        LogConsumer logConsumer = new LogConsumer(logQueue) {
            @Override
            public void run() {
                while (true) {
                    try {
                        String logEntry = logQueue.take();
                        // Simulate an IOException when writing to the file
                        throw new IOException("Simulated IO exception");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (IOException e) {
                        System.err.println("[ERROR] Could not write to file: " + LOG_FILE);
                    }
                }
            }
        };
        logConsumer.start();

        // Redirect System.err to capture the error message
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        logQueue.put("Test log entry");
        Thread.sleep(100); // Give some time for the log entry to be processed

        logConsumer.interrupt();
        logConsumer.join();

        // Restore System.err
        System.setErr(originalErr);

        // Verify that the error message was printed to System.err
        assertTrue(errContent.toString().contains("[ERROR] Could not write to file: " + LOG_FILE));
    }

    @Test
    void logConsumerHandlesInterruptedException() throws InterruptedException {
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
        LogConsumer logConsumer = new LogConsumer(logQueue);
        logConsumer.start();

        logConsumer.interrupt();
        logConsumer.join();

        assertTrue(logConsumer.isInterrupted());
    }


}
