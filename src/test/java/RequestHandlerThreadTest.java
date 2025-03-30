import HTMLSynchronization.HTMLSyncAccess;
import Utils.ConfigReader.ConfigJSONReader;
import Utils.Configuration.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;



class RequestHandlerThreadTest {
    private RequestHandlerThread requestHandlerThread;
    private ServerConfig serverConfig;
    private HTMLSyncAccess htmlSyncAccess;

    @BeforeEach
    public void setUp() {
        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();

        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }

        // We will read a json config file
        ConfigJSONReader reader = new ConfigJSONReader(absolutePath + "config/serverConfig.json");
        serverConfig = new ServerConfig(reader);
        htmlSyncAccess = new HTMLSyncAccess("src/test/java/TestHTMLFiles");
        requestHandlerThread = new RequestHandlerThread(null, "src/test/java/TestHTMLFiles", serverConfig, htmlSyncAccess);
    }

    @Test
    void parseRouteIdentifyNeedsDefaultPage_withHtmlRoute() {
        String route = "/index.html";
        String result = requestHandlerThread.parseRouteIdentifyNeedsDefaultPage(route);
        assertEquals("/index.html", result);
        //checking a file that not exists
        String route2 = "/about/index.html";
        String result2 = requestHandlerThread.parseRouteIdentifyNeedsDefaultPage(route2);
        assertEquals("/404.html", result2);
        //checking an empty route
        String route3 = "/";
        String result3 = requestHandlerThread.parseRouteIdentifyNeedsDefaultPage(route3);
        assertEquals("/index.html", result3);

    }

    @Test
    void readDocument_withInvalidRoute_throwsIOException() {
        String route = "/nonexistent.html";
        assertThrows(IOException.class, () -> requestHandlerThread.readDocument(route, serverConfig, "src/test/java/TestHTMLFiles"));
        assertDoesNotThrow(() -> requestHandlerThread.readDocument("/index.html", serverConfig, "src/test/java/TestHTMLFiles"));
    }

    @Test
    void readBinaryFile_withValidPath() throws IOException {
        String path = "src/test/java/TestHTMLFiles/index.html";
        byte[] expectedContent = "content".getBytes();
        Files.write(Paths.get(path), expectedContent);
        RequestHandlerThread requestHandlerThread = new RequestHandlerThread(null, "", null, null);
        byte[] result = requestHandlerThread.readBinaryFile(path);
        assertArrayEquals(expectedContent, result);
    }

    @Test
    void readBinaryFile_withInvalidPath() {
        String path = "src/test/java/TestHTMLFiles/nonexistent.html";
        RequestHandlerThread requestHandlerThread = new RequestHandlerThread(null, "", null, null);
        assertThrows(IOException.class, () -> requestHandlerThread.readBinaryFile(path));
    }

    @Test
    void parseHTTPRequest_withValidRequest() throws IOException {
        BufferedReader br = new BufferedReader(new StringReader("GET /index.html HTTP/1.1\r\n\r\n"));
        RequestHandlerThread requestHandlerThread = new RequestHandlerThread(null, "", null, null);
        String result = requestHandlerThread.parseHTTPRequest(br);
        assertEquals("/index.html", result);
    }



}