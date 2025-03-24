import HTMLSynchronization.HTMLSyncAccess;
import Logging.Logger;
import Utils.Configuration.ServerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandlerThread extends Thread {
    private Socket client;
    private String SERVER_ROOT;
    private ServerConfig serverConfig;
    private HTMLSyncAccess htmlSyncAccess;

    public RequestHandlerThread(Socket client, String SERVER_ROOT, ServerConfig serverConfig, HTMLSyncAccess htmlSyncAccess) {

        this.SERVER_ROOT = SERVER_ROOT;
        this.serverConfig = serverConfig;
        this.client = client;
        this.htmlSyncAccess = htmlSyncAccess;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream clientOutput = client.getOutputStream()) {

            //System.out.println("New client connected: " + client);

            // Read and parse the HTTP request, returns the route
            String route = parseHTTPRequest(br);
            String method = "GET"; // This server only supports GET requests
            String origin = client.getInetAddress().getHostAddress();
            int statusCode = 200;

            byte[] content = serverDefaultPage(route, serverConfig, SERVER_ROOT);

            // Check if the page was not found (serverDefaultPage deals with this)
            if (new String(content).contains("404")) {
                statusCode = 404;
            }

            //Simulate a delay
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Send HTTP response headers
            clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
            clientOutput.write("Content-Type: text/html\r\n".getBytes());
            clientOutput.write("Access-Control-Allow-Origin: *\r\n".getBytes()); // Add CORS header
            clientOutput.write("\r\n".getBytes());

            // Send response body
            clientOutput.write(content);
            clientOutput.write("\r\n\r\n".getBytes());
            clientOutput.flush();

            // Register the request in the server log
            Logger.logRequest(method, route, origin, statusCode);

        } catch (IOException e) {
            System.err.println("Error handling client request.");
            e.printStackTrace();
        }
    }

    /**
     * Serves the default page or a 404 error page if the requested route does not exist.
     *
     * @param route The requested route.
     * @return A byte array containing the contents of the requested file or the 404 error page.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private byte[] serverDefaultPage(String route, ServerConfig serverConfig, String SERVER_ROOT) throws IOException {
        // if the route does not contain .html, append the default page
        if (!route.contains(".html") && !route.contains(".css") && !route.contains(".js") && !route.contains(".ico") &&
                !route.contains(".png") && !route.contains(".jpg") && !route.contains(".jpeg") && !route.contains(".gif") &&
                !route.contains(".svg") && !route.contains(".pdf") && !route.contains(".txt") && !route.contains(".xml")) {
            route += serverConfig.getDefaultPage() + serverConfig.getDefaultPageExtension();
        }

        //System.out.println("Route: " + route);
        if (route.contains(".html")) {
            htmlSyncAccess.LockFile(SERVER_ROOT + route);
        }
        htmlSyncAccess.getSyncLockMap().forEach((k, v) -> System.out.println("Key: " + k + " Value: " + v));
        System.out.println("Route: " + SERVER_ROOT + route);


        byte[] content;
        try {
            //System.out.println("Path : " + SERVER_ROOT + route);
            content = readBinaryFile(SERVER_ROOT + route);

        } catch (IOException e) {

            content = readBinaryFile(SERVER_ROOT + '/' + serverConfig.getPage404());
        }
        if (route.contains(".html")) {
            htmlSyncAccess.UnlockFile(SERVER_ROOT + route);
        }
        return  content;
    }

    /**
     * Parses the HTTP request from the client and returns the requested route.
     *
     * @param br The BufferedReader to read the HTTP request.
     * @return The requested route, or null if the request is invalid.
     * @throws IOException If an I/O error occurs while reading the request.
     */
    private String parseHTTPRequest(BufferedReader br) throws IOException {
        StringBuilder requestBuilder = new StringBuilder();

        String line;
        while (!(line = br.readLine()).isBlank()) {
            requestBuilder.append(line).append("\r\n");
        }

        String request = requestBuilder.toString();
        String[] tokens = request.split(" ");
        if (tokens.length < 2) {
            System.err.println("Invalid request received.");
            return null;
        }

        String route = tokens[1];
        //System.out.println("Request received: " + request);
        return route;
    }

    /**
     * Reads a binary file and returns its contents as a byte array.
     *
     * @param path The file path to read.
     * @return A byte array containing the file's contents, or an empty array if an error occurs.
     */
    private byte[] readBinaryFile(String path) throws IOException {

        return Files.readAllBytes(Paths.get(path));

    }
}
