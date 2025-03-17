import Utils.ConfigReader.ConfigJSONReader;
import Utils.Configuration.ServerConfig;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();

        // Ensure the path ends with a separator
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }

        // We will read a json config file
        ConfigJSONReader reader = new ConfigJSONReader(absolutePath + "config/serverConfig.json");
        ServerConfig serverConfig = new ServerConfig(reader);
        //Print Config
        System.out.println("Loaded Config:");
        System.out.println("Port: " + serverConfig.getPort());
        System.out.println("Root: " + serverConfig.getRoot());
        System.out.println("Document Root: " + serverConfig.getDocumentRoot());
        System.out.println("Default Page: " + serverConfig.getDefaultPage());
        System.out.println("Default Page Extension: " + serverConfig.getDefaultPageExtension());
        System.out.println("Page 404: " + serverConfig.getPage404());
        System.out.println("Maximum Requests: " + serverConfig.getMaximumRequests());


        MainHTTPServerThread s = new MainHTTPServerThread(serverConfig);
        s.start();
        try {
            s.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
