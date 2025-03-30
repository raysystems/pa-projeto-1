package Configuration;

import Utils.ConfigReader.Interfaces.IConfigReader;
import Utils.Configuration.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigTest {

    private IConfigReader configReader;
    private ServerConfig serverConfig;

    @BeforeEach
    void setUp() {
        configReader = new IConfigReader() {
            @Override
            public Map<String, Object> readServerConfigFile() {
                Map<String, Object> config = new HashMap<>();
                config.put("port", 8080);
                config.put("documentRoot", "testDocRoot");
                config.put("defaultPage", "index");
                config.put("defaultPageExtension", ".html");
                config.put("page404", "404.html");
                config.put("maximumRequests", 100);
                return config;
            }
        };
        serverConfig = new ServerConfig(configReader);
    }

    @Test
    void getRootReturnsCorrectValue() {
        serverConfig.setRoot("testRoot");
        assertEquals("testRoot", serverConfig.getRoot());
    }

    @Test
    void setRootUpdatesValue() {
        serverConfig.setRoot("newRoot");
        assertEquals("newRoot", serverConfig.getRoot());
    }

    @Test
    void getPortReturnsCorrectValue() {
        assertEquals(8080, serverConfig.getPort());
    }

    @Test
    void setPortUpdatesValue() {
        serverConfig.setPort(9090);
        assertEquals(9090, serverConfig.getPort());
    }

    @Test
    void getDocumentRootReturnsCorrectValue() {
        assertEquals("testDocRoot", serverConfig.getDocumentRoot());
    }

    @Test
    void setDocumentRootUpdatesValue() {
        serverConfig.setDocumentRoot("newDocRoot");
        assertEquals("newDocRoot", serverConfig.getDocumentRoot());
    }

    @Test
    void getDefaultPageReturnsCorrectValue() {
        assertEquals("index", serverConfig.getDefaultPage());
    }

    @Test
    void setDefaultPageUpdatesValue() {
        serverConfig.setDefaultPage("home");
        assertEquals("home", serverConfig.getDefaultPage());
    }

    @Test
    void getDefaultPageExtensionReturnsCorrectValue() {
        assertEquals(".html", serverConfig.getDefaultPageExtension());
    }

    @Test
    void setDefaultPageExtensionUpdatesValue() {
        serverConfig.setDefaultPageExtension(".htm");
        assertEquals(".htm", serverConfig.getDefaultPageExtension());
    }

    @Test
    void getPage404ReturnsCorrectValue() {
        assertEquals("404.html", serverConfig.getPage404());
    }

    @Test
    void setPage404UpdatesValue() {
        serverConfig.setPage404("notfound.html");
        assertEquals("notfound.html", serverConfig.getPage404());
    }

    @Test
    void getMaximumRequestsReturnsCorrectValue() {
        assertEquals(100, serverConfig.getMaximumRequests());
    }

    @Test
    void setMaximumRequestsUpdatesValue() {
        serverConfig.setMaximumRequests(200);
        assertEquals(200, serverConfig.getMaximumRequests());
    }
}