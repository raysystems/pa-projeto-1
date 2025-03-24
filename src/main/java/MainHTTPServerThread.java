import Logging.Logger;
import Utils.Configuration.ServerConfig;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A simple HTTP server that listens on a specified port.
 * It serves files from a predefined server root directory.
 */
public class MainHTTPServerThread extends Thread {

    private String SERVER_ROOT; // Define by user
    private ServerConfig serverConfig;
    private final int port;
    private ServerSocket server;

    /**
     * Constructor to initialize the HTTP server thread with the specified configuration.
     *
     * @param cfg An instance of ServerConfig containing all attributes read from the configuration file.
     */
    public MainHTTPServerThread(ServerConfig cfg) {
        this.serverConfig = cfg;
        this.port = cfg.getPort();
        this.SERVER_ROOT = cfg.getDocumentRoot();

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

    /**
     * Reads a text file and returns its contents as a string.
     *
     * @param path The file path to read.
     * @return A string containing the file's contents, or an empty string if an error occurs.
     */
    private String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + path);
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * Starts the HTTP server and listens for incoming client requests.
     * Processes HTTP GET requests and serves files from the defined server root directory.
     */
    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started on port: " + port);
            System.out.println("Working Directory: " + System.getProperty("user.dir"));

            while (true) {
                Socket client = server.accept();
                System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
                //AQUI E ONDE SERA FEITO TUDO RELACIONADO COM O PARALELISMO

                handleClientRequest(client);
            }

        } catch (IOException e) {
            System.err.println("Server error: Unable to start on port " + port);
            e.printStackTrace();
        }
    }

    /**
     * Handles the client request by reading the HTTP request, serving the appropriate file,
     * and sending the HTTP response.
     *
     * @param client The client socket.
     * @throws IOException If an I/O error occurs while handling the client request.
     */
    private void handleClientRequest(Socket client) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream clientOutput = client.getOutputStream()) {

            System.out.println("New client connected: " + client);

            // Read and parse the HTTP request, returns the route
            String route = parseHTTPRequest(br);
            String method = "GET"; // This server only supports GET requests
            String origin = client.getInetAddress().getHostAddress();
            int statusCode = 200;

            byte[] content = serverDefaultPage(route);

            // Check if the page was not found (serverDefaultPage deals with this)
            if (new String(content).contains("404")) {
                statusCode = 404;
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
    private byte[] serverDefaultPage(String route) throws IOException {
        // if the route does not contain .html, append the default page
        if (!route.contains(".html")) {
            route += '/' + serverConfig.getDefaultPage() + serverConfig.getDefaultPageExtension();
        }

        System.out.println("Route: " + route);


        byte[] content;
        try {
            System.out.println("Path : " + SERVER_ROOT + route);
            content = readBinaryFile(SERVER_ROOT + route);

        } catch (IOException e) {
            System.out.println("Path : " + SERVER_ROOT + serverConfig.getPage404());
            content = readBinaryFile(SERVER_ROOT + '/' + serverConfig.getPage404());
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
        System.out.println("Request received: " + request);
        return route;
    }
}