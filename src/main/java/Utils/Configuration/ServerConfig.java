package Utils.Configuration;

import Utils.ConfigReader.Interfaces.IConfigReader;
import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    private String root;
    private int port;
    private String documentRoot;
    private String defaultPage;
    private String defaultPageExtension;
    private String page404;
    private int maximumRequests;
    private Map<String, Object> configMap;

    // Constructor
    public ServerConfig(IConfigReader cfgReader) {

        this.root = "";
        updateRoot();
        this.port = 0;
        this.documentRoot = "";
        this.defaultPage = "";
        this.defaultPageExtension = "";
        this.page404 = "";
        this.maximumRequests = 0;

        configMap = cfgReader.readServerConfigFile();
        ExtractConfig();


    }
    private void updateRoot() {
        //Calculate root Directory of project
        this.root = System.getProperty("user.dir");

    }
    private void ExtractConfig() {
        this.port = (int) configMap.get("port");
        this.documentRoot = (String) configMap.get("documentRoot");
        this.defaultPage = (String) configMap.get("defaultPage");
        this.defaultPageExtension = (String) configMap.get("defaultPageExtension");
        this.page404 = (String) configMap.get("page404");
        this.maximumRequests = (int) configMap.get("maximumRequests");
    }

    // Getters and Setters
    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDocumentRoot() {
        return documentRoot;
    }

    public void setDocumentRoot(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    public String getDefaultPage() {
        return defaultPage;
    }

    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    public String getDefaultPageExtension() {
        return defaultPageExtension;
    }

    public void setDefaultPageExtension(String defaultPageExtension) {
        this.defaultPageExtension = defaultPageExtension;
    }

    public String getPage404() {
        return page404;
    }

    public void setPage404(String page404) {
        this.page404 = page404;
    }

    public int getMaximumRequests() {
        return maximumRequests;
    }

    public void setMaximumRequests(int maximumRequests) {
        this.maximumRequests = maximumRequests;
    }
}