package Logging;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;


public class LogProducerTest {

    @Test
    void logRequestAddsLogEntryToQueue() throws InterruptedException, IOException {
        // Clear the log file before the test
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("server.log"))) {
            writer.write("");
        }

        LogProducer.logRequest("GET", "/home", "127.0.0.1", 200);
        Thread.sleep(100); // Give some time for the log entry to be processed

        // Read the log file and check if it contains the log entry
        String logEntry;
        try (BufferedReader reader = new BufferedReader(new FileReader("server.log"))) {
            logEntry = reader.readLine();
        }

        assertNotNull(logEntry);
        assertTrue(logEntry.contains("\"method\": \"GET\""));
        assertTrue(logEntry.contains("\"route\": \"/home\""));
        assertTrue(logEntry.contains("\"origin\": \"127.0.0.1\""));
        assertTrue(logEntry.contains("\"HTTP response status\": 200"));
    }

}
