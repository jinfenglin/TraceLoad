package loadServer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This file hold the configuration for the server object.
 */
public class ServerConfig {
    private Map<String, String> properties;

    /**
     * Read configurations from a file, the file must in the class loader path.
     *
     * @param resourceName
     */
    public ServerConfig(String resourceName) {
        readConfig(resourceName);
    }

    private void readConfig(String resourceName) {
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        properties = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(configInputStream))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split("=");
                if (parts.length != 2) {
                    continue;
                }
                String propertyName = parts[0].trim();
                String propertyValue = parts[1].trim();
                properties.put(propertyName, propertyValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }
}
