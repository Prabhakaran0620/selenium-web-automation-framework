package Utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    public static Properties properties = new Properties(); // ✅ Initialize here

    public static void loadProperties(String env) {
        try {
            // Load global config
            FileInputStream global = new FileInputStream("src/test/java/features/config.properties");
            properties.load(global);

            // Load environment-specific config
            FileInputStream envFile = new FileInputStream("src/test/java/features/" + env + "_config.properties");
            properties.load(envFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config files: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}