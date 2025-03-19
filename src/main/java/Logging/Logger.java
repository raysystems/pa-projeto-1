package Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FILE = "server.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logRequest(String method, String route, String origin, int statusCode) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format(
                "{ \"timestamp\": \"%s\", \"method\": \"%s\", \"route\": \"%s\", \"origin\": \"%s\", \"HTTP response status\": %d }%n",
                timestamp, method, route, origin, statusCode
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("[ERROR] Could not write to file: " + LOG_FILE);
        }
    }
}
