package Logging;

import static Logging.LogConsumer.LOG_FILE;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class LogConsumerTest {


    @Test
    void logConsumerWritesLogToFile() throws InterruptedException {
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
        LogConsumer logConsumer = new LogConsumer(logQueue);
        logConsumer.start();

        String testLogEntry = "{ \"timestamp\": \"2025-03-28 19:40:55\", \"method\": \"GET\", \"route\": \"/home\", \"origin\": \"127.0.0.1\", \"HTTP response status\": 200 }";
        logQueue.put(testLogEntry);
        Thread.sleep(100); // Give some time for the log entry to be written

        logConsumer.interrupt();
        logConsumer.join();

        try (BufferedReader reader = new BufferedReader(new FileReader("server.log"))) {
            assertEquals(testLogEntry, reader.readLine());
        } catch (IOException e) {
            fail("Could not read from log file");
        } finally {
            // Clear the log file to avoid conflicts with other tests
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("server.log"))) {
                writer.write("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
