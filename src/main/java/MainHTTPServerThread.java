import HTMLSynchronization.HTMLSyncAccess;
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
    private ThreadPoolRunner runner;
    private HTMLSyncAccess htmlSyncAccess;

    /**
     * Constructor to initialize the HTTP server thread with the specified configuration.
     *
     * @param cfg An instance of ServerConfig containing all attributes read from the configuration file.
     */
    public MainHTTPServerThread(ServerConfig cfg) {
        this.serverConfig = cfg;
        this.port = cfg.getPort();
        this.SERVER_ROOT = cfg.getDocumentRoot();
        this.runner = new ThreadPoolRunner(5);
        this.htmlSyncAccess = new HTMLSyncAccess("html");

    }


    /**
     * Starts the HTTP server and listens for incoming client requests.
     * Processes HTTP GET requests and serves files from the defined server root directory.
     */
    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            //System.out.println("Server started on port: " + port);
            //System.out.println("Working Directory: " + System.getProperty("user.dir"));
            //Create Thread to print Runner Status
            Thread status = new Thread(() -> {
                while (true) {
                    System.out.println("Total Free Workers: " +  String.valueOf(runner.getFreeWorkers()) + "\nWorkers Ativos do ThreadPool: " + runner.getActiveCount() + " \nEm espera: " + runner.getQueueSize());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            status.start();
            while (true) {
                Socket client = server.accept();
                //System.out.println("Client connected: " + client.getInetAddress().getHostAddress());

                RequestHandlerThread task = new RequestHandlerThread(client, SERVER_ROOT, serverConfig, htmlSyncAccess);
                runner.submit(task);

            }

        } catch (IOException e) {
            //System.err.println("Server error: Unable to start on port " + port);
            e.printStackTrace();
        }
    }
}