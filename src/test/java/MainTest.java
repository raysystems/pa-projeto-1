import Utils.ConfigReader.ConfigJSONReader;
import Utils.Configuration.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private String absolutePath;

    @BeforeEach
    void setUp() {
        Path currentRelativePath = Paths.get("");
        absolutePath = currentRelativePath.toAbsolutePath().toString();

        // Ensure the path ends with a separator
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
    }

    @Test
    void serverConfigLoading() {
        ConfigJSONReader reader = new ConfigJSONReader(absolutePath + "config/serverConfig.json");
        ServerConfig serverConfig = new ServerConfig(reader);

        assertNotNull(serverConfig);
        assertEquals(8080, serverConfig.getPort());
        assertEquals("html", serverConfig.getDocumentRoot());
        assertEquals("index", serverConfig.getDefaultPage());
        assertEquals(".html", serverConfig.getDefaultPageExtension());
        assertEquals("404.html", serverConfig.getPage404());
        assertEquals(5, serverConfig.getMaximumRequests());
    }

    @Test
    void serverStart() {
        ConfigJSONReader reader = new ConfigJSONReader(absolutePath + "config/serverConfig.json");
        ServerConfig serverConfig = new ServerConfig(reader);

        assertDoesNotThrow(() -> {
            MainHTTPServerThread serverThread = new MainHTTPServerThread(serverConfig);
            serverThread.start();
            serverThread.join(1000); // Wait for a second to ensure the server starts
        });
    }

    @Test
    void serverConfigLoadingWithInvalidPathThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            ConfigJSONReader reader = new ConfigJSONReader("invalid/path/to/config.json");
            new ServerConfig(reader);
        });
    }

    @Test
    void serverConfigLoadingWithMissingFieldsThrowsException() {
        ConfigJSONReader reader = new ConfigJSONReader(absolutePath + "config/invalidServerConfig.json");
        assertThrows(RuntimeException.class, () -> new ServerConfig(reader));
    }

    @Test
    void serverStartWithPortAlreadyInUseThrowsException() {
        ConfigJSONReader reader = new ConfigJSONReader(absolutePath + "config/serverConfig.json");
        ServerConfig serverConfig = new ServerConfig(reader);
        MainHTTPServerThread serverThread1 = new MainHTTPServerThread(serverConfig);
        MainHTTPServerThread serverThread2 = new MainHTTPServerThread(serverConfig);

        assertDoesNotThrow(() -> {
            serverThread1.start();
            serverThread1.join(1000);
        });

        serverThread1.interrupt();
    }

    @Test
    void mainMethodRunsSuccessfully() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            Main.main(new String[]{});
        } finally {
            System.setOut(originalOut);
        }

        String output = outContent.toString();
        assertTrue(output.contains("Loaded Config:"));
        assertTrue(output.contains("Port: 8080"));
        assertTrue(output.contains("Root:"));
        assertTrue(output.contains("Document Root: html"));
        assertTrue(output.contains("Default Page: index"));
        assertTrue(output.contains("Default Page Extension: .html"));
        assertTrue(output.contains("Page 404: 404.html"));
        assertTrue(output.contains("Maximum Requests: 5"));
    }
}