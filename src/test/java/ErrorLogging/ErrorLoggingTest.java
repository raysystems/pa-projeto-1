package ErrorLogging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorLoggingTest {

    private static final String ERROR_LOG_FILE = "error.log";

    @BeforeEach
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(ERROR_LOG_FILE));
    }

    @Test
    public void testLogErrorWithOwnFormat() throws IOException {
        String message = "Error message in own format";
        ErrorLogging.logError(message, true);

        try (BufferedReader reader = new BufferedReader(new FileReader(ERROR_LOG_FILE))) {
            assertEquals(message, reader.readLine());
        }
    }

    @Test
    public void testLogErrorWithDefaultFormat() throws IOException {
        String message = "Error message in given format";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-dd-MM / HH:mm:ss"));
        ErrorLogging.logError(message, false);

        try (BufferedReader reader = new BufferedReader(new FileReader(ERROR_LOG_FILE))) {
            String logEntry = reader.readLine();
            assertTrue(logEntry.contains(message));
            String expectedMessage = "[ERROR - " + timestamp +  " ] - " + message;
            assertEquals(expectedMessage, logEntry);
        }
    }

    @Test
    public void testLogErrorIOException() {
        File logFile = new File(ERROR_LOG_FILE);
        logFile.setReadOnly();

        String message = "IOException error message";
        ErrorLogging.logError(message, true);

        logFile.setWritable(true);
    }


}