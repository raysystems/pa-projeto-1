package Logging;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

public class LogConsumerTest {


    @org.junit.jupiter.api.Test
    void logConsumerWritesLogToFile() throws InterruptedException {
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
        LogConsumer logConsumer = new LogConsumer(logQueue);
        logConsumer.start();

        logQueue.put("Test log entry");
        Thread.sleep(100); // Give some time for the log entry to be written

        logConsumer.interrupt();
        logConsumer.join();

        try (BufferedReader reader = new BufferedReader(new FileReader("server.log"))) {
            assertEquals("Test log entry", reader.readLine());
        } catch (IOException e) {
            fail("Could not read from log file");
        } finally {
            //Clear the log file to avoid conflicts with other tests
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("server.log"))) {
                writer.write("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @org.junit.jupiter.api.Test
    void logConsumerHandlesInterruptedException() throws InterruptedException {
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
        LogConsumer logConsumer = new LogConsumer(logQueue);
        logConsumer.start();

        logConsumer.interrupt();
        logConsumer.join();

        assertTrue(logConsumer.isInterrupted());
    }

    @org.junit.jupiter.api.Test
    void logConsumerHandlesIOException() throws InterruptedException {
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
        LogConsumer logConsumer = new LogConsumer(logQueue) {
            @Override
            public void run() {
                while (true) {
                    try {
                        String logEntry = logQueue.take();
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

        logQueue.put("Test log entry");
        Thread.sleep(100); // Give some time for the log entry to be processed

        logConsumer.interrupt();
        logConsumer.join();

        // Check that the log file is empty
        try (BufferedReader reader = new BufferedReader(new FileReader("server.log"))) {
            assertNull(reader.readLine());
        } catch (IOException e) {
            fail("Could not read from log file");
        }
    }

}
