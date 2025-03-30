import ErrorLogging.ErrorLogging;
import HTMLSynchronization.HTMLSyncAccess;
import Utils.Configuration.ServerConfig;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple HTTP server that listens on a specified port.
 * It serves files from a predefined server root directory.
 */
public class MainHTTPServerThread extends Thread {

    private final String SERVER_ROOT; // Define by user
    private final ServerConfig serverConfig;
    private final int port;
    private final ThreadPoolRunner runner;
    private final HTMLSyncAccess htmlSyncAccess;

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
            ServerSocket server = new ServerSocket(port);

            //Create Thread to print Runner Status
            Thread status = new Thread(() -> {
                while (true) {
                    System.out.println("Total Free Workers: " +  String.valueOf(runner.getFreeWorkers()) +
                                       "\nActive Workers on ThreadPool: " + runner.getActiveCount() +
                                       "\nWaiting: " + runner.getQueueSize());
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

                RequestHandlerThread task = new RequestHandlerThread(client, SERVER_ROOT, serverConfig, htmlSyncAccess);
                runner.submit(task);

            }

        } catch (IOException e) {
            Thread thread = new Thread(() -> ErrorLogging.logError(e.getMessage(), false));
            thread.start();
            e.printStackTrace();
        }
    }
}