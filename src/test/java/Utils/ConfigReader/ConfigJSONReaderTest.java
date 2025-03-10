package Utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

class ConfigJSONReaderTest {

    @org.junit.jupiter.api.Test
    void readServerConfigFile() {
        ConfigJSONReader reader = new ConfigJSONReader("src/test/java/Utils/ConfigTestFile/testConfig.json");
        assertAll(
                () -> assertEquals(8888, reader.readServerConfigFile().get("port")),
                () -> assertEquals("/var/www/html", reader.readServerConfigFile().get("documentRoot")),
                () -> assertEquals("index.html", reader.readServerConfigFile().get("defaultPage")),
                () -> assertEquals(".html", reader.readServerConfigFile().get("defaultPageExtension")),
                () -> assertEquals("404.html", reader.readServerConfigFile().get("page404")),
                () -> assertEquals(100, reader.readServerConfigFile().get("maximumRequests")),
                //Test if HashMap is empty
                () -> assertFalse(reader.readServerConfigFile().isEmpty()),
                //Check if each key and value is present
                () -> assertTrue(reader.readServerConfigFile().containsKey("port")),
                () -> assertTrue(reader.readServerConfigFile().containsKey("documentRoot")),
                () -> assertTrue(reader.readServerConfigFile().containsKey("defaultPage")),
                () -> assertTrue(reader.readServerConfigFile().containsKey("defaultPageExtension")),
                () -> assertTrue(reader.readServerConfigFile().containsKey("page404")),
                () -> assertTrue(reader.readServerConfigFile().containsKey("maximumRequests")),
                () -> assertTrue(reader.readServerConfigFile().containsValue(8888)),
                () -> assertTrue(reader.readServerConfigFile().containsValue("index.html")),
                () -> assertTrue(reader.readServerConfigFile().containsValue(".html")),
                () -> assertTrue(reader.readServerConfigFile().containsValue("404.html")),
                () -> assertTrue(reader.readServerConfigFile().containsValue(100))


        );


    }

}