package Utils.ConfigReader;

import Utils.ConfigReader.Interfaces.IConfigReader;
import Utils.Configuration.ServerConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ConfigJSONReader implements IConfigReader {

    ServerConfig serverConfig;
    String cfgPath;

    public ConfigJSONReader(String cfgPath) {
        this.cfgPath = cfgPath;
    }

    @Override
    public Map<String, Object> readServerConfigFile() {
        Map<String, Object> configMap = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(cfgPath));

            configMap.put("port", root.get("port").asInt());
            configMap.put("documentRoot", root.get("documentRoot").asText());
            configMap.put("defaultPage", root.get("defaultPage").asText());
            configMap.put("defaultPageExtension", root.get("defaultPageExtension").asText());
            configMap.put("page404", root.get("page404").asText());
            configMap.put("maximumRequests", root.get("maximumRequests").asInt());
        } catch (IOException e) {
            System.err.println("Error reading server configuration file: " + cfgPath);
            e.printStackTrace();
        }

        return configMap;
    }
}