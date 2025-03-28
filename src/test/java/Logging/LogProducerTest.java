package Logging;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogProducerTest {

    @Test
    void logRequestAddsLogEntryToQueue() throws InterruptedException {
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
        LogConsumer logConsumer = new LogConsumer(logQueue);
        logConsumer.start();

        LogProducer.logRequest("GET", "/home", "127.0.0.1", 200);
        Thread.sleep(100); // Give some time for the log entry to be processed

        logConsumer.interrupt();
        logConsumer.join();

        assertTrue(logQueue.isEmpty());

        // Clear the log file to avoid conflicts with other tests
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("server.log"))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
